package cute.instrument;

import soot.*;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.IntConstant;
import soot.jimple.internal.JCaughtExceptionRef;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:12:03 PM
 */
public class WrapWholeBodyInTryCatch {
    public static void instrument(Body body,SootMethod sm,boolean isConcurrent,String mainClass) {
        Local tmpLocal = Jimple.v().newLocal("__ct_"+body.getLocalCount(),RefType.v("java.lang.Throwable"));
        body.getLocals().add(tmpLocal);
        Stmt eStmt = Jimple.v().newIdentityStmt(tmpLocal,new JCaughtExceptionRef());
        Trap t = Jimple.v().newTrap(Scene.v().getSootClass("java.lang.Throwable"),
                (Stmt)body.getUnits().getFirst(),(Stmt)body.getUnits().getLast(),eStmt);
        body.getTraps().addLast(t);
        body.getUnits().addLast(eStmt);
        if((isConcurrent && sm.getSubSignature().equals("void run()") && Utils.isRunnableSubType(sm.getDeclaringClass()))
        || (sm.getSubSignature().equals("void main(java.lang.String[])")
                && sm.getDeclaringClass().getName().equals(mainClass))){
            SootMethodRef mr;
            mr = Scene.v().getMethod("<cute.concolic.Call: void threadException(java.lang.Throwable)>").makeRef();
            body.getUnits().addLast(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,tmpLocal)));
        } else {
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void funEnd(int)>").makeRef();
            body.getUnits().addLast(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(-1))));
            mr = Scene.v().getMethod("<cute.concolic.Call: void popAll(int)>").makeRef();
            body.getUnits().addLast(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(-1))));
        }
        body.getUnits().addLast(Jimple.v().newThrowStmt(tmpLocal));

    }

}
