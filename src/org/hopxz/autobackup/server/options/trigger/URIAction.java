package org.hopxz.autobackup.server.options.trigger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.hopxz.autobackup.server.common.utils.ReadFileUtils;
import org.hopxz.autobackup.server.message.MsgEntrance;
import org.hopxz.autobackup.server.message.xmlUtils.ParserXML;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class URIAction {
    private Logger log = Logger.getLogger("URIAction");
    private Object resultObj = null;
    private String functionName = null;
    private static final String configFilePath = "src/org/hopxz/autobackup/server/manage/cfgFile/Config.xml";
    private ArrayList<HashMap<String,Object>> arrayMapList = new ArrayList<>();
    private HttpExchange httpExchange = null;
    private String requestString = null;
    private String URIPath1 = null;
    public void uriCreateContext(HttpServer httpServer, HttpHandler httpHandler){
        for(HashMap<String,Object> hashMap1:arrayMapList){
            String URIPath = hashMap1.get("URI_Path").toString();
            log.info("createContext URI_Path:"+URIPath);
            httpServer.createContext(URIPath,httpHandler);
        }
    }
    public Object httpRelationShip(){
        requestString = getReqInfo(httpExchange);
        URIPath1 = httpExchange.getRequestURI().getPath();
        getMethodFromXml();
        return resultObj;
    }
    public Object TCPRelationShip(){
        URIPath1 = requestString.substring(requestString.indexOf("<msgtype>")+9,
                requestString.indexOf("</msgtype>"));
        getMethodFromXml();
        return resultObj;
    }
    private void getMethodFromXml(){
        for(HashMap<String,Object> hashMap1:arrayMapList){
            String URIPath = hashMap1.get("URI_Path").toString();
            log.info("轮询URIPath:"+URIPath);
            log.info("URIPath:"+URIPath1);
            if(URIPath.equals(URIPath1)){
                functionName = hashMap1.get("Method_Name").toString();
                String methodPath = hashMap1.get("Method_Path").toString();
                log.info("触发Function:"+methodPath);
                reflectResult(methodPath,requestString);
                log.info("触发Function end");
            }else{
                log.info("判断false");
            }
        }
    }
    private void reflectResult(String methodPath,String reqMsgStr){
        log.info("待处理的请求信息:"+reqMsgStr);
        try {
            log.info("调用反射方法来调用其他功能:"+methodPath);
            Class clazz = Class.forName(methodPath);
            Method method = clazz.getMethod(functionName,HashMap.class);
            resultObj = method.invoke(clazz.newInstance(),new MsgEntrance().parser(reqMsgStr));
            log.info("结果处理结束，并收到回执信息："+resultObj);
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
    }
    private String getReqInfo(HttpExchange httpExchange){
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(httpExchange.getRequestBody(),"utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //构造函数，新建时获取配置文件信息
    public URIAction(String socketType){
        ParserXML parserXML = new ParserXML();
        HashMap<String, Object> configHashMap =
             parserXML.parser(ReadFileUtils.fileContextStr(configFilePath));
        HashMap<String,Object> hashMap = new HashMap<>();
        for(HashMap<String,Object> tempHashMap:
                (ArrayList<HashMap<String, Object>>)configHashMap.get("/autoBackup/configInfoList/array/configInfo")){
            if(socketType.equals(tempHashMap.get("conn_type"))){
                hashMap = parserXML.parser(ReadFileUtils.fileContextStr(tempHashMap.get("config_path").toString()));
            }
        }
        arrayMapList =
                (ArrayList<HashMap<String, Object>>) hashMap.get("/URIAction/URIActionList/array/URIActionInfo");
    }
    public void setHttpExchange(HttpExchange httpExchange){
        this.httpExchange = httpExchange;
    }
    public void setRequestString(String requestString){
        this.requestString = requestString;
    }
}
