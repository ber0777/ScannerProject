package model;

import service.TextStatisticsService.Statistics;

public class OCRResult {
    private final String recognizedText;
    private final long processingTimeMillis;
    private String sourceImage;
    private String language;
    private Statistics statistics;

    public OCRResult(String recognizedText, long processingTimeMillis, String sourceImage, String language) {
        this.recognizedText = recognizedText == null ? "" : recognizedText;
        this.processingTimeMillis = processingTimeMillis;
        this.sourceImage = sourceImage;
        this.language = language;
    }
    public String getRecognizedText() { return recognizedText; }
    public int getCharacterCount() { return recognizedText.length(); }
    public int getWordCount() { return statistics == null ? 0 : statistics.words(); }
    public int getLineCount() { return statistics == null ? 0 : statistics.lines(); }
    public double getProcessingTime() { return processingTimeMillis / 1000.0; }
    public long getProcessingTimeMillis() { return processingTimeMillis; }
    public String getSourceImage() { return sourceImage; }
    public String getLanguage() { return language; }
    public Statistics getStatistics() { return statistics; }
    public void setSourceImage(String sourceImage) { this.sourceImage = sourceImage; }
    public void setLanguage(String language) { this.language = language; }
    public void setStatistics(Statistics statistics) { this.statistics = statistics; }
}
