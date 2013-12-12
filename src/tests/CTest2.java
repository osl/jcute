package tests;




/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class CTest2 {
    public static int x;
    public static int z;
    public static Sem2 sem;



    public static void main(String[] args) {
        TTest2 t = new TTest2();
        x=0;
        sem = new Sem2();
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

class TTest2 extends Thread {
    public void run() {
        CTest2.x=3;
        CTest2.z = 5;
        CTest2.sem.release();
    }
}

class Sem2 {
    private int counter;

    public Sem2() {
        this(0);
    }

    public Sem2(int i) {
        if (i < 0) throw new IllegalArgumentException(i + " < 0");
        counter = i;
    }

    public synchronized void release() {
        counter++;
        this.notify();// aditi
    }

    public synchronized void acquire(){
//        acquireTmp();
//    }
//
//    public synchronized void acquireTmp(){
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
//@jcute.optionLogPath=false
//@jcute.optionLogTraceAndInput=false
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=D:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=20
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
