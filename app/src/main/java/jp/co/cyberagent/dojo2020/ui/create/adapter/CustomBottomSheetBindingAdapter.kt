package jp.co.cyberagent.dojo2020.ui.create.adapter

import android.content.res.ColorStateList
import android.widget.LinearLayout
import androidx.annotation.StyleRes
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.ui.home.adapter.visibleOrGone
import jp.co.cyberagent.dojo2020.ui.widget.CategoryCreateBottomSheet
import jp.co.cyberagent.dojo2020.ui.widget.CategoryFilterBottomSheet

sealed class ChipType(@StyleRes val style: Int) {
    object Action : ChipType(R.style.Widget_MaterialComponents_Chip_Action)
    object Choice : ChipType(R.style.Widget_MaterialComponents_Chip_Choice)
    object Entry : ChipType(R.style.Widget_MaterialComponents_Chip_Entry)
    object Filter : ChipType(R.style.Widget_MaterialComponents_Chip_Filter)
}

@BindingAdapter("app:visibility")
fun bindVisibility(linearLayout: LinearLayout, isVisible: Boolean) {
    linearLayout.visibility = visibleOrGone(isVisible)
}

@BindingAdapter(
    "app:chipType",
    "app:categories",
    "app:chipStateMap",
    "app:onCategoryChoiceClick",
    "app:onCategoryFilterChipClick",
    requireAll = false
)
fun bindChips(
    chipGroup: ChipGroup,
    type: ChipType,
    categorySet: Set<Category>? = null,
    chipStateMap: HashMap<Category, CategoryFilterBottomSheet.ChipState>? = null,
    onChoiceChipClickListener: CategoryCreateBottomSheet.OnChipClickListener? = null,
    onFilterChipClickListener: CategoryFilterBottomSheet.OnChipClickListener? = null
) {

    chipGroup.removeAllViews()
    categorySet?.forEach {
        chipGroup.addView(
            createChip(chipGroup, type.style, it, chipStateMap?.get(it)) { chip, category ->
                when (type) {
                    ChipType.Action -> {
                    }

                    ChipType.Choice -> onChoiceChipClickListener?.onClick(category)

                    ChipType.Entry -> {
                    }

                    ChipType.Filter -> onFilterChipClickListener?.onClick(chip, category)
                }
            },
            0
        )
    }
}

private fun createChip(
    chipGroup: ChipGroup,
    @StyleRes style: Int,
    category: Category,
    chipState: CategoryFilterBottomSheet.ChipState?,
    onChipClick: (Chip, Category) -> Unit
): Chip {
    val chip = Chip(chipGroup.context)
    val chipDrawable = ChipDrawable.createFromAttributes(
        chipGroup.context,
        null,
        0,
        style
    )

    return chip.apply {
        setChipDrawable(chipDrawable)
        setOnClickListener { onChipClick(chip, category) }

        text = category.name
        chipBackgroundColor = ColorStateList.valueOf(category.color.value)

        chipState ?: return@apply
        isChecked = if (chipGroup.isSingleSelection) isChecked else chipState.isChecked
    }
}