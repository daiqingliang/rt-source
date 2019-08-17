package sun.net.ftp.impl;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpClientProvider;

public class DefaultFtpClientProvider extends FtpClientProvider {
  public FtpClient createFtpClient() { return FtpClient.create(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ftp\impl\DefaultFtpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */