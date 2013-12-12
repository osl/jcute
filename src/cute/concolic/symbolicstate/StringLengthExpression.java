package cute.concolic.symbolicstate;

import cute.concolic.input.InputMap;

import java.util.Stack;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 22, 2006
 * Time: 11:46:01 AM
 */
public class StringLengthExpression extends FunctionExpression{
    private PointerExpression sym;

    public static StringLengthExpression getExpression(String fname, Stack syms,Stack vals){
        if(fname.equals("<java.lang.String: int length()>")){
            Expression e1 = (Expression)syms.elementAt(0);
            if(e1!=null && vals.elementAt(0)!=null && e1 instanceof PointerExpression){
                return new StringLengthExpression((PointerExpression)e1);
            }
        }
        return null;
    }

    public StringLengthExpression(PointerExpression expression) {
        sym = expression;
    }

    public void solve(double v, InputMap input) {
        int len = (int)v;
        if(len<0) len = 0;
        System.out.println("len = " + len);
        StringBuffer sb = new StringBuffer(len);
        for(int i=0;i<len;i++){
            sb.append('x');
        }
        setValue(sym,sb.toString(),input);
    }

    public void printExpression(PrintWriter out) {
        out.print("String.length() ");
        sym.printExpression(out);
    }
}
