package jp.co.cyberagent.dojo2020.ui.create

import android.content.Context
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.ui.home.visibleOrGone
import jp.co.cyberagent.dojo2020.ui.widget.OnClickChipListener

@BindingAdapter("app:visibility")
fun bindVisibility(linearLayout: LinearLayout, isVisible: Boolean) {
    linearLayout.visibility = visibleOrGone(isVisible)
}

@BindingAdapter("app:categories", "app:onCategoryChipClick")
fun bindChips(
    chipGroup: ChipGroup,
    categorySet: Set<Category>?,
    onClickChipListener: OnClickChipListener
) {

    chipGroup.removeAllViews()
    categorySet?.forEach { category ->
        chipGroup.addView(
            createChip(chipGroup.context, category) { onClickChipListener.onClick(it) },
            0
        )
    }
}

private fun createChip(
    context: Context,
    category: Category,
    onClickChip: (String) -> Unit
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
        setOnClickListener { onClickChip(category.name) }

        text = category.name
    }
}