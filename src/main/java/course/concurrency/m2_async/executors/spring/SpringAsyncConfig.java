package course.concurrency.m2_async.executors.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {
    //@Override
    //public Executor getAsyncExecutor() {
    //    return Executors.newCachedThreadPool();
    //}

    @Bean(name = "singleThreadExecutor")
    public Executor singleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}