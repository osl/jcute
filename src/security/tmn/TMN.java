package security.tmn;

import security.Agent;
import security.Message;
import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
class A extends Agent{
    int state;
    int nonce;


    public A(int numGuards,int numIterations) {
        super(numGuards,numIterations);
        state = TMN.SLEEP;
    }

    public boolean guard1(Message m){
      return state==TMN.SLEEP;
    }


    public void action1(Message m){
        TMNMessage net = (TMNMessage)m;
        net.from = TMN.a;
        net.to = TMN.s;
        net.dest = TMN.b;
        net.nonce = TMN.a;
        net.key = TMN.s;
        this.state=TMN.WAIT;
    }

    public boolean guard2(Message m){
        TMNMessage net = (TMNMessage)m;
        return TMN.b==net.dest && this.state==TMN.WAIT && net.from==TMN.s && net.to==TMN.a && net.key==TMN.a;
    }

    public void action2(Message m){
        TMNMessage net = (TMNMessage)m;
        state=TMN.COMMIT;
        nonce = net.nonce;
    }

}

class SState {
    int initiator;
    int responder;
    int nonce;
}


class Server extends Agent{
    SState[] states;
    int i;

    public Server(int numGuards, int numIterations) {
        super(numGuards, numIterations);
        states = new SState[TMN.MAXSTATES];
        for (int j = 0; j < states.length; j++) {
            states[j] = new SState();
        }
        i = 0;
    }

    public boolean guard1(Message m){
        TMNMessage net = (TMNMessage)m;
        return (net.to==TMN.s && net.key==TMN.s);
    }

    public void action1(Message m){
        int j;
        TMNMessage net = (TMNMessage)m;


        for(j=0;j<i;j++){
            if(states[j].initiator==net.dest && net.from == states[j].responder){
                net.from = TMN.s;
                net.to = states[j].initiator;
                net.dest = states[j].responder;
                net.key = states[j].nonce;
                Cute.Assert(!(net.to==TMN.ii && net.nonce==TMN.b && net.key==TMN.ii));
                return;
            }
        }
        if(i==TMN.MAXSTATES) i--;
        states[i].initiator = net.from;
        states[ i].responder = net.dest;
        states[ i].nonce = net.nonce;

        net.from = TMN.s;
        net.to =  states[ i].responder;
        net.dest =  states[ i].initiator;
        net.nonce=TMN.EMPTY;
        if( i<TMN.MAXSTATES) i++;

    }

}


class IState {
    int nonce;
    int key;
}

class Intruder extends Agent{

    public Intruder(int numGuards, int numIterations) {
        super(numGuards, numIterations);
        know = new IState[TMN.MAXKNOWLEDGE];
        for (int j = 0; j < know.length; j++) {
            know[j] = new IState();
        }
    }

    IState [] know;
    int i;

    public boolean guard1(Message m) {
        TMNMessage net = (TMNMessage)m;
        return net.nonce!=TMN.EMPTY;
    }

    public void action1(Message m) {
        TMNMessage net = (TMNMessage)m;

        int j;
        boolean flag;
        int drop = Cute.input.Integer();
        flag=false;
        for(j=0;!flag && j<i;j++){
            if(know[j].nonce==net.nonce && know[j].key==net.key)
                flag = true;
        }
        if(!flag && i<TMN.MAXKNOWLEDGE){
            know[i].nonce = net.nonce;
            know[i].key = net.key;
            i++;
        }
        if(drop!=0){
            net.invalidate();
        }
    }

    public boolean guard2(Message m) {
        return true;
    }

    public void action2(Message m) {
        TMNMessage net = (TMNMessage)m;
        int toss;

/*        #ifdef GENERAL
CU_input(toss);
CU_assume(toss==a || toss==b || toss==ii);
net.from = toss;
#else */
        net.from = TMN.ii;
/*    #endif */
        net.to = TMN.s;

/*    #ifdef GENERAL
CU_input(toss);
CU_assume(toss==a || toss==b || toss==ii);
net.dest = toss;
#else */
        net.dest = TMN.ii;
/*    #endif */

        net.nonce = TMN.ii;
        net.key = TMN.s;
    }

    public boolean guard3(Message m) {
        return i>0;
    }

    public void action3(Message m) {
        TMNMessage net = (TMNMessage)m;
        int toss,j;

/*        #ifdef GENERAL
CU_input(toss);
CU_assume(toss==a || toss==b || toss==ii);
net.from = toss;
#else */
        net.from = TMN.ii;
/*        #endif */
        net.to = TMN.s;

/*    #ifdef GENERAL
CU_input(toss);
CU_assume(toss==a || toss==b || toss==ii);
net.dest = toss;
#else */
        net.dest = TMN.ii;
/*    #endif */

        for(j=0;j<i;j++){
            toss = Cute.input.Integer();
            if(toss==0) {
                net.nonce = know[j].nonce;
                net.key = know[j].key;
                return;
            }
        }
        net.invalidate();
    }

}


class B extends Agent {

    public B(int numGuards, int numIterations) {
        super(numGuards, numIterations);
    }

