package service;

import model.OCRResult;

public interface OCRService {
    OCRResult recognize(String imagePath, String language) throws Exception;
}
