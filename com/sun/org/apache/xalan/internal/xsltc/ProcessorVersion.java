package com.sun.org.apache.xalan.internal.xsltc;

public class ProcessorVersion {
  private static int MAJOR = 1;
  
  private static int MINOR = 0;
  
  private static int DELTA = 0;
  
  public static void main(String[] paramArrayOfString) { System.out.println("XSLTC version " + MAJOR + "." + MINOR + ((DELTA > 0) ? ("." + DELTA) : "")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\ProcessorVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */