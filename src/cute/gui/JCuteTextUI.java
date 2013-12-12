package cute.gui;

import ccl.util.FileUtil;
import cute.Cute;
import cute.concolic.logging.BranchCoverageLog;
import cute.concolic.logging.JUnitTestGenerator;
import cute.concolic.logging.ExecutionLog;
import cute.concolic.generateinputandschedule.ProgressLog;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 22, 2005
 * Time: 6:17:23 PM
 */
public class JCuteTextUI implements ProcessOwner{
    private OutputLogger output;
    private ProgressLogger progress;
    private MessageLogger box;
    private GuiControllerMask mask;
    private Process proc;
    private GenerateJCuteLog jcuteLog;
    public LinkedList list;

    public final static int LOG_BRANCHES = 3;
    public final static int LOG_ALL = 2;
    public final static int LOG_ERRORS = 1;
    public final static int LOG_TENTH = 0;
    public final static String RUN_DIRECTORY_PREFIX = "run";
    public final static String LAST_RUN_DIRECTORY = "last";
    public final static String pathSeparator=System.getProperty("path.separator");
    public final static String fileSeparator = System.getProperty("file.separator");;
    public final static int SEARCH_DFS = 0;
    public final static int SEARCH_RANDOM = 1;
    public final static int SEARCH_QUICK = 2;

    private int exitv = 0;
    private int runCount = 0;
    private int branchCount = 0;
    private int lastBranchCoverageIncrementedAt=0;
    private int errorCountRace = 0;
    private int errorCountDeadlock = 0;
    private int errorCountAssertion = 0;
    private int errorCountException = 0;
    private int errorCountTotal = 0;
    private long runningTime = 0;
    private boolean isPaused = false;
    private boolean isCanceled = false;

    private boolean optionPrintOutput = true;
    private boolean optionLogPath = true;
    private boolean optionLogTraceAndInput = true;
    private boolean optionGenerateJUnit = true;
    private String optionExtraOptions = "";
    private String optionJUnitOutputFolderName =System.getProperty("user.dir");
    private String optionJUnitPkgName = "";
    private int optionNumberOfPaths = 10000;
    private int optionLogLevel = LOG_BRANCHES;
    private int optionDepthForDFS = 0;
    private int optionSearchStrategy = SEARCH_DFS;
    private boolean optionSequential=true;
    private int optionQuickSearchThreshold = 100;
    private boolean optionLogRace=true;
    private boolean optionLogDeadlock=true;
    private boolean optionLogException =true;
    private boolean optionLogAssertion =true;
    private boolean optionUseRandomInputs = false;

    private String[] envp;

    private String srcFileName;
    private String srcDirName;

    // tmpjcute
    private String tmpDir;
    private File tmpDirFile;
    private String jcuteHome;
    private String tmpClassPath;
    private File tmpLastFile;
    private String tmpOutput;
    private String mainClassName;
    private String mainFunName;
    private String mainClassNamePlusFun;
    private BranchCoverageLog bc;


    public int getCountRaceFields(){
        return ExecutionLog.countFieldsWithRace(tmpLastFile);
    }

    public int getCountExceptions() {
        return ExecutionLog.countDistinctErrors(tmpLastFile);
    }

    public BranchCoverageLog getBc() {
        return bc;
    }

    public int getErrorCountRace() {
        return errorCountRace;
    }

    public int getErrorCountDeadlock() {
        return errorCountDeadlock;
    }

    public int getErrorCountAssertion() {
        return errorCountAssertion;
    }

    public int getErrorCountException() {
        return errorCountException;
    }

    public int getErrorCountTotal() {
        return errorCountTotal;
    }

    public long getRunningTime() {
        return runningTime;
    }

    public void addExitValue(Object o){
        list.addFirst(o);
    }

