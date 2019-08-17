package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class BASE64Decoder extends CharacterDecoder {
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  private static final byte[] pem_convert_array = new byte[256];
  
  byte[] decode_buffer = new byte[4];
  
  protected int bytesPerAtom() { return 4; }
  
  protected int bytesPerLine() { return 72; }
  
  protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt) throws IOException {
    byte b1 = -1;
    byte b2 = -1;
    byte b3 = -1;
    byte b4 = -1;
    if (paramInt < 2)
      throw new CEFormatException("BASE64Decoder: Not enough bytes for an atom."); 
    do {
      i = paramPushbackInputStream.read();
      if (i == -1)
        throw new CEStreamExhausted(); 
    } while (i == 10 || i == 13);
    this.decode_buffer[0] = (byte)i;
    int i = readFully(paramPushbackInputStream, this.decode_buffer, 1, paramInt - 1);
    if (i == -1)
      throw new CEStreamExhausted(); 
    if (paramInt > 3 && this.decode_buffer[3] == 61)
      paramInt = 3; 
    if (paramInt > 2 && this.decode_buffer[2] == 61)
      paramInt = 2; 
    switch (paramInt) {
      case 4:
        b4 = pem_convert_array[this.decode_buffer[3] & 0xFF];
      case 3:
        b3 = pem_convert_array[this.decode_buffer[2] & 0xFF];
      case 2:
        b2 = pem_convert_array[this.decode_buffer[1] & 0xFF];
        b1 = pem_convert_array[this.decode_buffer[0] & 0xFF];
        break;
    } 
    switch (paramInt) {
      case 2:
        paramOutputStream.write((byte)(b1 << 2 & 0xFC | b2 >>> 4 & 0x3));
        break;
      case 3:
        paramOutputStream.write((byte)(b1 << 2 & 0xFC | b2 >>> 4 & 0x3));
        paramOutputStream.write((byte)(b2 << 4 & 0xF0 | b3 >>> 2 & 0xF));
        break;
      case 4:
        paramOutputStream.write((byte)(b1 << 2 & 0xFC | b2 >>> 4 & 0x3));
        paramOutputStream.write((byte)(b2 << 4 & 0xF0 | b3 >>> 2 & 0xF));
        paramOutputStream.write((byte)(b3 << 6 & 0xC0 | b4 & 0x3F));
        break;
    } 
  }
  
  static  {
    byte b;
    for (b = 0; b < 'ÿ'; b++)
      pem_convert_array[b] = -1; 
    for (b = 0; b < pem_array.length; b++)
      pem_convert_array[pem_array[b]] = (byte)b; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\BASE64Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */