package jp.co.cyberagent.dojo2020.data.remote.firestore.memo

import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Content
import jp.co.cyberagent.dojo2020.data.model.Memo

data class MemoEntity(
    val id: String? = null,
    val title: String? = null,
    val contentText: String? = null,
    val time: Long? = null,
    val categoryName: String? = null
) {
    fun modelOrNull(): Memo? {
        val containsNull = listOf(id, title, contentText, time, categoryName).contains(null)
        if (containsNull) {
            return null
        }

        val category = Category(categoryName!!)
        val content = Content(contentText!!)

        return Memo(id!!, title!!, content, time!!, category)
    }

}