package cute.gui;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 10:35:43 PM
 */
public class DefaultMessageLogger implements MessageLogger{
    public void ask(String s) {
        System.err.println(s);
    }
}
