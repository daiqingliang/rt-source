package java.security;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DigestOutputStream extends FilterOutputStream {
  private boolean on = true;
  
  protected MessageDigest digest;
  
  public DigestOutputStream(OutputStream paramOutputStream, MessageDigest paramMessageDigest) {
    super(paramOutputStream);
    setMessageDigest(paramMessageDigest);
  }
  
  public MessageDigest getMessageDigest() { return this.digest; }
  
  public void setMessageDigest(MessageDigest paramMessageDigest) { this.digest = paramMessageDigest; }
  
  public void write(int paramInt) throws IOException {
    this.out.write(paramInt);
    if (this.on)
      this.digest.update((byte)paramInt); 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    if (this.on)
      this.digest.update(paramArrayOfByte, paramInt1, paramInt2); 
  }
  
  public void on(boolean paramBoolean) { this.on = paramBoolean; }
  
  public String toString() { return "[Digest Output Stream] " + this.digest.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\DigestOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */