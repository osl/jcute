package cute.dcute;

import cute.concolic.pathconstraint.DSchedule;
import cute.concolic.Globals;

import java.util.*;

/**
 *  .
 * User: ksen
 * Date: Oct 9, 2005
 * Time: 2:31:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DScheduler {
    public static DScheduler sched = new DScheduler();

    private LinkedList q;
    private Vector chist;

    public DScheduler() {
        q = new LinkedList();
        chist = new Vector(20);
    }

    public void sendMessage(Actor a,int qid,Message m){
        boolean found = false;
        ListIterator iter = q.listIterator();
        ActorQueues aq = null;
        while(!found && iter.hasNext()){
            aq = (ActorQueues)iter.next();
            if(aq.getActor()==a){
                found = true;
            }
        }
        if(!found){
            aq = new ActorQueues(a);
            q.addLast(aq);
        }
        aq.addMessage(a,qid,m);
    }

    public void scheduleNext(){
        int k = Globals.globals.path.size();
        int l = Globals.globals.history.size();
        DSchedule ds;
        int pid;
        int mid;

        if (k<l) {
            ds = (DSchedule)Globals.globals.history.get(k);
            Globals.globals.path.add(ds,true);
            pid = ds.pid;
            mid = ds.mid;
            Pair p = next(pid,mid);
            ds.nextPid = p.x;
            ds.nextMid = p.y;
        } else {
            ds = new DSchedule();
            if(!Globals.globals.information.optimalDistributed)
                ds.isRace = true;
            else
                ds.isRace = false;
            ds.pid=pid =0;
            ds.mid=mid = 0;
            Pair p = next(pid,mid);
            ds.nextPid = p.x;
            ds.nextMid = p.y;
            Globals.globals.history.add(ds);
            Globals.globals.path.add(ds,true);
        }
        ActorQueues aq = (ActorQueues)q.get(pid);
        chist.add(new ClockHistory(aq,ds));

        Message m = (Message)aq.getAndRemove(mid);
        if(aq.isEmpty()){
            q.remove(pid);
        }

        if(k>=l && pid>1 && mid==0){
            for(int j=0;j<pid;j++){
                q.addLast(q.removeFirst());
            }
        }


        aq.getActor().maxVc(m.vc);
        aq.getActor().receive(m);
    }

    public void checkAndSetRace(Actor a,Actor to,int qid){
        int sz = chist.size();
        for(int j=0;j<sz;j++){
            ClockHistory ch = (ClockHistory)chist.get(j);
            Actor ra = ch.getActor();
            DSchedule rs = ch.getDs();
            IdentityHashMap rv = ch.getVc();
            LinkedList mqs = ch.getQueues();
            if(to==ra && a.isIndependent(rv)){
                //System.out.println("Set true");
                boolean found = false;
                for (Iterator iterator = mqs.iterator(); !found && iterator.hasNext();) {
                    MessageQueue mq = (MessageQueue) iterator.next();
                    if(mq.equals(to,qid)){
                        found = true;
                    }
                }
                if(!found){
//                    System.out.println("mqs "+mqs.size());
                    rs.isRace=true;
                }
            }
        }
    }

    private Pair next(int pid, int mid) {
        Pair ret = new Pair();
        ActorQueues aq = (ActorQueues)q.get(pid);
        //System.out.println("pid = " + pid);
        //System.out.println("mid = " + mid);
        if(q.size()==pid+1 && aq.size()==mid+1){
            ret.x = -1;
            ret.y = -1;
        } else {
            if(aq.size()==mid+1){
                ret.x = pid+1;
                ret.y = 0;
            } else {
                ret.x = pid;
                ret.y = mid+1;
            }
        }
        //System.out.println("ret.x = " + ret.x);
        //System.out.println("ret.y = " + ret.y);
        return ret;
    }

    public void start(){
        while(!q.isEmpty()){
            scheduleNext();
        }
    }
}
