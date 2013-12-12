package dstest;

import instrumented.java.util.Collections;
import instrumented.java.util.LinkedList;
import instrumented.java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 29, 2005
 * Time: 2:27:12 PM
 */
public class MTLinkedListInfiniteLoop extends Thread {
    List al1;
    List al2;
    int c;

    public MTLinkedListInfiniteLoop(List l1, List l2,int c) {
        this.al1 = l1;
        this.al2 = l2;
        this.c = c;
    }

    public void run() {
        switch(c){
            case 1:
                al1.clear();
                break;
            case 2:
                al1.containsAll(al2);
                break;
        }    }

    public static void main(String[] args) {
        List al1 = Collections.synchronizedList(new LinkedList());
        List al2 = Collections.synchronizedList(new LinkedList());
        al1.add(null);
        al2.add(null);
        (new MTLinkedListInfiniteLoop(al1,al2,1)).start();
        (new MTLinkedListInfiniteLoop(al2,al1,2)).start();
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
//@jcute.optionNumberOfPaths=2
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
