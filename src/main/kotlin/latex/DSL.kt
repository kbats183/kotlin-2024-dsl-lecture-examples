package latex

import latex.model.*
import java.nio.file.Path
import kotlin.io.path.writeText


@DslMarker
annotation class LatexDsl

@LatexDsl
class MathBuilder {
    val String.m: Expression
        get() = TextExpression(this)

    val Number.m: Expression
        get() = TextExpression(this.toString())

    operator fun Expression.plus(that: Expression): Expression {
        return AddOperationExpression(this, that)
    }

    operator fun Expression.minus(that: Expression): Expression {
        return SubOperationExpression(this, that)
    }

    operator fun Expression.times(that: Expression): Expression {
        return MulOperationExpression(this, that)
    }

    operator fun Expression.div(that: Expression): Expression {
        return DivOperationExpression(this, that)
    }

    val Expression.sqrt: Expression
        get() = SqrtOperationExpression(this)
}

@LatexDsl
class ParagraphBuilder {
    val elements = mutableListOf<InParagraphElement>()

    operator fun String.unaryPlus(): TextElement {
        elements.add(SeparatorElement)
        val el = TextElement(this)
        elements.add(el)
        return el
    }

    fun bold(init: ParagraphBuilder.() -> Unit) {
        elements.add(BoldFormatElement(ParagraphBuilder().apply(init).elements))
    }

    fun it(init: ParagraphBuilder.() -> Unit) {
        elements.add(ItalicsFormatElement(ParagraphBuilder().apply(init).elements))
    }

    fun tt(init: ParagraphBuilder.() -> Unit) {
        elements.add(TeletypeFormatElement(ParagraphBuilder().apply(init).elements))
    }

    infix fun InParagraphElement.linkTo(link: String) {
        if (this == elements.lastOrNull()) {
            elements.removeLast()
        }
        elements.add(HrefElement(this, link))
    }

    fun math(init: MathBuilder.() -> Expression) {
        elements.add(MathModeElement(MathBuilder().run(init)))
    }
}

@LatexDsl
class DocumentBuilder {
    val elements = mutableListOf<LatexElement>()

    fun title() {
        elements += TitleElement
    }

    fun paragraph(init: ParagraphBuilder.() -> Unit) {
        elements += ParagraphElement(ParagraphBuilder().apply(init).elements)
    }

    var title: String? = null
    var author: String? = null
    var date: String? = null
}

fun latex(type: String? = null, init: DocumentBuilder.() -> Unit): LatexDocument {
    val builder = DocumentBuilder().apply(init)
    return LatexDocument(
        type = type,
        title = builder.title,
        author = builder.author,
        date = builder.date,
        elements = builder.elements,
    )
}

fun main() {
    val document = latex("article") {
        title = "Article"
        author = "Any author"
        date = "Today"

        title()

        paragraph {
            +"Hello"

            +"World"

            bold {
                +"Bold"
                +"Bold too"
            }
        }

        paragraph {
            tt { +"Ttttt" }
        }

        paragraph {
            tt { +"Cursive" }

            +"Text text" linkTo "https://site.com"

            math {
                (1.m + 32.m / "x".m).sqrt
            }
        }
    }
    Path.of("output.tex").writeText(document.latex())
}
