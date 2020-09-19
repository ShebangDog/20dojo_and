package jp.co.cyberagent.dojo2020.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import jp.co.cyberagent.dojo2020.data.timer.TimerDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class FlowTimer private constructor() {
    private var isStarting = true
    private var differenceTime = 0L

    fun stop() {
        isStarting = false
    }

    fun start() {
        isStarting = true
    }

    @ExperimentalCoroutinesApi
    val timeFlow = TimerDataSource.timerFlow
        .onEach { if (!isStarting) differenceTime++ }
        .map { it - differenceTime }

    companion object {
        private val hashInstance = hashMapOf<String, LiveData<Long>>()

        @ExperimentalCoroutinesApi
        fun instance(id: String) = hashInstance.getOrElse(id) {
            FlowTimer().timeFlow.asLiveData().also { hashInstance[id] = it }
        }
    }
}