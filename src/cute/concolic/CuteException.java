package cute.concolic;


/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

public class CuteException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1813467350937697145L;
	private Throwable prev = null;

    public CuteException(String message) {
        super(message);
    }

    public CuteException(String message, Throwable t) {
        super(message);
        prev = t;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append(getMessage());
        ret.append("\n");
        StackTraceElement[] els = getStackTrace();
        for (int i = 0; i < els.length; i++) {
            ret.append("   at: ");
            ret.append(els[i]);
            ret.append("\n");
        }
        if (prev != null) {
            ret.append("\n");
            ret.append(prev.getMessage());
            ret.append("\n");
            els = prev.getStackTrace();
            for (int i = 0; i < els.length; i++) {
                ret.append("   at: ");
                ret.append(els[i]);
                ret.append("\n");
            }
        }
        return ret.toString();
    }
}
