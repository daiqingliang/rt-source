package sun.misc;

import java.io.IOException;
import java.io.OutputStream;

public class UCEncoder extends CharacterEncoder {
  private static final byte[] map_array = { 
      48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
      75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
      85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
      111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
      121, 122, 40, 41 };
  
  private int sequence;
  
  private byte[] tmp = new byte[2];
  
  private CRC16 crc = new CRC16();
  
  protected int bytesPerAtom() { return 2; }
  
  protected int bytesPerLine() { return 48; }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    byte b5;
    byte b4 = paramArrayOfByte[paramInt1];
    if (paramInt2 == 2) {
      b5 = paramArrayOfByte[paramInt1 + 1];
    } else {
      b5 = 0;
    } 
    this.crc.update(b4);
    if (paramInt2 == 2)
      this.crc.update(b5); 
    paramOutputStream.write(map_array[(b4 >>> 2 & 0x38) + (b5 >>> 5 & 0x7)]);
    byte b2 = 0;
    byte b3 = 0;
    byte b1;
    for (b1 = 1; b1 < 256; b1 *= 2) {
      if ((b4 & b1) != 0)
        b2++; 
      if ((b5 & b1) != 0)
        b3++; 
    } 
    b2 = (b2 & true) * 32;
    b3 = (b3 & true) * 32;
    paramOutputStream.write(map_array[(b4 & 0x1F) + b2]);
    paramOutputStream.write(map_array[(b5 & 0x1F) + b3]);
  }
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt) throws IOException {
    paramOutputStream.write(42);
    this.crc.value = 0;
    this.tmp[0] = (byte)paramInt;
    this.tmp[1] = (byte)this.sequence;
    this.sequence = this.sequence + 1 & 0xFF;
    encodeAtom(paramOutputStream, this.tmp, 0, 2);
  }
  
  protected void encodeLineSuffix(OutputStream paramOutputStream) throws IOException {
    this.tmp[0] = (byte)(this.crc.value >>> 8 & 0xFF);
    this.tmp[1] = (byte)(this.crc.value & 0xFF);
    encodeAtom(paramOutputStream, this.tmp, 0, 2);
    this.pStream.println();
  }
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream) throws IOException {
    this.sequence = 0;
    super.encodeBufferPrefix(paramOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\UCEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */