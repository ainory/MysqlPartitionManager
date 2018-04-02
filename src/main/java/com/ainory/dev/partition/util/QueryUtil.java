package com.ainory.dev.partition.util;

import com.ainory.dev.partition.share.Define;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ainory on 2016. 6. 21..
 */
public class QueryUtil {
    private static final Logger logger = LoggerFactory.getLogger(QueryUtil.class);

    public static ArrayList<String> addPartitionQueryGenerator(String dbName, String tableName, String partitionUnit, String partitionPrefix, ArrayList<String> partitionList){
        /*
            ALTER TABLE dtlnms.fm_alarm_hist ADD PARTITION ( PARTITION P20160915 VALUES LESS THAN (TO_DAYS('2016-09-16 00:00:00')));
            ALTER TABLE metatron.hm_yarn_job_info ADD PARTITION ( PARTITION P20160924 VALUES LESS THAN (TO_DAYS('2016-09-24 00:00:00')))
            partition_pattern = "%Y-%m-%d %H:00:00"
            add_partition_method = "TO_SECONDS"

            partition_pattern = "%Y-%m-%d 00:00:00"
            add_partition_method = "TO_DAYS"
         */

        ArrayList<String> queryList = new ArrayList<>();

        for(String partition : partitionList){
            StringBuffer query = new StringBuffer();

            try{
                if(StringUtils.equals(partitionUnit,Define.PARTITION_UNIT_DAY)){
                    query.append("ALTER TABLE ").append(dbName).append(".").append(tableName)
                            .append(" ADD PARTITION ( PARTITION ").append(partition).append(" VALUES LESS THAN (").append(Define.DB_DATE_FUNCTION_DAY).append("('").append(incrementDay(StringUtils.remove(partition,partitionPrefix))).append("')))");
                }else if(StringUtils.equals(partitionUnit,Define.PARTITION_UNIT_HOUR)){
                    query.append("ALTER TABLE ").append(dbName).append(".").append(tableName)
                            .append(" ADD PARTITION ( PARTITION ").append(partition).append(" VALUES LESS THAN (").append(Define.DB_DATE_FUNCTION_HOUR).append("('").append(incrementHour(StringUtils.remove(partition,partitionPrefix))).append("')))");
                }

                if(query.length() > 0){
                    queryList.add(query.toString());
                }

            }catch (Exception e){
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

        return queryList;
    }

    public static ArrayList<String> dropPartitionQueryGenerator(String dbName, String tableName, ArrayList<String> partitionList){
         /*
            ALTER TABLE dtlnms.fm_alarm_hist DROP PARTITION P20160514;
         */
        ArrayList<String> queryList = new ArrayList<>();

        for(String partition : partitionList){
            StringBuffer query = new StringBuffer();

            query.append("ALTER TABLE ").append(dbName).append(".").append(tableName).append(" DROP PARTITION ").append(partition);

            queryList.add(query.toString());
        }

        return queryList;
    }

    public static String incrementDay(String partitionName){

        try{
            Calendar calendar = DateUtils.toCalendar(DateUtils.parseDate(partitionName, Define.PARTITION_DAY_DATEFORMAT));

            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1);

            return DateFormatUtils.format(calendar,Define.DB_DATE_FORMAT_DAY);

        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
    public static String incrementHour(String partitionName){

        try{
            Calendar calendar = DateUtils.toCalendar(DateUtils.parseDate(partitionName, Define.PARTITION_DAY_DATEFORMAT));

            calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY)+1);

            return DateFormatUtils.format(calendar,Define.DB_DATE_FORMAT_HOUR);

        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

}
