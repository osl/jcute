package cute.instrument;

import soot.Value;
import soot.SootMethodRef;
import soot.Local;
import soot.Scene;
import soot.jimple.*;
import soot.util.Chain;

import java.util.LinkedList;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:36:19 PM
 */
public class AddCallWithAddress {
/*
            addCallWithAddress(v,units,s,"loadAddress",true);
            the above inserts before the statement s loadAddress(address of v)
*/
    public static void instrument(Value v,Chain units,Stmt s,
                                  String methodName,boolean before,SymbolTable st,int lineNo,boolean definitelyAdd){
        SootMethodRef mr = null;
        Value v1 = null;
        Value v2 = null;
        if(v instanceof Local){
            mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(int,int,int)>").makeRef();
            v1 = IntConstant.v(0);
            v2 = IntConstant.v(st.get(((Local)v).getName()));
        } else if(v instanceof ArrayRef){
            mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(java.lang.Object,int,int)>").makeRef();
            v1 = ((ArrayRef)v).getBase();
            v2 = ((ArrayRef)v).getIndex();
        } else if(v instanceof InstanceFieldRef){
            mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(java.lang.Object,int,int)>").makeRef();
            v1 = ((InstanceFieldRef)v).getBase();
            v2 = IntConstant.v(st.get(((InstanceFieldRef)v).getField().getName()));
        } else if(v instanceof StaticFieldRef){
            mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(int,int,int)>").makeRef();
            v1 = IntConstant.v(st.get(((StaticFieldRef)v).getField().getDeclaringClass().getName()));
            v2 = IntConstant.v(st.get(((StaticFieldRef)v).getField().getName()));
        } else if(definitelyAdd) {
            mr = Scene.v().getMethod("<cute.concolic.Call: void "+methodName+"(int,int,int)>").makeRef();
            v1 = IntConstant.v(0);
            v2 = IntConstant.v(0);
        }
        if(mr!=null){
            LinkedList args = new LinkedList();
            args.addLast(v1);
            args.addLast(v2);
            args.addLast(IntConstant.v(lineNo));
            if(before){
                units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args)),s);
            } else {
                units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args)),s);
            }
        }
    }

}
