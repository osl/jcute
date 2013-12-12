package cute.instrument;

import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 24, 2006
 * Time: 11:36:08 AM
 */
public class AddCallWithDummyValue {
    public static void instrument(Chain units, Stmt s,int lineNo){
        SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void loadValue(int)>").makeRef();
        units.insertBefore(Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(mr, IntConstant.v(lineNo))),s);
    }
}
