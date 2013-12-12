package cute.concolic.pathconstraint;

import java.io.PrintWriter;
import java.io.Serializable;

/**
 *  .
 * User: ksen
 * Date: Oct 9, 2005
 * Time: 6:16:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DSchedule implements Serializable, Constraint {
    /**
     *
     */
    private static final long serialVersionUID = 8965352739040973220L;
    public int pid;
    public int mid;
    public int nextPid;
    public int nextMid;
    public boolean isRace;

    public void printConstraint(PrintWriter out) {
        out.print("pid = " + pid);
        out.print("\tmid = " + mid);
        out.print("\tnextPid = " + nextPid);
        out.print("\tnextMid = " + nextMid);
        out.println("\tisRace = " + isRace);
    }
}
