package jp.co.cyberagent.dojo2020.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import jp.co.cyberagent.dojo2020.DI
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.TimeEachCategory
import jp.co.cyberagent.dojo2020.ui.create.spinner.SpinnerAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat

class ProfileViewModel(context: Context) : ViewModel() {

    private val firebaseUserInfoRepository = DI.injectDefaultUserInfoRepository()
    private val profileRepository = DI.injectDefaultProfileRepository(context)
    private val memoRepository = DI.injectDefaultMemoRepository(context)
    private val categoryRepository = DI.injectDefaultCategoryRepository(context)

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()

    @FlowPreview
    private val profileFlow = userFlow.flatMapConcat { profileRepository.fetchProfile(it?.uid) }

    @FlowPreview
    private val timeEachCategoryFlow = userFlow.flatMapConcat { firebaseUserInfo ->
        val uid = firebaseUserInfo?.uid

        memoRepository.fetchAllMemo(uid)
            .combine(categoryRepository.fetchAllCategory(uid)) { memoList, ownCategoryList ->
                val defaultCategoryList = SpinnerAdapter.defaultItemList(context)
                    .map { Category(it) }

                val categoryList = ownCategoryList + defaultCategoryList

                categoryList.mapNotNull { category ->
                    val totalTimeForEachCategory = memoList
                        .filter { it.category == category }
                        .fold(0L) { result, memo -> result + memo.time }
                        .takeUnless { it == 0L }

                    totalTimeForEachCategory?.let { TimeEachCategory(it, category) }
                }
            }
    }

    val firebaseUserInfoLiveData = userFlow.asLiveData()

    @FlowPreview
    val timeEachCategoryLiveData = timeEachCategoryFlow.asLiveData()

    @FlowPreview
    val profileLiveData = profileFlow.asLiveData()

}