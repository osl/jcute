package dtests;

import cute.dcute.Actor;
import cute.dcute.Message;
import cute.dcute.DScheduler;
import cute.Cute;

/**
 *  .
 * User: ksen
 * Date: Oct 9, 2005
 * Time: 7:56:16 PM
 * To change this template use File | Settings | File Templates.
 */
class Message1 extends Message {
    public int x;

    public Message1(int x) {
        this.x = x;
    }
}

class Actor1 extends Actor{
    Actor2 a1;
    Actor3 a2;

    public Actor1(Actor2 a1, Actor3 a3) {
        this.a1 = a1;
        this.a2 = a3;
    }

    public void receive(Message m) {
        System.out.println("a1");
        int x = Cute.input.Integer();
        send(a1,new Message1(1));
        send(a2,new Message1(4));
        send(a2,new Message1(x));
    }
}

class Actor2 extends Actor {
    Actor3 a3;

    public Actor2(Actor3 a3) {
        this.a3 = a3;
    }

    public void receive(Message m) {
        System.out.println("a2");
    }
}

class Actor3 extends Actor {
    int x;
    boolean first = true;

    public void receive(Message m) {
        System.out.println("a3");
        int y = Cute.input.Integer();
        Message1 m1 = (Message1)m;
        if(first){
            first = false;
        } else {
            if(m1.x==2*y+1)
                System.out.println("Error");
        }
    }
}

public class DExample1 {
    public static void main(String[] args) {
        Actor3 a3 = new Actor3();
        Actor2 a2 = new Actor2(a3);
        Actor1 a1 = new Actor1(a2,a3);
        a1.send(a1,new Message1(0));
        DScheduler.sched.start();
    }
}
