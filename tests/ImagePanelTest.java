import model.ImageDocument;
import ui.ImagePanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImagePanelTest {
    public static void main(String[] args) {
        ImagePanel panel = new ImagePanel();
        BufferedImage image = new BufferedImage(100, 60, BufferedImage.TYPE_INT_RGB);
        ImageDocument document = new ImageDocument(null, image, "png", "test", ImageDocument.Source.CLIPBOARD);
        panel.setDocument(document);
        panel.setSize(200, 160);
        panel.getPreferredSize();
        Point start = new Point(60, 60);
        Point end = new Point(100, 90);
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 1, 0, start.x, start.y, 1, false));
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_DRAGGED, 2, 0, end.x, end.y, 0, false));
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_RELEASED, 3, 0, end.x, end.y, 1, false));
        Rectangle selection = document.selection();
        if (!new Rectangle(10, 10, 40, 30).equals(selection)) {
            throw new AssertionError("Expected original-image coordinates, got " + selection);
        }
        System.out.println("ImagePanelTest: PASS");
    }
}
