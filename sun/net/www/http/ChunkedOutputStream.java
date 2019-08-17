package sun.net.www.http;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ChunkedOutputStream extends PrintStream {
  static final int DEFAULT_CHUNK_SIZE = 4096;
  
  private static final byte[] CRLF = { 13, 10 };
  
  private static final int CRLF_SIZE = CRLF.length;
  
  private static final byte[] FOOTER = CRLF;
  
  private static final int FOOTER_SIZE = CRLF_SIZE;
  
  private static final byte[] EMPTY_CHUNK_HEADER = getHeader(0);
  
  private static final int EMPTY_CHUNK_HEADER_SIZE = getHeaderSize(0);
  
  private byte[] buf;
  
  private int size;
  
  private int count;
  
  private int spaceInCurrentChunk;
  
  private PrintStream out;
  
  private int preferredChunkDataSize;
  
  private int preferedHeaderSize;
  
  private int preferredChunkGrossSize;
  
  private byte[] completeHeader;
  
  private static int getHeaderSize(int paramInt) { return Integer.toHexString(paramInt).length() + CRLF_SIZE; }
  
  private static byte[] getHeader(int paramInt) {
    try {
      String str = Integer.toHexString(paramInt);
      byte[] arrayOfByte1 = str.getBytes("US-ASCII");
      byte[] arrayOfByte2 = new byte[getHeaderSize(paramInt)];
      for (byte b = 0; b < arrayOfByte1.length; b++)
        arrayOfByte2[b] = arrayOfByte1[b]; 
      arrayOfByte2[arrayOfByte1.length] = CRLF[0];
      arrayOfByte2[arrayOfByte1.length + 1] = CRLF[1];
      return arrayOfByte2;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new InternalError(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
    } 
  }
  
  public ChunkedOutputStream(PrintStream paramPrintStream) { this(paramPrintStream, 4096); }
  
  public ChunkedOutputStream(PrintStream paramPrintStream, int paramInt) {
    super(paramPrintStream);
    this.out = paramPrintStream;
    if (paramInt <= 0)
      paramInt = 4096; 
    if (paramInt > 0) {
      int i = paramInt - getHeaderSize(paramInt) - FOOTER_SIZE;
      if (getHeaderSize(i + 1) < getHeaderSize(paramInt))
        i++; 
      paramInt = i;
    } 
    if (paramInt > 0) {
      this.preferredChunkDataSize = paramInt;
    } else {
      this.preferredChunkDataSize = 4096 - getHeaderSize(4096) - FOOTER_SIZE;
    } 
    this.preferedHeaderSize = getHeaderSize(this.preferredChunkDataSize);
    this.preferredChunkGrossSize = this.preferedHeaderSize + this.preferredChunkDataSize + FOOTER_SIZE;
    this.completeHeader = getHeader(this.preferredChunkDataSize);
    this.buf = new byte[this.preferredChunkGrossSize];
    reset();
  }
  
  private void flush(boolean paramBoolean) {
    if (this.spaceInCurrentChunk == 0) {
      this.out.write(this.buf, 0, this.preferredChunkGrossSize);
      this.out.flush();
      reset();
    } else if (paramBoolean) {
      if (this.size > 0) {
        int i = this.preferedHeaderSize - getHeaderSize(this.size);
        System.arraycopy(getHeader(this.size), 0, this.buf, i, getHeaderSize(this.size));
        this.buf[this.count++] = FOOTER[0];
        this.buf[this.count++] = FOOTER[1];
        this.out.write(this.buf, i, this.count - i);
      } else {
        this.out.write(EMPTY_CHUNK_HEADER, 0, EMPTY_CHUNK_HEADER_SIZE);
      } 
      this.out.flush();
      reset();
    } 
  }
  
  public boolean checkError() { return this.out.checkError(); }
  
  private void ensureOpen() {
    if (this.out == null)
      setError(); 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    ensureOpen();
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    int i = paramInt2;
    int j = paramInt1;
    do {
      if (i >= this.spaceInCurrentChunk) {
        for (byte b = 0; b < this.completeHeader.length; b++)
          this.buf[b] = this.completeHeader[b]; 
        System.arraycopy(paramArrayOfByte, j, this.buf, this.count, this.spaceInCurrentChunk);
        j += this.spaceInCurrentChunk;
        i -= this.spaceInCurrentChunk;
        this.count += this.spaceInCurrentChunk;
        this.buf[this.count++] = FOOTER[0];
        this.buf[this.count++] = FOOTER[1];
        this.spaceInCurrentChunk = 0;
        flush(false);
        if (checkError())
          break; 
      } else {
        System.arraycopy(paramArrayOfByte, j, this.buf, this.count, i);
        this.count += i;
        this.size += i;
        this.spaceInCurrentChunk -= i;
        i = 0;
      } 
    } while (i > 0);
  }
  
  public void write(int paramInt) {
    byte[] arrayOfByte = { (byte)paramInt };
    write(arrayOfByte, 0, 1);
  }
  
  public void reset() {
    this.count = this.preferedHeaderSize;
    this.size = 0;
    this.spaceInCurrentChunk = this.preferredChunkDataSize;
  }
  
  public int size() { return this.size; }
  
  public void close() {
    ensureOpen();
    if (this.size > 0)
      flush(true); 
    flush(true);
    this.out = null;
  }
  
  public void flush() {
    ensureOpen();
    if (this.size > 0)
      flush(true); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\ChunkedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */