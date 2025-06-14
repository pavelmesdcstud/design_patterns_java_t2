package lt.esdc.texts.interpreter;

import lt.esdc.texts.exception.TextParseException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.Map;

public class BitwiseExpressionCalculator implements ExpressionCalculator {

    private final InfixToPostfixConverter converter = new InfixToPostfixConverter();

    private static final Map<String, UnaryOperator<Integer>> UNARY_OPERATIONS = Map.of(
            "~", v -> ~v
    );

    private static final Map<String, BinaryOperator<Integer>> BINARY_OPERATIONS = Map.of(
            "&", (a, b) -> a & b,
            "|", (a, b) -> a | b,
            "^", (a, b) -> a ^ b,
            "<<", (a, b) -> a << b,
            ">>", (a, b) -> a >> b,
            ">>>", (a, b) -> a >>> b
    );


    @Override
    public String calculate(String expression) throws TextParseException {
        String parsableExpression = expression
                .replaceAll("(?i)i", "1") // Replace 'i' with 1, as per example logic.
                .replaceAll("(?i)j", "1"); // Replace 'j' with 1

        List<String> postfix = converter.convert(parsableExpression, true);
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
                throw new TextParseException("Invalid bitwise expression: " + expression);
            }
            return stack.pop().toString();
        } catch (Exception e) {
            throw new TextParseException("Failed to calculate bitwise expression: " + expression, e);
        }
    }
}
