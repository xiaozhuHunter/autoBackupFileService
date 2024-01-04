import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {
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
        String xmlStr4 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<server>\n" +
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
                "</server>";;
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
        String xmlStr5 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<URIAction>\n" +
                "<URIINFO>"+
                "<array>\n" +
                "<URIActionList>\n" +
                "<URI_Path>/login</URI_Path>\n" +
                "<Method_Type>Sheet</Method_Type>\n" +
                "<Method_Path>src/org/hopxz/autobackup/server/manage/webUI/index.html</Method_Path>\n" +
                "</URIActionList>\n" +
                "<URIActionList>\n" +
                "<URI_Path>/checkLoginInfo</URI_Path>\n" +
                "<Method_Type>Function</Method_Type>\n" +
                "<Method_Path>org.hopxz.autobackup.server.manage.webApp.application.Login</Method_Path>\n" +
                "</URIActionList>\n" +
                "</array>\n" +
                "</URIINFO>"+
                "</URIAction>";
        String msgtypStr = xmlStr.substring(xmlStr.indexOf("<msgtype>")+9,xmlStr.indexOf("</msgtype>"));
        System.out.println(msgtypStr);
        /*String xmlStr3= ReadFileUtils.fileContextStr("src/org/hopxz/autobackup/server/manage/webApp/cfgFile/HTTPConn.xml");
        xmlStr5 = xmlStr5.replace("\n","").replace("\t","");
        System.out.println(xmlStr3.contains("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
        ParserXML parserXML = new ParserXML();
        HashMap<String,Object> hashMap = parserXML.parser(xmlStr3);
        Set<String> keyset = hashMap.keySet();
        for(int i = 0;i<hashMap.size();i++){
            System.out.println(keyset.toArray()[i]+"--"+hashMap.get(keyset.toArray()[i]));
        }
        /*PackerXML packerXML = new PackerXML();
        System.out.println(packerXML.packer(hashMap));*/
    }
}