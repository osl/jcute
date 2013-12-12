package cute.gui;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 27, 2005
 * Time: 10:16:38 AM
 */
public class TooltipJList extends JList {

    public String getToolTipText( MouseEvent e ){
        int row = locationToIndex( e.getPoint() );
        if (row < 0)
            return null;
        else {
            ListElement le = (ListElement)getModel().getElementAt(row);
            return le.getToolTip();

        }
    }

}
