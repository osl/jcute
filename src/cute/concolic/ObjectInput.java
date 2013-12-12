package cute.concolic;

import java.io.*;


/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ObjectInput {
    public ObjectInputStream in;
    private String ext;
    private boolean isBackPresent;
    private Information information;

    public ObjectInput(String ext,boolean isBackPresent,Information information) {
        this.ext = ext;
        this.isBackPresent = isBackPresent;
        this.information = information;
        if(information.mode==Globals.RESTART_MODE){
            if(isBackPresent)
                (new File(Globals.BACK+ext)).delete();
            (new File(Globals.OLD+ext)).delete();
            (new File(Globals.NEW+ext)).delete();
        }
        try {
            if(information.mode==Globals.REPLAY_MODE){
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(Globals.OLD+ext)));
            } else {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(Globals.NEW+ext)));
            }
        } catch(IOException ex){
            in = null;
        }
    }

    public void close(){
        if(in==null)
            return;
        try {
            in.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        if (information.mode !=Globals.REPLAY_MODE) {
            if(isBackPresent){
                (new File(Globals.BACK+ext)).delete();
                (new File(Globals.OLD+ext)).renameTo(new File(Globals.BACK+ext));
            }
            (new File(Globals.OLD+ext)).delete();
            (new File(Globals.NEW+ext)).renameTo(new File(Globals.OLD+ext));
        }
    }

    public boolean ok() {
        return in!=null;
    }
}
