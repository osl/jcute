package tests;



/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class MultiLock extends Thread {
    private int iter;
    public static int x;
    public static String lock="";


    public MultiLock(String name,int iter) {
        super(name);
        this.iter = iter;
    }

    public void run() {
        for(int i=0;i<iter;i++){
            synchronized(lock){
                for(int j=0;j<5-i;j++){
                    x=1;
                }
                System.out.print(Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) {
        lock = "lock";
        MultiLock m1 = new MultiLock("1",3);
        MultiLock m2 = new MultiLock("2",3);
        MultiLock m3 = new MultiLock("3",1);
        MultiLock m4 = new MultiLock("4",1);
        m1.start();
        m2.start();
        m3.start();
        m4.start();
        System.out.println("");
    }
}
