package jp.co.cyberagent.dojo2020.ui.create.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.ui.home.adapter.visibleOrGone
import jp.co.cyberagent.dojo2020.ui.widget.OnChipClickListener

@BindingAdapter("app:visibility")
fun bindVisibility(linearLayout: LinearLayout, isVisible: Boolean) {
    linearLayout.visibility = visibleOrGone(isVisible)
}

@BindingAdapter("app:categories", "app:onCategoryChipClick")
fun bindChips(
    chipGroup: ChipGroup,
    categorySet: Set<Category>?,
    onChipClickListener: OnChipClickListener
) {

    chipGroup.removeAllViews()
    categorySet?.forEach { category ->
        chipGroup.addView(
            createChip(chipGroup.context, category) {
                onChipClickListener.onClick(it)
            },
            0
        )
    }
}

private fun createChip(
    context: Context,
    category: Category,
    onChipClick: (Category) -> Unit
): Chip {
    val chip = Chip(context)
    val chipDrawable = ChipDrawable.createFromAttributes(
        context,
        null,
        0,
        R.style.Widget_MaterialComponents_Chip_Choice
    )

    return chip.apply {
        setChipDrawable(chipDrawable)
        setOnClickListener { onChipClick(category) }

        text = category.name
        chipBackgroundColor = ColorStateList.valueOf(category.color.value)
    }
}