package org.xml.sax.helpers;

class NewInstance {
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
  
  static Object newInstance(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    Class clazz;
    boolean bool = false;
    if (System.getSecurityManager() != null && paramString != null && paramString.startsWith("com.sun.org.apache.xerces.internal"))
      bool = true; 
    if (paramClassLoader == null || bool) {
      clazz = Class.forName(paramString);
    } else {
      clazz = paramClassLoader.loadClass(paramString);
    } 
    return clazz.newInstance();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\NewInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */