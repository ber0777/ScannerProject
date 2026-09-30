package ui;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class ToolbarPanel extends JPanel {
    public final ModernButton open = new ModernButton("＋  Добавить", false);
    public final ModernButton paste = new ModernButton("Вставить", false);
    public final ModernButton recognize = new ModernButton("Распознать OCR", true);
    public final ModernButton recognizeSelection = new ModernButton("OCR области", false);
    public final ModernButton save = new ModernButton("Сохранить", false);
    public final ModernButton clear = new ModernButton("Очистить", false);
    public final ModernButton statistics = new ModernButton("Статистика", false);
    public final JComboBox<String> language = combo(new String[]{"Русский", "English", "Հայերեն", "Русский + English", "English + Հայերեն", "Русский + English + Հայերեն"});
    public final JComboBox<String> mode = combo(new String[]{"Всё изображение", "Выделенная область"});
    public final JComboBox<String> preprocessing = combo(new String[]{"Без обработки", "Grayscale", "Grayscale + Contrast", "Grayscale + Binary", "Увеличить изображение"});

    public ToolbarPanel() {
        super(new BorderLayout(0, 7));
        setBackground(Theme.SURFACE);
        setBorder(new EmptyBorder(9, 16, 11, 16));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(open); actions.add(paste); actions.add(recognize); actions.add(recognizeSelection); actions.add(save); actions.add(clear); actions.add(statistics);
        JPanel settings = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        settings.setOpaque(false);
        settings.add(label("Язык OCR")); settings.add(language);
        settings.add(label("Режим")); settings.add(mode);
        settings.add(label("Обработка")); settings.add(preprocessing);
        add(actions, BorderLayout.NORTH);
        add(settings, BorderLayout.SOUTH);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER), new EmptyBorder(9, 16, 11, 16)));
    }

    private static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(12, Font.BOLD));
        label.setForeground(Theme.SECONDARY);
        return label;
    }

    private static JComboBox<String> combo(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(Theme.font(13, Font.PLAIN));
        combo.setForeground(Theme.DARK);
        combo.setBackground(Theme.INPUT);
        combo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true), new EmptyBorder(4, 6, 4, 6)));
        combo.setFocusable(true);
        return combo;
    }
}
