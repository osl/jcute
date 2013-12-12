package cute.instrument;

import soot.jimple.*;
import soot.util.Chain;
import soot.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:47:31 PM
 */
public class AddSyncMethodInstr {
    public static void instrument(InvokeExpr right,Chain units,Body body,Stmt s,boolean before,String mc,int lineNo){
        if(right.getMethod().isSynchronized()){

            if(right instanceof InstanceInvokeExpr){
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,mc,before,lineNo);
            } else if(right instanceof StaticInvokeExpr){
                Local tmpLocal = Jimple.v().newLocal("__ct_"+body.getLocalCount(),RefType.v("java.lang.Object"));
                body.getLocals().add(tmpLocal);
                SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: java.lang.Class myClass(java.lang.String)>").makeRef();
                String cname = ((StaticInvokeExpr) right).getMethod().getDeclaringClass().getName();
                InvokeExpr ie = Jimple.v().newStaticInvokeExpr(mr,StringConstant.v(cname));
                if(before){
                    units.insertBefore(Jimple.v().newAssignStmt(tmpLocal,ie),s);
                    AddCallWithObject.instrument(tmpLocal,units,s,mc,before,lineNo);
                } else {
                    AddCallWithObject.instrument(tmpLocal,units,s,mc,before,lineNo);
                    units.insertAfter(Jimple.v().newAssignStmt(tmpLocal,ie),s);
                }
            }
        } else if(right instanceof InstanceInvokeExpr && right.getMethod().getSubSignature().indexOf("<init>")==-1){
            AddCallWithObjectAndString.instrument(((InstanceInvokeExpr)right).getBase(),units,s,mc,before,
                    right.getMethod().getSubSignature(),lineNo);
        }

    }

}
