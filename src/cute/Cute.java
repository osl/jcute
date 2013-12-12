package cute;

import cute.concolic.Globals;
import cute.concolic.input.InputImpl;
import cute.gui.JCuteGui;
import junit.framework.TestCase;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

//@todo array inputs
//@todo handling unknown (library) functions with concurrency primitive

public class Cute {
    public static int N;
    public static final int EXIT_COMPLETE = 2;
    public static final int EXIT_ASSERT_FAILED = 32;
    public static final int EXIT_ERROR = 8;
    public static final int EXIT_RACE = 4;
    public static final int EXIT_DEADLOCK = 16;

    public static Input input = new InputImpl();

    public static void main(String[] args) {
        JCuteGui.main(args);
    }

    public static void Assume(boolean b){
        if(!b) {
            if(Globals.globals.initialized){
                if(Globals.globals.information.brackTrackAt<0)
                    Globals.globals.information.brackTrackAt = Globals.globals.path.size()-1;
                Thread.currentThread().stop();
            } else {
                System.exit(0);
            }
        }
    }

    public static void Assert(boolean b){
        if(!b) {
            if(Globals.globals.initialized){
                System.err.println("Assertion failed");
                Thread.dumpStack();
                System.err.println("Exiting with error");
                Globals.globals.information.returnVal = Cute.EXIT_ASSERT_FAILED+Globals.globals.information.returnVal;
                Globals.globals.solver.predict();
            } else {
                TestCase.assertTrue(b);
            }
        }
    }


}
