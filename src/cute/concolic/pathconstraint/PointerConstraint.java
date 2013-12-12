package cute.concolic.pathconstraint;

import java.io.PrintWriter;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class PointerConstraint implements Constraint{
    public int first;
    public int second;
    public boolean equal;

    public PointerConstraint(int first, int second, boolean equal) {
        this.first = first;
        this.second = second;
        this.equal = equal;
    }

    public void printConstraint(PrintWriter out) {
        if(first==0){
            out.print("null");
        } else {
            out.print("p");
            out.print(first);
        }
        if(equal){
            out.print(" == ");
        } else {
            out.print(" != ");
        }
        if(second==0){
            out.println("null");
        } else {
            out.print("p");
            out.println(second);
        }
    }
}
