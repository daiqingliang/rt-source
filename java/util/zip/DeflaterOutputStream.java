package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DeflaterOutputStream extends FilterOutputStream {
  protected Deflater def;
  
  protected byte[] buf;
  
  private boolean closed = false;
  
  private final boolean syncFlush;
  
  boolean usesDefaultDeflater = false;
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, int paramInt, boolean paramBoolean) {
    super(paramOutputStream);
    if (paramOutputStream == null || paramDeflater == null)
      throw new NullPointerException(); 
    if (paramInt <= 0)
      throw new IllegalArgumentException("buffer size <= 0"); 
    this.def = paramDeflater;
    this.buf = new byte[paramInt];
    this.syncFlush = paramBoolean;
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, int paramInt) { this(paramOutputStream, paramDeflater, paramInt, false); }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, boolean paramBoolean) { this(paramOutputStream, paramDeflater, 512, paramBoolean); }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater) { this(paramOutputStream, paramDeflater, 512, false); }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, boolean paramBoolean) {
    this(paramOutputStream, new Deflater(), 512, paramBoolean);
    this.usesDefaultDeflater = true;
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream) {
    this(paramOutputStream, false);
    this.usesDefaultDeflater = true;
  }
  
  public void write(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = (byte)(paramInt & 0xFF);
    write(arrayOfByte, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.def.finished())
      throw new IOException("write beyond end of stream"); 
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfByte.length - paramInt1 + paramInt2) < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    if (!this.def.finished()) {
      this.def.setInput(paramArrayOfByte, paramInt1, paramInt2);
      while (!this.def.needsInput())
        deflate(); 
    } 
  }
  
  public void finish() throws IOException {
    if (!this.def.finished()) {
      this.def.finish();
      while (!this.def.finished())
        deflate(); 
    } 
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      finish();
      if (this.usesDefaultDeflater)
        this.def.end(); 
      this.out.close();
      this.closed = true;
    } 
  }
  
  protected void deflate() throws IOException {
    int i = this.def.deflate(this.buf, 0, this.buf.length);
    if (i > 0)
      this.out.write(this.buf, 0, i); 
  }
  
  public void flush() throws IOException {
    if (this.syncFlush && !this.def.finished()) {
      int i = 0;
      while ((i = this.def.deflate(this.buf, 0, this.buf.length, 2)) > 0) {
        this.out.write(this.buf, 0, i);
        if (i < this.buf.length)
          break; 
      } 
    } 
    this.out.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\DeflaterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */