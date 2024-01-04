package org.hopxz.autobackup.server.message;


import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.common.DefaultRecvMsg;
import org.hopxz.autobackup.server.message.xmlUtils.ParserXML;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class MsgEntrance {
    private SQLUtils sqlUtils = new SQLUtils();
    private Logger log = Logger.getLogger("MsgEntrance");
    public HashMap<String,Object> parser(String msgString){
        HashMap<String,Object>hashMap = new HashMap<>();
        ParserXML parserXML = new ParserXML();
        HashMap<String,Object> tempHashMap = parserXML.parser(msgString);
        if(tempHashMap.containsKey("/server/comm_head/msgtype") &&
                (tempHashMap.get("/server/comm_head/msgtype").equals("") ||
                        tempHashMap.get("/server/comm_head/msgtype") !=null)){//判断报文是否带有报文类型字段，字段值是否为空
            log.info("undeal msg:"+msgString);
            String msgtypeStr = tempHashMap.get("/server/comm_head/msgtype").toString();
            log.info("报文类型："+msgtypeStr);
            Set<String> keySets = tempHashMap.keySet();
            for(int i=0;i<keySets.size();i++){
                String tempHashMapKey = keySets.toArray()[i].toString();
                String simpleKey = sqlUtils.getResultBySelect(
                        "fieldName","cfg_msg_list",
                        "msgName in ('comm_head','"+msgtypeStr+"_req')" +
                                "and fieldPathInMsg = '"+tempHashMapKey+"'").get(0)
                        .get("fieldName").toString();
                log.info("第"+(i+1)+"个报文字段:"+simpleKey+"报文字段路径："+tempHashMapKey+"，字段值:"+tempHashMap.get(tempHashMapKey));
                hashMap.put(simpleKey,tempHashMap.get(tempHashMapKey));
            }
        }
        return hashMap;
    }
}
