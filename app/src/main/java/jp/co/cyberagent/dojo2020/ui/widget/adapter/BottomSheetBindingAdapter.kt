package jp.co.cyberagent.dojo2020.ui.widget.adapter

import android.content.res.ColorStateList
import androidx.annotation.StyleRes
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category

sealed class ChipType(@StyleRes val style: Int) {
    object Action : ChipType(R.style.Widget_MaterialComponents_Chip_Action)
    object Choice : ChipType(R.style.Widget_MaterialComponents_Chip_Choice)
    object Entry : ChipType(R.style.Widget_MaterialComponents_Chip_Entry)
    object Filter : ChipType(R.style.Widget_MaterialComponents_Chip_Filter)
}


sealed class OnChipClickListener {
    interface OnChoiceChipClickListener {
        fun onClick(category: Category)
    }

    interface OnFilterChipClickListener {
        fun onClick(chip: Chip, category: Category)
    }
}

@BindingAdapter(
    "app:chipType",
    "app:categories",
    "app:chipStateSet",
    "app:onCategoryChoiceClick",
    "app:onCategoryFilterChipClick",
    requireAll = false
)
fun bindChips(
    chipGroup: ChipGroup,
    type: ChipType,
    categorySet: Set<Category>? = null,
    chipStateSet: Set<Category>? = null,
    onChoiceChipClickListener: OnChipClickListener.OnChoiceChipClickListener? = null,
    onFilterChipClickListener: OnChipClickListener.OnFilterChipClickListener? = null
) {

    chipGroup.removeAllViews()
    categorySet?.forEach {
        val isChecked = chipStateSet?.contains(it) ?: false

        chipGroup.addView(
            createChip(chipGroup, type.style, it, isChecked) { chip, category ->
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
    isChipChecked: Boolean,
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
        isChecked = if (chipGroup.isSingleSelection) isChecked else isChipChecked
    }
}