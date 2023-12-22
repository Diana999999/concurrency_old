package course.concurrency.m3_shared;

public class PingPong {

    public static void ping() {

    }

    public static void pong() {

    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> ping());
        Thread t2 = new Thread(() -> pong());
        t1.start();
        t2.start();
    }
}
