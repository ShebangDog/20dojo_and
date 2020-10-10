package jp.co.cyberagent.dojo2020.data.local.db.category

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.co.cyberagent.dojo2020.data.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val color: Color
) {
    fun toModel(): Category {
        return Category(name, color)
    }
}