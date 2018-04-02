package com.ainory.dev.partition.entity.db;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by ainory on 2016. 9. 13..
 */
public class PartitionVO {

    private String table_name;
    private String data_term;
    private String partition_unit;
    private String partition_name;

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getData_term() {
        return data_term;
    }

    public void setData_term(String data_term) {
        this.data_term = data_term;
    }

    public String getPartition_unit() {
        return partition_unit;
    }

    public void setPartition_unit(String partition_unit) {
        this.partition_unit = partition_unit;
    }

    public String getPartition_name() {
        return partition_name;
    }

    public void setPartition_name(String partition_name) {
        this.partition_name = partition_name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("table_name", table_name)
                .append("data_term", data_term)
                .append("partition_unit", partition_unit)
                .append("partition_name", partition_name)
                .toString();
    }
}
