package com.sun.imageio.plugins.common;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

public class I18NImpl {
  protected static final String getString(String paramString1, String paramString2, String paramString3) {
    PropertyResourceBundle propertyResourceBundle = null;
    try {
      InputStream inputStream = Class.forName(paramString1).getResourceAsStream(paramString2);
      propertyResourceBundle = new PropertyResourceBundle(inputStream);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    } 
    return (String)propertyResourceBundle.handleGetObject(paramString3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\I18NImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */