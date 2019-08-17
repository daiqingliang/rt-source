package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;

public class GZIPOutputStream extends DeflaterOutputStream {
  protected CRC32 crc = new CRC32();
  
  private static final int GZIP_MAGIC = 35615;
  
  private static final int TRAILER_SIZE = 8;
  
  public GZIPOutputStream(OutputStream paramOutputStream, int paramInt) throws IOException { this(paramOutputStream, paramInt, false); }
  
  public GZIPOutputStream(OutputStream paramOutputStream, int paramInt, boolean paramBoolean) throws IOException {
    super(paramOutputStream, new Deflater(-1, true), paramInt, paramBoolean);
    writeHeader();
    this.crc.reset();
  }
  
  public GZIPOutputStream(OutputStream paramOutputStream) throws IOException { this(paramOutputStream, 512, false); }
  
  public GZIPOutputStream(OutputStream paramOutputStream, boolean paramBoolean) throws IOException { this(paramOutputStream, 512, paramBoolean); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void finish() throws IOException {
    if (!this.def.finished()) {
      this.def.finish();
      while (!this.def.finished()) {
        int i = this.def.deflate(this.buf, 0, this.buf.length);
        if (this.def.finished() && i <= this.buf.length - 8) {
          writeTrailer(this.buf, i);
          i += 8;
          this.out.write(this.buf, 0, i);
          return;
        } 
        if (i > 0)
          this.out.write(this.buf, 0, i); 
      } 
      byte[] arrayOfByte = new byte[8];
      writeTrailer(arrayOfByte, 0);
      this.out.write(arrayOfByte);
    } 
  }
  
  private void writeHeader() throws IOException { this.out.write(new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 }); }
  
  private void writeTrailer(byte[] paramArrayOfByte, int paramInt) throws IOException {
    writeInt((int)this.crc.getValue(), paramArrayOfByte, paramInt);
    writeInt(this.def.getTotalIn(), paramArrayOfByte, paramInt + 4);
  }
  
  private void writeInt(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws IOException {
    writeShort(paramInt1 & 0xFFFF, paramArrayOfByte, paramInt2);
    writeShort(paramInt1 >> 16 & 0xFFFF, paramArrayOfByte, paramInt2 + 2);
  }
  
  private void writeShort(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws IOException {
    paramArrayOfByte[paramInt2] = (byte)(paramInt1 & 0xFF);
    paramArrayOfByte[paramInt2 + 1] = (byte)(paramInt1 >> 8 & 0xFF);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\GZIPOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */