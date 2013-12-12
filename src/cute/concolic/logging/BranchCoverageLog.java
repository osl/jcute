package cute.concolic.logging;

import cute.concolic.Information;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class BranchCoverageLog implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3425578445552948701L;
    private long lastIncrementedAt = 0;
    private boolean isIncremented = false;
    private long lastAt = 0;
    private Information information;

    public BranchCoverageLog(Information information) {
        this.information = information;
    }

    public HashMap getFunctionBranchCoverage() {
        return functionBranchCoverage;
    }

    public long getTime() {
        return time;
    }

    public long getIterations() {
        return iterations;
    }

    /**
     *
     */
    public HashMap functionBranchCoverage;
    public final static String file = "cuteCoverage";
    private long time;
    private long iterations;
    private int nThreads;

    public boolean isIncremented() {
        return isIncremented;
    }

    public long getLastIncrementedAt() {
        return lastIncrementedAt;
    }

    public long getLastAt() {
        return lastAt;
    }

    public void incLastIncrementedAt() {
        this.lastIncrementedAt++;
    }

    public void beginTime(){
        time -= System.currentTimeMillis();
    }

    public void endTime(){
        time += System.currentTimeMillis();
    }

    public void read(){
        if(information.mode==2){
            (new File(file)).delete();
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            functionBranchCoverage = new HashMap();
            time = 0;
            iterations = 0;
            lastIncrementedAt = 0;
            lastAt = 0;
            nThreads = 0;
            beginTime();
            iterations++;
            return;
        }
        try {
            functionBranchCoverage = (HashMap) in.readObject();
            time = ((Long)in.readObject()).longValue();
            iterations = ((Long)in.readObject()).longValue();
            nThreads = ((Integer)in.readObject()).intValue();
            lastIncrementedAt = ((Long)in.readObject()).longValue();
            lastAt = ((Long)in.readObject()).longValue();
            //System.out.println("lastIncrementedAt = " + lastIncrementedAt);
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
        beginTime();
        iterations++;
    }

    public boolean branchTaken(String fname,int bid,int bCount,boolean pos){
        int[] branches = (int [])functionBranchCoverage.get(fname);
        if(branches==null){
            branches = new int[bCount];
            functionBranchCoverage.put(fname,branches);
        }
        if((branches[bid-1] & (pos?1:2))==0){
            lastIncrementedAt = 0;
            isIncremented = true;
            lastAt = iterations;
        }
        branches[bid-1] = branches[bid-1] | (pos?1:2);
        return (branches[bid-1] & (pos?2:1))>0;
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
            endTime();
            if(information.nThreads>nThreads){
                nThreads = information.nThreads;
            }
            out.writeObject(functionBranchCoverage);
            out.writeObject(new Long(time));
            out.writeObject(new Long(iterations));
            out.writeObject(new Integer(nThreads));
            out.writeObject(new Long(lastIncrementedAt));
            out.writeObject(new Long(lastAt));
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

    public void printPercentageCoverage(){
        int total=0;
        int sum = 0;
        for (Iterator iterator = functionBranchCoverage.keySet().iterator(); iterator.hasNext();) {
            String fname = (String) iterator.next();
            int[] branches = (int []) functionBranchCoverage.get(fname);
            total += (2*branches.length);
            for (int i = 0; i < branches.length; i++) {
                int branch = branches[i];
                if ((branch & 1)!=0) sum++;
                if ((branch & 2)!=0) sum++;
            }
        }
        double percentage = (100.0*sum)/total;
        System.out.println("Percentage of branches covered = " + percentage + " in time "+time+" (ms)");
        System.out.flush();
    }

    public void printFunctionsCovered(){
        System.out.println("Total functions invoked = " + functionBranchCoverage.size());
        System.out.flush();
    }

    public void printBranchesCovered(){
        int sum = 0;
        for (Iterator iterator = functionBranchCoverage.keySet().iterator(); iterator.hasNext();) {
            String fname = (String) iterator.next();
            int[] branches = (int []) functionBranchCoverage.get(fname);
            for (int i = 0; i < branches.length; i++) {
                int branch = branches[i];
                if ((branch & 1)!=0) sum++;
                if ((branch & 2)!=0) sum++;
            }
        }
        System.out.println("Total branches covered = " + sum);
        System.out.flush();
    }

    public void printDetailedCoverage(){
        int total=0;
        int sum = 0;
        System.out.println("___________________________________________________________________");
        System.out.println("Printing branch coverage statistics");
        System.out.println("___________________________________________________________________");
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
            System.out.println(localSum+" branches covered out of "+(branches.length*2)+" branches in the function "+fname);
        }
        double percentage = (100.0*sum)/total;
        System.out.println("Total functions invoked = " + functionBranchCoverage.size());
        System.out.println("Total branches covered = " + sum);
        System.out.println("Percentage of branches covered = " + percentage + " in time "+time+" (ms)");
        System.out.println("Number of threads = "+nThreads);
        System.out.println("Number of iterations = "+iterations);
        System.out.println("___________________________________________________________________");
        System.out.flush();
    }

    public static void main(String[] args) {
        BranchCoverageLog bc = readCoverageLog(null);
        if(bc!=null){
            bc.printDetailedCoverage();
        }
    }

    public static BranchCoverageLog readCoverageLog(File dir) {
        ObjectInputStream in = null;
        BranchCoverageLog bc = new BranchCoverageLog(null);
        try {
            if(dir==null)
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("tmpjcute/"+file)));
            else
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(dir,file))));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            bc.functionBranchCoverage = (HashMap) in.readObject();
            bc.time = ((Long)in.readObject()).longValue();
            bc.iterations = ((Long)in.readObject()).longValue();
            bc.nThreads = ((Integer)in.readObject()).intValue();
            bc.lastIncrementedAt = ((Long)in.readObject()).longValue();
            bc.lastAt = ((Long)in.readObject()).longValue();
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
        //bc.printDetailedCoverage();
        return bc;
    }


    public double getCoverage(){
        int total=0;
        int sum = 0;
        for (Iterator iterator = functionBranchCoverage.keySet().iterator(); iterator.hasNext();) {
            String fname = (String) iterator.next();
            int[] branches = (int []) functionBranchCoverage.get(fname);
            total += (2*branches.length);
            for (int i = 0; i < branches.length; i++) {
                int branch = branches[i];
                if ((branch & 1)!=0) { sum++; }
                if ((branch & 2)!=0) { sum++; }
            }
        }
        return ((int)((10000.0*sum)/total))/100.0;
    }

    public int getBranches(File f) {
        int sum = 0;
        for (Iterator iterator = this.functionBranchCoverage.keySet().iterator();
             iterator.hasNext();) {
            String fname = (String) iterator.next();
            int[] branches = (int []) this.functionBranchCoverage.get(fname);
            for (int i = 0; i < branches.length; i++) {
                int branch = branches[i];
                if ((branch & 1)!=0) { sum++; }
                if ((branch & 2)!=0) { sum++; }
            }
        }
        return sum;
    }
}
