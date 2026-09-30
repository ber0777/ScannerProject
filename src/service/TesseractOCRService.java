package service;

import model.OCRResult;
import util.ImageUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TesseractOCRService implements OCRService {
    private static final String MISSING = "Tesseract OCR не найден.\n\nПроверьте:\n1. Установлен ли Tesseract.\n2. Правильно ли указан путь в config.properties.\n3. Установлены ли необходимые языковые модели.";
    private final Properties config = new Properties();

    public TesseractOCRService() {
        Path configPath = Path.of("config.properties");
        if (Files.isRegularFile(configPath)) {
            try (var reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                config.load(reader);
            } catch (IOException e) {
                System.err.println("Не удалось прочитать config.properties: " + e.getMessage());
            }
        }
    }

    public static boolean validLanguage(String language) {
        if (language == null || language.isBlank()) return false;
        for (String part : language.split("\\+")) {
            if (!part.equals("rus") && !part.equals("eng") && !part.equals("hye")) return false;
        }
        return true;
    }

    @Override
    public OCRResult recognize(String imagePath, String language) throws Exception {
        var sourceImage = ImageUtils.loadValidated(imagePath);
        if (!validLanguage(language)) {
            throw new IllegalArgumentException("Язык OCR должен быть rus, eng, hye либо их комбинацией через +.");
        }
        Path executable = resolveExecutable();
        Path output = Files.createTempFile("thesisscan-ocr-", ".txt");
        // Some Windows Tesseract builds cannot open filenames containing non-ASCII
        // characters. Java/ImageIO can read the original path, so give Tesseract a
        // lossless PNG copy with an ASCII-only path instead.
        Path tesseractInput = ImageUtils.writeTemp(sourceImage);
        try {
            var command = new java.util.ArrayList<String>();
            command.add(executable.toString());
            command.add(tesseractInput.toAbsolutePath().normalize().toString());
            command.add("stdout");
            command.add("-l");
            command.add(language);
            String tessdata = config.getProperty("tessdata.path", "").trim();
            if (!tessdata.isEmpty()) {
                command.add("--tessdata-dir");
                command.add(tessdata);
            }

            long start = System.nanoTime();
            Process process;
            try {
                process = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(output.toFile()).start();
            } catch (IOException e) {
                throw new IOException(MISSING + "\n\nУкажите полный путь к tesseract.exe в config.properties.", e);
            }
            if (!process.waitFor(2, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                throw new IOException("Превышено время ожидания Tesseract (2 минуты).");
            }
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            String outputText = Files.readString(output, StandardCharsets.UTF_8);
            if (process.exitValue() != 0) {
                String lowerOutput = outputText.toLowerCase(java.util.Locale.ROOT);
                if (lowerOutput.contains("error opening data file") || lowerOutput.contains("failed loading language")) {
                    throw new IOException("Не найдены языковые модели OCR для '" + language + "'. Установите соответствующие traineddata-файлы "
                            + "(rus, eng, hye) в tessdata либо задайте tessdata.path в config.properties.\n" + outputText.trim());
                }
                throw new IOException("Tesseract завершился с ошибкой (код " + process.exitValue() + "): " + outputText.trim());
            }
            return new OCRResult(outputText, elapsed, imagePath, language);
        } finally {
            Files.deleteIfExists(output);
            Files.deleteIfExists(tesseractInput);
        }
    }

    private Path resolveExecutable() throws IOException {
        String configured = config.getProperty("tesseract.path", "").trim();
        if (!configured.isEmpty()) {
            Path path = Path.of(configured).toAbsolutePath().normalize();
            if (Files.isRegularFile(path) && Files.isExecutable(path)) return path;
            throw new IOException("Путь tesseract.path из config.properties не указывает на доступный файл: " + path);
        }

        String pathValue = System.getenv("PATH");
        if (pathValue != null) {
            for (String entry : pathValue.split(java.util.regex.Pattern.quote(java.io.File.pathSeparator))) {
                Path candidate = Path.of(entry).resolve(isWindows() ? "tesseract.exe" : "tesseract");
                if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) return candidate.toAbsolutePath().normalize();
            }
        }

        if (isWindows()) {
            for (String candidate : List.of(
                    "C:/Program Files/Tesseract-OCR/tesseract.exe",
                    "C:/Program Files (x86)/Tesseract-OCR/tesseract.exe")) {
                Path path = Path.of(candidate);
                if (Files.isRegularFile(path) && Files.isExecutable(path)) return path;
            }
        }

        throw new IOException(MISSING + "\n\nУкажите путь к исполняемому файлу в config.properties, например:\n"
                + "tesseract.path=C:/Program Files/Tesseract-OCR/tesseract.exe");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("win");
    }
}
