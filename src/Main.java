import org.hopxz.autobackup.server.common.utils.SQLUtils;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        SQLUtils sqlUtils = new SQLUtils();
        System.out.println(sqlUtils.getResultBySelect(
                "*",
                "cfg_msg_list",
                "1=1"));
    }
}