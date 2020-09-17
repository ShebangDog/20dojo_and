package jp.co.cyberagent.dojo2020.ui.ext

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide

fun ImageView.showImage(viewBinding: ViewBinding, @DrawableRes drawableId: Int) {
    val drawable = viewBinding.root.context.getDrawable(drawableId)

    Glide.with(viewBinding.root).load(drawable).into(this)
}

fun ImageView.showImage(viewBinding: ViewBinding, uri: Uri?) {
    Glide.with(viewBinding.root).load(uri).into(this)
}