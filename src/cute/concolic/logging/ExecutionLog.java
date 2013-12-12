package cute.concolic.logging;

import cute.concolic.Globals;
import cute.concolic.Information;
import cute.concolic.concurrency.IndexInfo;
import cute.concolic.concurrency.RacePair;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *  .
 * User: ksen
 * Date: Oct 28, 2005
 * Time: 12:03:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionLog {
    //private static String lastLine = null;
    //private static PrintWriter out = null;
    private Logger logger;
    private Information information;

    private LinkedList traceLog = new LinkedList();
    private LinkedList inputLog = new LinkedList();
    private int raceCount = 0;
    private HashMap indexedTrace = new HashMap();
    private HashSet races = new HashSet();

    public static int countDistinctErrors(File dir){
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(new File(dir,"cuteErrorLog"))));
            if(in!=null){
                HashSet tmp = (HashSet)in.readObject();
                in.close();
                return tmp.size();
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
        return 0;
    }


    public static int countFieldsWithRace(File dir){
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(new File(dir,"cuteRaceLog"))));
            if(in!=null){
                HashSet tmp = (HashSet)in.readObject();
                in.close();
                return tmp.size();
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
        return 0;
    }

    public ExecutionLog(Logger logger, Information information) {
        this.logger = logger;
        this.information = information;
    }

    public void write(){
        PrintWriter traceOut = null;
        PrintWriter inputOut = null;

        if(information.mode==Globals.RESTART_MODE){
            (new File("cuteRaceLog")).delete();
            (new File("cuteErrorLog")).delete();
        }
        if(information.mode!=Globals.REPLAY_MODE){
            if(!races.isEmpty()){
                HashSet tmp = new HashSet();
                try {
                    ObjectInputStream in = new ObjectInputStream(
                            new BufferedInputStream(new FileInputStream("cuteRaceLog")));
                    if(in!=null){
                        tmp = (HashSet)in.readObject();
                        in.close();
                    }
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                }
                races.addAll(tmp);
                ObjectOutputStream out = null;
                try {
                    out = new ObjectOutputStream(
                            new BufferedOutputStream(new FileOutputStream("cuteRaceLog")));
                    if(out!=null){
                        out.writeObject(races);
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    System.exit(1);
                }
            }
        }

        if(information.printTraceAndInputs){
            try {
                traceOut = new PrintWriter(new BufferedWriter(new FileWriter("cuteTraceLog")));
                for (Iterator iterator = traceLog.iterator(); iterator.hasNext();) {
                    TraceLog log = (TraceLog) iterator.next();
                    log.print(traceOut);
                }
                traceOut.close();
                inputOut = new PrintWriter(new BufferedWriter(new FileWriter("cuteInputLog")));
                for (Iterator iterator = inputLog.iterator(); iterator.hasNext();) {
                    InputLog log = (InputLog) iterator.next();
                    log.print(inputOut);
                }
                inputOut.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
    }

    synchronized public void printRace(RacePair rp,String raceVar){
        System.err.println("Race over field "+raceVar);
        races.add(raceVar);
    }

    synchronized public void printLineNumber(int lineno, IndexInfo ii,String type, RacePair rp) {
        if(information.printTraceAndInputs || (information.debugLevel & 512)!=0){
            try {
                throw new Exception();
            } catch(Exception e){
                StackTraceElement[] ste = e.getStackTrace();
                boolean inside = false;
                boolean flag = true;
                StackTraceElement st = null;
                for (int i = 1; flag && i < ste.length; i++) {
                    st = ste[i];
                    if(st.getClassName().equals("cute.concolic.Call")){
                        inside = true;
                    } else if(inside){
                        flag = false;
                    }
                }
                Thread curr = Thread.currentThread();
                String cName = st.getClassName();
                String packages[] = cName.split("\\.");
                StringBuffer fName = new StringBuffer();
                for(int i=0;i<packages.length-1;i++){
                    fName.append(packages[i]);
                    fName.append('/');
                }
                packages = st.getFileName().split("\\.");
                for(int i=0;i<packages.length-1;i++){
                    fName.append(packages[i]);
                    if(i<packages.length-2)
                        fName.append('/');
                }
                fName.append(".java");
                StringBuffer lineb2 = new StringBuffer();
                lineb2.append(type).append('@').append(fName).append(':')
                        .append(lineno)
                        .append('@').append(curr.getName()).append('\n');

                String line = lineb2.toString();
                TraceLog tl2 = traceLog.isEmpty()?null:(TraceLog)traceLog.getLast();
                if(ii!=null ||
                        (lineno>=0 && (tl2==null || !line.equals(tl2.getLine())))){
                    TraceLog tl = new TraceLog(line);
                    traceLog.addLast(tl);
                    if(ii!=null){
                        indexedTrace.put(new Integer(ii.index),tl);
                    }
                    if(rp!=null && (rp.rl1!=null || rp.rl2!=null)){
                        raceCount++;
                        tl.addEndRace(raceCount);
                        if(rp.rl1!=null){
                            ((TraceLog)indexedTrace.get(new Integer(rp.rl1.begin)))
                                    .addBeginRace(raceCount);
                        }
                        if(rp.rl2!=null){
                            ((TraceLog)indexedTrace.get(new Integer(rp.rl2.begin)))
                                    .addBeginRace(raceCount);
                        }
                    }
                    logger.info(512,line,null);
                }
            }
        }
    }

    synchronized public void printInput(String str){
        if(information.printTraceAndInputs || (information.debugLevel & 512)!=0){
            logger.info(512,str,null);
            inputLog.addLast(new InputLog(str));
        }
    }

    synchronized public void printInputLn(String str){
        if(information.printTraceAndInputs || (information.debugLevel & 512)!=0){
            try {
                throw new Exception();
            } catch(Exception e){
                StackTraceElement[] ste = e.getStackTrace();
                boolean inside = false;
                boolean flag = true;
                StackTraceElement st = null;
                for (int i = 1; flag && i < ste.length; i++) {
                    st = ste[i];
                    if(st.getClassName().startsWith("cute.")){
                        inside = true;
                    } else if(inside){
                        flag = false;
                    }
                }
                String cName = st.getClassName();
                String packages[] = cName.split("\\.");
                StringBuffer fName = new StringBuffer();
                for(int i=0;i<packages.length-1;i++){
                    fName.append(packages[i]);
                    fName.append('/');
                }
                packages = st.getFileName().split("\\.");
                for(int i=0;i<packages.length-1;i++){
                    fName.append(packages[i]);
                    if(i<packages.length-2)
                        fName.append('/');
                }
                fName.append(".java");
                int lineno = st.getLineNumber();
                String line = new StringBuffer().append(str)
                        .append('@').append(fName).append(":").append(lineno)
                        .append('@').append(Thread.currentThread().getName())
                        .append('\n').toString();
                if(!(lineno<0)){
                    logger.info(512,line,null);
                    if(information.printTraceAndInputs){
                        inputLog.addLast(new InputLog(line));
                    }
                }
            }
        }
    }

    synchronized public void printLineNumber(int lineno){
        printLineNumber(lineno,null,"other",null);
    }

    public synchronized void writeError(String error) {
        if(information.mode!=Globals.REPLAY_MODE){
            HashSet tmp = new HashSet();
            try {
                ObjectInputStream in = new ObjectInputStream(
                        new BufferedInputStream(new FileInputStream("cuteErrorLog")));
                if(in!=null){
                    tmp = (HashSet)in.readObject();
                    in.close();
                }
            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
            }
            tmp.add(error);
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(
                        new BufferedOutputStream(new FileOutputStream("cuteErrorLog")));
                if(out!=null){
                    out.writeObject(tmp);
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
    }
}
