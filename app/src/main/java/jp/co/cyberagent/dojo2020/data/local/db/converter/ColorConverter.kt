package jp.co.cyberagent.dojo2020.data.local.db.converter

import android.graphics.Color
import androidx.room.TypeConverter

class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color): Int = color.toArgb()

    @TypeConverter
    fun intToColor(value: Int): Color = Color.valueOf(value)
}