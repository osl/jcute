package cute.concolic.pathconstraint;

import cute.concolic.logging.Logger;
import cute.concolic.logging.Printable;
import cute.concolic.symbolicstate.ArithmeticExpression;
import cute.concolic.Information;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class PathConstraint implements Printable {
    private Logger logger;
    private Information information;

    private Vector pathc;
    private Vector otherBranchTaken;

    public PathConstraint(Logger logger, Information information) {
        this.logger = logger;
        this.information = information;
        pathc = new Vector();
        otherBranchTaken = new Vector();
    }

    public void add(Constraint c,boolean isOtherBranchTaken) {
        pathc.add(c);
//        if(c instanceof ArithmeticExpression){
//            ((ArithmeticExpression)c).checkValidity();
//        }
        otherBranchTaken.add(new Boolean(isOtherBranchTaken));
        if((information.debugLevel&32)!=0) logger.info(32,null,this);
    }

    public void print(PrintWriter out){
        int count=0;
        out.println("-------------------");
        for (Iterator iterator = pathc.iterator(); iterator.hasNext();) {
            count++;
            out.print(count-1);
            out.print(" : ");
            Constraint constraint = (Constraint) iterator.next();
            if(constraint==null){
                out.println("null");
            } else {
                constraint.printConstraint(out);
            }
        }
        out.println("-------------------");
        out.flush();
    }

    public int size() {
        return pathc.size();
    }


    public boolean isOtherBranchTaken(int k){
        Boolean b = (Boolean)otherBranchTaken.get(k);
        return b.booleanValue();
    }

    public ArithmeticExpression getArith(int k) {
        Constraint c = (Constraint)pathc.get(k);
        if(c==null) return null;
        if(c instanceof ArithmeticExpression) return (ArithmeticExpression)c;
        else return null;
    }

    public PointerConstraint getPointer(int k) {
        Constraint c = (Constraint)pathc.get(k);
        if(c==null) return null;
        if(c instanceof PointerConstraint) return (PointerConstraint)c;
        else return null;
    }

    public ScheduleConstraint getSchedule(int k) {
        Constraint c = (Constraint)pathc.get(k);
        if(c==null) return null;
        if(c instanceof ScheduleConstraint) return (ScheduleConstraint)c;
        else return null;
    }

    public DSchedule getDSchedule(int k) {
        Constraint c = (Constraint)pathc.get(k);
        if(c==null) return null;
        if(c instanceof DSchedule) return (DSchedule)c;
        else return null;
    }

}
