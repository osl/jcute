package tests;

import cute.Cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 21, 2005
 * Time: 6:38:33 PM
 */
public class ErrorTest {
    public static void f1(){
        int x = Cute.input.Integer();
        if(x==1){
            String s =null;
            s.length();
        } else if(x==2){
            Cute.Assert(false);
        } else if(x==3){
            Cute.Assume(x==2);
        }
    }

    public void f3(){
        int x = Cute.input.Integer();
        if(x==1){
            Cute.Assume(x==2);
        } else if(x==2){
            Cute.Assert(false);
        } else if(x==3){
            String s =null;
            s.length();
        }
    }

    public void f2(){
        int x = Cute.input.Integer();
        if(x==1){
            String s =null;
            s.length();
        } else if(x==2){
            Cute.Assume(x==2);
        } else if(x==3){
            Cute.Assert(false);
        }
    }

    public static void main(String[] args) {
        
    }
}
