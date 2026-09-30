package ui;

import model.ImageDocument;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ImagePanel extends JPanel {
    private ImageDocument document;
    private Rectangle displaySelection;
    private Point dragStart;
    private double scale = 1;
    private Consumer<Rectangle> selectionListener = ignored -> { };
    private JScrollPane scrollPane;

    public ImagePanel() {
        setBackground(Theme.INPUT);
        setOpaque(true);
        setPreferredSize(new Dimension(460, 420));
        MouseAdapter mouse = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (document == null || !imageBounds().contains(e.getPoint())) return;
                dragStart = e.getPoint(); updateSelection(e.getPoint());
            }
            @Override public void mouseDragged(MouseEvent e) { if (dragStart != null) updateSelection(e.getPoint()); }
            @Override public void mouseReleased(MouseEvent e) { if (dragStart != null) { updateSelection(e.getPoint()); dragStart = null; } }
        };
        addMouseListener(mouse); addMouseMotionListener(mouse);
    }

    public void setScrollPane(JScrollPane pane) {
        scrollPane = pane;
        pane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent event) { revalidate(); repaint(); }
        });
    }
    public void setSelectionListener(Consumer<Rectangle> listener) { selectionListener = listener; }
    public ImageDocument getDocument() { return document; }
    public void setDocument(ImageDocument value) { document=value; displaySelection=null; dragStart=null; revalidate(); repaint(); }
    public void clearSelection() { displaySelection=null; if(document!=null)document.setSelection(null); selectionListener.accept(null); repaint(); }

    private void updateSelection(Point end) {
        Rectangle bounds=imageBounds();
        int x1=clamp(Math.min(dragStart.x,end.x),bounds.x,bounds.x+bounds.width), y1=clamp(Math.min(dragStart.y,end.y),bounds.y,bounds.y+bounds.height);
        int x2=clamp(Math.max(dragStart.x,end.x),bounds.x,bounds.x+bounds.width), y2=clamp(Math.max(dragStart.y,end.y),bounds.y,bounds.y+bounds.height);
        displaySelection=new Rectangle(x1,y1,Math.max(1,x2-x1),Math.max(1,y2-y1));
        int sx=clamp((int)Math.floor((x1-bounds.x)/scale),0,document.width()-1), sy=clamp((int)Math.floor((y1-bounds.y)/scale),0,document.height()-1);
        int ex=clamp((int)Math.ceil((x2-bounds.x)/scale),sx+1,document.width()), ey=clamp((int)Math.ceil((y2-bounds.y)/scale),sy+1,document.height());
        Rectangle original=new Rectangle(sx,sy,ex-sx,ey-sy); document.setSelection(original); selectionListener.accept(original); repaint();
    }
    private Rectangle imageBounds(){if(document==null)return new Rectangle();int x=Math.max(0,(getWidth()-(int)(document.width()*scale))/2),y=Math.max(0,(getHeight()-(int)(document.height()*scale))/2);return new Rectangle(x,y,Math.max(1,(int)(document.width()*scale)),Math.max(1,(int)(document.height()*scale)));}

    @Override public Dimension getPreferredSize(){if(document==null)return new Dimension(460,420);Dimension view=scrollPane==null?new Dimension(460,420):scrollPane.getViewport().getExtentSize();int vw=Math.max(1,view.width-24),vh=Math.max(1,view.height-24);scale=Math.min(1d,Math.min((double)vw/document.width(),(double)vh/document.height()));return new Dimension(Math.max(vw,(int)(document.width()*scale)),Math.max(vh,(int)(document.height()*scale)));}

    @Override protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);Graphics2D g=(Graphics2D)graphics.create();
        if(document==null){
            g.setColor(Theme.SECONDARY);String[] lines={"▧","Перетащите изображение сюда","или нажмите Ctrl+V"};int lineHeight=31,start=getHeight()/2-(lines.length*lineHeight)/2;
            for(int i=0;i<lines.length;i++){g.setFont(Theme.font(i==0?34:14,java.awt.Font.PLAIN));String s=lines[i];g.drawString(s,(getWidth()-g.getFontMetrics().stringWidth(s))/2,start+i*lineHeight);}
        }else{
            Rectangle b=imageBounds();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.drawImage(document.image(),b.x,b.y,b.width,b.height,null);
            if(displaySelection!=null){g.setColor(new java.awt.Color(10,110,75,42));g.fill(displaySelection);g.setColor(Theme.PRIMARY);g.setStroke(new BasicStroke(2));g.draw(displaySelection);}
        }
        g.dispose();
    }
    private static int clamp(int value,int min,int max){return Math.max(min,Math.min(value,max));}
}
