package cute.concolic.symbolicstate;

import java.io.PrintWriter;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class PointerExpression extends Expression{
    private int p;


    public PointerExpression(int e) {
        p = e;
    }

    public int getP() {
        return p;
    }

    public void printExpression(PrintWriter out) {
        out.print("p");
        out.println(p);
    }
}
