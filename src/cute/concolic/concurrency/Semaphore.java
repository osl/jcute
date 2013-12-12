package cute.concolic.concurrency;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class Semaphore {
    private int counter;

    public Semaphore() {
        this(0);
    }

    public Semaphore(int i) {
        if (i < 0) throw new IllegalArgumentException(i + " < 0");
        counter = i;
    }

    public synchronized void release() {
        counter++;
        this.notify();
    }

    public synchronized void acquire(){
        while (counter <= 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
        counter--;
    }

    public synchronized void decrement(int i) {
        counter = counter-i;
    }
}

