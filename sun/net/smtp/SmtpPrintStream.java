package sun.net.smtp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

class SmtpPrintStream extends PrintStream {
  private SmtpClient target;
  
  private int lastc = 10;
  
  SmtpPrintStream(OutputStream paramOutputStream, SmtpClient paramSmtpClient) throws UnsupportedEncodingException {
    super(paramOutputStream, false, paramSmtpClient.getEncoding());
    this.target = paramSmtpClient;
  }
  
  public void close() {
    if (this.target == null)
      return; 
    if (this.lastc != 10)
      write(10); 
    try {
      this.target.issueCommand(".\r\n", 250);
      this.target.message = null;
      this.out = null;
      this.target = null;
    } catch (IOException iOException) {}
  }
  
  public void write(int paramInt) {
    try {
      if (this.lastc == 10 && paramInt == 46)
        this.out.write(46); 
      if (paramInt == 10 && this.lastc != 13)
        this.out.write(13); 
      this.out.write(paramInt);
      this.lastc = paramInt;
    } catch (IOException iOException) {}
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      byte b = this.lastc;
      while (--paramInt2 >= 0) {
        byte b1 = paramArrayOfByte[paramInt1++];
        if (b == 10 && b1 == 46)
          this.out.write(46); 
        if (b1 == 10 && b != 13)
          this.out.write(13); 
        this.out.write(b1);
        b = b1;
      } 
      this.lastc = b;
    } catch (IOException iOException) {}
  }
  
  public void print(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      write(paramString.charAt(b)); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\smtp\SmtpPrintStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */