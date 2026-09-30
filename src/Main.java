import model.OCRResult;
import service.ImagePreprocessor;
import service.OCRService;
import service.TesseractOCRService;
import service.TextStatisticsService;
import util.FileUtils;
import util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
//C:\Users\LOQ\OneDrive\Рисунки\Screenshots\Screenshot 2026-08-10 094425.png
public class Main {
    private final Scanner in = new Scanner(System.in);
    private final OCRService ocr = new TesseractOCRService();
    private final ImagePreprocessor preprocessor = new ImagePreprocessor();
    private final TextStatisticsService statistics = new TextStatisticsService();
    private OCRResult latest;

    public static void main(String[] args) {
        Main app = new Main();
        if (args.length >= 2) {
            app.commandMode(args[0], args[1]);
            return;
        }
        app.menu();
    }

    private void menu() {
        while (true) {
            System.out.println("\n=================================\n          ThesisScan\n=================================\n\n1. Распознать изображение\n2. Распознать выделенную область\n3. Статистика текста\n4. Сохранить последний результат\n5. Показать последний результат\n0. Выход\n");
            String choice = prompt("Выберите действие: ").trim();
            try {
                switch (choice) {
                    case "1" -> recognize(false);
                    case "2" -> recognize(true);
                    case "3" -> showStatistics();
                    case "4" -> saveLatest();
                    case "5" -> showLatest();
                    case "0" -> { System.out.println("До свидания."); return; }
                    default -> System.out.println("Неизвестный пункт меню.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + friendly(e));
            }
        }
    }

    private void recognize(boolean crop) throws Exception {
        String source = prompt("Введите путь к изображению: ").trim();
        BufferedImage image = ImageUtils.loadValidated(source);
        Path ocrInput = Path.of(source);
        Path temporary = null;
        try {
            if (crop) {
                int x = integer("Введите X: "), y = integer("Введите Y: ");
                int w = integer("Введите ширину: "), h = integer("Введите высоту: ");
                BufferedImage sub = ImageUtils.crop(image, x, y, w, h);
                temporary = ImageUtils.writeTemp(sub);
                ocrInput = temporary;
            }
            BufferedImage chosen = ImageUtils.loadValidated(ocrInput.toString());
            int mode = processingChoice();
            if (mode != 1) {
                BufferedImage processed = preprocessor.process(chosen, mode);
                temporary = ImageUtils.writeTemp(processed);
                ocrInput = temporary;
            }
            String language = languageChoice();
            latest = ocr.recognize(ocrInput.toString(), language);
            latest.setSourceImage(source);
            latest.setLanguage(language);
            latest.setStatistics(statistics.calculate(latest.getRecognizedText()));
            FileUtils.appendHistory(Path.of("data/history.txt"), latest);
            display(latest);
            if (prompt("Сохранить результат? (y/n): ").trim().equalsIgnoreCase("y")) saveLatest();
        } finally {
            if (temporary != null) try { java.nio.file.Files.deleteIfExists(temporary); } catch (IOException ignored) { }
        }
    }

    private int processingChoice() {
        System.out.println("Предварительная обработка:\n1. Без обработки\n2. Grayscale\n3. Grayscale + Contrast\n4. Grayscale + Binary\n5. Увеличить изображение");
        while (true) {
            int c = integer("Ваш выбор: ");
            if (c >= 1 && c <= 5) return c;
            System.out.println("Выберите число от 1 до 5.");
        }
    }

    private String languageChoice() {
        System.out.println("Выберите язык:\n1. Русский\n2. Английский\n3. Армянский\n4. Ввести комбинацию (например rus+eng)");
        while (true) {
            String c = prompt("Ваш выбор: ").trim();
            String lang = switch (c) { case "1" -> "rus"; case "2" -> "eng"; case "3" -> "hye"; case "4" -> prompt("Языки (rus, eng, hye через +): ").trim(); default -> c; };
            if (TesseractOCRService.validLanguage(lang)) return lang;
            System.out.println("Допустимы языки rus, eng, hye и их комбинации через +.");
        }
    }

    private void commandMode(String image, String language) {
        try {
            if (!TesseractOCRService.validLanguage(language)) throw new IllegalArgumentException("Поддерживаются rus, eng, hye и их комбинации через +.");
            ImageUtils.loadValidated(image);
            latest = ocr.recognize(image, language);
            latest.setSourceImage(image); latest.setLanguage(language);
            latest.setStatistics(statistics.calculate(latest.getRecognizedText()));
            FileUtils.appendHistory(Path.of("data/history.txt"), latest);
            System.out.println("OCR completed.\n\nCharacters: " + latest.getCharacterCount() + "\nWords: " + latest.getWordCount() + "\nLines: " + latest.getLineCount() + "\nTime: " + String.format("%.2f", latest.getProcessingTime()) + " sec\n\nResult:\n\n" + latest.getRecognizedText());
        } catch (Exception e) { System.out.println("Ошибка: " + friendly(e)); }
    }

    private void display(OCRResult r) {
        var s = r.getStatistics();
        System.out.println("\n=================================\nРЕЗУЛЬТАТ\n=================================\n\nРаспознанный текст:\n\n" + r.getRecognizedText() + "\n\n---------------------------------\n\nСтатистика:\n\nСимволов: " + s.characters() + "\nСимволов без пробелов: " + s.charactersWithoutWhitespace() + "\nСлов: " + s.words() + "\nСтрок: " + s.lines() + "\nПредложений: " + s.sentences() + "\n\nВремя OCR: " + String.format("%.2f", r.getProcessingTime()) + " сек.\n=================================");
    }
    private void showLatest() { if (latest == null) { System.out.println("Результатов распознавания пока нет."); return; } display(latest); }
    private void showStatistics() { if (latest == null) { System.out.println("Результатов распознавания пока нет."); return; } display(latest); }
    private void saveLatest() {
        if (latest == null) { System.out.println("Результатов распознавания пока нет."); return; }
        try { String path = prompt("Введите путь для сохранения: ").trim(); FileUtils.writeUtf8(Path.of(path), latest.getRecognizedText()); System.out.println("Результат сохранён: " + path); }
        catch (Exception e) { System.out.println("Не удалось сохранить: " + friendly(e)); }
    }
    private int integer(String label) { while (true) try { return Integer.parseInt(prompt(label).trim()); } catch (NumberFormatException e) { System.out.println("Введите целое число."); } }
    private String prompt(String text) { System.out.print(text); return in.nextLine(); }
    private static String friendly(Exception e) { return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage(); }
}
