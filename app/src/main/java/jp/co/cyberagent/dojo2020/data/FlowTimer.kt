package jp.co.cyberagent.dojo2020.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

class FlowTimer {
    private var isStarting = true
    private var differenceTime = 0L

    fun stop() {
        isStarting = false
    }

    fun start() {
        isStarting = true
    }

    @ExperimentalCoroutinesApi
    fun timerFlow(startTime: Long) = callbackFlow<Long> {
        val timer = Timer()
        var time = startTime

        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try {
                        if (isStarting) offer(time - differenceTime)
                    } catch (exception: Exception) {
                    }

                    time++
                    if (!isStarting) differenceTime++
                }
            },
            0,
            1000L
        )

        awaitClose { timer.cancel() }
    }
}