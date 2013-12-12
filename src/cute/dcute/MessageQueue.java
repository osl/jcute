package cute.dcute;

import java.util.LinkedList;

/**
 *  .
 * User: ksen
 * Date: Oct 11, 2005
 * Time: 3:13:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageQueue {
    private int qid;
    private Actor a;
    private LinkedList q;

    public MessageQueue(Actor a, int qid) {
        this.a = a;
        this.qid = qid;
        q = new LinkedList();
    }

    public boolean equals(Actor from, int qid) {
        return from==a && qid==this.qid;
    }

    public void addMessage(Message m) {
        q.addLast(m);
    }

    public Message getAndRemove() {
        return (Message)q.removeFirst();
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }
}
