package sun.util.locale.provider;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.resources.LocaleData;
import sun.util.spi.CalendarProvider;

public class JRELocaleProviderAdapter extends LocaleProviderAdapter implements ResourceBundleBasedAdapter {
  private static final String LOCALE_DATA_JAR_NAME = "localedata.jar";
  
  private final ConcurrentMap<String, Set<String>> langtagSets = new ConcurrentHashMap();
  
  private final ConcurrentMap<Locale, LocaleResources> localeResourcesMap = new ConcurrentHashMap();
  
  public LocaleProviderAdapter.Type getAdapterType() { return LocaleProviderAdapter.Type.JRE; }
  
  public <P extends java.util.spi.LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass) {
    switch (paramClass.getSimpleName()) {
      case "BreakIteratorProvider":
        return (P)getBreakIteratorProvider();
      case "CollatorProvider":
        return (P)getCollatorProvider();
      case "DateFormatProvider":
        return (P)getDateFormatProvider();
      case "DateFormatSymbolsProvider":
        return (P)getDateFormatSymbolsProvider();
      case "DecimalFormatSymbolsProvider":
        return (P)getDecimalFormatSymbolsProvider();
      case "NumberFormatProvider":
        return (P)getNumberFormatProvider();
      case "CurrencyNameProvider":
        return (P)getCurrencyNameProvider();
      case "LocaleNameProvider":
        return (P)getLocaleNameProvider();
      case "TimeZoneNameProvider":
        return (P)getTimeZoneNameProvider();
      case "CalendarDataProvider":
        return (P)getCalendarDataProvider();
      case "CalendarNameProvider":
        return (P)getCalendarNameProvider();
      case "CalendarProvider":
        return (P)getCalendarProvider();
    } 
    throw new InternalError("should not come down here");
  }
  
  public BreakIteratorProvider getBreakIteratorProvider() {
    if (this.breakIteratorProvider == null) {
      BreakIteratorProviderImpl breakIteratorProviderImpl = new BreakIteratorProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this) {
        if (this.breakIteratorProvider == null)
          this.breakIteratorProvider = breakIteratorProviderImpl; 
      } 
    } 
    return this.breakIteratorProvider;
  }
  
  public CollatorProvider getCollatorProvider() {
    if (this.collatorProvider == null) {
      CollatorProviderImpl collatorProviderImpl = new CollatorProviderImpl(getAdapterType(), getLanguageTagSet("CollationData"));
      synchronized (this) {
        if (this.collatorProvider == null)
          this.collatorProvider = collatorProviderImpl; 
      } 
    } 
    return this.collatorProvider;
  }
  
  public DateFormatProvider getDateFormatProvider() {
    if (this.dateFormatProvider == null) {
      DateFormatProviderImpl dateFormatProviderImpl = new DateFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this) {
        if (this.dateFormatProvider == null)
          this.dateFormatProvider = dateFormatProviderImpl; 
      } 
    } 
    return this.dateFormatProvider;
  }
  
  public DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
    if (this.dateFormatSymbolsProvider == null) {
      DateFormatSymbolsProviderImpl dateFormatSymbolsProviderImpl = new DateFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this) {
        if (this.dateFormatSymbolsProvider == null)
          this.dateFormatSymbolsProvider = dateFormatSymbolsProviderImpl; 
      } 
    } 
    return this.dateFormatSymbolsProvider;
  }
  
  public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
    if (this.decimalFormatSymbolsProvider == null) {
      DecimalFormatSymbolsProviderImpl decimalFormatSymbolsProviderImpl = new DecimalFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this) {
        if (this.decimalFormatSymbolsProvider == null)
          this.decimalFormatSymbolsProvider = decimalFormatSymbolsProviderImpl; 
      } 
    } 
    return this.decimalFormatSymbolsProvider;
  }
  
  public NumberFormatProvider getNumberFormatProvider() {
    if (this.numberFormatProvider == null) {
      NumberFormatProviderImpl numberFormatProviderImpl = new NumberFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this) {
        if (this.numberFormatProvider == null)
          this.numberFormatProvider = numberFormatProviderImpl; 
      } 
    } 
    return this.numberFormatProvider;
  }
  
  public CurrencyNameProvider getCurrencyNameProvider() {
    if (this.currencyNameProvider == null) {
      CurrencyNameProviderImpl currencyNameProviderImpl = new CurrencyNameProviderImpl(getAdapterType(), getLanguageTagSet("CurrencyNames"));
      synchronized (this) {
        if (this.currencyNameProvider == null)
          this.currencyNameProvider = currencyNameProviderImpl; 
      } 
    } 
    return this.currencyNameProvider;
  }
  
  public LocaleNameProvider getLocaleNameProvider() {
    if (this.localeNameProvider == null) {
      LocaleNameProviderImpl localeNameProviderImpl = new LocaleNameProviderImpl(getAdapterType(), getLanguageTagSet("LocaleNames"));
      synchronized (this) {
        if (this.localeNameProvider == null)
          this.localeNameProvider = localeNameProviderImpl; 
      } 
    } 
    return this.localeNameProvider;
  }
  
  public TimeZoneNameProvider getTimeZoneNameProvider() {
    if (this.timeZoneNameProvider == null) {
      TimeZoneNameProviderImpl timeZoneNameProviderImpl = new TimeZoneNameProviderImpl(getAdapterType(), getLanguageTagSet("TimeZoneNames"));
      synchronized (this) {
        if (this.timeZoneNameProvider == null)
          this.timeZoneNameProvider = timeZoneNameProviderImpl; 
      } 
    } 
    return this.timeZoneNameProvider;
  }
  
  public CalendarDataProvider getCalendarDataProvider() {
    if (this.calendarDataProvider == null) {
      CalendarDataProviderImpl calendarDataProviderImpl = new CalendarDataProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
      synchronized (this) {
        if (this.calendarDataProvider == null)
          this.calendarDataProvider = calendarDataProviderImpl; 
      } 
    } 
    return this.calendarDataProvider;
  }
  
  public CalendarNameProvider getCalendarNameProvider() {
    if (this.calendarNameProvider == null) {
      CalendarNameProviderImpl calendarNameProviderImpl = new CalendarNameProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this) {
        if (this.calendarNameProvider == null)
          this.calendarNameProvider = calendarNameProviderImpl; 
      } 
    } 
    return this.calendarNameProvider;
  }
  
  public CalendarProvider getCalendarProvider() {
    if (this.calendarProvider == null) {
      CalendarProviderImpl calendarProviderImpl = new CalendarProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
      synchronized (this) {
        if (this.calendarProvider == null)
          this.calendarProvider = calendarProviderImpl; 
      } 
    } 
    return this.calendarProvider;
  }
  
  public LocaleResources getLocaleResources(Locale paramLocale) {
    LocaleResources localeResources = (LocaleResources)this.localeResourcesMap.get(paramLocale);
    if (localeResources == null) {
      localeResources = new LocaleResources(this, paramLocale);
      LocaleResources localeResources1 = (LocaleResources)this.localeResourcesMap.putIfAbsent(paramLocale, localeResources);
      if (localeResources1 != null)
        localeResources = localeResources1; 
    } 
    return localeResources;
  }
  
  public LocaleData getLocaleData() {
    if (this.localeData == null)
      synchronized (this) {
        if (this.localeData == null)
          this.localeData = new LocaleData(getAdapterType()); 
      }  
    return this.localeData;
  }
  
  public Locale[] getAvailableLocales() { return (Locale[])localeList.clone(); }
  
  public Set<String> getLanguageTagSet(String paramString) {
    Set set = (Set)this.langtagSets.get(paramString);
    if (set == null) {
      set = createLanguageTagSet(paramString);
      Set set1 = (Set)this.langtagSets.putIfAbsent(paramString, set);
      if (set1 != null)
        set = set1; 
    } 
    return set;
  }
  
  protected Set<String> createLanguageTagSet(String paramString) {
    String str = LocaleDataMetaInfo.getSupportedLocaleString(paramString);
    if (str == null)
      return Collections.emptySet(); 
    HashSet hashSet = new HashSet();
    StringTokenizer stringTokenizer = new StringTokenizer(str);
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken();
      if (str1.equals("|")) {
        if (isNonENLangSupported())
          continue; 
        break;
      } 
      hashSet.add(str1);
    } 
    return hashSet;
  }
  
  private static Locale[] createAvailableLocales() {
    StringTokenizer stringTokenizer;
    String str = LocaleDataMetaInfo.getSupportedLocaleString("AvailableLocales");
    if (str.length() == 0)
      throw new InternalError("No available locales for JRE"); 
    int i = str.indexOf('|');
    if (isNonENLangSupported()) {
      stringTokenizer = new StringTokenizer(str.substring(0, i) + str.substring(i + 1));
    } else {
      stringTokenizer = new StringTokenizer(str.substring(0, i));
    } 
    int j = stringTokenizer.countTokens();
    Locale[] arrayOfLocale = new Locale[j + 1];
    arrayOfLocale[0] = Locale.ROOT;
    for (byte b = 1; b <= j; b++) {
      String str1 = stringTokenizer.nextToken();
      switch (str1) {
        case "ja-JP-JP":
          arrayOfLocale[b] = JRELocaleConstants.JA_JP_JP;
          break;
        case "no-NO-NY":
          arrayOfLocale[b] = JRELocaleConstants.NO_NO_NY;
          break;
        case "th-TH-TH":
          arrayOfLocale[b] = JRELocaleConstants.TH_TH_TH;
          break;
        default:
          arrayOfLocale[b] = Locale.forLanguageTag(str1);
          break;
      } 
    } 
    return arrayOfLocale;
  }
  
  private static boolean isNonENLangSupported() {
    if (isNonENSupported == null)
      synchronized (JRELocaleProviderAdapter.class) {
        if (isNonENSupported == null) {
          String str1 = File.separator;
          String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "localedata.jar";
          final File f = new File(str2);
          isNonENSupported = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                public Boolean run() { return Boolean.valueOf(f.exists()); }
              });
        } 
      }  
    return isNonENSupported.booleanValue();
  }
  
  private static class AvailableJRELocales {
    private static final Locale[] localeList = JRELocaleProviderAdapter.createAvailableLocales();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\JRELocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */