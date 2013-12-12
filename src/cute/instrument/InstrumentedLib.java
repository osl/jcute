package cute.instrument;

import java.util.Vector;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 25, 2006
 * Time: 11:01:39 AM
 */
public class InstrumentedLib {
    public String[] libsDotStyle;
    public String[] libsFolderStyle;
    public String include;
    public static InstrumentedLib instance = new InstrumentedLib();

    public InstrumentedLib(){
        Vector list = new Vector();
        list.add("java.lang.String");
        list.add("java.lang.StringBuffer");
        list.add("java.lang.AbstractStringBuilder");
        list.add("java.lang.String");
        list.add("java.lang.StringBuilder");
        list.add("java.lang.StringCoding");
        list.add("java.lang.Boolean");
        list.add("java.lang.Byte");
        list.add("java.lang.Short");
        list.add("java.lang.Integer");
        list.add("java.lang.Long");
        list.add("java.lang.Character");
        list.add("java.util");

        libsDotStyle = new String[list.size()];
        libsFolderStyle = new String[list.size()];

        StringBuffer sb = new StringBuffer();
        int i =0;
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();
            libsDotStyle[i] = s;
            libsFolderStyle[i] = s.replace('.','/');
            sb.append("-i ");
            sb.append(s);
            i++;
        }
        include = sb.toString();
    }

}
