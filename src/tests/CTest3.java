package tests;




/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class CTest3 {
    public static int x;
    public static int z;
    public static Sem3 sem;



    public static void main(String[] args) {
        TTest3 t = new TTest3();
        x=0;
        sem = new Sem3();
        t.start();
        sem.acquire();
        z = 1;
        x = 4;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}

class TTest3 extends Thread {
    public void run() {
        CTest3.x=3;
        CTest3.z = 5;
        CTest3.sem.release();
    }
}

class Sem3 {
    private int counter;

    public Sem3() {
        this(0);
    }

    public Sem3(int i) {
        if (i < 0) throw new IllegalArgumentException(i + " < 0");
        counter = i;
    }

    public synchronized void release() {
        counter++;
        this.notify();// aditi
    }

    public synchronized void acquire(){
        acquireTmp();
    }

    public void acquireTmp(){
        while (counter <= 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        counter--;
    }

}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=20
//@jcute.optionLogLevel=2
//@jcute.optionLogStatistics=true
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
