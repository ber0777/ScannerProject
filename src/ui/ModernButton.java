package ui;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ModernButton extends JButton {
    private final boolean primary;

    public ModernButton(String text, boolean primary) {
        super(text);
        this.primary = primary;
        setFont(Theme.font(13, java.awt.Font.BOLD));
        setForeground(primary ? Color.WHITE : Theme.DARK);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        setMinimumSize(new java.awt.Dimension(92, 38));
        setPreferredSize(new java.awt.Dimension(Math.max(98, getPreferredSize().width + 22), 38));
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = primary ? Theme.PRIMARY : Color.WHITE;
        if (!isEnabled()) fill = primary ? new Color(0x9ab9ad) : new Color(0xf0f2f1);
        else if (getModel().isPressed()) fill = primary ? Theme.PRIMARY_HOVER : new Color(0xf0f4f1);
        else if (getModel().isRollover()) fill = primary ? Theme.PRIMARY_HOVER : Theme.PRIMARY_LIGHT;
        g.setColor(fill);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        if (!primary) {
            g.setColor(getModel().isRollover() ? Theme.PRIMARY : Theme.BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        }
        if (isFocusOwner() && isEnabled()) {
            g.setColor(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 100));
            g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
        }
        g.dispose();
        super.paintComponent(graphics);
    }
}
