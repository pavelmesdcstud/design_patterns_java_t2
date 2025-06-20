package lt.esdc.texts.service;

import lt.esdc.texts.composite.ComponentType;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.composite.TextComposite;
import lt.esdc.texts.exception.TextParseException;
import lt.esdc.texts.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TextOperationServiceTest {

    private TextOperationService service;
    private TextComponent textComposite;

    private static final String TEST_TEXT =
            "    This is the second paragraph. It only has one sentence.\n" +
                    "    First paragraph, first sentence. Second sentence has a supercalifragilisticexpialidocious word. Third sentence.\n" +
                    "    The third paragraph is here. Sentence two is short. Sentence three is also short. And sentence four.";

    @BeforeEach
    void setUp() throws TextParseException {
        service = new TextOperationService();

        AbstractParser parserChain = new TextParser(
                new ParagraphParser(
                        new SentenceParser(
                                new LexemeParser()
                        )
                )
        );
        textComposite = new TextComposite(ComponentType.TEXT);
        parserChain.parse(textComposite, TEST_TEXT);
    }

    @Test
    void sortParagraphsBySentenceCount_SortsCorrectly() {
        // Act
        TextComponent sortedText = service.sortParagraphsBySentenceCount(textComposite);
        List<TextComponent> sortedParagraphs = sortedText.getChildren();

        // Assert
        // The paragraphs are correctly parsed as having 2, 3, and 4 sentences respectively.
        assertThat(sortedParagraphs).hasSize(3);
        // Paragraph 1 (original #1) has 2 sentences
        assertThat(sortedParagraphs.get(0).getChildren()).hasSize(2); // FIX: Was 1
        // Paragraph 2 (original #2) has 3 sentences
        assertThat(sortedParagraphs.get(1).getChildren()).hasSize(3);
        // Paragraph 3 (original #3) has 4 sentences
        assertThat(sortedParagraphs.get(2).getChildren()).hasSize(4);
    }

    @Test
    void findSentencesWithLongestWord_FindsCorrectSentence() {
        // Act
        List<TextComponent> sentences = service.findSentencesWithLongestWord(textComposite);

        // Assert (This test was likely correct already)
        assertThat(sentences).hasSize(1);
        assertThat(sentences.getFirst().toString()).contains("supercalifragilisticexpialidocious");
    }

    @Test
    void removeSentencesWithWordCountLessThan_RemovesCorrectly() {
        // Act: Remove sentences with fewer than 5 words
        TextComponent filteredText = service.removeSentencesWithWordCountLessThan(textComposite, 5);
        List<TextComponent> paragraphs = filteredText.getChildren();

        // Assert
        // Correct parsing leads to different counts than originally assumed.
        // P1 (2 sents): 5 words, 5 words -> Both kept.
        // P2 (3 sents): 4 words, 7 words, 2 words -> Only the 7-word one is kept.
        // P3 (4 sents): 5 words, 4 words, 5 words, 3 words -> The two 5-word ones are kept.
        assertThat(paragraphs).hasSize(3);
        assertThat(paragraphs.get(0).getChildren()).hasSize(2); // FIX: Was 1
        assertThat(paragraphs.get(1).getChildren()).hasSize(1); // Unchanged
        assertThat(paragraphs.get(2).getChildren()).hasSize(2); // Unchanged

        assertThat(filteredText.toString()).doesNotContain("Third sentence."); // 2 words
        assertThat(filteredText.toString()).doesNotContain("Sentence two is short."); // 4 words
    }

    @Test
    void removeSentencesWithWordCountLessThan_RemovesEmptyParagraphs() {
        // Act: High threshold to remove all sentences
        TextComponent filteredText = service.removeSentencesWithWordCountLessThan(textComposite, 8);
        List<TextComponent> paragraphs = filteredText.getChildren();

        // Assert
        // The longest sentence has 7 words ("Second sentence has a super...").
        // Since 7 < 8, this sentence is removed. All other sentences are also removed.
        // The resulting text component should have zero paragraphs.
        assertThat(paragraphs).isEmpty(); // FIX: Was hasSize(1)
    }

    @Test
    void countIdenticalWords_CountsCorrectlyAndIsCaseInsensitive() {
        // Act
        Map<String, Long> wordCounts = service.countIdenticalWords(textComposite);

        // Assert
        // A correct manual count shows 'sentence' appears 7 times, not 5.
        assertThat(wordCounts)
                .containsEntry("sentence", 7L)
                .containsEntry("is", 4L)
                .containsEntry("the", 2L)
                .containsEntry("paragraph", 3L)
                .containsEntry("a", 1L)
                .containsEntry("word", 1L)
                .doesNotContainKey("paragraph,");
    }

    @Test
    void countVowelsAndConsonantsInSentences_CalculatesCorrectly() {
        // Arrange (This test was correct as it used its own simple text)
        String simpleText = "    Hi there. Go now!";
        TextComponent simpleComposite = new TextComposite(ComponentType.TEXT);
        try {
            new TextParser(new ParagraphParser(new SentenceParser(new LexemeParser())))
                    .parse(simpleComposite, simpleText);
        } catch (TextParseException e) {
            // Should not happen in test
        }

        // Act
        Map<String, String> counts = service.countVowelsAndConsonantsInSentences(simpleComposite);

        // Assert
        assertThat(counts)
                .hasSize(2)
                .containsEntry("Sentence 1", "Vowels: 3, Consonants: 4")
                .containsEntry("Sentence 2", "Vowels: 2, Consonants: 3");
    }
}
