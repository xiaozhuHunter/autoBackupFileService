package org.hopxz.autobackup.server.function.trigger;

public class defaultRecvMsg {
    private String successMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<server><comm_head><returncode>success</returncode></comm_head><body/></server>";
    private String failMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<server><comm_head><returncode>failure</returncode></comm_head><body/></server>";
    public String getDefaultSuccessResult() {
        return successMsg;
    }
    public String getDefaultFailMsg() {
        return failMsg;
    }
}
