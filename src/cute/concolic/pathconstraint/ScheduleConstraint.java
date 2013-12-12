package cute.concolic.pathconstraint;

import java.io.Serializable;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ScheduleConstraint implements Serializable, Constraint{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5535478144645041080L;
	private LinkedList postponed;
    private Integer currentThreadId;
    private int enabledThreadCount;
    private boolean isRace;

    public ScheduleConstraint() {
        postponed = new LinkedList();
        isRace = false;
    }

    public void setThreadId(Integer thread) {
        this.currentThreadId = thread;
    }

    public LinkedList getPostponed(){
        return new LinkedList(postponed);
    }

    public void setEnabledThreadCount(int i){
        enabledThreadCount = i;
    }

    public boolean isBackTrackingRequired(){
        return isRace && (postponed.size()<enabledThreadCount-1);
    }

    public boolean isBackTrackingPoint(){
        return isRace || postponed.size()>0;
    }

    public void postponeCurrentThread(){
        postponed.addLast(currentThreadId);
        isRace = false;
    }

    public void printConstraint(PrintWriter out) {
        out.print("postponed = " + postponed);
        out.print("\tthread = " + currentThreadId);
        out.println("\tisRace = " + isRace);
    }

    public void setRace(boolean race) {
        isRace = race;
    }

    public Integer getThreadId() {
        return currentThreadId;
    }

    public void setPostponed(LinkedList postponed) {
        this.postponed.clear();
        this.postponed.addAll(postponed);
    }
}
