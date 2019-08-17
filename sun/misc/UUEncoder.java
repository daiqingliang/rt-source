package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoder extends CharacterEncoder {
  private String bufferName = "encoder.buf";
  
  private int mode = 644;
  
  public UUEncoder() {}
  
  public UUEncoder(String paramString) {}
  
  public UUEncoder(String paramString, int paramInt) {}
  
  protected int bytesPerAtom() { return 3; }
  
  protected int bytesPerLine() { return 45; }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    byte b2 = 1;
    byte b3 = 1;
    byte b1 = paramArrayOfByte[paramInt1];
    if (paramInt2 > 1)
      b2 = paramArrayOfByte[paramInt1 + 1]; 
    if (paramInt2 > 2)
      b3 = paramArrayOfByte[paramInt1 + 2]; 
    byte b4 = b1 >>> 2 & 0x3F;
    byte b5 = b1 << 4 & 0x30 | b2 >>> 4 & 0xF;
    byte b6 = b2 << 2 & 0x3C | b3 >>> 6 & 0x3;
    byte b7 = b3 & 0x3F;
    paramOutputStream.write(b4 + 32);
    paramOutputStream.write(b5 + 32);
    paramOutputStream.write(b6 + 32);
    paramOutputStream.write(b7 + 32);
  }
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt) throws IOException { paramOutputStream.write((paramInt & 0x3F) + 32); }
  
  protected void encodeLineSuffix(OutputStream paramOutputStream) throws IOException { this.pStream.println(); }
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream) throws IOException {
    this.pStream = new PrintStream(paramOutputStream);
    this.pStream.print("begin " + this.mode + " ");
    if (this.bufferName != null) {
      this.pStream.println(this.bufferName);
    } else {
      this.pStream.println("encoder.bin");
    } 
    this.pStream.flush();
  }
  
  protected void encodeBufferSuffix(OutputStream paramOutputStream) throws IOException {
    this.pStream.println(" \nend");
    this.pStream.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\UUEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */