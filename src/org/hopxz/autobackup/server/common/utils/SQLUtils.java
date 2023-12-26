package org.hopxz.autobackup.server.common.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class SQLUtils {
    private Logger log = Logger.getLogger("SQLUtils");
    /*获得数据库连接，sqlite*/
    public Connection getConnection(String driverTypeStr,String uri){
        Connection conn=null;
        try {
            Class.forName(driverTypeStr);
            conn = DriverManager.getConnection(uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return conn;
    }
    /*获得数据库连接，MySQL，Oracle，SqlServer*/
    public Connection getConnection(String driverTypeStr,String uri,String sqlName,String sqlPwd){
        Connection conn = null;
        try {
            Class.forName(driverTypeStr);
            conn = DriverManager.getConnection(uri, sqlName, sqlPwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return conn;
    }
    /*关闭数据库连接，释放资源*/
    public void closeSQLConn(Connection conn){
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    protected ArrayList<HashMap<String,Object>> setHashMapFromResultSet(ResultSet rs){
        ArrayList<HashMap<String,Object>> arrHashMap = new ArrayList<>();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()){
                HashMap<String,Object>hashMap = new HashMap<>();
                for(int i=0;i<rsmd.getColumnCount();i++){
                    hashMap.put(rsmd.getColumnName(i+1), rs.getObject(i+1));
                }
                arrHashMap.add(hashMap);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return arrHashMap;
    }
    /*查询*/
    public ArrayList<HashMap<String,Object>> getResultBySelect(Connection conn,String columnStr,String tableName,String conditonStr){
        ArrayList<HashMap<String,Object>> arrHashMap = new ArrayList<>();
        PreparedStatement stat = null;
        String sqlStr = "select "+columnStr+" from "+tableName+" where "+conditonStr+";";
        log.info("执行sql语句为：【"+sqlStr+"】");
        try {
            stat = conn.prepareStatement(sqlStr);
            ResultSet rs = stat.executeQuery();
            arrHashMap = setHashMapFromResultSet(rs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            closeSQLConn(conn);
            if(stat != null){
                try {
                    stat.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return arrHashMap;
    }
    /*默认使用项目内sqlite3数据库*/
    public ArrayList<HashMap<String,Object>> getResultBySelect(String columnStr,String tableName,String conditonStr){
        Connection conn = getConnection("org.sqlite.JDBC","jdbc:sqlite:database.db");
        return getResultBySelect(conn,columnStr,tableName,conditonStr);
    }
    public boolean insertDB(HashMap<String,Object>hashMap,String tableName){
        Connection conn = getConnection("org.sqlite.JDBC","jdbc:sqlite:database.db");
        return insertDB(conn,hashMap,tableName);
    }
    public boolean updateDB(HashMap<String,Object> hashMap, String tableName, String conditionStr){
        Connection conn = getConnection("org.sqlite.JDBC","jdbc:sqlite:database.db");
        return updateDB(conn,hashMap,tableName,conditionStr);
    }
    /*插入和更新共同调用的方法*/
    protected boolean executeDB(Connection conn,String sqlStr){
        boolean flag = false;
        PreparedStatement stat =null;
        log.info("执行sql语句为：【"+sqlStr+"】");
        try {
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(sqlStr);
            int i = stat.executeUpdate();
            if(i>0){
                flag = true;
            }else{
                flag = false;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(stat != null){
                try {
                    stat.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            closeSQLConn(conn);
        }
        return flag;
    }
    /*新增*/
    public boolean insertDB(Connection conn,HashMap<String,Object>hashMap,String tableName){
        boolean flag = false;
        String columnName = "";
        String columnValue = "";
        Set<String> columnNameSet = hashMap.keySet();
        for(int i =0;i<hashMap.size();i++){
            if(i == 0 ) {
                columnName = columnNameSet.toArray()[i]  + columnName;
                columnValue = "'" + hashMap.get(columnNameSet.toArray()[i]) + "'" + columnValue;
            }else{
                columnName = columnNameSet.toArray()[i]+","+ columnName;
                columnValue = "'" + hashMap.get(columnNameSet.toArray()[i]) + "'," + columnValue;
            }
        }
        String sqlStr = "insert into "+tableName+"("+columnName+") values ("+columnValue+");";
        executeDB(conn,sqlStr);
        return flag;
    }
    /*更新*/
    public boolean updateDB(Connection conn, HashMap<String,Object> hashMap, String tableName, String conditionStr){
        boolean flag = false;
        String updateStr = "";
        Set<String> columnNameSet = hashMap.keySet();
        for(int i =0 ;i < hashMap.size();i++){
            if(i < hashMap.size()-1){
                updateStr = updateStr+columnNameSet.toArray()[i]+" = '"+hashMap.get(columnNameSet.toArray()[i])+"',";
            }else{
                updateStr = updateStr+columnNameSet.toArray()[i]+" = '"+hashMap.get(columnNameSet.toArray()[i])+"'";
            }
        }
        updateStr = "update "+tableName+" set "+updateStr+" where "+conditionStr+";";
        executeDB(conn,updateStr);
        return flag;
    }
}
