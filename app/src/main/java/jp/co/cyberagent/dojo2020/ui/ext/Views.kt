package jp.co.cyberagent.dojo2020.ui.ext

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide

fun ImageView.showImage(@DrawableRes drawableId: Int) {
    val drawable = context.getDrawable(drawableId)

    Glide.with(context).load(drawable).into(this)
}

fun ImageView.showImage(uri: Uri?) {
    Glide.with(context).load(uri).into(this)
}