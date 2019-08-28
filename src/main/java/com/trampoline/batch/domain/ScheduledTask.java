package com.trampoline.batch.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
//@NamedQuery(name="ScheduledTask.findAll", query="SELECT r FROM ScheduledTask r")
@Table(name = "scheduled_task")
public class ScheduledTask implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String taskid;

	private int companyid;

	private String cronExpression;

	@Lob
	private String jobdata;

	@Column(name="task_type")
	private String taskType;

	private String jobClass;

	private Long repeatTime;

	private String jobName;
	private String jobGroup;
	private Boolean cronJob;


}