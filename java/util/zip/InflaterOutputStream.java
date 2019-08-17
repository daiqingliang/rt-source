package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class InflaterOutputStream extends FilterOutputStream {
  protected final Inflater inf;
  
  protected final byte[] buf;
  
  private final byte[] wbuf = new byte[1];
  
  private boolean usesDefaultInflater = false;
  
  private boolean closed = false;
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
  
  public InflaterOutputStream(OutputStream paramOutputStream) {
    this(paramOutputStream, new Inflater());
    this.usesDefaultInflater = true;
  }
  
  public InflaterOutputStream(OutputStream paramOutputStream, Inflater paramInflater) { this(paramOutputStream, paramInflater, 512); }
  
  public InflaterOutputStream(OutputStream paramOutputStream, Inflater paramInflater, int paramInt) {
    super(paramOutputStream);
    if (paramOutputStream == null)
      throw new NullPointerException("Null output"); 
    if (paramInflater == null)
      throw new NullPointerException("Null inflater"); 
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size < 1"); 
    this.inf = paramInflater;
    this.buf = new byte[paramInt];
  }
  
  public void close() throws IOException {
    if (!this.closed)
      try {
        finish();
      } finally {
        this.out.close();
        this.closed = true;
      }  
  }
  
  public void flush() throws IOException {
    ensureOpen();
    if (!this.inf.finished())
      try {
        while (!this.inf.finished() && !this.inf.needsInput()) {
          int i = this.inf.inflate(this.buf, 0, this.buf.length);
          if (i < 1)
            break; 
          this.out.write(this.buf, 0, i);
        } 
        super.flush();
      } catch (DataFormatException dataFormatException) {
        String str = dataFormatException.getMessage();
        if (str == null)
          str = "Invalid ZLIB data format"; 
        throw new ZipException(str);
      }  
  }
  
  public void finish() throws IOException {
    ensureOpen();
    flush();
    if (this.usesDefaultInflater)
      this.inf.end(); 
  }
  
  public void write(int paramInt) throws IOException {
    this.wbuf[0] = (byte)paramInt;
    write(this.wbuf, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramArrayOfByte == null)
      throw new NullPointerException("Null buffer for read"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    try {
      while (true) {
        int i;
        if (this.inf.needsInput()) {
          if (paramInt2 < 1)
            break; 
          int j = (paramInt2 < 512) ? paramInt2 : 512;
          this.inf.setInput(paramArrayOfByte, paramInt1, j);
          paramInt1 += j;
          paramInt2 -= j;
        } 
        do {
          i = this.inf.inflate(this.buf, 0, this.buf.length);
          if (i <= 0)
            continue; 
          this.out.write(this.buf, 0, i);
        } while (i > 0);
        if (this.inf.finished())
          break; 
        if (this.inf.needsDictionary())
          throw new ZipException("ZLIB dictionary missing"); 
      } 
    } catch (DataFormatException dataFormatException) {
      String str = dataFormatException.getMessage();
      if (str == null)
        str = "Invalid ZLIB data format"; 
      throw new ZipException(str);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\InflaterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */