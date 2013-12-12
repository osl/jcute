package cute.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Oct 31, 2005
 * Time: 8:44:53 AM
 */
public class OptionsGui extends OptionsView implements ActionListener{
    /**
     *
     */
    private static final long serialVersionUID = -5564604647555843498L;
    private JCuteGui gui;
    private JFileChooser chooser1;

    public OptionsGui(JCuteGui gui){
        super(gui,"Options",true);
        this.gui = gui;
        setResizable(false);

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        generateJUnit.addActionListener(this);
        junitFolderB.addActionListener(this);
        randomSearch.addActionListener(this);
        dfsSearch.addActionListener(this);
        quickSearch.addActionListener(this);
        logPath.addActionListener(this);
        chooser1 = new JFileChooser(System.getProperty("user.dir"));
        chooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser1.setDialogTitle("Choose JUnit Test Case Output Folder");
        onlyErrors.addActionListener(this);
        allTraces.addActionListener(this);
        branchCoverageRelevantTraces.addActionListener(this);
        tenthTrace.addActionListener(this);
        synchronizeGuiWithData();
    }

    public void synchronizeGuiWithData(){
        otherOptions.setText(gui.tui.getOptionExtraOptions());
        runs.setText(gui.tui.getOptionNumberOfPaths() +"");
        allTraces.setSelected(gui.tui.getOptionLogLevel() ==JCuteTextUI.LOG_ALL);
        onlyErrors.setSelected(gui.tui.getOptionLogLevel() == JCuteTextUI.LOG_ERRORS);
        branchCoverageRelevantTraces.setSelected(gui.tui.getOptionLogLevel() == JCuteTextUI.LOG_BRANCHES);
        tenthTrace.setSelected(gui.tui.getOptionLogLevel() ==JCuteTextUI.LOG_TENTH);

        logAssertion.setSelected(gui.tui.isOptionLogAssertion());
        logException.setSelected(gui.tui.isOptionLogException());
        logDeadlock.setSelected(gui.tui.isOptionLogDeadlock());
        logRace.setSelected(gui.tui.isOptionLogRace());
        logAssertion.setEnabled(gui.tui.getOptionLogLevel()==JCuteTextUI.LOG_ERRORS);
        logException.setEnabled(gui.tui.getOptionLogLevel()==JCuteTextUI.LOG_ERRORS);
        logDeadlock.setEnabled(gui.tui.getOptionLogLevel()==JCuteTextUI.LOG_ERRORS);
        logRace.setEnabled(gui.tui.getOptionLogLevel()==JCuteTextUI.LOG_ERRORS);

//        logStats.setSelected(gui.tui.isOptionLogStatistics());
//        logStats.setEnabled(false);
        useRandomInputs.setSelected(gui.tui.isOptionUseRandomInputs());
        concurrent.setSelected(!gui.tui.isOptionSequential());
        printOutput.setSelected(gui.tui.isOptionPrintOutput());
        depth.setText(gui.tui.getOptionDepthForDFS() +"");
        threshold.setText(gui.tui.getOptionQuickSearchThreshold() +"");
        randomSearch.setSelected((gui.tui.getOptionSearchStrategy())==JCuteTextUI.SEARCH_RANDOM);
        dfsSearch.setSelected(gui.tui.getOptionSearchStrategy() ==JCuteTextUI.SEARCH_DFS);
        quickSearch.setSelected(gui.tui.getOptionSearchStrategy() ==JCuteTextUI.SEARCH_QUICK);
        generateJUnit.setSelected(gui.tui.isOptionGenerateJUnit());
        logPath.setSelected(gui.tui.isOptionLogPath());
        logInputAndTrace.setSelected(gui.tui.isOptionLogPath() && gui.tui.isOptionLogTraceAndInput());
        logInputAndTrace.setEnabled(gui.tui.isOptionLogPath());
        junitFolderT.setEnabled(generateJUnit.isSelected());
        junitFolderB.setEnabled(generateJUnit.isSelected());
        junitPkgT.setEnabled(generateJUnit.isSelected());
        junitFolderT.setText(gui.tui.getOptionJUnitOutputFolderName());
        junitPkgT.setText(gui.tui.getOptionJUnitPkgName());
        depth.setEnabled(dfsSearch.isSelected());
        threshold.setEnabled(quickSearch.isSelected());
        gui.tui.updateSoftCompleted();
    }

