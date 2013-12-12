package dstest;

import cute.Cute;
import instrumented.java.util.List;
import instrumented.java.util.Collections;
import instrumented.java.util.ArrayList;
import instrumented.java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 26, 2005
 * Time: 9:46:07 AM
 */
public class MTListTest extends Thread {
    List al1, al2;

    public MTListTest(List al1, List al2) {
        this.al1 = al1;
        this.al2 = al2;
    }

    public void run() {
        int c = Cute.input.Integer();
        SimpleObject o1 = (SimpleObject)Cute.input.Object("dstest.SimpleObject");
        switch(c){
            case 0:
                al1.add(o1);
                break;
            case 1:
                al1.addAll(al2);
                break;
            case 2:
                al1.clear();
                break;
            case 3:
                al1.contains(o1);
                break;
            case 4:
                al1.containsAll(al2);
                break;
            case 5:
                al1.hashCode();
                break;
            case 6:
                al1.remove(o1);
                break;
            case 7:
                al1.removeAll(al2);
                break;
            default :
                al1.retainAll(al2);
                break;
        }
    }

    public static void arrayList(){
        List al1 = Collections.synchronizedList(new ArrayList());
        List al2 = Collections.synchronizedList(new ArrayList());
        (new MTListTest(al1,al2)).start();
        (new MTListTest(al2,al1)).start();
        (new MTListTest(al1,al2)).start();
        (new MTListTest(al2,al1)).start();
    }

    public static void main(String args[]){
        List al1 = Collections.synchronizedList(new LinkedList());
        List al2 = Collections.synchronizedList(new LinkedList());
        (new MTListTest(al1,al2)).start();
        (new MTListTest(al2,al1)).start();
        (new MTListTest(al1,al2)).start();
        (new MTListTest(al2,al1)).start();
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
