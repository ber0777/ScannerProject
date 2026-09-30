package ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

public class SearchPanel extends JPanel {
    private final JTextField query = new JTextField(14);
    private final ModernButton previous = new ModernButton("Предыдущее", false);
    private final ModernButton next = new ModernButton("Следующее", false);
    private final JTextArea text;
    private int cursor;

    public SearchPanel(JTextArea text) {
        super(new BorderLayout(7, 0)); this.text=text;
        setOpaque(false);
        JPanel row=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));row.setOpaque(false);
        javax.swing.JLabel label=new javax.swing.JLabel("Поиск");label.setFont(Theme.font(12,Font.BOLD));label.setForeground(Theme.SECONDARY);
        query.setFont(Theme.font(13,Font.PLAIN));query.setForeground(Theme.DARK);query.setCaretColor(Theme.PRIMARY);query.setBackground(Theme.INPUT);query.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER,1,true),BorderFactory.createEmptyBorder(7,9,7,9)));
        row.add(label);row.add(query);row.add(previous);row.add(next);add(row,BorderLayout.WEST);
        query.addActionListener(e->find(true));previous.addActionListener(e->find(false));next.addActionListener(e->find(true));
    }
    private void find(boolean forward){String needle=query.getText();if(needle.isEmpty())return;String content=text.getText();if(content.isEmpty())return;String lower=content.toLowerCase(java.util.Locale.ROOT),target=needle.toLowerCase(java.util.Locale.ROOT);int found;if(forward){found=lower.indexOf(target,Math.min(cursor,content.length()));if(found<0)found=lower.indexOf(target);}else{int from=Math.max(0,cursor-needle.length()-1);found=lower.lastIndexOf(target,from);if(found<0)found=lower.lastIndexOf(target);}text.getHighlighter().removeAllHighlights();if(found>=0)try{text.getHighlighter().addHighlight(found,found+needle.length(),new DefaultHighlighter.DefaultHighlightPainter(new Color(10,110,75,70)));text.setCaretPosition(found);cursor=forward?found+needle.length():found;}catch(BadLocationException ignored){}}
}
