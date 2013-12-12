package cute.concolic.generateinputandschedule;

import cute.concolic.ObjectInput;
import cute.concolic.ObjectOutput;
import cute.concolic.Information;

import java.io.IOException;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class Counter {
    public static void inc(Information information) {
        int[] ret;

        ret = read(information);
        (ret[1])++;
        write(1,ret[1]);
    }

    public static int get(Information information) {
        int[] ret = read(information);
        if (ret[0]!=0) {
            write(0,ret[1]);
            return ret[1];
        } else {
            write(0,0);
            return 0;
        }
    }

    private static int[] read(Information information){
        int flag;
        int times;

        ObjectInput in = new ObjectInput("Counter",false,information);
    	if (in.ok()) {
            try{
                flag = in.in.readInt();
                times = in.in.readInt();
            } catch(IOException ex){
                in.close();
                times = 0;
                flag = 0;
            }
        } else {
            flag = 0;
            times=0;
        }
        int[] ret = new int[2];
        ret[0] = flag;
        ret[1] = times;
        return ret;
    }

    private static void write(int flag,int times){
        ObjectOutput out = new ObjectOutput("Counter");

        if (out.ok()) {
            try{
                out.out.writeInt(flag);
                out.out.writeInt(times);
            } catch(IOException ex){
                out.close();
                ex.printStackTrace();
                System.exit(1);
            }
            out.close();
        }
    }

}
