package tests;

import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class Demo extends Thread {
    static int x;

    public void f(int y){
        Demo d = new Demo();
        d.start();
        x=1;
        if(x==2*y){
            System.out.println("ERROR");
        }
    }

    public void run() {
        x = 2;
    }

    public static void main(String[] args) {
        int y;
        y = Cute.input.Integer();
        Demo d = new Demo();
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
//@jcute.optionLogRace=false
//@jcute.optionLogDeadlock=false
//@jcute.optionLogException=false
//@jcute.optionLogAssertion=false
//@jcute.optionUseRandomInputs=false
