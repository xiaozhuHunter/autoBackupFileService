package org.hopxz.autobackup.server.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Base64Utils {
    /*
     * 将文件转化为base64密文
     */
    public String encrytFileToBase64(String filepath){
        String base64Str = null;
        try {
            byte[]buffer = Files.readAllBytes(Paths.get(filepath));
            base64Str = Base64.getEncoder().encodeToString(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return base64Str;
    }
    /*将base64密文转换为文件
     * */
    public boolean depcrytBase64ToFile(String base64Str,String filepath){
        boolean flag = false;
        FileOutputStream fos = null;
        File f = new File(filepath);
        byte[] b = Base64.getDecoder().decode(base64Str);
        try {
            fos = new FileOutputStream(f);
            fos.write(b);
            fos.close();
            flag = true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }
}
