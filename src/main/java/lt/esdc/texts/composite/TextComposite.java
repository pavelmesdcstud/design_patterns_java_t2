package lt.esdc.texts.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TextComposite implements TextComponent {
    private final ComponentType type;
    private final List<TextComponent> components = new ArrayList<>();

    // Punctuation that should not have a space before it.
    private static final Set<String> NO_SPACE_BEFORE = Set.of(".", ",", "!", "?", ";", ":", ")", "...");
    // Punctuation that should not have a space after it.
    private static final Set<String> NO_SPACE_AFTER = Set.of("(", "“", "\"");

    public TextComposite(ComponentType type) {
        this.type = type;
    }

    @Override
    public boolean add(TextComponent component) {
        return components.add(component);
    }

    @Override
    public boolean remove(TextComponent component) {
        return components.remove(component);
    }

    @Override
    public List<TextComponent> getChildren() {
        return List.copyOf(components);
    }

    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (type == ComponentType.TEXT) {
            // Special handling for top-level text to add paragraph indentation.
            for (int i = 0; i < components.size(); i++) {
                builder.append(components.get(i).toString());
                if (i < components.size() - 1) {
                    builder.append("\n    "); // Paragraph separator
                }
            }
            return builder.toString();
        }

        // Handling for PARAGRAPH and SENTENCE
        for (int i = 0; i < components.size(); i++) {
            TextComponent current = components.get(i);
            builder.append(current.toString());

            if (i < components.size() - 1) {
                TextComponent next = components.get(i + 1);
                // Add a space unless the current or next component is specific punctuation.
                if (!(NO_SPACE_BEFORE.contains(next.toString()) || NO_SPACE_AFTER.contains(current.toString()))) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }
}
