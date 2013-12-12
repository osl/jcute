package cute.concolic.input;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 19, 2006
 * Time: 9:46:25 AM
 */
public class ReferenceObject implements Serializable {
    public int val;

    public ReferenceObject(int id) {
        val = id;
    }

    public String toString() {
        return ""+val;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
