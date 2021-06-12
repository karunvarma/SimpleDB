package server;

import file.FileMgr;

import java.io.File;

public class SimpleDB {

    public static int BLOCK_SIZE = 400;

    // singleton object
    private final FileMgr fileMgr;

    /**
     *
     * @param dirName it is the name of the database directory
     * @param blockSize its is the block size for the page
     */
    public SimpleDB(String dirName, int blockSize)
    {
        File file = new File(dirName);
        this.fileMgr = new FileMgr(file, blockSize);
    }



    public FileMgr getFileMgr() {
        return fileMgr;
    }
}
