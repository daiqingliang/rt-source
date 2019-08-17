package sun.net.www.protocol.http;

import java.io.InputStream;

class EmptyInputStream extends InputStream {
  public int available() { return 0; }
  
  public int read() { return -1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\EmptyInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */