package lt.esdc.texts.parser;

import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.exception.TextParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceParser extends AbstractParser {
    private static final Pattern LEXEME_PATTERN = Pattern.compile("[^\\s]+");


    public SentenceParser(AbstractParser nextParser) {
        super(nextParser);
    }

    @Override
    public void parse(TextComponent component, String text) throws TextParseException {
        Matcher matcher = LEXEME_PATTERN.matcher(text);
        while (matcher.find()) {
            String lexemeText = matcher.group();
            if (nextParser != null) {
                nextParser.parse(component, lexemeText);
            }
        }
    }
}
