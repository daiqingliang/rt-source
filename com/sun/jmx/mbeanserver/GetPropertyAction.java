package com.sun.jmx.mbeanserver;

import java.security.PrivilegedAction;

public class GetPropertyAction extends Object implements PrivilegedAction<String> {
  private final String key;
  
  public GetPropertyAction(String paramString) { this.key = paramString; }
  
  public String run() { return System.getProperty(this.key); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */