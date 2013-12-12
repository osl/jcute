package dstest;

import instrumented.java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 30, 2005
 * Time: 10:00:50 AM
 */
public class MTSetDeadlock extends Thread {
    Set v1;
    Set v2;

    public MTSetDeadlock(Set v1, Set v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public void run() {
        v1.removeAll(v2);
    }

    public static void hashSetDeadlock() {
        Set v1 = Collections.synchronizedSet(new HashSet());
        v1.add(new SimpleObject(1));
        Set v2 = Collections.synchronizedSet(new HashSet());
        v2.add(new SimpleObject(2));
        (new MTSetDeadlock(v1,v2)).start();
        (new MTSetDeadlock(v2,v1)).start();
    }

    public static void linkedHashSetDeadlock() {
        Set v1 = Collections.synchronizedSet(new LinkedHashSet());
        v1.add(new SimpleObject(1));
        Set v2 = Collections.synchronizedSet(new LinkedHashSet());
        v2.add(new SimpleObject(2));
        (new MTSetDeadlock(v1,v2)).start();
        (new MTSetDeadlock(v2,v1)).start();
    }

    public static void treeSetDeadlock() {
        Set v1 = Collections.synchronizedSet(new TreeSet());
        v1.add(new SimpleObject(1));
        Set v2 = Collections.synchronizedSet(new TreeSet());
        v2.add(new SimpleObject(2));
        (new MTSetDeadlock(v1,v2)).start();
        (new MTSetDeadlock(v2,v1)).start();
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
//@jcute.optionNumberOfPaths=150
//@jcute.optionLogLevel=2
//@jcute.optionLogStatistics=true
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
