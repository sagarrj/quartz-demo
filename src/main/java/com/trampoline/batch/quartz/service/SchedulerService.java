package com.trampoline.batch.quartz.service;

import com.trampoline.batch.domain.ScheduledTask;

/**
 * @author Chamith
 */
public interface SchedulerService {

    void startAllSchedulers();

    void scheduleNewJob(ScheduledTask jobInfo);

    void updateScheduleJob(ScheduledTask jobInfo);

    boolean unScheduleJob(String jobName);

    boolean deleteJob(ScheduledTask jobInfo);

    boolean pauseJob(ScheduledTask jobInfo);

    boolean resumeJob(ScheduledTask jobInfo);

    boolean startJobNow(ScheduledTask jobInfo);

}
