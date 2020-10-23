package jp.co.cyberagent.dojo2020.ui.profile

import android.app.Application
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import jp.co.cyberagent.dojo2020.data.CategoryRepository
import jp.co.cyberagent.dojo2020.data.MemoRepository
import jp.co.cyberagent.dojo2020.data.UserInfoRepository
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.TimeEachCategory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class ProfileViewModel @ViewModelInject constructor(
    private val memoRepository: MemoRepository,
    private val categoryRepository: CategoryRepository,
    firebaseUserInfoRepository: UserInfoRepository,
    application: Application
) : AndroidViewModel(application) {

    data class GraphData(val totalTime: Long, val pieDataSet: PieDataSet)

    data class ValueView(val textSize: Float, @ColorInt val textColor: Int) {
        companion object {
            val Default = ValueView(16F, Color.WHITE)
        }
    }

    private val userFlow = firebaseUserInfoRepository.fetchUserInfo()

    val firebaseUserInfoLiveData = userFlow.asLiveData()


    @FlowPreview
    fun graphLiveData(label: String, valueText: ValueView) = MediatorLiveData<GraphData>()
        .apply {
            val pieDataSetLiveData = pieDataSetLiveData(label, valueText)

            listOf(totalTimeLiveData, pieDataSetLiveData).forEach { liveData ->
                addSource(liveData) {
                    val totalTime = totalTimeLiveData.value ?: return@addSource
                    val pieDataSet = pieDataSetLiveData.value ?: return@addSource

                    value = GraphData(totalTime, pieDataSet)
                }
            }

        }

    @FlowPreview
    private val memoListFlow = userFlow.flatMapConcat { firebaseUserInfo ->
        val uid = firebaseUserInfo?.uid

        memoRepository.fetchAllMemo(uid)
    }

    @FlowPreview
    private val totalTimeFlow = memoListFlow
        .map { memoList -> memoList.fold(0L) { result, memo -> result + memo.time } }

    @FlowPreview
    private val totalTimeLiveData = totalTimeFlow.asLiveData()

    @FlowPreview
    private val timeEachCategoryFlow = userFlow.flatMapConcat { firebaseUserInfo ->
        val uid = firebaseUserInfo?.uid

        memoListFlow
            .combine(categoryRepository.fetchAllCategory(uid)) { memoList, ownCategoryList ->
                val defaultCategoryList = Category.defaultCategoryList(application)

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
    private fun pieDataSetFlow(label: String, valueText: ValueView) =
        timeEachCategoryFlow
            .map { list ->
                list.map {
                    PieEntry(
                        it.time.toFloat(),
                        it.category.name
                    )
                } to list.map { it.category.color }
            }
            .map { (pieEntryList, color) -> PieDataSet(pieEntryList, label) to color }
            .map { (pieDataSet, colorList) ->
                pieDataSet.apply {
                    colors = colorList.map { it.value }

                    valueText.also {
                        valueTextSize = it.textSize
                        valueTextColor = it.textColor
                    }
                }
            }

    @FlowPreview
    private fun pieDataSetLiveData(label: String, valueText: ValueView) =
        pieDataSetFlow(label, valueText).asLiveData()
}