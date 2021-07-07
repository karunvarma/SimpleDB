package log;

import file.BlockId;
import file.FileMgr;
import file.Page;

import java.util.Iterator;


/*
A database engine has a single log Manager which was created
during the process startup
 */
public class LogMgr {


    /*
    Log manager uses FileManager abstraction to
    to read/write to the files
     */
    private FileMgr fileMgr;


    private String logFileName;


    /*
    Log manager maintains the a permanent page to
    maintains the contents of the last log block
     */
    private Page logPage;

    /*
    Lat log page block id
     */
    private BlockId blockId;
    private int latestLSN = 0;
    private int lastSavedLsn = 0;

    /*
    Log manager takes the file manager as a reference
    as it will not directly interact with the file operations
    instead it will ask the file manager to do the job
     */

    public LogMgr(FileMgr fileMgr, String logFileName)
    {
        this.fileMgr = fileMgr;
        this.logFileName = logFileName;

        // File manager reads and writes to files with a specific block size
        // so create a block with that size
        byte[] block = new byte[fileMgr.getBlockSize()];

        // we need to have a page for log record
        // attach the created buffer to that page

        logPage = new Page(block);

        // return the no of blocks within the logs file
        int blockCount = fileMgr.length(logFileName);

        if(blockCount == 0) {
            /*
            If the log file is empty create a new block
            and store the new block into the logPage
             */
            blockId = appendNewBlock();
        }
        else {

            blockId = new BlockId(logFileName,blockCount-1);

            // read the last block into the logPage
            fileMgr.read(blockId,logPage);
        }
    }


    private BlockId appendNewBlock() {

        // ask the fileMge to create a empty block in the file
        BlockId newBlock = fileMgr.append(logFileName);
        // Reset boundary of in memory page
        logPage.setInt(0,fileMgr.getBlockSize());
        // write the page into the file
        fileMgr.write(newBlock,logPage);
        return newBlock;
    }

    /**
     * THis method appends the a record to the log and returns an integer
     * @param rec is arbitarly sized byte array it save the array in the log file but has
     *            no idea what its contents are.
     * @return it returns an identifier called LSN(log sequence number)
     */
    public synchronized int append(byte[] logRecord) {

        /*
        here the appending takes place from right to left
         */
        int boundary = logPage.getInt(0);
        int recSize = logRecord.length;
        int bytesneeded = recSize+Integer.BYTES;

        // record doest not fit inside the page;
        if(boundary - bytesneeded<Integer.BYTES)
        {
            // put the page back to the disk
            flush();
            blockId = appendNewBlock();
            boundary = logPage.getInt(0);
        }

        int recordPos =  boundary - bytesneeded;

        // first it stores length(buffer) + buffer itself
        logPage.setBytes(recordPos,logRecord);
        logPage.setInt(0,recordPos); // update the new boundary
        latestLSN+=1;
        return latestLSN;
    }

    /**
     * A client can force a specific record to disk by calling the method flush
     * this method ensures that all log records <=lsn are written to the disk
     * @param lsn
     */
    public void flush(int lsn)
    {
        // write the in memory page back to the disk
        if(lsn >= lastSavedLsn)
            fileMgr.write(blockId,logPage);
    }

    public void flush()
    {
        lastSavedLsn = latestLSN;
        fileMgr.write(blockId,logPage);
    }

    public Iterator<byte[]> iterator()
    {
        flush();
        return new LogIterator(fileMgr,blockId);
    }
}
