package cute.instrument;

import soot.Value;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 6:01:22 PM
 */
public class InstrumentIdentityStatement {
    public static void instrument(Stmt s, Chain units,SymbolTable st,int lineNo){
         IdentityStmt is = (IdentityStmt)s;
         Value left = is.getLeftOp();
         Value right = is.getRightOp();
         if(right instanceof ParameterRef || right instanceof ThisRef){
             AddCallWithAddress.instrument(left,units,s,"popStore",true,st,lineNo,false);
         }
     }

}
