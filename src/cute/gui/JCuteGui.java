package cute.gui;

import ccl.util.FileUtil;
import cute.concolic.Globals;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Oct 29, 2005
 * Time: 6:46:56 PM
 */
public class JCuteGui extends JCuteView implements ActionListener,
        WindowListener, ListSelectionListener, GuiControllerMask, ChangeListener {
    /**
     *
     */
    private static final long serialVersionUID = 9207283570419444196L;

    private JFileChooser chooser1;
    private JFileChooser chooser2;
    private OptionsGui optionDialog;
    private DefaultListModel runNumberListModel;
    private DefaultListModel inputListModel;
    private DefaultListModel traceListModel;
    private DefaultComboBoxModel funModel;
    private String loadedFile = null;

    MessageBox box;
    private String selectedRun = null;

    private boolean tmp_browseMainJavaFileButton;
    private boolean tmp_browseSrcDirButton;
    private boolean tmp_compileButton;
    private boolean tmp_startButton;
    private boolean tmp_continueButton;
    private boolean tmp_optionsButton;
    private boolean tmp_replayButton;
    private boolean tmp_cancelButton;
    private boolean tmp_pauseButton;

    public Process proc;
    private SplashWindow splashScreen;
    public JCuteTextUI tui;
    private OptionReader optionReader;

    public JCuteGui(){
        super();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(this);

        chooser1 = new JFileChooser(System.getProperty("user.dir"));
        chooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser1.setDialogTitle("Choose Source Directory");
        chooser2 = new JFileChooser(System.getProperty("user.dir"));
        chooser2.setFileFilter(new JCuteFileFilter());
        chooser2.setDialogTitle("Choose Main Java File");
        box = new MessageBox(this);

        browseSrcDirButton.addActionListener(this);
        browseSrcDirButton.setToolTipText("Select the root directory of the Java source");
        browseMainJavaFileButton.addActionListener(this);
        browseMainJavaFileButton.setToolTipText("Select the Java file " +
                "containing the main class");
        compileButton.addActionListener(this);
        compileButton.setToolTipText("Compile and instrument the Java program for testing");
        startButton.addActionListener(this);
        startButton.setToolTipText("Start or Restart testing from the begining");
        continueButton.addActionListener(this);
        continueButton.setToolTipText("Continue testing after the last input");
        replayButton.addActionListener(this);
        replayButton.setToolTipText("Re-execute on the input selected");
        optionsButton.addActionListener(this);
        optionsButton.setToolTipText("Change testing options");
        pauseButton.addActionListener(this);
        pauseButton.setToolTipText("Pause testing");
        cancelButton.addActionListener(this);
        cancelButton.setToolTipText("Kill the current execution");
        aboutButton.addActionListener(this);
        aboutButton.setToolTipText("Show Information About Cute for Java");
        helpButton.addActionListener(this);
        helpButton.setToolTipText("Show Flash Demo on how to use CUTE for Java");
        quitButton.addActionListener(this);
        quitButton.setToolTipText("Remove all temporary files and exit CUTE for Java");
        junitButton.addActionListener(this);
        junitButton.setToolTipText("View Generated JUnit Tests");
        runNumberListModel = new DefaultListModel();
        runNumber.setModel(runNumberListModel);
        runNumber.setCellRenderer(new ColorAndTooltipListRenderer());
        ToolTipManager.sharedInstance().registerComponent(runNumber);
        runNumber.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        runNumber.addListSelectionListener(this);
        funModel = new DefaultComboBoxModel();
        funT.setModel(funModel);
        funT.setLightWeightPopupEnabled(true);

        inputListModel = new DefaultListModel();
        input.setModel(inputListModel);
        input.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        input.addListSelectionListener(this);
        input.setCellRenderer(new ColorAndTooltipListRenderer());
        ToolTipManager.sharedInstance().registerComponent(input);

        traceListModel = new DefaultListModel();
        trace.setModel(traceListModel);
        trace.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trace.addListSelectionListener(this);
        trace.setCellRenderer(new ColorAndTooltipListRenderer());
        ToolTipManager.sharedInstance().registerComponent(trace);

        EditorKit editorKit = new StyledEditorKit()
        {
            /**
             *
             */
            private static final long serialVersionUID = 1282507614362283319L;

            public Document createDefaultDocument()
            {
                return new SyntaxDocument();
            }
        };

        source.setEditorKitForContentType("text/java", editorKit);
        source.setContentType("text/java");
        source.setAutoscrolls(true);

        tabbedComponent.addChangeListener(this);

        tui = new JCuteTextUI(output,progressComponent,box,this,true);
        optionReader = new OptionReader(tui);
        optionDialog = new OptionsGui(this);
        enableInit();
    }

    void disableALL(){
        tmp_browseMainJavaFileButton = browseMainJavaFileButton.isEnabled();
        browseMainJavaFileButton.setEnabled(false);
        tmp_browseSrcDirButton = browseSrcDirButton.isEnabled();
        browseSrcDirButton.setEnabled(false);
        tmp_compileButton = compileButton.isEnabled();
        compileButton.setEnabled(false);
        tmp_startButton = startButton.isEnabled();
        startButton.setEnabled(false);
        tmp_continueButton = continueButton.isEnabled();
        continueButton.setEnabled(false);
        tmp_optionsButton = optionsButton.isEnabled();
        optionsButton.setEnabled(false);
        tmp_replayButton = replayButton.isEnabled();
        replayButton.setEnabled(false);
        tmp_cancelButton = cancelButton.isEnabled();
        cancelButton.setEnabled(false);
        tmp_pauseButton = pauseButton.isEnabled();
        pauseButton.setEnabled(false);
    }

    public void enablePreviousState(){
        browseMainJavaFileButton.setEnabled(tmp_browseMainJavaFileButton);
        browseSrcDirButton.setEnabled(tmp_browseSrcDirButton);
        compileButton.setEnabled(tmp_compileButton);
        startButton.setEnabled(tmp_startButton);
        continueButton.setEnabled(tmp_continueButton);
        optionsButton.setEnabled(tmp_optionsButton);
        replayButton.setEnabled(tmp_replayButton);
        cancelButton.setEnabled(tmp_cancelButton);
        pauseButton.setEnabled(tmp_pauseButton);
        runNumber.setEnabled(true);
    }

    void enableInit(){
        browseMainJavaFileButton.setEnabled(true);
        browseSrcDirButton.setEnabled(true);
        compileButton.setEnabled(funModel.getSize()>0);
        startButton.setEnabled(false);
        continueButton.setEnabled(false);
        optionsButton.setEnabled(true);
        replayButton.setEnabled(false);
        quitButton.setEnabled(true);
        cancelButton.setEnabled(false);
        pauseButton.setEnabled(false);
        runNumber.setEnabled(true);
        runNumberListModel.clear();
        inputListModel.clear();
        traceListModel.clear();
        source.setText("");
        loadedFile = null;
        sourceName.setText("Source: ");
        selectedRun = null;
    }


    public void enableCompileOnly(){
        browseMainJavaFileButton.setEnabled(true);
        browseSrcDirButton.setEnabled(true);
        compileButton.setEnabled(funModel.getSize()>0);
        startButton.setEnabled(false);
        continueButton.setEnabled(false);
        optionsButton.setEnabled(true);
        replayButton.setEnabled(false);
        quitButton.setEnabled(true);
        cancelButton.setEnabled(false);
        pauseButton.setEnabled(false);
        runNumber.setEnabled(true);
        runNumberListModel.clear();
        inputListModel.clear();
        traceListModel.clear();

        source.setText("");
        loadedFile = null;
        sourceName.setText("Source: ");

        selectedRun = null;
    }

    public void enablePauseAndCancel() {
        browseMainJavaFileButton.setEnabled(false);
        browseSrcDirButton.setEnabled(false);
        compileButton.setEnabled(false);
        startButton.setEnabled(false);
        continueButton.setEnabled(false);
        optionsButton.setEnabled(false);
        replayButton.setEnabled(false);
        quitButton.setEnabled(true);
        cancelButton.setEnabled(true);
        pauseButton.setEnabled(true);
        runNumber.setEnabled(false);
    }

    public void enableStart(){
        browseMainJavaFileButton.setEnabled(true);
        browseSrcDirButton.setEnabled(true);
        compileButton.setEnabled(funModel.getSize()>0);
//        compileButton.setEnabled(true);
        startButton.setEnabled(true);
        continueButton.setEnabled(false);
        optionsButton.setEnabled(true);
        replayButton.setEnabled(selectedRun!=null);
        quitButton.setEnabled(true);
        cancelButton.setEnabled(false);
        pauseButton.setEnabled(false);
        runNumber.setEnabled(true);
    }

    public void enableStartAndContinue(){
        browseMainJavaFileButton.setEnabled(true);
        browseSrcDirButton.setEnabled(true);
        compileButton.setEnabled(funModel.getSize()>0);
        startButton.setEnabled(true);
        continueButton.setEnabled(true);
        optionsButton.setEnabled(true);
        replayButton.setEnabled(selectedRun!=null);
        quitButton.setEnabled(true);
        cancelButton.setEnabled(false);
        pauseButton.setEnabled(false);
        runNumber.setEnabled(true);
    }

    public void browseSrcDirAction(){
        int returnVal = chooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            output.setText("");
            tui.setSrcDirName(chooser1.getSelectedFile().getAbsolutePath());
            srcDir.setText(String.valueOf(tui.getSrcDirName()));
            chooser2.setCurrentDirectory(new File(tui.getSrcDirName()));
            Utils.populateTestableFunctions(srcDir.getText(),mainJavaFile.getText(),
                    funModel,funT);
        }
        enablePreviousState();
        compileButton.setEnabled(funModel.getSize()>0);
        if(funModel.getSize()>0){
            tui.setCompilableExt();
            optionReader.readOptions();
            loadFile(tui.getSrcFileName(),2);
        }
    }

    public void browseMainFileAction(){
        int returnVal = chooser2.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            output.setText("");
            tui.setSrcFileName(chooser2.getSelectedFile().getAbsolutePath());
            mainJavaFile.setText(String.valueOf(tui.getSrcFileName()));
            Utils.populateTestableFunctions(srcDir.getText(),mainJavaFile.getText(),
                    funModel,funT);
        }
        enablePreviousState();
        compileButton.setEnabled(funModel.getSize()>0);
        if(funModel.getSize()>0){
            tui.setCompilableExt();
            optionReader.readOptions();
            loadFile(tui.getSrcFileName(),2);
        }
    }

    public void compileAction(){
        runNumberListModel.clear();
        inputListModel.clear();
        traceListModel.clear();
        source.setText("");
        loadedFile = null;
        sourceName.setText("Source: ");
        selectedRun = null;

        tui.setMainClassNamePlusFun((String)funT.getSelectedItem());
        tui.compileAction();
        if(tui.isCompiled()){
            enableStart();
        } else {
            enableCompileOnly();
        }
    }

    public void continueAction(){
        enablePauseAndCancel();
        tui.continueAction(paramsT.getText());
        if(tui.isCompleted() || tui.isSoftCompleted())
            enableStart();
        else if(tui.isTestStarted())
            enableStartAndContinue();
        else
            enableStart();
        tabbedComponent.setSelectedComponent(executionLogComponent);
        loadTraceAndInput();
    }

    public void replayAction() {
        tui.replayAction(selectedRun,paramsT.getText());
        loadTraceAndInput();
        enablePreviousState();
    }

    public void deleteAction() {
        tui.deleteAction();

        runNumberListModel.clear();
        inputListModel.clear();
        traceListModel.clear();
        source.setText("");
        loadedFile = null;
        sourceName.setText("Source: ");
        selectedRun = null;
        optionReader.writeOptions();
        replayButton.setEnabled(false);
    }

    public void quitAction(){
        tui.quitAction();
        _getFrame(this).dispose();
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == browseSrcDirButton) {
            disableALL();
            (new ExecutionTask(this,"dir")).start();
        }
        if(e.getSource() == browseMainJavaFileButton) {
            disableALL();
            (new ExecutionTask(this,"file")).start();
        }
        if(e.getSource() == compileButton){
            disableALL();
            tabbedComponent.setSelectedComponent(outputComponent);
            (new ExecutionTask(this,"compile")).start();
        }
        if(e.getSource() == continueButton){
            disableALL();
            if(tui.isOptionPrintOutput())
                tabbedComponent.setSelectedComponent(outputComponent);
            else
                tabbedComponent.setSelectedComponent(executionLogComponent);
            (new ExecutionTask(this,"continue")).start();
        }
        if(e.getSource() == startButton){
            disableALL();
            if(tui.isOptionPrintOutput())
                tabbedComponent.setSelectedComponent(outputComponent);
            else
                tabbedComponent.setSelectedComponent(executionLogComponent);
            (new ExecutionTask(this,"start")).start();
        }
        if(e.getSource() == pauseButton){
            tabbedComponent.setSelectedComponent(executionLogComponent);
            tui.pauseAction();
            pauseButton.setEnabled(false);
        }
        if(e.getSource() == replayButton){
            disableALL();
            tabbedComponent.setSelectedComponent(outputComponent);
            (new ExecutionTask(this,"replay")).start();
        }
        if (e.getSource() == quitButton) {
            disableALL();
            (new ExecutionTask(this,"quit")).start();
        }
        if (e.getSource() == junitButton) {
            String s = tui.getJUnitFileName();
            if(s!=null){
                tabbedComponent.setSelectedComponent(executionLogComponent);
                loadFile(s,3);
            }
            else
                box.ask("JUnit Tests not yet generated");
        }
        if(e.getSource()==cancelButton){
            int answer = JOptionPane.showConfirmDialog(this,
                    "This can have adverse side-effect\n" +
                    "on this testing session. If your\n" +
                    "program is not responding, then use\n" +
                    "this option. Otherwise, use the pause button.\n" +
                    "Do you really want to stop?","Warning",
                    JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(answer==JOptionPane.YES_OPTION){
                tabbedComponent.setSelectedComponent(executionLogComponent);
                tui.cancelAction();
            }
        }
        if(e.getSource() == optionsButton){
            optionDialog.synchronizeGuiWithData();
            optionDialog.setVisible(true);
            if(tui.isTestStarted() && !tui.isSoftCompleted() && !tui.isCompleted()){
                enableStartAndContinue();
            } else if(tui.isSoftCompleted() || tui.isCompleted()){
                enableStart();
            }
        }
        if(e.getSource()==aboutButton){
            splashScreen.setVisible(true);
        }
        if(e.getSource() == helpButton){
            BareBonesBrowserLaunch.openURL("file://"+tui.getJcuteHome()+"/demo.htm",this);
        }
    }

    private void loadInput(){
        File f = new File(tui.getTmpOutput()+JCuteTextUI.fileSeparator
                +selectedRun+JCuteTextUI.fileSeparator+"cuteInputLog");
        String line = null;
        BufferedReader reader = null;

        inputListModel.clear();
        source.setText("");
        loadedFile = null;
        sourceName.setText("Source: ");
        try{
            reader = new BufferedReader(new FileReader(f));
            while((line=reader.readLine())!=null){
                inputListModel.addElement(new LogElement(line));
            }
            reader.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Input of "+selectedRun+" was not logged");
            return;
        } catch(IOException ioe){
            ioe.printStackTrace();
            return;
        }
    }

    private void loadTrace(){
        File f = new File(tui.getTmpOutput()+JCuteTextUI.fileSeparator+selectedRun
                +JCuteTextUI.fileSeparator+"cuteTraceLog");
        String line = null;
        BufferedReader reader = null;

        traceListModel.clear();
        source.setText("");
        loadedFile = null;
        sourceName.setText("Source: ");


        try{
            reader = new BufferedReader(new FileReader(f));
            while((line=reader.readLine())!=null){
                LogElement le = new LogElement(line);
                traceListModel.addElement(le);
            }
            reader.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Trace of "+selectedRun+" was not logged");
            return;
        } catch(IOException ioe){
            ioe.printStackTrace();
            return;
        }
    }

    public void loadFile(String fName,int lineno){
        if(!fName.equals(loadedFile)){
            try {
                FileReader fr = new FileReader(fName);
                source.read(fr, null);
                fr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                source.setText("");
                return;
            }
            loadedFile = fName;
            sourceName.setText("Source: "+fName);
        }
        Element root = source.getDocument().getDefaultRootElement();
        Element paragraph=root.getElement(lineno-1);
        source.setCaretPosition(paragraph.getStartOffset()-1);
        source.setSelectionStart(paragraph.getStartOffset()-1);
        source.setSelectionEnd(paragraph.getEndOffset()-1);
        source.requestFocus();
    }

    public void loadTraceAndInput(){
        if (runNumber.isSelectionEmpty()) {
            selectedRun = null;
            replayButton.setEnabled(false);
        } else {
            int i = runNumber.getSelectedIndex();
            selectedRun = ((RunElement)runNumberListModel.getElementAt(i)).getName();
            if(!selectedRun.equals(JCuteTextUI.LAST_RUN_DIRECTORY)){
                selectedRun = JCuteTextUI.RUN_DIRECTORY_PREFIX+selectedRun;
            }
            replayButton.setEnabled(true);
            loadInput();
            loadTrace();
        }
    }

    public void updateRunNumberListModel(String s,int err) {
        int size= runNumberListModel.size();
        if(size>0 ){
            if(((RunElement)runNumberListModel.getElementAt(size-1)).getName().
                    equals(JCuteTextUI.LAST_RUN_DIRECTORY)){
                runNumberListModel.removeElementAt(size-1);
            }
        }
        runNumberListModel.addElement(new RunElement(s,err));
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;

        JList theList = (JList)e.getSource();
        if(theList==runNumber){
            loadTraceAndInput();
        }
        if(theList==input){
            if (!theList.isSelectionEmpty()) {
                int goTo = input.getSelectedIndex();
                LogElement le = ((LogElement)inputListModel.getElementAt(goTo));
                loadFile(tui.getSrcDirName()+JCuteTextUI.fileSeparator+le.getFName(),
                        le.getLineNo());
            }
        }
        if(theList==trace){
            if (!theList.isSelectionEmpty()) {
                int goTo = trace.getSelectedIndex();
                LogElement le = ((LogElement)traceListModel.getElementAt(goTo));
                loadFile(tui.getSrcDirName()+JCuteTextUI.fileSeparator+le.getFName(),
                        le.getLineNo());
            }
        }
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public static Frame _getFrame(Component c) {
        try {
            while (!(c instanceof Frame)) c = c.getParent();
            return (Frame) c;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void windowClosing(WindowEvent evt) {
        if (evt.getSource() == this) {
            FileUtil.deleteRecursively(tui.getTmpDir());
            _getFrame(this).dispose();
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        System.out.println("CUTE "+ Globals.version+" for Java.\n" +
                "Copyright 2006, " +
                "University of Illinois at Urbana Champaign. All rights reserved.\n");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        Box b = new Box();
        SplashWindow sw = new SplashWindow("images/cute.jpg",b,3000);
        b.gui.splashScreen = sw;
        b.gui.setSize(b.gui.getPreferredSize());
        b.gui.pack();
        b.gui.setVisible(true);
    }


    public void stateChanged(ChangeEvent e) {
        if(tabbedComponent.getSelectedComponent()==coverageComponent){
            coverageComponent.fillGuiWithData(tui.getBc(),tui);
        }
    }
}

class Box {
    public JCuteGui gui;
}