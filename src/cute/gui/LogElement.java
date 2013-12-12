package cute.gui;

import java.awt.*;
import java.util.HashMap;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Oct 31, 2005
 * Time: 12:43:09 PM
 */
public class LogElement implements ListElement {
    private String toShow;
    private String fileName;
    private int lineNo;
    private String tName;
    private static HashMap map = new HashMap();
    private Float f;

    public LogElement(String str) {
        String[] strs = str.split("@");
        toShow = strs[0];
        String[] strs2 = strs[1].split(":");
        fileName = strs2[0];
        lineNo = Integer.parseInt(strs2[1]);
        tName = strs[2];
        toShow = toShow +" in "+tName;
        f = (Float)map.get(tName);
        if(f==null){
            f = new Float((((map.size()+1)*29)%53)/53.0f);
            map.put(tName,f);
        }
    }

    public String toString() {
        return toShow;
    }

    public String getFName() {
        return fileName;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getName() {
        return toShow;
    }

    public Color getColor(Color background) {
        return Color.getHSBColor(f.floatValue(),0.3f,1.0f);
    }

    public String getToolTip() {
        return new StringBuffer().append(tName).append(":")
                .append(fileName).append(":").append(lineNo).toString();
    }

}
