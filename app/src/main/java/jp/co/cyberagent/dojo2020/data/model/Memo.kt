package jp.co.cyberagent.dojo2020.data.model

import java.util.*

data class Category(val name: String) : Comparable<Category> {
    override fun compareTo(other: Category): Int {
        if (this.name == other.name) return 0
        if (this.name == defaultCategory) return -1
        if (other.name == defaultCategory) return 1

        return this.name.compareTo(other.name)
    }

    companion object {
        const val defaultCategory = "None"
    }
}

data class Memo(
    val id: String,
    val title: String,
    val contents: String,
    val time: Long,
    val category: Category
) {
    companion object {
        fun createMemo(title: String, contents: String, time: Long, category: Category): Memo {
            val id = UUID.randomUUID().toString()

            return Memo(id, title, contents, time, category)
        }
    }
}