package java.util.zip;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class GZIPInputStream extends InflaterInputStream {
  protected CRC32 crc = new CRC32();
  
  protected boolean eos;
  
  private boolean closed = false;
  
  public static final int GZIP_MAGIC = 35615;
  
  private static final int FTEXT = 1;
  
  private static final int FHCRC = 2;
  
  private static final int FEXTRA = 4;
  
  private static final int FNAME = 8;
  
  private static final int FCOMMENT = 16;
  
  private byte[] tmpbuf = new byte[128];
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
  
  public GZIPInputStream(InputStream paramInputStream, int paramInt) throws IOException {
    super(paramInputStream, new Inflater(true), paramInt);
    readHeader(paramInputStream);
  }
  
  public GZIPInputStream(InputStream paramInputStream) throws IOException { this(paramInputStream, 512); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (this.eos)
      return -1; 
    int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1) {
      if (readTrailer()) {
        this.eos = true;
      } else {
        return read(paramArrayOfByte, paramInt1, paramInt2);
      } 
    } else {
      this.crc.update(paramArrayOfByte, paramInt1, i);
    } 
    return i;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      super.close();
      this.eos = true;
      this.closed = true;
    } 
  }
  
  private int readHeader(InputStream paramInputStream) throws IOException {
    CheckedInputStream checkedInputStream = new CheckedInputStream(paramInputStream, this.crc);
    this.crc.reset();
    if (readUShort(checkedInputStream) != 35615)
      throw new ZipException("Not in GZIP format"); 
    if (readUByte(checkedInputStream) != 8)
      throw new ZipException("Unsupported compression method"); 
    int i = readUByte(checkedInputStream);
    skipBytes(checkedInputStream, 6);
    int j = 10;
    if ((i & 0x4) == 4) {
      int k = readUShort(checkedInputStream);
      skipBytes(checkedInputStream, k);
      j += k + 2;
    } 
    if ((i & 0x8) == 8)
      do {
        j++;
      } while (readUByte(checkedInputStream) != 0); 
    if ((i & 0x10) == 16)
      do {
        j++;
      } while (readUByte(checkedInputStream) != 0); 
    if ((i & 0x2) == 2) {
      int k = (int)this.crc.getValue() & 0xFFFF;
      if (readUShort(checkedInputStream) != k)
        throw new ZipException("Corrupt GZIP header"); 
      j += 2;
    } 
    this.crc.reset();
    return j;
  }
  
  private boolean readTrailer() throws IOException {
    InputStream inputStream = this.in;
    int i = this.inf.getRemaining();
    if (i > 0)
      inputStream = new SequenceInputStream(new ByteArrayInputStream(this.buf, this.len - i, i), new FilterInputStream(this, inputStream) {
            public void close() throws IOException {}
          }); 
    if (readUInt(inputStream) != this.crc.getValue() || readUInt(inputStream) != (this.inf.getBytesWritten() & 0xFFFFFFFFL))
      throw new ZipException("Corrupt GZIP trailer"); 
    if (this.in.available() > 0 || i > 26) {
      int j = 8;
      try {
        j += readHeader(inputStream);
      } catch (IOException iOException) {
        return true;
      } 
      this.inf.reset();
      if (i > j)
        this.inf.setInput(this.buf, this.len - i + j, i - j); 
      return false;
    } 
    return true;
  }
  
  private long readUInt(InputStream paramInputStream) throws IOException {
    long l = readUShort(paramInputStream);
    return readUShort(paramInputStream) << 16 | l;
  }
  
  private int readUShort(InputStream paramInputStream) throws IOException {
    int i = readUByte(paramInputStream);
    return readUByte(paramInputStream) << 8 | i;
  }
  
  private int readUByte(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    if (i == -1)
      throw new EOFException(); 
    if (i < -1 || i > 255)
      throw new IOException(this.in.getClass().getName() + ".read() returned value out of range -1..255: " + i); 
    return i;
  }
  
  private void skipBytes(InputStream paramInputStream, int paramInt) throws IOException {
    while (paramInt > 0) {
      int i = paramInputStream.read(this.tmpbuf, 0, (paramInt < this.tmpbuf.length) ? paramInt : this.tmpbuf.length);
      if (i == -1)
        throw new EOFException(); 
      paramInt -= i;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\GZIPInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */