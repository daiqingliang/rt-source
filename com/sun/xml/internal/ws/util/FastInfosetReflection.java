package com.sun.xml.internal.ws.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class FastInfosetReflection {
  public static final Constructor fiStAXDocumentParser_new;
  
  public static final Method fiStAXDocumentParser_setInputStream;
  
  public static final Method fiStAXDocumentParser_setStringInterning;
  
  static  {
    Constructor constructor = null;
    Method method1 = null;
    Method method2 = null;
    try {
      Class clazz = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
      constructor = clazz.getConstructor(new Class[0]);
      method1 = clazz.getMethod("setInputStream", new Class[] { java.io.InputStream.class });
      method2 = clazz.getMethod("setStringInterning", new Class[] { boolean.class });
    } catch (Exception exception) {}
    fiStAXDocumentParser_new = constructor;
    fiStAXDocumentParser_setInputStream = method1;
    fiStAXDocumentParser_setStringInterning = method2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\FastInfosetReflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */