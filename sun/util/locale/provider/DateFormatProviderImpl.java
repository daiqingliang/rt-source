package sun.util.locale.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class DateFormatProviderImpl extends DateFormatProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public DateFormatProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public boolean isSupportedLocale(Locale paramLocale) { return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags); }
  
  public DateFormat getTimeInstance(int paramInt, Locale paramLocale) { return getInstance(-1, paramInt, paramLocale); }
  
  public DateFormat getDateInstance(int paramInt, Locale paramLocale) { return getInstance(paramInt, -1, paramLocale); }
  
  public DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale) { return getInstance(paramInt1, paramInt2, paramLocale); }
  
  private DateFormat getInstance(int paramInt1, int paramInt2, Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", paramLocale);
    Calendar calendar = simpleDateFormat.getCalendar();
    try {
      String str = LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getDateTimePattern(paramInt2, paramInt1, calendar);
      simpleDateFormat.applyPattern(str);
    } catch (MissingResourceException missingResourceException) {
      simpleDateFormat.applyPattern("M/d/yy h:mm a");
    } 
    return simpleDateFormat;
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\DateFormatProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */