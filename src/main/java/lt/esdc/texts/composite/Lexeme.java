package lt.esdc.texts.composite;

import java.util.Collections;
import java.util.List;

public class Lexeme implements TextComponent {
    private final String value;
    private final ComponentType type;

    private Lexeme(String value, ComponentType type) {
        this.value = value;
        this.type = type;
    }

    public static Lexeme word(String value) {
        return new Lexeme(value, ComponentType.WORD);
    }

    public static Lexeme expression(String value) {
        return new Lexeme(value, ComponentType.EXPRESSION);
    }

    public static Lexeme punctuation(String value) {
        return new Lexeme(value, ComponentType.PUNCTUATION);
    }

    @Override
    public boolean add(TextComponent component) {
        // Leaf component
        return false;
    }

    @Override
    public boolean remove(TextComponent component) {
        // Leaf component
        return false;
    }

    @Override
    public List<TextComponent> getChildren() {
        // Leaf component
        return Collections.emptyList();
    }



    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public String toString() {
        return value;
    }
}
