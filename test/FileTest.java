import file.BlockId;
import file.FileMgr;
import file.Page;
import org.junit.Assert;
import org.junit.Test;
import server.SimpleDB;

import static org.junit.Assert.assertEquals;

public class FileTest {

    @Test
    public  void TestFileManger()
    {
        // create a DB with directory as testDir
        // and data db is file divided into blocks for size 400 bytes

        SimpleDB simpleDB = new SimpleDB("testDir",400);

        FileMgr fileMgr = simpleDB.getFileMgr();


        // this block ID is related to testfile
        BlockId blockId = new BlockId("testfile",2);

        Page page1 = new Page(fileMgr.getBlockSize());

        int pos1 = 88;
        String str = "karun";
        page1.setString(pos1,str);

        // size is the encode the size
        int size = page1.maxLength(str.length());
        int pos2 = pos1 + size;

        page1.setInt(pos2,346);

        // write to the file
        fileMgr.write(blockId,page1);


        Page page2 = new Page(fileMgr.getBlockSize());

        // read the block id postion to page
        fileMgr.read(blockId,page2);

        assertEquals(346,page2.getInt(pos2));
        assertEquals("karun",page2.getString(pos1));


    }

}


