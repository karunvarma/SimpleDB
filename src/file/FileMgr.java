package file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * FileMgr is a class that handles the actual interaction with the
 * OS file system.
 *
 * Its primary job is to implement methods that read and write pages to
 * the disk blocks.
 *
 * Singleton object
 *
 */
public class FileMgr {

    // name of the folder that contains files for the database
    private final File dbDirectory;
    private final int blockSize;
    private boolean isNew;

    // design pattern ??
    private Map<String, RandomAccessFile> openFiles = new HashMap<>();

    public FileMgr(File dbDirectory, int blockSize) {
        this.dbDirectory = dbDirectory;
        this.blockSize = blockSize;

        isNew = !dbDirectory.exists();


        if(isNew)
        {
            // create a directory
            dbDirectory.mkdir();
        }

        // delete a left over temporary tables
        for(String fileName: dbDirectory.list())
        {
            if(fileName.startsWith("temp"))
            {
                System.out.println("Deleting the file "+dbDirectory+" "+fileName);
                new File(dbDirectory,fileName).delete();
            }
        }

    }

    /**
     * reads the contents of the specified block into the page
     * or update the page.
     * @param blockId
     * @param page
     */
    public synchronized void read(BlockId blockId, Page page)
    {
        try
        {
            RandomAccessFile randomAccessFile = getFile(blockId.getFileName());
            randomAccessFile.seek(blockId.getBlockNum() * blockSize);
            randomAccessFile.getChannel().read(page.content());

        }  catch (IOException e) {
            throw  new RuntimeException("cannot read block"+blockId);
        }
    }



    /**
     * Writing the contents of the page into the specified block.
     * @param blockId
     * @param page
     */
    public synchronized void write(BlockId blockId, Page page)
    {
        try {

            RandomAccessFile randomAccessFile = getFile(blockId.getFileName());
            randomAccessFile.seek(blockId.getBlockNum() * blockSize);
            randomAccessFile.getChannel().write(page.content());

        } catch (IOException e) {
            throw new RuntimeException("Cannot write a block"+blockId);
        }

    }


    private RandomAccessFile getFile(String fileName) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = openFiles.get(fileName);

        // if the file ref is present, create one
        if(randomAccessFile == null)
        {
            // create a file in directory with filename as (fileName)
            File dbTable = new File(dbDirectory,fileName);

            // rws ??
            randomAccessFile = new RandomAccessFile(dbTable,"rws");
            openFiles.put(fileName,randomAccessFile);
        }
        return randomAccessFile;
    }

    /**
     * It basically writes a empty buffer into the page
     * @param filename on whih the operatons are performed
     * @return the block number of the new created buffer
     */
    public synchronized BlockId append(String filename)
    {
        int newBlockNumber = length(filename);
        System.out.println("fasak"+newBlockNumber);
        BlockId blockId = new BlockId(filename,newBlockNumber);
        byte[] b = new byte[blockSize];
        try{
            RandomAccessFile f = getFile(filename);
            // move the file pointer to the new starting location
            f.seek(blockSize*blockId.getBlockNum());
            f.write(b);

        } catch (IOException e) {
            throw new RuntimeException("Cannot append a block");
        }
        return blockId;
    }

    public boolean isNew()
    {
        return isNew;
    }


    /**
     * Returns the no of blocks inside the given the filename
     * @param fileName
     * @return block count
     */
    public int length(String fileName)
    {
        try
        {
            RandomAccessFile randomAccessFile = getFile(fileName);
            return (int)(randomAccessFile.length()/blockSize);
        }
        catch(IOException e)
        {
            throw new RuntimeException("Cannot acess "+ fileName);
        }
    }

    public int getBlockSize() {
        return blockSize;
    }
}
