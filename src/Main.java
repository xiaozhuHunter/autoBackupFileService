import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.hopxz.autobackup.server.common.utils.SQLUtils;
import org.hopxz.autobackup.server.message.xmlUtils.PackerXML;
import org.hopxz.autobackup.server.message.xmlUtils.ParserXML;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        /*SQLUtils sqlUtils = new SQLUtils();
        System.out.println(sqlUtils.getResultBySelect(
                "*",
                "nodeal_file_list",
                "filepath||devFromWhere||'/'||filename = '/home/filebackup/testDevice/test1'"));*/
        String xmlStr2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<server>\n" +
                "\t<comm_head>\n" +
                "\t\t<loginid>loginid</loginid>\n" +
                "\t\t<passwd>passwd</passwd>\n" +
                "\t\t<msgtype>filesList</msgtype>\n" +
                "\t\t<deviceid>deviceid</deviceid>\n" +
                "\t</comm_head>\n" +
                "\t<body>\n" +
                //"\t\t<test1>17238UJAHHG</test1>\n" +
                "\t\t<array>\n" +
                "\t\t\t<filelist>\n" +
                "\t\t\t\t<fileName>fileName1</fileName>\n" +
                "\t\t\t\t<md5Str>md5Str1</md5Str>\n" +
                "\t\t\t</filelist>\n" +
                "\t\t\t<filelist>\n" +
                "\t\t\t\t<fileName>fileName2</fileName>\n" +
                "\t\t\t\t<md5Str>md5Str2</md5Str>\n" +
                "\t\t\t</filelist>\n" +
                "\t\t</array>\n" +
                //"\t\t<test2>gyfuyf098765</test2>\n" +
                "\t</body>\n" +
                "</server>";
        String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<server>\n" +
                "\t<comm_head>\n" +
                "\t\t<loginid>loginid</loginid>\n" +
                "\t\t<passwd>passwd</passwd>\n" +
                "\t\t<msgtype>fileTrans</msgtype>\n" +
                "\t\t<deviceid>deviceid</deviceid>\n" +
                "\t</comm_head>\n" +
                "\t<body>\n" +
                "\t\t<fileName>filename</fileName>\n" +
                "\t\t<tempFileNum>tempFileNum</tempFileNum>\n" +
                "\t\t<tempFileName>tempFileName</tempFileName>\n" +
                "\t\t<fileContent>fileContent</fileContent>\n" +
                "\t</body>\n" +
                "</server>";
        ParserXML parserXML = new ParserXML();
        HashMap<String,Object> hashMap = parserXML.parser(xmlStr);
        Set<String> keyset = hashMap.keySet();
        for(int i = 0;i<hashMap.size();i++){
            System.out.println(keyset.toArray()[i]+"--"+hashMap.get(keyset.toArray()[i]));
        }
        PackerXML packerXML = new PackerXML();
        System.out.println(packerXML.packer(hashMap));

    }
}