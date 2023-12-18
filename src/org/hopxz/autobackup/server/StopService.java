package org.hopxz.autobackup.server;

import org.hopxz.autobackup.server.communication.TCPServerThread;
import org.hopxz.autobackup.server.function.service.MergeTmpfileRunable;

public class StopService {
    public static void main(String[] args){
        Thread task = new Thread(new MergeTmpfileRunable());
        task.stop();
        new TCPServerThread(10086).stop();
    }
}
