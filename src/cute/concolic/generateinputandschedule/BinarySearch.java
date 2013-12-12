package cute.concolic.generateinputandschedule;

import java.util.Vector;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class BinarySearch {
    private Vector bin;
    private int index;

    public BinarySearch() {
        bin = new Vector(20);
        index = 0;
    }

    public boolean isComplete(){
        int i,sz;

        sz = bin.size();
        for (i=0;i<sz;i++) {
            if (((Byte)bin.get(i)).byteValue()==0)
                return false;
        }
        return true;
    }

    byte get(){
        if (index < bin.size()) {
            index++;
            return ((Byte)bin.get(index-1)).byteValue();
        } else {
            bin.add(new Byte((byte)0));
            index++;
            return 0;
        }
    }

    void addOne(){
        int i;
        byte val;

        index=0;
        i = bin.size();
        while (true) {
            i--;
            assert(i>=0);
            val = ((Byte)bin.get(i)).byteValue();
            if (val!=0) {
                bin.set(i,new Byte((byte)0));
            } else {
                bin.set(i,new Byte((byte)1));
                return;
            }
        }
    }

    void print(){
        int i,val;

        System.out.print("Binary\n");
        i = bin.size();
        while (i>0) {
            i--;
            val = ((Byte)bin.get(i)).byteValue();
            System.out.println(val);
        }
    }

}
