package cute.concolic.concurrency;

import java.util.IdentityHashMap;
import java.util.Vector;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class VarInfo {
    private IdentityHashMap vc;
    private Vector locks;
    private int index;
    private int indexForLock;

    public VarInfo(IdentityHashMap vc, Vector locks, int index) {
        this.vc = new IdentityHashMap(vc);
        this.locks = new Vector(locks);
        this.indexForLock = this.index = index;
    }

    public IdentityHashMap getVc() {
        return vc;
    }

    public Vector getLocks() {
        return locks;
    }

    public int getIndex() {
        return index;
    }

    public int getIndexForLock() {
        return indexForLock;
    }

    public void setIndexForLock(int i) {
        indexForLock = i;
    }
}
