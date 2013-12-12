package cute.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 5:47:24 PM
 */
public class JTextPaneOutputLogger extends JTextPane implements OutputLogger {
    private Document document;
    private Style regularStyle;
    private Style errorStyle;
    private Style commandStyle;
    private static int MAX_LENGTH = 100000;

    public JTextPaneOutputLogger() {
        this.document = this.getDocument();
        this.setEditable(false);
        javax.swing.text.Style def = StyleContext.getDefaultStyleContext()
                .getStyle(javax.swing.text.StyleContext.DEFAULT_STYLE);

        regularStyle = this.addStyle("regular", def);
        StyleConstants.setFontFamily(regularStyle, "SansSerif");
        StyleConstants.setFontSize(regularStyle, 12);
        errorStyle = this.addStyle("error", regularStyle);
        StyleConstants.setForeground(errorStyle, Color.red);
        commandStyle = this.addStyle("timeStamp", regularStyle);
        StyleConstants.setForeground(commandStyle,Color.blue);
    }

    public void appendText(String str,String sstyle) {
        synchronized(this){
            AttributeSet style = null;
            if(sstyle.equals("error")){
                style = errorStyle;
            } else if(sstyle.equals("command")){
                style = commandStyle;
            } else {
                style = regularStyle;
            }
            try {
                document.insertString(document.getLength(), str, style);
                document.insertString(document.getLength(), "\n", style);
                int tooMuch = document.getLength() - MAX_LENGTH;
                if (tooMuch > 0) {
                    document.remove(0, tooMuch);
                }
                setCaretPosition(document.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println(str);
            System.out.flush();
        }
    }

    public void clear() {
        setText("");
    }
}
