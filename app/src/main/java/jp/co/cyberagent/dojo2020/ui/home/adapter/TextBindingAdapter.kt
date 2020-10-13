package jp.co.cyberagent.dojo2020.ui.home.adapter

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.ui.ext.showImage

@BindingAdapter("app:stopWatchIcon")
fun binsStopWatchIcon(imageButton: ImageButton, isStarting: Boolean) {
    val icStartingTimer = R.drawable.ic_starting_timer
    val icStoppingTimer = R.drawable.ic_stopping_timer

    imageButton.showImage(if (isStarting) icStartingTimer else icStoppingTimer)
}
