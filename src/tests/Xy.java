package tests;




/**
 *  .
 * User: ksen
 * Date: Oct 3, 2005
 * Time: 12:47:43 AM
 * To change this template use File | Settings | File Templates.
 */

class X extends Thread {
    public void run() {
        Xy.x = 1;
    }
}

class Y extends Thread {
    public void run(){
        Xy.y=2;
    }
}

public class Xy {
    public static int x;
    public static int y;

    public static void main(String[] args) {
        (new X()).start();
        (new Y()).start();
        (new X()).start();
        (new Y()).start();
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
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
