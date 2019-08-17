package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;
import java.text.Collator;
import java.util.Locale;

public class CollatorFactoryBase implements CollatorFactory {
  public static final Locale DEFAULT_LOCALE = Locale.getDefault();
  
  public static final Collator DEFAULT_COLLATOR = Collator.getInstance();
  
  public Collator getCollator(String paramString1, String paramString2) { return Collator.getInstance(new Locale(paramString1, paramString2)); }
  
  public Collator getCollator(Locale paramLocale) { return (paramLocale == DEFAULT_LOCALE) ? DEFAULT_COLLATOR : Collator.getInstance(paramLocale); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CollatorFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */