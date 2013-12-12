package dtests;

import cute.dcute.Message;
import cute.dcute.Actor;
import cute.dcute.DScheduler;
import cute.Cute;

/**
 *  .
 * User: ksen
 * Date: Oct 10, 2005
 * Time: 9:41:32 AM
 * To change this template use File | Settings | File Templates.
 */

class LMsgB extends Message {
    public int type;
    public int id;

    public LMsgB(int type, int id) {
        this.type = type;
        this.id = id;
    }
}

class LProcessB extends Actor {
    private int number, maxi, neighborR;
    public boolean active;
    private LProcessB right;

    final public static int first = 0;
    final public static int second = 1;

    public void init(LProcessB right, int id) {
        this.right = right;
        this.maxi = id;
        this.active = true;
        send(right,new LMsgB(first,id));
    }


    public void receive(Message m) {
        LMsgB msg = (LMsgB)m;
        if(msg.type==first){
            number = msg.id;
            if(active && number!=maxi){
                send(right,new LMsgB(second,number));
                neighborR = number;
            } else if(!active) {
                send(right,new LMsgB(first,number));
            }
        } else if(msg.type==second){
            number = msg.id;
            if(active){
                if(neighborR>number && neighborR>maxi){
                    maxi = neighborR;
                    send(right,new LMsgB(first,neighborR));
                } else {
                    active = false;
                }
            } else {
                send(right,new LMsgB(second,number));
            }
        }
    }
}

public class LeaderBuggy {

    public static void main(String[] args) {
        int i,j,n=Cute.N;
        LProcessB[] ps = new LProcessB[n];
        for(i=0;i<n;i++){
            ps[i] = new LProcessB();
        }
        int[] ids = new int[n];
        for(i=0;i<n;i++){
            ids[i] = Cute.input.Integer();
            for(j=0;j<i;j++){
                Cute.Assume(ids[i]!=ids[j]);
            }
            ps[i].init(ps[(i+1)%n],ids[i]);
        }
        DScheduler.sched.start();

        int count = 0;
        for(i=0;i<n;i++){
            if(ps[i].active) count++;
        }
        Cute.Assert(count==1);

    }
}
