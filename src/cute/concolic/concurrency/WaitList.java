package cute.concolic.concurrency;

import java.util.IdentityHashMap;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class WaitList {
    private IdentityHashMap waits;

    public WaitList() {
        waits = new IdentityHashMap();
    }

    public void add(Object lock,Thread t){
        IdentityHashMap threads = (IdentityHashMap)waits.get(lock);
        if(threads==null){
            threads = new IdentityHashMap();
            waits.put(lock,threads);
        } else {
            assert !threads.containsKey(t);
        }
        threads.put(t,t);
        //System.out.println("waits add = " + Thread.currentThread()+" "+ waits);
        //System.out.flush();
    }

    public void remove(Object lock,Thread t){
        IdentityHashMap threads = (IdentityHashMap)waits.get(lock);
        assert threads!=null;
        assert threads.containsKey(t);
        threads.remove(t);
    }

    public boolean contains(Object lock){
        IdentityHashMap threads = (IdentityHashMap)waits.get(lock);
        if(threads==null) return false;
        return !threads.isEmpty();
    }

    public int size(Object l) {
        IdentityHashMap threads = (IdentityHashMap)waits.get(l);
        if(threads==null) return 0;
        return threads.size();
    }

    public String toString() {
        return waits.toString();
    }
}
