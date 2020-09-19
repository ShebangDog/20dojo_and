package jp.co.cyberagent.dojo2020.data.timer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

object TimerDataSource {

    @ExperimentalCoroutinesApi
    val timerFlow = callbackFlow<Long> {
        val timer = Timer()
        var time = 0L

        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try { offer(time) } catch (exception: Exception) { }

                    time++
                }
            },
            0,
            1000L
        )

        awaitClose { timer.cancel() }
    }
}