package latex.model

interface Expression {
    fun mathLatex(): String
}

class TextExpression(private val text: String) : Expression {
    override fun mathLatex() = text.escapeLatexText()
}

abstract class BinOpExpression(protected val left: Expression, protected val right: Expression) : Expression {
    protected abstract val operation: String

    override fun mathLatex() = buildString {
        append(left.mathLatex())
        append(" ")
        append(operation)
        append(" ")
        append(right.mathLatex())
    }
}

class AddOperationExpression(left: Expression, right: Expression) : BinOpExpression(left, right) {
    override val operation: String = "+"
}
class SubOperationExpression(left: Expression, right: Expression) : BinOpExpression(left, right) {
    override val operation: String = "-"
}
class MulOperationExpression(left: Expression, right: Expression) : BinOpExpression(left, right) {
    override val operation: String = "\\cdot"
}

class DivOperationExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun mathLatex() = "\\frac{${left.mathLatex()}}{${right.mathLatex()}}"
}

class SqrtOperationExpression(private val left: Expression) : Expression {
    override fun mathLatex() = "\\sqrt{${left.mathLatex()}}"
}
