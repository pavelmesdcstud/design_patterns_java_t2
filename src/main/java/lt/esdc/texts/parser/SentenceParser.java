package lt.esdc.texts.parser;

import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.exception.TextParseException;

public class SentenceParser extends AbstractParser {
    // Regex to split by lexemes (words, punctuation), keeping the delimiters
    private static final String LEXEME_REGEX = "\\s+";

    public SentenceParser(AbstractParser nextParser) {
        super(nextParser);
    }

    @Override
    public void parse(TextComponent component, String text) throws TextParseException {
        String[] lexemes = text.split(LEXEME_REGEX);
        for (String lexemeText : lexemes) {
            if (nextParser != null) {
                // The LexemeParser will create and add the final leaf components
                nextParser.parse(component, lexemeText);
            }
        }
    }
}
