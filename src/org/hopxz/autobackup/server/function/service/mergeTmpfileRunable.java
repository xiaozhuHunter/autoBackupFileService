package org.hopxz.autobackup.server.function.service;

import org.hopxz.autobackup.server.common.utils.Base64Utils;
import org.hopxz.autobackup.server.common.utils.SQLUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class mergeTmpfileRunable implements Runnable {
    private SQLUtils sqlUtils = new SQLUtils();
    private Base64Utils base64Utils = new Base64Utils();
    private long sleeptimes = 36000;
    @Override
    public void run() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
        while(true){
            arrayList = selectNeedMergeTempFileList();
            for(String tempfilePath:arrayList){
                String filepath = tempfilePath.replace("/temp","");
                mergeTmpFile(getBase64StrListBySelectTmp(tempfilePath),filepath);
            }
            Thread.sleep(sleeptimes);
        }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSleeptimes(long sleeptimes) {
        this.sleeptimes = sleeptimes;
    }

    protected ArrayList<String> selectNeedMergeTempFileList(){
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
            }
        }
        return arrlist;
   }
   protected ArrayList<String> getBase64StrListBySelectTmp(String fileNamePath){
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
        String content = "";
       try {
           content = new String(Files.readAllBytes(Paths.get(filePath)));
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
       return content;
   }
   protected void mergeTmpFile(ArrayList<String> base64StrList,String filepath){
        String base64FromFileStr = "";
        for(String s:base64StrList){
            base64FromFileStr = base64FromFileStr + s;
        }
        base64Utils.depcrytBase64ToFile(base64FromFileStr,filepath);
   }
}
