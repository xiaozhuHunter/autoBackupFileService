package org.hopxz.autobackup.server.communication;

import com.sun.net.httpserver.HttpServer;
import org.hopxz.autobackup.server.communication.handler.SimpleHttpHandler;
import org.hopxz.autobackup.server.manage.trigger.URIAction;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SimpleHttpServer {
    private static Logger logger = Logger.getLogger("SimpleHttpServer");
    public static void Start(int port,int poolSize) throws IOException {
        logger.info("http_port:"+port+"\nhttpserver poolSize:"+poolSize);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port),0);
        SimpleHttpHandler httpHandler = new SimpleHttpHandler();
        //创建http相关配置对象，用于启动服务配置
        new URIAction("HTTP").uriCreateContext(httpServer,httpHandler);
        httpServer.setExecutor(Executors.newFixedThreadPool(poolSize));
        httpServer.start();
    }
}
