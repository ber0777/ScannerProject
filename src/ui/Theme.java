package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

public final class Theme {
    public static final Color PRIMARY = new Color(0x0a6e4b);
    public static final Color PRIMARY_HOVER = new Color(0x085d40);
    public static final Color PRIMARY_LIGHT = new Color(0xe8f3ee);
    public static final Color DARK = new Color(0x3d3d3d);
    public static final Color SECONDARY = new Color(0x6b7280);
    public static final Color BORDER = new Color(0xe2e7e4);
    public static final Color SURFACE = Color.WHITE;
    public static final Color BACKGROUND = new Color(0xf7f9f8);
    public static final Color INPUT = new Color(0xf9fbfa);

    private Theme() { }

    public static Font font(float size, int style) {
        String family = Font.SANS_SERIF;
        for (String available : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (available.equalsIgnoreCase("Segoe UI")) { family = available; break; }
        }
        return new Font(family, style, Math.round(size));
    }
}
