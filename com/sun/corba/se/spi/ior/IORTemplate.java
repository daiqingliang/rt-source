package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import java.util.List;

public interface IORTemplate extends List, IORFactory, MakeImmutable {
  Iterator iteratorById(int paramInt);
  
  ObjectKeyTemplate getObjectKeyTemplate();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\IORTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */