package dstest;

import instrumented.java.util.*;
import cute.Cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 29, 2005
 * Time: 10:21:17 AM
 */
public class MTSetTest extends Thread {
    Set s1,s2;

    public MTSetTest(Set s1, Set s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public void run() {
        int c = Cute.input.Integer();
        SimpleObject o1 = (SimpleObject)Cute.input.Object("dstest.SimpleObject");
        Cute.Assume(o1!=null);
        switch(c){
            case 0:
                s1.add(o1);
                break;
            case 1:
                s1.addAll(s2);
                break;
            case 2:
                s1.clear();
                break;
            case 3:
                s1.contains(o1);
                break;
            case 4:
                s1.containsAll(s2);
                break;
            case 5:
                s1.remove(o1);
                break;
            case 6:
                s1.removeAll(s2);
                break;
            default:
                s1.retainAll(s2);
                break;
        }
    }

    public static void linkedHashSet() {
        Set s1 = Collections.synchronizedSet(new LinkedHashSet());
        Set s2 = Collections.synchronizedSet(new LinkedHashSet());
        (new MTSetTest(s1,s2)).start();
        (new MTSetTest(s2,s1)).start();
        (new MTSetTest(s1,s2)).start();
        (new MTSetTest(s2,s1)).start();
    }

    public static void treeSet() {
        Set s1 = Collections.synchronizedSet(new TreeSet());
        Set s2 = Collections.synchronizedSet(new TreeSet());
        (new MTSetTest(s1,s2)).start();
        (new MTSetTest(s2,s1)).start();
        (new MTSetTest(s1,s2)).start();
        (new MTSetTest(s2,s1)).start();
    }

    public static void main(String[] args) {
        Set s1 = Collections.synchronizedSet(new HashSet());
        Set s2 = Collections.synchronizedSet(new HashSet());
        (new MTSetTest(s1,s2)).start();
        (new MTSetTest(s2,s1)).start();
        (new MTSetTest(s1,s2)).start();
        (new MTSetTest(s2,s1)).start();
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=false
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=20000
//@jcute.optionLogLevel=1
//@jcute.optionLogStatistics=true
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=false
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=false
//@jcute.optionUseRandomInputs=false
