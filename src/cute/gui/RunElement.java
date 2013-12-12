package cute.gui;

import cute.Cute;

import java.awt.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 3, 2005
 * Time: 10:36:25 AM
 */
public class RunElement implements ListElement{
    private String name;
    private int err;
    private String toolTip;

    public RunElement(String name, int err) {
        this.name = name;
        this.err = err;
        if(err>0 && err!=Cute.EXIT_COMPLETE){
            StringBuffer sb = new StringBuffer();
            sb.append(((err& Cute.EXIT_DEADLOCK)>0)?"Deadlock":"");
            if((err&Cute.EXIT_RACE)>0){
                if(sb.length()>0) sb.append(" and ");
                sb.append("Data-Race");
            }
            if((err&Cute.EXIT_ERROR)>0){
                if(sb.length()>0) sb.append(" and ");
                sb.append("Uncaught Exception");
            }
            if((err&Cute.EXIT_ASSERT_FAILED)>0){
                if(sb.length()>0) sb.append(" and ");
                sb.append("Assertion Failed");
            }
            toolTip= sb.toString();
        } else {
            toolTip = "No Error";
        }
    }

    public String toString() {
        if(err>0 && (err!= Cute.EXIT_COMPLETE))
            return name+"*["+(((err&Cute.EXIT_DEADLOCK)>0)?"D":"")
                    +(((err&Cute.EXIT_RACE)>0)?"R":"")
                    +(((err&Cute.EXIT_ERROR)>0)?"E":"")+"]";
        else
            return name;
    }

    public int getErr() {
        return err;
    }

    public String getName() {
        return name;
    }

    public Color getColor(Color background) {
        if(err>0 && err!=Cute.EXIT_COMPLETE){
            if((err&Cute.EXIT_ASSERT_FAILED)>0)
                return Color.getHSBColor(0.0f,0.3f,1.0f);
            if((err&Cute.EXIT_DEADLOCK)>0)
                return Color.getHSBColor(0.05f,0.6f,1.0f);
            if((err&Cute.EXIT_ERROR)>0)
                return Color.getHSBColor(0.12f,0.5f,1.0f);
            return Color.getHSBColor(0.14f,0.3f,1.0f);
        }
        else
            return background;
    }

    public String getToolTip() {
        return toolTip;
    }
}
