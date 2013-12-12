package dstest;

import instrumented.java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 26, 2005
 * Time: 5:29:10 PM
 */
public class MTListDeadlock extends Thread {
    List v1;
    List v2;

    public MTListDeadlock(List v1, List v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public void run() {
        v1.addAll(v2);
    }

    public static void linkedListDeadlock() {
        List v1 = Collections.synchronizedList(new LinkedList());
        v1.add(new SimpleObject(1));
        List v2 = Collections.synchronizedList(new LinkedList());
        v2.add(new SimpleObject(1));
        (new MTListDeadlock(v1,v2)).start();
        (new MTListDeadlock(v2,v1)).start();
    }

    public static void arrayListDeadlock() {
        List v1 = Collections.synchronizedList(new ArrayList());
        v1.add(new SimpleObject(1));
        List v2 = Collections.synchronizedList(new ArrayList());
        v2.add(new SimpleObject(1));
        (new MTListDeadlock(v1,v2)).start();
        (new MTListDeadlock(v2,v1)).start();
    }

    public static void vectorDeadlock() {
        List v1 = new Vector();
        v1.add(new SimpleObject(1));
        List v2 = new Vector();
        v2.add(new SimpleObject(1));
        (new MTListDeadlock(v1,v2)).start();
        (new MTListDeadlock(v2,v1)).start();
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
//@jcute.optionNumberOfPaths=10
//@jcute.optionLogLevel=1
//@jcute.optionLogStatistics=true
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
