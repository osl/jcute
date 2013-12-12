package cute.concolic.logging;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jan 2, 2006
 * Time: 11:13:40 AM
 */
public class RaceLog extends Object {
    public int begin;
    public int end;

    public RaceLog(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public String toString() {
        return "race at "+begin + " and "+end; 
    }
}
