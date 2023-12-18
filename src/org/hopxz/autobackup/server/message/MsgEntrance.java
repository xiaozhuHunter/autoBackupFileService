package org.hopxz.autobackup.server.message;


import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.message.xmlUtils.ParserXML;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MsgEntrance {
    private SQLUtils sqlUtils = new SQLUtils();
    private ArrayList<HashMap<String,Object>> resultMap = new ArrayList<>();
    public Object dealMsgAndInvokeChildMethod(String msgString){
        Object resultMsgObj = null;
        HashMap<String,Object> hashMap = parser(msgString);//methodName
        String className = resultMap.get(0).get("className").toString();
        String methodName = resultMap.get(0).get("methodName").toString();
        try {
            Class clazz=Class.forName(className);
            Method method = clazz.getMethod(methodName,HashMap.class);
            resultMsgObj = method.invoke(clazz.newInstance(),hashMap);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        return resultMsgObj;
    }
    protected HashMap<String,Object> parser(String msgString){
        HashMap<String,Object>hashMap = new HashMap<>();
        ParserXML parserXML = new ParserXML();
        HashMap<String,Object> tempHashMap = parserXML.parser(msgString);
        if(tempHashMap.containsKey("/server/comm_head/msgtype") &&
                (tempHashMap.get("/server/comm_head/msgtype").equals("") ||
                        tempHashMap.get("/server/comm_head/msgtype") !=null)){//判断报文是否带有报文类型字段，字段值是否为空
            String msgtypeStr = tempHashMap.get("/server/comm_head/msgtype").toString();
            Set<String> keySets = tempHashMap.keySet();
            for(int i=0;i<keySets.size();i++){
                String tempHashMapKey = keySets.toArray()[i].toString();
                String simpleKey = sqlUtils.getResultBySelect(
                        "fieldName","cfg_msg_list",
                        "msgName = 'comm_head' or " +
                                "(msgName = '"+msgtypeStr+"' and fieldPathInMsg = '"+tempHashMapKey+"')").get(0)
                        .get("fieldName").toString();
                hashMap.put(simpleKey,hashMap.get(tempHashMapKey));
            }
            resultMap = sqlUtils.getResultBySelect(
                            "methodName,className","cfg_msg_list",
                            "msgName = '"+msgtypeStr+"'");
        }
        return hashMap;
    }
}
