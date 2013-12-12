package cute.concolic;

import argparser.ArgParser;
import argparser.BooleanHolder;
import argparser.IntHolder;
import cute.Cute;
import cute.concolic.concurrency.Scheduler;
import cute.concolic.input.InputMap;
import cute.concolic.logging.BranchCoverageLog;
import cute.concolic.logging.ExecutionLog;
import cute.concolic.logging.JUnitTestGenerator;
import cute.concolic.logging.Logger;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.symbolicexecution.BranchHistory;
import cute.concolic.symbolicexecution.ComputationStacks;
import cute.concolic.symbolicstate.State;
import cute.concolic.generateinputandschedule.GenerateInputAndSchedule;
import cute.instrument.SymbolTable;

import java.io.*;
import java.util.Random;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

public class Globals {
    public static final String version = "1.0.1";

    public static Globals globals = new Globals();
    public static final int RESTART_MODE = 2;
    public static final int REPLAY_MODE = 1;
    public static final int NEXT_MODE = 0;

    public static final int SEARCH_DFS = 1;
    public static final int SEARCH_RANDOM = 2;
    public static final int SEARCH_QUICK = 3;
    public static final int SEARCH_RANDOM2 = 4;

    public static final String BACK = "cuteBack";
    public static final String OLD = "cuteOld";
    public static final String NEW = "cuteNew";

    public static final int INT = 1;
    public static final int SHORT = 2;
    public static final int LONG = 3;
    public static final int BYTE = 4;
    public static final int CHAR = 5;
    public static final int FLOAT = 6;
    public static final int DOUBLE = 7;
    public static final int BOOLEAN = 8;
    public static final int REFERENCE = 9;
    public static final int OBJECT = 10;

    public InputMap input;
    public State state;
    public SymbolTable st;
    public ComputationStacks cstack;
    public BranchHistory history;
    public PathConstraint path;
    public Scheduler sched;
    public ExecutionLog ptrace;
    public Logger logger;
    public JUnitTestGenerator junitTest;
    public Random rand;
    public BranchCoverageLog coverage;
    public GenerateInputAndSchedule solver;

    public boolean initialized;
    public Information information;


    public Globals() {
        initialized = false;
    }

    public void begin(){
        information = new Information();
        ArgParser parser = new ArgParser("program");
        IntHolder depth = new IntHolder(0);
        parser.addOption("-d %d #Depth of search. Default is 0, which implies infinite depth",depth);
        IntHolder seed = new IntHolder((int)System.currentTimeMillis());
        //IntHolder seed = new IntHolder(34526);
        parser.addOption("-s %d #Seed for random number generator in case -r " +
                "option is given. Default is current system time.",seed);
        IntHolder debugLevel = new IntHolder(0);
        parser.addOption("-t %d #Various debug information and statistics. Default is 0 (no debug information printed).\n" +
                "\t1 print trace of instrumentation function call's entry and exit.\n" +
                "\t2 print info about instrumented function call inserted for concurrency.\n" +
                "\t4 print input map after reading from disk.\n" +
                "\t8 print history at every history change.\n" +
                "\t16 print symbolic state at every state change.\n" +
                "\t32 print path contraint whenever path constraint is updated.\n" +
                "\t64 print old and new history at the end of execution.\n" +
                "\t128 print old and new input map at the end of the execution.\n" +
                "\t256 print path constraint at the end of the excution.\n" +
                "\t512 print line number executed.",debugLevel);
        IntHolder mode = new IntHolder(0);
        parser.addOption("-m %d {0,1,2} #\n\t0 - next path (depends on history), " +
                "\n\t1 - replay last execution,\n\t2 - start fresh execution without " +
                "looking at any history. Default is 0.",mode);
        BooleanHolder random = new BooleanHolder(false);
        parser.addOption("-r %v #if -r is specified, inputs are randomly initialized; " +
                "else, inputs are set to 0. Objects are initialized to null in either cases.",random);
        IntHolder randomSearch = new IntHolder(SEARCH_DFS);
        parser.addOption("-p %d {1,2,3,4} #search strategy to be invoked: " +
                "1 (default) is DFS, 2 is random, 3 is quick, 4 is better random",randomSearch);
        BooleanHolder optimalDistributed = new BooleanHolder(false);
        parser.addOption("-a %v #turn off Optimal Distrubuted Search ",optimalDistributed);
        BooleanHolder generateJUnit = new BooleanHolder(true);
        parser.addOption("-j %v #generate JUnit test cases",generateJUnit);
        BooleanHolder printTraceAndInputs = new BooleanHolder(true);
                parser.addOption("-v %v #verbose: print inputs and trace of execution",printTraceAndInputs);
                IntHolder NArg = new IntHolder(0);
        parser.addOption("-n %d #Pass a single integer argument ",NArg);

        String arg = System.getProperty("cute.args","");
        arg = arg.trim();
        if(arg.startsWith(":")){
            arg = arg.substring(1);
        }
        //System.out.println("arg = "+arg);
        String[] args2 = null;
        if(!arg.equals("")){
            args2 = arg.split(":");
        } else {
            args2 = new String[0];
        }
        parser.matchAllArgs(args2);

        this.information.seed = seed.value;
        this.information.depth = depth.value;
        this.information.random = random.value;
        this.information.searchMode = randomSearch.value;
        this.information.mode = mode.value;
        this.information.debugLevel = debugLevel.value;
        this.information.optimalDistributed = !optimalDistributed.value;
        this.information.printTraceAndInputs = printTraceAndInputs.value;
        Cute.N = NArg.value;
        this.information.generateJUnit = generateJUnit.value;

        initialize();

        this.initialized = true;
        this.sched.setPriority(Thread.MIN_PRIORITY);
        //Runtime.getRuntime().addShutdownHook(new SolveOnExit());
        this.sched.start();
    }


    public void initialize() {
        rand = new Random(information.seed);
        logger = new Logger(information,new PrintWriter(System.out));
        junitTest = new JUnitTestGenerator(information);
        ptrace = new ExecutionLog(logger,information);

        try {
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("cuteSymbolTable")));
            try {
                st = (SymbolTable)in.readObject();
                st.reverseMap();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }

        state = new State(logger,information);

        history = new BranchHistory(logger,information);
        history.read();

        path = new PathConstraint(logger,information);

        coverage = new BranchCoverageLog(information);
        coverage.read();


        input = new InputMap(information,logger,junitTest,state,ptrace,st,rand);
        input.read();

        cstack = new ComputationStacks(state,path,history,coverage,input);

        solver = new GenerateInputAndSchedule(information,input,path,history,ptrace,logger,junitTest,rand,coverage);

        sched = new Scheduler(information,path,state,history,rand,solver);

        information.brackTrackAt = -1;
        information.nThreads = 1;

    }

}
