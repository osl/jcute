package tests;

import cute.Cute;

/**
 *  .
 * User: ksen
 * Date: Oct 25, 2005
 * Time: 7:38:52 PM
 * To change this template use File | Settings | File Templates.
 */

class MyException extends RuntimeException {

}

public class SwitchTest {

    public static int f(int x){
        if(x>199){
            throw new MyException();
        } else {
            return 2*x+1;
        }
    }

    public static int g(int y){
        int ret = f(y) * 23;
        return ret;
    }

    public static void main(String[] args) {
        int x = Cute.input.Integer();
        int y;
        switch(x){
            case -100:
                y=1;
                break;
            case 0:
                y = 2;
                break;
            case 100:
                y = 3;
                break;
            default:
                y=4;
        }
        try {
            int z = g(x);
            if(z==69){
                System.out.println("y = " + y);
            }
        } catch(MyException e){
            y = x+10;
            if(y==250)
                System.out.println("OOPS ...");
        }
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=true
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=D:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=100
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
