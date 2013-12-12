package cute.instrument;

import soot.jimple.*;
import soot.*;
import soot.util.Chain;

import java.util.LinkedList;
import java.util.Iterator;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:07:52 PM
 */
public class InstrumentLookupSwitchStatement {
    public static int instrument(int branchCount, int tBanchCount,
                                 Stmt s, SootMethod thisMethod, Body body, Chain units,
                                 SymbolTable st,boolean isConcurrent,int lineNo){
        LookupSwitchStmt lss = (LookupSwitchStmt)s;
        int sz = lss.getTargetCount();
        NopStmt ns = Jimple.v().newNopStmt();
        LinkedList poss = new LinkedList();
        for(int i=0;i<sz;i++){
            EqExpr eq = Jimple.v().newEqExpr(lss.getKey(),IntConstant.v(lss.getLookupValue(i)));
            branchCount++;
            Value v1 = StringConstant.v(thisMethod.getSignature());
            Value v2 = IntConstant.v(branchCount);
            Value v3 = IntConstant.v(tBanchCount);
            LinkedList args = new LinkedList();
            args.addLast(v1);
            args.addLast(v2);
            args.addLast(v3);
            args.addLast(IntConstant.v(lineNo));
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void branchPos(java.lang.String,int,int,int)>").makeRef();
            Stmt pos = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args));
            mr = Scene.v().getMethod("<cute.concolic.Call: void branchNeg(java.lang.String,int,int,int)>").makeRef();
            Stmt neg = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args));
            ParseExpr.instrument(body,eq,null,units,s,st,isConcurrent,lineNo,true);
            IfStmt is2 = Jimple.v().newIfStmt(eq,pos);
            units.insertBefore(is2,s);
            units.insertBefore(neg,s);
            poss.addLast(pos);
        }
        units.insertBefore(Jimple.v().newGotoStmt(ns),s);
        for (Iterator iterator = poss.iterator(); iterator.hasNext();) {
            Stmt stmt = (Stmt) iterator.next();
            units.insertBefore(stmt,s);
            units.insertBefore(Jimple.v().newGotoStmt(ns),s);
        }
        units.insertBefore(ns,s);
        return branchCount;
    }
    
}
