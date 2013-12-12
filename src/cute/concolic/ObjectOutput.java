package cute.concolic;

import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ObjectOutput {
    public ObjectOutputStream out;

    public ObjectOutput(String ext) {
        if(Globals.globals.information.mode==Globals.REPLAY_MODE)
            out=null;
        else {
            try {
                out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(Globals.NEW+ext)));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
    }

    public boolean ok(){
        return out!=null;
    }

    public void close(){
        if(out!=null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
    }
}
