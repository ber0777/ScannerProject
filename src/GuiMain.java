import service.ImagePreprocessor;
import service.TesseractOCRService;
import service.TextStatisticsService;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.Theme;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            UIManager.put("Panel.background", Theme.SURFACE);
            UIManager.put("Label.foreground", Theme.DARK);
            UIManager.put("ComboBox.background", Theme.INPUT);
            UIManager.put("ComboBox.foreground", Theme.DARK);
            UIManager.put("TextField.background", Theme.INPUT);
            UIManager.put("TextField.foreground", Theme.DARK);
            UIManager.put("ScrollBar.thumb", Theme.BORDER);
            UIManager.put("ProgressBar.foreground", Theme.PRIMARY);
            new MainFrame(new TesseractOCRService(), new ImagePreprocessor(), new TextStatisticsService()).setVisible(true);
        });
    }
}
