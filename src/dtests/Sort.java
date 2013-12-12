package dtests;

import cute.dcute.Actor;
import cute.dcute.Message;
import cute.dcute.DScheduler;
import cute.Cute;

/**
 *  .
 * User: ksen
 * Date: Oct 10, 2005
 * Time: 10:39:35 AM
 * To change this template use File | Settings | File Templates.
 */

class SMsg extends Message {
    public int type;
    public int v;

    public SMsg(int type,int v) {
        this.v = v;
        this.type = type;
    }
}

class SProcess extends Actor{
    private SProcess left;
    private SProcess right;
    public int v;

    public void init(SProcess left, SProcess right) {
        this.left = left;
        this.right = right;
        this.v = Cute.input.Integer();
        if(left!=null){
            send(left,new SMsg(Sort.notifyl,v));
        }
        if(right!=null){
            send(right,new SMsg(Sort.notifyr,v));
        }
    }

    public void receive(Message m) {
        SMsg msg = (SMsg)m;
        if(msg.type==Sort.update){
            v = msg.v;
            if(left!=null){
                send(left,new SMsg(Sort.notifyl,v));
            }
            if(right!=null){
                send(right,new SMsg(Sort.notifyr,v));
            }
        } else if(msg.type==Sort.notifyl){
            if(v>msg.v){
                send(right,new SMsg(Sort.update,v));
                send(this,new SMsg(Sort.update,msg.v));
            }
        } else if(msg.type == Sort.notifyr){
            if(msg.v > v){
                send(left,new SMsg(Sort.update,v));
                send(this,new SMsg(Sort.update,msg.v));
            }
        }
    }
}

public class Sort {
    public static final int N = 4;
    public static final int notifyl = 1;
    public static final int notifyr = 2;
    public static final int update = 3;

    public static void main(String[] args) {
        SProcess[] sp = new SProcess[N];
        int i;
        for(i=0;i<N;i++){
            sp[i] = new SProcess();
        }
        for(i=0;i<N;i++){
            sp[i].init(i==0?null:sp[i-1],i==(N-1)?null:sp[i+1]);
        }
        DScheduler.sched.start();
        for(i=0;i<N-1;i++){
            Cute.Assert(sp[i].v<=sp[i+1].v);
            System.out.print(sp[i].v+" ");
        }
        System.out.println(sp[i].v);
    }
}
