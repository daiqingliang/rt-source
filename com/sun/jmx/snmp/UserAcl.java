package com.sun.jmx.snmp;

public interface UserAcl {
  String getName();
  
  boolean checkReadPermission(String paramString);
  
  boolean checkReadPermission(String paramString1, String paramString2, int paramInt);
  
  boolean checkContextName(String paramString);
  
  boolean checkWritePermission(String paramString);
  
  boolean checkWritePermission(String paramString1, String paramString2, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\UserAcl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */