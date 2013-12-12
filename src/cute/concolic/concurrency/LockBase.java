package cute.concolic.concurrency;


import java.util.IdentityHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 26, 2006
 * Time: 2:50:09 PM
 */

class PrevCount {
    private IdentityHashMap prevC = new IdentityHashMap();

    public void setCount(int c){
        Thread curr = Thread.currentThread();
        prevC.put(curr,new Integer(c));
    }

    public int getCount(){
        Thread curr = Thread.currentThread();
        Integer i = (Integer)prevC.get(curr);
        if(i==null)
            return 1;
        else {
            return i.intValue();
        }
    }
}


public class LockBase {
    private IdentityHashMap locks;

    public LockBase() {
        locks = new IdentityHashMap();
    }

    public boolean isHeldByCurrentThread(Object l){
        LockCount lc = (LockCount)locks.get(l);
        if(lc==null){
            return false;
        } else {
            if(lc.getThread()==Thread.currentThread()){
                return lc.getCount()>0;
            } else {
                return false;
            }
        }
    }

    public void incrementCount(Object l){
        LockCount lc = (LockCount)locks.get(l);
        assert lc.getCount()>0;
        lc.increment();
    }

    public void decrementCount(Object l){
        LockCount lc = (LockCount)locks.get(l);
        assert lc.getCount()>0;
        lc.decrement();

    }

    public void acquire(Object l){
        LockCount lc = (LockCount)locks.get(l);
        if(lc==null){
            lc = new LockCount();
            locks.put(l,lc);
        }
        assert lc.getCount()==0;
        lc.acquire();
    }

    public void release(Object l){
        LockCount lc = (LockCount)locks.get(l);
        assert lc.getCount()>0;
        lc.release();
    }

    public boolean isHeldByAnyThread(Object l) {
        LockCount lc = (LockCount)locks.get(l);
        if(lc==null)
            return false;
        else
            return lc.getCount()>0;
    }

    public int getCount(Object l) {
        LockCount lc = (LockCount)locks.get(l);
        if(lc==null){
            return 0;
        } else {
            return  lc.getCount();
        }
    }
}
