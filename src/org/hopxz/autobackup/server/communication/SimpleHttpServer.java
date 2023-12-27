package org.hopxz.autobackup.server.communication;

import com.sun.net.httpserver.HttpServer;
import org.hopxz.autobackup.server.communication.handler.SimpleHttpHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SimpleHttpServer {
    private static Logger logger = Logger.getLogger("SimpleHttpServer");
    public static void Start(int port,String contextStr,int poolSize) throws IOException {
        logger.info("http_port:"+port+"\nhttpserver poolSize:"+poolSize);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port),0);
        logger.info("contextStr: "+contextStr);
        SimpleHttpHandler httpHandler = new SimpleHttpHandler();
        httpServer.createContext(contextStr,httpHandler);
        httpServer.setExecutor(Executors.newFixedThreadPool(poolSize));
        httpServer.start();
    }
}
