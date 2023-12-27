package org.hopxz.autobackup.server.manage.webApp.application;

import org.hopxz.autobackup.server.manage.webApp.application.impl.BaseApplicationImpl;

import java.util.logging.Logger;

public class Login implements BaseApplicationImpl {
    private Logger log = Logger.getLogger("Login");
    @Override
    public String resultStr(String reqStr) {
        log.info(reqStr);
        return "<html><body><h1>HTTP ERROR 500</h1><body></html>";
    }
}
