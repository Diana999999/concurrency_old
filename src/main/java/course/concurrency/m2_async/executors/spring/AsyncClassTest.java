package course.concurrency.m2_async.executors.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@EnableAsync
public class AsyncClassTest {

    @Autowired
    public ApplicationContext context;

//    @Lazy
//    @Autowired
//    AsyncClassTest asyncClassTest;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private ThreadPoolTaskExecutor executor;

//    @Bean(name = "applicationTaskExecutor")
//    public Executor asyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(1);
//        return  executor;
//    }



    @Async
    public void runAsyncTask() {
        System.out.println("runAsyncTask: " + Thread.currentThread().getName());
        internalTask();
    }

    @Async
    public void internalTask() {
        System.out.println("internalTask: " + Thread.currentThread().getName());
    }
}
