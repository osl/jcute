package cute.concolic.concurrency;

import cute.concolic.concurrency.Scheduler;
import cute.concolic.concurrency.Semaphore;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ThreadBase extends Semaphore {
    public Object waitingOn=null;
    public boolean enabled=true;
    public IdentityHashMap vc;
    private Vector locks;
    public long toAccess;
    public int toAccessType;
    public Thread thread;


    public ThreadBase(Thread t){
        vc = new IdentityHashMap();
        locks = new Vector();
        lastRaceAt = new HashMap();
        toAccessType = Scheduler.NONE;
        thread = t;
    }

    public void setVc(IdentityHashMap vc2){
        vc.clear();
        vc.putAll(vc2);
    }

    public IdentityHashMap getVc() {
        return vc;
    }

    public void addOneToVc(Thread t){
        Integer time = (Integer)vc.get(t);
        if(time==null){
            vc.put(t,new Integer(1));
        } else {
            vc.put(t,new Integer(time.intValue()+1));
        }
    }

    public void addOneToVc(){
        Thread t = Thread.currentThread();
        Integer time = (Integer)vc.get(t);
        if(time==null){
            vc.put(t,new Integer(1));
        } else {
            vc.put(t,new Integer(time.intValue()+1));
        }
    }

    public int getTime(Thread t){
        Integer time = (Integer)vc.get(t);
        if(time==null){
            return 0;
        } else {
            return time.intValue();
        }
    }

    public void putTime(Thread t,int time){
        if(time==0){
            vc.remove(t);
        } else {
            vc.put(t,new Integer(time));
        }
    }


    public void maxVc(IdentityHashMap vc2){
        if(vc2==null) return;
        for (Iterator iterator = vc2.keySet().iterator(); iterator.hasNext();) {
            Thread t = (Thread) iterator.next();
            int time2 = ((Integer)vc2.get(t)).intValue();
            int time1 = getTime(t);
            if(time2>time1){
                putTime(t,time2);
            }
        }
    }

    public boolean isLE(IdentityHashMap vc2){
        for (Iterator iterator = vc.keySet().iterator(); iterator.hasNext();) {
            Thread t1 = (Thread) iterator.next();
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
            Thread t2 = (Thread) iterator.next();
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

    public void enableAll() {
        waitingOn = null;
        enabled = true;
    }

    public Vector getLocks() {
        return locks;
    }

    public boolean intersects(Vector locks) {
        for (Iterator iterator = locks.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            for (Iterator iterator1 = this.locks.iterator(); iterator1.hasNext();) {
                Object o1 = iterator1.next();
                if(o1==o){
                    return true;
                }
            }
        }
        return false;
    }

    public void addLock(Object l){
        locks.add(l);
    }

    public void removeLock(){
        locks.remove(locks.size()-1);
    }

    private HashMap lastRaceAt;

    public int getLastRaceAt(Integer tid) {
        Integer index = (Integer)lastRaceAt.get(tid);
        if(index==null) return -1;
        else return index.intValue();
    }

    public void setLastRaceAt(Integer tid, int index) {
        lastRaceAt.put(tid,new Integer(index));
    }

    public void removeAllLocks() {
        locks.setSize(0);
    }

    public Thread getThread() {
        return thread;
    }
}
