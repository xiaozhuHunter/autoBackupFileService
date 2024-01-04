package org.hopxz.autobackup.server.communication.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hopxz.autobackup.server.manage.trigger.URIAction;

import java.io.*;
import java.util.logging.Logger;

public class SimpleHttpHandler implements HttpHandler {
    private Logger logger = Logger.getLogger("SimpleHttpHandler");
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String rspStr=new URIAction().decrpyConfig(exchange);
        exchange.getResponseHeaders().add("Content-Type","text/html;charset=utf-8");
        exchange.sendResponseHeaders(200,rspStr.length());
        OutputStream os = exchange.getResponseBody();
        os.write(rspStr.getBytes());
        os.flush();
        os.close();
    }
}
