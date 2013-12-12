package dstest;

import instrumented.java.util.Vector;
import cute.Cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 26, 2005
 * Time: 5:05:28 PM
 */
public class MTVectorTest extends Thread {
    Vector v1;
    Vector v2;

    public MTVectorTest(Vector v1, Vector v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public void run() {
        int c = Cute.input.Integer();
        SimpleObject o1 = (SimpleObject)Cute.input.Object("dstest.SimpleObject");
        switch(c){
            case 0:
                v1.add(o1);
                break;
            case 1:
                v1.addAll(v2);
                break;
            case 2:
                v1.clear();
                break;
            case 3:
                v1.contains(o1);
                break;
            case 4:
                v1.containsAll(v2);
                break;
            case 5:
                v1.hashCode();
                break;
            case 6:
                v1.remove(o1);
                break;
            case 7:
                v1.removeAll(v2);
                break;
            default:
                v1.retainAll(v2);
                break;
        }
    }

    public static void main(String[] args) {
        Vector v1 = new Vector();
        Vector v2 = new Vector();
        (new MTVectorTest(v1,v2)).start();
        (new MTVectorTest(v2,v1)).start();
        (new MTVectorTest(v1,v2)).start();
        (new MTVectorTest(v2,v1)).start();
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java\sootOutput
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=20000
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
