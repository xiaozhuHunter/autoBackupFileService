package org.hopxz.autobackup.server.application.tcpApp;

import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.application.impl.BaseTriggerFunctionImpl;
import org.hopxz.autobackup.server.common.DefaultRecvMsg;
import org.hopxz.autobackup.server.message.MsgExit;
import org.hopxz.autobackup.server.message.xmlUtils.PackerXML;

import java.util.ArrayList;
import java.util.HashMap;

public class RecallNeedBackupFilelistToDevices implements BaseTriggerFunctionImpl {
    @Override
    public String getResult(HashMap<String, Object> hashMap) {
        String msgStr = null;
        try{
            msgStr = getResultAsSoonAsSummaryDifferenceBetweenServerAndDevice(hashMap);
        }catch (Exception e){
            msgStr = DefaultRecvMsg.failMsg;
        }
        return msgStr;
    }
    protected String getResultAsSoonAsSummaryDifferenceBetweenServerAndDevice(HashMap<String,Object>hashMap){
        String deviceInfo = hashMap.get("deviceid").toString();
        String userIdInfo = hashMap.get("loginid").toString();
        ArrayList<HashMap<String,Object>> fileInfoList = (ArrayList<HashMap<String, Object>>) hashMap.get("fileList");
        HashMap<String,Object> recvMsgMap = new HashMap<>();//用于收集结果map对象
        PackerXML packerXML = new PackerXML();
        SQLUtils sqlUtils = new SQLUtils();
        String pathStr = sqlUtils.getResultBySelect("userRootPath",
                "nodeal_file_list",
                "userId = '"+userIdInfo+"'").get(0).get("userRootPath").toString();
        ArrayList<HashMap<String,Object>> sqlResultsStr = sqlUtils.
                getResultBySelect("a.filename as fileName,a.md5str as fileMd5",
                        "nodeal_file_list a,user_info b,user_device_cfg c",
                        "b.userid = c.userid and a.devFromWhere = c.deviceid "+
                                "a.filepath = b.userRootPath + a.devFromWhere and " +
                                "b.userid = '"+userIdInfo+"' and b.devFromWhere = '" +
                                deviceInfo+"' and a.backupFlag in ('0','1','2')");
        ArrayList<HashMap<String,Object>> tempArrayList = new ArrayList<>();
        for(HashMap<String,Object> hashMap1:fileInfoList){
            if(!sqlResultsStr.contains(hashMap1)){
                tempArrayList.add(hashMap1);
                HashMap<String,Object> tempHashMap = new HashMap<>();
                tempHashMap.putAll(hashMap1);
                tempHashMap.put("devFromWhere",deviceInfo);
                tempHashMap.put("backupFlag",0);
                tempHashMap.put("filePath",pathStr+deviceInfo);
                sqlUtils.insertDB(tempHashMap,"nodeal_file_list");
            }
        }
        recvMsgMap.put("fileInfoList",tempArrayList);
        recvMsgMap.put("deviceid","backupFileService");
        recvMsgMap.put("msgtype","filesList");
        recvMsgMap.put("returncode","success");
        return new MsgExit().packer(recvMsgMap);
    }
}
