package sun.util.locale.provider;

import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.spi.CalendarProvider;

public abstract class AuxLocaleProviderAdapter extends LocaleProviderAdapter {
  private ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProvider> providersMap = new ConcurrentHashMap();
  
  private static Locale[] availableLocales = null;
  
  private static NullProvider NULL_PROVIDER = new NullProvider(null);
  
  public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass) {
    LocaleServiceProvider localeServiceProvider = (LocaleServiceProvider)this.providersMap.get(paramClass);
    if (localeServiceProvider == null) {
      localeServiceProvider = findInstalledProvider(paramClass);
      this.providersMap.putIfAbsent(paramClass, (localeServiceProvider == null) ? NULL_PROVIDER : localeServiceProvider);
    } 
    return (P)localeServiceProvider;
  }
  
  protected abstract <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> paramClass);
  
  public BreakIteratorProvider getBreakIteratorProvider() { return (BreakIteratorProvider)getLocaleServiceProvider(BreakIteratorProvider.class); }
  
  public CollatorProvider getCollatorProvider() { return (CollatorProvider)getLocaleServiceProvider(CollatorProvider.class); }
  
  public DateFormatProvider getDateFormatProvider() { return (DateFormatProvider)getLocaleServiceProvider(DateFormatProvider.class); }
  
  public DateFormatSymbolsProvider getDateFormatSymbolsProvider() { return (DateFormatSymbolsProvider)getLocaleServiceProvider(DateFormatSymbolsProvider.class); }
  
  public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() { return (DecimalFormatSymbolsProvider)getLocaleServiceProvider(DecimalFormatSymbolsProvider.class); }
  
  public NumberFormatProvider getNumberFormatProvider() { return (NumberFormatProvider)getLocaleServiceProvider(NumberFormatProvider.class); }
  
  public CurrencyNameProvider getCurrencyNameProvider() { return (CurrencyNameProvider)getLocaleServiceProvider(CurrencyNameProvider.class); }
  
  public LocaleNameProvider getLocaleNameProvider() { return (LocaleNameProvider)getLocaleServiceProvider(LocaleNameProvider.class); }
  
  public TimeZoneNameProvider getTimeZoneNameProvider() { return (TimeZoneNameProvider)getLocaleServiceProvider(TimeZoneNameProvider.class); }
  
  public CalendarDataProvider getCalendarDataProvider() { return (CalendarDataProvider)getLocaleServiceProvider(CalendarDataProvider.class); }
  
  public CalendarNameProvider getCalendarNameProvider() { return (CalendarNameProvider)getLocaleServiceProvider(CalendarNameProvider.class); }
  
  public CalendarProvider getCalendarProvider() { return (CalendarProvider)getLocaleServiceProvider(CalendarProvider.class); }
  
  public LocaleResources getLocaleResources(Locale paramLocale) { return null; }
  
  public Locale[] getAvailableLocales() {
    if (availableLocales == null) {
      HashSet hashSet = new HashSet();
      for (Class clazz : LocaleServiceProviderPool.spiClasses) {
        LocaleServiceProvider localeServiceProvider = getLocaleServiceProvider(clazz);
        if (localeServiceProvider != null)
          hashSet.addAll(Arrays.asList(localeServiceProvider.getAvailableLocales())); 
      } 
      availableLocales = (Locale[])hashSet.toArray(new Locale[0]);
    } 
    return availableLocales;
  }
  
  private static class NullProvider extends LocaleServiceProvider {
    private NullProvider() {}
    
    public Locale[] getAvailableLocales() { return new Locale[0]; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\AuxLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */