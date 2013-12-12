package cute.instrument;

import soot.SootMethod;
import soot.SootMethodRef;
import soot.Scene;
import soot.util.Chain;
import soot.jimple.Stmt;
import soot.jimple.Jimple;
import soot.jimple.IntConstant;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:17:06 PM
 */
public class InstrumentReturnVoidStatement {
    public static void instrument(SootMethod sm,Stmt s,Chain units,
                                                     boolean isConcurrent,int lineNo,String mainClass) {
        if(isConcurrent && sm.getSubSignature().equals("void run()")
                && Utils.isRunnableSubType(sm.getDeclaringClass())){
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void endBefore(int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
        }
        if(sm.getSubSignature().equals("void main(java.lang.String[])")
                && sm.getDeclaringClass().getName().equals(mainClass)){
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void endBefore(int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);

        }
    }

}
