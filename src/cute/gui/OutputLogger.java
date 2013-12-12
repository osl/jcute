package cute.gui;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 5:44:49 PM
 */
public interface OutputLogger {
    public void appendText(String str,String style);
    void clear();
}
