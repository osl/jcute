package tests;

import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

class Agent {
    public int state;
    public int responder;
}

class Net {
    public int from;
    public int to;
    public int data1;
    public int data2;
    public int key;
    public int datatype;
}

class Intruder {
    public int i;
    public int nonce_a_seen;
    public int nonce_b_seen;
    public Net[] messages;

    public Intruder(){
        messages = new Net[Ns.MAXKNOWLEDGE];
        for (int j = 0; j < messages.length; j++) {
            messages[j] = new Net();
        }
    }
}


public class Ns {

    final public static int EMPTY = -1;
    final public static int SLEEP = 1;
    final public static int WAIT = 2;
    final public static int COMMIT = 3;

    final public static int NONCE = 1;
    final public static int NONCE_ADDRESS = 2;
    final public static int NONCE_NONCE = 3;
    final public static int MAXKNOWLEDGE = 10;

    final public static int a = 0;
    final public static int b = 1;
    final public static int ii = 2;


    static public Intruder I;
    static public Net net;
    static public Agent A,B;

    static public void invalidate(){
        net.to = EMPTY;
        net.from = EMPTY;
    }

    static public void init(){
        I = new Intruder();
        net = new Net();
        A = new Agent();
        B = new Agent();
        
        net.to = EMPTY;
        net.from = EMPTY;
        A.state = SLEEP;
        B.state = SLEEP;
        I.i = 0;
        I.nonce_a_seen = 0;
        I.nonce_b_seen = 0;
    }

    static public boolean enabled_none_to_A(){
        return A.state==SLEEP;
    }

    static public void action_none_to_A(){
        int toss;

        toss = Cute.input.Integer();
        Cute.Assume(toss==ii || toss==b);

        net.from = a;
        net.to = toss;
        net.data1 = a;
        net.data2 = a;
        net.datatype = NONCE_ADDRESS;
        net.key = toss;

        A.state = WAIT;
        A.responder = toss;
    }

    static public boolean enabled_any_to_A(){
        return net.to == a && A.state==WAIT && net.data1 == a && net.datatype == NONCE_NONCE && net.key == a;
    }

    static public void action_any_to_A(){
        A.state = COMMIT;

        net.from = a;
        net.to = A.responder;
        net.data1 = net.data2;
        net.datatype = NONCE;
        net.key = A.responder;
    }

    static public boolean enabled_any_to_B_sleep(){
        return B.state==SLEEP && net.to==b && net.key==b && net.datatype==NONCE_ADDRESS;
    }

    static public void action_any_to_B_sleep(){
        B.state = WAIT;
        B.responder = net.data2;

        net.to = net.from;
        net.from = b;
        net.key = net.data2;
        net.data2 = b;
        net.datatype=NONCE_NONCE;
    }

    static public boolean enabled_any_to_B_wait(){
        return B.state == WAIT && net.to==b && net.key==b && net.datatype == NONCE && net.data1 == b;
    }

    static public void action_any_to_B_wait(){
        B.state = COMMIT;
        Cute.Assert(B.responder!=a || A.responder==b);
    }

    static public boolean enabled_any_I(){
        return net.from != ii;
    }

