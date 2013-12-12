package tests;

import cute.Cute;

import java.util.Set;
import java.util.HashSet;


/**
 * Used by JPF
 */
class List {
    Node header;

    public List() {
        header = (Node)Cute.input.Object("tests.Node");
        int i=0;
        Node t = header;
        while(t!=null){
            i++;
            t = t.next;
        }
        System.out.println("i = " + i);
    }
//@ precondition: acyclic();
    void distributedSort() {
        if (header == null) return;
        if (header.next == null) return;
        int i = 0;
        Node t = header;
        while (t.next != null) {
            new Swapper(t, ++i).start();
            t = t.next;
        }
    }

    boolean acyclic() {
        Set visited = new HashSet();
        Node current = header;
        while (current != null) {
            if (!visited.add(current))
                return false;
            current = current.next;
        }
        return true;
    }


}
class Swapper extends java.lang.Thread {
//can swap current.elem,current.next.elem
    Node current;
    int maxSwaps;

    Swapper(Node m, int n) {
        current = m; maxSwaps = n;
    }

    public void run() {
        int swapCount = 0;
        for (int i = 0; i < maxSwaps; i++)
            if (current.swapElem()) swapCount++;
        if(swapCount==maxSwaps)
            Cute.Assert(current.inOrder());
//@ assert: if (swapCount == maxSwaps)
//@ current.inOrder();
    }
}

public class DSort {
    public static void main(String[] args) {
        List l = new List();
        l.distributedSort();
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=false
//@jcute.optionLogTraceAndInput=false
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=D:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=20
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
