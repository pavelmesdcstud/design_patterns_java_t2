package lt.esdc.texts.reader;

import lt.esdc.texts.exception.TextParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextReaderTest {

    private final TextReader textReader = new TextReader();

    @Test
    void readAll_ValidFile_ReturnsContent(@TempDir Path tempDir) throws IOException, TextParseException {
        Path file = tempDir.resolve("test.txt");
        String expectedContent = "Hello, this is a test.";
        Files.writeString(file, expectedContent);
        String actualContent = textReader.readAll(file.toString());
        assertThat(actualContent).isEqualTo(expectedContent);
    }

    @Test
    void readAll_EmptyFile_ReturnsEmptyString(@TempDir Path tempDir) throws IOException, TextParseException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        String actualContent = textReader.readAll(file.toString());

        assertThat(actualContent).isEmpty();
    }

    @Test
    void readAll_FileDoesNotExist_ThrowsTextParseException() {
        String nonExistentFilePath = "path/to/non/existent/file.txt";

        assertThatThrownBy(() -> textReader.readAll(nonExistentFilePath))
                .isInstanceOf(TextParseException.class)
                .hasMessage("Failed to read file: " + nonExistentFilePath)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void readAll_PathIsDirectory_ThrowsTextParseException(@TempDir Path tempDir) {
        String directoryPath = tempDir.toString();

        assertThatThrownBy(() -> textReader.readAll(directoryPath))
                .isInstanceOf(TextParseException.class)
                .hasMessage("Failed to read file: " + directoryPath)
                .hasCauseInstanceOf(IOException.class);
    }
}
