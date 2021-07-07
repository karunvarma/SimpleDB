package server;

import file.FileMgr;
import log.LogMgr;

import java.io.File;

public class SimpleDB {

    public static int BLOCK_SIZE = 400;
    public static String LOG_FILE = "simpledb.log";

    // singleton object
    private final FileMgr fileMgr;
    private final LogMgr logMgr;

    /**
     *
     * @param directoryName it is the name of the database directory
     * @param blockSize its is the block size for the page
     */
    public SimpleDB(String directoryName, int blockSize)
    {
        File databaseDirectory = new File(directoryName);
        fileMgr = new FileMgr(databaseDirectory, blockSize);
        logMgr = new LogMgr(fileMgr,LOG_FILE);
    }



    public FileMgr getFileMgr() {
        return fileMgr;
    }

    public LogMgr getLogMgr() {
        return logMgr;
    }
}
