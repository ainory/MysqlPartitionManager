package com.ainory.dev.partition.entity.queryloader;

import java.util.HashMap;

/**
 * Created by ainory on 2016. 6. 20..
 */
public class Queries {

    private HashMap<String,String> queryMap = new HashMap<String, String>();

    public void addSql(Query query){

        this.queryMap.put(query.getId(),query.getValue());
    }

    public String getQuery(String queryId){
        return this.queryMap.get(queryId);
    }

    @Override
    public String toString() {
        return "Queries{" +
                "queryMap=" + queryMap +
                '}';
    }


}
