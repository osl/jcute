package tests;

public class Producer extends Thread {
    private Buffer cubbyhole;
    private int number;

    public Producer(Buffer c, int number) {
        cubbyhole = c;
        this.number = number;
    }

    public void run() {
        for (int i = 0; i < 2; i++) {
            cubbyhole.put(number, i);
            //try {
            //    sleep((int)(Math.random() * 100));
            //} catch (InterruptedException e) { }
        }
    }
}
