package jp.co.cyberagent.dojo2020.data.local.db.converter

import androidx.room.TypeConverter
import jp.co.cyberagent.dojo2020.data.model.Content

class ContentConverter {
    @TypeConverter
    fun fromContent(content: Content): String? {
        return content.text
    }

    @TypeConverter
    fun toContent(string: String): Content {
        return Content(string)
    }
}