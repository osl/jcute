package cute.gui;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 27, 2005
 * Time: 11:22:40 AM
 */
public interface ListElement {
    public String getName();
    public Color getColor(Color background);
    public String getToolTip();
}
