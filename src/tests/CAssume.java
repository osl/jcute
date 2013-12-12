package tests;

import cute.Cute;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class CAssume extends Thread {
    public static String lock = "";

    public void run() {
        synchronized(lock){
            int x = Cute.input.Integer();
            Cute.Assume(x>=1);
            System.out.println("x>=1");
        }
    }

    public static void main(String[] args) {
        CAssume ca = new CAssume();
        ca.start();
    }
}
