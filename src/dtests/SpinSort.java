package dtests;

import cute.dcute.Actor;
import cute.dcute.Message;
import cute.dcute.DScheduler;
import cute.Cute;

/**
 *  .
 * User: ksen
 * Date: Oct 11, 2005
 * Time: 5:49:52 PM
 * To change this template use File | Settings | File Templates.
 */

class SpinSMsg extends Message {
    public int v;

    public SpinSMsg(int v) {
        this.v = v;
    }
}

class PLeft extends Actor{
    private Actor out;

    public PLeft(Actor out) {
        this.out = out;
    }

    public void init() {
        int counter = 0;
        int seed = 15;
        while(true){
            send(out,1,new SpinSMsg(Cute.input.Integer()));
            counter++;
            if(counter==SpinSort.N){
                return;
            }
            seed = (seed*3 +14)%100;
        }
    }

    public void receive(Message m) {

    }
}

class PMiddle extends Actor {
    private Actor out;
    private int counter;
    public int myval;
    private int nextval;
    private boolean first = true;

    public PMiddle(Actor out,int procnum) {
        this.out = out;
        counter = SpinSort.N - procnum;
    }

    public void receive(Message m) {
        SpinSMsg msg = (SpinSMsg)m;
        if(first){
            myval=msg.v;
            first = false;
        } else if(counter>0){
            nextval = msg.v;
            if(nextval>=myval)
                send(out,1,new SpinSMsg(nextval));
            else {
                send(out,1,new SpinSMsg(myval));
                myval = nextval;
            }
            counter--;
        }
    }
}

public class SpinSort {
    public static int N = 5;

    public static void main(String[] args) {

        N = Cute.N;
        Actor tmp = null;
        PMiddle[] a = new PMiddle[N];

        for(int i=0;i<N;i++){
            tmp = a[N-i-1]= new PMiddle(tmp,N-i);
        }
        tmp = new PLeft(tmp);
        ((PLeft)tmp).init();
        DScheduler.sched.start();
        for(int j=0;j<N-1;j++){
            Cute.Assert(a[j].myval<=a[j+1].myval);
        }
    }
}
