package jp.co.cyberagent.dojo2020.util

import java.util.concurrent.TimeUnit

object Utility {
    fun millsToFormattedTime(totalTime: Long): String {
        val hours = TimeUnit.SECONDS.toHours(totalTime)
        val minutes = TimeUnit.SECONDS.toMinutes(totalTime - TimeUnit.HOURS.toSeconds(hours))
        val seconds =
            totalTime - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes)

        return listOf(hours, minutes, seconds)
            .map { it.toString() }
            .joinToString(":") { if (it.length == 1) "0$it" else it }
    }
}