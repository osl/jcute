package cute.concolic;

import cute.Cute;
import cute.concolic.concurrency.IndexInfo;
import cute.concolic.concurrency.RacePair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

public class Call {

    public static long id(int f,int s){
        long l = f;
        l = l << 32;
        l += s;
        return l;
    }

    public static Class myClass(String c){
        try {
            return Class.forName(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
        return null;
    }

    public static void threadException(Throwable e){
        if(e instanceof ThreadDeath){
            popAll(-1);
            endBefore(-1);
        } else {
            StringWriter strOut = new StringWriter();
            e.printStackTrace(new PrintWriter(strOut));
            String error = strOut.toString();
            System.err.println("Error in "+Thread.currentThread()+" "+e.getCause());
            System.err.println(error);
            Globals.globals.ptrace.writeError(error);
            Globals.globals.information.returnVal = Cute.EXIT_ERROR+Globals.globals.information.returnVal;
            Globals.globals.solver.predict();
        }
    }

    public static void printDebugBefore(String msg,boolean concurrent){
        if(!Globals.globals.initialized) {
            Globals.globals.begin();
        }
        if((Globals.globals.information.debugLevel&1)!=0) Globals.globals.logger.info(1,"Begin "+msg,null);
    }

    public static void printDebugAfter(String msg,double id,boolean concurrent,String cMsg){
        if((Globals.globals.information.debugLevel&1)!=0)  Globals.globals.logger.info(1,"End "+msg,null);
        if(concurrent){
            if((Globals.globals.information.debugLevel&2)!=0){
                StringBuffer out = new StringBuffer(msg);
                out.append(" ");
                if(id>=0){
                    out.append(id);
                }
                out.append(cMsg);
                out.append(" Thread ").append(Thread.currentThread());
                Globals.globals.logger.info(2,out.toString(),null);
            }
        }
    }

    public static int getLockDepth(Object l){
        int ldepth = Globals.globals.sched.getLockDepth(l);
//        System.out.println("ldepth = " + ldepth);
//        System.out.flush();
        return ldepth;
    }

    public static void printStr(String s){
        System.err.println("%%%%%%%%%%%%"+Thread.currentThread()+" "+s);
        System.err.flush();
    }

    public static void popStore(int f,int s,int lineno){
        printDebugBefore("popStore 2",false);
        Globals.globals.cstack.pop(id(f,s));
        //ExecutionLog.printLineNumber(lineno);
        printDebugAfter("popStore 2",id(f,s),false,"");
    }

    public static void popStore(Object o,int s,int lineno){
        printDebugBefore("popStore 3",false);
        int f = System.identityHashCode(o);
        Globals.globals.cstack.pop(id(f,s));
        //ExecutionLog.printLineNumber(lineno);
        printDebugAfter("popStore 3",id(f,s),false,"");
    }

    public static void storeReturn(int f,int s,int lineno){
        printDebugBefore("storeReturn 2",false);
        Globals.globals.cstack.storeReturn(id(f,s));
        //ExecutionLog.printLineNumber(lineno);
        printDebugAfter("storeReturn 2",id(f,s),false,"");
    }

    public static void storeReturn(Object o,int s,int lineno){
        printDebugBefore("storeReturn 3",false);
        int f = System.identityHashCode(o);
        Globals.globals.cstack.storeReturn(id(f,s));
        //ExecutionLog.printLineNumber(lineno);
        printDebugAfter("storeReturn 3",id(f,s),false,"");
    }

    public static void store(int f,int s,int lineno){
        printDebugBefore("store 1",false);
        Globals.globals.cstack.store(id(f,s));
        //ExecutionLog.printLineNumber(lineno);
        printDebugAfter("store 1",id(f,s),false,"");
    }

    public static void store(Object o,int s,int lineno){
        printDebugBefore("store 2",false);
        int f = System.identityHashCode(o);
        Globals.globals.cstack.store(id(f,s));
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("store 2",id(f,s),false,"");
    }


    public static void loadAddress(int f,int s,int lineno){
        printDebugBefore("loadAddress 1",false);
        Globals.globals.cstack.loadAddress(id(f,s));
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("loadAddress 1",id(f,s),false,"");
    }

    public static void loadAddress(Object o,int s,int lineno){
        printDebugBefore("loadAddress 2",false);

        int f = System.identityHashCode(o);
        Globals.globals.cstack.loadAddress(id(f,s));
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("loadAddress 2",id(f,s),false,"");
    }

    public static void pushArg(Object o,int s,int lineno){
        printDebugBefore("pushArg 1",false);
        int f = System.identityHashCode(o);
        Globals.globals.cstack.push(id(f,s));
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("pushArg 1",id(f,s),false,"");
    }

    public static void pushArg(int f,int s,int lineno){
        printDebugBefore("pushArg 2",false);
        Globals.globals.cstack.push(id(f,s));
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("pushArg 2",id(f,s),false,"");
    }

    public static void setReturn(Object o,int s,int lineno){
        printDebugBefore("setReturn 1",false);
        int f = System.identityHashCode(o);
        Globals.globals.cstack.setReturn(id(f,s));
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("setReturn 1",id(f,s),false,"");
    }

    public static void setReturn(int f,int s,int lineno){
        printDebugBefore("setReturn 2",false);
        Globals.globals.cstack.setReturn(id(f,s));
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("setReturn 2",id(f,s),false,"");
    }

    public static void popAll(int lineno){
        printDebugBefore("popAll 1",false);
        Globals.globals.cstack.popAll();
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("popAll 1",-1,false,"");
    }

    public static void funBegin(String fname,int lineno){
        printDebugBefore("funBegin 1",false);
        Globals.globals.cstack.funBegin(fname);
        Globals.globals.ptrace.printLineNumber(lineno,null,"call",null);
        printDebugAfter("funBegin 1",-1,false,"");
    }

    public static void funEnd(int lineno){
        printDebugBefore("funEnd 1",false);
        Globals.globals.cstack.funEnd();
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("funEnd 1",-1,false,"");
    }

    public static void loadValue(double l,int lineno){
        printDebugBefore("loadValue 1",false);
        Globals.globals.cstack.loadValue(l);
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("loadValue 1",l,false,"");
    }

    public static void loadValue(int l,int lineno){
        loadValue((double)l,lineno);
    }

    public static void loadValue(byte l,int lineno){
        loadValue((double)l,lineno);
    }

    public static void loadValue(short l,int lineno){
        loadValue((double)l,lineno);
    }

    public static void loadValue(char l,int lineno){
        loadValue((double)l,lineno);
    }

    public static void loadValue(boolean l,int lineno){
        loadValue((double)(l?1:0),lineno);
    }

    public static void loadValue(float l,int lineno){
        loadValue((double)l,lineno);
    }

    public static void loadValue(long l,int lineno){
        loadValue((double)l,lineno);
    }

    public static void loadValue(Object o,int lineno){
        printDebugBefore("loadValue 2",false);
        long l = o==null?0:System.identityHashCode(o);
        Globals.globals.cstack.loadValueLong(l);
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("loadValue 2",System.identityHashCode(o),false,"");
    }

    public static void loadValue(int lineno){
        printDebugBefore("loadValue 3",false);
        Globals.globals.cstack.loadValueLong(0);
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("loadValue 3",0,false,"");
    }

    public static void pushValue(double l,int lineno){
        pushValue(new Double(l),lineno);
    }

    public static void pushValue(int l,int lineno){
        pushValue(new Integer(l),lineno);
    }

    public static void pushValue(byte l,int lineno){
        pushValue(new Byte(l),lineno);
    }

    public static void pushValue(short l,int lineno){
        pushValue(new Short(l),lineno);
    }

    public static void pushValue(char l,int lineno){
        pushValue(new Character(l),lineno);
    }

    public static void pushValue(boolean l,int lineno){
        pushValue(new Boolean(l),lineno);
    }

    public static void pushValue(float l,int lineno){
        pushValue(new Float(l),lineno);
    }

    public static void pushValue(long l,int lineno){
        pushValue(new Long(l),lineno);
    }

    public static void pushValue(Object o,int lineno){
        printDebugBefore("pushValue 1",false);
        Globals.globals.cstack.pushValue(o);
        printDebugAfter("pushValue 3",0,false,"");
    }

    public static void applyOp(String op,int lineno){
        printDebugBefore("applyOp 1",false);
        Globals.globals.cstack.applyOp(op);
        //Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("applyOp 1",-1,false,op);
    }

    public static void branchPos(String funName,int bid,int bCount,int lineno){
        printDebugBefore("branchPos 1",false);
//        System.out.print("funName = " + funName);
//        System.out.print(" bid = " + bid);
//        System.out.println(" bCount = " + bCount);
//        System.out.flush();
        Globals.globals.cstack.branch(true,funName,bid,bCount);
        Globals.globals.ptrace.printLineNumber(lineno,null,"branch",null);
        printDebugAfter("branchPos 1",-1,false,"");
    }

    public static void branchNeg(String funName, int bid, int bCount,int lineno){
        printDebugBefore("branchNeg 1",false);
//        System.out.print("funName = " + funName);
//        System.out.print(" bid = " + bid);
//        System.out.println(" bCount = " + bCount);
//        System.out.flush();
        Globals.globals.cstack.branch(false,funName,bid,bCount);
        Globals.globals.ptrace.printLineNumber(lineno,null,"branch",null);
        printDebugAfter("branchNeg 1",-1,false,"");
    }


    // For concurrency only
    public static void lock(Object l,String sig,int lineno){
        printDebugBefore("lock 2",true);
        Method[] ms = l.getClass().getMethods();
        boolean flag = true;
        for (int i = 0; flag && i < ms.length; i++) {
            Method m = ms[i];
            if(m.toString().endsWith(sig) && m.toString().indexOf("synchronized")!=-1){
                flag = false;
            }
        }

        if(!flag)
            Globals.globals.sched.lock(l);
        Globals.globals.ptrace.printLineNumber(lineno,null,"lock",null);
        printDebugAfter("lock 2",System.identityHashCode(l),true,"");
    }

    public static void unlock(Object l,String sig,int lineno){
        printDebugBefore("unlock 2",true);
        printDebugAfter(">unlock 2",System.identityHashCode(l),true,"");
        Method[] ms = l.getClass().getMethods();
        boolean flag = true;
        for (int i = 0; flag && i < ms.length; i++) {
            Method m = ms[i];
            if(m.toString().endsWith(sig) && m.toString().indexOf("synchronized")!=-1){
                flag = false;
            }
        }

        if(!flag)
            Globals.globals.sched.unlock(l);
        Globals.globals.ptrace.printLineNumber(lineno,null,"lock",null);
        printDebugAfter("unlock 2",System.identityHashCode(l),true,"");
    }


    public static void lock(Object l,int lineno){
        printDebugBefore("lock 1",true);
        Globals.globals.sched.lock(l);
        Globals.globals.ptrace.printLineNumber(lineno,null,"lock",null);
        printDebugAfter("lock 1",System.identityHashCode(l),true,"");
    }

    public static void unlock(Object l,int lineno){
        printDebugBefore("unlock 1",true);
        printDebugAfter(">unlock 1",System.identityHashCode(l),true,"");
        Globals.globals.sched.unlock(l);
        Globals.globals.ptrace.printLineNumber(lineno,null,"unlock",null);
        printDebugAfter("unlock 1",System.identityHashCode(l),true,"");
    }

    public static void readAccess(int f,int s,int lineno){
        printDebugBefore("readAccess 1",true);
        IndexInfo ii = new IndexInfo();
        RacePair rp = Globals.globals.sched.access(id(f,s),true,ii);
        Globals.globals.ptrace.printLineNumber(lineno,ii,"read",rp);
        if(rp.rl1!=null || rp.rl2!=null){
            Globals.globals.ptrace.printRace(rp,Globals.globals.st.getReverse(f)
                    +"."+Globals.globals.st.getReverse(s));
        }
        printDebugAfter("readAccess 1",id(f,s),true,"");
    }

    public static void readAccess(Object o,int s,int lineno){
        printDebugBefore("readAccess 2",true);
        int f = System.identityHashCode(o);
        IndexInfo ii = new IndexInfo();
        RacePair rp = Globals.globals.sched.access(id(f,s),true,ii);
        Globals.globals.ptrace.printLineNumber(lineno,ii,"read",rp);
        if(rp.rl1!=null || rp.rl2!=null){
            Globals.globals.ptrace.printRace(rp,o.getClass()
                    +"."+Globals.globals.st.getReverse(s));
        }
        printDebugAfter("readAccess 2",id(f,s),true,"");
    }

    public static void writeAccess(int f,int s,int lineno){
        printDebugBefore("writeAccess 1",true);
        IndexInfo ii = new IndexInfo();
        RacePair rp = Globals.globals.sched.access(id(f,s),false,ii);
        Globals.globals.ptrace.printLineNumber(lineno,ii,"write",rp);
        if(rp.rl1!=null || rp.rl2!=null){
            Globals.globals.ptrace.printRace(rp,Globals.globals.st.getReverse(f)
                    +"."+Globals.globals.st.getReverse(s));
        }
        printDebugAfter("writeAccess 1",id(f,s),true,"");
    }

    public static void writeAccess(Object o,int s,int lineno){
        printDebugBefore("writeAccess 2",true);
        IndexInfo ii = new IndexInfo();
        int f = System.identityHashCode(o);
        RacePair rp = Globals.globals.sched.access(id(f,s),false,ii);
        Globals.globals.ptrace.printLineNumber(lineno,ii,"write",rp);
        if(rp.rl1!=null || rp.rl2!=null){
            Globals.globals.ptrace.printRace(rp,o.getClass()
                    +"."+Globals.globals.st.getReverse(s));
        }
        printDebugAfter("writeAccess 2",id(f,s),true,"");
    }

    public static void waitBefore(Object l,int lineno){
        printDebugBefore("waitBefore 1",true);
        Globals.globals.sched.waitBefore(l);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("waitBefore 1",System.identityHashCode(l),true,"");
    }

    public static void waitOver(Object l,int lineno){
        System.out.println("wait over on  " + l);
        System.out.println("lineno = " + lineno);
        System.out.flush();
    }


    public static void waitAfter(Object l,int lineno){
        printDebugBefore("waitAfter 1",true);
        printDebugAfter(">waitAfter 1",System.identityHashCode(l),true,"");
        Globals.globals.sched.waitAfter(l);
        printDebugAfter("waitAfter 1",System.identityHashCode(l),true,"");
    }

    public static void notifyBefore(Object l,int lineno){
        printDebugBefore("notifyBefore 1",true);
        Globals.globals.sched.notifyBefore(l);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("notifyBefore 1",System.identityHashCode(l),true,"");
    }

    public static void notifyAllBefore(Object l,int lineno){
        printDebugBefore("notifyAllBefore 1",true);
        Globals.globals.sched.notifyAllBefore(l);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("notifyAllBefore 1",System.identityHashCode(l),true,"");
    }

    public static void notifyAfter(Object l,int lineno){
        printDebugBefore("notifyAfter 1",true);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("notifyAfter 1",System.identityHashCode(l),true,"");
    }

    public static void notifyAllAfter(Object l,int lineno){
        printDebugBefore("notifyAllAfter 1",true);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("notifyAllAfter 1",System.identityHashCode(l),true,"");
    }

    public static void endBefore(int lineno){
        printDebugBefore("endBefore 1",true);
        Globals.globals.sched.endBefore();
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("endBefore 1",-1,true,"");
    }

    public static void joinBefore(Object t,int lineno){
        printDebugBefore("joinBefore 1",true);
        Globals.globals.sched.joinBefore((Thread)t);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("joinBefore 1",-1,true,""+t);
    }

    public static void joinAfter(Object t,int lineno){
        printDebugBefore("joinAfter 1",true);
        Globals.globals.sched.joinAfter((Thread)t);
        printDebugAfter("joinAfter 1",-1,true,""+t);
    }

    public static void startBefore(Object t,int lineno){
        printDebugBefore("startBefore 1",true);
        Globals.globals.sched.startBefore((Thread)t);
        Globals.globals.ptrace.printLineNumber(lineno);
        printDebugAfter("startBefore 1",-1,true,""+t);
    }

    public static void startAfter(Object t,int lineno){
        printDebugBefore("startAfter 1",true);
        Globals.globals.sched.startAfter();
        printDebugAfter("startAfter 1",-1,true,""+t);
    }
}
