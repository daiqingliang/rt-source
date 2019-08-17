package sun.util.locale.provider;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.BreakIterator;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;

public class SPILocaleProviderAdapter extends AuxLocaleProviderAdapter {
  public LocaleProviderAdapter.Type getAdapterType() { return LocaleProviderAdapter.Type.SPI; }
  
  protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> c) {
    try {
      return (P)(LocaleServiceProvider)AccessController.doPrivileged(new PrivilegedExceptionAction<P>() {
            public P run() {
              LocaleServiceProvider localeServiceProvider = null;
              for (LocaleServiceProvider localeServiceProvider1 : ServiceLoader.loadInstalled(c)) {
                if (localeServiceProvider == null)
                  try {
                    localeServiceProvider = (LocaleServiceProvider)Class.forName(SPILocaleProviderAdapter.class.getCanonicalName() + "$" + c.getSimpleName() + "Delegate").newInstance();
                  } catch (ClassNotFoundException|InstantiationException|IllegalAccessException classNotFoundException) {
                    LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, classNotFoundException.toString());
                    return null;
                  }  
                ((SPILocaleProviderAdapter.Delegate)localeServiceProvider).addImpl(localeServiceProvider1);
              } 
              return (P)localeServiceProvider;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, privilegedActionException.toString());
      return null;
    } 
  }
  
  private static <P extends LocaleServiceProvider> P getImpl(Map<Locale, P> paramMap, Locale paramLocale) {
    for (Locale locale : LocaleServiceProviderPool.getLookupLocales(paramLocale)) {
      LocaleServiceProvider localeServiceProvider = (LocaleServiceProvider)paramMap.get(locale);
      if (localeServiceProvider != null)
        return (P)localeServiceProvider; 
    } 
    return null;
  }
  
  static class BreakIteratorProviderDelegate extends BreakIteratorProvider implements Delegate<BreakIteratorProvider> {
    private ConcurrentMap<Locale, BreakIteratorProvider> map = new ConcurrentHashMap();
    
    public void addImpl(BreakIteratorProvider param1BreakIteratorProvider) {
      for (Locale locale : param1BreakIteratorProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1BreakIteratorProvider); 
    }
    
    public BreakIteratorProvider getImpl(Locale param1Locale) { return (BreakIteratorProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public BreakIterator getWordInstance(Locale param1Locale) {
      BreakIteratorProvider breakIteratorProvider = getImpl(param1Locale);
      assert breakIteratorProvider != null;
      return breakIteratorProvider.getWordInstance(param1Locale);
    }
    
    public BreakIterator getLineInstance(Locale param1Locale) {
      BreakIteratorProvider breakIteratorProvider = getImpl(param1Locale);
      assert breakIteratorProvider != null;
      return breakIteratorProvider.getLineInstance(param1Locale);
    }
    
    public BreakIterator getCharacterInstance(Locale param1Locale) {
      BreakIteratorProvider breakIteratorProvider = getImpl(param1Locale);
      assert breakIteratorProvider != null;
      return breakIteratorProvider.getCharacterInstance(param1Locale);
    }
    
    public BreakIterator getSentenceInstance(Locale param1Locale) {
      BreakIteratorProvider breakIteratorProvider = getImpl(param1Locale);
      assert breakIteratorProvider != null;
      return breakIteratorProvider.getSentenceInstance(param1Locale);
    }
  }
  
  static class CalendarDataProviderDelegate extends CalendarDataProvider implements Delegate<CalendarDataProvider> {
    private ConcurrentMap<Locale, CalendarDataProvider> map = new ConcurrentHashMap();
    
    public void addImpl(CalendarDataProvider param1CalendarDataProvider) {
      for (Locale locale : param1CalendarDataProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1CalendarDataProvider); 
    }
    
    public CalendarDataProvider getImpl(Locale param1Locale) { return (CalendarDataProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public int getFirstDayOfWeek(Locale param1Locale) {
      CalendarDataProvider calendarDataProvider = getImpl(param1Locale);
      assert calendarDataProvider != null;
      return calendarDataProvider.getFirstDayOfWeek(param1Locale);
    }
    
    public int getMinimalDaysInFirstWeek(Locale param1Locale) {
      CalendarDataProvider calendarDataProvider = getImpl(param1Locale);
      assert calendarDataProvider != null;
      return calendarDataProvider.getMinimalDaysInFirstWeek(param1Locale);
    }
  }
  
  static class CalendarNameProviderDelegate extends CalendarNameProvider implements Delegate<CalendarNameProvider> {
    private ConcurrentMap<Locale, CalendarNameProvider> map = new ConcurrentHashMap();
    
    public void addImpl(CalendarNameProvider param1CalendarNameProvider) {
      for (Locale locale : param1CalendarNameProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1CalendarNameProvider); 
    }
    
    public CalendarNameProvider getImpl(Locale param1Locale) { return (CalendarNameProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public String getDisplayName(String param1String, int param1Int1, int param1Int2, int param1Int3, Locale param1Locale) {
      CalendarNameProvider calendarNameProvider = getImpl(param1Locale);
      assert calendarNameProvider != null;
      return calendarNameProvider.getDisplayName(param1String, param1Int1, param1Int2, param1Int3, param1Locale);
    }
    
    public Map<String, Integer> getDisplayNames(String param1String, int param1Int1, int param1Int2, Locale param1Locale) {
      CalendarNameProvider calendarNameProvider = getImpl(param1Locale);
      assert calendarNameProvider != null;
      return calendarNameProvider.getDisplayNames(param1String, param1Int1, param1Int2, param1Locale);
    }
  }
  
  static class CollatorProviderDelegate extends CollatorProvider implements Delegate<CollatorProvider> {
    private ConcurrentMap<Locale, CollatorProvider> map = new ConcurrentHashMap();
    
    public void addImpl(CollatorProvider param1CollatorProvider) {
      for (Locale locale : param1CollatorProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1CollatorProvider); 
    }
    
    public CollatorProvider getImpl(Locale param1Locale) { return (CollatorProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public Collator getInstance(Locale param1Locale) {
      CollatorProvider collatorProvider = getImpl(param1Locale);
      assert collatorProvider != null;
      return collatorProvider.getInstance(param1Locale);
    }
  }
  
  static class CurrencyNameProviderDelegate extends CurrencyNameProvider implements Delegate<CurrencyNameProvider> {
    private ConcurrentMap<Locale, CurrencyNameProvider> map = new ConcurrentHashMap();
    
    public void addImpl(CurrencyNameProvider param1CurrencyNameProvider) {
      for (Locale locale : param1CurrencyNameProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1CurrencyNameProvider); 
    }
    
    public CurrencyNameProvider getImpl(Locale param1Locale) { return (CurrencyNameProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public String getSymbol(String param1String, Locale param1Locale) {
      CurrencyNameProvider currencyNameProvider = getImpl(param1Locale);
      assert currencyNameProvider != null;
      return currencyNameProvider.getSymbol(param1String, param1Locale);
    }
    
    public String getDisplayName(String param1String, Locale param1Locale) {
      CurrencyNameProvider currencyNameProvider = getImpl(param1Locale);
      assert currencyNameProvider != null;
      return currencyNameProvider.getDisplayName(param1String, param1Locale);
    }
  }
  
  static class DateFormatProviderDelegate extends DateFormatProvider implements Delegate<DateFormatProvider> {
    private ConcurrentMap<Locale, DateFormatProvider> map = new ConcurrentHashMap();
    
    public void addImpl(DateFormatProvider param1DateFormatProvider) {
      for (Locale locale : param1DateFormatProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1DateFormatProvider); 
    }
    
    public DateFormatProvider getImpl(Locale param1Locale) { return (DateFormatProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public DateFormat getTimeInstance(int param1Int, Locale param1Locale) {
      DateFormatProvider dateFormatProvider = getImpl(param1Locale);
      assert dateFormatProvider != null;
      return dateFormatProvider.getTimeInstance(param1Int, param1Locale);
    }
    
    public DateFormat getDateInstance(int param1Int, Locale param1Locale) {
      DateFormatProvider dateFormatProvider = getImpl(param1Locale);
      assert dateFormatProvider != null;
      return dateFormatProvider.getDateInstance(param1Int, param1Locale);
    }
    
    public DateFormat getDateTimeInstance(int param1Int1, int param1Int2, Locale param1Locale) {
      DateFormatProvider dateFormatProvider = getImpl(param1Locale);
      assert dateFormatProvider != null;
      return dateFormatProvider.getDateTimeInstance(param1Int1, param1Int2, param1Locale);
    }
  }
  
  static class DateFormatSymbolsProviderDelegate extends DateFormatSymbolsProvider implements Delegate<DateFormatSymbolsProvider> {
    private ConcurrentMap<Locale, DateFormatSymbolsProvider> map = new ConcurrentHashMap();
    
    public void addImpl(DateFormatSymbolsProvider param1DateFormatSymbolsProvider) {
      for (Locale locale : param1DateFormatSymbolsProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1DateFormatSymbolsProvider); 
    }
    
    public DateFormatSymbolsProvider getImpl(Locale param1Locale) { return (DateFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public DateFormatSymbols getInstance(Locale param1Locale) {
      DateFormatSymbolsProvider dateFormatSymbolsProvider = getImpl(param1Locale);
      assert dateFormatSymbolsProvider != null;
      return dateFormatSymbolsProvider.getInstance(param1Locale);
    }
  }
  
  static class DecimalFormatSymbolsProviderDelegate extends DecimalFormatSymbolsProvider implements Delegate<DecimalFormatSymbolsProvider> {
    private ConcurrentMap<Locale, DecimalFormatSymbolsProvider> map = new ConcurrentHashMap();
    
    public void addImpl(DecimalFormatSymbolsProvider param1DecimalFormatSymbolsProvider) {
      for (Locale locale : param1DecimalFormatSymbolsProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1DecimalFormatSymbolsProvider); 
    }
    
    public DecimalFormatSymbolsProvider getImpl(Locale param1Locale) { return (DecimalFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public DecimalFormatSymbols getInstance(Locale param1Locale) {
      DecimalFormatSymbolsProvider decimalFormatSymbolsProvider = getImpl(param1Locale);
      assert decimalFormatSymbolsProvider != null;
      return decimalFormatSymbolsProvider.getInstance(param1Locale);
    }
  }
  
  static interface Delegate<P extends LocaleServiceProvider> {
    void addImpl(P param1P);
    
    P getImpl(Locale param1Locale);
  }
  
  static class LocaleNameProviderDelegate extends LocaleNameProvider implements Delegate<LocaleNameProvider> {
    private ConcurrentMap<Locale, LocaleNameProvider> map = new ConcurrentHashMap();
    
    public void addImpl(LocaleNameProvider param1LocaleNameProvider) {
      for (Locale locale : param1LocaleNameProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1LocaleNameProvider); 
    }
    
    public LocaleNameProvider getImpl(Locale param1Locale) { return (LocaleNameProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public String getDisplayLanguage(String param1String, Locale param1Locale) {
      LocaleNameProvider localeNameProvider = getImpl(param1Locale);
      assert localeNameProvider != null;
      return localeNameProvider.getDisplayLanguage(param1String, param1Locale);
    }
    
    public String getDisplayScript(String param1String, Locale param1Locale) {
      LocaleNameProvider localeNameProvider = getImpl(param1Locale);
      assert localeNameProvider != null;
      return localeNameProvider.getDisplayScript(param1String, param1Locale);
    }
    
    public String getDisplayCountry(String param1String, Locale param1Locale) {
      LocaleNameProvider localeNameProvider = getImpl(param1Locale);
      assert localeNameProvider != null;
      return localeNameProvider.getDisplayCountry(param1String, param1Locale);
    }
    
    public String getDisplayVariant(String param1String, Locale param1Locale) {
      LocaleNameProvider localeNameProvider = getImpl(param1Locale);
      assert localeNameProvider != null;
      return localeNameProvider.getDisplayVariant(param1String, param1Locale);
    }
  }
  
  static class NumberFormatProviderDelegate extends NumberFormatProvider implements Delegate<NumberFormatProvider> {
    private ConcurrentMap<Locale, NumberFormatProvider> map = new ConcurrentHashMap();
    
    public void addImpl(NumberFormatProvider param1NumberFormatProvider) {
      for (Locale locale : param1NumberFormatProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1NumberFormatProvider); 
    }
    
    public NumberFormatProvider getImpl(Locale param1Locale) { return (NumberFormatProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public NumberFormat getCurrencyInstance(Locale param1Locale) {
      NumberFormatProvider numberFormatProvider = getImpl(param1Locale);
      assert numberFormatProvider != null;
      return numberFormatProvider.getCurrencyInstance(param1Locale);
    }
    
    public NumberFormat getIntegerInstance(Locale param1Locale) {
      NumberFormatProvider numberFormatProvider = getImpl(param1Locale);
      assert numberFormatProvider != null;
      return numberFormatProvider.getIntegerInstance(param1Locale);
    }
    
    public NumberFormat getNumberInstance(Locale param1Locale) {
      NumberFormatProvider numberFormatProvider = getImpl(param1Locale);
      assert numberFormatProvider != null;
      return numberFormatProvider.getNumberInstance(param1Locale);
    }
    
    public NumberFormat getPercentInstance(Locale param1Locale) {
      NumberFormatProvider numberFormatProvider = getImpl(param1Locale);
      assert numberFormatProvider != null;
      return numberFormatProvider.getPercentInstance(param1Locale);
    }
  }
  
  static class TimeZoneNameProviderDelegate extends TimeZoneNameProvider implements Delegate<TimeZoneNameProvider> {
    private ConcurrentMap<Locale, TimeZoneNameProvider> map = new ConcurrentHashMap();
    
    public void addImpl(TimeZoneNameProvider param1TimeZoneNameProvider) {
      for (Locale locale : param1TimeZoneNameProvider.getAvailableLocales())
        this.map.putIfAbsent(locale, param1TimeZoneNameProvider); 
    }
    
    public TimeZoneNameProvider getImpl(Locale param1Locale) { return (TimeZoneNameProvider)SPILocaleProviderAdapter.getImpl(this.map, param1Locale); }
    
    public Locale[] getAvailableLocales() { return (Locale[])this.map.keySet().toArray(new Locale[0]); }
    
    public boolean isSupportedLocale(Locale param1Locale) { return this.map.containsKey(param1Locale); }
    
    public String getDisplayName(String param1String, boolean param1Boolean, int param1Int, Locale param1Locale) {
      TimeZoneNameProvider timeZoneNameProvider = getImpl(param1Locale);
      assert timeZoneNameProvider != null;
      return timeZoneNameProvider.getDisplayName(param1String, param1Boolean, param1Int, param1Locale);
    }
    
    public String getGenericDisplayName(String param1String, int param1Int, Locale param1Locale) {
      TimeZoneNameProvider timeZoneNameProvider = getImpl(param1Locale);
      assert timeZoneNameProvider != null;
      return timeZoneNameProvider.getGenericDisplayName(param1String, param1Int, param1Locale);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\SPILocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */