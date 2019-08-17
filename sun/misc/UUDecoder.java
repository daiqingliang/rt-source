package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UUDecoder extends CharacterDecoder {
  public String bufferName;
  
  public int mode;
  
  private byte[] decoderBuffer = new byte[4];
  
  protected int bytesPerAtom() { return 3; }
  
  protected int bytesPerLine() { return 45; }
  
  protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < 4; b++) {
      int i = paramPushbackInputStream.read();
      if (i == -1)
        throw new CEStreamExhausted(); 
      stringBuffer.append((char)i);
      this.decoderBuffer[b] = (byte)(i - 32 & 0x3F);
    } 
    byte b1 = this.decoderBuffer[0] << 2 & 0xFC | this.decoderBuffer[1] >>> 4 & 0x3;
    byte b2 = this.decoderBuffer[1] << 4 & 0xF0 | this.decoderBuffer[2] >>> 2 & 0xF;
    byte b3 = this.decoderBuffer[2] << 6 & 0xC0 | this.decoderBuffer[3] & 0x3F;
    paramOutputStream.write((byte)(b1 & 0xFF));
    if (paramInt > 1)
      paramOutputStream.write((byte)(b2 & 0xFF)); 
    if (paramInt > 2)
      paramOutputStream.write((byte)(b3 & 0xFF)); 
  }
  
  protected void decodeBufferPrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {
    int i;
    StringBuffer stringBuffer = new StringBuffer(32);
    boolean bool;
    for (bool = true;; bool = (i == 10 || i == 13) ? 1 : 0) {
      i = paramPushbackInputStream.read();
      if (i == -1)
        throw new CEFormatException("UUDecoder: No begin line."); 
      if (i == 98 && bool) {
        i = paramPushbackInputStream.read();
        if (i == 101)
          break; 
      } 
    } 
    while (i != 10 && i != 13) {
      i = paramPushbackInputStream.read();
      if (i == -1)
        throw new CEFormatException("UUDecoder: No begin line."); 
      if (i != 10 && i != 13)
        stringBuffer.append((char)i); 
    } 
    String str = stringBuffer.toString();
    if (str.indexOf(' ') != 3)
      throw new CEFormatException("UUDecoder: Malformed begin line."); 
    this.mode = Integer.parseInt(str.substring(4, 7));
    this.bufferName = str.substring(str.indexOf(' ', 6) + 1);
    if (i == 13) {
      i = paramPushbackInputStream.read();
      if (i != 10 && i != -1)
        paramPushbackInputStream.unread(i); 
    } 
  }
  
  protected int decodeLinePrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {
    int i = paramPushbackInputStream.read();
    if (i == 32) {
      i = paramPushbackInputStream.read();
      i = paramPushbackInputStream.read();
      if (i != 10 && i != -1)
        paramPushbackInputStream.unread(i); 
      throw new CEStreamExhausted();
    } 
    if (i == -1)
      throw new CEFormatException("UUDecoder: Short Buffer."); 
    i = i - 32 & 0x3F;
    if (i > bytesPerLine())
      throw new CEFormatException("UUDecoder: Bad Line Length."); 
    return i;
  }
  
  protected void decodeLineSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {
    while (true) {
      int i = paramPushbackInputStream.read();
      if (i == -1)
        throw new CEStreamExhausted(); 
      if (i == 10)
        break; 
      if (i == 13) {
        i = paramPushbackInputStream.read();
        if (i != 10 && i != -1)
          paramPushbackInputStream.unread(i); 
        break;
      } 
    } 
  }
  
  protected void decodeBufferSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {
    int i = paramPushbackInputStream.read(this.decoderBuffer);
    if (this.decoderBuffer[0] != 101 || this.decoderBuffer[1] != 110 || this.decoderBuffer[2] != 100)
      throw new CEFormatException("UUDecoder: Missing 'end' line."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\UUDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */