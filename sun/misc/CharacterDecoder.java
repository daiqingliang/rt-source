package sun.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;

public abstract class CharacterDecoder {
  protected abstract int bytesPerAtom();
  
  protected abstract int bytesPerLine();
  
  protected void decodeBufferPrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {}
  
  protected void decodeBufferSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {}
  
  protected int decodeLinePrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException { return bytesPerLine(); }
  
  protected void decodeLineSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream) throws IOException {}
  
  protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt) throws IOException { throw new CEStreamExhausted(); }
  
  protected int readFully(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    for (int i = 0; i < paramInt2; i++) {
      int j = paramInputStream.read();
      if (j == -1)
        return !i ? -1 : i; 
      paramArrayOfByte[i + paramInt1] = (byte)j;
    } 
    return paramInt2;
  }
  
  public void decodeBuffer(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    int i = 0;
    PushbackInputStream pushbackInputStream = new PushbackInputStream(paramInputStream);
    decodeBufferPrefix(pushbackInputStream, paramOutputStream);
    try {
      while (true) {
        int k = decodeLinePrefix(pushbackInputStream, paramOutputStream);
        int j;
        for (j = 0; j + bytesPerAtom() < k; j += bytesPerAtom()) {
          decodeAtom(pushbackInputStream, paramOutputStream, bytesPerAtom());
          i += bytesPerAtom();
        } 
        if (j + bytesPerAtom() == k) {
          decodeAtom(pushbackInputStream, paramOutputStream, bytesPerAtom());
          i += bytesPerAtom();
        } else {
          decodeAtom(pushbackInputStream, paramOutputStream, k - j);
          i += k - j;
        } 
        decodeLineSuffix(pushbackInputStream, paramOutputStream);
      } 
    } catch (CEStreamExhausted cEStreamExhausted) {
      decodeBufferSuffix(pushbackInputStream, paramOutputStream);
      return;
    } 
  }
  
  public byte[] decodeBuffer(String paramString) throws IOException {
    byte[] arrayOfByte = new byte[paramString.length()];
    paramString.getBytes(0, paramString.length(), arrayOfByte, 0);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    decodeBuffer(byteArrayInputStream, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public byte[] decodeBuffer(InputStream paramInputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    decodeBuffer(paramInputStream, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public ByteBuffer decodeBufferToByteBuffer(String paramString) throws IOException { return ByteBuffer.wrap(decodeBuffer(paramString)); }
  
  public ByteBuffer decodeBufferToByteBuffer(InputStream paramInputStream) throws IOException { return ByteBuffer.wrap(decodeBuffer(paramInputStream)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\CharacterDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */