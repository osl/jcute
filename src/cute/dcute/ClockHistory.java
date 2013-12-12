package cute.dcute;

import cute.concolic.pathconstraint.DSchedule;

import java.util.IdentityHashMap;
import java.util.LinkedList;

/**
 *  .
 * User: ksen
 * Date: Oct 11, 2005
 * Time: 3:59:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClockHistory extends Object {
    private Actor actor;
    private IdentityHashMap vc;
    private LinkedList queues;
    private DSchedule ds;

    public Actor getActor() {
        return actor;
    }

    public IdentityHashMap getVc() {
        return vc;
    }

    public LinkedList getQueues() {
        return queues;
    }

    public DSchedule getDs() {
        return ds;
    }

    public ClockHistory(ActorQueues aq, DSchedule ds) {
        actor = aq.getActor();
        vc = new IdentityHashMap(actor.vc);
        queues = new LinkedList(aq.getQ());
        this.ds = ds;
    }
}
