package cute.concolic.symbolicstate;

import cute.concolic.input.InputMap;
import cute.concolic.Globals;

import java.util.Stack;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 22, 2006
 * Time: 2:25:18 PM
 */
public class PrimitiveValueExpression extends FunctionExpression{
    private PointerExpression sym;
    private int type;

    public PrimitiveValueExpression(PointerExpression sym,int type) {
        this.sym = sym;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private static Expression check(Stack syms, Stack vals,int type){
        Expression e1 = (Expression)syms.elementAt(0);
        if(e1!=null && vals.elementAt(0)!=null){
            if(e1 instanceof PrimitiveObjectExpression){
                return ((PrimitiveObjectExpression)e1).getSym();
            } else if(e1 instanceof PointerExpression){
                return new PrimitiveValueExpression((PointerExpression)e1,type);
            }
        }
        return null;

    }

    public static Expression getExpression(String fname, Stack syms,Stack vals){
        Expression tmp;

        int type = -100;
        if(fname.startsWith("<java.lang.Integer")) type = Globals.INT;
        else if(fname.startsWith("<java.lang.Long")) type = Globals.LONG;
        else if(fname.startsWith("<java.lang.Short")) type = Globals.SHORT;
        else if(fname.startsWith("<java.lang.Byte")) type = Globals.BYTE;
        else if(fname.startsWith("<java.lang.Float")) type = Globals.FLOAT;
        else if(fname.startsWith("<java.lang.Double")) type = Globals.DOUBLE;

        if(type!=-100){
            if(fname.endsWith(": int intValue()>")
                    || fname.endsWith(": short shortValue()>")
                    || fname.endsWith(": long longValue()>")
                    || fname.endsWith(": byte byteValue()>")
                    || fname.endsWith(": float floatValue()>")
                    || fname.endsWith(": double doubleValue()>")
                    ){
                tmp = check(syms,vals,type);
                if(tmp!=null) return tmp;
            }
        }

        if(fname.equals("<java.lang.Boolean: boolean booleanValue()>")){
            tmp = check(syms,vals,Globals.BOOLEAN);
            if(tmp!=null) return tmp;
        }
        if(fname.equals("<java.lang.Character: char charValue()>")){
            tmp = check(syms,vals,Globals.CHAR);
            if(tmp!=null) return tmp;
        }

        if(fname.equals("ArrayLength")){
            tmp = check(syms,vals,Globals.INT);
            if(tmp!=null) return tmp;
        }

        return null;
    }

    public void solve(double v, InputMap input) {
        switch(type){
            case Globals.INT:
                setValue(sym,new Integer((int)v),input);
                break;
            case Globals.LONG:
                setValue(sym,new Long((long)v),input);
                break;
            case Globals.SHORT:
                setValue(sym,new Short((short)v),input);
                break;
            case Globals.BYTE:
                setValue(sym,new Byte((byte)v),input);
                break;
            case Globals.FLOAT:
                setValue(sym,new Float((float)v),input);
                break;
            case Globals.DOUBLE:
                setValue(sym,new Double((double)v),input);
                break;
            case Globals.BOOLEAN:
                setValue(sym,new Boolean(v>0.5),input);
                break;
            case Globals.CHAR:
                setValue(sym,new Character((char)v),input);
                break;
        }
    }

    public void printExpression(PrintWriter out) {
        out.println("Primitive.primitiveValue()");
        sym.printExpression(out);
    }
}
