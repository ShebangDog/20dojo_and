package jp.co.cyberagent.dojo2020.ui.home

import android.view.View
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.ui.ext.showImage


@BindingAdapter("app:visibility")
fun bindVisibility(imageButton: ImageButton, isShrinked: Boolean) {
    imageButton.visibility = visibleOrGone(isShrinked)
}

@BindingAdapter("app:stopWatchIcon")
fun binsStopWatchIcon(imageButton: ImageButton, isStarting: Boolean) {
    val icStartingTimer = R.drawable.ic_starting_timer
    val icStoppingTimer = R.drawable.ic_stopping_timer

    imageButton.showImage(if (isStarting) icStartingTimer else icStoppingTimer)
}

fun visibleOrGone(isVisible: Boolean) = if (isVisible) View.VISIBLE else View.GONE
