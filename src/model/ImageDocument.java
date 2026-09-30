package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Image and its origin in the UI; selection coordinates always refer to the source pixels. */
public class ImageDocument {
    public enum Source { FILE, CLIPBOARD, DRAG_AND_DROP }

    private final Path path;
    private final BufferedImage image;
    private final String format;
    private final String name;
    private final Source source;
    private Rectangle selection;

    public ImageDocument(Path path, BufferedImage image, String format) {
        this(path, image, format, path == null ? "Clipboard image" : path.getFileName().toString(), Source.FILE);
    }

    public ImageDocument(Path path, BufferedImage image, String format, String name, Source source) {
        if (image == null) throw new IllegalArgumentException("Image must not be null.");
        this.path = path;
        this.image = image;
        this.format = format;
        this.name = name == null ? "Image" : name;
        this.source = source == null ? Source.FILE : source;
    }

    public Path path() { return path; }
    public BufferedImage image() { return image; }
    public String format() { return format; }
    public String name() { return name; }
    public Source source() { return source; }
    public int width() { return image.getWidth(); }
    public int height() { return image.getHeight(); }
    public Rectangle selection() { return selection == null ? null : new Rectangle(selection); }
    public void setSelection(Rectangle selection) { this.selection = selection == null ? null : new Rectangle(selection); }
}
