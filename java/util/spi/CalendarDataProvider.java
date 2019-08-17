package java.util.spi;

import java.util.Locale;

public abstract class CalendarDataProvider extends LocaleServiceProvider {
  public abstract int getFirstDayOfWeek(Locale paramLocale);
  
  public abstract int getMinimalDaysInFirstWeek(Locale paramLocale);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\spi\CalendarDataProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */