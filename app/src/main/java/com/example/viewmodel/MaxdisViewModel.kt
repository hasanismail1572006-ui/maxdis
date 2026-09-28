package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioAlarmManager
import com.example.model.AisleItem
import com.example.model.ComplianceStatus
import com.example.model.WorkMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class ComplianceAlertState(
    val isVisible: Boolean = false,
    val isWarning: Boolean = false, // true = Red Warning, false = Green Success
    val aisleName: String = "",
    val title: String = "",
    val message: String = "",
    val remainingText: String = ""
)

data class MaxdisUiState(
    val inputFormula: String = "5+10+6",
    val aisles: List<AisleItem> = emptyList(),
    val currentTimeString: String = "",
    val isSessionRunning: Boolean = false,
    val sessionStartTimeMillis: Long? = null,
    val frozenEstimatedEndTimeString: String = "--:--:--",
    val totalDurationSeconds: Long = 0L,
    val totalRemainingSeconds: Long = 0L,
    val workMode: WorkMode = WorkMode.SEQUENTIAL,
    val activeAisleIndex: Int? = null,
    val complianceAlert: ComplianceAlertState = ComplianceAlertState(),
    val isAudioEnabled: Boolean = true,
    val showInfoDialog: Boolean = false,
    val showBrowserDialog: Boolean = false
)

class MaxdisViewModel(context: Context) : ViewModel() {

    private val audioAlarmManager = AudioAlarmManager(context.applicationContext)

    private val _uiState = MutableStateFlow(MaxdisUiState())
    val uiState: StateFlow<MaxdisUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        // Initial parse of default example formula "5+10+6"
        processFormula("5+10+6")
        startClockAndTicker()
    }

    /**
     * Real-time anti-lag ticker.
     * Uses System.currentTimeMillis() (Date.now() equivalent)
     * so it never accumulates drift or lags even across background pauses.
     */
    private fun startClockAndTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val formattedNow = timeFormat.format(Date(now))

                _uiState.update { currentState ->
                    val updatedAisles = if (currentState.isSessionRunning) {
                        var justFinishedAisle: AisleItem? = null
                        val processed = currentState.aisles.map { aisle ->
                            if (aisle.isRunning && aisle.startedAtMillis != null && !aisle.isCompleted) {
                                val elapsedSec = (now - aisle.startedAtMillis) / 1000L
                                val rem = max(0L, aisle.durationSeconds - elapsedSec)

                                if (rem == 0L) {
                                    // Requirement: Auto-Save sendiri saat waktu habis
                                    justFinishedAisle = aisle
                                    aisle.copy(
                                        remainingSeconds = 0L,
                                        isRunning = false,
                                        isCompleted = true,
                                        complianceStatus = ComplianceStatus.COMPLIANT_ON_TIME,
                                        savedAtMillis = now,
                                        remainingWhenSaved = 0L,
                                        alarmPlayed = true
                                    )
                                } else {
                                    aisle.copy(remainingSeconds = rem)
                                }
                            } else {
                                aisle
                            }
                        }

                        // Jika lorong baru saja habis dan auto-save, mulai lorong berikutnya satu per satu
                        if (justFinishedAisle != null) {
                            if (currentState.isAudioEnabled) {
                                audioAlarmManager.playTimerFinishedAlarm()
                            }
                            var nextStarted = false
                            processed.map { a ->
                                if (!a.isCompleted && !nextStarted) {
                                    nextStarted = true
                                    a.copy(
                                        isRunning = true,
                                        startedAtMillis = now
                                    )
                                } else {
                                    a
                                }
                            }
                        } else {
                            processed
                        }
                    } else {
                        currentState.aisles
                    }

                    // Compute total remaining seconds
                    val totalRemaining = updatedAisles.filter { !it.isCompleted }.sumOf { it.remainingSeconds }

                    // Check if total session completed
                    val allCompleted = updatedAisles.isNotEmpty() && updatedAisles.all { it.isCompleted || it.remainingSeconds == 0L }
                    val isRunning = if (allCompleted && currentState.isSessionRunning) false else currentState.isSessionRunning

                    currentState.copy(
                        currentTimeString = formattedNow,
                        aisles = updatedAisles,
                        totalRemainingSeconds = totalRemaining,
                        isSessionRunning = isRunning
                    )
                }
                delay(250) // Frequent sampling ensures anti-lag responsiveness
            }
        }
    }

    // Keypad Input Handlers
    fun onKeypadPress(key: String) {
        _uiState.update { state ->
            val current = state.inputFormula
            val next = when (key) {
                "+" -> {
                    if (current.isEmpty() || current.endsWith("+")) current
                    else "$current+"
                }
                else -> {
                    // Digits 0-9
                    if (current == "0" && key != "+") key
                    else current + key
                }
            }
            state.copy(inputFormula = next)
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            val current = state.inputFormula
            val next = if (current.isNotEmpty()) current.dropLast(1) else ""
            state.copy(inputFormula = next)
        }
    }

    fun onClearInput() {
        _uiState.update { it.copy(inputFormula = "") }
    }

    /**
     * Requirement 2:
     * Saat "Enter" ditekan, aplikasi memisahkan angka tersebut, membuang input kosong,
     * dan mengurutkannya secara otomatis dari yang terkecil ke terbesar (menjadi 5, 6, 10).
     */
    fun onEnterFormula() {
        val formula = _uiState.value.inputFormula
        processFormula(formula)
    }

    private fun processFormula(formula: String) {
        val numbers = formula.split("+")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it > 0 }
            .sorted() // Sort ascending: smallest to largest

        val newAisles = numbers.mapIndexed { index, plu ->
            val duration = plu * 5L
            AisleItem(
                id = index + 1,
                name = "Lorong ${index + 1}",
                pluCount = plu,
                durationSeconds = duration,
                remainingSeconds = duration,
                isRunning = false,
                isCompleted = false,
                complianceStatus = ComplianceStatus.PENDING,
                startedAtMillis = null,
                alarmPlayed = false
            )
        }

        val totalDuration = newAisles.sumOf { it.durationSeconds }

        _uiState.update { state ->
            state.copy(
                aisles = newAisles,
                totalDurationSeconds = totalDuration,
                totalRemainingSeconds = totalDuration,
                isSessionRunning = false,
                sessionStartTimeMillis = null,
                frozenEstimatedEndTimeString = "--:--:--", // Frozen until 'Mulai' is pressed
                activeAisleIndex = null
            )
        }
    }

    /**
     * Requirement 5:
     * "Tampilkan 'Estimasi Selesai'. Pastikan estimasi selesai ini dikunci/dibekukan
     * dan baru muncul hanya saat tombol 'Mulai' ditekan (Total Waktu + Jam Mulai).
     * Jangan biarkan jam estimasinya berjalan sendiri mengikuti jam saat ini sebelum tombol mulai ditekan."
     */
    fun startSession() {
        val state = _uiState.value
        if (state.aisles.isEmpty()) return

        val now = System.currentTimeMillis()
        val totalSecs = state.totalDurationSeconds
        val estimatedEndMillis = now + (totalSecs * 1000L)
        val frozenEndTimeStr = timeFormat.format(Date(estimatedEndMillis))

        val updatedAisles = when (state.workMode) {
            WorkMode.SEQUENTIAL -> {
                // Find first non-completed aisle and start it
                var startedFirst = false
                state.aisles.mapIndexed { index, aisle ->
                    if (!aisle.isCompleted && !startedFirst) {
                        startedFirst = true
                        aisle.copy(
                            isRunning = true,
                            startedAtMillis = now - ((aisle.durationSeconds - aisle.remainingSeconds) * 1000L)
                        )
                    } else {
                        aisle.copy(isRunning = false)
                    }
                }
            }
            WorkMode.PARALLEL -> {
                // Start all unfinished aisles simultaneously
                state.aisles.map { aisle ->
                    if (!aisle.isCompleted) {
                        aisle.copy(
                            isRunning = true,
                            startedAtMillis = now - ((aisle.durationSeconds - aisle.remainingSeconds) * 1000L)
                        )
                    } else {
                        aisle
                    }
                }
            }
        }

        _uiState.update {
            it.copy(
                isSessionRunning = true,
                sessionStartTimeMillis = it.sessionStartTimeMillis ?: now,
                frozenEstimatedEndTimeString = frozenEndTimeStr,
                aisles = updatedAisles
            )
        }
    }

    fun pauseSession() {
        _uiState.update { state ->
            val updatedAisles = state.aisles.map { aisle ->
                if (aisle.isRunning) {
                    aisle.copy(isRunning = false)
                } else {
                    aisle
                }
            }
            state.copy(
                isSessionRunning = false,
                aisles = updatedAisles
            )
        }
    }

    fun resetSession() {
        processFormula(_uiState.value.inputFormula)
    }

    fun setWorkMode(mode: WorkMode) {
        _uiState.update { it.copy(workMode = mode) }
    }

    fun toggleAudio() {
        _uiState.update { it.copy(isAudioEnabled = !it.isAudioEnabled) }
    }

    /**
     * Requirement 6:
     * Di sebelah masing-masing timer lorong, sediakan tombol "Selesai (Save)" untuk simulasi pengerjaan.
     * Logika Kepatuhan:
     * - Jika pengguna menekan tombol "Selesai" sebelum timer lorong tersebut habis (masih ada sisa waktu mundur),
     *   tampilkan peringatan berwarna Merah dengan teks "INDIKASI TIDAK MENGGUNAKAN MAX DISPLAY".
     * - Sebaliknya, jika pengguna menekan "Selesai" setelah waktu habis (0:00),
     *   tampilkan status Hijau dengan teks "DISPLAY SESUAI WAKTU".
     */
    fun saveAisle(aisleId: Int) {
        val now = System.currentTimeMillis()
        var triggeredAlert: ComplianceAlertState? = null

        _uiState.update { state ->
            val targetAisle = state.aisles.find { it.id == aisleId } ?: return@update state

            val isEarly = targetAisle.remainingSeconds > 0
            val status = if (isEarly) {
                ComplianceStatus.WARNING_EARLY
            } else {
                ComplianceStatus.COMPLIANT_ON_TIME
            }

            triggeredAlert = if (isEarly) {
                ComplianceAlertState(
                    isVisible = true,
                    isWarning = true,
                    aisleName = targetAisle.name,
                    title = "INDIKASI TIDAK MENGGUNAKAN MAX DISPLAY",
                    message = "Peringatan: Lorong disimpan terlalu cepat dari standar waktu!\nSisa waktu kerja yang belum dihabiskan: ${targetAisle.formattedRemaining}.",
                    remainingText = targetAisle.formattedRemaining
                )
            } else {
                ComplianceAlertState(
                    isVisible = true,
                    isWarning = false,
                    aisleName = targetAisle.name,
                    title = "DISPLAY SESUAI WAKTU",
                    message = "Pengerjaan lorong valid! Waktu kerja display telah memenuhi standar kepatuhan Max Display (12 PLU = 1 Menit).",
                    remainingText = "00:00"
                )
            }

            val updatedAisles = state.aisles.map { aisle ->
                if (aisle.id == aisleId) {
                    aisle.copy(
                        isCompleted = true,
                        isRunning = false,
                        complianceStatus = status,
                        savedAtMillis = now,
                        remainingWhenSaved = targetAisle.remainingSeconds
                    )
                } else {
                    aisle
                }
            }

            // If sequential mode and session is running, automatically activate the next aisle
            val nextAisles = if (state.workMode == WorkMode.SEQUENTIAL && state.isSessionRunning) {
                var foundNext = false
                updatedAisles.map { a ->
                    if (!a.isCompleted && !foundNext) {
                        foundNext = true
                        a.copy(
                            isRunning = true,
                            startedAtMillis = now
                        )
                    } else {
                        a
                    }
                }
            } else {
                updatedAisles
            }

            state.copy(
                aisles = nextAisles,
                complianceAlert = triggeredAlert ?: state.complianceAlert
            )
        }

        // Sound feedback
        triggeredAlert?.let { alert ->
            if (_uiState.value.isAudioEnabled) {
                if (alert.isWarning) {
                    audioAlarmManager.playWarningAlarm()
                } else {
                    audioAlarmManager.playSuccessChime()
                }
            }
        }
    }

    fun dismissComplianceAlert() {
        _uiState.update { it.copy(complianceAlert = it.complianceAlert.copy(isVisible = false)) }
    }

    fun showInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showInfoDialog = show) }
    }

    fun showBrowserDialog(show: Boolean) {
        _uiState.update { it.copy(showBrowserDialog = show) }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
        audioAlarmManager.release()
    }
}
