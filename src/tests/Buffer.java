package tests;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 12:51:27 PM
 */
public interface Buffer {
    public int get(int who);
    public void put(int who, int value);
}
