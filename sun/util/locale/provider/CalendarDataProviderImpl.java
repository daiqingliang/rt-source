package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CalendarDataProvider;

public class CalendarDataProviderImpl extends CalendarDataProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public CalendarDataProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public int getFirstDayOfWeek(Locale paramLocale) { return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getCalendarData("firstDayOfWeek"); }
  
  public int getMinimalDaysInFirstWeek(Locale paramLocale) { return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getCalendarData("minimalDaysInFirstWeek"); }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\CalendarDataProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */