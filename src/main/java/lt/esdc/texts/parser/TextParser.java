package lt.esdc.texts.parser;

import lt.esdc.texts.composite.ComponentType;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.composite.TextComposite;
import lt.esdc.texts.exception.TextParseException;

public class TextParser extends AbstractParser {
    private static final String PARAGRAPH_REGEX = "(?m)(?=^\\s{4,})";

    public TextParser(AbstractParser nextParser) {
        super(nextParser);
    }

    @Override
    public void parse(TextComponent component, String text) throws TextParseException {
        String[] paragraphs = text.trim().split(PARAGRAPH_REGEX);
        for (String paragraphText : paragraphs) {
            if (!paragraphText.isBlank()) {
                TextComponent paragraphComponent = new TextComposite(ComponentType.PARAGRAPH);
                component.add(paragraphComponent);
                if (nextParser != null) {
                    nextParser.parse(paragraphComponent, paragraphText.trim());
                }
            }
        }
    }
}
