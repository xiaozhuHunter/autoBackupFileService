package org.hopxz.autobackup.server.communication;

import org.hopxz.autobackup.server.message.MsgEntrance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServerThread {
    private int port;
    private boolean isFinished;
    private ServerSocket serverSocket;
    private ArrayList<SocketThread> socketThreads;
    public TCPServerThread(int port){
        this.port = port;
        socketThreads = new ArrayList<>();
    }
    public void start(){
        isFinished = false;
        try {
            serverSocket = new ServerSocket(port);
            while (!isFinished){
                Socket socket = serverSocket.accept();
                SocketThread socketThread =new SocketThread(socket);
                socketThreads.add(socketThread);
                socketThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void stop(){
        isFinished = true;
        for(SocketThread socketThread:socketThreads){
            socketThread.interrupt();
            socketThread.close();
        }
        try {
            if(serverSocket != null){
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private class SocketThread extends Thread{
        private Socket socket;
        private InputStream in;
        private OutputStream out;
        SocketThread(Socket socket){
            this.socket = socket;
            try {
                in=socket.getInputStream();
                out=socket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void run(){
            while(!interrupted()){
                if(in == null){
                    return;
                }
                try{
                    int available = in.available();
                    if(available>0){
                        byte[]buffer=new byte[available];
                        int size = in.read(buffer);
                        if(size > 0){
                            String data = new String(buffer,0,size);
                            String resultStr = new MsgEntrance().dealMsgAndInvokeChildMethod(data).toString();
                            out.write(resultStr.getBytes());
                            out.flush();
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        void close(){
            try{
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
                if(socket != null){
                    socket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}