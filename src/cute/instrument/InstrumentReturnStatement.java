package cute.instrument;

import soot.jimple.Stmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Jimple;
import soot.jimple.IntConstant;
import soot.util.Chain;
import soot.Value;
import soot.SootMethodRef;
import soot.Scene;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:15:22 PM
 */
public class InstrumentReturnStatement {
    public static void instrument(Stmt s,Chain units,SymbolTable st,int lineNo) {
        Value varg = ((ReturnStmt)s).getOp();
        SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void popAll(int)>").makeRef();
        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
        AddCallWithAddress.instrument(varg,units,s,"setReturn",true,st,lineNo,false);
    }
}


