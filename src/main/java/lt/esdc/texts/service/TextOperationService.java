package lt.esdc.texts.service;

import lt.esdc.texts.composite.ComponentType;
import lt.esdc.texts.composite.TextComponent;
import lt.esdc.texts.composite.TextComposite;
import java.util.*;
import java.util.stream.Collectors;

public class TextOperationService {

    public TextComponent sortParagraphsBySentenceCount(TextComponent text) {
        List<TextComponent> paragraphs = new ArrayList<>(text.getChildren());
        paragraphs.sort(Comparator.comparingInt(p -> p.getChildren().size()));

        TextComponent sortedText = new TextComposite(ComponentType.TEXT);
        paragraphs.forEach(sortedText::add);
        return sortedText;
    }

    public List<TextComponent> findSentencesWithLongestWord(TextComponent text) {
        List<TextComponent> allSentences = new ArrayList<>();
        text.getChildren().forEach(p -> allSentences.addAll(p.getChildren()));

        if (allSentences.isEmpty()) {
            return Collections.emptyList();
        }

        int maxLength = 0;
        List<TextComponent> resultSentences = new ArrayList<>();

        for (TextComponent sentence : allSentences) {
            for (TextComponent lexeme : sentence.getChildren()) {
                if (lexeme.getType() == ComponentType.WORD) {
                    int currentLength = lexeme.toString().length();
                    if (currentLength > maxLength) {
                        maxLength = currentLength;
                        resultSentences.clear();
                        resultSentences.add(sentence);
                    } else if (currentLength == maxLength && !resultSentences.contains(sentence)) {
                        resultSentences.add(sentence);
                    }
                }
            }
        }
        return resultSentences;
    }

    public TextComponent removeSentencesWithWordCountLessThan(TextComponent text, int minWordCount) {
        TextComponent newText = new TextComposite(ComponentType.TEXT);
        for (TextComponent paragraph : text.getChildren()) {
            TextComponent newParagraph = new TextComposite(ComponentType.PARAGRAPH);
            for (TextComponent sentence : paragraph.getChildren()) {
                long wordCount = sentence.getChildren().stream()
                        .filter(l -> l.getType() == ComponentType.WORD)
                        .count();
                if (wordCount >= minWordCount) {
                    newParagraph.add(sentence);
                }
            }
            if (!newParagraph.getChildren().isEmpty()) {
                newText.add(newParagraph);
            }
        }
        return newText;
    }

    public Map<String, Long> countIdenticalWords(TextComponent text) {
        return text.getChildren().stream() // paragraphs
                .flatMap(p -> p.getChildren().stream()) // sentences
                .flatMap(s -> s.getChildren().stream()) // lexemes
                .filter(l -> l.getType() == ComponentType.WORD)
                .map(l -> l.toString().toLowerCase())
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }

    public Map<String, String> countVowelsAndConsonantsInSentences(TextComponent text) {
        Map<String, String> results = new LinkedHashMap<>();
        String vowels = "aeiouAEIOU";

        List<TextComponent> sentences = text.getChildren().stream()
                .flatMap(p -> p.getChildren().stream())
                .toList();

        for (int i = 0; i < sentences.size(); i++) {
            TextComponent sentence = sentences.get(i);
            long vowelCount = 0;
            long consonantCount = 0;
            for (TextComponent lexeme : sentence.getChildren()) {
                if (lexeme.getType() == ComponentType.WORD) {
                    for (char c : lexeme.toString().toCharArray()) {
                        if (Character.isLetter(c)) {
                            if (vowels.indexOf(c) != -1) {
                                vowelCount++;
                            } else {
                                consonantCount++;
                            }
                        }
                    }
                }
            }
            String result = String.format("Vowels: %d, Consonants: %d", vowelCount, consonantCount);
            results.put("Sentence " + (i + 1), result);
        }
        return results;
    }
}
