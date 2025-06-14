package lt.esdc.texts.interpreter;

import java.util.*;

public class InfixToPostfixConverter {

    private static final Map<String, Integer> ARITHMETIC_PRIORITY = Map.of(
            "(", 0, ")", 0,
            "+", 1, "-", 1,
            "*", 2, "/", 2
    );

    private static final Map<String, Integer> BITWISE_PRIORITY = Map.of(
            "(", 0, ")", 0,
            "|", 1,
            "^", 2,
            "&", 3,
            "<<", 4, ">>", 4, ">>>", 4,
            "~", 5
    );

    public List<String> convert(String infix, boolean isBitwise) {
        Map<String, Integer> priorities = isBitwise ? BITWISE_PRIORITY : ARITHMETIC_PRIORITY;
        List<String> postfix = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        // Tokenize the expression
        StringTokenizer tokenizer = new StringTokenizer(infix, "()+-*/&|^~<> \t", true);
        List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                // Handle multi-character operators like >>, <<, >>>
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
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                stack.pop(); // Pop the opening bracket
            } else if (priorities.containsKey(token)) { // It's an operator
                // Handle unary minus/bitwise NOT
                if ((token.equals("-") || token.equals("~")) && (prevToken.isEmpty() || prevToken.equals("("))) {
                    if(token.equals("-")) {
                        stack.push("u-"); // unary minus
                    } else {
                        stack.push("~"); // bitwise not
                    }
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
            postfix.add(stack.pop());
        }

        return postfix;
    }

    private boolean isNumber(String token) {
        return token.matches("-?\\d+");
    }
}
