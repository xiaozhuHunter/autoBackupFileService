package org.hopxz.autobackup.server;

import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.communication.SimpleHttpServer;
import org.hopxz.autobackup.server.communication.TCPServerThread;
import org.hopxz.autobackup.server.function.service.MergeTmpfileRunable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class StartService {
    private static HashMap<String,Object> initMap = new HashMap<>();
    private static Logger log = Logger.getLogger("StartService");
    public static void main(String[] args){
        try {
            //参数初始化
            initializParameter();
            log.info("参数初始化成功");
            //合并分片文件线程任务启动sleep_times
            Thread task = new Thread(new MergeTmpfileRunable(Integer.parseInt(initMap.get("sleep_times").toString())));
            task.start();
            log.info("分片合并服务启动成功");
            //HTTP通讯服务启动
            SimpleHttpServer.Start(Integer.parseInt(initMap.get("http_server_port").toString()),
                    Integer.parseInt(initMap.get("pool_size").toString()));
            log.info("HTTPServer started");
            //TCP通讯服务启动
            new TCPServerThread(Integer.parseInt(initMap.get("server_port").toString())).start();
        }catch (NumberFormatException e){
            log.warning("参数格式出现错误，错误信息为："+e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    protected static void initializParameter(){//读取当前系统初始化参数方法
        /*初始化参数的数值类型目前限制为Long，Integer，String*/
        log.info("服务开始启动，参数初始化开始");
        SQLUtils sqlUtils = new SQLUtils();
        ArrayList<HashMap<String,Object>> resultMaps = sqlUtils.
                getResultBySelect("*",
                        "server_initmsg",
                        "system_code = 'StartService'");
        for(HashMap<String,Object> hashMap:resultMaps) {
            String typeStr = hashMap.get("init_attribute").toString();
            Object valueObj = hashMap.get("init_value").toString();
            String keyStr = hashMap.get("init_code").toString();
            log.info("typeStr:"+typeStr+" valueObj:"+valueObj+" keyStr:"+keyStr);
            if (typeStr.equals("Integer")) {
                initMap.put(keyStr,Integer.parseInt(valueObj.toString()));
            }else if(typeStr.equals("Long")){
                initMap.put(keyStr,Long.parseLong(valueObj.toString()));
            }else if(typeStr.equals("String")){
                initMap.put(keyStr,valueObj.toString());
            }else{
                throw new NumberFormatException();
            }
        }
        log.info("初始化参数获取完成");
    }
}
