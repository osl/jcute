package tests;



/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class OneWrite extends Thread {
    public static int x;

    public OneWrite(String name) {
        super(name);
    }

    public void run() {
        x = 1;
    }

    public static void main(String[] args) {
        (new OneWrite("t1")).start();
        (new OneWrite("t2")).start();
        (new OneWrite("t3")).start();
        (new OneWrite("t4")).start();
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
//@jcute.optionNumberOfPaths=50
//@jcute.optionLogLevel=2
//@jcute.optionLogStatistics=true
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=false
//@jcute.optionLogDeadlock=false
//@jcute.optionLogException=false
//@jcute.optionLogAssertion=false