    public JCuteTextUI(boolean isRecord) {
        this.output = new cute.gui.DefaultOutputLogger();
        this.progress = new cute.gui.DefaultProgressLogger();
        this.box = new cute.gui.DefaultMessageLogger();
        this.mask = new cute.gui.DefaultGuiControllerMask();
        list = new LinkedList();
        jcuteLog = new GenerateJCuteLog(isRecord);
        setAllPaths();
        setInit();
        jcuteLog.logInit();
    }

    public JCuteTextUI(OutputLogger output, ProgressLogger pl,
                       MessageLogger box, GuiControllerMask mask,
                       boolean isRecord) {
        this.output = output;
        this.progress = pl;
        this.box = box;
        this.mask = mask;
        list = new LinkedList();
        jcuteLog = new GenerateJCuteLog(isRecord);
        setAllPaths();
        setInit();
        jcuteLog.logInit();
    }

    public String getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
        jcuteLog.logCall("setTmpDir",tmpDir);
    }

    public File getTmpLastFile() {
        return tmpLastFile;
    }

    public String getJcuteHome() {
        return jcuteHome;
    }

    public String getTmpOutput() {
        return tmpOutput;
    }

    public void setMainClassNamePlusFun(String s) {
        mainClassNamePlusFun = s;
        jcuteLog.logCall("setMainClassNamePlusFun",s);
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
        jcuteLog.logCall("setSrcFileName",srcFileName);
    }

    public String getSrcDirName() {
        return srcDirName;
    }

    public void setSrcDirName(String srcDirName) {
        this.srcDirName = srcDirName;
        jcuteLog.logCall("setSrcDirName",srcDirName);
    }

    private void reset(){
        exitv = 0;
        runCount = 0;
        branchCount = 0;
        lastBranchCoverageIncrementedAt = 0;
        errorCountAssertion = 0;
        errorCountDeadlock = 0;
        errorCountException = 0;
        errorCountRace = 0;
        errorCountTotal = 0;
        runningTime = 0;
    }

    private boolean isError(int exitv){
        return (optionLogDeadlock && (exitv&Cute.EXIT_DEADLOCK)>0)
                || (optionLogRace && (exitv&Cute.EXIT_RACE)>0)
                || (optionLogException && (exitv&Cute.EXIT_ERROR)>0)
                || (optionLogAssertion && (exitv & Cute.EXIT_ASSERT_FAILED)>0);
    }

    private String colonize(String others){
        String[] args = others.trim().split("\\s+");
        String ret = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(!arg.equals(""))
                ret = ret + arg+":";
        }
        return  ret;
    }


    private void setAllPaths(){
        srcFileName = null;
        srcDirName = null;

        String tmp = JCuteGui.class.getResource( "JCuteGui.class" ).getPath();
        System.out.println("jcute.jar resource location = "+tmp);

        String javaClassPath = tmp.substring(5, tmp.indexOf('!'));

        File tmp2 = new File(javaClassPath);
        javaClassPath = tmp2.getAbsolutePath();
        javaClassPath = javaClassPath.replaceAll("\\\\","/");
        System.out.println("jcute.jar location = "+javaClassPath);
        if(!javaClassPath.endsWith("jcute.jar") || !(new File(javaClassPath)).exists()){
            System.err.println("It seems that you have invoked CUTE for Java " +
                    "without using jcute.jar." +
                    " Don't do this!");
            System.exit(1);
        }
        jcuteHome = javaClassPath.substring(0,javaClassPath.length()-9);

        if(jcuteHome.indexOf(' ')>=0){
            System.err.println("The path where jCUTE has been installed contains blank space: "
                    +jcuteHome);
            System.err.println("Please make sure that the installation " +
                    "path of CUTE for Java has no space.");
            System.exit(1);
        }
        if(!(new File(jcuteHome+fileSeparator+"liblpsolve51.so")).exists()){
            System.err.println(jcuteHome+fileSeparator+"liblpsolve51.so is missing.");
            System.exit(1);
        }
        if(!(new File(jcuteHome+fileSeparator+"liblpsolve51j.so")).exists()){
            System.err.println(jcuteHome+fileSeparator+"liblpsolve51j.so is missing.");
            System.exit(1);
        }
        if(!(new File(jcuteHome+fileSeparator+"lpsolve51.dll")).exists()){
            System.err.println(jcuteHome+fileSeparator+"lpsolve51.dll is missing.");
            System.exit(1);
        }
        if(!(new File(jcuteHome+fileSeparator+"lpsolve51j.dll")).exists()){
            System.err.println(jcuteHome+fileSeparator+"lpsolve51j.dll is missing.");
            System.exit(1);
        }
        System.out.println("CUTE for Java home directory is = "+jcuteHome);

        if(tmpDir!=null){
            tmpDir = tmpDir+fileSeparator+"tmpjcute";
        } else {
            tmpDir = System.getProperty("user.dir")+fileSeparator+"tmpjcute";
        }
        if(tmpDir.indexOf(' ')>=0){
             System.err.println("The path for working directory (i.e. the directory from " +
                     "where you invoked CUTE for Java) contains blank space: "
                     +System.getProperty("user.dir"));
             System.err.println("Please make sure that the path of " +
                     "working directory has no space.");
             System.exit(1);
         }
        System.out.println("Temporary Working Directory is " + tmpDir);
        System.out.println();
        tmpDirFile = new File(tmpDir);
        tmpOutput = tmpDir+fileSeparator+"output";
        tmpLastFile = new File(tmpOutput+fileSeparator+LAST_RUN_DIRECTORY);

        tmpClassPath = tmpDir+fileSeparator+"classes";
        String fullClassPath = tmpClassPath + pathSeparator + javaClassPath;

        System.out.println("Setting environment as follows:");
        envp = new String[4];
        envp[0] = "LD_LIBRARY_PATH="+jcuteHome;
        envp[1] = "JAVA_LIB_PATH="+jcuteHome;
        envp[2] = "PATH="+System.getProperty("java.library.path")+pathSeparator+jcuteHome;
        envp[3] = "CLASSPATH="+fullClassPath+pathSeparator+System.getProperty("java.class.path");
        for (int i = 0; i < envp.length; i++) {
            String s = envp[i];
            System.out.println("env["+i+"]:"+s);
        }
    }

    private int runJCuteOnce(int m,String d,String p,String v,String j,String r,String others,
                             File wd,String params){
        FileUtil.copy("coverage.ec",tmpLastFile.getAbsolutePath()+fileSeparator+"coverage.ec");
        int exitv = CommandLine.executeOnce("java -ea -Xmx512m -Xms512m" +
                " -Djava.library.path="+jcuteHome +
                " -Dcute.args=-m:"+m+":"+d+p+v+j+r
                +colonize(others)+" cute.RunOnce "
                + mainClassNamePlusFun+" "+params,
                envp,wd,output,this,isOptionPrintOutput());
        FileUtil.copy(tmpLastFile.getAbsolutePath()+fileSeparator+"coverage.ec","coverage.ec");
        ProgressLog plog = ProgressLog.readCoverage(tmpLastFile);
        BranchCoverageLog cover = BranchCoverageLog.readCoverageLog(tmpLastFile);
        progress.setProgress(plog,cover,runCount,getOptionNumberOfPaths(),branchCount,errorCountTotal);
        jcuteLog.logExitValue(exitv,this);
        return exitv;
    }

    private int runJCuteOnce(int m,String d,String p,String v,String j,String r,
                             String others,String params){
        return runJCuteOnce(m,d,p,v,j,r,others,tmpLastFile,params);
    }

    public void replayAction(String selectedRun,String params){
        jcuteLog.logCall("replayAction",selectedRun,params);
        File f = new File(tmpOutput+fileSeparator+selectedRun);
        System.out.println("Should replay in directory "+f.getAbsolutePath());
        if(!f.exists()){
            box.ask(selectedRun+" (input) was not logged");
        } else {
            String v;
            if(isOptionLogPath() && isOptionLogTraceAndInput()){
                v = "-v:";
            } else {
                v ="";
            }
            String j;
            if(isOptionGenerateJUnit()){
                j = "-j:";
            } else {
                j ="";
            }
            String r;
            if(isOptionUseRandomInputs()){
                r = "-r:";
            } else {
                r = "";
            }
            runJCuteOnce(1,"","",v,j,r,getOptionExtraOptions(),f,params);
            if(isOptionGenerateJUnit()){
                int x = runCount;
                if(selectedRun.startsWith(JCuteTextUI.RUN_DIRECTORY_PREFIX)){
                    x = Integer.parseInt(selectedRun.substring(3));
                }
                JUnitTestGenerator.appendToJunitTestCase(
                        getOptionJUnitOutputFolderName(),getOptionJUnitPkgName(),
                        mainClassName,mainFunName,x,f);
            }
        }
    }

    public void continueAction(String params){
        if(!isCompiled() || isSoftCompleted() || isCompleted()){
            box.ask("Either the program is not compiled & instrumented or Testing ended");
            jcuteLog.logCall("continueAction",params);
            return;
        }
        if(!tmpLastFile.exists()){
            if(FileUtil.mkdir(tmpLastFile.getAbsolutePath())){
                box.ask("Cannot create directory "+tmpLastFile.getAbsolutePath());
                setStarted();
                //mask.enableStartAndContinue();
                jcuteLog.logCall("continueAction",params);
                return;
            }
        }
        if(FileUtil.copy(tmpDir+fileSeparator+"cuteSymbolTable",
                tmpLastFile.getAbsolutePath()+fileSeparator+"cuteSymbolTable")){
            box.ask("Cannot copy "+tmpDir+fileSeparator+"cuteSymbolTable"+" to "+
                    tmpLastFile.getAbsolutePath()+fileSeparator+"cuteSymbolTable");
            setStarted();
            jcuteLog.logCall("continueAction",params);
            return;
        }

        String v;
        if(getOptionLogLevel() ==LOG_ALL && isOptionLogPath() && isOptionLogTraceAndInput()){
            v = "-v:";
        } else {
            v ="";
        }
        String d ="-d:"+getOptionDepthForDFS() +":";
        String p;
        if(getOptionSearchStrategy() ==SEARCH_RANDOM){
            p ="-p:4:";
        } else {
            p = "-p:1:";
        }
        String j;
        if(isOptionGenerateJUnit()){
            j = "-j:";
        } else {
            j ="";
        }
        String r;
        if(isOptionUseRandomInputs()){
            r = "-r:";
        } else {
            r = "";
        }
        output.clear();

        boolean lastLogged = false;
        bc = null;
        while(!isPaused && ((exitv& Cute.EXIT_COMPLETE)==0) && runCount<getOptionNumberOfPaths()
                && (bc==null || bc.getCoverage()<100.0)){
            runCount++;
            lastLogged = false;
            if(runCount-lastBranchCoverageIncrementedAt>getOptionQuickSearchThreshold()
                    &&getOptionSearchStrategy() ==SEARCH_QUICK){
                lastBranchCoverageIncrementedAt = runCount;
                p = "-p:3:";
            } else if(getOptionSearchStrategy() ==SEARCH_QUICK){
                p = "-p:1:";
            }
            long beginTime = System.currentTimeMillis();
            exitv = runJCuteOnce(0,d,p,v,j,r,getOptionExtraOptions(),params);
            if(exitv==1) cancelAction();

            boolean flag = false;
            if(isCanceled){
                runCount--;
            }  else {
                runningTime += (System.currentTimeMillis()-beginTime);
                updateErrorCounts(exitv);
                bc = BranchCoverageLog.readCoverageLog(tmpLastFile);
                if(bc!=null){
                    int currentBranches=bc.getBranches(tmpLastFile);
                    //coverage.fillGuiWithData(bc);
                    if(currentBranches>branchCount){
                        lastBranchCoverageIncrementedAt = runCount;
                        branchCount = currentBranches;
                        flag = true;
                    }
                }
                if(isOptionGenerateJUnit() && flag){
                    JUnitTestGenerator.appendToJunitTestCase(
                            getOptionJUnitOutputFolderName(),getOptionJUnitPkgName(),
                            mainClassName,mainFunName,runCount,tmpLastFile);
                }
            }
            if(!isCanceled && (getOptionLogLevel() ==LOG_ALL ||
                    (getOptionLogLevel() ==LOG_ERRORS && isError(exitv)) ||
                    (getOptionLogLevel() ==LOG_TENTH && runCount%10==0) ||
                    (getOptionLogLevel() ==LOG_BRANCHES && flag))){
                lastLogged = true;
                if(isOptionLogPath()){
                    if(getOptionLogLevel() != LOG_ALL && isOptionLogTraceAndInput())
                        runJCuteOnce(1,"","","-v:","",r,getOptionExtraOptions(),params);

                    if(FileUtil.copyDirNew(tmpLastFile.getAbsolutePath(),
                            tmpOutput+fileSeparator+RUN_DIRECTORY_PREFIX+runCount)){
                        box.ask("Cannot copy "+tmpLastFile.getAbsolutePath()+
                                " to "+tmpOutput+fileSeparator+RUN_DIRECTORY_PREFIX+runCount);
                        setCompiled();
                        //mask.enableStart();
                        isPaused = false;
                        isCanceled = false;
                        jcuteLog.logCall("continueAction",params);
                        return;
                    } else {
                        mask.updateRunNumberListModel(String.valueOf(runCount),exitv);
                    }
                }
            } else {
                lastLogged = false;
            }
        }
        if(isPaused){
            jcuteLog.logPause();
        }
        if(!lastLogged && !isCanceled){
            if(isOptionLogPath()){
                if(getOptionLogLevel() != LOG_ALL && isOptionLogTraceAndInput()){
                    runJCuteOnce(1,"","","-v:","",r,getOptionExtraOptions(),params);
                }
                mask.updateRunNumberListModel(LAST_RUN_DIRECTORY,exitv);
            }
        }
        if((exitv&Cute.EXIT_COMPLETE)!=0 || (bc!=null && bc.getCoverage()>=99.9999999)){
            setCompleted();
        } else if(runCount>=getOptionNumberOfPaths()){
            setSoftCompleted();
        } else {
            setStarted();
        }
        isPaused = false;
        isCanceled = false;
        jcuteLog.logCall("continueAction",params);
    }

    private void updateErrorCounts(int exitv) {
        if((exitv&Cute.EXIT_ASSERT_FAILED)>0) errorCountAssertion++;
        if((exitv&Cute.EXIT_DEADLOCK)>0) errorCountDeadlock++;
        if((exitv&Cute.EXIT_ERROR)>0) errorCountException++;
        if((exitv&Cute.EXIT_RACE)>0) errorCountRace++;
        if(exitv>0 && exitv != Cute.EXIT_COMPLETE) errorCountTotal++;
    }

    public void compileAction(){
        jcuteLog.logCall("tui.compileAction();");
        if(!isCompilable()){
            box.ask("Nothing to compile. Use setCompilable() to enable compilation.");
            return;
        }
        if(!Utils.isPackageNameOk(srcDirName,srcFileName,box)){
            reset();
            setCompilable();
            //mask.enableCompileOnly();
            return;
        }
        if(mainClassNamePlusFun==null){
            box.ask("Please Select Function to be Tested");
            reset();
            setCompilable();
            //mask.enableCompileOnly();
            return;
        }
        mainClassName =
                mainClassNamePlusFun.substring(0,mainClassNamePlusFun.lastIndexOf('.'));
        mainFunName =
                mainClassNamePlusFun.substring(mainClassNamePlusFun.lastIndexOf('.')+1);
        FileUtil.deleteRecursively(tmpDir);
        if(FileUtil.mkdir(tmpClassPath)) {
            box.ask("Creation of temporary directory "+tmpClassPath+" failed!");
            reset();
            setCompilable();
            //mask.enableCompileOnly();
            return;
        }
        output.clear();

        String command1 = "javac -d " + tmpClassPath + " -sourcepath "
                + srcDirName + " " + srcFileName;
        if(CommandLine.executeOnce(command1,envp,tmpDirFile,output,this,true)!=0) {
            box.ask("Compilation of "+srcFileName+" failed! See output window.");
            reset();
            setCompilable();
            //mask.enableCompileOnly();
            return;
        }

        String command2 = "java -Xmx512m -Xms512m -Dcute.sequential=" + (new Boolean(isOptionSequential())).toString()
                + " cute.instrument.CuteInstrumenter -keep-line-number -d "
                + tmpClassPath
                //+" " + InstrumentedLib.instance.include
                +" -x com.vladium -x cute -x lpsolve --app " + mainClassName;

        if(CommandLine.executeOnce(command2,envp,tmpDirFile,output,this,true)!=0) {
            box.ask("Instrumentation of "+srcFileName+" failed! See output window.");
            reset();
            setCompilable();
            return;
        }
        setCompiled();
    }

    public void instrumentAction(){
        jcuteLog.logCall("tui.instrumentAction();");
        if(!isCompilable()){
            box.ask("Nothing to instrument.");
            return;
        }
        if(mainClassNamePlusFun==null){
            box.ask("Please Select Function to be Tested");
            reset();
            setCompilable();
            //mask.enableCompileOnly();
            return;
        }
        mainClassName =
                mainClassNamePlusFun.substring(0,mainClassNamePlusFun.lastIndexOf('.'));
        mainFunName =
                mainClassNamePlusFun.substring(mainClassNamePlusFun.lastIndexOf('.')+1);
        FileUtil.deleteRecursively(tmpDir);
        if(FileUtil.mkdir(tmpClassPath)) {
            box.ask("Creation of temporary directory "+tmpClassPath+" failed!");
            reset();
            setCompilable();
            //mask.enableCompileOnly();
            return;
        }
        output.clear();

        String command2 = "java -Xmx512m -Xms512m -Dcute.sequential=" + (new Boolean(isOptionSequential())).toString()
                + " cute.instrument.CuteInstrumenter -keep-line-number -d "
                + tmpClassPath
                //+" " + InstrumentedLib.instance.include
                +" -x com.vladium -x cute -x lpsolve --app " + mainClassName;

        if(CommandLine.executeOnce(command2,envp,tmpDirFile,output,this,true)!=0) {
            box.ask("Instrumentation of "+srcFileName+" failed! See output window.");
            reset();
            setCompilable();
            return;
        }
        setCompiled();
    }

    public void deleteAction(){
        jcuteLog.logCall("tui.deleteAction();");
        FileUtil.deleteRecursively(tmpOutput);

        reset();
        setCompiled();

        JUnitTestGenerator.deleteJunitTestCase(getOptionJUnitOutputFolderName(),
                getOptionJUnitPkgName(),mainClassName,mainFunName);
    }

    public void cancelAction() {
        if(proc!=null){
            proc.destroy();
        }
        isPaused = true;
        isCanceled = true;
    }

    public void pauseAction() {
        isPaused = true;
    }


    public void quitAction() {
        jcuteLog.logExit();
        FileUtil.deleteRecursively(getTmpDir());
    }

    public void setProcess(Process proc) {
        this.proc = proc;
    }

    private boolean isCompilable = false;
    private boolean isCompiled = false;
    private boolean isTestStarted = false;
    private boolean isSoftCompleted = false;
    private boolean isCompleted = false;

    public boolean isCompilable() {
        return isCompilable;
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public boolean isTestStarted() {
        return isTestStarted;
    }

    public boolean isSoftCompleted() {
        return isSoftCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private void setInit(){
        isCompilable = false;
        isCompiled = false;
        isTestStarted = false;
        isSoftCompleted = false;
        isCompleted = false;
    }

    private void setCompilable(){
        isCompilable = true;
        isCompiled = false;
        isTestStarted = false;
        isSoftCompleted = false;
        isCompleted = false;
    }

    public void setCompilableExt(){
        jcuteLog.logCall("tui.setCompilableExt();");
        setCompilable();
    }

    private void setCompiled(){
        isCompilable = true;
        isCompiled = true;
        isTestStarted = false;
        isSoftCompleted = false;
        isCompleted = false;
    }

    private void setStarted(){
        isCompilable = true;
        isCompiled = true;
        isTestStarted = true;
        isSoftCompleted = false;
        isCompleted = false;
    }

    /**
     * called when testing has not explored all feasible execution paths, but
     * total number of paths explored is greater than or equal to the
     * number of paths that we want to explore
     */
    private void setSoftCompleted(){
        isCompilable = true;
        isCompiled = true;
        isTestStarted = true;
        isSoftCompleted = true;
        isCompleted = false;
    }

    /**
     * called when testing has explored all feasible execution paths
     */
    private void setCompleted(){
        isCompilable = true;
        isCompiled = true;
        isTestStarted = true;
        isSoftCompleted = false;
        isCompleted = true;
    }

    public void updateSoftCompleted() {
        jcuteLog.logCall("tui.updateSoftCompleted();");
        if(getOptionNumberOfPaths() >runCount && isSoftCompleted()){
            setStarted();
        } else if(getOptionNumberOfPaths() <= runCount && isTestStarted() && !isCompleted()){
            setSoftCompleted();
        }
    }

    public String getJUnitFileName() {
        File f = JUnitTestGenerator.getJUnitFileName(getOptionJUnitOutputFolderName(),
                mainClassName,mainFunName);
        if(f!=null && f.exists())
            return f.getAbsolutePath();
        return null;
    }

    public boolean isOptionPrintOutput() {
        return optionPrintOutput;
    }

    public void setOptionPrintOutput(boolean optionPrintOutput) {
        this.optionPrintOutput = optionPrintOutput;
        jcuteLog.logCall("setOptionPrintOutput",optionPrintOutput);
    }

    public boolean isOptionLogPath() {
        return optionLogPath;
    }

    public void setOptionLogPath(boolean optionLogPath) {
        this.optionLogPath = optionLogPath;
        jcuteLog.logCall("setOptionLogPath",optionLogPath);
    }

    public boolean isOptionLogTraceAndInput() {
        return optionLogTraceAndInput;
    }

    public void setOptionLogTraceAndInput(boolean optionLogTraceAndInput) {
        this.optionLogTraceAndInput = optionLogTraceAndInput;
        jcuteLog.logCall("setOptionLogTraceAndInput",optionLogTraceAndInput);
    }

    public boolean isOptionGenerateJUnit() {
        return optionGenerateJUnit;
    }

    public void setOptionGenerateJUnit(boolean optionGenerateJUnit) {
        this.optionGenerateJUnit = optionGenerateJUnit;
        jcuteLog.logCall("setOptionGenerateJUnit",optionGenerateJUnit);
    }

    public String getOptionExtraOptions() {
        return optionExtraOptions;
    }

    public void setOptionExtraOptions(String optionExtraOptions) {
        this.optionExtraOptions = optionExtraOptions;
        jcuteLog.logCall("setOptionExtraOptions",optionExtraOptions);
    }

    public String getOptionJUnitOutputFolderName() {
        return optionJUnitOutputFolderName;
    }

    public void setOptionJUnitOutputFolderName(String optionJUnitOutputFolderName) {
        this.optionJUnitOutputFolderName = optionJUnitOutputFolderName;
        jcuteLog.logCall("setOptionJUnitOutputFolderName",optionJUnitOutputFolderName);
    }

    public String getOptionJUnitPkgName() {
        return optionJUnitPkgName;
    }

    public void setOptionJUnitPkgName(String optionJUnitPkgName) {
        this.optionJUnitPkgName = optionJUnitPkgName;
        jcuteLog.logCall("setOptionJUnitPkgName",optionJUnitPkgName);
    }

    public int getOptionNumberOfPaths() {
        return optionNumberOfPaths;
    }

    public void setOptionNumberOfPaths(int optionNumberOfPaths) {
        this.optionNumberOfPaths = optionNumberOfPaths;
        jcuteLog.logCall("setOptionNumberOfPaths",optionNumberOfPaths);
    }

    public int getOptionLogLevel() {
        return optionLogLevel;
    }

    public void setOptionLogLevel(int optionLogLevel) {
        this.optionLogLevel = optionLogLevel;
        jcuteLog.logCall("setOptionLogLevel",optionLogLevel);
    }

    public int getOptionDepthForDFS() {
        return optionDepthForDFS;
    }

    public void setOptionDepthForDFS(int optionDepthForDFS) {
        this.optionDepthForDFS = optionDepthForDFS;
        jcuteLog.logCall("setOptionDepthForDFS",optionDepthForDFS);
    }

    public int getOptionSearchStrategy() {
        return optionSearchStrategy;
    }

    public void setOptionSearchStrategy(int optionSearchStrategy) {
        this.optionSearchStrategy = optionSearchStrategy;
        jcuteLog.logCall("setOptionSearchStrategy",optionSearchStrategy);
    }

    public boolean isOptionSequential() {
        return optionSequential;
    }

    public void setOptionSequential(boolean optionSequential) {
        this.optionSequential = optionSequential;
        jcuteLog.logCall("setOptionSequential",optionSequential);
    }

    public int getOptionQuickSearchThreshold() {
        return optionQuickSearchThreshold;
    }

    public void setOptionQuickSearchThreshold(int optionQuickSearchThreshold) {
        this.optionQuickSearchThreshold = optionQuickSearchThreshold;
        jcuteLog.logCall("setOptionQuickSearchThreshold",optionQuickSearchThreshold);
    }

    public boolean isOptionLogRace() {
        return optionLogRace;
    }

    public void setOptionLogRace(boolean optionLogRace) {
        this.optionLogRace = optionLogRace;
        jcuteLog.logCall("setOptionLogRace",optionLogRace);
    }

    public boolean isOptionLogDeadlock() {
        return optionLogDeadlock;
    }

    public void setOptionLogDeadlock(boolean optionLogDeadlock) {
        this.optionLogDeadlock = optionLogDeadlock;
        jcuteLog.logCall("setOptionLogDeadlock",optionLogDeadlock);
    }

    public boolean isOptionLogException() {
        return optionLogException;
    }

    public void setOptionLogException(boolean optionLogException) {
        this.optionLogException = optionLogException;
        jcuteLog.logCall("setOptionLogException",optionLogException);
    }

    public boolean isOptionLogAssertion() {
        return optionLogAssertion;
    }

    public void setOptionLogAssertion(boolean optionLogAssertion) {
        this.optionLogAssertion = optionLogAssertion;
        jcuteLog.logCall("setOptionLogAssertion",optionLogAssertion);
    }

    public boolean isOptionUseRandomInputs() {
        return optionUseRandomInputs;
    }

    public void setOptionUseRandomInputs(boolean optionUseRandomInputs) {
        this.optionUseRandomInputs = optionUseRandomInputs;
    }
}
