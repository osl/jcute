package cute.concolic.concurrency;


/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 26, 2006
 * Time: 2:50:09 PM
 */
class LockCount{
    private Thread thread = null;
    private int count = 0;
    private PrevCount prevC = new PrevCount();

    public Thread getThread() {
        return thread;
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        count++;
    }

    public void decrement() {
        prevC.setCount(count);
        count--;
    }

    public void release(){
        prevC.setCount(count);
        count=0;
    }

    public void acquire(){
        thread = Thread.currentThread();
        count = prevC.getCount();
    }
}
