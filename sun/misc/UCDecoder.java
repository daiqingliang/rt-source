package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UCDecoder extends CharacterDecoder {
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
  
  private ByteArrayOutputStream lineAndSeq = new ByteArrayOutputStream(2);
  
  protected int bytesPerAtom() { return 2; }
  
  protected int bytesPerLine() { return 48; }
  
  protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt) throws IOException {
    byte b5 = -1;
    byte b6 = -1;
    byte b7 = -1;
    byte[] arrayOfByte = new byte[3];
    int i = paramPushbackInputStream.read(arrayOfByte);
    if (i != 3)
      throw new CEStreamExhausted(); 
    for (i = 0; i < 64 && (b5 == -1 || b6 == -1 || b7 == -1); i++) {
      if (arrayOfByte[0] == map_array[i])
        b5 = (byte)i; 
      if (arrayOfByte[1] == map_array[i])
        b6 = (byte)i; 
      if (arrayOfByte[2] == map_array[i])
        b7 = (byte)i; 
    } 
    byte b8 = (byte)(((b5 & 0x38) << 2) + (b6 & 0x1F));
    byte b9 = (byte)(((b5 & 0x7) << 5) + (b7 & 0x1F));
    byte b1 = 0;
    byte b2 = 0;
    for (i = 1; i < 256; i *= 2) {
      if ((b8 & i) != 0)
        b1++; 
      if ((b9 & i) != 0)
        b2++; 
    } 
    byte b3 = (b6 & 0x20) / 32;
    byte b4 = (b7 & 0x20) / 32;
    if ((b1 & true) != b3)
      throw new CEFormatException("UCDecoder: High byte parity error."); 
    if ((b2 & true) != b4)
      throw new CEFormatException("UCDecoder: Low byte parity error."); 
    paramOutputStream.write(b8);
    this.crc.update(b8);
    if (paramInt == 2) {
      paramOutputStream.write(b9);
      this.crc.update(b9);
    } 
  }
  
  protected void decodeBufferPrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) { this.sequence = 0; }
  
  protected int decodeLinePrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {
    this.crc.value = 0;
    do {
      int i = paramPushbackInputStream.read(this.tmp, 0, 1);
      if (i == -1)
        throw new CEStreamExhausted(); 
    } while (this.tmp[0] != 42);
    this.lineAndSeq.reset();
    decodeAtom(paramPushbackInputStream, this.lineAndSeq, 2);
    byte[] arrayOfByte = this.lineAndSeq.toByteArray();
    byte b1 = arrayOfByte[0] & 0xFF;
    byte b2 = arrayOfByte[1] & 0xFF;
    if (b2 != this.sequence)
      throw new CEFormatException("UCDecoder: Out of sequence line."); 
    this.sequence = this.sequence + 1 & 0xFF;
    return b1;
  }
  
  protected void decodeLineSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) {
    int i = this.crc.value;
    this.lineAndSeq.reset();
    decodeAtom(paramPushbackInputStream, this.lineAndSeq, 2);
    byte[] arrayOfByte = this.lineAndSeq.toByteArray();
    byte b = (arrayOfByte[0] << 8 & 0xFF00) + (arrayOfByte[1] & 0xFF);
    if (b != i)
      throw new CEFormatException("UCDecoder: CRC check failed."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\UCDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */