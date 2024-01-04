package org.hopxz.autobackup.server.manage.trigger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.hopxz.autobackup.server.common.utils.ReadFileUtils;
import org.hopxz.autobackup.server.function.trigger.DefaultRecvMsg;
import org.hopxz.autobackup.server.message.xmlUtils.ParserXML;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class URIAction {
    private Logger log = Logger.getLogger("URIAction");
    private String resultString = null;
    private String functionName = null;
    private static final String configFilePath = "src/org/hopxz/autobackup/server/manage/webApp/cfgFile/URIAction.xml";
    private ArrayList<HashMap<String,Object>> arrayMapList = new ArrayList<>();
    public void uriCreateContext(HttpServer httpServer, HttpHandler httpHandler){
        for(HashMap<String,Object> hashMap1:arrayMapList){
            String URIPath = hashMap1.get("URI_Path").toString();
            log.info("createContext URI_Path:"+URIPath);
            httpServer.createContext(URIPath,httpHandler);
        }
    }
    public String decrpyConfig(HttpExchange httpExchange){
        log.info("调用的URIContext："+httpExchange.getRequestURI().getPath());
        String reqstr = getReqInfo(httpExchange);
        log.info("requestMsg:"+reqstr);
        for(HashMap<String,Object> hashMap1:arrayMapList){
            String URIPath = hashMap1.get("URI_Path").toString();
            log.info("轮询URIPath:"+URIPath);
            if(URIPath.equals(httpExchange.getRequestURI().getPath())){
                log.info("逻辑判断为true");
                functionName = hashMap1.get("Method_Name").toString();
                String methodPath = hashMap1.get("Method_Path").toString();
                log.info("methodType:"+functionName+" methodPath:"+methodPath);
                log.info("触发Function:"+methodPath);
                reflectResult(methodPath,reqstr);
                log.info("触发Function end");
            }else{
                log.info("判断false");
            }
        }
        return resultString;
    }
    private void reflectResult(String methodPath,String reqMsgStr){
        log.info("待处理的请求信息:"+reqMsgStr);
        try {
            log.info("调用反射方法来调用其他功能:"+methodPath);
            Class clazz = Class.forName(methodPath);
            Method method = clazz.getMethod(functionName,String.class);
            resultString = method.invoke(clazz.newInstance(),reqMsgStr).toString();
            log.info("结果处理结束，并收到回执信息："+resultString);
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
            log.info("sb.toString() : "+sb.toString());
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //构造函数，新建时获取配置文件信息
    public URIAction(){
        ParserXML parserXML = new ParserXML();
        HashMap<String, Object> hashMap = parserXML
                .parser(ReadFileUtils.fileContextStr(configFilePath));
        arrayMapList =
                (ArrayList<HashMap<String, Object>>) hashMap.get("/URIAction/URIActionList/array/URIActionInfo");
    }
}
