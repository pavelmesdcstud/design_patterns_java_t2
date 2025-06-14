package lt.esdc.texts.interpreter;

import lt.esdc.texts.exception.TextParseException;

@FunctionalInterface
public interface ExpressionCalculator {
    String calculate(String expression) throws TextParseException;
}
