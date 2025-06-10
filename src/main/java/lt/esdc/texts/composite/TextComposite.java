package lt.esdc.texts.composite;

import java.util.ArrayList;
import java.util.List;

public class TextComposite implements TextComponent {
    private final ComponentType type;
    private final List<TextComponent> components = new ArrayList<>();

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
        String delimiter = switch (type) {
            case TEXT -> "\n    ";
            case PARAGRAPH, SENTENCE -> " ";
            default -> "";
        };

        for (int i = 0; i < components.size(); i++) {
            TextComponent current = components.get(i);
            builder.append(current.toString());

            // Add delimiter intelligently
            if (i < components.size() - 1) {
                TextComponent next = components.get(i + 1);
                boolean noSpaceBeforePunctuation = next.getType() == ComponentType.PUNCTUATION
                        && !next.toString().equals("(");
                if (!noSpaceBeforePunctuation) {
                    builder.append(delimiter);
                }
            }
        }
        return builder.toString().trim();
    }
}
