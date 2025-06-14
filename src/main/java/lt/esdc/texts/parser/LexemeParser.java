package lt.esdc.texts.parser;

import lt.esdc.texts.composite.Lexeme;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.exception.TextParseException;
import lt.esdc.texts.interpreter.ArithmeticExpressionCalculator;
import lt.esdc.texts.interpreter.BitwiseExpressionCalculator;
import lt.esdc.texts.interpreter.ExpressionCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LexemeParser extends AbstractParser {
    private static final Logger logger = LogManager.getLogger(LexemeParser.class);

    // Simple regex to identify potential expressions (contains numbers and operators)
    private static final String ARITHMETIC_EXPRESSION_REGEX = ".*[\\d].*[+\\-*/].*";
    private static final String BITWISE_EXPRESSION_REGEX = ".*[\\d].*[&|^~<>].*";
    private static final String PUNCTUATION_REGEX = "[.,!?'\"():;-]";

    private final ExpressionCalculator arithmeticCalculator = new ArithmeticExpressionCalculator();
    private final ExpressionCalculator bitwiseCalculator = new BitwiseExpressionCalculator();

    @Override
    public void parse(TextComponent component, String text) throws TextParseException {
        // A lexeme can be a word with punctuation attached, so we need to separate them.
        String wordPart = text.replaceAll(PUNCTUATION_REGEX, "");
        String punctuationPart = text.substring(wordPart.length());

        if (wordPart.matches(BITWISE_EXPRESSION_REGEX)) {
            try {
                String result = bitwiseCalculator.calculate(wordPart);
                component.add(Lexeme.expression(result));
                logger.info("Calculated bitwise expression '{}' -> '{}'", wordPart, result);
            } catch (TextParseException e) {
                logger.warn("Could not parse bitwise expression: {}. Treating as word.", wordPart, e);
                component.add(Lexeme.word(wordPart));
            }
        } else if (wordPart.matches(ARITHMETIC_EXPRESSION_REGEX)) {
            try {
                String result = arithmeticCalculator.calculate(wordPart);
                component.add(Lexeme.expression(result));
                logger.info("Calculated arithmetic expression '{}' -> '{}'", wordPart, result);
            } catch (TextParseException e) {
                logger.warn("Could not parse arithmetic expression: {}. Treating as word.", wordPart, e);
                component.add(Lexeme.word(wordPart));
            }
        } else if (!wordPart.isEmpty()){
            component.add(Lexeme.word(wordPart));
        }

        if (!punctuationPart.isEmpty()) {
            component.add(Lexeme.punctuation(punctuationPart));
        }
    }
}
