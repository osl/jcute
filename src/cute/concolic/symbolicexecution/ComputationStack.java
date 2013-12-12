package cute.concolic.symbolicexecution;

import cute.concolic.CuteException;
import cute.concolic.input.InputMap;
import cute.concolic.logging.BranchCoverageLog;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.pathconstraint.PointerConstraint;
import cute.concolic.symbolicstate.*;

import java.io.PrintWriter;
import java.util.Stack;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ComputationStack {
    private State state;
    private PathConstraint path;
    private BranchHistory history;
    private BranchCoverageLog coverage;
    private InputMap input;

    private Stack stack;
    private Stack stackValue;
    private Stack stackSymbolic;
    ComputationStackElem[] biops;
    int ibiops;
    String op;
    private long addr;
    private boolean ready = false;
    private String fname;
    private Expression returnExpression;

    public ComputationStack(State state, PathConstraint path, BranchHistory history, BranchCoverageLog coverage,InputMap input) {
        this.state = state;
        this.path = path;
        this.history = history;
        this.coverage = coverage;
        this.input = input;
        stack = new Stack();
        stackSymbolic = new Stack();
        stackValue = new Stack();
        biops = new ComputationStackElem[2];
        for (int i = 0; i < biops.length; i++) {
            biops[i] = new ComputationStackElem();
        }
        ibiops = -1;
        op = null;
    }

    public void push(long adr){
        Expression e = state.getState(adr);
        stack.push(e);
        stackSymbolic.push(new Long(adr));
        //((Expression)stack.peek()).printExpression();
    }

    public void setReturn(long adr){
        returnExpression = state.getState(adr);
    }

    public void setReturnExpression(Expression returnExpression) {
        this.returnExpression = returnExpression;
    }

    public void pushValue(Object val) {
        stackValue.push(val);

        if(stackSymbolic.size()==stackValue.size()){
            int sz = stackSymbolic.size();
            int szby2 = sz / 2;
            Object o;
            // reverse the stack
            for(int i=0;i<szby2;i++){
                o = stackSymbolic.elementAt(i);
                stackSymbolic.setElementAt(stackSymbolic.elementAt(sz-i-1),i);
                stackSymbolic.setElementAt(o,sz-i-1);
            }
            boolean isSymbolic = false;
            for(int i=0;i<sz;i++){
                Expression e = state.getState(((Long)stackSymbolic.elementAt(i)).longValue(),
                        System.identityHashCode(stackValue.elementAt(i)));
                if(e!=null) {
                    isSymbolic = true;
                }
                stackSymbolic.setElementAt(e,i);
            }
            if(isSymbolic){
                FunctionExpression.handleFunction(fname,stackSymbolic,stackValue,this,input);
            }
        }
    }

    public void loadAddress(long adr){
        addr = adr;
        ready = true;
    }

    public void loadValue(double val){
        ibiops++;
        if(ready){
            biops[ibiops].address = addr;
        } else {
            biops[ibiops].address = 0;
        }
        biops[ibiops].value = val;
        ready = false;
    }

    public void loadValueLong(long val){
        ibiops++;
        if(ready){
            biops[ibiops].address = addr;
        } else {
            biops[ibiops].address = 0;
        }
        biops[ibiops].valueLong = val;
        ready = false;
    }

    public void popAll(){
        stack.clear();
        stackSymbolic.clear();
        stackValue.clear();
        fname = null;
        returnExpression = null;
        ibiops = -1;
        op = null;
    }

    public void applyOp(String op){
        this.op = op.trim();
    }

    public void funBegin(String fname){
        state.pushLocals();
        this.fname = fname;
    }

    public void funEnd(){
        state.popLocals();

    }

    public void pop(long addr){
        if(stack.isEmpty()){
            state.setState(addr,null);
        } else {
            state.setState(addr,(Expression)stack.pop());
        }
        //state.print();
    }

    public void storeReturn(long addr){
        state.setState(addr,returnExpression);
    }

    public void store(long addr){
        if(ibiops==-1){
            state.setState(addr,null);
        } else if(ibiops==0){
            if(op==null){
                state.setState(addr,
                        state.getState(biops[0].address));
            } else if(op.equals("-")){
                state.setState(addr,
                        ((ArithmeticExpression)state.getState(biops[0].address)).negate());

            } else {
                throw new CuteException("Unknown unary op "+op);
            }
        } else if(ibiops==1){
            ArithmeticExpression first = (ArithmeticExpression)state.getState(biops[0].address);
            ArithmeticExpression second = (ArithmeticExpression)state.getState(biops[1].address);
            if(first!=null && second!=null){
                if(op.equals("+")){
                    state.setState(addr,first.add(second));
                } else if(op.equals("-")){
                    state.setState(addr,first.subtract(second));
                } else if(op.equals("cmpl")){
                    state.setState(addr,first.subtract(second));
                } else if(op.equals("cmpg")){
                    state.setState(addr,second.subtract(first));
                } else if(op.equals("cmp")){
                    state.setState(addr,first.subtract(second));
                } else if(op.equals("*")){
                    second.printExpression(new PrintWriter(System.out));
                    state.setState(addr,second.multiply(biops[0].value));
                } else {
                    throw new CuteException("Unknown binary exception "+op);
                }
            } else if(first==null && second!=null){
                if(op.equals("+")){
                    state.setState(addr,second.add(biops[0].value));
                } else if(op.equals("-")){
                    state.setState(addr,second.subtractFrom(biops[0].value));
                } else if(op.equals("cmpl")){
                    state.setState(addr,second.subtractFrom(biops[0].value));
                } else if(op.equals("cmpg")){
                    state.setState(addr,second.subtract(biops[0].value));
                } else if(op.equals("cmp")){
                    state.setState(addr,second.subtractFrom(biops[0].value));
                } else if(op.equals("*")){
                    state.setState(addr,second.multiply(biops[0].value));
                } else {
                    throw new CuteException("Unknown binary exception "+op);
                }
            } else if(first!=null && second==null){
                if(op.equals("+")){
                    state.setState(addr,first.add(biops[1].value));
                } else if(op.equals("-")){
                    state.setState(addr,first.subtract(biops[1].value));
                } else if(op.equals("cmpl")){
                    state.setState(addr,first.subtract(biops[1].value));
                } else if(op.equals("cmpg")){
                    state.setState(addr,first.subtractFrom(biops[1].value));
                } else if(op.equals("cmp")){
                    state.setState(addr,first.subtract(biops[1].value));
                } else if(op.equals("*")){
                    state.setState(addr,first.multiply(biops[1].value));
                } else {
                    throw new CuteException("Unknown binary exception "+op);
                }
            } else {
                state.setState(addr,null);
            }
        } else {
            throw new CuteException("Unknown number of ops");
        }
        ibiops = -1;
        op = null;
    }

    public void branch(boolean pos,String fname,int bid,int bCount){
        boolean added = false;
        /*System.out.println("Branching "+bid);
        state.print();
        System.out.println("pos = " + pos);
        */
        boolean isOtherBranchTaken = false;
        if(fname!=null)
            isOtherBranchTaken = coverage.branchTaken(fname,bid,bCount,pos);
        if(ibiops==1 && (op.equals("==") || op.equals("!="))){
            Expression first = (Expression)state.getState(biops[0].address);
            Expression second = (Expression)state.getState(biops[1].address);
            if(first!=null && second==null && first instanceof PointerExpression && biops[1].valueLong==0){
                path.add(new PointerConstraint(0,((PointerExpression)first).getP(),
                        (op.equals("==") && pos) || (op.equals("!=") && !pos)),isOtherBranchTaken);
                added = true;
            }
            if(first==null && second!=null && second instanceof PointerExpression && biops[0].valueLong==0){
                path.add(new PointerConstraint(0,((PointerExpression)second).getP(),
                        (op.equals("==") && pos) || (op.equals("!=") && !pos)),isOtherBranchTaken);
                added = true;
            }
            if(first!=null && second!=null && first instanceof PointerExpression && second instanceof PointerExpression){
                path.add(new PointerConstraint(((PointerExpression)first).getP(),((PointerExpression)second).getP(),
                        (op.equals("==") && pos) || (op.equals("!=") && !pos)),isOtherBranchTaken);
                added = true;
            }
        }
        if(ibiops==1){
            Expression first = (Expression)state.getState(biops[0].address);
            Expression second = (Expression)state.getState(biops[1].address);
            ArithmeticExpression ret = null;
            if(first!=null && second==null && first instanceof ArithmeticExpression){
                ret = ((ArithmeticExpression)first).subtract(biops[1].value);
            }
            if(first==null && second!=null && second instanceof ArithmeticExpression){
                ret = ((ArithmeticExpression)second).subtractFrom(biops[0].value);
            }
            if(first!=null && second!=null && first instanceof ArithmeticExpression && second instanceof ArithmeticExpression){
                ret = ((ArithmeticExpression)first).subtract((ArithmeticExpression)second);
            }
            if(ret!=null){
                added = true;
                if(op.equals("<")){
                    if(pos) ret.setL();
                    else ret.setGE();
                }
                if(op.equals(">")){
                    if(pos) ret.setG();
                    else ret.setLE();
                }
                if(op.equals(">=")){
                    if(pos) ret.setGE();
                    else ret.setL();
                }
                if(op.equals("<=")){
                    if(pos) ret.setLE();
                    else ret.setG();
                }
                if(op.equals("==")){
                    if(pos) ret.setEQ();
                    else ret.setNE();
                }
                if(op.equals("!=")){
                    if(pos) ret.setNE();
                    else ret.setEQ();
                }
                path.add(ret,isOtherBranchTaken);
            }
        }
        if(!added){
            path.add(null,isOtherBranchTaken);
        }
        op=null;
        ibiops = -1;
        history.compareAndSet(pos,path.size());
    }

}
