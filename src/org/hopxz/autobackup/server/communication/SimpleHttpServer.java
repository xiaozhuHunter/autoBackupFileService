package org.hopxz.autobackup.server.communication;

import com.sun.net.httpserver.HttpServer;
import org.hopxz.autobackup.server.communication.handler.SimpleHttpHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SimpleHttpServer {
    public static void Start(int port,String contextStr,int poolSize,String reqStr,String rspStr) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port),0);
        httpServer.createContext(contextStr,new SimpleHttpHandler());
        httpServer.setExecutor(Executors.newFixedThreadPool(poolSize));
        httpServer.start();
    }
}
