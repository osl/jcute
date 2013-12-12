package tests;

import cute.Cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jun 29, 2006
 * Time: 8:16:58 AM
 */
public class InterfaceTest {
    public static void main(String[] args) {
        int x = Cute.input.Integer();
        if(x==100){
            System.out.println("Error");
        }
    }
}
