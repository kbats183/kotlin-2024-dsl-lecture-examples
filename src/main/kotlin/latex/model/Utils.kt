package latex.model

fun String.escapeLatexText(): String {
    return buildString {
        for (c in this@escapeLatexText) {
            when (c) {
                '#', '$', '%', '_', '^', '{', '}', '~', '\\' -> append("\\$c")
                else -> append(c)
            }
        }
    }
}
