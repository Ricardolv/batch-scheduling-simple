package com.richard.config.jobs;

import com.richard.TracePerformanceAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class JobConfig {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final TracePerformanceAspect trace;

    public JobConfig(JobLauncher jobLauncher, Job job, TracePerformanceAspect trace) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.trace = trace;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void perform() throws Exception {

        JobParameters params = new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
        jobLauncher.run(job, params);
    }
}
