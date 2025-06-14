package lt.esdc.texts.interpreter;

import lt.esdc.texts.exception.TextParseException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class ArithmeticExpressionCalculator implements ExpressionCalculator {

    private final InfixToPostfixConverter converter = new InfixToPostfixConverter();

    private static final Map<String, UnaryOperator<Integer>> UNARY_OPERATIONS = Map.of(
            "u-", v -> -v
    );

    private static final Map<String, BinaryOperator<Integer>> BINARY_OPERATIONS = Map.of(
            "+", Integer::sum,
            "-", (a, b) -> a - b,
            "*", (a, b) -> a * b,
            "/", (a, b) -> a / b
    );

    @Override
    public String calculate(String expression) throws TextParseException {
        List<String> postfix = converter.convert(expression, false);
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
