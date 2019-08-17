package com.sun.org.apache.xalan.internal;

public class Version {
  public static String getVersion() { return getProduct() + " " + getImplementationLanguage() + " " + getMajorVersionNum() + "." + getReleaseVersionNum() + "." + ((getDevelopmentVersionNum() > 0) ? ("D" + getDevelopmentVersionNum()) : ("" + getMaintenanceVersionNum())); }
  
  public static void _main(String[] paramArrayOfString) { System.out.println(getVersion()); }
  
  public static String getProduct() { return "Xalan"; }
  
  public static String getImplementationLanguage() { return "Java"; }
  
  public static int getMajorVersionNum() { return 2; }
  
  public static int getReleaseVersionNum() { return 7; }
  
  public static int getMaintenanceVersionNum() { return 0; }
  
  public static int getDevelopmentVersionNum() {
    try {
      return ((new String("")).length() == 0) ? 0 : Integer.parseInt("");
    } catch (NumberFormatException numberFormatException) {
      return 0;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */