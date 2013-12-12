package cute.instrument;

import soot.SootClass;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:40:10 PM
 */
public class Utils {
    public static boolean isRunnableSubType(SootClass c) {
        if(c.implementsInterface("java.lang.Runnable"))
            return true;
        if(c.hasSuperclass())
            return isRunnableSubType(c.getSuperclass());
        return false;
    }

    public static boolean isThreadSubType(SootClass c) {
        if(c.getName().equals("java.lang.Thread"))
            return true;
        if(!c.hasSuperclass()){
            return false;
        }
        return isThreadSubType(c.getSuperclass());
    }

}
