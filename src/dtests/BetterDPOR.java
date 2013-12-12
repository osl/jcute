package dtests;

import cute.dcute.Actor;
import cute.dcute.Message;
import cute.dcute.DScheduler;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 17, 2005
 * Time: 10:12:03 PM
 */

class BetterDPORMsg extends Message {
    public int v;
    public Actor client;

    public BetterDPORMsg(int v,Actor client) {
        this.v = v;
        this.client = client;
    }
}

class XVar extends Actor {
    private int x;

    public void receive(Message m) {
        BetterDPORMsg msg = (BetterDPORMsg)m;
        x = msg.v;
        System.out.println("x = " + x);
        send(msg.client,new Message());
    }
}

class A1 extends Actor {
    int i=0;
    XVar x;

    public A1(XVar x){
        this.x = x;
        send(this,new Message());
    }

    public void receive(Message m) {
        i++;
        if(i==1){
            send(x,new BetterDPORMsg(2,this));
        } else if(i==2){
            send(x,new BetterDPORMsg(3,this));
        }
    }
}

class A2 extends Actor {
    int i=0;
    XVar x;
    XVar y;

    public A2(XVar x,XVar y){
        this.x = x;
        this.y = y;
        send(this,new Message());
    }

    public void receive(Message m) {
        i++;
        if(i==1){
            send(y,new BetterDPORMsg(1,this));
        } else if(i==2){
            send(x,new BetterDPORMsg(4,this));
        }
    }
}

public class BetterDPOR {
    public static void main(String[] args) {
        XVar x = new XVar();
        XVar y = new XVar();
        new A2(x,y);
        new A1(x);
        DScheduler.sched.start();
    }
}
