package cute.concolic.symbolicstate;

import cute.concolic.input.InputMap;

import java.util.Stack;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 22, 2006
 * Time: 9:21:30 AM
 */
public class StringEqualsExpression extends FunctionExpression {
    private PointerExpression sym;
    private String val;

    public static StringEqualsExpression getExpression(String fname, Stack syms,Stack vals){
        if(fname.equals("<java.lang.String: boolean equals(java.lang.Object)>")){
            Expression e1 = (Expression)syms.elementAt(0);
            Expression e2 = (Expression)syms.elementAt(1);
            if(e1!=null && vals.elementAt(0)!=null && e1 instanceof PointerExpression){
                return new StringEqualsExpression((PointerExpression)e1,vals.elementAt(1).toString());
            } else if(vals.elementAt(1)!=null && e2 instanceof PointerExpression){
                return new StringEqualsExpression((PointerExpression)e2,(String)vals.elementAt(0));
            }
        }
        return null;
    }

    public StringEqualsExpression(PointerExpression expression, String s) {
        sym = expression;
        val = s;
    }

    public void solve(double v, InputMap input) {
        boolean result = (v>0.5);
        if(!result){
            if(val.equals(""))
                val = "x";
            else
                val = "";
        }
        setValue(sym,val,input);
    }

    public void printExpression(PrintWriter out) {
        out.print("String.equals()"+val+",");
        sym.printExpression(out);
    }
}
