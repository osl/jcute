package cute.dcute;

import cute.concolic.Globals;

import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 *  .
 * User: ksen
 * Date: Oct 9, 2005
 * Time: 2:30:44 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class Actor {
    protected IdentityHashMap vc;
    private int qid = 0;

    public Actor(){
        vc = new IdentityHashMap();
        vc.put(this,new Integer(1));
    }

    public void send(Actor a,Message m){
        send(a,qid++,m);
    }

    public void send(Actor a,int qid,Message m){
        addOneToVc();
        if(Globals.globals.information.optimalDistributed)
            DScheduler.sched.checkAndSetRace(this,a,qid);
        m.vc = new IdentityHashMap(vc);
        DScheduler.sched.sendMessage(a,qid,m);
    }

    abstract public void receive(Message m);

    public void addOneToVc(Actor a){
        Integer time = (Integer)vc.get(a);
        if(time==null){
            vc.put(a,new Integer(1));
        } else {
            vc.put(a,new Integer(time.intValue()+1));
        }
    }

    public void addOneToVc(){
        addOneToVc(this);
    }

    public int getTime(Actor t){
        Integer time = (Integer)vc.get(t);
        if(time==null){
            return 0;
        } else {
            return time.intValue();
        }
    }

    public void putTime(Actor t,int time){
        if(time==0){
            vc.remove(t);
        } else {
            vc.put(t,new Integer(time));
        }
    }


    public void maxVc(IdentityHashMap vc2){
        if(vc2==null) return;
        for (Iterator iterator = vc2.keySet().iterator(); iterator.hasNext();) {
            Actor t = (Actor) iterator.next();
            int time2 = ((Integer)vc2.get(t)).intValue();
            int time1 = getTime(t);
            if(time2>time1){
                putTime(t,time2);
            }
        }
    }

    public boolean isLE(IdentityHashMap vc2){
        for (Iterator iterator = vc.keySet().iterator(); iterator.hasNext();) {
            Actor t1 = (Actor) iterator.next();
            Integer time2 = (Integer)vc2.get(t1);
            int ltime2;
            if(time2==null){
                ltime2 = 0;
            } else {
                ltime2 = time2.intValue();
            }
            if(getTime(t1)>ltime2){
                return false;
            }
        }
        return true;
    }

    public boolean isGE(IdentityHashMap vc2){
        for (Iterator iterator = vc2.keySet().iterator(); iterator.hasNext();) {
            Actor t2 = (Actor) iterator.next();
            Integer time2 = (Integer)vc2.get(t2);
            int ltime2;
            if(time2==null){
                ltime2 = 0;
            } else {
                ltime2 = time2.intValue();
            }
            if(ltime2>getTime(t2))
                return false;
        }
        return true;
    }

    public boolean isIndependent(IdentityHashMap vc2){
        return !isLE(vc2) && !isGE(vc2);
    }


}
