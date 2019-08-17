package com.sun.org.apache.xalan.internal.xsltc;

import java.text.Collator;
import java.util.Locale;

public interface CollatorFactory {
  Collator getCollator(String paramString1, String paramString2);
  
  Collator getCollator(Locale paramLocale);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\CollatorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */