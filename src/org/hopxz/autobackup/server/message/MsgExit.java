package org.hopxz.autobackup.server.message;

import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.message.xmlUtils.PackerXML;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class MsgExit {
    private Logger log = Logger.getLogger("MsgExit");
    private SQLUtils sqlUtils = new SQLUtils();
    public String packer(HashMap<String,Object> hashMap){
        HashMap<String,Object> tempHashMap = new HashMap<>();
        Set<String> keySets = hashMap.keySet();
        String msgtypeStr = hashMap.get("msgtype").toString();
        for(int i = 0 ;i < hashMap.size();i++){
            String tempStr = keySets.toArray()[i].toString();
            String pathStr = sqlUtils.getResultBySelect("fieldPathInMsg",
                    "cfg_msg_list",
                    "msgName in ('comm_head','"+msgtypeStr+"_rsp')" +
                            "and fieldName = '"+tempStr+"'").get(0).get("fieldPathInMsg").toString();
            tempHashMap.put(pathStr,hashMap.get(tempStr));
        }
        return new PackerXML().packer(tempHashMap);
    }
}
