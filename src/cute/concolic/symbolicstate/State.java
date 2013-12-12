package cute.concolic.symbolicstate;

import cute.concolic.logging.Logger;
import cute.concolic.logging.Printable;
import cute.concolic.Information;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

public class State implements Printable {
    private HashMap gs;
    private IdentityHashMap localStacks;
    private Logger logger;
    private Information information;
    private HashMap concreteToSymbolic;

    public State(Logger logger, Information information) {
        this.logger = logger;
        this.information = information;
        gs = new HashMap();
        localStacks = new IdentityHashMap();
        concreteToSymbolic  = new HashMap();
    }

    public void setState(long address, Expression e) {
        Thread t = Thread.currentThread();
        HashMap ls;
        synchronized(localStacks){
            Stack localStack = (Stack)localStacks.get(t);
            if(localStack == null){
                localStack = new Stack();
                localStacks.put(t,localStack);
                ls = new HashMap();
                localStack.push(ls);
            } else {
                ls = (HashMap)localStack.peek();
            }
            if(e==null){
                ls.remove(new Long(address));
                gs.remove(new Long(address));
            } else {
                if(address>>32 == 0){
                    ls.put(new Long(address),e);
                } else {
                    gs.put(new Long(address),e);
                }
            }
            if((information.debugLevel&16)!=0) logger.info(16,null,this);
        }
    }

    public void popLocals() {
        Thread t = Thread.currentThread();
        synchronized(localStacks){
            Stack localStack = (Stack)localStacks.get(t);
            if(localStack!=null){
                localStack.pop();
                if(localStack.isEmpty())
                    localStacks.remove(t);
            }
            if((information.debugLevel&16)!=0) logger.info(16,null,this);
        }
    }

    public void pushLocals() {
        Thread t = Thread.currentThread();
        Stack localStack;
        HashMap ls;
        synchronized(localStacks){
            localStack = (Stack)localStacks.get(t);
            if(localStack == null){
                localStack = new Stack();
                localStacks.put(t,localStack);
            }
            ls = new HashMap();
            localStack.push(ls);
            if((information.debugLevel&16)!=0) logger.info(16,null,this);
        }
    }

    public Expression getState(long address) {
        Thread t = Thread.currentThread();
        Stack localStack;
        synchronized(localStacks){
            localStack = (Stack)localStacks.get(t);
        }
        HashMap ls;
        if(localStack == null){
            return null;
        }
        if(localStack.isEmpty()){
            return null;
        }
        ls = (HashMap)localStack.peek();
        if(address>>32 == 0){
            return (Expression)ls.get(new Long(address));
        } else {
            return (Expression)gs.get(new Long(address));
        }
    }

    public Expression getState(long address,long oid) {
        Expression e = getState(address);
        if(e!=null) return e;
        return getStateConcrete(oid);
    }

    public void print(PrintWriter out){
        int i=0;
        out.println("Global Symbolic State");
        for (Iterator iterator = gs.keySet().iterator(); iterator.hasNext();) {
            Long l = (Long) iterator.next();
            Expression e = (Expression)gs.get(l);
            out.print(l+" : ");
            e.printExpression(out);
        }
        for (Iterator iterator2 = localStacks.keySet().iterator(); iterator2.hasNext();) {
            Thread t = (Thread) iterator2.next();
            Stack localStack = (Stack) localStacks.get(t);
            out.println("Local Symbolic State of thread "+t);
            for (Iterator iterator = localStack.iterator(); iterator.hasNext();) {
                i++;
                out.println("Local Stack "+i);
                HashMap tmp = (HashMap) iterator.next();
                for (Iterator iter2 = tmp.keySet().iterator(); iter2.hasNext();) {
                    Long l = (Long) iter2.next();
                    Expression e = (Expression)tmp.get(l);
                    out.print(l+" : ");
                    e.printExpression(out);
                }
            }
        }
        out.println("Object map ");
        for (Iterator iterator = concreteToSymbolic.keySet().iterator(); iterator.hasNext();) {
            Long aLong = (Long) iterator.next();
            out.print(aLong+" :");
            getStateConcrete(aLong).printExpression(out);

        }
        out.flush();
    }

    public void setStateConcrete(Long oid, Expression e) {
        concreteToSymbolic.put(oid,e);
    }

    public Expression getStateConcrete(Long l){
        return (Expression)concreteToSymbolic.get(l);
    }

    public Expression getStateConcrete(long l){
        return getStateConcrete(new Long(l));
    }
}
