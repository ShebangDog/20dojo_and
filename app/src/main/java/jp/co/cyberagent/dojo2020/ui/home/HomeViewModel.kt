package jp.co.cyberagent.dojo2020.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.chip.Chip
import jp.co.cyberagent.dojo2020.data.DraftRepository
import jp.co.cyberagent.dojo2020.data.FlowTimer
import jp.co.cyberagent.dojo2020.data.MemoRepository
import jp.co.cyberagent.dojo2020.data.UserInfoRepository
import jp.co.cyberagent.dojo2020.data.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

typealias Filter = (Text) -> Boolean

class HomeViewModel @ViewModelInject constructor(
    private val draftRepository: DraftRepository,
    private val memoRepository: MemoRepository,
    firebaseUserInfoRepository: UserInfoRepository
) : ViewModel() {

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()

    @ExperimentalCoroutinesApi
    private val filterStateFlow: MutableStateFlow<Filter?> = MutableStateFlow(null)
    val userLiveData = userFlow.asLiveData()

    @ExperimentalCoroutinesApi
    private val textListFlow = userFlow.flatMapLatest { userInfo ->
        val uid = userInfo?.uid

        draftRepository.fetchAllDraft()
            .combine(memoRepository.fetchAllMemo(uid)) { draftList, memoList ->

                val leftList = draftList.map { it.toText() }.sortedBy { it.category }
                val rightList = memoList.map { it.toText() }.sortedBy { it.category }

                (leftList + rightList).distinctBy { it.id }
            }
    }

    @ExperimentalCoroutinesApi
    fun timeLiveData(draft: Draft): LiveData<Long> {
        val currentSeconds = TimeUnit.MILLISECONDS.toSeconds(
            System.currentTimeMillis() - draft.startTime
        )

        return FlowTimer.timeFlow.map { it + currentSeconds }.asLiveData()
    }

    @ExperimentalCoroutinesApi
    val filteredTextListLiveData = textListFlow.combine(filterStateFlow) { textList, filter ->
        textList.filter { filter?.invoke(it) ?: true }
    }.asLiveData()

    @ExperimentalCoroutinesApi
    fun filter(chip: Chip, category: Category) = viewModelScope.launch {

    }

    fun saveDraft(draft: Draft) = viewModelScope.launch {
        draftRepository.saveDraft(draft)
    }

    fun saveMemo(memo: Memo) = viewModelScope.launch {
        val savedMemo = memoRepository.fetchMemoById(userFlow.first()?.uid, memo.id).first()

        memoRepository.saveMemo(userFlow.first()?.uid, addTimeToMemo(memo, savedMemo?.time ?: 0L))
    }

    fun deleteDraft(draft: Draft) = viewModelScope.launch {
        draftRepository.deleteDraftById(draft.id)
    }

    private fun addTimeToMemo(memo: Memo, time: Long) = memo
        .let { Memo(it.id, it.title, it.contents, time + it.time, it.category) }

}