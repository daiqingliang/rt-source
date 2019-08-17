package sun.net.spi.nameservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface NameService {
  InetAddress[] lookupAllHostAddr(String paramString) throws UnknownHostException;
  
  String getHostByAddr(byte[] paramArrayOfByte) throws UnknownHostException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\spi\nameservice\NameService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */