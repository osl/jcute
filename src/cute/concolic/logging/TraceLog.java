package cute.concolic.logging;

import java.util.Vector;
import java.util.Iterator;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jan 2, 2006
 * Time: 10:43:41 AM
 */
public class TraceLog {
    private String line;
    private Vector beginRace;
    private Vector endRace;

    public TraceLog(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public void addBeginRace(int i) {
        if(beginRace==null){
            beginRace = new Vector(2);
        }
        beginRace.add(new Integer(i));
    }

    public void addEndRace(int i) {
        if(endRace==null){
            endRace = new Vector(2);
        }
        endRace.add(new Integer(i));
    }

    public void print(PrintWriter out){
        StringBuffer line = new StringBuffer();
        if(beginRace!=null || endRace!=null){
            line.append("[Race ");
            if(beginRace!=null){
                for (Iterator iterator = beginRace.iterator(); iterator.hasNext();) {
                    Integer integer = (Integer) iterator.next();
                    line.append(integer);
                    line.append("v");
                    if(iterator.hasNext()){
                        line.append(',');
                    }
                }
            }
            if(endRace!=null){
                if(beginRace!=null){
                    line.append(',');
                }
                for (Iterator iterator = endRace.iterator(); iterator.hasNext();) {
                    Integer integer = (Integer) iterator.next();
                    line.append(integer);
                    line.append("^");
                    if(iterator.hasNext()){
                        line.append(',');
                    }
                }
            }
            line.append("]");
        } else {
            line.append("---");
        }
        line.append(" : ");
        line.append(this.line);
        out.print(line.toString());
    }
}
