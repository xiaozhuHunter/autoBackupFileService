package org.hopxz.autobackup.server.function.trigger;

import org.hopxz.autobackup.server.common.utils.Base64Utils;
import org.hopxz.autobackup.server.common.utils.DeleteFileUtils;
import org.hopxz.autobackup.server.common.utils.MD5Utils;
import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.function.impl.baseTriggerFunctionImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class recvTempfileFromDevices implements baseTriggerFunctionImpl {
    private defaultRecvMsg msg = new defaultRecvMsg();
    @Override
    public String getResult(HashMap<String, Object> hashMap) {
        String msgStr = msg.getDefaultFailMsg();
        if(saveFileFromMsgmap(hashMap)){
            msgStr = msg.getDefaultSuccessResult();
        }
        return msgStr;
    }
    protected boolean saveFileFromMsgmap(HashMap<String, Object> hashMap){
        SQLUtils sqlUtils = new SQLUtils();
        boolean flag = false;
        HashMap<String,Object> userAndDevicesInfoMap = sqlUtils.getResultBySelect(
                        "a.userRootPath as userRootPath,b.deviceid as deviceid",
                        "user_info a,devices_info b",
                        "a.userId = b.userId and a.userId = '"+hashMap.get("loginid")+"'")
                .get(0);
        String filepath = userAndDevicesInfoMap.get("userRootPath").toString();
        filepath =filepath + userAndDevicesInfoMap.get("deviceid").toString();
        String fileContentStr = hashMap.get("fileContent").toString();
        if(checkTempsignFromMsgmap(hashMap)){//如果发过来的字符串是分片文件内容
            HashMap<String,Object> tempfileInfoMap = new HashMap<>();
            String tempFilePath = filepath+"/temp/"+hashMap.get("fileName")+"/";
            //将分片文件的信息统计，准备写入临时文件信息表中
            tempfileInfoMap.put("tmpfilePath",tempFilePath);
            tempfileInfoMap.put("tmpfileNum",(Integer)hashMap.get("tempFileNum"));
            tempfileInfoMap.put("filename",hashMap.get("fileName"));
            tempfileInfoMap.put("tmpfileNm",hashMap.get("tempFileName"));
            tempfileInfoMap.put("tmpfileStatus",0);//0-未处理
            //将分片文件内容先写入临时文件
            flag = writeIntoTempfile(fileContentStr,tempFilePath+hashMap.get("tempFileName"));
            //分片文件信息写入临时文件信息表
            if(flag){
                sqlUtils.insertDB(tempfileInfoMap,"nodeal_tmpfile_list");
            }
        }else{//如果发过来的字符串不是临时文件内容，是完整文件的
            MD5Utils md5Utils = new MD5Utils();
            String tempPath = filepath;
            filepath = filepath +"/"+ hashMap.get("fileName");
            Base64Utils base64Utils = new Base64Utils();
            HashMap<String,Object> fileInfoMap = sqlUtils.getResultBySelect("md5str,chkSign",
                    "nodeal_file_list",
                    "filename = '"+hashMap.get("fileName")+"' and " +
                            "devFromWhere = '"+userAndDevicesInfoMap.get("deviceid").toString()+"' and " +
                            "filePath = '"+tempPath+"'").get(0);
            //更新待处理文件信息表中文件状态
            if(base64Utils.depcrytBase64ToFile(fileContentStr,filepath)){
                //如果文件生成成功后对文件进行校验，与报文中的文件信息是否一致
                String md5ByFile = md5Utils.getFileMD5(new File(filepath));
                HashMap<String,Object> fileUpdateInfoMap = new HashMap<>();
                fileUpdateInfoMap.put("backupFlag",1);
                flag =(fileInfoMap.get("md5str").toString().equals(md5ByFile))//校验接收到的文件md5与信息中的md5是否一致
                        && sqlUtils.updateDB(fileUpdateInfoMap,
                        "nodeal_file_list",
                        "filename = '"+hashMap.get("fileName")+"' and " +
                                "md5str = '"+md5ByFile+"' and " +
                                "devFromWhere = '"+userAndDevicesInfoMap.get("deviceid").toString()+"' and " +
                                "filePath = '"+tempPath+"'");//备份文件的备份状态更新
            }
            if(!flag && fileInfoMap.get("chkSign").toString().equals("1")){
                //如果文件信息更新失败，且文件匹配标志为需要匹配（1-需要校验）时，移除已生成文件
                DeleteFileUtils deleteFileUtils = new DeleteFileUtils();
                deleteFileUtils.deleteSigleFile(filepath);
            }
        }
        return flag;
    }
    protected boolean writeIntoTempfile(String fileContentStr,String filepath){
        boolean flag = false;
        File file = new File(filepath);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(fileContentStr);
            flag = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }
    protected boolean checkTempsignFromMsgmap(HashMap<String, Object> hashMap){
        //校验map中包含的报文信息中是否携带临时文件标志
        boolean flag = false;
        Set<String> keySets = hashMap.keySet();
        if(keySets.contains("tempFileName") && !hashMap.get("tempFileName").equals("")
                && hashMap.get("tempFileName")!=null){
            flag = true;
        }
        return flag;
    }
}
