package cute.concolic.input;

import cute.concolic.Call;
import cute.concolic.Globals;
import cute.concolic.Information;
import cute.concolic.generateinputandschedule.PointerSolver;
import cute.concolic.logging.ExecutionLog;
import cute.concolic.logging.JUnitTestGenerator;
import cute.concolic.logging.Logger;
import cute.concolic.logging.Printable;
import cute.concolic.symbolicstate.*;
import cute.instrument.SymbolTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;


/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

public class InputMap implements Printable {
    public InputElement input=null;
    public InputElement currInput = null;
    private InputElement prevInput = null;
    public Vector symbolicArithInputValue;

    private Information information;
    private Logger logger;
    private JUnitTestGenerator junitTest;
    private State state;
    private ExecutionLog ptrace;
    private SymbolTable st;
    private Random rand;

    public InputMap(Information information, Logger logger, JUnitTestGenerator junitTest,
                    State state, ExecutionLog ptrace, SymbolTable st, Random rand) {
        this.information = information;
        this.logger = logger;
        this.junitTest = junitTest;
        this.state = state;
        this.ptrace = ptrace;
        this.st = st;
        this.rand = rand;
    }

    public int nSymbolicArithInputValues(){
        return symbolicArithInputValue.size();
    }

    public int putAndGetArithExpression(InputElement val) {
        int x = nSymbolicArithInputValues()+1;
        symbolicArithInputValue.add(val);
        return x;
    }

    public void updateArithInput(int j, double v) {
        InputElement tmp = (InputElement)(symbolicArithInputValue.get(j-1));
        if(tmp.val instanceof FunctionExpression){
            ((FunctionExpression)tmp.val).solve(v,this);
        } else {
            switch(tmp.type){
                case Globals.INT:
                    tmp.val = new Integer((int)(v+(v>0.1?0.2:-0.2)));
                    break;
                case Globals.SHORT:
                    tmp.val = new Short((short)(v+(v>0.1?0.2:-0.2)));
                    break;
                case Globals.LONG:
                    tmp.val = new Long((long)(v+(v>0.1?0.2:-0.2)));
                    break;
                case Globals.BYTE:
                    tmp.val = new Byte((byte)(v+(v>0.1?0.2:-0.2)));
                    break;
                case Globals.CHAR:
                    tmp.val = new Character((char)(v+(v>0.1?0.2:-0.2)));
                    break;
                case Globals.BOOLEAN:
                    tmp.val = new Boolean(v>0.5);
                    break;
                case Globals.FLOAT:
                    tmp.val = new Float((float)v);
                    break;
                case Globals.DOUBLE:
                    tmp.val = new Double((double)v);
                    break;
            }
        }
    }

    public double getArithInput(int j) {
        InputElement tmp = (InputElement)(symbolicArithInputValue.get(j-1));
        switch(tmp.type){
            case Globals.INT:
                return ((Integer)tmp.val).doubleValue();
            case Globals.SHORT:
                return ((Short)tmp.val).doubleValue();
            case Globals.LONG:
                return ((Long)tmp.val).doubleValue();
            case Globals.BYTE:
                return ((Byte)tmp.val).doubleValue();
            case Globals.CHAR:
                return ((Character)tmp.val).charValue();
            case Globals.BOOLEAN:
                return ((Boolean)tmp.val).booleanValue()?1.0:0.0;
            case Globals.FLOAT:
                return ((Float)tmp.val).doubleValue();
            case Globals.DOUBLE:
                return ((Double)tmp.val).doubleValue();
        }
        System.exit(1);
        return 0.0;
    }

    public int symbolicArithInputType(int i) {
        return ((InputElement)(symbolicArithInputValue.get(i-1))).type;
    }


    public Vector symbolicPointerInputValue;

    public int nSymbolicPointerInputValues(){
        return symbolicPointerInputValue.size();
    }

    public int putAndGetPointerExpression(InputElement val) {
        int x = nSymbolicPointerInputValues()+1;
        symbolicPointerInputValue.add(val);
        return x;
    }

    public InputElement getPointerElement(int i) {
        return (InputElement)(symbolicPointerInputValue.get(i-1));
    }


