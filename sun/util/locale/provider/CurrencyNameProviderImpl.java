package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CurrencyNameProvider;

public class CurrencyNameProviderImpl extends CurrencyNameProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public CurrencyNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public String getSymbol(String paramString, Locale paramLocale) { return getString(paramString.toUpperCase(Locale.ROOT), paramLocale); }
  
  public String getDisplayName(String paramString, Locale paramLocale) { return getString(paramString.toLowerCase(Locale.ROOT), paramLocale); }
  
  private String getString(String paramString, Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getCurrencyName(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\CurrencyNameProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */