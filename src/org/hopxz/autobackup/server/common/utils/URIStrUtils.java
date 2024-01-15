package org.hopxz.autobackup.server.common.utils;

import java.util.HashMap;

public class URIStrUtils {
    public static HashMap<String,String> getMapFromURIStr(String uriString){
        HashMap<String,String> hashMap = new HashMap<>();
        String[] strArr = uriString.split("&");
        for(String s:strArr){
            String[] strArr2 = s.split("=");
            hashMap.put(strArr2[0],strArr2[1]);
        }
        return hashMap;
    }
}
