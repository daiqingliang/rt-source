package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public interface ObjectAdapter {
  ORB getORB();
  
  Policy getEffectivePolicy(int paramInt);
  
  IORTemplate getIORTemplate();
  
  int getManagerId();
  
  short getState();
  
  ObjectReferenceTemplate getAdapterTemplate();
  
  ObjectReferenceFactory getCurrentFactory();
  
  void setCurrentFactory(ObjectReferenceFactory paramObjectReferenceFactory);
  
  Object getLocalServant(byte[] paramArrayOfByte);
  
  void getInvocationServant(OAInvocationInfo paramOAInvocationInfo);
  
  void enter() throws OADestroyed;
  
  void exit() throws OADestroyed;
  
  void returnServant() throws OADestroyed;
  
  OAInvocationInfo makeInvocationInfo(byte[] paramArrayOfByte);
  
  String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\oa\ObjectAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */