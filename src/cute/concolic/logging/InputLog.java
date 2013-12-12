package cute.concolic.logging;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jan 2, 2006
 * Time: 10:42:59 AM
 */
public class InputLog {
    private String str;

    public InputLog(String str) {
        this.str = str;
    }

    public void print(PrintWriter inputOut) {
        inputOut.print(str);
    }
}
