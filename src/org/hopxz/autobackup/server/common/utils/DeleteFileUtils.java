package org.hopxz.autobackup.server.common.utils;

import java.io.File;

public class DeleteFileUtils {
    public void deleteSigleFile(String filepath){
        File file = new File(filepath);
        deleteSigleFile(file);
    }
    public void deleteSigleFile(File f){
        if(f.isFile()&&f.exists())f.delete();
    }
    public void deleteSomeFiles(File[] files){
        for(File file:files){
            deleteSigleFile(file);
        }
    }
    public void deleteSomeFiles(String folderPath){
        File f = new File(folderPath);
        deleteSomeFiles(f);
    }
    public void deleteSomeFiles(File folder){
        if(folder.isDirectory()){
            File[] files = folder.listFiles();
            if(files !=null)deleteSomeFiles(files);
        }
        folder.delete();
    }
}
