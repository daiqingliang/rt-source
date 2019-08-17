package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider {
  public abstract Locale[] getAvailableLocales();
  
  public boolean isSupportedLocale(Locale paramLocale) {
    paramLocale = paramLocale.stripExtensions();
    for (Locale locale : getAvailableLocales()) {
      if (paramLocale.equals(locale.stripExtensions()))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\spi\LocaleServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */