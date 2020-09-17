package jp.co.cyberagent.dojo2020.ui.profile

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import jp.co.cyberagent.dojo2020.DI
import jp.co.cyberagent.dojo2020.data.model.*
import jp.co.cyberagent.dojo2020.ui.create.spinner.SpinnerAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

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

    @FlowPreview
    val userItemListLiveData = userFlow
        .combine(timeEachCategoryFlow) { firebaseUser, timeEachCategory -> firebaseUser to timeEachCategory }
        .combine(profileFlow) { (firebaseUser, timeEachCategory), profile ->
            val primaryAccount = firebaseUser.let { UserItem.PrimaryAccountItem(it) }
            val secondaryAccountList =
                profile?.accountList.orEmpty().map { UserItem.SecondaryAccountItem(it) }
            val analytic = timeEachCategory.let { UserItem.AnalyticItem(it) }

            val userItemList = listOf(primaryAccount) + secondaryAccountList + analytic

            userItemList
        }
        .asLiveData()

    @FlowPreview
    fun saveProfile() = viewModelScope.launch {
        userFlow.collect { firebaseUserInfo ->
            val uid = firebaseUserInfo?.uid

            Log.d(TAG, "saveProfile")
            profileRepository.saveProfile(
                uid, Profile(
                    "Profile-Name", "iconURL", listOf(
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com"),
                        Account("Twitter", "ShebangDog", "Twitter.com")
                    )
                )
            )
        }
    }
}