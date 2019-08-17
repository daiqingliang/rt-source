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

public class _ServantActivatorStub extends ObjectImpl implements ServantActivator {
  public static final Class _opsClass = ServantActivatorOperations.class;
  
  private static String[] __ids = { "IDL:omg.org/PortableServer/ServantActivator:2.3", "IDL:omg.org/PortableServer/ServantManager:1.0" };
  
  public Servant incarnate(byte[] paramArrayOfByte, POA paramPOA) throws ForwardRequest {
    servantObject = _servant_preinvoke("incarnate", _opsClass);
    ServantActivatorOperations servantActivatorOperations = (ServantActivatorOperations)servantObject.servant;
    try {
      return servantActivatorOperations.incarnate(paramArrayOfByte, paramPOA);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public void etherealize(byte[] paramArrayOfByte, POA paramPOA, Servant paramServant, boolean paramBoolean1, boolean paramBoolean2) {
    servantObject = _servant_preinvoke("etherealize", _opsClass);
    ServantActivatorOperations servantActivatorOperations = (ServantActivatorOperations)servantObject.servant;
    try {
      servantActivatorOperations.etherealize(paramArrayOfByte, paramPOA, paramServant, paramBoolean1, paramBoolean2);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\_ServantActivatorStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */