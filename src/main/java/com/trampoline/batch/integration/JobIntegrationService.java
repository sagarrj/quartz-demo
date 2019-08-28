package com.trampoline.batch.integration;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by sagar on 28/8/19.
 */
@Slf4j
@DisallowConcurrentExecution
public class JobIntegrationService extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("SampleCronJob Start................");
        System.out.println("Hello world");
    }
}
