package cute.concolic;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 27, 2006
 * Time: 5:18:34 PM
 */
public class Information {
    public boolean random;
    public int searchMode;
    public int mode;
    public int optimized;

    public int depth;
    public int seed;
    public int debugLevel;
    public int brackTrackAt;
    public int nThreads;
    public boolean optimalDistributed;
    public boolean solved;

    public boolean printTraceAndInputs;
    public boolean suddenExit;
    public int returnVal;
    public boolean generateJUnit;
    public long randomThreshold = 25;

    public Information() {
        optimized = 7;
        returnVal = 0;
        solved = false;
        printTraceAndInputs = false;
        suddenExit = false;
    }

}
