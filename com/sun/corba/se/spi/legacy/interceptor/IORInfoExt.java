package com.sun.corba.se.spi.legacy.interceptor;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public interface IORInfoExt {
  int getServerPort(String paramString) throws UnknownType;
  
  ObjectAdapter getObjectAdapter();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\legacy\interceptor\IORInfoExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */