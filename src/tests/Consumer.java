package tests;

public class Consumer extends Thread {
    private Buffer cubbyhole;
    private int number;

    public Consumer(Buffer c, int number) {
        cubbyhole = c;
        this.number = number;
    }

    public void run() {
        int value = 0;
        for (int i = 0; i < 2; i++) {
            value = cubbyhole.get(number);
        }
    }
}

