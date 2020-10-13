package jp.co.cyberagent.dojo2020.ui

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:visibility")
fun bindVisibility(view: View, isVisible: Boolean) {
    view.visibility = visibleOrGone(isVisible)
}

private fun visibleOrGone(isVisible: Boolean) = if (isVisible) View.VISIBLE else View.GONE
