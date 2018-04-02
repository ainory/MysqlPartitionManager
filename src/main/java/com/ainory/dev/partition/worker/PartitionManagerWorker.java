package com.ainory.dev.partition.worker;

import com.ainory.dev.partition.share.Context;
import com.ainory.dev.partition.util.ScheduleUtil;
import com.ainory.dev.partition.worker.runner.PartitionManagerRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;

/**
 * Created by ainory on 2016. 9. 13..
 */
public class PartitionManagerWorker {

    private static final Logger logger = LoggerFactory.getLogger(PartitionManagerWorker.class);

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void executeRunner(){

        try{
            logger.info("Partition Manager Start !!!");

            Date executionDate = null;
            long taskPeriod = 0;
            if( context.getExecutionPeriodHour() != null && context.getExecutionPeriodMinute() != null && context.getExecutionPeriodSecond() != null){
                executionDate = ScheduleUtil.getExecutionDate(context.getExecutionPeriodHour(), context.getExecutionPeriodMinute(), context.getExecutionPeriodSecond());
                taskPeriod = ScheduleUtil.getTaskPeriod_EveryDay();
            }else if(context.getExecutionPeriodHour() == null && context.getExecutionPeriodMinute() != null && context.getExecutionPeriodSecond() != null){
                executionDate = ScheduleUtil.getExecutionDate( context.getExecutionPeriodMinute(), context.getExecutionPeriodSecond());
                taskPeriod = ScheduleUtil.getTaskPeriod_EveryHour();
            }else if(context.getExecutionPeriodHour() == null && context.getExecutionPeriodMinute() == null && context.getExecutionPeriodSecond() != null){
                executionDate = ScheduleUtil.getExecutionDate(context.getExecutionPeriodSecond());
                taskPeriod = ScheduleUtil.getTaskPeriod_EveryMinute();
            }

            if (executionDate == null){
                System.exit(0);
            }

            if(StringUtils.isNotEmpty(context.getDatabaseList())){
                for(String db : StringUtils.split(context.getDatabaseList(),",")){
                    logger.info("["+db+"] PartitionManager begins at "+ DateFormatUtils.format(executionDate,"yyyy-MM-dd HH:mm:ss"));

                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new PartitionManagerRunner(db,context), executionDate, taskPeriod);
                }
            }else{
                logger.error("DB List is Empty!!! Exit...");
            }

        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
