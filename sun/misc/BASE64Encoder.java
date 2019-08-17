package sun.misc;

import java.io.IOException;
import java.io.OutputStream;

public class BASE64Encoder extends CharacterEncoder {
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  protected int bytesPerAtom() { return 3; }
  
  protected int bytesPerLine() { return 57; }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 == 1) {
      byte b = paramArrayOfByte[paramInt1];
      boolean bool1 = false;
      boolean bool2 = false;
      paramOutputStream.write(pem_array[b >>> 2 & 0x3F]);
      paramOutputStream.write(pem_array[(b << 4 & 0x30) + (bool1 >>> 4 & 0xF)]);
      paramOutputStream.write(61);
      paramOutputStream.write(61);
    } else if (paramInt2 == 2) {
      byte b1 = paramArrayOfByte[paramInt1];
      byte b2 = paramArrayOfByte[paramInt1 + 1];
      boolean bool = false;
      paramOutputStream.write(pem_array[b1 >>> 2 & 0x3F]);
      paramOutputStream.write(pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)]);
      paramOutputStream.write(pem_array[(b2 << 2 & 0x3C) + (bool >>> 6 & 0x3)]);
      paramOutputStream.write(61);
    } else {
      byte b1 = paramArrayOfByte[paramInt1];
      byte b2 = paramArrayOfByte[paramInt1 + 1];
      byte b3 = paramArrayOfByte[paramInt1 + 2];
      paramOutputStream.write(pem_array[b1 >>> 2 & 0x3F]);
      paramOutputStream.write(pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)]);
      paramOutputStream.write(pem_array[(b2 << 2 & 0x3C) + (b3 >>> 6 & 0x3)]);
      paramOutputStream.write(pem_array[b3 & 0x3F]);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\BASE64Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */