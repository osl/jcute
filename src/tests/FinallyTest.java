package tests;

/**
 *  .
 * User: ksen
 * Date: Oct 25, 2005
 * Time: 5:33:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FinallyTest {
    public void f(int x){
        try {
            int y = x;
            return;
        }
        catch(RuntimeException e){
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        System.out.println("args.length = " + args.length);
    }
}
