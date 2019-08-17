package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public interface IdentifiableFactory {
  int getId();
  
  Identifiable create(InputStream paramInputStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */