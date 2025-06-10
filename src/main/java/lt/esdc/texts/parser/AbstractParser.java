package lt.esdc.texts.parser;

import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.exception.TextParseException;

public abstract class AbstractParser {
    protected AbstractParser nextParser;

    public AbstractParser() {
    }

    public AbstractParser(AbstractParser nextParser) {
        this.nextParser = nextParser;
    }

    public void setNextParser(AbstractParser nextParser) {
        this.nextParser = nextParser;
    }

    public abstract void parse(TextComponent component, String text) throws TextParseException;
}
