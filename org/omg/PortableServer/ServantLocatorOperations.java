package org.omg.PortableServer;

import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public interface ServantLocatorOperations extends ServantManagerOperations {
  Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder) throws ForwardRequest;
  
  void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\ServantLocatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */