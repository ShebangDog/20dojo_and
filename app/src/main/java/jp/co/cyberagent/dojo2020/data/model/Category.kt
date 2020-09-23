package jp.co.cyberagent.dojo2020.data.model

data class Category(val name: String) : Comparable<Category> {
    override fun compareTo(other: Category): Int {
        if (this.name == other.name) return 0
        if (this.name == defaultCategory) return -1
        if (other.name == defaultCategory) return 1

        return this.name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Category -> this.name == other.name
        else -> false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        const val defaultCategory = "None"
    }
}