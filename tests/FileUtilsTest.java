import util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtilsTest {
    public static void main(String[] args) throws Exception {
        Path p=Files.createTempFile("thesisscan-utf8-", ".txt");
        try { String text="Հայերեն տեքստ\nРусский текст\nEnglish text"; FileUtils.writeUtf8(p,text); if(!text.equals(FileUtils.readUtf8(p))) throw new AssertionError("UTF-8 roundtrip failed"); System.out.println("FileUtilsTest: PASS"); }
        finally { Files.deleteIfExists(p); }
    }
}
