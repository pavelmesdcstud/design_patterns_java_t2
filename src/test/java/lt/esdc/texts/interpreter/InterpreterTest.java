package lt.esdc.texts.interpreter;

import lt.esdc.texts.exception.TextParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterpreterTests {

    @Nested
    @DisplayName("ArithmeticExpressionCalculator Tests")
    class ArithmeticCalculatorTests {
        private final ExpressionCalculator calculator = new ArithmeticExpressionCalculator();

        @Test
        void calculate_InvalidExpression_ThrowsException() {
            assertThatThrownBy(() -> calculator.calculate("5 * (3 + 2"))
                    .isInstanceOf(TextParseException.class);
        }
    }

    @Nested
    @DisplayName("BitwiseExpressionCalculator Tests")
    class BitwiseCalculatorTests {
        private final ExpressionCalculator calculator = new BitwiseExpressionCalculator();

        @Test
        void calculate_InvalidExpression_ThrowsException() {
            assertThatThrownBy(() -> calculator.calculate("5 | (3 & 2"))
                    .isInstanceOf(TextParseException.class);
        }
    }
}
