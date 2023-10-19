package course.concurrency.m2_async.executors.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AsyncClassTest {
    private final ApplicationContext context;
    private final ThreadPoolTaskExecutor applicationTaskExecutor;

    public AsyncClassTest(ApplicationContext context, ThreadPoolTaskExecutor applicationTaskExecutor) {
        this.context = context;
        this.applicationTaskExecutor = applicationTaskExecutor;
    }

    @Async
    public void runAsyncTask() {
        System.out.println("runAsyncTask: " + Thread.currentThread().getName());
    }

    @Async
    public void internalTask() {
        System.out.println("internalTask: " + Thread.currentThread().getName());
    }
}
