package lt.esdc.texts.composite;

import java.util.List;

public interface TextComponent {
    boolean add(TextComponent component);
    boolean remove(TextComponent component);
    List<TextComponent> getChildren();
    ComponentType getType();
    String toString();
}
