package cute.instrument;

import soot.jimple.Stmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:22:06 PM
 */
public class InstrumentMonitorStatements {
    public static void instrument(Stmt s, Chain units,boolean isConcurrent,int lineNo){
        if(isConcurrent){
            if(s instanceof EnterMonitorStmt){
                AddCallWithObject.instrument(((EnterMonitorStmt)s).getOp(),units,s,"lock",true,lineNo);
            } else if(s instanceof ExitMonitorStmt){
                AddCallWithObject.instrument(((ExitMonitorStmt)s).getOp(),units,s,"unlock",false,lineNo);
            }
        }
    }

}
