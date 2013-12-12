package security.ns;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */


import cute.Cute;
import security.Agent;
import security.Message;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */

class A extends Agent {
    public int state;
    public int responder;
    public Intruder In;

    public A(int numGuards, int numIterations, Intruder i) {
        super(numGuards, numIterations);
        state = NS.SLEEP;
        In = i;
    }

    public boolean guard1(Message m) {
        return state==NS.SLEEP;
    }

    public void action1(Message m) {
        Net net = (Net)m;
        int toss;

        toss = Cute.input.Integer();
        Cute.Assume(toss==NS.ii || toss==NS.b);

        net.from = NS.a;
        net.to = toss;
        net.data1 = NS.a;
        net.data2 = NS.a;
        net.datatype = NS.NONCE_ADDRESS;
        net.key = toss;

        state = NS.WAIT;
        responder = toss;
        Cute.Assume(In.guard1(m));
        In.action1(m);
    }

    public boolean guard2(Message m) {
        Net net = (Net)m;
        return net.to == NS.a && state==NS.WAIT && net.data1 == NS.a && net.datatype == NS.NONCE_NONCE && net.key == NS.a;
    }

    public void action2(Message m) {
        Net net = (Net)m;

        net.from = NS.a;
        net.to = responder;
        net.data1 = net.data2;
        net.datatype = NS.NONCE;
        net.key = responder;
        Cute.Assume(In.guard1(m));
        In.action1(m);
    }
}

class B extends Agent {
    public int state;
    public int responder;
    public A myA;
    public Intruder In;

    public B(int numGuards, int numIterations, A myA, Intruder i) {
        super(numGuards, numIterations);
        state = NS.SLEEP;
        this.myA = myA;
        In = i;
    }

    public boolean guard1(Message m) {
        Net net = (Net)m;
        return state==NS.SLEEP && net.to==NS.b && net.key==NS.b && net.datatype==NS.NONCE_ADDRESS;
    }


    public void action1(Message m) {
        Net net = (Net)m;
        state = NS.WAIT;
        responder = net.data2;

        net.to = net.from;
        net.from = NS.b;
        net.key = net.data2;
        net.data2 = NS.b;
        net.datatype=NS.NONCE_NONCE;
        Cute.Assume(In.guard1(m));
        In.action1(m);
    }

    public boolean guard2(Message m) {
        Net net = (Net)m;
        return state == NS.WAIT && net.to==NS.b && net.key==NS.b && net.datatype == NS.NONCE && net.data1 == NS.b;
    }

    public void action2(Message m) {
        state = NS.COMMIT;
        Cute.Assert(responder!=NS.a || myA.responder==NS.b);
        Cute.Assume(In.guard1(m));
        In.action1(m);
    }

}

class Net extends Message {
    public int from;
    public int to;
    public int data1;
    public int data2;
    public int key;
    public int datatype;

    public Net() {
    }

    public void invalidate(){
        to = NS.EMPTY;
        from = NS.EMPTY;
    }
}

class Intruder extends Agent{
    public int i;
    public int nonce_a_seen;
    public int nonce_b_seen;
    public Net[] messages;

    public Intruder(int numGuards, int numIterations) {
        super(numGuards, numIterations);
        messages = new Net[NS.MAXKNOWLEDGE];
        for (int j = 0; j < messages.length; j++) {
            messages[j] = new Net();
        }
        i = 0;
        nonce_a_seen = 0;
        nonce_b_seen = 0;
    }

    public boolean guard1(Message m) {
        Net net = (Net)m;
        return net.from != NS.ii;
    }

