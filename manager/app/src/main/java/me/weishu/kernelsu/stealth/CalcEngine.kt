
```kotlin
package me.weishu.kernelsu.stealth

import java.math.BigDecimal
import java.math.RoundingMode

object CalcEngine {

    fun evaluate(expression: String): BigDecimal? = try {
        val parser = Parser(expression)
        val value = parser.parseExpression()
        parser.skipSpaces()
        if (parser.pos != expression.length) null else value
    } catch (t: Throwable) {
        null
    }

    fun format(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return if (stripped.scale() < 0) stripped.setScale(0).toPlainString()
        else stripped.toPlainString()
    }

    private class Parser(private val src: String) {
        var pos = 0

        fun skipSpaces() {
            while (pos < src.length && src[pos] == ' ') pos++
        }

        private fun peek(): Char? = if (pos < src.length) src[pos] else null

        fun parseExpression(): BigDecimal {
            var value = parseTerm()
            while (true) {
                skipSpaces()
                when (peek()) {
                    '+' -> { pos++; value = value.add(parseTerm()) }
                    '-' -> { pos++; value = value.subtract(parseTerm()) }
                    else -> return value
                }
            }
        }

        private fun parseTerm(): BigDecimal {
            var value = parseFactor()
            while (true) {
                skipSpaces()
                when (peek()) {
                    '×', '*' -> { pos++; value = value.multiply(parseFactor()) }
                    '÷', '/' -> {
                        pos++
                        val divisor = parseFactor()
                        if (divisor.signum() == 0) throw ArithmeticException("div by zero")
                        value = value.divide(divisor, 12, RoundingMode.HALF_UP)
                    }
                    else -> return value
                }
            }
        }

        private fun parseFactor(): BigDecimal {
            skipSpaces()
            when (peek()) {
                '-' -> { pos++; return parseFactor().negate() }
                '+' -> { pos++; return parseFactor() }
                '(' -> {
                    pos++
                    val value = parseExpression()
                    skipSpaces()
                    if (peek() != ')') throw IllegalArgumentException("missing )")
                    pos++
                    return value
                }
            }
            val start = pos
            while (pos < src.length && (src[pos].isDigit() || src[pos] == '.')) pos++
            if (start == pos) throw IllegalArgumentException("unexpected char at $pos")
            return BigDecimal(src.substring(start, pos))
        }
    }
}
```
