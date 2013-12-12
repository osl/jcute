package cute.instrument;

import soot.Value;
import soot.SootMethodRef;
import soot.Scene;
import soot.jimple.Stmt;
import soot.jimple.Jimple;
import soot.jimple.IntConstant;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:28:11 PM
 */
public class AddCallWithObject {
    public static void instrument(Value v,Chain units,Stmt s,String methodName,boolean before,int lineNo){
         SootMethodRef mr = null;

         mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(java.lang.Object,int)>").makeRef();
         if(before){
             units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,v,IntConstant.v(lineNo))),s);
         } else {
             units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,v,IntConstant.v(lineNo))),s);
         }
     }
}
