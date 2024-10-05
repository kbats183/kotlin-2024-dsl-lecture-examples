package latex.model

interface LatexElement {
    fun latex(): String
}

object TitleElement : LatexElement {
    override fun latex() = "\\maketitle\n"
}

class ParagraphElement(private val elements: List<InParagraphElement>) : LatexElement {
    override fun latex(): String = elements.joinToString("", transform = LatexElement::latex) + "\n\n"
}

interface InParagraphElement : LatexElement

object SeparatorElement : InParagraphElement {
    override fun latex() = "\n"
}

class TextElement(private val text: String) : InParagraphElement {
    override fun latex() = text.escapeLatexText()
}

abstract class FormatElement(private val elements: List<LatexElement>) : InParagraphElement {
    abstract val command: String

    override fun latex(): String {
        return "\\$command{${elements.joinToString("", transform = LatexElement::latex)}}"
    }
}

class BoldFormatElement(elements: List<LatexElement>) : FormatElement(elements) {
    override val command: String = "textbf"
}

class ItalicsFormatElement(elements: List<LatexElement>) : FormatElement(elements) {
    override val command: String = "textit"
}

class TeletypeFormatElement(elements: List<LatexElement>) : FormatElement(elements) {
    override val command: String = "texttt"
}

class HrefElement(private val element: InParagraphElement, private val url: String) : InParagraphElement {
    override fun latex(): String {
        return "\\href{${url.escapeLatexText()}}{${element.latex()}}"
    }
}

class MathModeElement(private val expression: Expression) : InParagraphElement {
    override fun latex() = "$${expression.mathLatex()}$"
}

class LatexDocument(
    private val type: String? = "article",
    private val title: String? = null,
    private val author: String? = null,
    private val date: String? = null,
    private val elements: List<LatexElement>
) {
    fun latex(): String {
        return buildString {
            appendLine("\\documentclass{${type ?: "article"}}")
            title?.let { appendLine("\\title{${it}}") }
            author?.let { appendLine("\\author{${it}}") }
            date?.let { appendLine("\\date{${it}}") }
            appendLine()
            appendLine("\\usepackage{hyperref}")
            appendLine()
            appendLine("\\begin{document}")
            appendLine(elements.joinToString("") { it.latex() })
            appendLine("\\end{document}")
        }
    }
}
