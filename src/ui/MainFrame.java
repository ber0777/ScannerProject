package ui;

import model.ImageDocument;
import model.OCRResult;
import service.ImagePreprocessor;
import service.OCRService;
import service.TextStatisticsService;
import util.FileUtils;
import util.ImageUtils;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MainFrame extends JFrame {
    private final OCRService ocr;
    private final ImagePreprocessor preprocessor;
    private final TextStatisticsService statistics;
    private final ImagePanel imagePanel = new ImagePanel();
    private final TextPanel textPanel = new TextPanel();
    private final ToolbarPanel toolbar = new ToolbarPanel();
    private final SearchPanel searchPanel = new SearchPanel(textPanel.textArea());
    private final StatusPanel status = new StatusPanel();
    private ImageDocument document;
    private OCRResult latest;

    public MainFrame(OCRService ocr, ImagePreprocessor preprocessor, TextStatisticsService statistics) {
        super("ThesisScan");
        this.ocr=ocr; this.preprocessor=preprocessor; this.statistics=statistics;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980,650)); setSize(1280,800); setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND); setLayout(new BorderLayout());
        buildUi(); bindActions(); bindShortcuts(); installDrop();
    }

    private void buildUi() {
        JPanel top = new JPanel(new BorderLayout()); top.setBackground(Theme.SURFACE);
        JPanel brandRow = new JPanel(new BorderLayout()); brandRow.setBackground(Theme.SURFACE);
        brandRow.setBorder(BorderFactory.createEmptyBorder(12,19,9,19));
        javax.swing.JLabel brand = new javax.swing.JLabel("ThesisScan"); brand.setFont(Theme.font(22,java.awt.Font.BOLD)); brand.setForeground(Theme.DARK);
        JPanel ready = new JPanel(new FlowLayout(FlowLayout.RIGHT,7,0)); ready.setOpaque(false);
        javax.swing.JLabel readyDot=new javax.swing.JLabel("●");readyDot.setForeground(Theme.PRIMARY);
        javax.swing.JLabel readyText=new javax.swing.JLabel("Готово");readyText.setFont(Theme.font(12,java.awt.Font.BOLD));readyText.setForeground(Theme.DARK);
        ready.add(readyDot);ready.add(readyText);brandRow.add(brand,BorderLayout.WEST);brandRow.add(ready,BorderLayout.EAST);
        top.add(brandRow,BorderLayout.NORTH);top.add(toolbar,BorderLayout.SOUTH);add(top,BorderLayout.NORTH);

        JScrollPane imageScroll=new JScrollPane(imagePanel);imageScroll.getViewport().setBackground(Theme.INPUT);imageScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER,1,true));imageScroll.getVerticalScrollBar().setUnitIncrement(16);imagePanel.setScrollPane(imageScroll);
        JPanel textContent=new JPanel(new BorderLayout(0,8));textContent.setBackground(Theme.SURFACE);textContent.add(searchPanel,BorderLayout.NORTH);textContent.add(textPanel,BorderLayout.CENTER);
        SectionPanel imageSection=new SectionPanel("Изображение",imageScroll);
        SectionPanel textSection=new SectionPanel("Распознанный текст",textContent);
        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,imageSection,textSection);split.setResizeWeight(.5);split.setContinuousLayout(true);split.setBorder(BorderFactory.createEmptyBorder(13,17,13,17));split.setDividerSize(8);split.setBackground(Theme.BACKGROUND);
        add(split,BorderLayout.CENTER);add(status,BorderLayout.SOUTH);

        imagePanel.setSelectionListener(rect->{if(rect!=null)status.setStatus("Выделена область: "+rect.width+" × "+rect.height);else if(document!=null)status.setStatus("Изображение загружено");});
        textPanel.textArea().getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){public void insertUpdate(javax.swing.event.DocumentEvent e){updateTextStats();}public void removeUpdate(javax.swing.event.DocumentEvent e){updateTextStats();}public void changedUpdate(javax.swing.event.DocumentEvent e){updateTextStats();}});
    }

    private void bindActions() {
        toolbar.open.addActionListener(e->chooseImage()); toolbar.paste.addActionListener(e->pasteImage());
        toolbar.recognize.addActionListener(e->recognize());
        toolbar.recognizeSelection.addActionListener(e->{toolbar.mode.setSelectedIndex(1);recognize();});
        toolbar.save.addActionListener(e->saveText()); toolbar.clear.addActionListener(e->clearDocument());
        toolbar.statistics.addActionListener(e->showStatistics());
    }

    private void bindShortcuts() {
        int mask=Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_V,mask),"pasteImage");
        getRootPane().getActionMap().put("pasteImage",new javax.swing.AbstractAction(){public void actionPerformed(java.awt.event.ActionEvent e){if(textPanel.textArea().isFocusOwner()){textPanel.textArea().paste();return;}pasteImage();}});
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S,mask),"saveText");
        getRootPane().getActionMap().put("saveText",new javax.swing.AbstractAction(){public void actionPerformed(java.awt.event.ActionEvent e){saveText();}});
    }

    private void chooseImage() {
        JFileChooser chooser=new JFileChooser();chooser.setFileFilter(new FileNameExtensionFilter("Изображения (PNG, JPG, JPEG, BMP)","png","jpg","jpeg","bmp"));
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)try{Path path=chooser.getSelectedFile().toPath();var loaded=ImageUtils.load(path.toString());acceptImage(new ImageDocument(loaded.path(),loaded.image(),loaded.format(),path.getFileName().toString(),ImageDocument.Source.FILE),"Изображение загружено");}catch(Exception ex){showError("Не удалось открыть изображение: "+ex.getMessage());}
    }

    private void pasteImage() {
        try {
            Transferable contents=Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if(contents==null||!contents.isDataFlavorSupported(DataFlavor.imageFlavor)){status.setStatus("В буфере нет изображения");showInfo("В буфере обмена нет изображения.");return;}
            Object data=contents.getTransferData(DataFlavor.imageFlavor);if(!(data instanceof Image image)){showInfo("Буфер обмена не содержит изображение.");return;}
            BufferedImage buffered=toBufferedImage(image);acceptImage(new ImageDocument(null,buffered,"png","Clipboard image",ImageDocument.Source.CLIPBOARD),"Изображение вставлено из Clipboard");
        } catch(Exception ex){status.setStatus("Ошибка Clipboard");showError("Не удалось вставить изображение: "+ex.getMessage());}
    }

    private static BufferedImage toBufferedImage(Image image){if(image instanceof BufferedImage b)return b;int w=image.getWidth(null),h=image.getHeight(null);if(w<=0||h<=0)throw new IllegalArgumentException("Не удалось определить размер изображения.");BufferedImage result=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);var g=result.createGraphics();g.drawImage(image,0,0,null);g.dispose();return result;}

    private void installDrop(){imagePanel.setTransferHandler(new TransferHandler(){@Override public boolean canImport(TransferSupport support){return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);}@Override public boolean importData(TransferSupport support){if(!canImport(support))return false;try{@SuppressWarnings("unchecked") List<File> files=(List<File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);if(files.isEmpty())return false;Path path=files.get(0).toPath();var loaded=ImageUtils.load(path.toString());acceptImage(new ImageDocument(loaded.path(),loaded.image(),loaded.format(),path.getFileName().toString(),ImageDocument.Source.DRAG_AND_DROP),"Изображение загружено перетаскиванием");return true;}catch(Exception ex){showError("Не удалось загрузить изображение: "+ex.getMessage());return false;}}});}

    private void acceptImage(ImageDocument image,String message){document=image;imagePanel.setDocument(image);textPanel.textArea().setText("");latest=null;status.resetStats();status.setStatus(message+" ("+image.width()+" × "+image.height()+")");toolbar.recognize.setEnabled(true);toolbar.recognizeSelection.setEnabled(true);}

    private void recognize(){
        if(document==null){showInfo("Сначала добавьте изображение.");return;}
        boolean selected=toolbar.mode.getSelectedIndex()==1;var rect=selected?document.selection():null;
        if(selected&&(rect==null||rect.width<=0||rect.height<=0)){showInfo("Сначала выделите область изображения.");return;}
        String language=languageCode(toolbar.language.getSelectedIndex());BufferedImage source=document.image();String sourceName=document.name();int preprocessingMode=toolbar.preprocessing.getSelectedIndex()+1;
        toolbar.recognize.setEnabled(false);toolbar.recognizeSelection.setEnabled(false);status.setBusy(true);status.setStatus("Распознавание...");
        new SwingWorker<OCRResult,Void>(){
            @Override protected OCRResult doInBackground()throws Exception{BufferedImage input=selected?ImageUtils.crop(source,rect.x,rect.y,rect.width,rect.height):source;BufferedImage processed=preprocessor.process(input,preprocessingMode);Path temp=ImageUtils.writeTemp(processed);try{OCRResult result=ocr.recognize(temp.toString(),language);result.setSourceImage(sourceName);result.setLanguage(language);result.setStatistics(statistics.calculate(result.getRecognizedText()));return result;}finally{Files.deleteIfExists(temp);}}
            @Override protected void done(){status.setBusy(false);toolbar.recognize.setEnabled(true);toolbar.recognizeSelection.setEnabled(true);try{latest=get();textPanel.textArea().setText(latest.getRecognizedText());textPanel.textArea().setCaretPosition(0);status.setStatus("Распознавание завершено");updateTextStats();FileUtils.appendHistory(Path.of("data/history.txt"),latest);}catch(Exception ex){Throwable cause=ex.getCause()==null?ex:ex.getCause();status.setStatus("Ошибка OCR");System.err.println("ThesisScan OCR error:");cause.printStackTrace();showError(userFriendlyOcrError(cause));}}
        }.execute();
    }

    private String userFriendlyOcrError(Throwable error){String message=error.getMessage()==null?"":error.getMessage();String lower=message.toLowerCase(java.util.Locale.ROOT);if(lower.contains("tesseract")||lower.contains("traineddata")||lower.contains("language"))return "Не удалось распознать изображение.\n\nПроверьте, установлен ли Tesseract OCR, настроен ли путь к нему и установлены ли языковые модели выбранного языка.\n\nПодробности выведены в консоль.";return "Не удалось распознать изображение.\n\n"+message;}
    private static String languageCode(int index){return switch(index){case 1->"eng";case 2->"hye";case 3->"rus+eng";case 4->"eng+hye";case 5->"rus+eng+hye";default->"rus";};}
    private void updateTextStats(){var s=statistics.calculate(textPanel.textArea().getText());status.setStats(s.characters(),s.charactersWithoutWhitespace(),s.words(),s.lines(),s.sentences(),latest==null?0:latest.getProcessingTime());}
    private void showStatistics(){var s=statistics.calculate(textPanel.textArea().getText());showInfo("Символов: "+s.characters()+"\nБез пробелов: "+s.charactersWithoutWhitespace()+"\nСлов: "+s.words()+"\nСтрок: "+s.lines()+"\nПредложений: "+s.sentences()+"\nВремя OCR: "+String.format(java.util.Locale.ROOT,"%.2f",latest==null?0:latest.getProcessingTime())+" сек.");}

    private void saveText(){if(textPanel.textArea().getText().isEmpty()){showInfo("Нет текста для сохранения.");return;}JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new File("result.txt"));chooser.setFileFilter(new FileNameExtensionFilter("Текстовый файл (*.txt)","txt"));if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)try{Path path=chooser.getSelectedFile().toPath();if(!path.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".txt"))path=path.resolveSibling(path.getFileName()+".txt");FileUtils.writeUtf8(path,textPanel.textArea().getText());status.setStatus("Файл сохранён: "+path.getFileName());}catch(Exception ex){showError("Не удалось сохранить файл: "+ex.getMessage());}}
    private void clearDocument(){if(document==null&&textPanel.textArea().getText().isEmpty())return;int answer=JOptionPane.showConfirmDialog(this,"Очистить текущий документ?","Очистить",JOptionPane.YES_NO_OPTION);if(answer!=JOptionPane.YES_OPTION)return;document=null;latest=null;imagePanel.setDocument(null);textPanel.textArea().setText("");status.resetStats();status.setStatus("Готово");}
    private void showInfo(String message){JOptionPane.showMessageDialog(this,message,"ThesisScan",JOptionPane.INFORMATION_MESSAGE);}
    private void showError(String message){JOptionPane.showMessageDialog(this,message==null?"Неизвестная ошибка.":message,"Ошибка ThesisScan",JOptionPane.ERROR_MESSAGE);}
}
