package cute.instrument;

import soot.jimple.*;
import soot.*;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:03:20 PM
 */
public class InstrumentInvokeStatement {
    /**
     * Instruments a statement of the form o.f(...).  It does not instrument a statement of the form x = o.f(...);
     * for the instrumentation of such statements see
     * {@link InstrumentAssignmentStatement#instrument(soot.jimple.Stmt, soot.Body, soot.util.Chain, boolean, SymbolTable, int)}.
     * The method first extracts the expression corresponding to the statement.
     * It then calls {@link ParseExpr#instrument(soot.Body, soot.Value, soot.util.Chain, soot.jimple.Stmt, SymbolTable, boolean, int, boolean)}
     * on the expression.  Finally, it adds {@link cute.concolic.Call#funEnd(int) cute.Concolic.Call.funEnd(lineNo)}
     * instrumentation function call to the body after the statement.
     * @param s  The statement of the from o.f(...) to be instrumented.
     * @param body  The body of the method containing s.
     * @param units The units of the body of the method containing s.
     * @param st The {@link SymbolTable symbol table object} used for mapping local variables and field names to unique
     * integers.  These integers are later used by the tester.
     * @param isConcurrent  If isConcuurent is set then instrumentation calls are added to perform dynamic partial
     * order reduction.
     * @param lineNo The line number of the statement s.
     */
    public static void instrument(Stmt s, Body body, Chain units,SymbolTable st,boolean isConcurrent,int lineNo) {
        InvokeExpr right = ((InvokeStmt)s).getInvokeExpr();
        String name = right.getMethod().getName();
        if(isConcurrent){
            if(right.getArgCount()==0 && name.equals("wait")){
                Local tmpLocal2 = Jimple.v().newLocal("__ct_"+body.getLocalCount(), IntType.v());
                body.getLocals().add(tmpLocal2);
                Local tmpLocal = Jimple.v().newLocal("__ct_"+body.getLocalCount(), IntType.v());
                body.getLocals().add(tmpLocal);

                SootMethodRef mr
                        = Scene.v().getMethod("<cute.concolic.Call: int getLockDepth(java.lang.Object)>").makeRef();
                units.insertBefore(Jimple.v().newAssignStmt(tmpLocal,
                        Jimple.v().newStaticInvokeExpr(mr,((InstanceInvokeExpr)right).getBase())),s);
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,"waitBefore",true,lineNo);

                Stmt sinc2 = Jimple.v().newAssignStmt(tmpLocal2,Jimple.v().newAddExpr(tmpLocal2,IntConstant.v(1)));
                units.insertAfter(Jimple.v().newIfStmt(Jimple.v().newLtExpr(tmpLocal2,tmpLocal),sinc2),s);
                units.insertAfter(Jimple.v().newEnterMonitorStmt(((InstanceInvokeExpr)right).getBase()),s);
                units.insertAfter(sinc2,s);
                units.insertAfter(Jimple.v().newAssignStmt(tmpLocal2,IntConstant.v(0)),s);

                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,"waitAfter",false,lineNo);

                Stmt sinc = Jimple.v().newAssignStmt(tmpLocal2,Jimple.v().newAddExpr(tmpLocal2,IntConstant.v(1)));
                units.insertAfter(Jimple.v().newIfStmt(Jimple.v().newLtExpr(tmpLocal2,tmpLocal),sinc),s);
//                mr = Scene.v().getMethod("<cute.concolic.Call: void printStr(java.lang.String)>").makeRef();
//                units.insertAfter(
//                        Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
//                                mr,StringConstant.v("Crossed"))),s);
                units.insertAfter(Jimple.v().newExitMonitorStmt(((InstanceInvokeExpr)right).getBase()),s);
                units.insertAfter(sinc,s);
                units.insertAfter(Jimple.v().newAssignStmt(tmpLocal2,IntConstant.v(0)),s);
            }
            else if(right.getArgCount()==0 && name.equals("notify")){
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),
                        units,s,"notifyBefore",true,lineNo);
//                        addCallWithObject(((InstanceInvokeExpr)right).getBase(),units,s,"notifyAfter",false);
            } else if(right.getArgCount()==0 && name.equals("notifyAll")){
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),
                        units,s,"notifyAllBefore",true,lineNo);
//                        addCallWithObject(((InstanceInvokeExpr)right).getBase(),units,s,"notifyAllAfter",false);
            }
            else if(right.getArgCount()==0 && name.equals("start") && right instanceof InstanceInvokeExpr
                    && Utils.isThreadSubType(((InstanceInvokeExpr)right).getMethod().getDeclaringClass())){
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,"startBefore",true,lineNo);
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,"startAfter",false,lineNo);
            } else if(right.getArgCount()==0 && name.equals("join") && right instanceof InstanceInvokeExpr
                    && Utils.isThreadSubType(((InstanceInvokeExpr)right).getMethod().getDeclaringClass())){
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,"joinBefore",true,lineNo);
                AddCallWithObject.instrument(((InstanceInvokeExpr)right).getBase(),units,s,"joinAfter",false,lineNo);
            } else {
                ParseExpr.instrument(body,right,null,units,s,st,isConcurrent,lineNo,false);
            }
        } else {
            ParseExpr.instrument(body,right,null,units,s,st,isConcurrent,lineNo,false);
        }

    }

}
