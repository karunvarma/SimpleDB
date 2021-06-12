package file;


import java.util.Objects;

/**
 * Block Id object identifies a specific block by its file name
 * and blocknum.
 */
public class BlockId {

    private final String fileName;
    private final int blockNum;


    public BlockId(String fileName, int blockNum) {
        this.fileName = fileName;
        this.blockNum = blockNum;
    }

    public String getFileName() {
        return fileName;
    }

    public int getBlockNum() {
        return blockNum;
    }

    // equals and hascode ??

    public boolean equals(Object obj) {
        BlockId blk = (BlockId) obj;
        return fileName.equals(blk.fileName) && blockNum == blk.blockNum;
    }

    public String toString() {
        return "[file " + fileName + ", block " + blockNum + "]";
    }

    public int hashCode() {
        return toString().hashCode();
    }
}
