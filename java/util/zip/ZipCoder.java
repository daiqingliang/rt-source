package java.util.zip;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;

final class ZipCoder {
  private Charset cs;
  
  private CharsetDecoder dec;
  
  private CharsetEncoder enc;
  
  private boolean isUTF8;
  
  private ZipCoder utf8;
  
  String toString(byte[] paramArrayOfByte, int paramInt) {
    CharsetDecoder charsetDecoder = decoder().reset();
    int i = (int)(paramInt * charsetDecoder.maxCharsPerByte());
    char[] arrayOfChar = new char[i];
    if (i == 0)
      return new String(arrayOfChar); 
    if (this.isUTF8 && charsetDecoder instanceof ArrayDecoder) {
      int j = ((ArrayDecoder)charsetDecoder).decode(paramArrayOfByte, 0, paramInt, arrayOfChar);
      if (j == -1)
        throw new IllegalArgumentException("MALFORMED"); 
      return new String(arrayOfChar, 0, j);
    } 
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfByte, 0, paramInt);
    CharBuffer charBuffer = CharBuffer.wrap(arrayOfChar);
    CoderResult coderResult = charsetDecoder.decode(byteBuffer, charBuffer, true);
    if (!coderResult.isUnderflow())
      throw new IllegalArgumentException(coderResult.toString()); 
    coderResult = charsetDecoder.flush(charBuffer);
    if (!coderResult.isUnderflow())
      throw new IllegalArgumentException(coderResult.toString()); 
    return new String(arrayOfChar, 0, charBuffer.position());
  }
  
  String toString(byte[] paramArrayOfByte) { return toString(paramArrayOfByte, paramArrayOfByte.length); }
  
  byte[] getBytes(String paramString) {
    CharsetEncoder charsetEncoder = encoder().reset();
    char[] arrayOfChar = paramString.toCharArray();
    int i = (int)(arrayOfChar.length * charsetEncoder.maxBytesPerChar());
    byte[] arrayOfByte = new byte[i];
    if (i == 0)
      return arrayOfByte; 
    if (this.isUTF8 && charsetEncoder instanceof ArrayEncoder) {
      int j = ((ArrayEncoder)charsetEncoder).encode(arrayOfChar, 0, arrayOfChar.length, arrayOfByte);
      if (j == -1)
        throw new IllegalArgumentException("MALFORMED"); 
      return Arrays.copyOf(arrayOfByte, j);
    } 
    ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
    CharBuffer charBuffer = CharBuffer.wrap(arrayOfChar);
    CoderResult coderResult = charsetEncoder.encode(charBuffer, byteBuffer, true);
    if (!coderResult.isUnderflow())
      throw new IllegalArgumentException(coderResult.toString()); 
    coderResult = charsetEncoder.flush(byteBuffer);
    if (!coderResult.isUnderflow())
      throw new IllegalArgumentException(coderResult.toString()); 
    return (byteBuffer.position() == arrayOfByte.length) ? arrayOfByte : Arrays.copyOf(arrayOfByte, byteBuffer.position());
  }
  
  byte[] getBytesUTF8(String paramString) {
    if (this.isUTF8)
      return getBytes(paramString); 
    if (this.utf8 == null)
      this.utf8 = new ZipCoder(StandardCharsets.UTF_8); 
    return this.utf8.getBytes(paramString);
  }
  
  String toStringUTF8(byte[] paramArrayOfByte, int paramInt) {
    if (this.isUTF8)
      return toString(paramArrayOfByte, paramInt); 
    if (this.utf8 == null)
      this.utf8 = new ZipCoder(StandardCharsets.UTF_8); 
    return this.utf8.toString(paramArrayOfByte, paramInt);
  }
  
  boolean isUTF8() { return this.isUTF8; }
  
  private ZipCoder(Charset paramCharset) {
    this.cs = paramCharset;
    this.isUTF8 = paramCharset.name().equals(StandardCharsets.UTF_8.name());
  }
  
  static ZipCoder get(Charset paramCharset) { return new ZipCoder(paramCharset); }
  
  private CharsetDecoder decoder() {
    if (this.dec == null)
      this.dec = this.cs.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT); 
    return this.dec;
  }
  
  private CharsetEncoder encoder() {
    if (this.enc == null)
      this.enc = this.cs.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT); 
    return this.enc;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZipCoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */