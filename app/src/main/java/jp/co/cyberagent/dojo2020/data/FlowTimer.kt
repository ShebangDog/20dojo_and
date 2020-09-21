package jp.co.cyberagent.dojo2020.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

object FlowTimer {
    @ExperimentalCoroutinesApi
    val timeFlow = callbackFlow<Long> {
        val timer = Timer()
        var time = 0L

        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try {
                        offer(time)
                    } catch (exception: Exception) {
                    }

                    time++
                }
            },
            0,
            1000L
        )

        awaitClose { timer.cancel() }
    }
}