package com.ainory.dev.partition.worker.runner;

import com.ainory.dev.partition.entity.db.InformationSchemaVO;
import com.ainory.dev.partition.entity.db.PartitionVO;
import com.ainory.dev.partition.share.Context;
import com.ainory.dev.partition.share.Define;
import com.ainory.dev.partition.util.DbUtil;
import com.ainory.dev.partition.util.QueryLoaderUtil;
import com.ainory.dev.partition.util.QueryUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by ainory on 2016. 9. 13..
 */
public class PartitionManagerRunner extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(PartitionManagerRunner.class);

    private String DB_NAME;
    private Context context;
    private DbUtil dbUtil;

    public PartitionManagerRunner(String DB_NAME, Context context) {
        this.DB_NAME = DB_NAME;
        this.context = context;

        try{
            String url = this.context.getCommonDatabaseUrl()+"/"+this.DB_NAME;
            this.dbUtil = new DbUtil(this.context.getCommonDatabaseDriver(), url, this.context.getCommonDatabaseUser(), this.context.getCommonDatabasePass());


        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void run() {
        logger.info("["+DB_NAME+"] Partition ADD/DROP Start !!!");

        long start = System.nanoTime();
        long end;
        long inner_start;
        long inner_end;

        for(PartitionVO partitionVO : selectPartitionTables()){

            inner_start = System.nanoTime();

            //Partition Add
            InformationSchemaVO informationSchemaVO = selectTablePartitionInfo(partitionVO.getTable_name());
            if(informationSchemaVO != null){
                dbExecution(QueryUtil.addPartitionQueryGenerator(DB_NAME,partitionVO.getTable_name(),partitionVO.getPartition_unit(),partitionVO.getPartition_name(),addPartitionList(partitionVO, informationSchemaVO)));
            }

            //Partition Drop
            informationSchemaVO = selectTablePartitionInfo(partitionVO.getTable_name());
            if(informationSchemaVO != null){
                dbExecution(QueryUtil.dropPartitionQueryGenerator(DB_NAME,partitionVO.getTable_name(),dropPartitionList(partitionVO, informationSchemaVO)));
            }

            inner_end = System.nanoTime();
            logger.info("["+DB_NAME+"/"+partitionVO.getTable_name()+"] Partition ADD/DROP End..("+(inner_end - inner_start) / 1000000.0 +"ms)");
        }
        end = System.nanoTime();

        logger.info("["+DB_NAME+"] Partition ADD/DROP End..("+(end - start) / 1000000.0 +"ms)");
        logger.info("");
    }

    private void dbExecution(ArrayList<String> queryList){
        Connection conn = null;
        try {
            conn = dbUtil.getConnection();

            QueryRunner queryRunner = new QueryRunner();
            for(String query : queryList){
                logger.info(query);
                queryRunner.update(conn, query);
            }

        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            dbUtil.closeConnection();
        }
    }

    /**
     * @return
     */
    private List<PartitionVO> selectPartitionTables(){

        Connection conn = null;
        try {
            conn = dbUtil.getConnection();

            ResultSetHandler resultSetHandler = new BeanListHandler(PartitionVO.class);
            QueryRunner queryRunner = new QueryRunner();
            List<PartitionVO> list = (List) queryRunner.query(conn, QueryLoaderUtil.getQueryDbName("select_partition_tables", this.DB_NAME), resultSetHandler);

            if(list.size() > 0){
                return list;
            }else {
                return null;
            }

        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return new ArrayList<>();
        } finally {
            dbUtil.closeConnection();
        }
    }

    private InformationSchemaVO selectTablePartitionInfo(String tableName){

        Connection conn = null;
        try {
            conn = dbUtil.getConnection();

            ResultSetHandler resultSetHandler = new BeanListHandler(InformationSchemaVO.class);
            QueryRunner queryRunner = new QueryRunner();

            Object params[] = new Object[]{DB_NAME,tableName};
            List<InformationSchemaVO> list = (List) queryRunner.query(conn, QueryLoaderUtil.getQuery("select_table_partition_info"), resultSetHandler,params);

            if(list.size() > 0){
                return list.get(0);
            }else {
                return null;
            }

        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        } finally {
            dbUtil.closeConnection();
        }
    }

    /** Partition Add
     * @param partitionVO
     * @param informationSchemaVO
     * @return
     */
    private ArrayList<String> addPartitionList(PartitionVO partitionVO, InformationSchemaVO informationSchemaVO){

        try{
            // Partition Name Validation
            if(!validatePartitionFormat(informationSchemaVO.getMax_partition(),partitionVO.getPartition_name(),partitionVO.getPartition_unit())){
                return new ArrayList<>();
            }

            String maxCreatePartition = getCreateMaxPartition(partitionVO.getPartition_unit(), partitionVO.getPartition_name());

            //CreateMaxPartition == Table Max Partition
            if(StringUtils.equals(informationSchemaVO.getMax_partition(), maxCreatePartition)){
                return new ArrayList<>();
            }

            String indexPartition = informationSchemaVO.getMax_partition();
            Calendar indexCalendar = getPartitionCalendar(indexPartition,partitionVO.getPartition_unit(),partitionVO.getPartition_name());

            if(indexCalendar == null){
                return new ArrayList<>();
            }

            //CreateMaxPartition < Table Max Partition
            if(dateCompare(StringUtils.remove(maxCreatePartition, partitionVO.getPartition_name()), StringUtils.remove(indexPartition, partitionVO.getPartition_name()), partitionVO.getPartition_unit()) == -1){
                return new ArrayList<>();
            }

            ArrayList<String> addList = new ArrayList<>();

            while(!StringUtils.equals(indexPartition,maxCreatePartition)){

                StringBuffer result = new StringBuffer();
                try{
                    if(StringUtils.equals(partitionVO.getPartition_unit(), Define.PARTITION_UNIT_DAY)){
                        indexCalendar.set(Calendar.DAY_OF_MONTH, indexCalendar.get(Calendar.DAY_OF_MONTH)+1);
                        result.append(partitionVO.getPartition_name()).append(DateFormatUtils.format(indexCalendar, Define.PARTITION_DAY_DATEFORMAT));
                        indexPartition = result.toString();

                    }else if(StringUtils.equals(partitionVO.getPartition_unit(),Define.PARTITION_UNIT_HOUR)){
                        indexCalendar.set(Calendar.HOUR_OF_DAY, indexCalendar.get(Calendar.HOUR_OF_DAY)+1);
                        result.append(partitionVO.getPartition_name()).append(DateFormatUtils.format(indexCalendar, Define.PARTITION_HOUR_DATEFORMAT));
                        indexPartition = result.toString();
                    }

                    if(result.length() != 0){
                        addList.add(result.toString());
                    }

                }catch (Exception e){
                    logger.error(ExceptionUtils.getStackTrace(e));
                    continue;
                }
            }

            return addList;
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return new ArrayList<>();
        }
    }

    /** Partition Drop
     * @param partitionVO
     * @param informationSchemaVO
     * @return
     */
    private ArrayList<String> dropPartitionList(PartitionVO partitionVO, InformationSchemaVO informationSchemaVO){

        try{
            // Partition Name Validation
            if(!validatePartitionFormat(informationSchemaVO.getMax_partition(),partitionVO.getPartition_name(),partitionVO.getPartition_unit())
                    && !validatePartitionFormat(informationSchemaVO.getMin_partition(),partitionVO.getPartition_name(),partitionVO.getPartition_unit())){
                return new ArrayList<>();
            }

            //Max == Min
            if(StringUtils.equals(informationSchemaVO.getMax_partition(),informationSchemaVO.getMin_partition())){
                return new ArrayList<>();
            }

            int diff = getDateDiff(partitionVO,informationSchemaVO);

            //Dataterm > partition count
            if(diff < Integer.parseInt(partitionVO.getData_term())){
                return new ArrayList<>();
            }

            String indexPartition = informationSchemaVO.getMin_partition();
            String maxDropPartition = getDropMaxPartition(partitionVO.getPartition_unit(),partitionVO.getPartition_name(),Integer.parseInt(partitionVO.getData_term()));

            if(dateCompare(StringUtils.replace(indexPartition, partitionVO.getPartition_name(), ""),StringUtils.replace(maxDropPartition, partitionVO.getPartition_name(), ""), partitionVO.getPartition_unit())  == 1 ){
                logger.debug("["+DB_NAME+"/"+partitionVO.getTable_name()+"][Skip] indexPartition: "+ indexPartition  +" / maxDropPartition: "+ maxDropPartition);
                return new ArrayList<>();
            }

            Calendar indexCalendar = getPartitionCalendar(indexPartition,partitionVO.getPartition_unit(),partitionVO.getPartition_name());

            ArrayList<String> dropList = new ArrayList<>();

            int incrementCount = 1;
            boolean firstLoop = true;

            while(!StringUtils.equals(indexPartition,maxDropPartition)){

                StringBuffer result = new StringBuffer();
                try{
                    if(StringUtils.equals(partitionVO.getPartition_unit(), Define.PARTITION_UNIT_DAY)){
                        indexCalendar.set(Calendar.DAY_OF_MONTH, indexCalendar.get(Calendar.DAY_OF_MONTH)+(firstLoop == true ? 0:incrementCount));
                        result.append(partitionVO.getPartition_name()).append(DateFormatUtils.format(indexCalendar, Define.PARTITION_DAY_DATEFORMAT));
                        indexPartition = result.toString();

                    }else if(StringUtils.equals(partitionVO.getPartition_unit(),Define.PARTITION_UNIT_HOUR)){
                        indexCalendar.set(Calendar.HOUR_OF_DAY, indexCalendar.get(Calendar.HOUR_OF_DAY)+(firstLoop == true ? 0:incrementCount));
                        result.append(partitionVO.getPartition_name()).append(DateFormatUtils.format(indexCalendar, Define.PARTITION_HOUR_DATEFORMAT));
                        indexPartition = result.toString();
                    }

                    if(result.length() != 0 && !StringUtils.equals(indexPartition,maxDropPartition)){
                        dropList.add(result.toString());
                    }

                    firstLoop = false;

                }catch (Exception e){
                    logger.error(ExceptionUtils.getStackTrace(e));
                    continue;
                }
            }

            return dropList;
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return new ArrayList<>();
        }
    }

    /**
     * Partition String Format Validation
     * @param partitionName
     * @param partitionPrefix
     * @param partitionUnit
     * @return
     */
    private boolean validatePartitionFormat(String partitionName, String partitionPrefix, String partitionUnit){

        if(StringUtils.equalsIgnoreCase(partitionUnit, Define.PARTITION_UNIT_DAY)){
            return DateValidator.getInstance().isValid(StringUtils.replace(partitionName, partitionPrefix,""), Define.PARTITION_DAY_DATEFORMAT);
        }else if(StringUtils.equalsIgnoreCase(partitionUnit, Define.PARTITION_UNIT_HOUR)){
            return DateValidator.getInstance().isValid(StringUtils.replace(partitionName, partitionPrefix,""), Define.PARTITION_HOUR_DATEFORMAT);
        }else{
            logger.error("Invalid Partition Format!!!! - " + partitionName);
            return false;
        }
    }

    /**
     * Date String To Calendar Convert
     * @param partitionName
     * @param partitionUnit
     * @param partitionPrefix
     * @return
     */
    private Calendar getPartitionCalendar(String partitionName, String partitionUnit, String partitionPrefix){

        Calendar indexCalendar = null;

        try{
            if(StringUtils.equals(partitionUnit, Define.PARTITION_UNIT_DAY)){
                indexCalendar = DateUtils.toCalendar(DateUtils.parseDate(StringUtils.remove(partitionName, partitionPrefix), Define.PARTITION_DAY_DATEFORMAT));
            }else if(StringUtils.equals(partitionUnit, Define.PARTITION_UNIT_HOUR)){
                indexCalendar = DateUtils.toCalendar(DateUtils.parseDate(StringUtils.remove(partitionName, partitionPrefix), Define.PARTITION_HOUR_DATEFORMAT));
            }
            return indexCalendar;
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Current Day + Add Range
     * @param partitionUnit
     * @param partitionPrefix
     * @return
     */
    private String getCreateMaxPartition(String partitionUnit, String partitionPrefix){

        Calendar calendar = Calendar.getInstance();

        StringBuffer result = new StringBuffer();

        if(StringUtils.equals(partitionUnit, Define.PARTITION_UNIT_DAY)){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+context.getPartitionDayAddRange());
            result.append(partitionPrefix).append(DateFormatUtils.format(calendar, Define.PARTITION_DAY_DATEFORMAT));

        }else if(StringUtils.equals(partitionUnit,Define.PARTITION_UNIT_HOUR)){
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+context.getPartitionHourAddRange());
            result.append(partitionPrefix).append(DateFormatUtils.format(calendar, Define.PARTITION_HOUR_DATEFORMAT));
        }else{
            return null;
        }

        return result.toString();
    }

    /**
     * Current Date - DataTerm
     * @param partitionUnit
     * @param partitionPrefix
     * @param data_term
     * @return
     */
    private String getDropMaxPartition(String partitionUnit, String partitionPrefix, int data_term){
        Calendar calendar = Calendar.getInstance();

        StringBuffer result = new StringBuffer();

        if(StringUtils.equals(partitionUnit, Define.PARTITION_UNIT_DAY)){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-(data_term));
            result.append(partitionPrefix).append(DateFormatUtils.format(calendar, Define.PARTITION_DAY_DATEFORMAT));

        }else if(StringUtils.equals(partitionUnit,Define.PARTITION_UNIT_HOUR)){
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)-(data_term));
            result.append(partitionPrefix).append(DateFormatUtils.format(calendar, Define.PARTITION_HOUR_DATEFORMAT));

        }else{
            return null;
        }

        return result.toString();
    }



    /**
     * Date String Diff Days
     * @param startDateStr
     * @param endDateStr
     * @param dateFormat
     * @return
     */
    private int getDateDayDiff(String startDateStr, String endDateStr, String dateFormat){

        try{
            DateTime start = new DateTime(DateUtils.parseDate(startDateStr, dateFormat).getTime());
            DateTime end = new DateTime(DateUtils.parseDate(endDateStr, dateFormat).getTime());

            return Days.daysBetween(start,end).getDays();
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    /**
     * Date String Diff Hours
     * @param startDateStr
     * @param endDateStr
     * @param dateFormat
     * @return
     */
    private int getDateHourDiff(String startDateStr, String endDateStr, String dateFormat){

        try{
            DateTime start = new DateTime(DateUtils.parseDate(startDateStr, dateFormat).getTime());
            DateTime end = new DateTime(DateUtils.parseDate(endDateStr, dateFormat).getTime());

            return Hours.hoursBetween(start,end).getHours();
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    private int getDateDiff(PartitionVO partitionVO, InformationSchemaVO informationSchemaVO){

        int diff = 0;
        if(StringUtils.equals(partitionVO.getPartition_unit(), Define.PARTITION_UNIT_DAY)){
            diff = getDateDayDiff(StringUtils.remove(informationSchemaVO.getMin_partition(),partitionVO.getPartition_name()),StringUtils.remove(informationSchemaVO.getMax_partition(),partitionVO.getPartition_name()),Define.PARTITION_DAY_DATEFORMAT);
            diff += context.getPartitionDayAddRange();
        }else if(StringUtils.equals(partitionVO.getPartition_unit(), Define.PARTITION_UNIT_HOUR)){
            diff = getDateHourDiff(StringUtils.remove(informationSchemaVO.getMin_partition(),partitionVO.getPartition_name()),StringUtils.remove(informationSchemaVO.getMax_partition(),partitionVO.getPartition_name()),Define.PARTITION_HOUR_DATEFORMAT);
            diff += context.getPartitionHourAddRange();
        }

        return diff;
    }

    /**
     * compareDate1 >  compareDate2 return 1
     * compareDate1 <  compareDate2 return -1
     * compareDate1 == compareDate2 return 0
     * @param compareDate1
     * @param compareDate2
     * @param partitionUnit
     * @return
     */
    private int dateCompare(String compareDate1, String compareDate2, String partitionUnit){

        String dateFormat = "";
        if(StringUtils.equals(partitionUnit, Define.PARTITION_UNIT_DAY)){
            dateFormat = Define.PARTITION_DAY_DATEFORMAT;
        }else if(StringUtils.equals(partitionUnit,Define.PARTITION_UNIT_HOUR)){
            dateFormat = Define.PARTITION_HOUR_DATEFORMAT;
        }

        try{
            DateTime date1 = new DateTime(DateUtils.parseDate(compareDate1, dateFormat).getTime());
            DateTime date2 = new DateTime(DateUtils.parseDate(compareDate2, dateFormat).getTime());

            DateTimeComparator dateTimeComparator = DateTimeComparator.getInstance();
            return dateTimeComparator.compare(date1,date2);
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }


}
