package org.hopxz.autobackup.server.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static final int BUFFER_SIZE=1024;
    public String getFileMD5(File f){
        //获取指定文件的MD5
        String md5Str = null;
        if(f == null || !f.isFile()){
            md5Str=null;
        }
        MessageDigest digest = null;
        FileInputStream fi;
        byte[] bytes = new byte[BUFFER_SIZE];
        int len = 0;
        try {
            digest = MessageDigest.getInstance("MD5");
            fi = new FileInputStream(f);
            while ((len = fi.read(bytes,0,BUFFER_SIZE))!= -1){
                digest.update(bytes,0,len);
            }
            fi.close();
        } catch (NoSuchAlgorithmException | FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[]md5Bytes = digest.digest();
        StringBuilder sb =new StringBuilder();
        for(byte b: md5Bytes){
            sb.append(String.format("%02x",b));
        }
        md5Str = sb.toString();
        return md5Str;
    }
}
