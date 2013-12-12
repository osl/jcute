package cute.instrument;

import soot.*;
import soot.jimple.Stmt;
import soot.jimple.Jimple;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:52:15 PM
 */
public class AddCallWithValue {
    public static void instrument(Body b, Value v, Chain units, Stmt s, Type t, String type, int lineNo, String call, boolean before){
        SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void "+call+"("+type+",int)>").makeRef();
        Local tmpLocal = Jimple.v().newLocal("__ct_"+b.getLocalCount(),t);
        b.getLocals().add(tmpLocal);
        AssignStmt as = null;
        as = Jimple.v().newAssignStmt(tmpLocal,v);
        if(before){
            units.insertBefore(as,s);
            units.insertBefore(Jimple.v().newInvokeStmt(
                    Jimple.v().newStaticInvokeExpr(mr,tmpLocal,IntConstant.v(lineNo))),s);
        } else {
            units.insertAfter(Jimple.v().newInvokeStmt(
                    Jimple.v().newStaticInvokeExpr(mr,tmpLocal,IntConstant.v(lineNo))),s);
            units.insertAfter(as,s);
        }
    }
}