    public void updatePointerInput(PointerSolver ps,int s, int b, boolean isEq) {
        int i;
        int s_points_to = s;
        int b_points_to = b;

        if (isEq) {
            if (s_points_to==0) {
                getPointerElement(b_points_to).val = null;
            } else if (getPointerElement(s_points_to).val == null) {
                if (ps.isEqual(s,0)) {
                    for (i=1;i<ps.nodes.length;i++) {
                        if (ps.isEqual(b,i)) {
                            b_points_to = i;
                            getPointerElement(b_points_to).val = null;
                        }
                    }
                } else {
                    getPointerElement(s_points_to).val
                            = getPointerElement(b_points_to).val;
                }
            } else if (getPointerElement(b_points_to).val == null) {
                if (ps.isEqual(b,0)) {
                    for (i=1;i<ps.nodes.length;i++) {
                        if (ps.isEqual(s,i)) {
                            s_points_to = i;
                            getPointerElement(s_points_to).val = null;
                        }
                    }
                } else {
                    getPointerElement(b_points_to).val
                            = getPointerElement(s_points_to).val;
                }
            } else {
                for (i=1;i<ps.nodes.length;i++) {
                    if (ps.isEqual(b,i)) {
                        b_points_to = i;
                        getPointerElement(b_points_to).val
                                = getPointerElement(s_points_to).val;
                    }
                }
                System.err.println("Making two non-NULL pointers equal");
            }
        } else {
            if (s_points_to==0) {
                getPointerElement(b_points_to).val = Dumbo.val;
            } else if (getPointerElement(s_points_to).val == null) {
                if (ps.isEqual(s,0)) {
                    getPointerElement(b_points_to).val = Dumbo.val;
                } else {
                    getPointerElement(s_points_to).val = Dumbo.val;
                }
            } else if (getPointerElement(b_points_to).val == null) {
                if (ps.isEqual(b,0)) {
                    getPointerElement(s_points_to).val = Dumbo.val;
                } else {
                    getPointerElement(b_points_to).val = Dumbo.val;
                }
            } else {
                getPointerElement(b_points_to).val = Dumbo.val;
                System.err.println("Making two non-NULL pointers unequal");
            }
        }
    }


