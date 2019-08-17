package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public interface ObjectAdapterFactory {
  void init(ORB paramORB);
  
  void shutdown(boolean paramBoolean);
  
  ObjectAdapter find(ObjectAdapterId paramObjectAdapterId);
  
  ORB getORB();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\oa\ObjectAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */