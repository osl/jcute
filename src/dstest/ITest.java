package dstest;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 21, 2005
 * Time: 9:14:19 AM
 */
class A {

}


public class ITest {
    class B {

    }

    public static void main(String[] args) {
        Object x = (args==null) ? new A() : new Object();
        //new A();
        //problem();
        (new ITest()).problem();
        System.out.println("Try me!");
        String y = "Hello";
        System.out.println("Bool " + y.equals("Hello"));
    }

    public B problem(){
        return new B();
    }

    public static void test1(){
        new A();
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=-t 512
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
