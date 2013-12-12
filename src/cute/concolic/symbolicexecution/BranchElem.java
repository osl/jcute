package cute.concolic.symbolicexecution;

import java.io.Serializable;
import java.io.PrintWriter;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class BranchElem implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1116442450936361795L;
    public boolean branch;
    public boolean status;

    public BranchElem(boolean branch, boolean status) {
        this.branch = branch;
        this.status = status;
    }

    public void print(PrintWriter out) {
        out.print("branch = " + branch);
        out.println("  status = " + status);
    }
}
