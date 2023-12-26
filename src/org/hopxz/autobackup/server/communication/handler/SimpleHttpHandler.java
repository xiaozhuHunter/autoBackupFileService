package org.hopxz.autobackup.server.communication.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hopxz.autobackup.server.message.MsgEntrance;

import java.io.*;
import java.util.logging.Logger;

public class SimpleHttpHandler implements HttpHandler {
    private Logger logger = Logger.getLogger("SimpleHttpHandler");
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(),"utf-8"));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while((line = bufferedReader.readLine())!=null){
            sb.append(line);
        }
        String requestString = sb.toString();
        String rspStr = new MsgEntrance().
                dealMsgAndInvokeChildMethod(requestString).toString();
        logger.info("recvice message:"+rspStr);
        exchange.getResponseHeaders().add("Content-Type","text/xml;charset=utf-8");
        exchange.sendResponseHeaders(200,rspStr.length());
        OutputStream os = exchange.getResponseBody();
        os.write(rspStr.getBytes());
        os.flush();
        os.close();
    }
}
