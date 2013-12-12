package cute.gui;

import cute.concolic.generateinputandschedule.ProgressLog;
import cute.concolic.logging.BranchCoverageLog;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 6:31:07 PM
 */
public interface ProgressLogger {
    void setProgress(ProgressLog plog, BranchCoverageLog cover,
                     int runCount,int total,int branchCount,int errors);
}
