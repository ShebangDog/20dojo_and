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
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.model.toText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomeViewModel @ViewModelInject constructor(
    private val draftRepository: DraftRepository,
    private val memoRepository: MemoRepository,
    firebaseUserInfoRepository: UserInfoRepository
) : ViewModel() {

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()

    @ExperimentalCoroutinesApi
    private val chipStateFlow = MutableStateFlow<Set<Category>>(setOf())

    @ExperimentalCoroutinesApi
    val chipStateLiveData = chipStateFlow.asLiveData()

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
    val filteredTextListLiveData = textListFlow.combine(chipStateFlow) { textList, chipStateList ->
        val isFiltered = chipStateList.isNotEmpty()
        if (!isFiltered) textList else textList.filter { chipStateList.contains(it.category) }
    }.asLiveData()

    @ExperimentalCoroutinesApi
    fun filter(chip: Chip, category: Category) = viewModelScope.launch {
        chipStateFlow.value = when (chip.isChecked) {
            true -> chipStateFlow.value + category
            false -> chipStateFlow.value.filter { it != category }.toSet()
        }
    }

    @ExperimentalCoroutinesApi
    fun clearFilter() = viewModelScope.launch {
        chipStateFlow.value = setOf()
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