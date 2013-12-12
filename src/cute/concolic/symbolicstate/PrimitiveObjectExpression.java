package cute.concolic.symbolicstate;

import cute.concolic.input.InputMap;

import java.util.Stack;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 22, 2006
 * Time: 12:34:10 PM
 */
public class PrimitiveObjectExpression extends FunctionExpression{
    private ArithmeticExpression sym;

    public ArithmeticExpression getSym() {
        return sym;
    }

    private static PrimitiveObjectExpression check(String fname, String str, Stack syms){
        if(fname.equals(str)){
            ArithmeticExpression e1 = (ArithmeticExpression)syms.elementAt(1);
            if(e1!=null){
                return new PrimitiveObjectExpression(e1);
            }
        }
        return null;
    }

    public static PrimitiveObjectExpression getExpression(String fname, Stack syms,Stack vals){
        PrimitiveObjectExpression tmp;

        tmp = check(fname,"NewArray",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Integer: void <init>(int)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Long: void <init>(long)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Short: void <init>(short)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Character: void <init>(char)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Byte: void <init>(byte)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Boolean: void <init>(boolean)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Double: void <init>(double)>",syms);
        if(tmp!=null) return tmp;

        tmp = check(fname,"<java.lang.Float: void <init>(float)>",syms);
        if(tmp!=null) return tmp;

        return null;
    }

    public PrimitiveObjectExpression(ArithmeticExpression expression) {
        sym = expression;
    }

    public void solve(double v, InputMap input) {
    }

    public void printExpression(PrintWriter out) {
        out.print("Primitive() ");
        sym.printExpression(out);
    }
}
