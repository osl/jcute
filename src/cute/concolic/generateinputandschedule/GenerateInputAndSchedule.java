package cute.concolic.generateinputandschedule;

import cute.Cute;
import cute.concolic.Globals;
import cute.concolic.Information;
import cute.concolic.input.InputMap;
import cute.concolic.logging.BranchCoverageLog;
import cute.concolic.logging.ExecutionLog;
import cute.concolic.logging.JUnitTestGenerator;
import cute.concolic.logging.Logger;
import cute.concolic.pathconstraint.DSchedule;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.pathconstraint.ScheduleConstraint;
import cute.concolic.symbolicexecution.BranchHistory;

import java.io.File;
import java.util.Random;
import java.util.Vector;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class GenerateInputAndSchedule {
    private Information information;
    private InputMap input;
    private PathConstraint path;
    private BranchHistory history;
    public ExecutionLog ptrace;
    public Logger logger;
    public JUnitTestGenerator junitTest;
    public Random rand;
    public BranchCoverageLog coverage;


    public GenerateInputAndSchedule(Information information, InputMap input,
                                    PathConstraint path, BranchHistory history,
                                    ExecutionLog ptrace, Logger logger,
                                    JUnitTestGenerator junitTest, Random rand,
                                    BranchCoverageLog coverage) {
        this.information = information;
        this.input = input;
        this.path = path;
        this.history = history;
        this.ptrace = ptrace;
        this.logger = logger;
        this.junitTest = junitTest;
        this.rand = rand;
        this.coverage = coverage;
    }

    private int findDepth(){
        int i,j,k;
        i = path.size()-1;
        k=0;
        if (information.depth > 0) {
            for (j=0;j<=i;j++) {
                if (path.getArith(j)!=null
                        || path.getPointer(j)!=null
                        || path.getSchedule(j)!=null
                        || path.getDSchedule(j)!=null ) {
                    k++;
                    if (k>=information.depth) {
                        return information.brackTrackAt>=0?(information.brackTrackAt>=j?j:information.brackTrackAt):j;
                    }
                }
            }
        }
        return information.brackTrackAt>=0?(information.brackTrackAt>=i?i:information.brackTrackAt):i;
    }

    private int findBackTrackingPoints(int i){
        int j,k;
        k=0;
        for (j=0;j<=i;j++) {
            if (path.getArith(j)!=null
                    || path.getPointer(j)!=null
                    || (path.getSchedule(j)!=null
                        && path.getSchedule(j).isBackTrackingPoint())
                    || path.getDSchedule(j)!=null ) {
                k++;
            }
        }
        return k;
    }

    private Vector getBranchingIndices(){
        Vector ret = new Vector(1000);
        int i,k;
        k = path.size();
        for(i=0;i<k;i++){
            if (path.getArith(i)!=null
                    || path.getPointer(i)!=null
                    || path.getSchedule(i)!=null
                    || path.getDSchedule(i)!=null) {
                ScheduleConstraint pe = path.getSchedule(i);
                if(pe!=null){
                    if(pe.isBackTrackingRequired()){
                        ret.add(new Integer(i));
                    }
                } else
                    ret.add(new Integer(i));
            }
        }
        return ret;
    }

    private void predictDirected(){
        int i,counter;


        if (!history.isOK()) {
            (new File(Globals.NEW+"Inputs")).delete();
            (new File(Globals.BACK+"Inputs")).renameTo(new File(Globals.NEW+"Inputs"));
            (new File(Globals.NEW+"Branches")).delete();
            (new File(Globals.BACK+"Branches")).renameTo(new File(Globals.NEW+"Branches"));
            Counter.inc(information);
            return;
        }

        counter = Counter.get(information);
        i = findDepth();
        while(true){
            while (i>=0 && history.getStatus(i)) {
                i--;
            }
            if (i<=-1) {
                writeHistoryAndInput(i);
                System.out.println("*************************** One complete search is over ************************");
                (new File(Globals.NEW+"Branches")).delete();
                (new File(Globals.NEW+"Inputs")).delete();
                (new File(Globals.NEW+"Counter")).delete();
                coverage.write();
                information.returnVal = Cute.EXIT_COMPLETE+information.returnVal;
                return;
            }
            if (path.getArith(i)!=null) {
                history.setBranchAndStatus(i,
                        !history.getBranch(i),history.getStatus(i));
                if ((new ArithmeticSolver(path,input,information.optimized))
                        .solveArith(i,counter)) {
                    if (counter!=0) {
                        i--;
                        counter--;
                        continue;
                    }
                    writeHistoryAndInput(i);
                    return;
                }
            }
            if (path.getPointer(i)!=null) {
                history.setBranchAndStatus(i,
                        !history.getBranch(i),history.getStatus(i));
                if ((new PointerSolver(path,input)).solvePointer(i,counter)) {
                    if (counter!=0) {
                        i--;
                        counter--;
                        continue;
                    }
                    writeHistoryAndInput(i);
                    return;
                }
            }
            ScheduleConstraint pe = path.getSchedule(i);
            if(pe!=null){
                if(pe.isBackTrackingRequired()){
                    writeHistoryAndInput(i);
                    return;
                }
            }
            DSchedule ds = path.getDSchedule(i);
            if(ds!=null){
                if(ds.nextPid!=-1){
                    if(ds.pid==ds.nextPid){
                        ds.mid = ds.nextMid;
                        writeHistoryAndInput(i);
                        return;
                    } else if(ds.isRace){
                        if(information.optimalDistributed)
                            ds.isRace = false;
                        ds.pid = ds.nextPid;
                        ds.mid = ds.nextMid;
                        writeHistoryAndInput(i);
                        return;
                    }
                }
            }
            i--;
        }
    }


    private void predictRandom2(){
        int max = findDepth();
        int i = history.getInitSize();
        boolean first = true;

        while(true){
            if(i>max){
                i=0;
                if(first){
                    first = false;
                    coverage.incLastIncrementedAt();
                }
            }
            long la = coverage.getLastAt();
            la = (la>50)?la:50;
            if (coverage.getLastIncrementedAt()>la) {
                writeHistoryAndInput(-1);
                System.out.println("*********************** jCUTE may not find any more bug ********************");
                (new File(Globals.NEW+"Branches")).delete();
                (new File(Globals.NEW+"Inputs")).delete();
                coverage.write();
                information.returnVal = Cute.EXIT_COMPLETE+information.returnVal;
                return;
            }
            boolean b = rand.nextBoolean();
            if (path.getArith(i)!=null &&
                    (b ||  !path.isOtherBranchTaken(i))) {
                if ((new ArithmeticSolver(path,input,information.optimized))
                        .solveArith(i,0)) {
                    history.setBranchAndStatus(i,
                            !history.getBranch(i),history.getStatus(i));
                    writeHistoryAndInput(i);
                    return;
                }
            }
            if (path.getPointer(i)!=null &&
                    (b ||  !path.isOtherBranchTaken(i))) {
                if ((new PointerSolver(path,input)).solvePointer(i,0)) {
                    history.setBranchAndStatus(i,
                            !history.getBranch(i),history.getStatus(i));
                    writeHistoryAndInput(i);
                    return;
                }
            }
            ScheduleConstraint pe = path.getSchedule(i);
            if(pe!=null && b){
                if(pe.isBackTrackingRequired()){
                    writeHistoryAndInput(i);
                    return;
                }
            }
            DSchedule ds = path.getDSchedule(i);
            if(ds!=null && b){
                if(ds.nextPid!=-1){
                    if(ds.pid==ds.nextPid){
                        ds.mid = ds.nextMid;
                        writeHistoryAndInput(i);
                        return;
                    } else if(ds.isRace){
                        if(information.optimalDistributed)
                            ds.isRace = false;
                        ds.pid = ds.nextPid;
                        ds.mid = ds.nextMid;
                        writeHistoryAndInput(i);
                        return;
                    }
                }
            }
            i++;
        }
    }


    private void predictQuick(){
        int i;

        i = findDepth();
        while(true){
            if (i<=-1) {
                return;
            }
            if (path.getArith(i)!=null
                    && !path.isOtherBranchTaken(i)) {
                if ((new ArithmeticSolver(path,input,information.optimized))
                        .solveArith(i,0)) {
                    history.setBranchAndStatus(i,
                            !history.getBranch(i),
                            !history.getStatus(i));
                    writeHistoryAndInput(i);
                    System.out.println("**************** Quick Search Hit found");
                    return;
                }
            }
            if (path.getPointer(i)!=null
                    && !path.isOtherBranchTaken(i)) {
                if ((new PointerSolver(path,input)).solvePointer(i,0)) {
                    history.setBranchAndStatus(i,
                            !history.getBranch(i),
                            !history.getStatus(i));
                    writeHistoryAndInput(i);
                    System.out.println("***************** Quick Search Hit found");
                    return;
                }
            }
            i--;
        }
    }

    private void writeHistoryAndInput(int i){
        if(information.searchMode!=Globals.SEARCH_QUICK){
            int total = findBackTrackingPoints(path.size()-1);
            int current = findBackTrackingPoints(i);
            ProgressLog plog = new ProgressLog(information);
            plog.read(null);
            plog.setLog(total,current,path.size());
            plog.write();
        }
        if(i>=0){
            input.write();
            history.write(i+1);
        }
    }

    private void predictRandom(){
        int i,k;
        Vector indices = getBranchingIndices();

        k = indices.size();
        if (k==0) {
            writeHistoryAndInput(-1);
            System.out.println("******************** One complete search is over *********************");
            return;
        }

        while(true){
            i = rand.nextInt();
            if(i<0) i = -i;
            i = i % k;

            i = ((Integer)indices.get(i)).intValue();

            if (path.getArith(i)!=null) {
                if ((new ArithmeticSolver(path,input,information.optimized))
                        .solveArith(i,0)) {
                    writeHistoryAndInput(i);
                    return;
                }
            }
            if (path.getPointer(i)!=null) {
                if ((new PointerSolver(path,input)).solvePointer(i,0)) {
                    writeHistoryAndInput(i);
                    return;
                }
            }
            ScheduleConstraint pe = path.getSchedule(i);
            if(pe!=null){
                if(pe.isBackTrackingRequired()){
                    writeHistoryAndInput(i);
                    return;
                }
            }
            DSchedule ds = path.getDSchedule(i);
            if(ds!=null){
                if(ds.nextPid!=-1){
                    if(ds.pid==ds.nextPid){
                        ds.mid = ds.nextMid;
                        writeHistoryAndInput(i);
                        return;
                    } else if(ds.isRace){
                        if(information.optimalDistributed)
                            ds.isRace = false;
                        ds.pid = ds.nextPid;
                        ds.mid = ds.nextMid;
                        writeHistoryAndInput(i);
                        return;
                    }
                }
            }
        }
    }


    synchronized public void predict(){
        //history.print();
        if (information.mode!=Globals.REPLAY_MODE){
            if(!information.solved){
                information.solved = true;
                if((information.debugLevel&64)!=0) logger.info(64,"Old Branch History",history);
                if((information.debugLevel&128)!=0)  logger.info(128,"Old Input",input);
                if (information.searchMode==Globals.SEARCH_RANDOM){
                    predictRandom();
                    coverage.write();
                }
                else if(information.searchMode==Globals.SEARCH_RANDOM2){
                    predictRandom2();
                    coverage.write();
                }
                else if(information.searchMode==Globals.SEARCH_DFS){
                    predictDirected();
                    coverage.write();
                }
                else if(information.searchMode==Globals.SEARCH_QUICK){
                    predictQuick();
                    coverage.write();
                }
                if((information.debugLevel&256)!=0) logger.info(256,"Path Constriant",path);
                if((information.debugLevel&64)!=0) logger.info(64,"New Branch History",history);
                if((information.debugLevel&128)!=0) logger.info(128,"New Input",input);
            }
        }
        if(coverage.isIncremented())
            junitTest.printAll();
        ptrace.write();
        System.exit(information.returnVal);
    }
}
