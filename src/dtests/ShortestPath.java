package dtests;

import cute.dcute.Actor;
import cute.dcute.Message;
import cute.dcute.DScheduler;
import cute.Cute;

import java.util.Vector;
import java.util.Iterator;

/**
 *  .
 * User: ksen
 * Date: Oct 11, 2005
 * Time: 11:34:48 PM
 * To change this template use File | Settings | File Templates.
 */

class SpMsg extends Message {
    public SPProcess sender;
    public int d;
    public int w;

    public SpMsg(SPProcess sender, int d, int w) {
        this.sender = sender;
        this.d = d;
        this.w = w;
    }
}

class SPProcess extends Actor {
    public int D = -1;
    public SPProcess N = null;
    private Vector neigbors = new Vector();
    private Vector neigborsD = new Vector();

    public void addNeighbor(SPProcess n,int d){
        neigbors.add(n);
        neigborsD.add(new Integer(d));
    }

    /* to start a.send(a,new SpMsg(null,0,0)); */

    public void receive(Message m) {
        SpMsg msg = (SpMsg)m;
        if(D==-1 || D > msg.d + msg.w){
            D = msg.d + msg.w;
            N = msg.sender;
            Iterator iter2 = neigborsD.iterator();
            for (Iterator iterator = neigbors.iterator(); iterator.hasNext();) {
                SPProcess spProcess = (SPProcess) iterator.next();
                int w = ((Integer)iter2.next()).intValue();
                send(spProcess,new SpMsg(this,D,w));
            }
        }
    }
}

public class ShortestPath {

    public static void main(String[] args) {
        SPProcess a0 = new SPProcess();
        SPProcess a1 = new SPProcess();
        SPProcess a2 = new SPProcess();
        SPProcess a3 = new SPProcess();
        SPProcess a4 = new SPProcess();
        if(Cute.N==4){
            a0.addNeighbor(a1,10);
            a1.addNeighbor(a0,10);
            a0.addNeighbor(a2,10);
            a2.addNeighbor(a0,10);
            a0.addNeighbor(a3,10);
            a3.addNeighbor(a0,10);
            a1.addNeighbor(a3,10);
            a3.addNeighbor(a1,10);
            a2.addNeighbor(a3,10);
            a3.addNeighbor(a2,10);
            a0.send(a0,new SpMsg(null,0,0));
        } else {
            a0.addNeighbor(a1,10);
            a1.addNeighbor(a0,10);
            a0.addNeighbor(a2,10);
            a2.addNeighbor(a0,10);
            a1.addNeighbor(a3,10);
            a3.addNeighbor(a1,10);
//            a2.addNeighbor(a3,10);
//            a3.addNeighbor(a2,10);
            a4.addNeighbor(a1,10);
            a4.addNeighbor(a0,10);
            a1.addNeighbor(a4,10);
            a0.addNeighbor(a4,10);
            a4.send(a4,new SpMsg(null,0,0));
        }
        DScheduler.sched.start();
    }
}
