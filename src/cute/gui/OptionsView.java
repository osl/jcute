package cute.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 2, 2005
 * Time: 9:03:35 PM
 */
public class OptionsView extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7772223625721087435L;
	ButtonGroup logTraces = new ButtonGroup();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    JPanel flow2 = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));
    JCheckBox concurrent = new JCheckBox();
//    JCheckBox logStats = new JCheckBox();
    JCheckBox useRandomInputs = new JCheckBox();
    JCheckBox printOutput = new JCheckBox();
    JCheckBox generateJUnit = new JCheckBox();
    JCheckBox logPath = new JCheckBox();
    JCheckBox logInputAndTrace = new JCheckBox();

    JPanel junitFolderFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));
    JLabel junitFolderLabel = new JLabel("      JUnit Test Output Folder");
    JTextField junitFolderT = new JTextField("",10);
    JButton junitFolderB = new JButton();

    JPanel junitPkgFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));
    JLabel junitPkgLabel = new JLabel("      JUnit Test Package Name");
    JTextField junitPkgT = new JTextField("",18);

    JLabel jLabel19 = new JLabel();
    JTextField runs = new JTextField("",6);
    JPanel flow24 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
    JPanel gridGeneral = new JPanel(new GridLayout(4,1,5,5));
    JLabel jLabel10 = new JLabel();
    JTextField otherOptions = new JTextField("",20);
    JPanel flowOther = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
    JRadioButton onlyErrors = new JRadioButton();
    JRadioButton allTraces = new JRadioButton();
    JRadioButton tenthTrace = new JRadioButton();
    JRadioButton branchCoverageRelevantTraces = new JRadioButton();
    JPanel gridLogWhat = new JPanel(new GridBagLayout());
    JPanel gridbag8 = new JPanel(new GridBagLayout());
    JPanel border1 = new JPanel(new BorderLayout(5,5));
    Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    JPanel gridSearchType = new JPanel(new GridLayout(5,1,5,5));
    JPanel gridJunit = new JPanel(new GridBagLayout());

    JRadioButton randomSearch = new JRadioButton();
    JRadioButton dfsSearch = new JRadioButton();
    JRadioButton quickSearch = new JRadioButton();
    ButtonGroup searchGroup = new ButtonGroup();
    JLabel depthLabel = new JLabel("    Depth of DFS");
    JTextField depth = new JTextField("0",5);
    JPanel depthPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));
    JLabel thresholdLabel = new JLabel("    Threshold of Quick Search");
    JTextField threshold = new JTextField("0",5);
    JPanel thresholdPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));

    JCheckBox logAssertion = new JCheckBox();
    JCheckBox logException = new JCheckBox();
    JCheckBox logDeadlock = new JCheckBox();
    JCheckBox logRace = new JCheckBox();

    public OptionsView(Frame _parent_,String title,boolean modal) {
        super(_parent_,title,modal);
        this.setResizable(true);
        
        okButton.setText("Ok");
        cancelButton.setText("Cancel");
        flow2.add(okButton);
        flow2.add(cancelButton);

        concurrent.setText("Concurrent (Instrumentation Option)");
//        logStats.setText("Log Branch Coverage");
        jLabel19.setText("Number of Paths to be Covered");
        printOutput.setText("Print Output");
        useRandomInputs.setText("Initially Use Random Inputs");

        generateJUnit.setText("Generate JUnit Test Cases");
        logPath.setText("Log Paths for Replay");
        logInputAndTrace.setText("Log Inputs and Traces");

        junitFolderB.setIcon(GetImageIcon.getIcon("images/Dir.gif"));
        junitFolderFlow.add(junitFolderLabel);
        junitFolderFlow.add(junitFolderT);
        junitFolderFlow.add(junitFolderB);

        junitPkgFlow.add(junitPkgLabel);
        junitPkgFlow.add(junitPkgT);


        flow24.add(jLabel19);
        flow24.add(runs);

        gridGeneral.add(concurrent);
//        gridGeneral.add(logStats);
        gridGeneral.add(flow24);
        gridGeneral.add(printOutput);
        gridGeneral.add(useRandomInputs);

        gridJunit.add(logPath,new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridJunit.add(logInputAndTrace,new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridJunit.add(generateJUnit,new GridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridJunit.add(junitFolderFlow,new GridBagConstraints(0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridJunit.add(junitPkgFlow,new GridBagConstraints(0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridJunit.add(gridLogWhat,new GridBagConstraints(1,0,1,8,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(0,2,2,2),0,0));

        TitledBorder tb1 = null;
        tb1 = BorderFactory.createTitledBorder(loweredetched, "General");
        tb1.setTitleJustification(TitledBorder.LEFT);
        gridGeneral.setBorder(tb1);


        jLabel10.setText("Other Options");
        flowOther.add(jLabel10);
        flowOther.add(otherOptions);

        onlyErrors.setText("For Error Coverage");
        logTraces.add(onlyErrors);
        allTraces.setText("For Optimal Path Coverage");
        logTraces.add(allTraces);
        branchCoverageRelevantTraces.setText("For Optimal Branch Coverage");
        logTraces.add(branchCoverageRelevantTraces);
        tenthTrace.setText("For Every 10th Path");
        logTraces.add(tenthTrace);

        logAssertion.setText("Assertion Violation");
        logException.setText("Uncaught Exception");
        logDeadlock.setText("Deadlock");
        logRace.setText("Data-Race");

        randomSearch.setText("Random Search Strategy");
        searchGroup.add(randomSearch);
        dfsSearch.setText("Depth First Search Strategy");
        searchGroup.add(dfsSearch);
        dfsSearch.setSelected(true);
        quickSearch.setText("Quick Search Strategy");
        searchGroup.add(quickSearch);

        tb1 = BorderFactory.createTitledBorder(loweredetched,
                "Log");
        tb1.setTitleJustification(TitledBorder.LEFT);
        gridLogWhat.setBorder(tb1);

        tb1 = BorderFactory.createTitledBorder(loweredetched, "Search Strategy");
        tb1.setTitleJustification(TitledBorder.LEFT);
        gridSearchType.setBorder(tb1);

        tb1 = BorderFactory.createTitledBorder(loweredetched,
                "JUnit Test, Trace, and Input Log Generation");
        tb1.setTitleJustification(TitledBorder.LEFT);
        gridJunit.setBorder(tb1);

        gridLogWhat.add(onlyErrors,new GridBagConstraints(0,0,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridLogWhat.add(logAssertion,new GridBagConstraints(0,1,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,24,2,2),8,0));
        gridLogWhat.add(logException,new GridBagConstraints(0,2,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,24,2,2),8,0));
        gridLogWhat.add(logDeadlock,new GridBagConstraints(0,3,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,24,2,2),8,0));
        gridLogWhat.add(logRace,new GridBagConstraints(0,4,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,24,2,2),8,0));
        gridLogWhat.add(allTraces,new GridBagConstraints(0,5,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridLogWhat.add(branchCoverageRelevantTraces,new GridBagConstraints(0,6,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));
        gridLogWhat.add(tenthTrace,new GridBagConstraints(0,7,1,1,0.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,2,2),0,0));

        gridSearchType.add(randomSearch);
        gridSearchType.add(dfsSearch);
        gridSearchType.add(depthPanel);
        gridSearchType.add(quickSearch);
        gridSearchType.add(thresholdPanel);

        depthPanel.add(depthLabel);
        depthPanel.add(depth);

        thresholdPanel.add(thresholdLabel);
        thresholdPanel.add(threshold);

        gridbag8.add(gridGeneral,
                new GBConstraints(0,0,1,1,0.5,1.0,10,GridBagConstraints.BOTH,0,0,
                        new Insets(0,0,0,0)));
        gridbag8.add(gridSearchType,
                new GBConstraints(1,0,1,1,0.5,0.0,10,GridBagConstraints.BOTH,0,0,
                        new Insets(0,0,0,0)));
        gridbag8.add(gridJunit,
                new GBConstraints(0,2,2,1,0.0,0.0,10,2,0,0,new Insets(0,0,0,0)));
        gridbag8.add(flowOther,
                new GBConstraints(0,4,2,1,0.0,0.0,10,2,0,0,new Insets(0,0,0,0)));
//        gridbag8.add(gridLogWhat,
//                new GBConstraints(0,3,2,1,0.5,0.0,10,GridBagConstraints.HORIZONTAL,0,0,
//                        new Insets(0,0,0,0)));

        border1.add("South",flow2);
        border1.add("Center",gridbag8);
        getContentPane().add("Center",border1);

        setSize(getPreferredSize());
//        setSize(200,200);
        pack();
    }


    class GBConstraints extends GridBagConstraints {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7853127912750696821L;

		public GBConstraints(
            int gridx,
            int gridy,
            int gridwidth,
            int gridheight,
            double weightx,
            double weighty,
            int anchor,
            int fill,
            int ipadx,
            int ipady,
            Insets insets)
        {
            super();
            this.gridx	= gridx;
            this.gridy	= gridy;
            this.gridwidth	= gridwidth;
            this.gridheight	= gridheight;
            this.weightx	= weightx;
            this.weighty	= weighty;
            this.anchor	= anchor;
            this.fill	= fill;
            this.ipadx	= ipadx;
            this.ipady	= ipady;
            this.insets	= insets;
        }
    }
}
