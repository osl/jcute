package cute.gui;

import javax.swing.*;
import java.net.URL;
import java.awt.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 8, 2005
 * Time: 10:38:48 AM
 */
public class GetImageIcon {
    public static ImageIcon getIcon(String filename){
        URL ul = getUrl(filename);

        ImageIcon icon = new ImageIcon(ul);
        return icon;
    }

    public static Image getImage(String filename){
        URL ul = getUrl(filename);

        Image icon= (new ImageIcon(ul)).getImage();
        return icon;
    }

    public static URL getUrl(String filename){
        Object dummy = new Object(){
        public String toString() { return super.toString(); }
        };
        ClassLoader cl = dummy.getClass().getClassLoader();

        return cl.getResource(filename);

    }
}
