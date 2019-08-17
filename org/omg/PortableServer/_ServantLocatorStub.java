package org.omg.PortableServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class _ServantLocatorStub extends ObjectImpl implements ServantLocator {
  public static final Class _opsClass = ServantLocatorOperations.class;
  
  private static String[] __ids = { "IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0" };
  
  public Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder) throws ForwardRequest {
    servantObject = _servant_preinvoke("preinvoke", _opsClass);
    ServantLocatorOperations servantLocatorOperations = (ServantLocatorOperations)servantObject.servant;
    try {
      return servantLocatorOperations.preinvoke(paramArrayOfByte, paramPOA, paramString, paramCookieHolder);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant) {
    servantObject = _servant_preinvoke("postinvoke", _opsClass);
    ServantLocatorOperations servantLocatorOperations = (ServantLocatorOperations)servantObject.servant;
    try {
      servantLocatorOperations.postinvoke(paramArrayOfByte, paramPOA, paramString, paramObject, paramServant);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    String str = paramObjectInputStream.readUTF();
    String[] arrayOfString = null;
    Properties properties = null;
    oRB = ORB.init(arrayOfString, properties);
    try {
      Object object = oRB.string_to_object(str);
      Delegate delegate = ((ObjectImpl)object)._get_delegate();
      _set_delegate(delegate);
    } finally {
      oRB.destroy();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    String[] arrayOfString = null;
    Properties properties = null;
    oRB = ORB.init(arrayOfString, properties);
    try {
      String str = oRB.object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    } finally {
      oRB.destroy();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\_ServantLocatorStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */