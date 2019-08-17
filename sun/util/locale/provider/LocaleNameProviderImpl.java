package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.LocaleNameProvider;

public class LocaleNameProviderImpl extends LocaleNameProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public LocaleNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public boolean isSupportedLocale(Locale paramLocale) { return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags); }
  
  public String getDisplayLanguage(String paramString, Locale paramLocale) { return getDisplayString(paramString, paramLocale); }
  
  public String getDisplayScript(String paramString, Locale paramLocale) { return getDisplayString(paramString, paramLocale); }
  
  public String getDisplayCountry(String paramString, Locale paramLocale) { return getDisplayString(paramString, paramLocale); }
  
  public String getDisplayVariant(String paramString, Locale paramLocale) { return getDisplayString("%%" + paramString, paramLocale); }
  
  private String getDisplayString(String paramString, Locale paramLocale) {
    if (paramString == null || paramLocale == null)
      throw new NullPointerException(); 
    return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getLocaleName(paramString);
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\LocaleNameProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */