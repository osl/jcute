package cute.instrument;

import soot.Value;
import soot.SootMethodRef;
import soot.Scene;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.util.Chain;

import java.util.LinkedList;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:48:59 PM
 */
public class AddCallWithObjectAndString {
    public static void instrument(Value v,Chain units,Stmt s,String methodName,boolean before,String sig,int lineNo){
        SootMethodRef mr = null;

        sig = sig.substring(sig.indexOf(' ')+1);

        mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(java.lang.Object,java.lang.String,int)>").makeRef();
        LinkedList args = new LinkedList();
        args.addLast(v);
        args.addLast(StringConstant.v(sig));
        args.addLast(IntConstant.v(lineNo));
        if(before){
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args)),s);
        } else {
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args)),s);
        }
    }

}
