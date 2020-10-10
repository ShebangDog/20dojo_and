package jp.co.cyberagent.dojo2020.data.local.db.converter

import android.graphics.Color
import androidx.room.TypeConverter
import jp.co.cyberagent.dojo2020.data.model.Category

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): String? {
        return listOf(category.name, category.color.toArgb()).joinToString(delimiter)
    }

    @TypeConverter
    fun stringToCategory(string: String): Category {
        val elemList = string.split(delimiter).toList()

        val name = elemList.first()
        val color = Color.valueOf(elemList.drop(1).first().toIntOrNull() ?: Color.WHITE)

        return Category(name, color)
    }

    companion object {
        const val delimiter = ","
    }
}