package cute.concolic.symbolicstate;

import cute.concolic.pathconstraint.Constraint;
import cute.concolic.Globals;

import java.io.PrintWriter;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class ArithmeticExpression extends Expression implements Constraint {

    public double[] coeff;
    public int[] svar;
    public int type;

    public double constant;
    private static final int LE = 1;
    private static final int GE = 2;
    public static final int EQ = 3;
    public static final int NE = 4;

    private ArithmeticExpression() {
    }

    public ArithmeticExpression(int i){
        svar = new int[1];
        svar[0] = i;
        coeff = new double[1];
        coeff[0] = 1.0;
        constant = 0.0;
    }

    public ArithmeticExpression(ArithmeticExpression from){
        constant = from.constant;
        type = from.type;
        svar = new int[from.svar.length];
        coeff = new double[from.coeff.length];
        for (int i = 0; i < coeff.length; i++) {
            coeff[i] = from.coeff[i];
            svar[i] = from.svar[i];
        }
    }

    public ArithmeticExpression negate() {
        ArithmeticExpression ret = new ArithmeticExpression(this);
        ret.constant = - constant;
        return ret;
    }

    public ArithmeticExpression add(ArithmeticExpression second,boolean add) {
        int count = coeff.length+second.coeff.length;
        for (int i = 0; i < coeff.length; i++) {
            for (int j = 0; j < second.coeff.length; j++) {
                if(svar[i]==second.svar[j]){
                    count--;
                    if((add && coeff[i]==-second.coeff[j]) || (!add && coeff[i]==second.coeff[j])){
                        count--;
                    }
                }
            }
        }
        if(count==0){
            return null;
        }
        ArithmeticExpression ret = new ArithmeticExpression();
        ret.coeff = new double[count];
        ret.svar = new int[count];
        int k =0;

        ret.constant = constant;
        if(add) ret.constant += second.constant;
        else ret.constant -= second.constant;
        boolean flag;
        for (int i = 0; i < coeff.length; i++) {
            flag = false;
            for (int j = 0; !flag && j < second.coeff.length; j++) {
                if(svar[i]==second.svar[j]){
                    flag = true;
                }
            }
            if(!flag){
                ret.coeff[k] = coeff[i];
                ret.svar[k] = svar[i];
                k++;
            }
        }
        for (int i = 0; i < second.coeff.length; i++) {
            flag = false;
            for (int j = 0; !flag && j < coeff.length; j++) {
                if(svar[i]==second.svar[j]){
                    flag = true;
                }
            }
            if(!flag){
                if(add)
                    ret.coeff[k] = second.coeff[i];
                else
                    ret.coeff[k] = -second.coeff[i];
                ret.svar[k] = second.svar[i];
                k++;
            }
        }
        for (int i = 0; i < coeff.length; i++) {
            for (int j = 0; j < second.coeff.length; j++) {
                if(svar[i]==second.svar[j]){
                    if((add && coeff[i]==-second.coeff[j]) || (!add && coeff[i]==second.coeff[j])){

                    } else {
                        ret.svar[k] = svar[i];
                        if(add)
                            ret.coeff[k] = coeff[i]+second.coeff[j];
                        else
                            ret.coeff[k] = coeff[i]-second.coeff[j];
                        k++;
                    }
                }
            }
        }
        return ret;
    }

    public ArithmeticExpression add(ArithmeticExpression second) {
        return add(second,true);
    }

    public ArithmeticExpression subtract(ArithmeticExpression second) {
        return add(second,false);
    }

    public ArithmeticExpression multiply(double value) {
        if(value==0) return null;
        ArithmeticExpression ret = new ArithmeticExpression(this);
        for (int i = 0; i < ret.coeff.length; i++) {
            ret.coeff[i] *= value;
        }
        ret.constant *= value;
        return ret;
    }

    public ArithmeticExpression add(double value) {
        ArithmeticExpression ret = new ArithmeticExpression(this);
        ret.constant += value;
        return ret;
    }

    public ArithmeticExpression subtractFrom(double value) {
        ArithmeticExpression ret = new ArithmeticExpression(this);
        for (int i = 0; i < ret.coeff.length; i++) {
            ret.coeff[i] = -ret.coeff[i];
        }
        ret.constant = value-ret.constant;
        return ret;
    }

    public ArithmeticExpression subtract(double value) {
        return add(-value);
    }

    public void setL() {
        type = LE;
        constant = -(1+constant);
    }

    public void setGE() {
        type = GE;
        constant = - constant;
    }

    public void setG() {
        type = GE;
        constant = 1 - constant;
    }

    public void setLE() {
        type = LE;
        constant = - constant;
    }

    public void setEQ() {
        type = EQ;
        constant = - constant;
    }

    public void setNE() {
        type = NE;
        constant = - constant;
    }

    public void printExpression(PrintWriter out){
        for (int i = 0; i < coeff.length; i++) {
            if(i!=0){
                out.print(" + ");
            }
            out.print(coeff[i]);
            out.print(" x");
            out.print(svar[i]);
        }
        out.print(" + ");
        out.println(constant);
    }

    public void printConstraint(PrintWriter out){
        for (int i = 0; i < coeff.length; i++) {
            if(i!=0){
                out.print(" + ");
            }
            out.print(coeff[i]);
            out.print(" x");
            out.print(svar[i]);
        }
        if(type==LE)
            out.print(" <= ");
        if(type==GE)
            out.print(" >= ");
        if(type==EQ)
            out.print(" == ");
        if(type==NE)
            out.print(" != ");
        out.println(constant);
    }

    public void checkValidity(){
        double sum = 0;
        boolean ret = false;
        for (int i = 0; i < coeff.length; i++) {
            sum += coeff[i]* Globals.globals.input.getArithInput(svar[i]);
        }
        if(type==LE)
            ret = (sum <= constant);
        if(type==GE)
            ret = (sum >= constant);
        if(type==EQ)
            ret = (sum == constant);
        if(type==NE)
            ret = (sum != constant);
        if(!ret){
            System.err.println("Validity check of contraint failed");
            PrintWriter out = new PrintWriter(System.out);
            printConstraint(out);
            out.flush();
            System.exit(1);
        } else {
            PrintWriter out = new PrintWriter(System.out);
            printConstraint(out);
            out.flush();
        }
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof ArithmeticExpression)) return false;
        if(this==obj) return true;
        ArithmeticExpression c1 = this;
        ArithmeticExpression c2 = (ArithmeticExpression)obj;

        int i,j,count,t2;
        boolean first;
        double times=1.0;

        first = true;

        if (c1==null && c2==null)	return true;

        if (c1==null || c2==null)	return false;

        count = c1.svar.length + c2.svar.length;
        for (i=0;i<c1.svar.length;i++) {
            for (j=0;j<c2.svar.length;j++) {
                if (c1.svar[i]== c2.svar[j]) {
                    (count)--;
                    if (first) {
                        first = false;
                        times = c1.coeff[i]/c2.coeff[j];
                        count--;
                    } else if (c1.coeff[i] == times*c2.coeff[j]) {
                        (count)--;
                    }
                }
            }
        }

        if (count==0) {
            t2 = c2.type;
            if (times<0) {
                if (t2==GE)	t2 = LE;
                else if (t2==LE) t2 = GE;
            }
            if (c1.constant == times * (c2.constant) && (c1.type==t2)) {
                if (t2==NE && (times != 1 || times != -1)) return false;
                return true;
            }
        }
        return false;

    }

    public void invert() {
        switch (type) {
        case GE:
            type = LE;
            constant = constant - 1;
            break;
        case LE:
            type = GE;
            constant = 1 + constant;
            break;
        case EQ:
            type = NE;
            break;
        case NE:
            type = EQ;
            break;
        }
    }


    public boolean unsat(ArithmeticExpression c2) {
        int i,j,count,t2;
        boolean first;
        double times=1.0;

        first = true;
        ArithmeticExpression c1 = this;

        if (c1==null || c2==null)	return false;

        count = c1.svar.length + c2.svar.length;
        for (i=0;i<c1.svar.length;i++) {
            for (j=0;j<c2.svar.length;j++) {
                if (c1.svar[i] == c2.svar[j]) {
                    (count)--;
                    if (first) {
                        first = false;
                        times = c1.coeff[i] / c2.coeff[j];
                        count--;
                    } else if (c1.coeff[i] == times*c2.coeff[j]) {
                        (count)--;
                    }
                }
            }
        }

        if (count==0) {
            if (c1.constant == times * (c2.constant) && ((c1.type==NE && c2.type==EQ) || (c1.type==EQ && c2.type==NE))) {
                return true;
            }
            if (c1.constant != times * (c2.constant) && (c1.type==EQ && c2.type==EQ)) {
                return true;
            }
            t2 = c2.type;
            if (times<0) {
                if (t2==GE)	t2 = LE;
                else if (t2==LE) t2 = GE;
            }
            if (c1.constant > times * (c2.constant) && (c1.type==GE && t2==EQ)) {
                return true;
            }
            if (c1.constant > times * (c2.constant) && (c1.type==EQ && t2==LE)) {
                return true;
            }
            if (c1.constant > times * (c2.constant) && (c1.type==GE && t2==LE)) {
                return true;
            }
            if (c1.constant < times * (c2.constant) && (c1.type==LE && t2==EQ)) {
                return true;
            }
            if (c1.constant < times * (c2.constant) && (c1.type==EQ && t2==GE)) {
                return true;
            }
            if (c1.constant < times * (c2.constant) && (c1.type==LE && t2==GE)) {
                return true;
            }
        }
        return false;
    }

}
