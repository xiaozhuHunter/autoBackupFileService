package org.hopxz.autobackup.server.manage.webApp.trigger;

import com.sun.net.httpserver.HttpExchange;
import org.hopxz.autobackup.server.common.utils.ReadFileUtils;
import org.hopxz.autobackup.server.message.xmlUtils.ParserXML;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class URIAction {
    private Logger log = Logger.getLogger("URIAction");
    private HttpExchange httpExchange = null;
    private String resultString = null;
    private static final String configFilePath = "src/org/hopxz/autobackup/server/manage/webApp/cfgFile/URIAction.xml";
    public String decrpyConfig(HttpExchange httpExchange){
        log.info("解析配置文件");
        ParserXML parserXML = new ParserXML();
        HashMap<String, Object> hashMap = parserXML
                .parser(ReadFileUtils.fileContextStr(configFilePath));
        ArrayList<HashMap<String,Object>> arrayMapList =
                (ArrayList<HashMap<String, Object>>) hashMap.get("/URIAction/URIActionInfo/array/URIActionList");
        log.info(arrayMapList.toString());
        for(HashMap<String,Object> hashMap1:arrayMapList){
            String URIPath = hashMap1.get("URI_Path").toString();
            if(URIPath.equals(httpExchange.getRequestURI().getPath())){
                String methodType = hashMap1.get("Method_Type").toString();
                String methodPath = hashMap1.get("Method_Path").toString();
                switch (methodType){
                    case "Sheet":
                        resultString=ReadFileUtils.fileContextStr(methodPath);
                        break;
                    case "Function":
                        reflectResult(methodPath);
                        break;
                    default:
                        resultString="<html><body><h1>HTTP ERROR 500</h1><body></html>";
                        break;
                }
            }
        }
        log.info("result: "+resultString);
        return resultString;
    }
    protected void reflectResult(String methondPath){
        log.info(getReqInfo(httpExchange));
        try {
            Class clazz = Class.forName(methondPath);
            Method method = clazz.getMethod("resultStr",String.class);
            resultString = method.invoke(clazz.newInstance(),getReqInfo(httpExchange)).toString();
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
}
