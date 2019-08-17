package sun.misc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.jar.Manifest;
import sun.nio.ByteBuffered;

public abstract class Resource {
  private InputStream cis;
  
  public abstract String getName();
  
  public abstract URL getURL();
  
  public abstract URL getCodeSourceURL();
  
  public abstract InputStream getInputStream() throws IOException;
  
  public abstract int getContentLength() throws IOException;
  
  private InputStream cachedInputStream() throws IOException {
    if (this.cis == null)
      this.cis = getInputStream(); 
    return this.cis;
  }
  
  public byte[] getBytes() throws IOException {
    int i;
    byte[] arrayOfByte;
    inputStream = cachedInputStream();
    bool = Thread.interrupted();
    while (true) {
      try {
        i = getContentLength();
        break;
      } catch (InterruptedIOException interruptedIOException) {
        Thread.interrupted();
        bool = true;
      } 
    } 
    try {
      arrayOfByte = new byte[0];
      if (i == -1)
        i = Integer.MAX_VALUE; 
      for (j = 0; j < i; j += m) {
        int k;
        if (j >= arrayOfByte.length) {
          k = Math.min(i - j, arrayOfByte.length + 1024);
          if (arrayOfByte.length < j + k)
            arrayOfByte = Arrays.copyOf(arrayOfByte, j + k); 
        } else {
          k = arrayOfByte.length - j;
        } 
        int m = 0;
        try {
          m = inputStream.read(arrayOfByte, j, k);
        } catch (InterruptedIOException interruptedIOException) {
          Thread.interrupted();
          bool = true;
        } 
        if (m < 0) {
          if (i != Integer.MAX_VALUE)
            throw new EOFException("Detect premature EOF"); 
          if (arrayOfByte.length != j)
            arrayOfByte = Arrays.copyOf(arrayOfByte, j); 
          break;
        } 
      } 
    } finally {
      try {
        inputStream.close();
      } catch (InterruptedIOException interruptedIOException) {
        bool = true;
      } catch (IOException iOException) {}
      if (bool)
        Thread.currentThread().interrupt(); 
    } 
    return arrayOfByte;
  }
  
  public ByteBuffer getByteBuffer() throws IOException {
    InputStream inputStream = cachedInputStream();
    return (inputStream instanceof ByteBuffered) ? ((ByteBuffered)inputStream).getByteBuffer() : null;
  }
  
  public Manifest getManifest() throws IOException { return null; }
  
  public Certificate[] getCertificates() { return null; }
  
  public CodeSigner[] getCodeSigners() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */