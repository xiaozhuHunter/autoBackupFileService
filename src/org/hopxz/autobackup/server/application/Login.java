package org.hopxz.autobackup.server.application;

import org.hopxz.autobackup.server.application.impl.BaseTriggerFunctionImpl;
import org.hopxz.autobackup.server.common.DefaultRecvMsg;

import java.util.HashMap;
import java.util.logging.Logger;

public class Login implements BaseTriggerFunctionImpl {
    private Logger log = Logger.getLogger("Login");
    @Override
    public String getResult(HashMap<String,Object> hashMap) {
        log.info("收到的讯息:"+hashMap);
        String result = new DefaultRecvMsg().getDefaultFailMsg();
        return result;
    }
}
