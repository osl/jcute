package cute.gui;

import cute.concolic.logging.BranchCoverageLog;

import java.util.HashMap;
import java.util.Iterator;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 4, 2005
 * Time: 11:09:55 PM
 */
public class CoverageGui extends CoverageView {

    /**
     *
     */
    private static final long serialVersionUID = -3224933131178645582L;

    public CoverageGui() {
        super();
    }


    public void fillGuiWithData(BranchCoverageLog bc,JCuteTextUI tui) {
        if(bc==null) return;
        int total=0;
        int sum = 0;
        lm.clear();
        HashMap functionBranchCoverage = bc.getFunctionBranchCoverage();
        for (Iterator iterator = functionBranchCoverage.keySet().iterator(); iterator.hasNext();) {
            String fname = (String) iterator.next();
            int[] branches = (int []) functionBranchCoverage.get(fname);
            total += (2*branches.length);
            int localSum = 0;
            for (int i = 0; i < branches.length; i++) {
                int branch = branches[i];
                if ((branch & 1)!=0) { sum++; localSum++;}
                if ((branch & 2)!=0) { sum++; localSum++;}
            }
            lm.addElement(localSum+" branches covered out of "+(branches.length*2)+" branches in the function "+fname);
        }
        double percentage = ((int)((10000.0*sum)/total))/100.0;
        l2.setText(String.valueOf(bc.functionBranchCoverage.size()));
        l3.setText(String.valueOf(sum));
        l4.setText(String.valueOf(percentage)+"%");
        l5.setText(String.valueOf(bc.getIterations()));
        l6.setText(String.valueOf(tui.getRunningTime()));

        countExceptions.setText(String.valueOf(tui.getCountExceptions()));
        countRaces.setText(String.valueOf(tui.getCountRaceFields()));
        races.setText(String.valueOf(tui.getErrorCountRace()));
        deadlocks.setText(String.valueOf(tui.getErrorCountDeadlock()));
        assertions.setText(String.valueOf(tui.getErrorCountAssertion()));
        exceptions.setText(String.valueOf(tui.getErrorCountException()));
        this.total.setText(String.valueOf(tui.getErrorCountTotal()));
    }


}
