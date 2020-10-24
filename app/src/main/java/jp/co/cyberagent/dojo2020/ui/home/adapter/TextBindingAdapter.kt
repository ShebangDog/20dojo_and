package jp.co.cyberagent.dojo2020.ui.home.adapter

import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.ui.ext.showImage

@BindingAdapter("app:stopWatchIcon")
fun bindStopWatchIcon(imageButton: ImageButton, isStarting: Boolean) {
    val icStartingTimer = R.drawable.ic_starting_timer
    val icStoppingTimer = R.drawable.ic_stopping_timer

    imageButton.showImage(if (isStarting) icStartingTimer else icStoppingTimer)
}

@BindingAdapter("app:isShrink")
fun bindExpandIcon(imageButton: ImageButton, isShrink: Boolean) {
    val expandLessIcon = R.drawable.ic_expand_less
    val expandMoreIcon = R.drawable.ic_expand_more

    imageButton.showImage(if (isShrink) expandMoreIcon else expandLessIcon)
}

@BindingAdapter(value = ["app:isOneLine", "app:contentText"])
fun bindContentText(textView: TextView, isOneLine: Boolean, contentText: Text?) {
    contentText?.content.also { textView.text = if (isOneLine) it?.toOneLine() else it?.text }
}
