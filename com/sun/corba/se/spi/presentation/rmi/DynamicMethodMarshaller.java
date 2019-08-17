package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface DynamicMethodMarshaller {
  Method getMethod();
  
  Object[] copyArguments(Object[] paramArrayOfObject, ORB paramORB) throws RemoteException;
  
  Object[] readArguments(InputStream paramInputStream);
  
  void writeArguments(OutputStream paramOutputStream, Object[] paramArrayOfObject);
  
  Object copyResult(Object paramObject, ORB paramORB) throws RemoteException;
  
  Object readResult(InputStream paramInputStream);
  
  void writeResult(OutputStream paramOutputStream, Object paramObject);
  
  boolean isDeclaredException(Throwable paramThrowable);
  
  void writeException(OutputStream paramOutputStream, Exception paramException);
  
  Exception readException(ApplicationException paramApplicationException);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\presentation\rmi\DynamicMethodMarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */