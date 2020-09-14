package jp.co.cyberagent.dojo2020.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import jp.co.cyberagent.dojo2020.data.model.Account
import jp.co.cyberagent.dojo2020.data.model.UserItem
import jp.co.cyberagent.dojo2020.data.model.ViewType
import jp.co.cyberagent.dojo2020.databinding.ItemAnalyticBinding
import jp.co.cyberagent.dojo2020.databinding.ItemPrimaryAccountBinding
import jp.co.cyberagent.dojo2020.databinding.ItemSecondaryAccountBinding
import jp.co.cyberagent.dojo2020.ui.ext.showImage

typealias PrimaryAccountApplier = ItemPrimaryAccountBinding.(UserItem) -> Unit
typealias SecondaryAccountApplier = ItemSecondaryAccountBinding.(UserItem) -> Unit
typealias AnalyticApplier = ItemAnalyticBinding.(UserItem) -> Unit

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserItemViewHolder>() {

    var userItemList: List<UserItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class UserItemViewHolder(
        private val viewBinding: ViewBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun setUserItem(
            userItem: UserItem,
            primaryAccountApplier: PrimaryAccountApplier,
            secondaryAccountApplier: SecondaryAccountApplier,
            analyticApplier: AnalyticApplier
        ) {

            when (userItem.viewType) {
                ViewType.PrimaryAccount -> primaryAccountApplier(
                    viewBinding as ItemPrimaryAccountBinding,
                    userItem
                )
                ViewType.SecondaryAccount -> secondaryAccountApplier(
                    viewBinding as ItemSecondaryAccountBinding,
                    userItem
                )
                ViewType.Analytic -> analyticApplier(viewBinding as ItemAnalyticBinding, userItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return userItemList[position].viewType.value
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val sealedViewType = when (viewType) {
            ViewType.PrimaryAccount.value -> ViewType.PrimaryAccount
            ViewType.SecondaryAccount.value -> ViewType.SecondaryAccount
            ViewType.Analytic.value -> ViewType.Analytic

            else -> throw Exception()
        }

        val viewBinding = when (sealedViewType) {
            ViewType.PrimaryAccount -> ItemPrimaryAccountBinding.inflate(inflater, parent, false)
            ViewType.SecondaryAccount -> ItemSecondaryAccountBinding.inflate(
                inflater,
                parent,
                false
            )
            ViewType.Analytic -> ItemAnalyticBinding.inflate(inflater, parent, false)
        }

        return UserItemViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        val userItem = userItemList[position]

        val primaryAccountApplier: PrimaryAccountApplier = { item ->
            when (item) {
                is UserItem.PrimaryAccountItem -> {
                    item.value?.also {
                        nameTextView.text = it.name
                        iconImageButton.showImage(this, it.imageUri)
                    }
                }
                else -> {
                }
            }
        }

        val secondaryAccountApplier: SecondaryAccountApplier = { item ->
            when (item) {
                is UserItem.SecondaryAccountItem -> {
                    item.value?.also {
                        accountIdTextView.text = it.id
                        accountUrlTextView.text = it.url
                        accountImageButton.showImage(
                            this,
                            Account.iconHashMap.getOrElse(it.serviceName) { Account.defaultIcon }
                        )
                    }
                }
                else -> {
                }
            }
        }

        val analyticApplier: AnalyticApplier = { item ->
            when (item) {
                is UserItem.AnalyticItem -> {
                    val timeEachCategoryList = item.value.orEmpty()
                    val pieEntryList = timeEachCategoryList.map {
                        PieEntry(it.time.toFloat(), it.category.name)
                    }

                    val dataSet = PieDataSet(pieEntryList, "category").apply {
                        setColors(*ColorTemplate.JOYFUL_COLORS)

                        valueTextSize = 12f
                        valueTextColor = Color.WHITE
                    }

                    timeEachCategoryGraphPieChart.apply {
                        data = PieData(dataSet)
                        centerText = "statistics"

                        setEntryLabelTextSize(13f)
                        setEntryLabelColor(Color.BLACK)
                        setCenterTextSize(15f)

                        animateY(750)
                        invalidate() //更新
                    }

                }
                else -> {
                }
            }
        }

        holder.setUserItem(
            userItem,
            primaryAccountApplier,
            secondaryAccountApplier,
            analyticApplier
        )
    }

    override fun getItemCount(): Int {
        return userItemList.size
    }

}