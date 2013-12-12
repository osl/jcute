package cute.dcute;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *  .
 * User: ksen
 * Date: Oct 9, 2005
 * Time: 3:09:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActorQueues {
    private Actor actor;
    private LinkedList q;

    public ActorQueues(Actor actor) {
        this.actor = actor;
        q = new LinkedList();
    }

    public void addMessage(Actor from,int qid,Message m) {
        boolean found = false;
        MessageQueue  messageQueue = null;
        for (Iterator iterator = q.iterator(); !found && iterator.hasNext();) {
            messageQueue = (MessageQueue) iterator.next();
            if(messageQueue.equals(from,qid)){
                found = true;
            }
        }
        if(!found){
            messageQueue = new MessageQueue(from,qid);
            q.addLast(messageQueue);
        }
        messageQueue.addMessage(m);
/*
        if(q.size()>1){
            System.out.println("Actor has more than one queue");
        }
*/
    }

    public Actor getActor() {
        return actor;
    }

    public Message getAndRemove(int mid) {
        Message ret = null;
        MessageQueue mq = (MessageQueue)q.get(mid);
        ret = (Message)mq.getAndRemove();
        if(mq.isEmpty()){
            q.remove(mid);
        }
        return ret;
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }

    public int size() {
        return q.size();
    }

    public LinkedList getQ() {
        return q;
    }
}
