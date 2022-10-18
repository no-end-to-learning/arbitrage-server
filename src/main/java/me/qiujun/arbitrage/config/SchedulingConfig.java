package me.qiujun.arbitrage.config;

import me.qiujun.arbitrage.util.ExecutorUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        value = "app.scheduling.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(new TaskSchedulerWithTrace(getScheduledExecutor()));
    }

    private ScheduledExecutorService getScheduledExecutor() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(32);
        executor.setThreadNamePrefix("scheduling-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(3);
        executor.initialize();
        return executor.getScheduledExecutor();
    }

    public static class TaskSchedulerWithTrace extends ConcurrentTaskScheduler {

        public TaskSchedulerWithTrace(ScheduledExecutorService scheduledExecutor) {
            super(scheduledExecutor);
        }

        @NotNull
        @Override
        public ScheduledFuture<?> schedule(@NotNull Runnable task, @NotNull Date startTime) {
            return super.schedule(ExecutorUtil.withTraceId(task), startTime);
        }

        @Override
        public ScheduledFuture<?> schedule(@NotNull Runnable task, @NotNull Trigger trigger) {
            return super.schedule(ExecutorUtil.withTraceId(task), trigger);
        }

        @NotNull
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable task, @NotNull Date startTime, long period) {
            return super.scheduleAtFixedRate(ExecutorUtil.withTraceId(task), startTime, period);
        }

        @NotNull
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable task, long period) {
            return super.scheduleAtFixedRate(ExecutorUtil.withTraceId(task), period);
        }

        @NotNull
        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable task, @NotNull Date startTime, long delay) {
            return super.scheduleWithFixedDelay(ExecutorUtil.withTraceId(task), startTime, delay);
        }

        @NotNull
        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable task, long delay) {
            return super.scheduleWithFixedDelay(ExecutorUtil.withTraceId(task), delay);
        }
    }
}
