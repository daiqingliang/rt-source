package com.sun.corba.se.impl.orbutil;

import java.net.MalformedURLException;

public interface RepositoryIdInterface {
  Class getClassFromType() throws ClassNotFoundException;
  
  Class getClassFromType(String paramString) throws ClassNotFoundException, MalformedURLException;
  
  Class getClassFromType(Class paramClass, String paramString) throws ClassNotFoundException, MalformedURLException;
  
  String getClassName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */