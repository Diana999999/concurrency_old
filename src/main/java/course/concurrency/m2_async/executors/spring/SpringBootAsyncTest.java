package course.concurrency.m2_async.executors.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootAsyncTest {

    private final AsyncClassTest testClass;

    public SpringBootAsyncTest(AsyncClassTest testClass) {
        this.testClass = testClass;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void actionAfterStartup() {
        System.out.println("start application: " + Thread.currentThread().getName());
        testClass.runAsyncTask();
        testClass.internalTask();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAsyncTest.class, args);
    }
}
