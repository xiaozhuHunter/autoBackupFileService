package org.hopxz.autobackup.server;

import org.hopxz.autobackup.server.communication.TCPServerThread;
import org.hopxz.autobackup.server.function.service.MergeTmpfileRunable;

public class StartService {
    public static void main(String[] args){
        Thread task = new Thread(new MergeTmpfileRunable());
        task.start();
        new TCPServerThread(10086).start();
    }
}
