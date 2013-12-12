package tests;

import cute.Cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: May 17, 2006
 * Time: 6:06:53 PM
 */

class PredictiveThread1 extends Thread {
    public void run() {
        int z = Cute.input.Integer();
        if(z==100){
            NoPredictive.x = 4;
            if(NoPredictive. x >= NoPredictive.y)
                System.out.println("OK");
            else
                System.out.println("ERROR");
        } else {
            NoPredictive.x = 5;
            if(NoPredictive. x >= NoPredictive.y)
                System.out.println("OK");
            else
                System.out.println("ERROR");
        }
    }
}

class PredictiveThread2 extends Thread {
    public void run() {
        NoPredictive.y = 2;
        if(NoPredictive. x >= NoPredictive.y)
            System.out.println("OK");
        else
            System.out.println("ERROR");
    }
}

public class NoPredictive {
    public static int x = 0;
    public static int y = 0;

    public static void main(String[] args) {
        (new PredictiveThread1()).start();
        (new PredictiveThread2()).start();
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
//@jcute.optionNumberOfPaths=10000
//@jcute.optionLogLevel=1
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
