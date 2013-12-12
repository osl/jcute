package tests;




/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class CTest1 {
    public static int x;
    public static int z;
    public static Sem sem;



    public static void main(String[] args) {
        TTest1 t = new TTest1();
        x=0;
        //sem = new Sem();
        t.start();
        //sem.acquire();
        //System.out.println("sem.acquire");
        z = 1;
        //System.out.println("z = 1");
        x = 4;
        //System.out.println("x = 4");
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}

class TTest1 extends Thread {
    public void run() {
        CTest1.x=3;
        //System.out.println("*x = 3");
        CTest1.z = 5;
        //System.out.println("*z = 5");
        //CTest1.sem.release();
        //System.out.println("*sem.release");
    }
}

class Sem {
    private int counter;

    public Sem() {
        this(0);
    }

    public Sem(int i) {
        if (i < 0) throw new IllegalArgumentException(i + " < 0");
        counter = i;
    }

    public synchronized void release() {
        counter++;
        this.notify();// aditi
    }

    public synchronized void acquire(){
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
