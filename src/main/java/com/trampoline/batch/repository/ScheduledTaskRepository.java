package com.trampoline.batch.repository;

import com.trampoline.batch.domain.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sagar on 26/8/19.
 */
@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask,String> {

    List<ScheduledTask> findByIsscheduled(int isScheduled);


}
