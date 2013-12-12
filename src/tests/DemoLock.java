package tests;

import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
class Cell {
    int x;

    synchronized public int getX() {
        return x;
    }

    synchronized public void setX(int x) {
        this.x = x;
    }
}

public class DemoLock extends Thread {
    Cell c;

    public DemoLock(Cell c) {
        this.c = c;
    }

    public void f(int y){
        DemoLock d = new DemoLock(c);
        d.start();
        c.setX(1);
        if(c.getX()==2*y){
            System.out.println("ERROR");
        }
    }

    public void run() {
        c.setX(2);
    }

    public static void main(String[] args) {
        int y;
        System.out.println("DemoLock");
        y = Cute.input.Integer();
        DemoLock d = new DemoLock(new Cell());
        d.f(y);
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
