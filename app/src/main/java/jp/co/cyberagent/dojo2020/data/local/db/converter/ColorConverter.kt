package jp.co.cyberagent.dojo2020.data.local.db.converter

import androidx.room.TypeConverter
import jp.co.cyberagent.dojo2020.data.model.Color

class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color): Int = color.value

    @TypeConverter
    fun intToColor(value: Int): Color = Color.valueOf(value)
}