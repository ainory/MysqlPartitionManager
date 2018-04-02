package com.ainory.dev.partition.util;

import com.ainory.dev.partition.entity.queryloader.Queries;
import com.ainory.dev.partition.entity.queryloader.Query;
import org.apache.commons.digester3.Digester;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ainory on 2016. 6. 20..
 */
public class QueryLoaderUtil implements ApplicationContextAware{

    private static QueryLoaderUtil queryLoaderUtil;

    private static String XML_PATH;

    private Digester digester = new Digester();
    private static Queries queries;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        queryLoaderUtil = (QueryLoaderUtil) applicationContext.getBean("queryLoaderUtil");
    }

    /*static{
        try{
            queryLoaderUtil = (QueryLoaderUtil)new ClassPathXmlApplicationContext("classpath:applicationContext.xml").getBean("queryLoaderUtil");
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    public QueryLoaderUtil() throws Exception{
        preLoad();
    }

    public QueryLoaderUtil(String queryFilePath) throws Exception{
        this.XML_PATH = queryFilePath;
        preLoad();
    }

    public static void setXML_PATH(String XML_PATH1) {
        XML_PATH = XML_PATH1;
    }

    private void preLoad() throws Exception{
        digester.setValidating(false);

        digester.addObjectCreate("queries", Queries.class);
        digester.addObjectCreate("queries/query", Query.class);

        digester.addSetNext("queries/query", "addSql");

        digester.addCallMethod("queries/query", "setValue", 1);
        digester.addCallParam("queries/query", 0);
        digester.addSetProperties("queries/query", "id", "id");

        digester.addSetNext("queries/query", "addSql");

//        queries = digester.parse(new File(XML_PATH));
        queries = digester.parse(getJarToFile("/",XML_PATH));
    }

    public static String getQuery(String queryId) throws Exception{
        return queries.getQuery(queryId);
    }
    public static String getQuery(String queryId, String tableName) throws Exception{
        return StringUtils.replace(queries.getQuery(queryId), "$tableName", tableName);
    }

    public static String getQueryDbName(String queryId, String dbName) throws Exception{
        return StringUtils.replace(queries.getQuery(queryId), "$dbName", dbName);
    }

    public static String getQueryInStr(String queryId, String inStr) throws Exception{
        return StringUtils.replace(queries.getQuery(queryId), "$inStr", inStr);
    }

    /**
     * Gets jar to file.
     *
     * @param path the path
     * @param file the file
     * @return the jar to file
     */
    protected BufferedReader getJarToFile(String path, String file) {
        InputStream in = getClass().getResourceAsStream(path+file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader;
    }

    public static void main(String[] args) {

        try{
            System.out.println(QueryLoaderUtil.getQuery("select_rest_server"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
