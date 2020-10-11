package jp.co.cyberagent.dojo2020.data.remote.firestore.memo

import jp.co.cyberagent.dojo2020.data.model.Content
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.remote.firestore.category.CategoryEntity

data class MemoEntity(
    val id: String? = null,
    val title: String? = null,
    val content: ContentEntity? = null,
    val time: Long? = null,
    val category: CategoryEntity? = null
) {
    fun modelOrNull(): Memo? {
        val nullableCategory = category?.modelOrNull()
        val nullableContent = content?.modelOrNull()

        val containsNull = listOf(
            id,
            title,
            nullableContent?.text,
            time,
            nullableCategory?.name,
            nullableCategory?.color
        ).contains(null)

        if (containsNull) {
            return null
        }

        return Memo(id!!, title!!, nullableContent!!, time!!, nullableCategory!!)
    }

}

data class ContentEntity(val text: String? = null) {
    fun modelOrNull(): Content? {
        text ?: return null

        return Content(text)
    }
}