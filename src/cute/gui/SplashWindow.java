package cute.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 10:06:14 PM
 */
public class SplashWindow extends JWindow {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9126790832814422160L;

	public SplashWindow(String filename, Box b,int waitTime) {

        JLabel l = new JLabel(GetImageIcon.getIcon(filename));
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                screenSize.height/2 - (labelSize.height/2));
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
                {
            public void run()
            {
                setVisible(false);
                setVisible(false);
            }
        };
        Runnable waitRunner = new Runnable()
                {
            public void run()
            {
                try
                {
                    Thread.sleep(pause);
                    SwingUtilities.invokeAndWait(closerRunner);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    // can catch InvocationTargetException
                    // can catch InterruptedException
                }
            }
        };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
        b.gui = new JCuteGui();
        try {
            splashThread.join();
            addMouseListener(new MouseAdapter()
                    {
                public void mousePressed(MouseEvent e)
                {
                    setVisible(false);
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