    public void read() {
        cute.concolic.ObjectInput in = new cute.concolic.ObjectInput("Inputs",true,information);
        Vector tmp = new Vector();
        symbolicArithInputValue = new Vector();
        symbolicPointerInputValue = new Vector();
        if(in.ok()){
            InputElement elt;
            try {
                int sz = in.in.readInt();
                for(int i=0;i<sz;i++){
                    elt = (InputElement)in.in.readObject();
                    elt.id=tmp.size();
                    tmp.add(elt);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            } catch(ClassNotFoundException e2){
                e2.printStackTrace();
                System.exit(1);
            }
            for (Iterator iterator = tmp.iterator(); iterator.hasNext();) {
                InputElement inputElement = (InputElement) iterator.next();
                if(inputElement.type==Globals.REFERENCE){
                    if(inputElement.val!=null && inputElement.val instanceof ReferenceObject){
                        inputElement.val = tmp.get(((ReferenceObject)inputElement.val).val);
                    } else if(inputElement.val!=null && inputElement.val instanceof Dumbo){
                        inputElement.val = Dumbo.val;
                    }
                }
                if(inputElement.next!=null && inputElement.next instanceof ReferenceObject){
                    inputElement.next = tmp.get(((ReferenceObject)inputElement.next).val);
                }
            }
            prevInput = null;
            currInput = input = (InputElement)tmp.get(0);
            if((information.debugLevel&4)!=0) logger.info(4,null,this);
            in.close();
        } else {
            prevInput = currInput = input = null;
        }
    }

    private void linearizeInput(IdentityHashMap seen, Vector v, InputElement elt){
        InputElement tmp = elt;
        while(tmp!=null){
            v.add(tmp);
            seen.put(tmp,tmp);
            tmp.id = v.size()-1;
            if(tmp.val instanceof InputElement && !seen.containsKey(tmp.val)){
                linearizeInput(seen, v,(InputElement)tmp.val);
            }
            tmp = (InputElement)tmp.next;
        }
    }

    public void write(){
        if(input==null) return;
        cute.concolic.ObjectOutput out = new cute.concolic.ObjectOutput("Inputs");
        if(out.ok()){
            Vector arr = new Vector();
            IdentityHashMap seen = new IdentityHashMap();
            linearizeInput(seen,arr,input);
            try {
                out.out.writeInt(arr.size());
                for (Iterator iterator = arr.iterator(); iterator.hasNext();) {
                    InputElement inputElement = (InputElement) iterator.next();
                    out.out.writeObject(inputElement.makeSerializable());
                }
            } catch(IOException e2){
                e2.printStackTrace();
                System.exit(1);
            }
            out.close();
        }
    }

    public void print(PrintWriter out){
        Vector arr = new Vector();
        IdentityHashMap seen = new IdentityHashMap();
        linearizeInput(seen,arr,input);
        out.println("-------------------");
        out.println("Input");
        out.println("-------------------");
        for (Iterator iterator = arr.iterator(); iterator.hasNext();) {
            InputElement inputElement = (InputElement) iterator.next();
            inputElement.print(out);
        }
        out.println("-------------------");
        out.flush();
    }


    public boolean isInputAvailable(int type) {
        if(currInput==null)
            return false;
        if(currInput.type!=type){
            currInput = null;
            return false;
            //System.err.println("CUTE: input seems to be corrupted");
            //System.exit(1);
        }
        return true;
    }

    /**
     * Inserts val in the list {@link #input} by creating a new {@link InputElement} object.
     * {@link #prevInput} points to the newly created object.
     * {@link #currInput} is always set to null.
     * {@link #input} points to the head of the list.
     * @param val
     * @param type
     * @return the new created object.
     */
    public InputElement setInput(Object val,int type) {
        InputElement elt = new InputElement();
        elt.next = null;
        elt.type = type;
        elt.val = val;
        if(prevInput!=null){
            prevInput.next = elt;
        }
        prevInput = elt;
        currInput = null;
        if(input==null){
            input = elt;
        }
        return elt;
    }

    public void pushCurrInput(InputElement elt) {
        prevInput = null;
        if(elt.val == Dumbo.val) {
            currInput = null;
        } else {
            currInput = (InputElement)elt.val;
        }
    }

    public void popCurrInput(InputElement elt) {
        this.prevInput = elt;
        this.currInput = (InputElement)elt.next;
    }

    public InputElement getInput() {
        prevInput = currInput;
        currInput = (InputElement)currInput.next;
        return prevInput;
    }

    private IdentityHashMap initialized = new IdentityHashMap();

    public boolean isAlreadyInitialized(InputElement inputElement) {
        return initialized.containsKey(inputElement);
    }

    public Object getInitialized(InputElement inputElement) {
        return initialized.get(inputElement);
    }

    public void setInitialized(InputElement inputElement, Object ret) {
        initialized.put(inputElement,ret);
    }

    public Object ObjectAux(String className,boolean recursive){
        Class c = null;
        junitTest.assignToInput(className);
        if((information.debugLevel&1)!=0) logger.info(1,"Input Class Begin",null);
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Object tmp = input(c,0,recursive);
        chooseNullAndNonullValues(tmp);
        if((information.debugLevel&1)!=0) logger.info(1,"Input Class End",null);
        return tmp;
    }

    final static int SOMELOCALVAR = 2;
    final static int NOLINENUMBER = -1;

    private void chooseNullAndNonullValues(Object tmp){
        Call.loadAddress(0,SOMELOCALVAR,NOLINENUMBER);
        Call.loadValue(tmp,NOLINENUMBER);
        Call.loadValue(null,NOLINENUMBER);
        Call.applyOp("==",NOLINENUMBER);
        if(tmp==null)
            Call.branchPos(null,-1,-1,NOLINENUMBER);
        else
            Call.branchNeg(null,-1,-1,NOLINENUMBER);
    }

    private void pushExpression(Expression e){
        state.setState(SOMELOCALVAR,e);
        Call.popAll(NOLINENUMBER);
        Call.setReturn(0,SOMELOCALVAR,NOLINENUMBER);
    }

    private Object initializeInputObjectToRet(String className,InputElement val,Object ret){

        if(information.printTraceAndInputs){
            ptrace.printInputLn(System.identityHashCode(ret)+"("+className+")");
        }
        if(ret == null)
            junitTest.valueObjectNull();
        else
            junitTest.valueObject(System.identityHashCode(ret),className);
        pushExpression(new PointerExpression(putAndGetPointerExpression(val)));
        return ret;
    }

    private void initializeInputObjectFields(Class c,Object ret,int tab,String varName,boolean recursive){
        Field[] field = c.getFields();
        for (int i = 0; i < field.length; i++) {
            Field field1 = field[i];
            try {
                int mod = field1.getModifiers();
                if(!Modifier.isPrivate(mod) && !Modifier.isProtected(mod)
                        && !Modifier.isStatic(mod) && !Modifier.isFinal(mod)){
                    String type = field1.getType().toString();
                    if(information.printTraceAndInputs){
                        for(int l=0;l<tab+1;l++)
                            ptrace.printInput("  ");
                        ptrace.printInput(field1.getName());
                        ptrace.printInput("=");
                    }
                    if(!type.startsWith("class")){
                        junitTest.assignTo(varName,field1.getName(),type);
                    }
                    Call.funBegin(null,NOLINENUMBER);
                    if(type.equals("int")){
                        field1.setInt(ret,((Integer)myInputWithoutAssignTo(Globals.INT, null)).intValue());
                    } else if(type.equals("short")){
                        field1.setShort(ret,((Short)myInputWithoutAssignTo(Globals.SHORT, null)).shortValue());
                    } else if(type.equals("byte")) {
                        field1.setByte(ret,((Byte)myInputWithoutAssignTo(Globals.BYTE, null)).byteValue());
                    } else if(type.equals("char")) {
                        field1.setChar(ret,((Character)myInputWithoutAssignTo(Globals.CHAR, null)).charValue());
                    } else if(type.equals("long")){
                        field1.setLong(ret,((Long)myInputWithoutAssignTo(Globals.LONG, null)).longValue());
                    } else if(type.equals("boolean")){
                        field1.setBoolean(ret,((Boolean)myInputWithoutAssignTo(Globals.BOOLEAN, null)).booleanValue());
                    } else if(type.equals("float")){
                        field1.setFloat(ret,((Float)myInputWithoutAssignTo(Globals.FLOAT, null)).floatValue());
                    } else if(type.equals("double")){
                        field1.setDouble(ret,((Double)myInputWithoutAssignTo(Globals.DOUBLE, null)).doubleValue());
                    } else if(type.startsWith("class")){
                        junitTest.assignTo(varName,field1.getName(),type.substring(6));
                        field1.set(ret,input(field1.getType(),tab+1,recursive));
                    }
                    Call.funEnd(NOLINENUMBER);
                    Call.storeReturn(ret,st.get(field1.getName()),NOLINENUMBER);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

    private void exitIfNonInstantiable(Class c){
        int cmod = c.getModifiers();
        if(Modifier.isAbstract(cmod) || Modifier.isInterface(cmod)){
            System.err.println("CUTE: Input type is not instantiable");
            System.exit(1);
        }
    }

    private Object newInstance(Class c){
        Object ret = CustomInitializer.getNewInstance(c,information.random,rand);
        if(ret==null){
            try {
                return c.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
        return ret;
    }

    private  Object input(Class c,int tab,boolean recursive){
        Object ret=null;
        InputElement val;
        String varName=null;

        exitIfNonInstantiable(c);
        if(!isInputAvailable(Globals.REFERENCE)){
            val = setInput(ret,Globals.REFERENCE);
            return  initializeInputObjectToRet(c.getName(),val,ret);
        }
        val = getInput();
        if(val.val==null){
            return  initializeInputObjectToRet(c.getName(),val,ret);
        }

        if(val.val != Dumbo.val && isAlreadyInitialized((InputElement)val.val)){
            ret = getInitialized((InputElement)val.val);
            return  initializeInputObjectToRet(c.getName(),val,ret);
        }
        pushCurrInput(val);

        Object o = null;
        if (val.val == Dumbo.val) {
            ret = newInstance(c);
            o = AnyObject.val;
            myInput(Globals.OBJECT, o);
        } else if (!(val.val instanceof InputElement)) {
            ret = val.val;
            o = ret;
            myInput(Globals.OBJECT, o);
        } else if (((InputElement) val.val).val instanceof AnyObject) {
            ret = newInstance(c);
            o = AnyObject.val;
            myInput(Globals.OBJECT, o);
        } else {
            ret = ((InputElement) val.val).val;
            o = ret;
            myInput(Globals.OBJECT, o);
        }
        val.val = prevInput;
        setInitialized((InputElement) val.val, ret);
        if (information.printTraceAndInputs) {
            if (o instanceof AnyObject)
                ptrace.printInputLn(System.identityHashCode(ret) + "(" + c.getName() + ")");
            else
                ptrace.printInputLn(ret + "(" + c.getName() + ")");
        }
        if (o instanceof AnyObject)
            varName = junitTest.valueObject(System.identityHashCode(ret), c.getName());
        else
            varName = junitTest.valueObject(System.identityHashCode(ret), c.getName(), ret);

        if(recursive)
            initializeInputObjectFields(c,ret,tab,varName,recursive);

        popCurrInput(val);
        Expression e = new PointerExpression(putAndGetPointerExpression(val));
        state.setStateConcrete(new Long(System.identityHashCode(ret)),e);
        pushExpression(e);
        return ret;
    }


    Object myInput(int type, Object o){
        switch(type){
            case Globals.INT:
                junitTest.assignToInput("Integer");
                break;
            case Globals.SHORT:
                junitTest.assignToInput("Short");
                break;
            case Globals.LONG:
                junitTest.assignToInput("Long");
                break;
            case Globals.BYTE:
                junitTest.assignToInput("Byte");
                break;
            case Globals.CHAR:
                junitTest.assignToInput("Character");
                break;
            case Globals.FLOAT:
                junitTest.assignToInput("Float");
                break;
            case Globals.DOUBLE:
                junitTest.assignToInput("Double");
                break;
            case Globals.BOOLEAN:
                junitTest.assignToInput("Boolean");
                break;
        }
        return myInputWithoutAssignTo(type, o);
    }

    private Object myInputWithoutAssignTo(int type, Object o) {
        Object ret;
        InputElement val;

        if((information.debugLevel&1)!=0) logger.info(1,"myInput Begin",null);
        if(isInputAvailable(type)){
            val = getInput();
            ret = val.val;
        } else {
            if(information.random){
                ret = nextRandom(type,o);
            } else {
                ret = nextDefault(type,o);
            }
            val = setInput(ret,type);
        }
        if((information.debugLevel&1)!=0) logger.info(1,"myInput End",null);
        if(information.printTraceAndInputs && (type != Globals.OBJECT )){
            ptrace.printInputLn(ret+"("+ret.getClass().getName().substring(10).toLowerCase()+")");
        }
        if(type!=Globals.OBJECT ){
            junitTest.valuePrimitive(ret);
            pushExpression(new ArithmeticExpression(putAndGetArithExpression(val)));
        }
        return ret;
    }

    private  Object nextDefault(int type,Object o) {
        switch(type){
            case Globals.INT:
                return new Integer(0);
            case Globals.SHORT:
                return new Short((short) 0);
            case Globals.LONG:
                return new Long(0);
            case Globals.BYTE:
                return new Byte((byte) 0);
            case Globals.CHAR:
                return new Character((char) 0);
            case Globals.FLOAT:
                return new Float(0.0);
            case Globals.DOUBLE:
                return new Double(0.0);
            case Globals.BOOLEAN:
                return new Boolean(false);
            case Globals.OBJECT:
                return o;
        }
        System.err.println("Control flow should not reach this point");
        System.exit(1);
        return null;
    }

    private  Object nextRandom(int type,Object o) {
        switch(type){
            case Globals.INT:
                return new Integer(rand.nextInt());
            case Globals.SHORT:
                return new Short((short)rand.nextInt());
            case Globals.LONG:
                return new Long(rand.nextLong());
            case Globals.BYTE:
                return new Byte((byte)rand.nextInt());
            case Globals.CHAR:
                return new Character((char)rand.nextInt());
            case Globals.FLOAT:
                return new Float(rand.nextFloat());
            case Globals.DOUBLE:
                return new Double(rand.nextDouble());
            case Globals.BOOLEAN:
                return new Boolean(rand.nextBoolean());
            case Globals.OBJECT:
                return o;
        }
        System.err.println("Control flow should not reach this point");
        System.exit(1);
        return null;
    }
}
