package foregg.foreggserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        int n = Runtime.getRuntime().availableProcessors();
        scheduler.setPoolSize(n+1);
        scheduler.setThreadNamePrefix("taskScheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
