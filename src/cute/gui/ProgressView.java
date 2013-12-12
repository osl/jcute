package cute.gui;

import cute.concolic.generateinputandschedule.ProgressLog;
import cute.concolic.logging.BranchCoverageLog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 8, 2005
 * Time: 7:50:39 PM
 */
public class ProgressView extends JPanel implements ProgressLogger{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2432813574234989829L;
	JLabel jLabel12 = new JLabel();
    JTextField iterationText = new JTextField("0",10);
    JLabel jLabel161 = new JLabel();
    JTextField coverageText1 = new JTextField("0",5);
    JLabel jLabel16 = new JLabel();
    JTextField coverageText = new JTextField("0.0",5);
    JLabel jLabel19 = new JLabel();
    JTextField pointsText = new JTextField("0",10);
    JPanel flow8 = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
    JLabel errorL = new JLabel();
    JTextField errorText = new JTextField("0",5);
    JLabel jLabel4 = new JLabel();
    JProgressBar simpleProgress = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
    Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    public ProgressView() {
        super(new GridBagLayout());
        jLabel12.setText("Paths Covered");
        iterationText.setEditable(false);
        iterationText.setHorizontalAlignment(JTextField.RIGHT);
        jLabel16.setText("Branch Coverage");
        coverageText.setEditable(false);
        coverageText.setHorizontalAlignment(JTextField.RIGHT);
        jLabel161.setText("Branches Covered");
        coverageText1.setEditable(false);
        coverageText1.setHorizontalAlignment(JTextField.RIGHT);
        errorL.setText("Errors");
        errorText.setEditable(false);
        errorText.setHorizontalAlignment(JTextField.RIGHT);
        jLabel19.setText("DFS Info");
        pointsText.setEditable(false);
        pointsText.setHorizontalAlignment(JTextField.RIGHT);
        flow8.add(jLabel12);
        flow8.add(iterationText);
        flow8.add(jLabel161);
        flow8.add(coverageText1);
        flow8.add(jLabel16);
        flow8.add(coverageText);
        flow8.add(errorL);
        flow8.add(errorText);
        flow8.add(jLabel19);
        flow8.add(pointsText);
        jLabel4.setText("Total Progress ");
        simpleProgress.setValue(0);
        add(flow8,new GBConstraints(0,0,2,1,0.0,0.0,10,0,0,0,new Insets(0,0,0,0)));
        add(jLabel4,new GBConstraints(0,1,1,1,0.0,0.0,17,0,0,0,new Insets(0,0,0,0)));
        add(simpleProgress,new GBConstraints(1,1,1,1,1.0,0.0,17,2,0,0,new Insets(0,0,0,0)));
        TitledBorder tb1 = BorderFactory.createTitledBorder(loweredetched, "Progress");
        tb1.setTitleJustification(TitledBorder.LEFT);
        setBorder(tb1);
    }

    public void setProgress(ProgressLog plog,BranchCoverageLog cover,
                            int runCount,int totalCount,int branchCount,int errorCount) {
        iterationText.setText(String.valueOf(runCount)+"/"+totalCount);
        simpleProgress.setMaximum(totalCount);
        simpleProgress.setValue(runCount);
        coverageText1.setText(String.valueOf(branchCount));
        errorText.setText(String.valueOf(errorCount));
        if(plog!=null){
            int total=plog.getTotal();
            pointsText.setText(String.valueOf(plog.getCurrent())
                    +"/"+String.valueOf(plog.getSofar())
                    +"/"+String.valueOf(total));
        }
        if(cover!=null){
            coverageText.setText(String.valueOf(cover.getCoverage())+"%");
        }
    }


    class GBConstraints extends GridBagConstraints {
        /**
		 * 
		 */
		private static final long serialVersionUID = -6669807688967920690L;

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
