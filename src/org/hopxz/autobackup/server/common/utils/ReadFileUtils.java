package org.hopxz.autobackup.server.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadFileUtils {
    public static String fileContextStr(File file){
        try {
            String str = new String(Files.readAllBytes(file.toPath()));
            str = str.replace("\t","")
                    .replace("\n","");
            if(str.contains("<?xml version=\"1.0\" encoding=\"utf-8\"?>")){
                str = str.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>","").
                        replace(" ","");
                str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+str;
            }
            return str;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String fileContextStr(String filepath){
        return fileContextStr(new File(filepath));
    }
}
