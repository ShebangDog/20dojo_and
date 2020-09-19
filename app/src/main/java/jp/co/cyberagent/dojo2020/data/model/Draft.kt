package jp.co.cyberagent.dojo2020.data.model

import java.util.*

data class Draft(
    val id: String,
    val title: String,
    val content: String,
    val startTime: Long,
    val category: Category
) {

    fun toMemo(totalTime: Long) = Memo(id, title, content, totalTime, category)

    companion object {
        fun createDraft(title: String, content: String, category: Category): Draft {
            val id = UUID.randomUUID().toString()
            val startTime = System.currentTimeMillis()

            return Draft(id, title, content, startTime, category)
        }
    }
}