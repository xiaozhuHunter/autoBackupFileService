import org.hopxz.autobackup.server.common.utils.SQLUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        SQLUtils sqlUtils = new SQLUtils();
        System.out.println(sqlUtils.getResultBySelect(
                "*",
                "nodeal_file_list",
                "filepath||devFromWhere||'/'||filename = '/home/filebackup/testDevice/test1'"));
    }
}