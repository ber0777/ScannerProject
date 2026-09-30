package service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImagePreprocessor {
    public BufferedImage process(BufferedImage source, int mode) {
        if (source == null) throw new IllegalArgumentException("Изображение не загружено.");
        return switch (mode) {
            case 2 -> grayscale(source);
            case 3 -> contrast(grayscale(source));
            case 4 -> binary(grayscale(source));
            case 5 -> scale(source, 2.0);
            default -> copy(source);
        };
    }
    public BufferedImage grayscale(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = out.createGraphics(); g.drawImage(src, 0, 0, null); g.dispose(); return out;
    }
    public BufferedImage contrast(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y=0;y<src.getHeight();y++) for(int x=0;x<src.getWidth();x++) { int v=new Color(src.getRGB(x,y)).getRed(); int n=Math.max(0,Math.min(255,(int)((v-128)*1.5+128))); out.setRGB(x,y,new Color(n,n,n).getRGB()); }
        return out;
    }
    public BufferedImage binary(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int y=0;y<src.getHeight();y++) for(int x=0;x<src.getWidth();x++) { int v=new Color(src.getRGB(x,y)).getRed(); out.setRGB(x,y,v<160?Color.BLACK.getRGB():Color.WHITE.getRGB()); }
        return out;
    }
    public BufferedImage scale(BufferedImage src, double factor) {
        if (factor <= 0) throw new IllegalArgumentException("Коэффициент увеличения должен быть положительным.");
        BufferedImage out=new BufferedImage(Math.max(1,(int)(src.getWidth()*factor)),Math.max(1,(int)(src.getHeight()*factor)),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=out.createGraphics(); g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC); g.drawImage(src,0,0,out.getWidth(),out.getHeight(),null); g.dispose(); return out;
    }
    private BufferedImage copy(BufferedImage src) { BufferedImage out=new BufferedImage(src.getWidth(),src.getHeight(),BufferedImage.TYPE_INT_RGB); Graphics2D g=out.createGraphics();g.drawImage(src,0,0,null);g.dispose();return out; }
}
