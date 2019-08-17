package com.sun.corba.se.impl.util;

public final class PackagePrefixChecker {
  private static final String PACKAGE_PREFIX = "org.omg.stub.";
  
  public static String packagePrefix() { return "org.omg.stub."; }
  
  public static String correctPackageName(String paramString) { return (paramString == null) ? paramString : (hasOffendingPrefix(paramString) ? ("org.omg.stub." + paramString) : paramString); }
  
  public static boolean isOffendingPackage(String paramString) { return (paramString != null && hasOffendingPrefix(paramString)); }
  
  public static boolean hasOffendingPrefix(String paramString) { return (paramString.startsWith("java.") || paramString.equals("java") || paramString.startsWith("net.jini.") || paramString.equals("net.jini") || paramString.startsWith("jini.") || paramString.equals("jini") || paramString.startsWith("javax.") || paramString.equals("javax")); }
  
  public static boolean hasBeenPrefixed(String paramString) { return paramString.startsWith(packagePrefix()); }
  
  public static String withoutPackagePrefix(String paramString) { return hasBeenPrefixed(paramString) ? paramString.substring(packagePrefix().length()) : paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\PackagePrefixChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */