package java.util.spi;

import java.util.Locale;

public abstract class LocaleNameProvider extends LocaleServiceProvider {
  public abstract String getDisplayLanguage(String paramString, Locale paramLocale);
  
  public String getDisplayScript(String paramString, Locale paramLocale) { return null; }
  
  public abstract String getDisplayCountry(String paramString, Locale paramLocale);
  
  public abstract String getDisplayVariant(String paramString, Locale paramLocale);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\spi\LocaleNameProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */