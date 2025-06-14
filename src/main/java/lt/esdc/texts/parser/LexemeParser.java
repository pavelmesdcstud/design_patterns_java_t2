package lt.esdc.texts.parser;

import lt.esdc.texts.composite.Lexeme;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.exception.TextParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LexemeParser extends AbstractParser {
    private static final Logger logger = LogManager.getLogger(LexemeParser.class);

    private static final String ARITHMETIC_EXPRESSION_REGEX = ".*[\\d].*[+\\-*/].*";
    private static final String BITWISE_EXPRESSION_REGEX = ".*[\\d].*[&|^~<>].*";
    private static final String PUNCTUATION_REGEX = "[.,!?'\"():;-]";


    @Override
    public void parse(TextComponent component, String text) throws TextParseException {
        // A lexeme can be a word with punctuation attached, so we need to separate them.
        String wordPart = text.replaceAll(PUNCTUATION_REGEX, "");
        String punctuationPart = text.substring(wordPart.length());

        if (!wordPart.isEmpty()){
            component.add(Lexeme.word(wordPart));
        }

        if (!punctuationPart.isEmpty()) {
            component.add(Lexeme.punctuation(punctuationPart));
        }
    }
}
