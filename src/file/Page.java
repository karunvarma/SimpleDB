package file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Page object holds the contents of a disk block
 */
public class Page {
    private ByteBuffer byteBuffer;
    public static final Charset CHARSET = StandardCharsets.US_ASCII;

    /**
     *  create a page that gets memory from the operating system
     *  I/O buffer.
     *
     * @param blockSize
     */
    public Page(int blockSize) {
        byteBuffer = ByteBuffer.allocateDirect(blockSize);
    }

    /**
     * creates a page that gets its
     * memory from a Java array; this constructor is used primarily by the log manage
     * @param bytes
     */
    public Page(byte[] bytes)
    {
        byteBuffer = ByteBuffer.wrap(bytes);
    }

    public void setBytes(int offSet, byte[] val)
    {
        // move the the offset
        byteBuffer.position(offSet);
        byteBuffer.putInt(val.length);
        byteBuffer.put(val);
    }

    public void setString(int offSet, String string)
    {
        /*
        The conversion between string and its byte representation is
        determined by the character encoding
         */
        byte[] bytes = string.getBytes(CHARSET);
        setBytes(offSet,bytes);
    }

    public void setInt(int offSet, int n)
    {
        byteBuffer.putInt(offSet,n);
    }

    public String getString(int offSet)
    {
        byte[] bytes = getBytes(offSet);
        return new String(bytes,CHARSET);
    }

    public byte[] getBytes(int offSet)
    {
        byteBuffer.position(offSet);
        int len = byteBuffer.getInt();
        byte[] bytes = new byte[len];
        byteBuffer.get(bytes);
        return bytes;
    }

    public int getInt(int offset)
    {
        return byteBuffer.getInt(offset);
    }

    ByteBuffer content()
    {
        // reset the position of the file pointer
        byteBuffer.position(0);
        return byteBuffer;
    }


    public static int maxLength(int strLen)
    {
        float bytesPerChar =  CHARSET.newEncoder().maxBytesPerChar();
        return Integer.BYTES + strLen * (int)bytesPerChar;
    }
}
