import service.TesseractOCRService;

import java.nio.file.Files;
import java.nio.file.Path;

public class OCRServiceTest {
    public static void main(String[] args) {
        Path sample=Path.of("test-data/ocr-sample.png");
        if(!Files.isRegularFile(sample)) { System.out.println("OCR integration test skipped:\nAdd test-data/ocr-sample.png and configure Tesseract to run this test."); return; }
        try { var result=new TesseractOCRService().recognize(sample.toString(),"eng"); System.out.println("OCRServiceTest: PASS ("+result.getCharacterCount()+" characters)"); }
        catch(Exception e) { if(e.getMessage()!=null && e.getMessage().contains("Tesseract OCR не найден")) System.out.println("OCR integration test skipped:\nTesseract is not configured."); else throw new RuntimeException(e); }
    }
}
