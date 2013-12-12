package cute.instrument;

import soot.Body;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:34:31 PM
 */
public class InstrumentAssignmentStatement {
    public static void instrument(Stmt s, Body body, Chain units,boolean isConcurrent,SymbolTable st,int lineNo){
        AssignStmt as = (AssignStmt)s;
        Value left = as.getLeftOp();
        if(left instanceof InstanceFieldRef){
            if(((InstanceFieldRef)left).getField().getName().equals("this$0"))
                return;
        }
        Value right = as.getRightOp();
        if(isConcurrent){
            if(left instanceof ArrayRef || left instanceof InstanceFieldRef || left instanceof StaticFieldRef){
                AddCallWithAddress.instrument(left,units,s,"writeAccess",true,st,lineNo,false);
            }

            if(right instanceof ArrayRef || right instanceof InstanceFieldRef || right instanceof StaticFieldRef){
                AddCallWithAddress.instrument(right,units,s,"readAccess",true,st,lineNo,false);
            }
        }
        if(right instanceof InvokeExpr || right instanceof LengthExpr){
            ParseExpr.instrument(body,right,left,units,s,st,isConcurrent,lineNo,false);
        } else {
            ParseExpr.instrument(body,right,left,units,s,st,isConcurrent,lineNo,false);
            AddCallWithAddress.instrument(left,units,s,"store",true,st,lineNo,false);
        }
    }

}
