package cute.instrument;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1721540850266275071L;
    private HashMap st;
    private HashMap revMap;


    public SymbolTable() {
        this.st = new HashMap();
    }

    public int get(String s){
        Integer ret = (Integer)st.get(s);
        if(ret!= null) return  ret.intValue();
        int i = st.size()+1;
        st.put(s,new Integer(i));
        return i;
    }

    public String toString() {
        return st.toString();
    }

    public void reverseMap() {
        revMap = new HashMap();
        for (Iterator iterator = st.keySet().iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            revMap.put(st.get(o),o);
        }
    }

    public String getReverse(int i){
        return (String)revMap.get(new Integer(i));
    }
}
