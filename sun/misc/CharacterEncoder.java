package sun.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

public abstract class CharacterEncoder {
  protected PrintStream pStream;
  
  protected abstract int bytesPerAtom();
  
  protected abstract int bytesPerLine();
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream) throws IOException { this.pStream = new PrintStream(paramOutputStream); }
  
  protected void encodeBufferSuffix(OutputStream paramOutputStream) throws IOException {}
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt) throws IOException {}
  
  protected void encodeLineSuffix(OutputStream paramOutputStream) throws IOException { this.pStream.println(); }
  
  protected abstract void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  protected int readFully(InputStream paramInputStream, byte[] paramArrayOfByte) throws IOException {
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      int i = paramInputStream.read();
      if (i == -1)
        return b; 
      paramArrayOfByte[b] = (byte)i;
    } 
    return paramArrayOfByte.length;
  }
  
  public void encode(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = new byte[bytesPerLine()];
    encodeBufferPrefix(paramOutputStream);
    while (true) {
      int j = readFully(paramInputStream, arrayOfByte);
      if (j == 0)
        break; 
      encodeLinePrefix(paramOutputStream, j);
      for (int i = 0; i < j; i += bytesPerAtom()) {
        if (i + bytesPerAtom() <= j) {
          encodeAtom(paramOutputStream, arrayOfByte, i, bytesPerAtom());
        } else {
          encodeAtom(paramOutputStream, arrayOfByte, i, j - i);
        } 
      } 
      if (j < bytesPerLine())
        break; 
      encodeLineSuffix(paramOutputStream);
    } 
    encodeBufferSuffix(paramOutputStream);
  }
  
  public void encode(byte[] paramArrayOfByte, OutputStream paramOutputStream) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    encode(byteArrayInputStream, paramOutputStream);
  }
  
  public String encode(byte[] paramArrayOfByte) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    String str = null;
    try {
      encode(byteArrayInputStream, byteArrayOutputStream);
      str = byteArrayOutputStream.toString("8859_1");
    } catch (Exception exception) {
      throw new Error("CharacterEncoder.encode internal error");
    } 
    return str;
  }
  
  private byte[] getBytes(ByteBuffer paramByteBuffer) {
    byte[] arrayOfByte = null;
    if (paramByteBuffer.hasArray()) {
      byte[] arrayOfByte1 = paramByteBuffer.array();
      if (arrayOfByte1.length == paramByteBuffer.capacity() && arrayOfByte1.length == paramByteBuffer.remaining()) {
        arrayOfByte = arrayOfByte1;
        paramByteBuffer.position(paramByteBuffer.limit());
      } 
    } 
    if (arrayOfByte == null) {
      arrayOfByte = new byte[paramByteBuffer.remaining()];
      paramByteBuffer.get(arrayOfByte);
    } 
    return arrayOfByte;
  }
  
  public void encode(ByteBuffer paramByteBuffer, OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = getBytes(paramByteBuffer);
    encode(arrayOfByte, paramOutputStream);
  }
  
  public String encode(ByteBuffer paramByteBuffer) {
    byte[] arrayOfByte = getBytes(paramByteBuffer);
    return encode(arrayOfByte);
  }
  
  public void encodeBuffer(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    int i;
    byte[] arrayOfByte = new byte[bytesPerLine()];
    encodeBufferPrefix(paramOutputStream);
    do {
      i = readFully(paramInputStream, arrayOfByte);
      if (i == 0)
        break; 
      encodeLinePrefix(paramOutputStream, i);
      for (int j = 0; j < i; j += bytesPerAtom()) {
        if (j + bytesPerAtom() <= i) {
          encodeAtom(paramOutputStream, arrayOfByte, j, bytesPerAtom());
        } else {
          encodeAtom(paramOutputStream, arrayOfByte, j, i - j);
        } 
      } 
      encodeLineSuffix(paramOutputStream);
    } while (i >= bytesPerLine());
    encodeBufferSuffix(paramOutputStream);
  }
  
  public void encodeBuffer(byte[] paramArrayOfByte, OutputStream paramOutputStream) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    encodeBuffer(byteArrayInputStream, paramOutputStream);
  }
  
  public String encodeBuffer(byte[] paramArrayOfByte) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    try {
      encodeBuffer(byteArrayInputStream, byteArrayOutputStream);
    } catch (Exception exception) {
      throw new Error("CharacterEncoder.encodeBuffer internal error");
    } 
    return byteArrayOutputStream.toString();
  }
  
  public void encodeBuffer(ByteBuffer paramByteBuffer, OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = getBytes(paramByteBuffer);
    encodeBuffer(arrayOfByte, paramOutputStream);
  }
  
  public String encodeBuffer(ByteBuffer paramByteBuffer) {
    byte[] arrayOfByte = getBytes(paramByteBuffer);
    return encodeBuffer(arrayOfByte);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\CharacterEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */