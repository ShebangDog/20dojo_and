package jp.co.cyberagent.dojo2020.ui.profile

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import jp.co.cyberagent.dojo2020.data.CategoryRepository
import jp.co.cyberagent.dojo2020.data.MemoRepository
import jp.co.cyberagent.dojo2020.data.ProfileRepository
import jp.co.cyberagent.dojo2020.data.UserInfoRepository
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.TimeEachCategory
import jp.co.cyberagent.dojo2020.ui.create.spinner.SpinnerAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat

class ProfileViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository,
    private val memoRepository: MemoRepository,
    private val categoryRepository: CategoryRepository,
    firebaseUserInfoRepository: UserInfoRepository,
    application: Application
) : AndroidViewModel(application) {

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()

    @FlowPreview
    private val profileFlow = userFlow.flatMapConcat { profileRepository.fetchProfile(it?.uid) }

    @FlowPreview
    private val timeEachCategoryFlow = userFlow.flatMapConcat { firebaseUserInfo ->
        val uid = firebaseUserInfo?.uid

        memoRepository.fetchAllMemo(uid)
            .combine(categoryRepository.fetchAllCategory(uid)) { memoList, ownCategoryList ->
                val defaultCategoryList =
                    SpinnerAdapter.defaultItemList(application.applicationContext)
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