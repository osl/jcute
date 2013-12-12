package cute.gui;

import cute.concolic.generateinputandschedule.ProgressLog;
import cute.concolic.logging.BranchCoverageLog;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 10:46:33 PM
 */
public class DefaultProgressLogger implements ProgressLogger {
    public void setProgress(ProgressLog plog, BranchCoverageLog cover,
                            int runCount,int totalCount,int branchCount,int errorCount) {
        System.out.println("Paths executed "+runCount+"/"+totalCount);
        System.out.println("Branches covered "+branchCount);
        System.out.println("Erroneous paths "+errorCount);
        if(cover!=null){
            System.out.println("Branch Coverage "+cover.getCoverage()+"%");
        }
        if(plog!=null){
            int total=plog.getTotal();
            int sofar = total - plog.getSofar()+1;
            System.out.println("DFS Total Progress "+String.valueOf(sofar)+"/"+String.valueOf(total));
            System.out.println("DFS Current progress "+(total-plog.getCurrent()+1));
        }

    }
}