    public boolean guard1(Message m){
        TMNMessage net = (TMNMessage)m;
        return net.from==TMN.s && net.to==TMN.b && net.nonce==TMN.EMPTY;
    }

    public void action1(Message m){
        TMNMessage net = (TMNMessage)m;

        net.from = TMN.b;
        net.to = TMN.s;
        net.nonce = TMN.b;
        net.key = TMN.s;
    }

}

class TMNMessage extends Message {

    public int from;
    public int to;
    public int dest;
    public int nonce;
    public int key;

    public TMNMessage() {
        invalidate();
    }

    public void invalidate(){
        to = TMN.EMPTY;
        from = TMN.EMPTY;
        nonce=TMN.EMPTY;
    }

    public void print(){
      System.out.print("from ");
      TMN.print_id(from);
      System.out.print(" to ");
      TMN.print_id(to);
      System.out.print(" dest ");
      TMN.print_id( dest);
      System.out.print(" nonce ");
      TMN.print_id( nonce);
      System.out.print(" key ");
      TMN.print_id( key);
      System.out.print("\n");
    }
}


public class TMN {
    final static  int EMPTY = -1;
    final static  int a = 0;
    final static  int b = 1;
    final static  int s = 2;
    final static  int ii = 3;
    final static  int MAXSTATES = 2;
    final static  int MAXKNOWLEDGE = 4;

    final static  int SLEEP = 0;
    final static  int WAIT = 1;
    final static  int COMMIT = 2;

    public static void print_id(int id){
      switch(id){
      case a:
        System.out.print("a");
        break;
      case b:
        System.out.print("b");
        break;
      case s:
        System.out.print("s");
        break;
      case ii:
        System.out.print("i");
        break;
      case EMPTY:
        System.out.print("empty");
        break;
      }
    }

    public static void main(String[] args) {
        A a = new A(2,1);
        B b = new B(1,1);
        Server s = new Server(1,3);
        Intruder i = new Intruder(3,3);
        TMNMessage m = new TMNMessage();
        m.m = m;

        /*

        for(int in=0;in<8;in++){
            int toss = Cute.input.Integer();
            if(toss==0){
                Cute.Assume(a.guard1(m));
                a.action1(m);
            } else if(toss==1){
                Cute.Assume(a.guard2(m));
                a.action2(m);
            } else if(toss==2){
                Cute.Assume(b.guard1(m));
                b.action1(m);
            } else if(toss==3){
                Cute.Assume(s.guard1(m));
                s.action1(m);
            } else if(toss==4){
                Cute.Assume(i.guard1(m));
                i.action1(m);
            } else if(toss==6){
                Cute.Assume(i.guard3(m));
                i.action3(m);
            } else {
                Cute.Assume(i.guard2(m));
                i.action2(m);
            }
        }
        */

        a.start();
        b.start();
        s.start();
        i.start();

        /*
        i.action2(m);
        assert(s.guard1(m));
        s.action1(m);
        m.print();
        assert(a.guard1(m));
        a.action1(m);
        assert(s.guard1(m));
        s.action1(m);
        m.print();
        assert(b.guard1(m));
        b.action1(m);
        assert(i.guard1(m));
        i.action1(m);
        assert(i.guard3(m));
        i.action3(m);
        m.print();
        assert(s.guard1(m));
        s.action1(m);
        */
    }
}

/*
void run_once(){
  int enabled[7];
  int i=0;
  int toss,drop;


  CU_input(toss);

  if(toss==0){
    CU_assume(rule_none_to_A_Enabled());
    rule_none_to_A_Action();
  } else if(toss==1){
    CU_assume(rule_S_to_A_Enabled());
    rule_S_to_A_Action();
  } else if(toss==2){
    CU_assume(rule_S_to_B_Enabled());
    rule_S_to_B_Action();
  } else if(toss==3){
    CU_assume(rule_S_rcv_Enabled());
    rule_S_rcv_Action();
  } else if(toss==4){
    CU_assume(rule_I_intercept_Enabled());
    CU_input(drop);
    rule_I_intercept(drop);
  } else if(toss==6){
    CU_assume(rule_I_replay_Enabled());
    rule_I_replay();
  } else {
    rule_I_send_ii();
  }
}

void run7(){
  int i;
  init();
  for(i=0;i<8;i++){
    run_once();
  }
}



main(){
  init();
  rule_I_send_ii();
  assert(rule_S_rcv_Enabled());
  print_message();
  rule_S_rcv_Action();
  assert(rule_none_to_A_Enabled());
  rule_none_to_A_Action();
  assert(rule_S_rcv_Enabled());
  print_message();
  rule_S_rcv_Action();
  assert(rule_S_to_B_Enabled());
  rule_S_to_B_Action();
  assert(rule_I_intercept_Enabled());
  rule_I_intercept(1);
  assert(rule_I_replay_Enabled());
  rule_I_replay();
  print_message();
  assert(rule_S_rcv_Enabled());
  rule_S_rcv_Action();
}
*/

//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=530
//@jcute.optionLogLevel=1
//@jcute.optionLogStatistics=true
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
