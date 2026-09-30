package util;

import model.OCRResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FileUtils {
    private FileUtils() { }
    public static void writeUtf8(Path path,String text) throws IOException {
        Path absolute=path.toAbsolutePath().normalize(); if(absolute.getParent()!=null) Files.createDirectories(absolute.getParent());
        Files.writeString(absolute,text,StandardCharsets.UTF_8);
    }
    public static String readUtf8(Path path) throws IOException { return Files.readString(path,StandardCharsets.UTF_8); }
    public static void appendHistory(Path path, OCRResult r) throws IOException {
        Path absolute=path.toAbsolutePath().normalize(); if(absolute.getParent()!=null) Files.createDirectories(absolute.getParent());
        String line=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" | "+r.getSourceImage()+" | "+r.getLanguage()+" | "+r.getCharacterCount()+" chars | "+r.getWordCount()+" words | "+String.format(java.util.Locale.ROOT,"%.2f",r.getProcessingTime())+" sec"+System.lineSeparator();
        Files.writeString(absolute,line,StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }
}
