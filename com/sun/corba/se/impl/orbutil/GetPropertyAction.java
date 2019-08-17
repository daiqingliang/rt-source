package com.sun.corba.se.impl.orbutil;

import java.security.PrivilegedAction;

public class GetPropertyAction implements PrivilegedAction {
  private String theProp;
  
  private String defaultVal;
  
  public GetPropertyAction(String paramString) { this.theProp = paramString; }
  
  public GetPropertyAction(String paramString1, String paramString2) {
    this.theProp = paramString1;
    this.defaultVal = paramString2;
  }
  
  public Object run() {
    String str = System.getProperty(this.theProp);
    return (str == null) ? this.defaultVal : str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */