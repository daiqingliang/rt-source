package com.sun.org.apache.xml.internal.security.utils;

public abstract class XPathFactory {
  private static boolean xalanInstalled;
  
  protected static boolean isXalanInstalled() { return xalanInstalled; }
  
  public static XPathFactory newInstance() { return !isXalanInstalled() ? new JDKXPathFactory() : (XalanXPathAPI.isInstalled() ? new XalanXPathFactory() : new JDKXPathFactory()); }
  
  public abstract XPathAPI newXPathAPI();
  
  static  {
    try {
      Class clazz = ClassLoaderUtils.loadClass("com.sun.org.apache.xpath.internal.compiler.FunctionTable", XPathFactory.class);
      if (clazz != null)
        xalanInstalled = true; 
    } catch (Exception exception) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\XPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */