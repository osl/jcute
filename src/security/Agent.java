package security;

import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class Agent extends Thread{
    private int numGuards;
    private int numIterations;

    public void run() {

        for(int i=0;i<numIterations;i++){
            synchronized(Message.m){
                int toss= Cute.input.Integer();
                Cute.Assume(toss >=1 && toss <=numGuards);
                if(toss==1){
                    Cute.Assume(guard1(Message.m));
                    action1(Message.m);
                } else if(toss==2){
                    Cute.Assume(guard2(Message.m));
                    action2(Message.m);
                } else if(toss==3){
                    Cute.Assume(guard3(Message.m));
                    action3(Message.m);
                } else if(toss==4){
                    Cute.Assume(guard4(Message.m));
                    action4(Message.m);
                } else if(toss==5){
                    Cute.Assume(guard5(Message.m));
                    action5(Message.m);
                } else if(toss==6){
                    Cute.Assume(guard6(Message.m));
                    action6(Message.m);
                } else if(toss==7){
                    Cute.Assume(guard7(Message.m));
                    action7(Message.m);
                } else if(toss==8){
                    Cute.Assume(guard8(Message.m));
                    action8(Message.m);
                } else if(toss==9){
                    Cute.Assume(guard9(Message.m));
                    action9(Message.m);
                } else if(toss==10){
                    Cute.Assume(guard10(Message.m));
                    action10(Message.m);
                } else if(toss==11){
                    Cute.Assume(guard11(Message.m));
                    action11(Message.m);
                }
            }
        }
    }

    public Agent(int numGuards, int numIterations) {
        this.numGuards = numGuards;
        this.numIterations = numIterations;
    }

    public boolean guard1(Message m){
        return false;
    }

    public void action1(Message m){
    }

    public boolean guard2(Message m){
        return false;
    }

    public void action2(Message m){
    }
    public boolean guard3(Message m){
        return false;
    }

    public void action3(Message m){
    }

    public boolean guard4(Message m){
        return false;
    }

    public void action4(Message m){
    }

    public boolean guard5(Message m){
        return false;
    }

    public void action5(Message m){
    }

    public boolean guard6(Message m){
        return false;
    }

    public void action6(Message m){
    }

    public boolean guard7(Message m){
        return false;
    }

    public void action7(Message m){
    }

    public boolean guard8(Message m){
        return false;
    }

    public void action8(Message m){
    }

    public boolean guard9(Message m){
        return false;
    }

    public void action9(Message m){
    }

    public boolean guard10(Message m){
        return false;
    }

    public void action10(Message m){
    }

    public boolean guard11(Message m){
        return false;
    }

    public void action11(Message m){
    }
}
