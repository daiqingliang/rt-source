package com.sun.org.apache.xerces.internal.impl;

public class Version {
  public static final String fVersion = getVersion();
  
  private static final String fImmutableVersion = "Xerces-J 2.7.1";
  
  public static String getVersion() { return "Xerces-J 2.7.1"; }
  
  public static void main(String[] paramArrayOfString) { System.out.println(fVersion); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */