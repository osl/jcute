package cute.concolic.input;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 19, 2006
 * Time: 10:21:50 AM
 */
public class AnyObject implements Serializable {
    public static AnyObject val = new AnyObject();

    public String toString() {
        return "(object follows)";
    }
}
