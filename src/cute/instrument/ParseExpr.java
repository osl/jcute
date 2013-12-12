package cute.instrument;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.List;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:41:17 PM
 */
public class ParseExpr {
    /**
     * Instruments the expression v contained in s.
     * <p>
     * If v = x, then instrumentation results in<br>
     * {@link cute.concolic.Call#loadAddress(int, int, int)}  loadAddress(0,st.get(x),lineNo);}<br>
     * loadValue(x, lineNo);<br>
     * s;
     * <p>
     * If v = o.x and , then instrumentation results in<br>
     * {@link cute.concolic.Call#loadAddress(Object, int, int)}  loadAddress(o,st.get(x),lineNo);}<br>
     * loadValue(o.x, lineNo);<br>
     * s;
     *
     * @param b
     * @param v
     * @param units
     * @param s
     * @param st
     * @param isConcurrent
     * @param lineNo
     * @param isCond
     */
    public static void instrument(Body b,Value v,Value left,Chain units,Stmt s,SymbolTable st,
                                  boolean isConcurrent,int lineNo,boolean isCond){
        if(v instanceof Local || v instanceof ArrayRef
                || v instanceof InstanceFieldRef || v instanceof StaticFieldRef){
            AddCallWithAddress.instrument(v,units,s,"loadAddress",true,st,lineNo,false);
        }
        if(v instanceof Local || v instanceof ArrayRef
                || v instanceof InstanceFieldRef || v instanceof StaticFieldRef
                || v instanceof Constant){
            AddValue.instrument(b,v,units,s,"loadValue",true,lineNo,isCond);
        } else if(v instanceof AddExpr || v instanceof SubExpr || v instanceof MulExpr
                || v instanceof CmpExpr || v instanceof CmpgExpr || v instanceof CmplExpr){
            if(!(((BinopExpr)v).getOp1() instanceof Constant
                    && ((BinopExpr)v).getOp2() instanceof Constant)){
                ParseExpr.instrument(b,((BinopExpr)v).getOp1(),null,units,s,st,isConcurrent,lineNo,false);
                ParseExpr.instrument(b,((BinopExpr)v).getOp2(),null,units,s,st,isConcurrent,lineNo,false);
                SootMethodRef mr =
                        Scene.v().getMethod("<cute.concolic.Call: void applyOp(java.lang.String,int)>").makeRef();
                Value sym =
                        StringConstant.v(((BinopExpr)v).getSymbol());
                units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,sym,IntConstant.v(lineNo))),s);
            }
        } else if(v instanceof EqExpr
                || v instanceof LeExpr || v instanceof LtExpr || v instanceof GeExpr || v instanceof GtExpr
                || v instanceof NeExpr){
            if(!(((BinopExpr)v).getOp1() instanceof Constant
                    && ((BinopExpr)v).getOp2() instanceof Constant)){
                ParseExpr.instrument(b,((BinopExpr)v).getOp1(),null,units,s,st,isConcurrent,lineNo,true);
                ParseExpr.instrument(b,((BinopExpr)v).getOp2(),null,units,s,st,isConcurrent,lineNo,true);
                SootMethodRef mr =
                        Scene.v().getMethod("<cute.concolic.Call: void applyOp(java.lang.String,int)>").makeRef();
                Value sym =
                        StringConstant.v(((BinopExpr)v).getSymbol());
                units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,sym,IntConstant.v(lineNo))),s);
            }
        } else if(v instanceof NegExpr){
            ParseExpr.instrument(b,((NegExpr)v).getOp(),null,units,s,st,isConcurrent,lineNo,false);
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void applyOp(java.lang.String,int)>").makeRef();
            Value sym = StringConstant.v("-");
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,sym,IntConstant.v(lineNo))),s);
        } else if(v instanceof CastExpr){
            ParseExpr.instrument(b,((CastExpr)v).getOp(),null,units,s,st,isConcurrent,lineNo,false);
        } else if(v instanceof InvokeExpr){
            // popAll()
            // pushArg(x) where x is local

            if(isConcurrent){
                AddSyncMethodInstr.instrument((InvokeExpr)v,units,b,s,true,"lock",lineNo);
            }

            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void popAll(int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
            List args = ((InvokeExpr)v).getArgs();
            for (int i=0; i<args.size(); i++) {
                Value varg = (Value) args.get(args.size()-i-1);
                AddCallWithAddress.instrument(varg,units,s,"pushArg",true,st,lineNo,true);
            }
            if(v instanceof InstanceInvokeExpr){
                AddCallWithAddress.instrument(((InstanceInvokeExpr)v).getBase(),units,s,"pushArg",true,st,lineNo,true);
            }
            Value msig = StringConstant.v(((InvokeExpr)v).getMethod().getSignature());
            System.out.println("Sig:"+msig);
            mr = Scene.v().getMethod("<cute.concolic.Call: void funBegin(java.lang.String,int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,msig,IntConstant.v(lineNo))),s);
            if(isConcurrent){
                AddSyncMethodInstr.instrument((InvokeExpr)v,units,b,s,false,"unlock",lineNo);
            }
            if(left!=null){
                AddCallWithAddress.instrument(left,units,s,"storeReturn",false,st,lineNo,false);
            }
            for (int i=0; i<args.size(); i++) {
                Value varg = (Value) args.get(i);
                AddValue.instrument(b,varg,units,s,"pushValue",false,lineNo,true);
            }
            if(v instanceof InstanceInvokeExpr){
                AddValue.instrument(b,((InstanceInvokeExpr)v).getBase(),units,s,"pushValue",false,lineNo,true);
            }
            mr = Scene.v().getMethod("<cute.concolic.Call: void funEnd(int)>").makeRef();
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
        } else if(v instanceof NewArrayExpr){
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void popAll(int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
            Value sizeValue = ((NewArrayExpr)v).getSize();
            AddCallWithAddress.instrument(sizeValue,units,s,"pushArg",true,st,lineNo,true);
            if(left!=null) AddCallWithAddress.instrument(left,units,s,"pushArg",true,st,lineNo,true);
            mr = Scene.v().getMethod("<cute.concolic.Call: void funBegin(java.lang.String,int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
                    mr,StringConstant.v("NewArray"),IntConstant.v(lineNo))),s);
            AddValue.instrument(b,sizeValue,units,s,"pushValue",false,lineNo,true);
            if (left != null) AddValue.instrument(b,left,units,s,"pushValue",false,lineNo,true);
            mr = Scene.v().getMethod("<cute.concolic.Call: void funEnd(int)>").makeRef();
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
        } else if(v instanceof LengthExpr){
            SootMethodRef mr = Scene.v().getMethod("<cute.concolic.Call: void popAll(int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
            Value arg = ((LengthExpr)v).getOp();
            AddCallWithAddress.instrument(arg,units,s,"pushArg",true,st,lineNo,true);
            mr = Scene.v().getMethod("<cute.concolic.Call: void funBegin(java.lang.String,int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
                    mr,StringConstant.v("ArrayLength"),IntConstant.v(lineNo))),s);
            if(left!=null){
                AddCallWithAddress.instrument(left,units,s,"storeReturn",false,st,lineNo,false);
            }
            AddValue.instrument(b,arg,units,s,"pushValue",false,lineNo,true);
            mr = Scene.v().getMethod("<cute.concolic.Call: void funEnd(int)>").makeRef();
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr,IntConstant.v(lineNo))),s);
        }

    }
}
