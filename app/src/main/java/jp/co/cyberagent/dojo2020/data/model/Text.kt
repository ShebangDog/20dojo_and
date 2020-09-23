package jp.co.cyberagent.dojo2020.data.model

sealed class Text(
    val id: String,
    val title: String,
    val contents: String,
    val category: Category
) {
    data class Left(val value: Draft) : Text(value.id, value.title, value.content, value.category)
    data class Right(val value: Memo) : Text(value.id, value.title, value.contents, value.category)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + contents.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Text

        if (id != other.id) return false
        if (title != other.title) return false
        if (contents != other.contents) return false
        if (category != other.category) return false

        return true
    }
}

fun Draft.toText(): Text = Text.Left(this)
fun Memo.toText(): Text = Text.Right(this)