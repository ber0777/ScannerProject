package ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;

public class TextPanel extends JPanel {
    private final JTextArea textArea = new JTextArea();
    public TextPanel() {
        super(new BorderLayout(0, 8));
        setBackground(Theme.SURFACE);
        textArea.setFont(Theme.font(14, Font.PLAIN));
        textArea.setForeground(Theme.DARK);
        textArea.setBackground(Theme.INPUT);
        textArea.setCaretColor(Theme.PRIMARY);
        textArea.setSelectionColor(Theme.PRIMARY_LIGHT);
        textArea.setLineWrap(true); textArea.setWrapStyleWord(true); textArea.setTabSize(4);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
    public JTextArea textArea() { return textArea; }
}
