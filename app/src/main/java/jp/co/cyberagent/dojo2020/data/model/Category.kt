package jp.co.cyberagent.dojo2020.data.model

import android.content.Context
import jp.co.cyberagent.dojo2020.R
import kotlin.random.Random

data class Category(val name: String, val color: Color) : Comparable<Category> {

    init {
        require(name.length <= maxLength)
    }

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
        const val maxLength = 10

        fun defaultCategoryList(context: Context): List<Category> {
            val nameList = context.resources.getStringArray(R.array.category_list).toList()
            val colorList = context.resources.getStringArray(R.array.category_color_list).toList()
                .map { Color.parseColor(it) }

            return (nameList zip colorList)
                .map { (name, color) -> Category(name, color) }
        }
    }
}

class Color private constructor(internalColor: android.graphics.Color) {
    val value = internalColor.toArgb()

    companion object {
        val defaultColor = valueOf(android.graphics.Color.WHITE)

        fun valueOf(rgb: Int): Color {
            return Color(android.graphics.Color.valueOf(rgb))
        }

        fun valueOf(rgb: Int?): Color {
            return if (rgb == null) defaultColor else valueOf(rgb)
        }

        private fun valueOf(red: Int, green: Int, blue: Int): Color {
            return Color(
                android.graphics.Color.valueOf(
                    red.toFloat(), green.toFloat(),
                    blue.toFloat()
                )
            )
        }

        fun parseColor(colorCode: String): Color {
            return valueOf(android.graphics.Color.parseColor(colorCode))
        }

        fun pickColor(): Color {
            fun randomElem() = Random.nextInt(256)

            return valueOf(randomElem(), randomElem(), randomElem())
        }
    }
}