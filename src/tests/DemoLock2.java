package tests;

import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class DemoLock2 extends Thread {
    static String lock = "";
    static int x;

    public void f(int y){
        DemoLock2 d = new DemoLock2();
        d.start();

        synchronized(lock) {
            x = 1;
        }

        synchronized(lock){
            if(x==2*y){
                System.out.println("ERROR");
            }
        }
    }

    public void run() {

        synchronized(lock) {
            x=2;
        }
    }

    public static void main(String[] args) {
        int y;
        y = Cute.input.Integer();
        DemoLock2 d = new DemoLock2();
        d.f(y);
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
