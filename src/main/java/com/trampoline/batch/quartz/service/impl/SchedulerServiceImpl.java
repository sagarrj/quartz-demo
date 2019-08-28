package com.trampoline.batch.quartz.service.impl;

import com.trampoline.batch.domain.ScheduledTask;
import com.trampoline.batch.quartz.component.JobScheduleCreator;
import com.trampoline.batch.quartz.service.SchedulerService;
import com.trampoline.batch.repository.ScheduledTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.quartz.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Chamith
 */
@Slf4j
@Transactional
@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreator scheduleCreator;

    @Override
    public void startAllSchedulers() {
        List<ScheduledTask> jobInfoList = scheduledTaskRepository.findAll();
        if (jobInfoList != null) {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            jobInfoList.forEach(jobInfo -> {
                try {
                    JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                            .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
                    if (!scheduler.checkExists(jobDetail.getKey())) {
                        Trigger trigger;
                        jobDetail = scheduleCreator.createJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
                                false, context, jobInfo.getJobName(), jobInfo.getJobGroup());

                        if (jobInfo.getCronJob() && CronExpression.isValidExpression(jobInfo.getCronExpression())) {
                            trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
                                    jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                        } else {
                            trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
                                    jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                        }

                        JSONParser jp= new JSONParser();
                        JSONObject jobdatajson=(JSONObject)jp.parse(jobInfo.getJobdata());
                        JobDataMap jdmap= jobDetail.getJobDataMap();
                        jdmap.put("CompanyId",jobInfo.getCompanyid()+"");
                        jdmap.put("jobdata", jobdatajson.toString());
                        jdmap.put("taskid", jobInfo.getTaskid()+ "");

                        scheduler.scheduleJob(jobDetail, trigger);

                    }
                } catch (ClassNotFoundException | NoSuchBeanDefinitionException e) {
                    log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
                } catch (ParseException | SchedulerException e) {
                    log.error(e.getMessage(), e);
                }catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void scheduleNewJob(ScheduledTask jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
            if (!scheduler.checkExists(jobDetail.getKey())) {

                jobDetail = scheduleCreator.createJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
                        false, context, jobInfo.getJobName(), jobInfo.getJobGroup());

                Trigger trigger;
                if (jobInfo.getCronJob()) {
                    trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(), jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(), jobInfo.getRepeatTime(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }

                scheduler.scheduleJob(jobDetail, trigger);
                scheduledTaskRepository.save(jobInfo);
            } else {
                log.error("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateScheduleJob(ScheduledTask jobInfo) {
        Trigger newTrigger;
        if (jobInfo.getCronJob()) {
            newTrigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(), jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
            newTrigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(), jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
            scheduledTaskRepository.save(jobInfo);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }


    @Override
    public boolean unScheduleJob(String jobName) {
        try {
            return schedulerFactoryBean.getScheduler().unscheduleJob(new TriggerKey(jobName));
        } catch (SchedulerException e) {
            log.error("Failed to un-schedule job - {}", jobName, e);
            return false;
        }
    }

    @Override
    public boolean deleteJob(ScheduledTask jobInfo) {
        try {
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    @Override
    public boolean pauseJob(ScheduledTask jobInfo) {
        try {
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    @Override
    public boolean resumeJob(ScheduledTask jobInfo) {
        try {
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    @Override
    public boolean startJobNow(ScheduledTask jobInfo) {
        try {
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to start new job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }
}
