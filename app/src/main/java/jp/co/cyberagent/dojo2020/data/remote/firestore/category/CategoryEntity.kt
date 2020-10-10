package jp.co.cyberagent.dojo2020.data.remote.firestore.category

import android.graphics.Color
import jp.co.cyberagent.dojo2020.data.model.Category

data class CategoryEntity(
    val name: String? = null,
    val color: Color? = null
) {
    fun modelOrNull(): Category? {
        name ?: return null
        color ?: return null

        return Category(name, color)
    }
}