package cute.instrument;

import soot.*;
import soot.jimple.*;
import soot.tagkit.LineNumberTag;
import soot.util.Chain;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */


/*
loadAddress(y);
loadValue(y);
loadAddress(z);
loadValue(z);
applyOp("+");
store(x);
x = y + z;

loadAddress(y);
loadValue(y);
loadValue(4);
applyOp("+");
store(x);
x = y + 4;

popAll()
pushArg(y);
pushArg(z);
funBegin();
x = f(y,z);
funEnd();
popStore(x);

popAll()
pushArg(y);
pushArg(z);
funBegin();
f(y,z);
funEnd();

popAll()
pushArg(y);
pushArg();
funBegin();
x = f(y,4);
funEnd();
popStore(x);

popAll()
pushArg(y);
pushArg();
funBegin();
f(y,4);
funEnd();

void f(int x,int y){
    popStore(x);
    popStore(y);

    return;
}


int f(int x,int y){
    popStore(x);
    popStore(y);

    popAll();
    pushArg(z);
    return z;
}

*/

public class CuteInstrumenter extends BodyTransformer{
    private static CuteInstrumenter instance = new CuteInstrumenter();
    public SymbolTable st;
//    private HashSet alreadyInstrumented;
    public static boolean isConcurrent = true;
    private static String mainClass;

    private CuteInstrumenter() {
        st = new SymbolTable();
//        alreadyInstrumented = new HashSet();
    }

    public static CuteInstrumenter v(){
        return instance;
    }


    protected void internalTransform(Body body, String pn, Map map) {
        SootMethod thisMethod = body.getMethod();
        String cName = thisMethod.getDeclaringClass().getName();
        if(cName.startsWith("cute.")){
            return;
        }
        if(thisMethod.isAbstract()){
            return;
        }

        int branchCount = 0;
        int tBanchCount = 0;

        Chain units = body.getUnits();
        Iterator stmtIt = units.snapshotIterator();

        while(stmtIt.hasNext()){
            Stmt s = (Stmt) stmtIt.next();
            if(s instanceof IfStmt){
                tBanchCount++;
            } else if(s instanceof LookupSwitchStmt){
                tBanchCount += ((LookupSwitchStmt)s).getTargetCount();
            } else if(s instanceof TableSwitchStmt){
                tBanchCount += ((TableSwitchStmt)s).getHighIndex()- ((TableSwitchStmt)s).getLowIndex()+1;
            }
        }

        stmtIt = units.snapshotIterator();
        while(stmtIt.hasNext()){
            Stmt s = (Stmt) stmtIt.next();
            int lineNo;
            if((s.getTags()).isEmpty()){
                lineNo = -1;
            } else {
                lineNo = ((LineNumberTag)s.getTags().get(0)).getLineNumber();
            }
            if(s instanceof EnterMonitorStmt || s instanceof ExitMonitorStmt){
                InstrumentMonitorStatements.instrument(s,units,isConcurrent,lineNo);
            } else if(s instanceof AssignStmt){
                InstrumentAssignmentStatement.instrument(s,body,units,isConcurrent,st,lineNo);
            } else  if(s instanceof InvokeStmt){
                InstrumentInvokeStatement.instrument(s,body,units,st,isConcurrent,lineNo);
            } else if(s instanceof IdentityStmt){
                InstrumentIdentityStatement.instrument(s,units,st,lineNo);
            } else if(s instanceof ReturnStmt){
                InstrumentReturnStatement.instrument(s,units,st,lineNo);
            } else if(s instanceof ReturnVoidStmt){
                InstrumentReturnVoidStatement.instrument(thisMethod,s,units,isConcurrent,lineNo,mainClass);
            } else if(s instanceof IfStmt){
                branchCount = InstrumentIfStatement.instrument(branchCount,tBanchCount,
                        s,thisMethod,body,units,st,isConcurrent,lineNo);
            } else if(s instanceof LookupSwitchStmt){
                branchCount = InstrumentLookupSwitchStatement.instrument(branchCount,tBanchCount,
                        s,thisMethod,body,units,st,isConcurrent,lineNo);
            } else if(s instanceof TableSwitchStmt){
                branchCount = InstrumentTableSwitchStatement.instrument(branchCount,tBanchCount,
                        s,thisMethod,body,units,st,isConcurrent,lineNo);
            }
        }
        WrapWholeBodyInTryCatch.instrument(body,thisMethod,isConcurrent,mainClass);
        body.validate();
    }




    public static void main(String[] args) {
        String seq = System.getProperty("cute.sequential");
        if(seq!=null && seq.equals("true")){
            isConcurrent=false;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("--app")){
                mainClass=args[i+1];
            }
        }
        Scene.v().loadClassAndSupport("cute.concolic.Call");
        PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter",CuteInstrumenter.v()));
        soot.Main.main(args);
        try {
            ObjectOutputStream out;
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("cuteSymbolTable")));
            out.writeObject(CuteInstrumenter.v().st);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }
}
