package tests;

import cute.Cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 23, 2006
 * Time: 4:51:30 PM
 */
public class Testme {

    public static void testme(int x,int y){
        int z = foo(y);
        if(z==x){
            if(x>y+10){
                Cute.Assert(false); // ERROR
            }

        }
    }

    private static int foo(int y) {
        return 2*y;
    }

    public static void main(String[] args){
        int x = Cute.input.Integer();
        int y = Cute.input.Integer();
        testme(x,y);
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=true
//@jcute.optionExtraOptions=-t 384
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=10000
//@jcute.optionLogLevel=3
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
