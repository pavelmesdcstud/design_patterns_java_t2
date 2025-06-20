package lt.esdc.texts.parser;

import lt.esdc.texts.composite.Lexeme;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.exception.TextParseException;
import lt.esdc.texts.interpreter.ArithmeticExpressionCalculator;
import lt.esdc.texts.interpreter.BitwiseExpressionCalculator;
import lt.esdc.texts.interpreter.ExpressionCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexemeParser extends AbstractParser {
    private static final Logger logger = LogManager.getLogger(LexemeParser.class);

    // This pattern is now only used as a fallback for non-expressions.
    private static final Pattern LEXEME_PARTS_PATTERN = Pattern.compile("^([(\"'“]*)(\\S+?)([.,!?'\"”);:-]*)$");

    // --- CORRECTED REGEXES ---
    // A bitwise expression MUST contain a bitwise operator.
    private static final String BITWISE_EXPRESSION_REGEX = ".*[&|^~<>].*";
    // An arithmetic expression MUST contain an arithmetic operator.
    private static final String ARITHMETIC_EXPRESSION_REGEX = ".*[+\\-*/].*";

    private final ExpressionCalculator arithmeticCalculator = new ArithmeticExpressionCalculator();
    private final ExpressionCalculator bitwiseCalculator = new BitwiseExpressionCalculator();

    @Override
    public void parse(TextComponent component, String text) {
        // --- CORRECTED CONTROL FLOW (if-else if-else) ---
        if (text.matches(BITWISE_EXPRESSION_REGEX)) {
            try {
                String result = bitwiseCalculator.calculate(text);
                component.add(Lexeme.expression(result));
                logger.info("Calculated bitwise expression '{}' -> '{}'", text, result);
                return;
            } catch (TextParseException e) {
                logger.debug("Lexeme '{}' looked like a bitwise expression but failed to parse. Treating as word. Reason: {}", text, e.getMessage());
            }
        } else if (text.matches(ARITHMETIC_EXPRESSION_REGEX)) {
            try {
                String result = arithmeticCalculator.calculate(text);
                component.add(Lexeme.expression(result));
                logger.info("Calculated arithmetic expression '{}' -> '{}'", text, result);
                return;
            } catch (TextParseException e) {
                logger.debug("Lexeme '{}' looked like an arithmetic expression but failed to parse. Treating as word. Reason: {}", text, e.getMessage());
            }
        }

        // If we reached here, the lexeme is NOT a valid expression or failed parsing.
        // Fall back to dissecting it into word/punctuation parts.
        Matcher matcher = LEXEME_PARTS_PATTERN.matcher(text);
        if (!matcher.matches()) {
            if (!text.isBlank()) {
                component.add(Lexeme.word(text));
            }
            return;
        }

        String leadingPunctuation = matcher.group(1);
        String corePart = matcher.group(2);
        String trailingPunctuation = matcher.group(3);

        if (!leadingPunctuation.isEmpty()) {
            component.add(Lexeme.punctuation(leadingPunctuation));
        }

        if (!corePart.isEmpty()) {
            component.add(Lexeme.word(corePart));
        }

        if (!trailingPunctuation.isEmpty()) {
            component.add(Lexeme.punctuation(trailingPunctuation));
        }
    }
}
