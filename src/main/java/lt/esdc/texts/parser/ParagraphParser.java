package lt.esdc.texts.parser;

import lt.esdc.texts.composite.ComponentType;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.composite.TextComposite;
import lt.esdc.texts.exception.TextParseException;

public class ParagraphParser extends AbstractParser {
    private static final String SENTENCE_REGEX = "(?<=[.?!…])\\s+";

    public ParagraphParser(AbstractParser nextParser) {
        super(nextParser);
    }

    @Override
    public void parse(TextComponent component, String text) throws TextParseException {
        String[] sentences = text.split(SENTENCE_REGEX);
        for (String sentenceText : sentences) {
            if (!sentenceText.isBlank()) {
                TextComponent sentenceComponent = new TextComposite(ComponentType.SENTENCE);
                component.add(sentenceComponent);
                if (nextParser != null) {
                    nextParser.parse(sentenceComponent, sentenceText);
                }
            }
        }
    }
}
