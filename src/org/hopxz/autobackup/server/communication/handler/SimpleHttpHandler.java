package org.hopxz.autobackup.server.communication.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hopxz.autobackup.server.message.MsgEntrance;

import java.io.*;

public class SimpleHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String rspStr = new MsgEntrance().
                dealMsgAndInvokeChildMethod(getRequestString(exchange)).toString();
        exchange.getResponseHeaders().add("Content-Type","text/xml;charset=utf-8");
        exchange.sendResponseHeaders(200,rspStr.length());
        OutputStream os = exchange.getResponseBody();
        os.write(rspStr.getBytes());
        os.flush();
        os.close();
    }
    private String getRequestString(HttpExchange exchange){
        String requestString = "";
        if(exchange.getRequestMethod().equals("GET")){
            requestString = exchange.getRequestURI().getQuery();
        }else{
            InputStreamReader is = null;
            try {
                is = new InputStreamReader(exchange.getRequestBody(),"utf-8");
                BufferedReader bufferedReader = new BufferedReader(is);
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }
                requestString = sb.toString();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return requestString;
    }
}
