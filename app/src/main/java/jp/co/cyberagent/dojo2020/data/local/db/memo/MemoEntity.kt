package jp.co.cyberagent.dojo2020.data.local.db.memo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Content
import jp.co.cyberagent.dojo2020.data.model.Memo
import java.util.*

@Entity(tableName = "memos")
data class MemoEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "contents")
    val contents: Content,

    @ColumnInfo(name = "time")
    val time: Long,

    @ColumnInfo(name = "category")
    val category: Category
) {
    fun toModel(): Memo {
        return Memo(id, title, contents, time, category)
    }
}