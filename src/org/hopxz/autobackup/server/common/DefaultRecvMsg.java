package org.hopxz.autobackup.server.common;

public class DefaultRecvMsg {
    public static final String successMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<server><comm_head><returncode>success</returncode></comm_head><body/></server>";
    public static final String failMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<server><comm_head><returncode>failure</returncode></comm_head><body/></server>";
}
