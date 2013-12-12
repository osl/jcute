package cute.concolic.generateinputandschedule;

import cute.concolic.input.InputMap;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.symbolicstate.ArithmeticExpression;
import cute.concolic.Globals;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 26, 2006
 * Time: 2:09:01 PM
 */
public class ArithmeticSolver {
    private PathConstraint path;
    private InputMap input;
    private int optimized;
    private int n;
    private int[] nodes;

    public void addEqual(int x, int y) {
        int k,i,j,tmp;

        i = nodes[x];
        j = nodes[y];
        if (i!=j) {
            if (j<i) {
                tmp = j;
                j=i;
                i = tmp;
            }
            for (k=1;k<=n;k++) {
                if (nodes[k]==j) {
                    nodes[k]=i;
                }
            }
        }
    }

    public boolean isEqual(int x, int y) {
        int i,j;
        i = nodes[x];
        j = nodes[y];
        return(i==j);
    }

    public void initDependency(int n){
        int i;

        this.n = n;
        nodes = new int[n+1];
        for (i=0;i<=n;i++) {
            nodes[i]=i;
        }
    }


    public void print(){
        int i;
        System.out.println("\n------------");
        for (i=0;i<=n;i++) {
            System.out.print(nodes[i]);
            System.out.println(" ");
        }
        System.out.println("\n------------");
    }

    public ArithmeticSolver(PathConstraint path, InputMap input, int optimized) {
        this.path = path;
        this.input = input;
        this.optimized = optimized;
    }

    private Vector getArithContsraints(int k){
        int i,j,x;
        boolean flag;
        Vector ret;
        ArithmeticExpression tmp;
        ArithmeticExpression tmp2;

        ret = new Vector(k);
        tmp = path.getArith(k);
        x = tmp.svar[0];
        ret.add(tmp);

        for (i=0;i<=k;i++) {
            tmp = path.getArith(i);
            if (tmp!=null) {
                if ((optimized & 1) == 1) {
                    x = tmp.svar[0];
                    j=1;
                } else {
                    j = 0;
                }
                for (;j<tmp.svar.length;j++) {
                    addEqual(x,tmp.svar[j]);
                }
            }
        }

        for (i=k-1;i>=0;i--) {
            tmp = path.getArith(i);
            if (tmp!=null) {
                flag = false;
                for (j=0;j<tmp.svar.length && !flag;j++) {
                    if (isEqual(x,tmp.svar[j]))
                        flag = true;
                }
                if ((optimized & 2)==2) {
                    for (j=k;j>i && flag;j--) {
                        tmp2 = path.getArith(j);
                        if (tmp2!=null && tmp.equals(tmp2)) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    ret.add(tmp);
                }
            }
        }
        return ret;
    }

    public boolean solveArith(int k,int counter){
        double inf;
        int i,status,sz;
        ArithmeticExpression  tmp;
        ArithmeticExpression tmp2;
        LpSolve  lp;
        Vector lc;

        if (k>=0) {
            tmp2 = path.getArith(k);
            tmp2.invert();
            if ((optimized & 4)==4) {
                for (i=0;i<k;i++) {
                    tmp = path.getArith(i);
                    if (tmp!=null && tmp2.unsat(tmp)) {
                        return false;
                    }
                }
            }
        }

        int nvars = input.nSymbolicArithInputValues();
        initDependency(nvars);
        lc = getArithContsraints(k);


        BinarySearch b = new BinarySearch();
        while (true) {
            try {
                lp = LpSolve.makeLp(0,nvars+1);

                inf = lp.getInfinite();
                for (i=1;i<=nvars+1;i++) {
                    if (i!=nvars+1) {
                        lp.setBounds(i,-inf,inf);
                        int typ = input.symbolicArithInputType(i);
                        switch(typ){
                            case Globals.INT:
                                lp.setBounds(i,Integer.MIN_VALUE,Integer.MAX_VALUE);
                                break;
                            case Globals.SHORT:
                                lp.setBounds(i,Short.MIN_VALUE,Short.MAX_VALUE);
                                break;
                            case Globals.LONG:
                                lp.setBounds(i,Long.MIN_VALUE,Long.MAX_VALUE);
                                break;
                            case Globals.BYTE:
                                lp.setBounds(i,Byte.MIN_VALUE,Byte.MAX_VALUE);
                                break;
                            case Globals.CHAR:
                                lp.setBounds(i,Character.MIN_VALUE,Character.MAX_VALUE);
                                break;
                            case Globals.BOOLEAN:
                                lp.setBounds(i,0,1);
                                break;
                            case Globals.FLOAT:
                                lp.setBounds(i,Integer.MIN_VALUE,Integer.MAX_VALUE);
                                break;
                            case Globals.DOUBLE:
                                lp.setBounds(i,Integer.MIN_VALUE,Integer.MAX_VALUE);
                                break;
                        }
                        if(typ!= Globals.FLOAT && typ!=Globals.DOUBLE)
                            lp.setInt(i,true);
                    } else {
                        lp.setInt(i,true);
                    }
                }
                lp.setAddRowmode(true);
                tmp = new ArithmeticExpression(nvars+1);
                lp.setObjFnex(1,tmp.coeff,tmp.svar);

                sz = lc.size();
                for (i=0;i<sz;i++) {
                    tmp = (ArithmeticExpression)lc.get(i);
                    if (tmp.type==ArithmeticExpression.NE) {
                        if (b.get()!=0) {
                            lp.addConstraintex(tmp.svar.length,tmp.coeff,tmp.svar,LpSolve.LE,tmp.constant-1);
                        } else {
                            lp.addConstraintex(tmp.svar.length,tmp.coeff,tmp.svar,LpSolve.GE,tmp.constant+1);
                        }
                    } else if (tmp.type==ArithmeticExpression.EQ) {
                        lp.addConstraintex(tmp.svar.length,tmp.coeff,tmp.svar,LpSolve.EQ,tmp.constant);
                    } else {
                        lp.addConstraintex(tmp.svar.length,tmp.coeff,tmp.svar,tmp.type,tmp.constant);
                    }
                }
                lp.setAddRowmode(false);
                lp.setVerbose(LpSolve.CRITICAL);
                status=lp.solve();
                if (LpSolve.OPTIMAL == status) {
                    double[] sol;
                    sol = lp.getPtrPrimalSolution();
                    tmp = (ArithmeticExpression)lc.get(0);
                    if (counter==0)
                        inputArithUpdate(sol,lp.getNrows(),tmp.svar[0]);
                    lp.deleteLp();
                    return true;
                } else {
                    lp.deleteLp();
                    if (b.isComplete()) {
                        return false;
                    }
                    b.addOne();
                }
            } catch (LpSolveException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void inputArithUpdate(double[] sol, int begin, int x) {
        int j;
        int sz = input.nSymbolicArithInputValues();
        for(j=1;j<=sz;j++){
            if(isEqual(x,j)){
                input.updateArithInput(j,sol[begin+j]);
            }
        }
    }
}
