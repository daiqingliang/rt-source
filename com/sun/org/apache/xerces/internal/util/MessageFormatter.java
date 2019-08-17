package com.sun.org.apache.xerces.internal.util;

import java.util.Locale;
import java.util.MissingResourceException;

public interface MessageFormatter {
  String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject) throws MissingResourceException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\MessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */