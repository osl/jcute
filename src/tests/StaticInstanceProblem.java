package tests;

import cute.Cute;

/**
 *  .
 * User: ksen
 * Date: Oct 26, 2005
 * Time: 10:57:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class StaticInstanceProblem {

    public static void f(int x,int y){
        if(x==100 && y==x+200)
            System.out.println("ERROR");
    }

    public static void main(String[] args) {
        StaticInstanceProblem sip = new StaticInstanceProblem();
        int x = Cute.input.Integer();
        sip.f(x,Cute.input.Integer());
    }
}
