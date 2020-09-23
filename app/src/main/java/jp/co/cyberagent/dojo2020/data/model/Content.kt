package jp.co.cyberagent.dojo2020.data.model

class Content(value: String) {
    val text = value

    fun toOneLine() =
        if (text.contains("\n")) text.takeWhile { it != '\n' } + stringTerminated else text

    fun isShrinkable() = toOneLine().takeLastWhile { ch -> ch == addedPostFix }.length >= 3

    companion object {
        private const val stringTerminated = "..."
        private const val addedPostFix = '.'
    }
}