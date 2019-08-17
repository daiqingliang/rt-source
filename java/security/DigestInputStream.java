package java.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DigestInputStream extends FilterInputStream {
  private boolean on = true;
  
  protected MessageDigest digest;
  
  public DigestInputStream(InputStream paramInputStream, MessageDigest paramMessageDigest) {
    super(paramInputStream);
    setMessageDigest(paramMessageDigest);
  }
  
  public MessageDigest getMessageDigest() { return this.digest; }
  
  public void setMessageDigest(MessageDigest paramMessageDigest) { this.digest = paramMessageDigest; }
  
  public int read() throws IOException {
    int i = this.in.read();
    if (this.on && i != -1)
      this.digest.update((byte)i); 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (this.on && i != -1)
      this.digest.update(paramArrayOfByte, paramInt1, i); 
    return i;
  }
  
  public void on(boolean paramBoolean) { this.on = paramBoolean; }
  
  public String toString() { return "[Digest Input Stream] " + this.digest.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\DigestInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */