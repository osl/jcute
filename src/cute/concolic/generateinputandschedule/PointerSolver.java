package cute.concolic.generateinputandschedule;

import cute.concolic.pathconstraint.PointerConstraint;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.input.InputMap;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class PointerSolver {
    private PathConstraint path;
    private InputMap input;
    public int[] nodes;
    private boolean [][] edges;

    public PointerSolver(PathConstraint path, InputMap input) {
        this.path = path;
        this.input = input;
    }

    public void initDependency(){
        int i,j;

        nodes = new int[input.nSymbolicPointerInputValues()+1];
        for (i=0;i<nodes.length;i++) {
            nodes[i]=i;
        }
        edges = new boolean [nodes.length][nodes.length];
        for (i=0;i<nodes.length;i++) {
            for (j=0;j<nodes.length;j++) {
                edges[i][j]=false;
            }
        }
    }

    public boolean isOkInequal(int first, int second) {
        int i,j;
        i = nodes[first];
        j = nodes[second];
        return(i!=j);
    }


    public boolean isOkEqual(int first, int second) {
        int i,j;
        i = nodes[first];
        j = nodes[second];
        return !(edges[i][j]);
    }

    public void addEqual(int first, int second) {
        int k,i,j,tmp;

        i = nodes[first];
        j = nodes[second];
        if (edges[i][j]) {
            System.err.println("Pointers "+first+" "+second+" are already inequal; they cannot be made equal");
            System.exit(1);
        }
        if (i!=j) {
            if (j<i) {
                tmp = j;
                j=i;
                i = tmp;
            }
            for (k=1;k<nodes.length;k++) {
                if (nodes[k]==j) {
                    nodes[k]=i;
                }
                edges[i][k]=edges[j][k] || edges[i][k];
                edges[k][i]=edges[k][j] || edges[i][k];
            }
        }
    }

    public void addInequal(int first, int second) {
        int i,j;

        i = nodes[first];
        j = nodes[second];
        if (i==j) {
            System.err.println("Pointers "+first+" "+second+" are already equal; they cannot be made inequal");
            System.exit(1);
        } else {
            edges[i][j]=true;
            edges[j][i]=true;
        }
    }

    public boolean isEqual(int first, int second){
        return !isOkInequal(first,second);
    }

    public boolean isInequal(int first, int second){
        return !isOkEqual(first,second);
    }

    public boolean solvePointer(int k,int counter){
        int i;
        PointerConstraint tmp=null;

        initDependency();
        for (i=0;i<=k;i++) {
            tmp = path.getPointer(i);

            if (tmp!=null) {
                if (i==k) {
                    if (tmp.equal && !isOkInequal(tmp.first,tmp.second)) {
                        return false;
                    } else if (!tmp.equal && !isOkEqual(tmp.first,tmp.second)) {
                        return false;
                    }
                } else {
                    if (tmp.equal) {
                        addEqual(tmp.first,tmp.second);
                    } else {
                        addInequal(tmp.first,tmp.second);
                    }
                }
            }
        }
        if (counter==0)
            input.updatePointerInput(this,tmp.first,tmp.second,!tmp.equal);
        return true;
    }

}
