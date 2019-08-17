package com.sun.corba.se.spi.ior;

import java.util.Iterator;

public interface ObjectAdapterId extends Writeable {
  int getNumLevels();
  
  Iterator iterator();
  
  String[] getAdapterName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\ObjectAdapterId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */