package com.example.model

/**
 * Compliance status for each aisle (lorong)
 */
enum class ComplianceStatus {
    PENDING,
    WARNING_EARLY,        // INDIKASI TIDAK MENGGUNAKAN MAX DISPLAY
    COMPLIANT_ON_TIME     // DISPLAY SESUAI WAKTU
}

/**
 * Data model for each work aisle / lorong
 */
data class AisleItem(
    val id: Int,
    val name: String,
    val pluCount: Int,
    val durationSeconds: Long = pluCount * 5L,
    val remainingSeconds: Long = pluCount * 5L,
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val complianceStatus: ComplianceStatus = ComplianceStatus.PENDING,
    val startedAtMillis: Long? = null,
    val savedAtMillis: Long? = null,
    val remainingWhenSaved: Long? = null,
    val alarmPlayed: Boolean = false
) {
    val progressFraction: Float
        get() {
            if (durationSeconds <= 0) return 1f
            val elapsed = durationSeconds - remainingSeconds
            return (elapsed.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        }

    val formattedRemaining: String
        get() = formatSecondsToMMSS(remainingSeconds)

    val formattedDuration: String
        get() = formatSecondsToMMSS(durationSeconds)

    companion object {
        fun formatSecondsToMMSS(totalSeconds: Long): String {
            val s = if (totalSeconds < 0) 0 else totalSeconds
            val minutes = s / 60
            val seconds = s % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}

/**
 * Execution Mode:
 * Sequential: Work lorong by lorong in order (Standard retail display workflow)
 * Parallel: All lorongs countdown simultaneously
 */
enum class WorkMode {
    SEQUENTIAL,
    PARALLEL
}
