package com.sun.java.browser.dom;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class DOMService {
  public static DOMService getService(Object paramObject) throws DOMUnsupportedException {
    try {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("com.sun.java.browser.dom.DOMServiceProvider"));
      Class clazz = DOMService.class.forName("sun.plugin.dom.DOMService");
      return (DOMService)clazz.newInstance();
    } catch (Throwable throwable) {
      throw new DOMUnsupportedException(throwable.toString());
    } 
  }
  
  public abstract Object invokeAndWait(DOMAction paramDOMAction) throws DOMAccessException;
  
  public abstract void invokeLater(DOMAction paramDOMAction);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\browser\dom\DOMService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */