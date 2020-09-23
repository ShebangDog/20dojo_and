package jp.co.cyberagent.dojo2020.ui.create

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import jp.co.cyberagent.dojo2020.data.CategoryRepository
import jp.co.cyberagent.dojo2020.data.DraftRepository
import jp.co.cyberagent.dojo2020.data.UserInfoRepository
import jp.co.cyberagent.dojo2020.data.ext.accessWithUid
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Content
import jp.co.cyberagent.dojo2020.data.model.Draft
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MemoCreateViewModel @ViewModelInject constructor(
    private val draftRepository: DraftRepository,
    private val categoryRepository: CategoryRepository,
    userInfoRepository: UserInfoRepository
) : ViewModel() {

    private val userInfoFlow = userInfoRepository.fetchUserInfo()

    @ExperimentalCoroutinesApi
    val categoryListLiveData = userInfoFlow.flatMapLatest { firebaseUserInfo ->
        val uid = firebaseUserInfo?.uid

        val categoryListFlow = categoryRepository.fetchAllCategory(uid)
        val categorySetFlow = categoryListFlow.map { it.toSet() }

        categorySetFlow
    }.asLiveData()

    fun addDraft(title: String, content: String, category: String) = viewModelScope.launch {
        val draft = Draft.createDraft(title, Content(content), Category(category))

        draftRepository.saveDraft(draft)
    }

    fun addCategory(categoryName: String) = viewModelScope.launch {

        userInfoFlow.accessWithUid { uid ->
            categoryRepository.saveCategory(
                uid,
                Category(categoryName)
            )
        }
    }
}