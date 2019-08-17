package com.sun.corba.se.spi.copyobject;

public interface CopierManager {
  void setDefaultId(int paramInt);
  
  int getDefaultId();
  
  ObjectCopierFactory getObjectCopierFactory(int paramInt);
  
  ObjectCopierFactory getDefaultObjectCopierFactory();
  
  void registerObjectCopierFactory(ObjectCopierFactory paramObjectCopierFactory, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\copyobject\CopierManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */