CREATE TABLE `scheduled_task` (
	`taskid` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`companyid` INT(11) NOT NULL,
	`cron_expression` VARCHAR(255) NULL DEFAULT NULL,
	`cron_job` BIT(1) NULL DEFAULT NULL,
	`job_class` VARCHAR(255) NULL DEFAULT NULL,
	`jobdata` LONGTEXT NULL,
	`job_group` VARCHAR(255) NULL DEFAULT NULL,
	`job_name` VARCHAR(255) NULL DEFAULT NULL,
	`repeat_time` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`taskid`),
	INDEX `taskid` (`taskid`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=3
;

