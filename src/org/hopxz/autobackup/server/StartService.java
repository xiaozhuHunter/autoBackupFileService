package org.hopxz.autobackup.server;

import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.communication.TCPServerThread;
import org.hopxz.autobackup.server.function.service.MergeTmpfileRunable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class StartService {
    private static HashMap<String,Object> initMap = new HashMap<>();
    private static Logger log = Logger.getLogger("StartService");
    public static void main(String[] args){
        try {
            initializParameter();
            Thread task = new Thread(new MergeTmpfileRunable());
            task.start();
            new TCPServerThread(Integer.parseInt(initMap.get("server_port").toString())).start();
        }catch (NumberFormatException e){
            log.warning(e.getMessage());
        }
    }
    protected static void initializParameter(){//读取当前系统初始化参数方法
        /*初始化参数的数值类型目前限制为Long，Integer，String*/
        SQLUtils sqlUtils = new SQLUtils();
        ArrayList<HashMap<String,Object>> resultMap = sqlUtils.
                getResultBySelect("*",
                        "server_initmsg",
                        "system_code = 'StartService'");
        for(HashMap<String,Object> hashMap:resultMap) {
            String typeStr = hashMap.get("init_attribute").toString();
            Object valueObj = hashMap.get("init_value").toString();
            String keyStr = hashMap.get("init_code").toString();
            if (typeStr.equals("Integer")) {
                initMap.put(keyStr,Integer.parseInt(valueObj.toString()));
            }else if(typeStr.equals("Long")){
                initMap.put(keyStr,Long.parseLong(valueObj.toString()));
            }else{
                throw new NumberFormatException();
            }
        }
    }
}
