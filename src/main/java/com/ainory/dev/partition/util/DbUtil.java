package com.ainory.dev.partition.util;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by ainory on 2016. 6. 13..
 */
public class DbUtil {

    private String JDBC_DRIVER = "";
    private String DB_URL = "";
    private String USER = "";
    private String PASS = "";

    private Connection conn = null;

    public DbUtil(String driver, String url, String user, String passwd) throws Exception{

        JDBC_DRIVER = driver;
        DB_URL = url;
        USER = user;
        PASS = passwd;


        if(!DbUtils.loadDriver(JDBC_DRIVER)){
            new Throwable("JDBC Driver Load Error" + JDBC_DRIVER);
        }
    }

    public Connection getConnection(){

        try{
            if(conn == null || conn.isClosed()){

                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                return conn;
            } else {
                return conn;
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean closeConnection(){

        try{
            if(conn == null || conn.isClosed()){
                return true;
            }else{
                conn.close();
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try{

            /*DbUtil dbUtil = new DbUtil();

            Connection conn = dbUtil.getConnection();

            System.out.println(conn.isValid(5));

            ResultSetHandler resultSetHandler = new BeanListHandler(SystemInfo.class);
            QueryRunner queryRunner = new QueryRunner();
            List<SystemInfo> list = (List)queryRunner.query(conn, QueryLoaderUtil.getQuery("select_rest_server"), resultSetHandler);

            for(SystemInfo systemInfo: list){
                System.out.println(systemInfo.toString());
            }*/



        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