    static public void action_any_I(){
        int i,toss;
        boolean flag;

        if(net.key==ii){
            if(net.data1==a) I.nonce_a_seen = 1;
            if(net.data1==b) I.nonce_b_seen = 1;
            if(net.datatype==NONCE_NONCE){
                if(net.data2==a) I.nonce_a_seen = 1;
                if(net.data2==b) I.nonce_b_seen = 1;
            }
        } else {
            flag = true;
            for(i=0;flag && i<I.i;i++){
                if(I.messages[i].data1 == net.data1 && I.messages[i].data2 == net.data2
                        && I.messages[i].datatype == net.datatype && I.messages[i].key == net.key){
                    flag = false;
                }
            }
            if(flag){
                I.messages[I.i].data1 = net.data1;
                I.messages[I.i].data2 = net.data2;
                I.messages[I.i].datatype = net.datatype;
                I.messages[I.i].key = net.key;
                (I.i)++;
            }
        }

/*   CU_input(toss); */
/*   if(toss){ */
/*     /* drop */
/*     invalidate(); */
/*   } else { */
        /* replay */
        for(i=0;i<I.i;i++){
            toss = Cute.input.Integer();
            if(toss==1) {
                toss = Cute.input.Integer();
                Cute.Assume(toss==a || toss==b);
                net.from = ii;
                net.to = toss;
                net.data1 = I.messages[i].data1;
                net.data2 = I.messages[i].data2;
                net.datatype = I.messages[i].datatype;
                net.key = I.messages[i].key;
                return;
            }
        }
        /* send nonce */
        toss = Cute.input.Integer();
        Cute.Assume(toss==a || toss==b);
        net.from = ii;
        net.to = toss;
        net.key = toss;
        toss = Cute.input.Integer();
        Cute.Assume(toss==NONCE || toss==NONCE_ADDRESS || toss==NONCE_NONCE);
        if(toss==NONCE && (I.nonce_a_seen != 0|| I.nonce_b_seen !=0)){
            net.datatype = NONCE;
            if(I.nonce_a_seen==0){
                net.data1 = b;
            } else if(I.nonce_b_seen==0){
                net.data1 = a;
            } else {
                toss = Cute.input.Integer();
                Cute.Assume(toss==a || toss==b);
                net.data1 = toss;
            }
            return;
        } else if(toss==NONCE_ADDRESS && (I.nonce_a_seen!=0 || I.nonce_b_seen!=0)){
            net.datatype = NONCE_ADDRESS;
            if(I.nonce_a_seen==0){
                net.data1 = b;
            } else if(I.nonce_b_seen==0){
                net.data1 = a;
            } else {
                toss = Cute.input.Integer();
                Cute.Assume(toss==a || toss==b);
                net.data1 = toss;
            }
            toss = Cute.input.Integer();
            Cute.Assume(toss==a || toss==b);
            net.data2 = toss;
            return;
        } else if(toss==NONCE_NONCE && (I.nonce_a_seen!=0 || I.nonce_b_seen!=0)){
            net.datatype = NONCE_NONCE;
            if(I.nonce_a_seen!=0 && I.nonce_b_seen!=0){
                toss = Cute.input.Integer();
                if(toss==1){
                    net.data1 = a;
                } else {
                    net.data1 = b;
                }
            } else if(I.nonce_a_seen!=0){
                net.data1 = a;
            } else {
                net.data1 = b;
            }
            if(I.nonce_a_seen!=0 && I.nonce_b_seen!=0){
                toss = Cute.input.Integer();
                if(toss==1){
                    net.data2 = a;
                } else {
                    net.data2 = b;
                }
            } else if(I.nonce_a_seen!=0){
                net.data2 = a;
            } else {
                net.data2 = b;
            }
            return;
        }
        invalidate();
        /*   } */
    }

    static public void run_once(int toss){

        if(toss==1){
            Cute.Assume(enabled_none_to_A());
            action_none_to_A();
            Cute.Assume(enabled_any_I());
            action_any_I();
        } else if(toss==2){
            Cute.Assume(enabled_any_to_B_sleep());
            action_any_to_B_sleep();
            Cute.Assume(enabled_any_I());
            action_any_I();
        } else if(toss==3){
            Cute.Assume(enabled_any_to_A());
            action_any_to_A();
            Cute.Assume(enabled_any_I());
            action_any_I();
        } else if(toss==4){
            Cute.Assume(enabled_any_to_B_wait());
            action_any_to_B_wait();
            Cute.Assume(enabled_any_I());
            action_any_I();
        }
    }

    static public void main(String args[]){
        int i,toss;
        init();

        for(i=0;i<4;i++){
            toss = Cute.input.Integer();
            run_once(toss);
        }
    }


}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=false
//@jcute.optionLogTraceAndInput=false
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=D:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=530
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=false
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
