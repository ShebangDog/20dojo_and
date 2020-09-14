package jp.co.cyberagent.dojo2020.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import jp.co.cyberagent.dojo2020.DI
import jp.co.cyberagent.dojo2020.data.model.TimeEachCategory
import jp.co.cyberagent.dojo2020.data.model.UserItem
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
            .combine(categoryRepository.fetchAllCategory(uid)) { memoList, categoryList ->
                categoryList.map { category ->
                    val totalTimeForEachCategory = memoList
                        .filter { it.category == category }
                        .fold(0L) { result, memo -> result + memo.time }

                    TimeEachCategory(totalTimeForEachCategory, category)
                }
            }
    }

    @FlowPreview
    val userItemListLiveData = profileFlow
        .combine(timeEachCategoryFlow) { profile, timeEachCategory -> profile to timeEachCategory }
        .combine(userFlow) { (profile, timeEachCategory), firebaseUser ->
            val primaryAccount = firebaseUser.let { UserItem.PrimaryAccountItem(it) }
            val secondaryAccountList =
                profile?.accountList.orEmpty().map { UserItem.SecondaryAccountItem(it) }
            val analytic = timeEachCategory.let { UserItem.AnalyticItem(it) }

            val userItemList = listOf(primaryAccount) + secondaryAccountList + analytic

            userItemList
        }
        .asLiveData()

}