package lt.esdc.texts.parser;

import lt.esdc.texts.composite.ComponentType;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.composite.TextComposite;
import lt.esdc.texts.exception.TextParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParserChainIntegrationTest {

    private static AbstractParser parserChain;

    @BeforeAll
    static void setupParserChain() {
        parserChain = new TextParser(
                new ParagraphParser(
                        new SentenceParser(
                                new LexemeParser()
                        )
                )
        );
    }

    @Test
    void parse_TextWithExpressionsAndPunctuation_BuildsCorrectCompositeTree() throws TextParseException {
        // Arrange
        String text = "    Hello (world). It is 5+3 now. Bye bye!";
        TextComponent textComposite = new TextComposite(ComponentType.TEXT);

        // Act
        parserChain.parse(textComposite, text);

        // Assert
        // Top Level (TEXT)
        assertThat(textComposite.getType()).isEqualTo(ComponentType.TEXT);
        assertThat(textComposite.getChildren()).hasSize(1); // 1 paragraph

        // Paragraph Level
        TextComponent paragraph = textComposite.getChildren().getFirst();
        assertThat(paragraph.getType()).isEqualTo(ComponentType.PARAGRAPH);
        assertThat(paragraph.getChildren()).hasSize(3); // 3 sentences

        // Sentence 1
        TextComponent sentence1 = paragraph.getChildren().getFirst();
        assertThat(sentence1.getType()).isEqualTo(ComponentType.SENTENCE);
        assertThat(sentence1.getChildren()).hasSize(5);
        assertThat(sentence1.getChildren().get(0).toString()).isEqualTo("Hello");
        assertThat(sentence1.getChildren().get(1).toString()).isEqualTo("(");
        assertThat(sentence1.getChildren().get(2).toString()).isEqualTo("world");
        assertThat(sentence1.getChildren().get(3).toString()).isEqualTo(")");
        assertThat(sentence1.getChildren().get(4).toString()).isEqualTo("."); // Add assertion for the period


        // Sentence 2
        TextComponent sentence2 = paragraph.getChildren().get(1);
        assertThat(sentence2.getType()).isEqualTo(ComponentType.SENTENCE);
        assertThat(sentence2.getChildren()).hasSize(5);
        assertThat(sentence2.getChildren().get(2).getType()).isEqualTo(ComponentType.EXPRESSION);
        assertThat(sentence2.getChildren().get(2).toString()).isEqualTo("8"); // 5+3 was calculated

        TextComponent sentence3 = paragraph.getChildren().get(2);
        assertThat(sentence3.getType()).isEqualTo(ComponentType.SENTENCE);
        assertThat(sentence3.getChildren()).hasSize(3);

        String reconstructed = textComposite.toString().replaceAll("\\s+", " ").trim();
        String expected = "Hello (world). It is 8 now. Bye bye!".replaceAll("\\s+", " ").trim();
        assertThat(reconstructed).isEqualTo(expected);
    }
}
