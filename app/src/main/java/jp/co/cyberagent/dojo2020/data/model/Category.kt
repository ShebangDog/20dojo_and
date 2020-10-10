package jp.co.cyberagent.dojo2020.data.model

import android.content.Context
import android.graphics.Color
import jp.co.cyberagent.dojo2020.R

data class Category(val name: String, val color: Color) : Comparable<Category> {

    override fun compareTo(other: Category): Int {
        if (this.name == other.name) return 0
        if (this.name == defaultCategory) return -1
        if (other.name == defaultCategory) return 1

        return this.name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Category -> this.name == other.name
        else -> false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        const val defaultCategory = "None"

        fun defaultCategoryList(context: Context): List<Category> {
            val nameList = context.resources.getStringArray(R.array.category_list).toList()
            val colorList = context.resources.getStringArray(R.array.category_color_list).toList()
                .map { Color.parseColor(it) }

            return (nameList zip colorList)
                .map { (name, color) -> Category(name, Color.valueOf(color)) }
        }
    }
}
