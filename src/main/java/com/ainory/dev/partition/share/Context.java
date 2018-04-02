package com.ainory.dev.partition.share;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by ainory on 2016. 8. 31..
 */
public class Context {

    private String databaseList;
    private Integer executionPeriodHour;
    private Integer executionPeriodMinute;
    private Integer executionPeriodSecond;

    private String commonDatabaseDriver;
    private String commonDatabaseUrl;
    private String commonDatabaseHost;
    private String commonDatabasePort;
    private String commonDatabaseUser;
    private String commonDatabasePass;

    private Integer partitionDayAddRange;
    private Integer partitionHourAddRange;

    public String getDatabaseList() {
        return databaseList;
    }

    public void setDatabaseList(String databaseList) {
        this.databaseList = databaseList;
    }

    public Integer getExecutionPeriodHour() {
        return executionPeriodHour;
    }

    public void setExecutionPeriodHour(Integer executionPeriodHour) {
        this.executionPeriodHour = executionPeriodHour;
    }

    public Integer getExecutionPeriodMinute() {
        return executionPeriodMinute;
    }

    public void setExecutionPeriodMinute(Integer executionPeriodMinute) {
        this.executionPeriodMinute = executionPeriodMinute;
    }

    public Integer getExecutionPeriodSecond() {
        return executionPeriodSecond;
    }

    public void setExecutionPeriodSecond(Integer executionPeriodSecond) {
        this.executionPeriodSecond = executionPeriodSecond;
    }

    public String getCommonDatabaseDriver() {
        return commonDatabaseDriver;
    }

    public void setCommonDatabaseDriver(String commonDatabaseDriver) {
        this.commonDatabaseDriver = commonDatabaseDriver;
    }

    public String getCommonDatabaseUrl() {
        return commonDatabaseUrl;
    }

    public void setCommonDatabaseUrl(String commonDatabaseUrl) {
        this.commonDatabaseUrl = commonDatabaseUrl;
    }

    public String getCommonDatabaseHost() {
        return commonDatabaseHost;
    }

    public void setCommonDatabaseHost(String commonDatabaseHost) {
        this.commonDatabaseHost = commonDatabaseHost;
    }

    public String getCommonDatabasePort() {
        return commonDatabasePort;
    }

    public void setCommonDatabasePort(String commonDatabasePort) {
        this.commonDatabasePort = commonDatabasePort;
    }

    public String getCommonDatabaseUser() {
        return commonDatabaseUser;
    }

    public void setCommonDatabaseUser(String commonDatabaseUser) {
        this.commonDatabaseUser = commonDatabaseUser;
    }

    public String getCommonDatabasePass() {
        return commonDatabasePass;
    }

    public void setCommonDatabasePass(String commonDatabasePass) {
        this.commonDatabasePass = commonDatabasePass;
    }

    public Integer getPartitionDayAddRange() {
        return partitionDayAddRange;
    }

    public void setPartitionDayAddRange(Integer partitionDayAddRange) {
        this.partitionDayAddRange = partitionDayAddRange;
    }

    public Integer getPartitionHourAddRange() {
        return partitionHourAddRange;
    }

    public void setPartitionHourAddRange(Integer partitionHourAddRange) {
        this.partitionHourAddRange = partitionHourAddRange;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("databaseList", databaseList)
                .append("executionPeriodHour", executionPeriodHour)
                .append("executionPeriodMinute", executionPeriodMinute)
                .append("executionPeriodSecond", executionPeriodSecond)
                .append("commonDatabaseDriver", commonDatabaseDriver)
                .append("commonDatabaseUrl", commonDatabaseUrl)
                .append("commonDatabaseHost", commonDatabaseHost)
                .append("commonDatabasePort", commonDatabasePort)
                .append("commonDatabaseUser", commonDatabaseUser)
                .append("commonDatabasePass", commonDatabasePass)
                .append("partitionDayAddRange", partitionDayAddRange)
                .append("partitionHourAddRange", partitionHourAddRange)
                .toString();
    }
}
