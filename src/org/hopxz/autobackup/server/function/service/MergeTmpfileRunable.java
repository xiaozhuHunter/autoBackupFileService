package org.hopxz.autobackup.server.function.service;

import org.hopxz.autobackup.server.common.utils.Base64Utils;
import org.hopxz.autobackup.server.common.utils.DeleteFileUtils;
import org.hopxz.autobackup.server.common.utils.MD5Utils;
import org.hopxz.autobackup.server.common.utils.SQLUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class MergeTmpfileRunable implements Runnable {
    private SQLUtils sqlUtils = new SQLUtils();
    private Base64Utils base64Utils = new Base64Utils();
    private long sleeptimes = 30000;//默认值30秒
    private MD5Utils md5Utils = new MD5Utils();
    private DeleteFileUtils deleteFileUtils = new DeleteFileUtils();
    @Override
    public void run() {
        try {
        while(true){
            ArrayList<String> arrayList = selectNeedMergeTempFileList();
            for(String tempfilePath:arrayList){
                mergeTmpFile(getBase64StrListBySelectTmp(tempfilePath),tempfilePath);
            }
            Thread.sleep(sleeptimes);
        }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //设置定时CD时间
    public MergeTmpfileRunable(long sleeptimes){
        if(sleeptimes != 0){
            this.sleeptimes = sleeptimes;
        }
    }
    protected ArrayList<String> selectNeedMergeTempFileList(){
        /*查询待处理合并的文件*/
        ArrayList<String> arrlist = new ArrayList<>();
        ArrayList<HashMap<String,Object>> resultBySelect = sqlUtils.
                getResultBySelect("distinct a.filename as fileName,b.tmpfilePath as tmpfilePath," +
                                "a.filepath as filePath,a.devFromWhere as deviceinfo",
                        "nodeal_file_list a,nodeal_tmpfile_list b",
                        "b.filename = a.filename and " +
                                "a.backupFlag = '0' and a.chkSign in ('0','1')");
        for(HashMap<String,Object> hashMap:resultBySelect){
            if(chkTempFileAllRecved(hashMap.get("fileName").toString(),hashMap.get("tmpfilePath").toString())) {
                arrlist.add(hashMap.get("filePath").toString()+
                        hashMap.get("deviceinfo").toString()+"/temp/"+
                        hashMap.get("fileName").toString());
                HashMap<String,Object> hashMap1 = new HashMap<>();
                hashMap1.put("backupFlag","2");//更新为备份中
                sqlUtils.updateDB(hashMap1,
                        "nodeal_file_list",
                        "filename = '"+hashMap.get("fileName")+"' and " +
                                "devFromWhere = '"+hashMap.get("deviceinfo")+"' and " +
                                "filepath = '"+hashMap.get("filePath")+"' and " +
                                "backupFlag = '0'");
            }
        }
        return arrlist;
   }
   protected ArrayList<String> getBase64StrListBySelectTmp(String fileNamePath){
        /*获取分片文件中的base64字符串数据*/
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<HashMap<String,Object>> resultBySelect = sqlUtils.
                getResultBySelect("tmpfileNm",
                        "nodeal_tmpfile_list",
                        "tmpfilePath = '"+fileNamePath+"/"+"'" +
                                "order by tmpfileNm");
        for(HashMap<String,Object> hashMap:resultBySelect){
            arrayList.add(readAsString(fileNamePath+"/"+hashMap.get("tmpfileNm")));
        }
        return arrayList;
   }
   protected boolean chkTempFileAllRecved(String fileName,String tmpfilePath){
        /*校验分片文件是否接收完全*/
        boolean flag = false;
        int countNum = (Integer) sqlUtils.getResultBySelect("count(*) as tmpfileCount",
                "nodeal_tmpfile_list",
                "filename = '"+fileName+"' and " +
                        "tmpfilePath = '"+tmpfilePath+"'").get(0).get("tmpfileCount");
        int tmpfileNum = (Integer) sqlUtils.getResultBySelect("tmpfileNum",
               "nodeal_tmpfile_list",
               "filename = '"+fileName+"' and " +
                       "tmpfilePath = '"+tmpfilePath+"'").get(0).get("tmpfileNum");
        flag = countNum==tmpfileNum && countNum != 0 && tmpfileNum != 0;
        return flag;
   }
   protected String readAsString(String filePath){
        /*从分片文件中读取内容字符串*/
        String content = "";
       try {
           content = new String(Files.readAllBytes(Paths.get(filePath)));
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
       return content;
   }
   protected void mergeTmpFile(ArrayList<String> base64StrList,String tempfilepath){
        /*合并分片文件主程序*/
        String base64FromFileStr = "";
        for(String s:base64StrList){
            base64FromFileStr = base64FromFileStr + s;
        }
       String filepath = tempfilepath.replace("/temp","");
        HashMap<String, Object> hashMap1 = new HashMap<>();
        if(base64Utils.depcrytBase64ToFile(base64FromFileStr,filepath)) {
            String md5str = md5Utils.getFileMD5(new File(filepath));
            hashMap1.put("backupFlag", "1");//更新为已备份
            if(sqlUtils.updateDB(hashMap1,
                    "nodeal_file_list",
                    "filepath||devFromWhere||'/'||filename = '" + filepath + "'and " +
                            "backupFlag = '2' and (" +
                            "(md5str = '"+md5str+"' and chksign = '1') or (chksign = '0'))")) {
                    deleteFileUtils.deleteSomeFiles(tempfilepath+"/");//合并文件的分片为整个文件，移除临时文件
            }else{
                    hashMap1.put("backupFlag","0");//更新文件备份状态失败，失败原因可能是文件上送的md5值与生成后的文件md5不一致
                    sqlUtils.updateDB(hashMap1,
                            "nodeal_file_list",
                            "filepath||devFromWhere||'/'||filename = '" + filepath + "'and " +
                                    "backupFlag = '2'");
                    deleteFileUtils.deleteSigleFile(filepath);//移除已生成的文件
                }
            }
    }
}
