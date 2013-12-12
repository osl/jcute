package dstest;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 21, 2005
 * Time: 10:25:37 AM
 */
public class SimpleObject implements Comparable {
    public int v;

    public SimpleObject() {
    }

    public SimpleObject(int v) {
        this.v = v;
    }

    public int hashCode() {
        return v;
    }

    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null) return false;
        return(this.v == ((SimpleObject)obj).v);
    }

    public int compareTo(Object o) {
        return ((SimpleObject)this).v - ((SimpleObject)o).v;
    }
}
