package cute.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Oct 29, 2005
 * Time: 7:24:07 PM
 */
public class JCuteFileFilter extends FileFilter{
    public boolean accept(File f) {
        if(f.isDirectory()) return true;
        return f.getName().endsWith(".java");
    }

    public String getDescription() {
        return "Java File";
    }
}
