package java.text.spi;

import java.text.DateFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatProvider extends LocaleServiceProvider {
  public abstract DateFormat getTimeInstance(int paramInt, Locale paramLocale);
  
  public abstract DateFormat getDateInstance(int paramInt, Locale paramLocale);
  
  public abstract DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\spi\DateFormatProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */