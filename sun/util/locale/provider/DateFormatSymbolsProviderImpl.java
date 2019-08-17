package sun.util.locale.provider;

import java.text.DateFormatSymbols;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Locale;
import java.util.Set;

public class DateFormatSymbolsProviderImpl extends DateFormatSymbolsProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public DateFormatSymbolsProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public boolean isSupportedLocale(Locale paramLocale) { return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags); }
  
  public DateFormatSymbols getInstance(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    return new DateFormatSymbols(paramLocale);
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\DateFormatSymbolsProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */