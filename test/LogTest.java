import com.sun.tools.jconsole.JConsoleContext;
import file.Page;
import log.LogMgr;
import org.junit.Test;
import server.SimpleDB;

import java.util.Iterator;

public class LogTest {


    private static LogMgr logMgr;

    @Test
    public void testLogManger()
    {
        SimpleDB simpleDB = new SimpleDB("logtestDir",400);
        logMgr = simpleDB.getLogMgr();
        createRecords(1,35);
        printLogRecords();

        createRecords(36,70);
        logMgr.flush(65);
        printLogRecords();
    }

    private void createRecords(int start, int end) {
        for(int i=start;i<=end;i++) {
            byte[] rec = createLogRecord("record"+i,i+100);;
            // once the buffer is created
            // ask the logManager to append it to the file.
            int lsn = logMgr.append(rec);
            System.out.println("LSN"+lsn);
        }
    }

    private static byte[] createLogRecord(String s,int n){
        int npos = Page.maxLength(s.length());
        byte[] b = new byte[npos+Integer.BYTES];

        //Here we are using the page wrapper
        //to write the data to the buffer.
        Page p = new Page(b);
        p.setString(0,s);
        p.setInt(npos,n);
        // stringN+ number;
        return b;
    }

    public  static void printLogRecords()
    {
//        System.out.println("print log records");
        Iterator<byte[]> iterator = logMgr.iterator();
        while(iterator.hasNext())
        {
            byte[] rec = iterator.next();
            Page p = new Page(rec);
            String s = p.getString(0);
            int npos = Page.maxLength(s.length());
            int val = p.getInt(npos);
            System.out.println("["+s+", "+val+"]");
        }
        System.out.println();
    }

}
