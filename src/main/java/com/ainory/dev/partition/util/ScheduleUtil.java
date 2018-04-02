package com.ainory.dev.partition.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ainory on 2016. 6. 21..
 */
public class ScheduleUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUtil.class);

    /** Execution Time Return(Hour:Minute:Second)
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getExecutionDate(int hour, int minute, int second){

        Calendar date = Calendar.getInstance();

        if(date.get(Calendar.HOUR_OF_DAY) < hour && date.get(Calendar.MINUTE) < minute && date.get(Calendar.SECOND) < second){
            date.set(Calendar.SECOND, second);
            date.set(Calendar.MINUTE, minute);
            date.set(Calendar.HOUR_OF_DAY, hour);

            date.set(Calendar.MILLISECOND, 0);

            return date.getTime();
        }

        if(date.get(Calendar.SECOND) >= second){
            date.add(Calendar.SECOND, ((60 - date.get(Calendar.SECOND))+second));
        }

        if(date.get(Calendar.MINUTE) >= minute){
            date.add(Calendar.MINUTE, ((60 - date.get(Calendar.MINUTE))+minute));
        }

        if(date.get(Calendar.HOUR_OF_DAY) >= hour){
            date.add(Calendar.HOUR_OF_DAY, ((24 - date.get(Calendar.HOUR_OF_DAY))+hour));
        }

        date.set(Calendar.MILLISECOND, 0);

        return date.getTime();
    }

    /** Execution Time Return(Minute:Second)
     * @param minute
     * @param second
     * @return
     */
    public static Date getExecutionDate(int minute, int second){

        Calendar date = Calendar.getInstance();

        if(date.get(Calendar.MINUTE) < minute && date.get(Calendar.SECOND) < second){
            date.set(Calendar.SECOND, second);
            date.set(Calendar.MINUTE, minute);

            date.set(Calendar.MILLISECOND, 0);

            return date.getTime();
        }

        if(date.get(Calendar.SECOND) >= second){
            date.add(Calendar.SECOND, ((60 - date.get(Calendar.SECOND))+second));
        }

        if(date.get(Calendar.MINUTE) >= minute){
            date.add(Calendar.MINUTE, ((60 - date.get(Calendar.MINUTE))+minute));
        }

        date.set(Calendar.MILLISECOND, 0);

        return date.getTime();
    }

    /** Execution Time Return(Second)
     * @param second
     * @return
     */
    public static Date getExecutionDate(int second){

        Calendar date = Calendar.getInstance();

        if(date.get(Calendar.SECOND) < second){
            date.set(Calendar.SECOND, second);

            date.set(Calendar.MILLISECOND, 0);

            return date.getTime();
        }

        if(date.get(Calendar.SECOND) >= second){
            date.add(Calendar.SECOND, ((60 - date.get(Calendar.SECOND))+second));
        }

        date.set(Calendar.MILLISECOND, 0);

        return date.getTime();
    }

    public static long getTaskPeriod_EveryDay(){
        return 24*60*60*1000;
    }

    public static long getTaskPeriod_EveryHour(){
        return 60*60*1000;
    }

    public static long getTaskPeriod_EveryMinute(){
        return 60*1000;
    }

    /** Get Task Period
     * @param second
     * @return
     */
    public static long getTaskPeriod(int second){
        return second*1000;
    }

    public static long getInsertTime(int prevSec) {
        int itv = prevSec*1000;
        long ct = System.currentTimeMillis() - (prevSec*1000);
        long nct = ((ct/itv)*itv)+itv;
        return nct;
    }

    public static long getInsertTime(long time, int prevSec) {
        int itv = prevSec*1000;
        long ct = time - (prevSec*1000);
        long nct = ((ct/itv)*itv)+itv;
        return nct;
    }

    public static void main(String[] args) {

//        System.out.println(DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.SSS"));
//        System.out.println(DateFormatUtils.format(getExecutionDate(15),"yyyy-MM-dd HH:mm:ss.SSS"));

//        System.out.println(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
//        System.out.println(DateFormatUtils.format(getExecutionDate(11,37,30),"yyyy-MM-dd HH:mm:ss.SSS"));
//        System.out.println(DateFormatUtils.format(getExecutionDate(37,30),"yyyy-MM-dd HH:mm:ss.SSS"));
//        System.out.println(DateFormatUtils.format(getExecutionDate(30),"yyyy-MM-dd HH:mm:ss.SSS"));

    }
}
