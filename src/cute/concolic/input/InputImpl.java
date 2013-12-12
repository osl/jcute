package cute.concolic.input;

import cute.Input;
import cute.concolic.Globals;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 16, 2005
 * Time: 9:19:09 AM
 */
public class InputImpl implements Input {

    public Object Object(String className){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return Globals.globals.input.ObjectAux(className,true);
    }

    public  Object ObjectShallow(String className){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return Globals.globals.input.ObjectAux(className,false);
    }

    public int Integer(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Integer)Globals.globals.input.myInput(Globals.INT, null)).intValue();
    }

    public  short Short(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Short)Globals.globals.input.myInput(Globals.SHORT, null)).shortValue();
    }

    public  long Long(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Long)Globals.globals.input.myInput(Globals.LONG, null)).longValue();
    }

    public  byte Byte(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Byte)Globals.globals.input.myInput(Globals.BYTE, null)).byteValue();
    }

    public  char Character(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Character)Globals.globals.input.myInput(Globals.CHAR, null)).charValue();
    }

    public  float Float(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Float)Globals.globals.input.myInput(Globals.FLOAT, null)).floatValue();
    }

    public  double Double(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Double)Globals.globals.input.myInput(Globals.DOUBLE, null)).doubleValue();
    }

    public  boolean Boolean(){
        if(!Globals.globals.initialized){
            Globals.globals.begin();
        }
        return ((Boolean)Globals.globals.input.myInput(Globals.BOOLEAN, null)).booleanValue();
    }

}
