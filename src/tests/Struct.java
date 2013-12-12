package tests;
import cute.Cute;

public class Struct {
    public int x = 0;
    public Struct next;

    public static int f(int v){
        return 2*v+1;
    }

    public static void testme(Struct p,int y){
        if(y>0){
            if(p!=null){
                if(f(y)==p.x){
                    Cute.Assert(p.next!=p);
                }
            }
        }
    }

    public static void main(String[] args) {
        Struct s = (Struct)cute.Cute.input.Object("tests.Struct");
        int y = cute.Cute.input.Integer();
        testme(s,y);
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
//@jcute.optionNumberOfPaths=100
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=false
//@jcute.optionLogDeadlock=false
//@jcute.optionLogException=false
//@jcute.optionLogAssertion=false
//@jcute.optionUseRandomInputs=false
