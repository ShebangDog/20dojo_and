package jp.co.cyberagent.dojo2020.data.model

import java.util.*

data class Memo(
    val id: String,
    val title: String,
    val contents: Content,
    val time: Long,
    val category: Category
) {

    fun toDraft(): Draft = Draft(id, title, contents, System.currentTimeMillis(), category)

    companion object {
        fun createMemo(title: String, contents: Content, time: Long, category: Category): Memo {
            val id = UUID.randomUUID().toString()

            return Memo(id, title, contents, time, category)
        }
    }
}