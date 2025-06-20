package lt.esdc.texts;

import lt.esdc.texts.composite.ComponentType;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.composite.TextComposite;
import lt.esdc.texts.exception.TextParseException;
import lt.esdc.texts.parser.*;
import lt.esdc.texts.reader.TextReader;
import lt.esdc.texts.service.TextOperationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // 1. Setup reader and parser chain
            TextReader reader = new TextReader();
            AbstractParser parserChain = new TextParser(
                    new ParagraphParser(
                            new SentenceParser(
                                    new LexemeParser()
                            )
                    )
            );

            // 2. Read and parse the text
            URL resource = Main.class.getClassLoader().getResource("input.txt");
            if (resource == null) {
                logger.error("Resource 'input.txt' not found in classpath.");
                return;
            }
            Path filePath = Paths.get(resource.toURI());
            String textContent = reader.readAll(filePath.toString()); // Or better, change readAll to accept a Path
            TextComponent textComposite = new TextComposite(ComponentType.TEXT);
            parserChain.parse(textComposite, textContent);

            logger.info("--- Original Text Reconstructed from Composite ---");
            System.out.println(textComposite);
            System.out.println("\n" + "=".repeat(80) + "\n");

            // 3. Perform operations using the service
            TextOperationService service = new TextOperationService();

            logger.info("--- 1. Paragraphs sorted by sentence count ---");
            TextComponent sortedText = service.sortParagraphsBySentenceCount(textComposite);
            System.out.println(sortedText.toString());
            System.out.println("\n" + "=".repeat(80) + "\n");

            logger.info("--- 2. Sentences with the longest word ---");
            service.findSentencesWithLongestWord(textComposite)
                    .forEach(s -> System.out.println(s.toString()));
            System.out.println("\n" + "=".repeat(80) + "\n");


            logger.info("--- 3. Text after removing sentences with less than 10 words ---");
            TextComponent filteredText = service.removeSentencesWithWordCountLessThan(textComposite, 10);
            System.out.println(filteredText.toString());
            System.out.println("\n" + "=".repeat(80) + "\n");

            logger.info("--- 4. Count of identical words (case-insensitive) ---");
            service.countIdenticalWords(textComposite)
                    .forEach((word, count) -> System.out.printf("%-20s -> %d\n", word, count));
            System.out.println("\n" + "=".repeat(80) + "\n");


            logger.info("--- 5. Vowel and consonant count in each sentence ---");
            service.countVowelsAndConsonantsInSentences(textComposite)
                    .forEach((sentence, counts) -> System.out.printf("%-15s -> %s\n", sentence, counts));

        } catch (TextParseException e) {
            logger.error("An error occurred during text processing.", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
