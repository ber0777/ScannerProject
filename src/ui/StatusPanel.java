package ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

public class StatusPanel extends JPanel {
    private final JLabel dot=new JLabel("●");
    private final JLabel status=new JLabel("Готово");
    private final JLabel stats=new JLabel("Символов: 0   Слов: 0   Строк: 0   Время: 0.00 сек.");
    private final JProgressBar progress=new JProgressBar();
    public StatusPanel(){super(new BorderLayout(12,0));setBorder(new EmptyBorder(9,17,9,17));setBackground(Theme.SURFACE);setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1,0,0,0,Theme.BORDER),new EmptyBorder(9,17,9,17)));JPanel left=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));left.setOpaque(false);dot.setForeground(Theme.PRIMARY);status.setFont(Theme.font(12,java.awt.Font.PLAIN));status.setForeground(Theme.DARK);left.add(dot);left.add(status);progress.setIndeterminate(true);progress.setVisible(false);progress.setPreferredSize(new java.awt.Dimension(88,13));progress.setForeground(Theme.PRIMARY);left.add(progress);stats.setFont(Theme.font(12,java.awt.Font.PLAIN));stats.setForeground(Theme.SECONDARY);add(left,BorderLayout.WEST);add(stats,BorderLayout.EAST);}
    public void setStatus(String value){status.setText(value);dot.setForeground(value.toLowerCase(java.util.Locale.ROOT).contains("ошибка")?new Color(0x777777):Theme.PRIMARY);}
    public void setBusy(boolean busy){progress.setVisible(busy);if(busy)dot.setForeground(Theme.SECONDARY);}
    public void setStats(int chars,int noSpaces,int words,int lines,int sentences,double seconds){stats.setText("Символов: "+chars+"   Слов: "+words+"   Строк: "+lines+"   Время: "+String.format(java.util.Locale.ROOT,"%.2f",seconds)+" сек.");stats.setToolTipText("Без пробелов: "+noSpaces+"   Предложений: "+sentences);}
    public void resetStats(){setStats(0,0,0,0,0,0);}
}
