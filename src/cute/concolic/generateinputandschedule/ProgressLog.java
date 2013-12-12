package cute.concolic.generateinputandschedule;

import cute.concolic.Information;

import java.io.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 8, 2005
 * Time: 8:25:59 PM
 */
public class ProgressLog implements Serializable{
    private Information information;

    private static final long serialVersionUID = 8726669116655279145L;
	public final static String file = "cuteProgressLog";
    private int total;
    private int current;
    private int sofar;
    private int actualTotal;

    public ProgressLog(Information information) {
        this.information = information;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSofar() {
        return sofar;
    }

    public void setSofar(int sofar) {
        this.sofar = sofar;
    }

    public void read(File dir){
        if(information.mode==2){
            (new File(file)).delete();
        }
        ObjectInputStream in = null;
        try {
            if(dir==null)
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            else
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(dir,file))));
        } catch (IOException e) {
            total = -1;
            current = -1;
            sofar = -1;
            actualTotal = -1;
            return;
        }
        try {
            total = ((Integer)in.readObject()).intValue();
            current = ((Integer)in.readObject()).intValue();
            sofar = ((Integer)in.readObject()).intValue();
            actualTotal = ((Integer)in.readObject()).intValue();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
    }

    public void write(){
        if(information.mode==1) return;
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
        try {
            out.writeObject(new Integer(total));
            out.writeObject(new Integer(current));
            out.writeObject(new Integer(sofar));
            out.writeObject(new Integer(actualTotal));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
    }


    public static ProgressLog readCoverage(File dir){
        ObjectInputStream in = null;
        ProgressLog ret = new ProgressLog(null);
        try {
            if(dir==null)
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            else
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(dir,file))));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            ret.setTotal(((Integer)in.readObject()).intValue());
            ret.setCurrent(((Integer)in.readObject()).intValue());
            ret.setSofar(((Integer)in.readObject()).intValue());
            ret.setActualTotal(((Integer)in.readObject()).intValue());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
        return  ret;
    }

    public void setLog(int ntotal,int ncurrent,int bp){
        if(total==-1){
            total = ntotal;
        } else if(total<ntotal){
            total = ntotal;
        }
        if(sofar==-1){
            sofar = ncurrent;
        } else if(sofar>ncurrent){
            sofar = ncurrent;
        }
        current = ncurrent;
        actualTotal = bp;
    }

    public int getActualTotal() {
        return actualTotal;
    }

    public void setActualTotal(int actualTotal) {
        this.actualTotal = actualTotal;
    }
}
