package cute.concolic.input;

import cute.concolic.Globals;

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

public class InputElement implements Serializable {
    public Object val=null;
    public Object next=null;
    public int type;
    public int id;

    public InputElement() {
    }

    public InputElement(Object val, Object next, int type, int id) {
        this.val = val;
        this.next = next;
        this.type = type;
        this.id = id;
    }

    public InputElement makeSerializable() {
        InputElement ret = new InputElement(val,next,type,id);
        if(next!=null){
            ret.next = new ReferenceObject(((InputElement)next).id);
        }
        if(type == Globals.REFERENCE && val!=null && val instanceof InputElement){
            ret.val = new ReferenceObject(((InputElement)val).id);
        }
        return  ret;
    }

    public void print(PrintWriter out) {
        out.println("id = " + id);
        if(next==null){
            out.println(" next = null");
        } else if(next instanceof InputElement){
            out.println(" next = " + (((InputElement)next).id));
        } else {
            out.println(" next = " + next);
        }
        switch(type){
            case Globals.INT:
                out.print(" int ");
                out.println(((Integer)val).intValue());
                break;
            case Globals.SHORT:
                out.print(" short ");
                out.println(((Short)val).shortValue());
                break;
            case Globals.LONG:
                out.print(" long ");
                out.println(((Long)val).longValue());
                break;
            case Globals.BYTE:
                out.print(" byte ");
                out.println(((Byte)val).byteValue());
                break;
            case Globals.CHAR:
                out.print(" char ");
                out.println(((Character)val).charValue());
                break;
            case Globals.FLOAT:
                out.print(" float ");
                out.println(((Float)val).floatValue());
                break;
            case Globals.DOUBLE:
                out.print(" double ");
                out.println(((Double)val).doubleValue());
                break;
            case Globals.BOOLEAN:
                out.print(" boolean ");
                out.println(((Boolean)val).booleanValue());
                break;
            case Globals.OBJECT:
                out.print(" Object ");
                out.println(val);
                break;
            case Globals.REFERENCE:
                if(val==null){
                    out.println(" Reference null");
                } else if(val == Dumbo.val){
                    out.println(" Reference uninitialized");
                } else if(val instanceof InputElement){
                    out.print(" Reference ");
                    out.println(((InputElement)val).id);
                } else {
                    out.print(" Reference to be initialized by ");
                    out.println(val);
                }
        }
    }

}
