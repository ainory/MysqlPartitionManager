package com.ainory.dev.partition.entity.db;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by ainory on 2016. 9. 13..
 */
public class InformationSchemaVO {

    private String max_partition;
    private String min_partition;

    public String getMax_partition() {
        return max_partition;
    }

    public void setMax_partition(String max_partition) {
        this.max_partition = max_partition;
    }

    public String getMin_partition() {
        return min_partition;
    }

    public void setMin_partition(String min_partition) {
        this.min_partition = min_partition;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("max_partition", max_partition)
                .append("min_partition", min_partition)
                .toString();
    }
}
