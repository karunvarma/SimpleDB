package log;

import file.BlockId;
import file.FileMgr;
import file.Page;

import java.util.Iterator;

public class LogIterator implements Iterator<byte[]> {

    private  FileMgr fileMgr;
    private  BlockId blockId;
    private Page page;
    private int currentPos;
    private int boundary;

    public LogIterator(FileMgr fileMgr, BlockId blockId) {
        this.fileMgr = fileMgr;
        this.blockId = blockId;
        byte[] b = new byte[fileMgr.getBlockSize()];
        // attach the block to the page
        page = new Page(b);
        getBlockFromDisk(blockId);
    }


    private void getBlockFromDisk(BlockId blockId) {
        // load the block into the page
        fileMgr.read(blockId,page);

        boundary = page.getInt(0);

//        System.out.println("boundary"+boundary);
        currentPos = boundary;
    }

    @Override
    public boolean hasNext() {
//        System.out.println(blockId.getBlockNum()+" - "+currentPos);
        return blockId.getBlockNum() > 0
        || currentPos < fileMgr.getBlockSize();
    }

    @Override
    public byte[] next() {


//        System.out.println("pos"+currentPos);

        if(currentPos == fileMgr.getBlockSize())
        {
            // prepare to get the next block
            blockId = new BlockId(blockId.getFileName(),blockId.getBlockNum()-1);
            getBlockFromDisk(blockId);
        }


        byte[] logRecord = page.getBytes(currentPos);
        currentPos += Integer.BYTES + logRecord.length;
        return logRecord;
    }
}
