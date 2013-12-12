package cute.gui;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 10:37:32 PM
 */
public class DefaultOutputLogger implements OutputLogger {
    public void appendText(String str,String style) {
        System.out.println(str);
    }

    public void clear() {
    }
}
