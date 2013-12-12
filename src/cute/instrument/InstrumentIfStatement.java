package cute.instrument;

import soot.jimple.*;
import soot.*;
import soot.util.Chain;

import java.util.LinkedList;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:09:46 PM
 */
public class InstrumentIfStatement {
    public static int instrument(int branchCount, int tBanchCount,
                                      Stmt s, SootMethod thisMethod, Body body, Chain units,
                                 SymbolTable st,boolean isConcurrent,int lineNo){
        IfStmt is = (IfStmt)s;
        Value cond = is.getCondition();
        if(cond instanceof BinopExpr){
            ParseExpr.instrument(body,is.getCondition(),null,units,s,st,isConcurrent,lineNo,true);
            branchCount++;
            Value v1 = StringConstant.v(thisMethod.getSignature());
            Value v2 = IntConstant.v(branchCount);
            Value v3 = IntConstant.v(tBanchCount);
            LinkedList args = new LinkedList();
            args.addLast(v1);
            args.addLast(v2);
            args.addLast(v3);
            args.addLast(IntConstant.v(lineNo));
            //args.addLast(tmpLocal);
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void branchPos(java.lang.String,int,int,int)>").makeRef();
            Stmt pos = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args));
            mr = Scene.v().getMethod("<cute.concolic.Call: void branchNeg(java.lang.String,int,int,int)>").makeRef();
            Stmt neg = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,args));
            IfStmt is2 = Jimple.v().newIfStmt(is.getCondition(),pos);
            NopStmt ns = Jimple.v().newNopStmt();
            units.insertBefore(is2,s);
            units.insertBefore(neg,s);
            units.insertBefore(Jimple.v().newGotoStmt(ns),s);
            units.insertBefore(pos,s);
            units.insertBefore(ns,s);
        }
        return branchCount;
    }

    
}
