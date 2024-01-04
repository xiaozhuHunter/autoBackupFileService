package org.hopxz.autobackup.server.manage.application;

import org.hopxz.autobackup.server.common.utils.ReadFileUtils;
import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.common.utils.URIStrUtils;
import org.hopxz.autobackup.server.manage.application.impl.BaseApplicationImpl;

import java.util.HashMap;
import java.util.logging.Logger;

public class Login implements BaseApplicationImpl {
    private Logger log = Logger.getLogger("Login");
    @Override
    public String resultStr(String reqStr) {
        log.info("收到的讯息:"+reqStr);
        String result = "";

        return result;
    }
}
