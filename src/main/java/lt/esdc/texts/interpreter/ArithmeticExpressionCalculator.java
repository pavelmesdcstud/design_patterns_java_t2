package lt.esdc.texts.interpreter;

import lt.esdc.texts.exception.TextParseException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArithmeticExpressionCalculator implements ExpressionCalculator {

    private final InfixToPostfixConverter converter = new InfixToPostfixConverter();

    // Patterns to find pre/post-increment/decrement
    private static final Pattern PRE_INCREMENT_PATTERN = Pattern.compile("\\+\\+(\\d+)");
    private static final Pattern POST_INCREMENT_PATTERN = Pattern.compile("(\\d+)\\+\\+");
    private static final Pattern PRE_DECREMENT_PATTERN = Pattern.compile("--(\\d+)");
    private static final Pattern POST_DECREMENT_PATTERN = Pattern.compile("(\\d+)--");

    private static final Map<String, UnaryOperator<Integer>> UNARY_OPERATIONS = Map.of(
            "u-", v -> -v
    );

    private static final Map<String, BinaryOperator<Integer>> BINARY_OPERATIONS = Map.of(
            "+", Integer::sum,
            "-", (a, b) -> a - b,
            "*", (a, b) -> a * b,
            "/", (a, b) -> a / b
    );

    // NEW METHOD: Pre-processor to handle non-standard syntax
    private String preprocessNonStandardSyntax(String expression) {
        // This method translates C-style increment/decrement into standard arithmetic
        // It is not stateful and handles each occurrence independently.
        String result = expression;

        Matcher preIncrementMatcher = PRE_INCREMENT_PATTERN.matcher(result);
        result = preIncrementMatcher.replaceAll(match -> Integer.toString(Integer.parseInt(match.group(1)) + 1));

        Matcher postIncrementMatcher = POST_INCREMENT_PATTERN.matcher(result);
        result = postIncrementMatcher.replaceAll(match -> Integer.toString(Integer.parseInt(match.group(1)) + 1));

        Matcher preDecrementMatcher = PRE_DECREMENT_PATTERN.matcher(result);
        result = preDecrementMatcher.replaceAll(match -> Integer.toString(Integer.parseInt(match.group(1)) - 1));

        Matcher postDecrementMatcher = POST_DECREMENT_PATTERN.matcher(result);
        result = postDecrementMatcher.replaceAll(match -> Integer.toString(Integer.parseInt(match.group(1)) - 1));

        return result;
    }


    @Override
    public String calculate(String expression) throws TextParseException {
        // Step 1: Pre-process the expression to normalize it
        String normalizedExpression = preprocessNonStandardSyntax(expression);

        // Step 2: Convert and calculate the now-standard expression
        List<String> postfix = converter.convert(normalizedExpression, false);
        Deque<Integer> stack = new ArrayDeque<>();
        try {
            for (String token : postfix) {
                if (token.matches("-?\\d+")) {
                    stack.push(Integer.parseInt(token));
                } else if (UNARY_OPERATIONS.containsKey(token)) {
                    int val = stack.pop();
                    stack.push(UNARY_OPERATIONS.get(token).apply(val));
                } else if (BINARY_OPERATIONS.containsKey(token)) {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(BINARY_OPERATIONS.get(token).apply(a, b));
                }
            }
            if (stack.size() != 1) {
                throw new TextParseException("Invalid arithmetic expression: " + expression);
            }
            return stack.pop().toString();
        } catch (Exception e) {
            throw new TextParseException("Failed to calculate arithmetic expression: " + expression, e);
        }
    }
}
