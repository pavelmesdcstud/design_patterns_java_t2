package lt.esdc.texts.interpreter;

import lt.esdc.texts.exception.TextParseException;

import java.util.*;

public class InfixToPostfixConverter {

    private static final Map<String, Integer> ARITHMETIC_PRIORITY = Map.of(
            "(", 0, ")", 0,
            "+", 1, "-", 1,
            "*", 2, "/", 2,
            "u-", 3 // Unary minus has high priority
    );

    private static final Map<String, Integer> BITWISE_PRIORITY = Map.of(
            "(", 0, ")", 0,
            "|", 1,
            "^", 2,
            "&", 3,
            "<<", 4, ">>", 4, ">>>", 4,
            "~", 5 // Bitwise NOT has high priority
    );

    public List<String> convert(String infix, boolean isBitwise) throws TextParseException { // <-- Added throws clause
        Map<String, Integer> priorities = isBitwise ? BITWISE_PRIORITY : ARITHMETIC_PRIORITY;
        List<String> postfix = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        // Tokenize the expression
        StringTokenizer tokenizer = new StringTokenizer(infix, "()+-*/&|^~<> \t", true);
        List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                if (!tokens.isEmpty() && (token.equals(">") || token.equals("<"))) {
                    String last = tokens.getLast();
                    if (last.equals(token) || last.equals(">>")) {
                        tokens.set(tokens.size() - 1, last + token);
                        continue;
                    }
                }
                tokens.add(token);
            }
        }

        String prevToken = "";
        for (String token : tokens) {
            if (isNumber(token)) {
                postfix.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                // FIX 1: Robustness check for mismatched ')'
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                if (stack.isEmpty()) { // This means we never found the opening '('
                    throw new TextParseException("Mismatched closing parenthesis in expression: " + infix);
                }
                stack.pop(); // Pop the opening bracket
            } else if (priorities.containsKey(token)) { // It's an operator
                // FIX 2: Improved unary minus/bitwise NOT detection
                boolean isUnary = (token.equals("-") || token.equals("~")) &&
                        (prevToken.isEmpty() || (priorities.containsKey(prevToken) && !prevToken.equals(")")));

                if (isUnary) {
                    stack.push(token.equals("-") ? "u-" : "~");
                } else {
                    while (!stack.isEmpty() && priorities.getOrDefault(stack.peek(), -1) >= priorities.get(token)) {
                        postfix.add(stack.pop());
                    }
                    stack.push(token);
                }
            }
            prevToken = token;
        }

        while (!stack.isEmpty()) {
            String top = stack.pop();
            if (top.equals("(")) {
                throw new TextParseException("Mismatched opening parenthesis in expression: " + infix);
            }
            postfix.add(top);
        }

        return postfix;
    }

    private boolean isNumber(String token) {
        return token.matches("-?\\d+");
    }
}
