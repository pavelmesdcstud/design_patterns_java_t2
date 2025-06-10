package lt.esdc.texts.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lt.esdc.texts.exception.TextParseException;

public class TextReader {
    public String readAll(String filePath) throws TextParseException {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            throw new TextParseException("Failed to read file: " + filePath, e);
        }
    }
}
