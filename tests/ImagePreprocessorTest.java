import service.ImagePreprocessor;
import util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImagePreprocessorTest {
    public static void main(String[] args) throws Exception {
        BufferedImage source=new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB);
        Path path=Files.createTempFile("image-preprocessor-test-", ".png");
        try { ImageIO.write(source,"png",path.toFile()); BufferedImage loaded=ImageUtils.loadValidated(path.toString()); BufferedImage gray=new ImagePreprocessor().grayscale(loaded); if(gray.getWidth()!=8) throw new AssertionError(); Path out=ImageUtils.writeTemp(gray); try { if(ImageIO.read(out.toFile())==null) throw new AssertionError(); } finally { Files.deleteIfExists(out); } System.out.println("ImagePreprocessorTest: PASS"); }
        finally { Files.deleteIfExists(path); }
    }
}
