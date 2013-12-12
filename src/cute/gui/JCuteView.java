package cute.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 1, 2005
 * Time: 9:57:35 PM
 */
public class JCuteView extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3852581110610303863L;



    JMenuItem menuItem41 = new JMenuItem("Options ...");
    JSeparator separator54 = new JSeparator();
    JMenuItem menuItem55 = new JMenuItem("Exit");
    JMenu menu4 = new JMenu("File");
    JMenuItem menuItem43 = new JMenuItem("Compile");
    JMenuItem menuItem44 = new JMenuItem("(Re)start");
    JMenuItem menuItem45 = new JMenuItem("Continue");
    JMenuItem menuItem46 = new JMenuItem("Replay");
    JSeparator separator51 = new JSeparator();
    JMenuItem menuItem52 = new JMenuItem("Pause");
    JMenuItem menuItem53 = new JMenuItem("Cancel");
    JMenu menu5 = new JMenu("Action");
    JMenuItem menuItem49 = new JMenuItem("Help");
    JSeparator separator56 = new JSeparator();
    JMenuItem menuItem57 = new JMenuItem("About");
    JMenu menu6 = new JMenu("Help");
    JMenuBar menuBar3 = new JMenuBar();
    ProgressView progressComponent = new ProgressView();
    CoverageGui coverageComponent = new CoverageGui();
    JButton compileButton = new JButton();
    JButton startButton = new JButton();
    JButton continueButton = new JButton();
    JButton replayButton = new JButton();
    JButton pauseButton = new JButton();
    JButton cancelButton = new JButton();
    JButton optionsButton = new JButton();
    JButton quitButton = new JButton();
    JButton aboutButton = new JButton();
    JButton helpButton = new JButton();
    JButton junitButton = new JButton();

    JPanel wholeComponent = new JPanel(new GridBagLayout());
    JToolBar toolBarComponent = new JToolBar(JToolBar.HORIZONTAL);
    JLabel jLabel19 = new JLabel();
    JTextField srcDir = new JTextField("",40);
    JButton browseSrcDirButton = new JButton();
    JLabel jLabel20 = new JLabel();
    JTextField mainJavaFile = new JTextField("",40);
    JButton browseMainJavaFileButton = new JButton();
    JPanel programSelectionComponent = new JPanel(new GridBagLayout());
    JList runNumber = new TooltipJList();
    JScrollPane runNumberScrollPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel runNumberComponent = new JPanel(new GridLayout(1,1));
    JList input = new TooltipJList();
    JScrollPane inputjScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel inputComponent = new JPanel(new GridLayout(1,1));
    JSplitPane runNumberAndInputSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    TooltipJList trace = new TooltipJList();
    JScrollPane traceScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel traceComponent = new JPanel(new GridLayout(1,1));
    JLabel sourceName = new JLabel();
    JTextPane source = new JTextPane();
    JScrollPane sourceScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel sourceComponent = new JPanel(new BorderLayout(5,5));
    JSplitPane traceAndSourceSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JSplitPane logSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JTextPaneOutputLogger output = new JTextPaneOutputLogger();
    JScrollPane outputScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel executionLogComponent = new JPanel(new GridLayout(1,1));
    JLabel funL = new JLabel("Function to be Tested");
    JComboBox funT = new JComboBox();
    JLabel paramsL = new JLabel("Program parameters");
    JTextField paramsT = new JTextField();
    Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    JPanel outputComponent = new JPanel(new GridLayout(1,1));
    JTabbedPane tabbedComponent = new JTabbedPane();

    public JCuteView() {
        super("jCUTE (CUTE for JAVA): A Concolic Unit Testing Engine for Java");
        this.setResizable(true);
        //setIconImage(GetImageIcon.getImage("images/main.gif"));

        TitledBorder tb1;

        menuItem41.setMnemonic('o');
        menuItem55.setMnemonic('x');
        menu4.setMnemonic('f');
        menu4.add(menuItem41);
        menu4.add(separator54);
        menu4.add(menuItem55);
        menuItem43.setMnemonic('c');
        menuItem44.setMnemonic('s');
        menuItem45.setMnemonic('n');
        menuItem46.setMnemonic('r');
        menuItem52.setMnemonic('p');
        menuItem53.setMnemonic('l');
        menu5.setMnemonic('a');
        menu5.add(menuItem43);
        menu5.add(menuItem44);
        menu5.add(menuItem45);
        menu5.add(menuItem46);
        menu5.add(separator51);
        menu5.add(menuItem52);
        menu5.add(menuItem53);
        menuItem49.setMnemonic('l');
        menuItem57.setMnemonic('b');
        menu6.setMnemonic('h');
        menu6.add(menuItem49);
        menu6.add(separator56);
        menu6.add(menuItem57);
        menuBar3.add(menu4);
        menuBar3.add(menu5);
        menuBar3.add(menu6);
        compileButton.setIcon(GetImageIcon.getIcon("images/Compile.gif"));
        startButton.setIcon(GetImageIcon.getIcon("images/Restart.gif"));
        continueButton.setIcon(GetImageIcon.getIcon("images/Continue.gif"));
        replayButton.setIcon(GetImageIcon.getIcon("images/Replay.gif"));
        pauseButton.setIcon(GetImageIcon.getIcon("images/Pause.gif"));
        cancelButton.setIcon(GetImageIcon.getIcon("images/Cancel.gif"));
        optionsButton.setIcon(GetImageIcon.getIcon("images/Options.gif"));
        quitButton.setIcon(GetImageIcon.getIcon("images/Exit.gif"));
        aboutButton.setIcon(GetImageIcon.getIcon("images/About.gif"));
        helpButton.setIcon(GetImageIcon.getIcon("images/Help.gif"));
        junitButton.setIcon(GetImageIcon.getIcon("images/junitlogo.gif"));
        toolBarComponent.add(compileButton);
        toolBarComponent.add(startButton);
        toolBarComponent.add(continueButton);
        toolBarComponent.add(replayButton);
        toolBarComponent.add(pauseButton);
        toolBarComponent.add(cancelButton);
        toolBarComponent.add(optionsButton);
        toolBarComponent.add(junitButton);
        toolBarComponent.add(helpButton);
        toolBarComponent.add(aboutButton);
        toolBarComponent.add(quitButton);
        jLabel19.setText("Source Directory ");
        browseSrcDirButton.setIcon(GetImageIcon.getIcon("images/Dir.gif"));
        jLabel20.setText("Main Java File");
        browseMainJavaFileButton.setIcon(GetImageIcon.getIcon("images/File.gif"));

        // Java program selection
        programSelectionComponent.add(jLabel19,new GBConstraints(0,0,1,1,0.0,0.0,17,0,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(srcDir,new GBConstraints(1,0,3,1,100.0,0.0,17,2,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(browseSrcDirButton,new GBConstraints(4,0,1,1,0.0,0.0,10,0,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(jLabel20,new GBConstraints(0,1,1,1,0.0,0.0,17,0,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(mainJavaFile,new GBConstraints(1,1,3,1,100.0,0.0,17,2,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(browseMainJavaFileButton,new GBConstraints(4,1,1,1,0.0,0.0,10,0,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(funL,new GBConstraints(0,2,1,1,0.0,0.0,17,0,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(funT,new GBConstraints(1,2,1,1,100.0,0.0,17,2,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(paramsL,new GBConstraints(2,2,1,1,0.0,0.0,17,0,0,0,new Insets(0,2,2,2)));
        programSelectionComponent.add(paramsT,new GBConstraints(3,2,1,1,100.0,0.0,17,2,0,0,new Insets(0,2,2,2)));
        tb1 = BorderFactory.createTitledBorder(loweredetched, "Java Program to be Tested");
        tb1.setTitleJustification(TitledBorder.LEFT);
        programSelectionComponent.setBorder(tb1);

        runNumber.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        runNumberScrollPanel.setViewportView(runNumber);
        runNumberComponent.add(runNumberScrollPanel);
        runNumberScrollPanel.setPreferredSize(new Dimension(50,300));
        tb1 = BorderFactory.createTitledBorder(loweredetched, "Path #");
        tb1.setTitleJustification(TitledBorder.LEFT);
        runNumberComponent.setBorder(tb1);

        input.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inputjScrollPane.setViewportView(input);
        inputComponent.add(inputjScrollPane);
        inputjScrollPane.setPreferredSize(new Dimension(150,300));
        tb1 = BorderFactory.createTitledBorder(loweredetched, "Input");
        tb1.setTitleJustification(TitledBorder.LEFT);
        inputComponent.setBorder(tb1);

        runNumberAndInputSplitPane.setDividerSize(3);
        runNumberAndInputSplitPane.setLeftComponent(runNumberComponent);
        runNumberAndInputSplitPane.setRightComponent(inputComponent);

        trace.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        traceScrollPane.setViewportView(trace);
        traceScrollPane.setPreferredSize(new Dimension(150,300));
        traceComponent.add(traceScrollPane);
        tb1 = BorderFactory.createTitledBorder(loweredetched, "Trace");
        tb1.setTitleJustification(TitledBorder.LEFT);
        traceComponent.setBorder(tb1);

        sourceName.setText("Source");
        source.setAutoscrolls(true);
        sourceScrollPane.setViewportView(source);
        sourceScrollPane.setPreferredSize(new Dimension(500,300));
        sourceComponent.add("North",sourceName);
        sourceComponent.add("Center",sourceScrollPane);
        traceAndSourceSplitPane.setDividerSize(3);
        traceAndSourceSplitPane.setLeftComponent(traceComponent);
        traceAndSourceSplitPane.setRightComponent(sourceComponent);
        logSplitPane.setDividerSize(3);
        logSplitPane.setLeftComponent(runNumberAndInputSplitPane);
        logSplitPane.setRightComponent(traceAndSourceSplitPane);
        outputScrollPane.setPreferredSize(new Dimension(840,100));
        outputScrollPane.setViewportView(output);

        tb1 = BorderFactory.createTitledBorder(loweredetched, "Log");
        tb1.setTitleJustification(TitledBorder.LEFT);
        executionLogComponent.setBorder(tb1);
        executionLogComponent.add(logSplitPane);

        outputComponent.add(outputScrollPane);
        tb1 = BorderFactory.createTitledBorder(loweredetched, "Output");
        tb1.setTitleJustification(TitledBorder.LEFT);
        outputComponent.setBorder(tb1);

        tabbedComponent.addTab("Testing Log",executionLogComponent);
        tabbedComponent.addTab("Output",outputComponent);
        tabbedComponent.addTab("Statistics",coverageComponent);
        getContentPane().add("Center",wholeComponent);
        wholeComponent.add(toolBarComponent,
                new GridBagConstraints(0,0,1,1,1.0,0.0,
                        GridBagConstraints.EAST,GridBagConstraints.BOTH,
                        new Insets(0,2,2,2),0,0));
        wholeComponent.add(programSelectionComponent,
                new GridBagConstraints(0,1,1,1,1.0,0.0,
                        GridBagConstraints.EAST,GridBagConstraints.BOTH,
                        new Insets(0,2,2,2),0,0));
        wholeComponent.add(tabbedComponent,
                new GridBagConstraints(0,2,1,1,1.0,1.0,
                        GridBagConstraints.EAST,GridBagConstraints.BOTH,
                        new Insets(0,2,2,2),0,0));
        wholeComponent.add(progressComponent,
                new GridBagConstraints(0,3,1,1,1.0,0.0,
                        GridBagConstraints.EAST,GridBagConstraints.BOTH,
                        new Insets(0,2,2,2),0,0));
    }


    class GBConstraints extends GridBagConstraints {
        /**
		 * 
		 */
		private static final long serialVersionUID = 7310438441686940516L;

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
