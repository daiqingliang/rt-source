package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.ORB;

public class JavaStreamObjectCopierImpl implements ObjectCopier {
  private ORB orb;
  
  public JavaStreamObjectCopierImpl(ORB paramORB) { this.orb = paramORB; }
  
  public Object copy(Object paramObject) {
    if (paramObject instanceof java.rmi.Remote)
      return Utility.autoConnect(paramObject, this.orb, true); 
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
      objectOutputStream.writeObject(paramObject);
      byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
      return objectInputStream.readObject();
    } catch (Exception exception) {
      System.out.println("Failed with exception:" + exception);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\copyobject\JavaStreamObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */