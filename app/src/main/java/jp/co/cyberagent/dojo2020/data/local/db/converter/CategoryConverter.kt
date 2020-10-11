package jp.co.cyberagent.dojo2020.data.local.db.converter

import androidx.room.TypeConverter
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Color

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): String? {
        return listOf(category.name, category.color.value).joinToString(delimiter)
    }

    @TypeConverter
    fun stringToCategory(string: String): Category {
        val elemList = string.split(delimiter).toList()

        val name = elemList.first()
        val color = Color.valueOf(elemList.drop(1).first().toIntOrNull())

        return Category(name, color)
    }

    companion object {
        const val delimiter = ","
    }
}