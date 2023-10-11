package course.concurrency.m2_async.cf;

import java.util.stream.IntStream;

public class LoadGenerator {

    private final boolean isSleep;

    /**
     * @param isSleep - if 0 - then use sleep, otherwise use compute
     */
    public LoadGenerator(boolean isSleep) {
        this.isSleep = isSleep;
    }

    public void work() {
        if (isSleep) {
            sleep();
        } else {
            compute();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int compute() {
        return IntStream.range(0, 5_000_000).boxed().filter(i -> i % 2 == 0).reduce((a, b) -> b).get();
    }
}