    public void synchronizeDataWithGui(){
        gui.tui.setOptionExtraOptions(otherOptions.getText().trim());
        gui.tui.setOptionSequential(!concurrent.isSelected());
//        gui.tui.setOptionLogStatistics(logStats.isSelected());
        gui.tui.setOptionUseRandomInputs(useRandomInputs.isSelected());
        gui.tui.setOptionPrintOutput(printOutput.isSelected());
        gui.tui.setOptionGenerateJUnit(generateJUnit.isSelected());
        gui.tui.setOptionLogPath(logPath.isSelected());
        gui.tui.setOptionLogTraceAndInput(logPath.isSelected() && logInputAndTrace.isSelected());

        if(branchCoverageRelevantTraces.isSelected()){
            gui.tui.setOptionLogLevel(JCuteTextUI.LOG_BRANCHES);
        }
        if(allTraces.isSelected()){
            gui.tui.setOptionLogLevel(JCuteTextUI.LOG_ALL);
        }
        if(onlyErrors.isSelected()) {
            gui.tui.setOptionLogLevel(JCuteTextUI.LOG_ERRORS);
        }
        if(tenthTrace.isSelected()){
            gui.tui.setOptionLogLevel(JCuteTextUI.LOG_TENTH);
        }

        gui.tui.setOptionLogAssertion(logAssertion.isSelected());
        gui.tui.setOptionLogException(logException.isSelected());
        gui.tui.setOptionLogDeadlock(logDeadlock.isSelected());
        gui.tui.setOptionLogRace(logRace.isSelected());

        int tmp;
        tmp = gui.tui.getOptionNumberOfPaths();
        try {
            gui.tui.setOptionNumberOfPaths(Integer.parseInt(runs.getText().trim()));
        } catch(NumberFormatException nfe){
            gui.box.ask("Inputs field must be a number greater than 0");
        }
        if(gui.tui.getOptionNumberOfPaths() <=0){
            gui.box.ask("Inputs field must be a number greater than 0");
            gui.tui.setOptionNumberOfPaths(tmp);
        }

        tmp = gui.tui.getOptionDepthForDFS();
        try {
            gui.tui.setOptionDepthForDFS(Integer.parseInt(depth.getText().trim()));
        } catch(NumberFormatException nfe){
            gui.box.ask("Depth field must be a number greater than or equal to 0");
        }
        if(gui.tui.getOptionDepthForDFS() <0){
            gui.tui.setOptionDepthForDFS(tmp);
        }

        tmp = gui.tui.getOptionQuickSearchThreshold();
        try {
            gui.tui.setOptionQuickSearchThreshold(Integer.parseInt(threshold.getText().trim()));
        } catch(NumberFormatException nfe){
            gui.box.ask("Threshold field must be a number greater than 0");
        }
        if(gui.tui.getOptionQuickSearchThreshold() <=0){
            gui.tui.setOptionQuickSearchThreshold(tmp);
        }

        if(dfsSearch.isSelected()){
            gui.tui.setOptionSearchStrategy(JCuteTextUI.SEARCH_DFS);
        }
        if(randomSearch.isSelected()){
            gui.tui.setOptionSearchStrategy(JCuteTextUI.SEARCH_RANDOM);
        }
        if(quickSearch.isSelected()){
            gui.tui.setOptionSearchStrategy(JCuteTextUI.SEARCH_QUICK);
        }
        gui.tui.setOptionJUnitOutputFolderName(junitFolderT.getText());
        gui.tui.setOptionJUnitPkgName(junitPkgT.getText());
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==okButton){
            synchronizeDataWithGui();
            synchronizeGuiWithData();
            setVisible(false);
        } else if(e.getSource() == dfsSearch
                || e.getSource() == randomSearch
                || e.getSource() == quickSearch){
            depth.setEnabled(dfsSearch.isSelected());
            threshold.setEnabled(quickSearch.isSelected());
        } else if(e.getSource()==generateJUnit){
            junitFolderT.setEnabled(generateJUnit.isSelected());
            junitFolderB.setEnabled(generateJUnit.isSelected());
            junitPkgT.setEnabled(generateJUnit.isSelected());
            if(generateJUnit.isSelected()){
                concurrent.setSelected(false);
            }
        } else if(e.getSource()==logPath){
            logInputAndTrace.setEnabled(logPath.isSelected());
            if(!logPath.isSelected())
                logInputAndTrace.setSelected(false);
        } else if(e.getSource()==onlyErrors || e.getSource() == branchCoverageRelevantTraces
                || e.getSource() == tenthTrace || e.getSource() == allTraces){
            logAssertion.setEnabled(onlyErrors.isSelected());
            logException.setEnabled(onlyErrors.isSelected());
            logDeadlock.setEnabled(onlyErrors.isSelected());
            logRace.setEnabled(onlyErrors.isSelected());
        } else if(e.getSource()==cancelButton){
            synchronizeGuiWithData();
            setVisible(false);
        } else if(e.getSource()==junitFolderB){
            int returnVal = chooser1.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                junitFolderT.setText(chooser1.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
