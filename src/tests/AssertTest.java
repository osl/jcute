package tests;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class AssertTest extends Thread{

    synchronized public void run() {
        try {
            System.out.println("Nothing");
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        AssertTest at = new AssertTest();
        at.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
