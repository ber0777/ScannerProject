package util;

import model.ImageDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

public final class ImageUtils {
    private static final Set<String> FORMATS = Set.of("jpg", "jpeg", "png", "bmp");
    private ImageUtils() { }
    public static ImageDocument load(String path) throws IOException {
        Path p=Path.of(path).toAbsolutePath().normalize();
        if(!Files.exists(p)) throw new IOException("Файл не существует: " + p);
        if(!Files.isRegularFile(p) || !Files.isReadable(p)) throw new IOException("Файл недоступен для чтения: " + p);
        String name=p.getFileName().toString(); int dot=name.lastIndexOf('.');
        String ext=dot<0?"":name.substring(dot+1).toLowerCase(Locale.ROOT);
        if(!FORMATS.contains(ext)) throw new IOException("Неподдерживаемый формат. Используйте JPG, JPEG, PNG или BMP.");
        BufferedImage image=ImageIO.read(p.toFile());
        if(image==null) throw new IOException("Файл не является поддерживаемым изображением или повреждён.");
        if(image.getWidth()<=0 || image.getHeight()<=0) throw new IOException("Некорректный размер изображения.");
        return new ImageDocument(p,image,ext);
    }
    public static BufferedImage loadValidated(String path) throws IOException { return load(path).image(); }
    public static BufferedImage crop(BufferedImage image,int x,int y,int width,int height) {
        if(x<0||y<0||width<=0||height<=0) throw new IllegalArgumentException("Координаты должны удовлетворять X >= 0, Y >= 0, ширина > 0, высота > 0.");
        if((long)x+width>image.getWidth() || (long)y+height>image.getHeight()) throw new IllegalArgumentException("Выделенная область выходит за границы изображения ("+image.getWidth()+"x"+image.getHeight()+").");
        return image.getSubimage(x,y,width,height);
    }
    public static Path writeTemp(BufferedImage image) throws IOException {
        Path dir=Path.of("temp"); Files.createDirectories(dir); Path out=Files.createTempFile(dir,"thesisscan-", ".png");
        if(!ImageIO.write(image,"png",out.toFile())) throw new IOException("Не удалось записать временное изображение."); return out;
    }
}
