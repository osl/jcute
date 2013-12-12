package tests;

import cute.Cute;


class MyThread3 extends Thread{
    StringBuffer sb1;
    StringBuffer sb2;

    public MyThread3(StringBuffer sb1, StringBuffer sb2) {
        this.sb1 = sb1;
        this.sb2 = sb2;
    }

    public void run() {
        //System.out.println("t1 sb1 = " + sb1);
        //System.out.println("t1 sb2 = " + sb2);
        //System.out.flush();
        sb1.append(sb2);
    }
}

class MyThread4 extends Thread{
    StringBuffer sb;

    public MyThread4(StringBuffer sb) {
        this.sb = sb;
    }

    public void run() {
        //System.out.println("t2 sb = " + sb);
        //System.out.flush();
        int toss = Cute.input.Integer();
        //System.out.println("toss = " + toss);
        if(toss==0){
            char c = Cute.input.Character();
            sb.append(c);
        } else if(toss==1){
            sb.setLength(0);
        } else if(toss==2){
            char c2 = Cute.input.Character();
            int index = Cute.input.Integer();
            synchronized(sb){
                Cute.Assume(index>=0 && index <= sb.length()-1);
                sb.insert(index,c2);
            }
        }
    }
}
