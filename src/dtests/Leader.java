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

class LMsg extends Message {
    public int type;
    public int id;

    public LMsg(int type, int id) {
        this.type = type;
        this.id = id;
    }
}

class LProcess extends Actor {
    private int number, maxi, neighborR;
    public boolean active;
    private LProcess right;

    final public static int first = 0;
    final public static int second = 1;

    public void init(LProcess right, int id) {
        this.right = right;
        this.maxi = id;
        this.active = true;
        send(right,0,new LMsg(first,id));
    }


    public void receive(Message m) {
        LMsg msg = (LMsg)m;
        if(msg.type==first){
            number = msg.id;
            if(active && number!=maxi){
                send(right,0,new LMsg(second,number));
                neighborR = number;
            } else if(!active) {
                send(right,0,new LMsg(first,number));
            }
        } else if(msg.type==second){
            number = msg.id;
            if(active){
                if(neighborR>number && neighborR>maxi){
                    maxi = neighborR;
                    send(right,0,new LMsg(first,neighborR));
                } else {
                    active = false;
                }
            } else {
                send(right,0,new LMsg(second,number));
            }
        }
    }
}

public class Leader {

    public static void main(String[] args) {
        int i,j,n=Cute.N;
        LProcess[] ps = new LProcess[n];
        for(i=0;i<n;i++){
            ps[i] = new LProcess();
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
