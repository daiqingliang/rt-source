package java.util.spi;

import java.util.Locale;

public abstract class TimeZoneNameProvider extends LocaleServiceProvider {
  public abstract String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale);
  
  public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\spi\TimeZoneNameProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */