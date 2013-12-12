package cute.concolic.symbolicstate;

import cute.concolic.Globals;
import cute.concolic.input.InputElement;
import cute.concolic.input.InputMap;
import cute.concolic.symbolicexecution.ComputationStack;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 21, 2006
 * Time: 8:24:16 PM
 */
public abstract class FunctionExpression extends Expression {

    public static void insertAuxVariableandReturnExpression(Expression e, int type, ComputationStack cstack, InputMap input){
        InputElement ie = new InputElement(e,null, type,-1);
        cstack.setReturnExpression(new ArithmeticExpression(input.putAndGetArithExpression(ie)));

    }

    public static void handleFunction(String fname, Stack syms, Stack vals, ComputationStack cstack, InputMap input){
        Expression e=null;

        e = StringEqualsExpression.getExpression(fname,syms,vals);
        if(e != null) {
            insertAuxVariableandReturnExpression(e,Globals.BOOLEAN,cstack,input);
            return;
        }

        e = StringLengthExpression.getExpression(fname,syms,vals);
        if(e != null) {
            insertAuxVariableandReturnExpression(e,Globals.INT,cstack,input);
            return;
        }

        e = PrimitiveObjectExpression.getExpression(fname,syms,vals);
        if(e!=null) {
            Globals.globals.state.setStateConcrete(new Long(System.identityHashCode(vals.elementAt(0))),e);
            return;
        }

        e = PrimitiveValueExpression.getExpression(fname,syms,vals);
        if(e!=null) {
            if(e instanceof PrimitiveValueExpression){
                insertAuxVariableandReturnExpression(e,((PrimitiveValueExpression)e).getType(),cstack,input);
            } else {
//                PrintWriter out = new PrintWriter(System.out);
//                e.printExpression(out);
//                out.flush();
                cstack.setReturnExpression(e);
            }
            return;
        }

    }

    public void setValue(PointerExpression sym,Object value,InputMap input){
        PointerExpression p = (PointerExpression)sym;
        InputElement ie = input.getPointerElement(p.getP());
        if(ie.val != null){
            ((InputElement)ie.val).val = value;
        }
    }

    abstract public void solve(double v,InputMap input);
}