    public void action1(Message m) {
        int i,toss;
        boolean flag;

        Net net = (Net)m;

        if(net.key==NS.ii){
            if(net.data1==NS.a) nonce_a_seen = 1;
            if(net.data1==NS.b) nonce_b_seen = 1;
            if(net.datatype==NS.NONCE_NONCE){
                if(net.data2==NS.a) nonce_a_seen = 1;
                if(net.data2==NS.b) nonce_b_seen = 1;
            }
        } else {
            flag = true;
            for(i=0;flag && i< this.i;i++){
                if( messages[i].data1 == net.data1 &&  messages[i].data2 == net.data2
                        &&  messages[i].datatype == net.datatype &&  messages[i].key == net.key){
                    flag = false;
                }
            }
            if(flag){
                messages[ this.i].data1 = net.data1;
                messages[ this.i].data2 = net.data2;
                messages[ this.i].datatype = net.datatype;
                messages[ this.i].key = net.key;
                ( this.i)++;
            }
        }

/*   CU_input(toss); */
/*   if(toss){ */
/*     /* drop */
/*     invalidate(); */
/*   } else { */
        /* replay */
        for(i=0;i< this.i;i++){
            toss = Cute.input.Integer();
            if(toss==1) {
                toss = Cute.input.Integer();
                Cute.Assume(toss==NS.a || toss==NS.b);
                net.from = NS.ii;
                net.to = toss;
                net.data1 =  messages[i].data1;
                net.data2 =  messages[i].data2;
                net.datatype =  messages[i].datatype;
                net.key =  messages[i].key;
                return;
            }
        }
        /* send nonce */
        toss = Cute.input.Integer();
        Cute.Assume(toss==NS.a || toss==NS.b);
        net.from = NS.ii;
        net.to = toss;
        net.key = toss;
        toss = Cute.input.Integer();
        Cute.Assume(toss==NS.NONCE || toss==NS.NONCE_ADDRESS || toss==NS.NONCE_NONCE);
        if(toss==NS.NONCE && ( nonce_a_seen != 0||  nonce_b_seen !=0)){
            net.datatype = NS.NONCE;
            if( nonce_a_seen==0){
                net.data1 = NS.b;
            } else if( nonce_b_seen==0){
                net.data1 = NS.a;
            } else {
                toss = Cute.input.Integer();
                Cute.Assume(toss==NS.a || toss==NS.b);
                net.data1 = toss;
            }
            return;
        } else if(toss==NS.NONCE_ADDRESS && ( nonce_a_seen!=0 ||  nonce_b_seen!=0)){
            net.datatype = NS.NONCE_ADDRESS;
            if( nonce_a_seen==0){
                net.data1 = NS.b;
            } else if( nonce_b_seen==0){
                net.data1 = NS.a;
            } else {
                toss = Cute.input.Integer();
                Cute.Assume(toss==NS.a || toss==NS.b);
                net.data1 = toss;
            }
            toss = Cute.input.Integer();
            Cute.Assume(toss==NS.a || toss==NS.b);
            net.data2 = toss;
            return;
        } else if(toss==NS.NONCE_NONCE && ( nonce_a_seen!=0 ||  nonce_b_seen!=0)){
            net.datatype = NS.NONCE_NONCE;
            if( nonce_a_seen!=0 &&  nonce_b_seen!=0){
                toss = Cute.input.Integer();
                if(toss==1){
                    net.data1 = NS.a;
                } else {
                    net.data1 = NS.b;
                }
            } else if( nonce_a_seen!=0){
                net.data1 = NS.a;
            } else {
                net.data1 = NS.b;
            }
            if( nonce_a_seen!=0 &&  nonce_b_seen!=0){
                toss = Cute.input.Integer();
                if(toss==1){
                    net.data2 = NS.a;
                } else {
                    net.data2 = NS.b;
                }
            } else if( nonce_a_seen!=0){
                net.data2 = NS.a;
            } else {
                net.data2 = NS.b;
            }
            return;
        }
        net.invalidate();
        /*   } */
    }

}







public class NS {
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

    static public void main(String args[]){
        Intruder i = new Intruder(1,3);
        A a = new A(2,2,i);
        B b = new B(2,2,a,i);
        Net n = new Net();
        n.m = n;

        a.start();
        b.start();

    }

//    static public void main(String args[]){
//        Cute.begin(args);
//        A a = new A(2,2);
//        B b = new B(2,2,a);
//        Intruder i = new Intruder(1,4);
//        Net n = new Net();
//        n.invalidate();
//        int toss;
//
////        Cute.Assume(a.guard1(n));
////        a.action1(n);
////        System.out.println("1");
////        Cute.Assume(i.guard1(n));
////        i.action1(n);
////        Cute.Assume(b.guard1(n));
////        b.action1(n);
////        System.out.println("2");
////        Cute.Assume(i.guard1(n));
////        i.action1(n);
////        Cute.Assume(a.guard2(n));
////        a.action2(n);
////        System.out.println("3");
////        Cute.Assume(i.guard1(n));
////        i.action1(n);
////        Cute.Assume(b.guard2(n));
////        b.action2(n);
////        System.out.println("4");
//
//        for(int j=0;j<4;j++){
//            toss = Cute.input.Integer();
//            if(toss==1){
//                Cute.Assume(a.guard1(n));
//                a.action1(n);
//                Cute.Assume(i.guard1(n));
//                i.action1(n);
//            } else if(toss==2){
//                Cute.Assume(b.guard1(n));
//                b.action1(n);
//                Cute.Assume(i.guard1(n));
//                i.action1(n);
//            } else if(toss==3){
//                Cute.Assume(a.guard2(n));
//                a.action2(n);
//                Cute.Assume(i.guard1(n));
//                i.action1(n);
//            } else if(toss==4){
//                Cute.Assume(b.guard2(n));
//                b.action2(n);
//                Cute.Assume(i.guard1(n));
//                i.action1(n);
//            }
//        }
//        Cute.end();
//    }

}
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
