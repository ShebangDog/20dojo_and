package jp.co.cyberagent.dojo2020.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import jp.co.cyberagent.dojo2020.data.DraftRepository
import jp.co.cyberagent.dojo2020.data.FlowTimer
import jp.co.cyberagent.dojo2020.data.MemoRepository
import jp.co.cyberagent.dojo2020.data.UserInfoRepository
import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.model.toText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val draftRepository: DraftRepository,
    private val memoRepository: MemoRepository,
    firebaseUserInfoRepository: UserInfoRepository
) : ViewModel() {

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()
    val userLiveData = userFlow.asLiveData()

    @ExperimentalCoroutinesApi
    fun timeLiveData(id: String, startTime: Long) = FlowTimer.instance(id).map { it + startTime }

    @ExperimentalCoroutinesApi
    val textListLiveData = userFlow.flatMapLatest { userInfo ->
        val uid = userInfo?.uid

        draftRepository.fetchAllDraft()
            .combine(memoRepository.fetchAllMemo(uid)) { draftList, memoList ->

                val leftList = draftList.map { it.toText() }.sortedBy { it.category }
                val rightList = memoList.map { it.toText() }.sortedBy { it.category }

                leftList + rightList
            }
    }.asLiveData()

    fun filter() = viewModelScope.launch {
        userFlow.collect { userInfo ->
            memoRepository.fetchAllMemo(userInfo?.uid).collect { memoList ->
                memoList.filter { it.category.name == "kotlin" }
            }
        }
    }

    fun saveMemo(memo: Memo) = viewModelScope.launch {
        userFlow.collect { userInfo -> memoRepository.saveMemo(userInfo?.uid, memo) }
    }

    fun deleteDraft(draft: Draft) = viewModelScope.launch {
        draftRepository.deleteDraftById(draft.id)
    }
}