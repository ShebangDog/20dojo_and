package jp.co.cyberagent.dojo2020.ui.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder

fun ImageView.showImage(
    @DrawableRes drawableId: Int,
    shape: (RequestBuilder<Drawable>.() -> RequestBuilder<Drawable>)? = null
) {
    val drawable = context.getDrawable(drawableId)

    Glide.with(context)
        .load(drawable)
        .let { if (shape != null) it.shape() else it }
        .into(this)
}

fun ImageView.showImage(
    uri: Uri?,
    shape: (RequestBuilder<Drawable>.() -> RequestBuilder<Drawable>)? = null
) {

    Glide.with(context)
        .load(uri)
        .let { if (shape != null) it.shape() else it }
        .into(this)
}