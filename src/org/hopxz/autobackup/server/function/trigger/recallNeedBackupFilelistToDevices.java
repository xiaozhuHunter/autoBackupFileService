package org.hopxz.autobackup.server.function.trigger;

import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.function.impl.baseTriggerFunctionImpl;
import org.hopxz.autobackup.server.message.xmlUtils.packerXML;

import java.util.ArrayList;
import java.util.HashMap;

public class recallNeedBackupFilelistToDevices implements baseTriggerFunctionImpl {
    @Override
    public String getResult(HashMap<String, Object> hashMap) {
        return getResultAsSoonAsSummaryDifferenceBetweenServerAndDevice(hashMap);
    }
    protected String getResultAsSoonAsSummaryDifferenceBetweenServerAndDevice(HashMap<String,Object>hashMap){
        String deviceInfo = hashMap.get("deviceid").toString();
        String userIdInfo = hashMap.get("loginid").toString();
        ArrayList<HashMap<String,Object>> fileInfoList = (ArrayList<HashMap<String, Object>>) hashMap.get("fileList");
        HashMap<String,Object> recvMsgMap = new HashMap<>();//用于收集结果map对象
        packerXML packerXML = new packerXML();
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
                                deviceInfo+"' and a.backupFlag = '1'");
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
        recvMsgMap.put("/server/body/array/fileInfoList",tempArrayList);
        recvMsgMap.put("/server/comm_head/deviceid","backupFileService");
        recvMsgMap.put("/server/comm_head/msgtype","returnResult");
        recvMsgMap.put("/server/comm_head/returncode","success");
        return packerXML.packer(recvMsgMap);
    }
}
