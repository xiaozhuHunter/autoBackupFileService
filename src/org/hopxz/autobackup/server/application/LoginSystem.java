package org.hopxz.autobackup.server.application;

import org.hopxz.autobackup.server.application.impl.BaseApplicationAbstact;
import org.hopxz.autobackup.server.application.impl.BaseTriggerFunctionImpl;
import org.hopxz.autobackup.server.common.DefaultRecvMsg;
import org.hopxz.autobackup.server.common.utils.SQLUtils;

import java.util.HashMap;
import java.util.logging.Logger;

public class LoginSystem extends BaseApplicationAbstact {
    private Logger log = Logger.getLogger("Login");
    private SQLUtils sqlUtils = new SQLUtils();
    @Override
    public String getResult(HashMap<String,Object> hashMap) {
        log.info("收到的讯息:"+hashMap);
        String result = null;//初始化回执结果，默认值为失败
        String LoginType = hashMap.get("msgtype").toString();
        switch (LoginType){
            case "loginAdd"://添加用户
                String userName = hashMap.get("userName").toString();
                String password = hashMap.get("password").toString();
                String loginName = hashMap.get("loginName").toString();
                if(loginCheck(userName)){
                    result = DefaultRecvMsg.successMsg;
                }else{
                    HashMap<String,Object> sqlHashMap = new HashMap<>();
                    int userIdMax = Integer.parseInt(super.getSelectResultsListByConditionAndColumnlist
                            ("userid as userId",
                            "user_info",
                            "1=1 " +
                                    "order by userId desc").get("userId").toString())+1;
                    sqlHashMap.put("userId",userIdMax);
                    sqlHashMap.put("loginNm",userName);
                    sqlHashMap.put("loginPasswd",password);
                    sqlHashMap.put("loginName",loginName);
                    sqlHashMap.put("userRootPath","/home/autoBackupFile/"+loginName+"/");
                    if(sqlUtils.insertDB(sqlHashMap,"user_info")) {
                        result = DefaultRecvMsg.successMsg;
                    }
                }
                break;
            case "loginCheck":
                String loginId = hashMap.get("loginId").toString();
                String passwd = hashMap.get("passwd").toString();
                String deviceMac = hashMap.get("deviceMac").toString();
                if(loginCheck(loginId,passwd)
                        && addInfoWhenDeviceIdIsExitsNotByLoginNm(loginId,deviceMac)){
                    result = DefaultRecvMsg.successMsg;
                };
                break;
            default:
                result = DefaultRecvMsg.failMsg;
                break;
        }
        return result;
    }
    private boolean loginCheck(String userName){
        boolean flag = false;
        try {
            String resultNum = super.getSelectResultsListByConditionAndColumnlist
                    ("count(*) as countNum",
                    "user_info",
                    "loginNm = '" + userName + "'").get("countNum").toString();
            if (Integer.parseInt(resultNum) != 0) {
                flag = true;
            }
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }
    private boolean loginCheck(String userName,String password){
        boolean flag = false;
        try {
            String resultNum = super.getSelectResultsListByConditionAndColumnlist
                    ("count(*) as countNum",
                    "user_info",
                    "loginNm = '" + userName + "' and " +
                            "loginPasswd = '" + password + "'").get("countNum").toString();
            if (Integer.parseInt(resultNum) != 0) {
                flag = true;
            }
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }
    private boolean addInfoWhenDeviceIdIsExitsNotByLoginNm(String userName,String deviceMac){
        boolean flag = false;
        int userid = Integer.parseInt(super.getSelectResultsListByConditionAndColumnlist
                ("userid as userId",
                "user_info",
                "loginNm = '"+userName+"'").get("userId").toString());
        //通过用户名和用户设备的mac值比对，确认是否已经绑定用户与设备
        int exitsNum = Integer.parseInt(super.getSelectResultsListByConditionAndColumnlist
                ("count(*) as countNum",
                "devices_info d,user_device_cfg ud",
                "d.deviceid = ud.deviceid and " +
                        "ud.userid= '"+userid+"' and " +
                        "u.loginNm = '"+userName+"' and " +
                        "d.devicemac = '"+deviceMac+"'").get("countNum").toString());
        //判断设备是否已经注册
        int deviceExits = Integer.parseInt(super.getSelectResultsListByConditionAndColumnlist
                ("count(*) as deviceExits ",
                "devices_info",
                "devicemac = '"+deviceMac+"'").get("deviceExits").toString());
        switch (exitsNum){
            case 0://注册设备，绑定用户与设备信息
                HashMap<String,Object> sql1HashMap = new HashMap<>();//用户与设备绑定map
                HashMap<String,Object> sql2HashMap = new HashMap<>();//新设备注册map
                sql1HashMap.put("userid",userid);
                if(deviceExits == 0){//设备是否已经新注册：0-未注册，其他-已注册
                    int deviceidMax = Integer.parseInt(super.getSelectResultsListByConditionAndColumnlist
                            ("deviceId as deviceId",
                            "devices_info",
                            "1=1 " +
                                    "order by deviceid desc").get("deviceId").toString())+1;
                    sql1HashMap.put("deviceid",deviceidMax);
                    sql2HashMap.put("deviceid",deviceidMax);
                    sql2HashMap.put("devicemac",deviceMac);
                    flag = sqlUtils.insertDB(sql1HashMap,"user_device_cfg")
                            && sqlUtils.insertDB(sql2HashMap,"devices_info");
                }else{
                    //获取设备已注册信息
                    int deviceid = Integer.parseInt(super.getSelectResultsListByConditionAndColumnlist
                            ("deviceId as deviceId",
                            "devices_info",
                            "deviceMac = '"+deviceMac+"'").get("deviceId").toString());
                    sql1HashMap.put("deviceid",deviceid);
                    flag = sqlUtils.insertDB(sql1HashMap,"user_device_cfg");
                }
                break;
            default:
                flag = true;
                break;
        }
        return flag;
    }
}
