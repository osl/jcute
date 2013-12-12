package cute.concolic.symbolicexecution;

import cute.concolic.symbolicstate.State;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.logging.BranchCoverageLog;
import cute.concolic.input.InputMap;

import java.util.IdentityHashMap;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ComputationStacks {
    private State state;
    private PathConstraint path;
    private BranchHistory history;
    private BranchCoverageLog coverage;
    private InputMap input;

    private IdentityHashMap localCS;

    public ComputationStacks(State state, PathConstraint path, BranchHistory history, BranchCoverageLog coverage,InputMap input) {
        this.state = state;
        this.path = path;
        this.history = history;
        this.coverage = coverage;
        this.input = input;
        localCS = new IdentityHashMap();
    }

    private synchronized ComputationStack getStack(){
        Thread t = Thread.currentThread();
        ComputationStack cs = (ComputationStack)localCS.get(t);
        if(cs==null){
            cs = new ComputationStack(state,path,history,coverage,input);
            localCS.put(t,cs);
        }
        return cs;
    }

    public void push(long adr){
        getStack().push(adr);
    }

    public void setReturn(long adr){
        getStack().setReturn(adr);
    }

    public void loadAddress(long adr){
        getStack().loadAddress(adr);
    }

    public void loadValue(double val){
        getStack().loadValue(val);
    }

    public void loadValueLong(long val){
        getStack().loadValueLong(val);
    }

    public void pushValue(Object o) {
        getStack().pushValue(o);
    }

    public void popAll(){
        getStack().popAll();
    }

    public void applyOp(String op){
        getStack().applyOp(op);
    }

    public void funBegin(String fname){
        getStack().funBegin(fname);
    }

    public void funEnd(){
        getStack().funEnd();
    }

    public void pop(long addr){
        getStack().pop(addr);
    }

    public void storeReturn(long addr){
        getStack().storeReturn(addr);
    }

    public void store(long addr){
        getStack().store(addr);
    }

    public void branch(boolean pos,String fname,int bid,int bCount){
        getStack().branch(pos,fname,bid ,bCount);
    }

}
