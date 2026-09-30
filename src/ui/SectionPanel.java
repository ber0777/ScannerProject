package ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class SectionPanel extends JPanel {
    public SectionPanel(String title, java.awt.Component content) {
        super(new BorderLayout(0, 8));
        setBackground(Theme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 13, 13, 13)));
        JLabel heading = new JLabel(title);
        heading.setFont(Theme.font(15, java.awt.Font.BOLD));
        heading.setForeground(Theme.DARK);
        add(heading, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
