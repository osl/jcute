package cute.concolic.symbolicexecution;

import cute.concolic.ObjectInput;
import cute.concolic.ObjectOutput;
import cute.concolic.Information;
import cute.concolic.logging.Logger;
import cute.concolic.logging.Printable;
import cute.concolic.pathconstraint.DSchedule;
import cute.concolic.pathconstraint.ScheduleConstraint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class BranchHistory implements Printable {
    private Logger logger;
    private Information information;

    private Vector history;
    private int initSize;
    private boolean isOk;

    public BranchHistory(Logger logger,Information information) {
        this.logger = logger;
        this.information = information;
    }

    public int getInitSize() {
        return initSize;
    }

    public boolean isOK(){
        return isOk;
    }

    private void setSize(int k){
        history.setSize(k);
    }

    public int size(){
        return history.size();
    }

    public void read(){
        isOk = true;
        initSize = 0;
        ObjectInput in = new ObjectInput("Branches",true,information);
        if(in.ok()){
            try{
                //System.out.println("Here");
                //System.out.flush();
                history = (Vector)in.in.readObject();

                while(in.in.available()>0){
                    history.add(in.in.readObject());
                }
            } catch(Exception e){
                e.printStackTrace();
                System.exit(1);
            }
            in.close();
        } else {
            history = new Vector(20);
        }
        initSize = history.size();
    }

    public boolean getBranch(int i){
        BranchElem be = (BranchElem)history.get(i);
        return be.branch;
    }

    public boolean getStatus(int i){
        Object c = history.get(i);
        if(!(c instanceof BranchElem))
            return false;
        BranchElem be = (BranchElem)c;
        return be.status;
    }

    private void addBranchAndStatus(boolean branch){
        history.add(new BranchElem(branch,false));
    }

    public void setBranchAndStatus(int i,boolean branch,boolean status){
        history.set(i,new BranchElem(branch,status));
    }


    public void write(int k){
        history.setSize(k);
        ObjectOutput out = new ObjectOutput("Branches");
        if(out.ok()){
            try {
                out.out.writeObject(history);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
            out.close();
        }
    }

    public void compareAndSet(boolean branch,int sizeSoFar){
        int k = sizeSoFar;
        k--;
        if(k<size()){
            if(getBranch(k)!=branch){
                isOk = false;
                setSize(k);
                addBranchAndStatus(branch);
                System.err.println("Prediction failed at "+(k+1));
            } else if(k==size()-1 && isOk){
                setBranchAndStatus(k,branch,!getStatus(k));
            }
        } else {
            addBranchAndStatus(branch);
        }
        if((information.debugLevel&8)!=0) logger.info(8,null,this);
    }

    public Object get(int k) {
        return history.get(k);
    }

    public void add(ScheduleConstraint tmp) {
        history.add(tmp);
        if((information.debugLevel&8)!=0) logger.info(8,null,this);
    }

    public void add(DSchedule sched){
        history.add(sched);
        if((information.debugLevel&8)!=0) logger.info(8,null,this);
    }

    public void print(PrintWriter out) {
        int i = 0;
        for (Iterator iterator = history.iterator(); iterator.hasNext();) {
            out.print(i+": ");
            i++;
            Object elem = iterator.next();
            if(elem instanceof BranchElem)
                ((BranchElem)elem).print(out);
            if(elem instanceof ScheduleConstraint)
                ((ScheduleConstraint)elem).printConstraint(out);
            if(elem instanceof DSchedule)
                ((DSchedule)elem).printConstraint(out);
        }
        out.flush();
    }
}
