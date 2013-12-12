package cute.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 27, 2005
 * Time: 9:31:17 AM
 */
public class ColorAndTooltipListRenderer extends JLabel implements ListCellRenderer{
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        ListElement e = (ListElement)value;
        setText(e.getName());
        Color c = e.getColor(list.getBackground());
        if (isSelected) {
            setBackground(c);
            setForeground(new Color(0.6f,0.0f,0.0f));
        }
        else {
            setBackground(c);
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
