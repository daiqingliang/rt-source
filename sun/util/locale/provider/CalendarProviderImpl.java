package sun.util.locale.provider;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import sun.util.spi.CalendarProvider;

public class CalendarProviderImpl extends CalendarProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public CalendarProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public boolean isSupportedLocale(Locale paramLocale) { return true; }
  
  public Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale) { return (new Calendar.Builder()).setLocale(paramLocale).setTimeZone(paramTimeZone).setInstant(System.currentTimeMillis()).build(); }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\CalendarProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */