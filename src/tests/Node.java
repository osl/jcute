package tests;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class Node {
    public int elem;
    public Node next;

    synchronized boolean swapElem(){
        synchronized (next) {
            if (elem > next.elem) {
// actual swap
                int t = elem;
                elem = next.elem;
                next.elem = t;
                return true;
            }
        }
        return false; // do nothing
    }

    synchronized boolean inOrder(){
        synchronized (next) {
            if (elem > next.elem) return false;
            return true;
        }
    }
}

