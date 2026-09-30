package service;

public class TextStatisticsService {
    public record Statistics(int characters, int charactersWithoutWhitespace, int words, int lines, int sentences) { }
    public Statistics calculate(String text) {
        if (text == null) text = "";
        int chars = text.length();
        int noWhitespace = (int) text.codePoints().filter(c -> !Character.isWhitespace(c)).count();
        String trimmed = text.trim();
        int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        int lines = text.isEmpty() ? 0 : text.split("\\R", -1).length;
        int sentences = 0;
        for (String s : text.split("[.!?։]+")) if (!s.isBlank()) sentences++;
        return new Statistics(chars, noWhitespace, words, lines, sentences);
    }
}
