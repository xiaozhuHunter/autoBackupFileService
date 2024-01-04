package org.hopxz.autobackup.server.communication.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hopxz.autobackup.server.options.trigger.URIAction;

import java.io.*;
import java.util.logging.Logger;

public class SimpleHttpHandler implements HttpHandler {
    private Logger logger = Logger.getLogger("SimpleHttpHandler");
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URIAction uriAction = new URIAction("HTTP");
        uriAction.setHttpExchange(exchange);
        String rspStr=uriAction.httpRelationShip().toString();
        exchange.getResponseHeaders().add("Content-Type","text/xml;charset=utf-8");
        exchange.sendResponseHeaders(200,rspStr.length());
        OutputStream os = exchange.getResponseBody();
        os.write(rspStr.getBytes());
        os.flush();
        os.close();
    }
}
