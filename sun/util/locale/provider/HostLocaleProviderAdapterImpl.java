package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import sun.util.spi.CalendarProvider;

public class HostLocaleProviderAdapterImpl {
  private static final int CAT_DISPLAY = 0;
  
  private static final int CAT_FORMAT = 1;
  
  private static final int NF_NUMBER = 0;
  
  private static final int NF_CURRENCY = 1;
  
  private static final int NF_PERCENT = 2;
  
  private static final int NF_INTEGER = 3;
  
  private static final int NF_MAX = 3;
  
  private static final int CD_FIRSTDAYOFWEEK = 0;
  
  private static final int CD_MINIMALDAYSINFIRSTWEEK = 1;
  
  private static final int DN_CURRENCY_NAME = 0;
  
  private static final int DN_CURRENCY_SYMBOL = 1;
  
  private static final int DN_LOCALE_LANGUAGE = 2;
  
  private static final int DN_LOCALE_SCRIPT = 3;
  
  private static final int DN_LOCALE_REGION = 4;
  
  private static final int DN_LOCALE_VARIANT = 5;
  
  private static final String[] calIDToLDML = { 
      "", "gregory", "gregory_en-US", "japanese", "roc", "", "islamic", "buddhist", "hebrew", "gregory_fr", 
      "gregory_ar", "gregory_en", "gregory_fr" };
  
  private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> dateFormatCache = new ConcurrentHashMap();
  
  private static ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> dateFormatSymbolsCache = new ConcurrentHashMap();
  
  private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> numberFormatCache = new ConcurrentHashMap();
  
  private static ConcurrentMap<Locale, SoftReference<DecimalFormatSymbols>> decimalFormatSymbolsCache = new ConcurrentHashMap();
  
  private static final Set<Locale> supportedLocaleSet;
  
  private static final String nativeDisplayLanguage;
  
  private static final Locale[] supportedLocale;
  
  public static DateFormatProvider getDateFormatProvider() { return new DateFormatProvider() {
        public Locale[] getAvailableLocales() { return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales(); }
        
        public boolean isSupportedLocale(Locale param1Locale) { return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(param1Locale); }
        
        public DateFormat getDateInstance(int param1Int, Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getDateTimePatterns(param1Locale);
          return new SimpleDateFormat((String)atomicReferenceArray.get(param1Int / 2), HostLocaleProviderAdapterImpl.getCalendarLocale(param1Locale));
        }
        
        public DateFormat getTimeInstance(int param1Int, Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getDateTimePatterns(param1Locale);
          return new SimpleDateFormat((String)atomicReferenceArray.get(param1Int / 2 + 2), HostLocaleProviderAdapterImpl.getCalendarLocale(param1Locale));
        }
        
        public DateFormat getDateTimeInstance(int param1Int1, int param1Int2, Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getDateTimePatterns(param1Locale);
          String str = (String)atomicReferenceArray.get(param1Int1 / 2) + " " + (String)atomicReferenceArray.get(param1Int2 / 2 + 2);
          return new SimpleDateFormat(str, HostLocaleProviderAdapterImpl.getCalendarLocale(param1Locale));
        }
        
        private AtomicReferenceArray<String> getDateTimePatterns(Locale param1Locale) {
          SoftReference softReference = (SoftReference)dateFormatCache.get(param1Locale);
          AtomicReferenceArray atomicReferenceArray;
          if (softReference == null || (atomicReferenceArray = (AtomicReferenceArray)softReference.get()) == null) {
            String str = HostLocaleProviderAdapterImpl.removeExtensions(param1Locale).toLanguageTag();
            atomicReferenceArray = new AtomicReferenceArray(4);
            atomicReferenceArray.compareAndSet(0, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(1, -1, str)));
            atomicReferenceArray.compareAndSet(1, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(3, -1, str)));
            atomicReferenceArray.compareAndSet(2, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(-1, 1, str)));
            atomicReferenceArray.compareAndSet(3, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(-1, 3, str)));
            softReference = new SoftReference(atomicReferenceArray);
            dateFormatCache.put(param1Locale, softReference);
          } 
          return atomicReferenceArray;
        }
      }; }
  
  public static DateFormatSymbolsProvider getDateFormatSymbolsProvider() { return new DateFormatSymbolsProvider() {
        public Locale[] getAvailableLocales() { return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales(); }
        
        public boolean isSupportedLocale(Locale param1Locale) { return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(param1Locale); }
        
        public DateFormatSymbols getInstance(Locale param1Locale) {
          SoftReference softReference = (SoftReference)dateFormatSymbolsCache.get(param1Locale);
          DateFormatSymbols dateFormatSymbols;
          if (softReference == null || (dateFormatSymbols = (DateFormatSymbols)softReference.get()) == null) {
            dateFormatSymbols = new DateFormatSymbols(param1Locale);
            String str = HostLocaleProviderAdapterImpl.removeExtensions(param1Locale).toLanguageTag();
            dateFormatSymbols.setAmPmStrings(HostLocaleProviderAdapterImpl.getAmPmStrings(str, dateFormatSymbols.getAmPmStrings()));
            dateFormatSymbols.setEras(HostLocaleProviderAdapterImpl.getEras(str, dateFormatSymbols.getEras()));
            dateFormatSymbols.setMonths(HostLocaleProviderAdapterImpl.getMonths(str, dateFormatSymbols.getMonths()));
            dateFormatSymbols.setShortMonths(HostLocaleProviderAdapterImpl.getShortMonths(str, dateFormatSymbols.getShortMonths()));
            dateFormatSymbols.setWeekdays(HostLocaleProviderAdapterImpl.getWeekdays(str, dateFormatSymbols.getWeekdays()));
            dateFormatSymbols.setShortWeekdays(HostLocaleProviderAdapterImpl.getShortWeekdays(str, dateFormatSymbols.getShortWeekdays()));
            softReference = new SoftReference(dateFormatSymbols);
            dateFormatSymbolsCache.put(param1Locale, softReference);
          } 
          return (DateFormatSymbols)dateFormatSymbols.clone();
        }
      }; }
  
  public static NumberFormatProvider getNumberFormatProvider() { return new NumberFormatProvider() {
        public Locale[] getAvailableLocales() { return HostLocaleProviderAdapterImpl.getSupportedNativeDigitLocales(); }
        
        public boolean isSupportedLocale(Locale param1Locale) { return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(param1Locale); }
        
        public NumberFormat getCurrencyInstance(Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getNumberPatterns(param1Locale);
          return new DecimalFormat((String)atomicReferenceArray.get(1), DecimalFormatSymbols.getInstance(param1Locale));
        }
        
        public NumberFormat getIntegerInstance(Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getNumberPatterns(param1Locale);
          return new DecimalFormat((String)atomicReferenceArray.get(3), DecimalFormatSymbols.getInstance(param1Locale));
        }
        
        public NumberFormat getNumberInstance(Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getNumberPatterns(param1Locale);
          return new DecimalFormat((String)atomicReferenceArray.get(0), DecimalFormatSymbols.getInstance(param1Locale));
        }
        
        public NumberFormat getPercentInstance(Locale param1Locale) {
          AtomicReferenceArray atomicReferenceArray = getNumberPatterns(param1Locale);
          return new DecimalFormat((String)atomicReferenceArray.get(2), DecimalFormatSymbols.getInstance(param1Locale));
        }
        
        private AtomicReferenceArray<String> getNumberPatterns(Locale param1Locale) {
          SoftReference softReference = (SoftReference)numberFormatCache.get(param1Locale);
          AtomicReferenceArray atomicReferenceArray;
          if (softReference == null || (atomicReferenceArray = (AtomicReferenceArray)softReference.get()) == null) {
            String str = param1Locale.toLanguageTag();
            atomicReferenceArray = new AtomicReferenceArray(4);
            for (byte b = 0; b <= 3; b++)
              atomicReferenceArray.compareAndSet(b, null, HostLocaleProviderAdapterImpl.getNumberPattern(b, str)); 
            softReference = new SoftReference(atomicReferenceArray);
            numberFormatCache.put(param1Locale, softReference);
          } 
          return atomicReferenceArray;
        }
      }; }
  
  public static DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() { return new DecimalFormatSymbolsProvider() {
        public Locale[] getAvailableLocales() { return HostLocaleProviderAdapterImpl.getSupportedNativeDigitLocales(); }
        
        public boolean isSupportedLocale(Locale param1Locale) { return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(param1Locale); }
        
        public DecimalFormatSymbols getInstance(Locale param1Locale) {
          SoftReference softReference = (SoftReference)decimalFormatSymbolsCache.get(param1Locale);
          DecimalFormatSymbols decimalFormatSymbols;
          if (softReference == null || (decimalFormatSymbols = (DecimalFormatSymbols)softReference.get()) == null) {
            decimalFormatSymbols = new DecimalFormatSymbols(HostLocaleProviderAdapterImpl.getNumberLocale(param1Locale));
            String str = HostLocaleProviderAdapterImpl.removeExtensions(param1Locale).toLanguageTag();
            decimalFormatSymbols.setInternationalCurrencySymbol(HostLocaleProviderAdapterImpl.getInternationalCurrencySymbol(str, decimalFormatSymbols.getInternationalCurrencySymbol()));
            decimalFormatSymbols.setCurrencySymbol(HostLocaleProviderAdapterImpl.getCurrencySymbol(str, decimalFormatSymbols.getCurrencySymbol()));
            decimalFormatSymbols.setDecimalSeparator(HostLocaleProviderAdapterImpl.getDecimalSeparator(str, decimalFormatSymbols.getDecimalSeparator()));
            decimalFormatSymbols.setGroupingSeparator(HostLocaleProviderAdapterImpl.getGroupingSeparator(str, decimalFormatSymbols.getGroupingSeparator()));
            decimalFormatSymbols.setInfinity(HostLocaleProviderAdapterImpl.getInfinity(str, decimalFormatSymbols.getInfinity()));
            decimalFormatSymbols.setMinusSign(HostLocaleProviderAdapterImpl.getMinusSign(str, decimalFormatSymbols.getMinusSign()));
            decimalFormatSymbols.setMonetaryDecimalSeparator(HostLocaleProviderAdapterImpl.getMonetaryDecimalSeparator(str, decimalFormatSymbols.getMonetaryDecimalSeparator()));
            decimalFormatSymbols.setNaN(HostLocaleProviderAdapterImpl.getNaN(str, decimalFormatSymbols.getNaN()));
            decimalFormatSymbols.setPercent(HostLocaleProviderAdapterImpl.getPercent(str, decimalFormatSymbols.getPercent()));
            decimalFormatSymbols.setPerMill(HostLocaleProviderAdapterImpl.getPerMill(str, decimalFormatSymbols.getPerMill()));
            decimalFormatSymbols.setZeroDigit(HostLocaleProviderAdapterImpl.getZeroDigit(str, decimalFormatSymbols.getZeroDigit()));
            softReference = new SoftReference(decimalFormatSymbols);
            decimalFormatSymbolsCache.put(param1Locale, softReference);
          } 
          return (DecimalFormatSymbols)decimalFormatSymbols.clone();
        }
      }; }
  
  public static CalendarDataProvider getCalendarDataProvider() { return new CalendarDataProvider() {
        public Locale[] getAvailableLocales() { return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales(); }
        
        public boolean isSupportedLocale(Locale param1Locale) { return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(param1Locale); }
        
        public int getFirstDayOfWeek(Locale param1Locale) {
          int i = HostLocaleProviderAdapterImpl.getCalendarDataValue(HostLocaleProviderAdapterImpl.removeExtensions(param1Locale).toLanguageTag(), 0);
          return (i != -1) ? ((i + 1) % 7 + 1) : 0;
        }
        
        public int getMinimalDaysInFirstWeek(Locale param1Locale) { return 0; }
      }; }
  
  public static CalendarProvider getCalendarProvider() { return new CalendarProvider() {
        public Locale[] getAvailableLocales() { return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales(); }
        
        public boolean isSupportedLocale(Locale param1Locale) { return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(param1Locale); }
        
        public Calendar getInstance(TimeZone param1TimeZone, Locale param1Locale) { return (new Calendar.Builder()).setLocale(HostLocaleProviderAdapterImpl.getCalendarLocale(param1Locale)).setTimeZone(param1TimeZone).setInstant(System.currentTimeMillis()).build(); }
      }; }
  
  public static CurrencyNameProvider getCurrencyNameProvider() { return new CurrencyNameProvider() {
        public Locale[] getAvailableLocales() { return supportedLocale; }
        
        public boolean isSupportedLocale(Locale param1Locale) { return (supportedLocaleSet.contains(param1Locale.stripExtensions()) && param1Locale.getLanguage().equals(nativeDisplayLanguage)); }
        
        public String getSymbol(String param1String, Locale param1Locale) {
          try {
            if (Currency.getInstance(param1Locale).getCurrencyCode().equals(param1String))
              return HostLocaleProviderAdapterImpl.getDisplayString(param1Locale.toLanguageTag(), 1, param1String); 
          } catch (IllegalArgumentException illegalArgumentException) {}
          return null;
        }
        
        public String getDisplayName(String param1String, Locale param1Locale) {
          try {
            if (Currency.getInstance(param1Locale).getCurrencyCode().equals(param1String))
              return HostLocaleProviderAdapterImpl.getDisplayString(param1Locale.toLanguageTag(), 0, param1String); 
          } catch (IllegalArgumentException illegalArgumentException) {}
          return null;
        }
      }; }
  
  public static LocaleNameProvider getLocaleNameProvider() { return new LocaleNameProvider() {
        public Locale[] getAvailableLocales() { return supportedLocale; }
        
        public boolean isSupportedLocale(Locale param1Locale) { return (supportedLocaleSet.contains(param1Locale.stripExtensions()) && param1Locale.getLanguage().equals(nativeDisplayLanguage)); }
        
        public String getDisplayLanguage(String param1String, Locale param1Locale) { return HostLocaleProviderAdapterImpl.getDisplayString(param1Locale.toLanguageTag(), 2, param1String); }
        
        public String getDisplayCountry(String param1String, Locale param1Locale) { return HostLocaleProviderAdapterImpl.getDisplayString(param1Locale.toLanguageTag(), 4, nativeDisplayLanguage + "-" + param1String); }
        
        public String getDisplayScript(String param1String, Locale param1Locale) { return null; }
        
        public String getDisplayVariant(String param1String, Locale param1Locale) { return null; }
      }; }
  
  private static String convertDateTimePattern(String paramString) {
    null = paramString.replaceAll("dddd", "EEEE");
    null = null.replaceAll("ddd", "EEE");
    null = null.replaceAll("tt", "aa");
    return null.replaceAll("g", "GG");
  }
  
  private static Locale[] getSupportedCalendarLocales() {
    if (supportedLocale.length != 0 && supportedLocaleSet.contains(Locale.JAPAN) && isJapaneseCalendar()) {
      Locale[] arrayOfLocale = new Locale[supportedLocale.length + 1];
      arrayOfLocale[0] = JRELocaleConstants.JA_JP_JP;
      System.arraycopy(supportedLocale, 0, arrayOfLocale, 1, supportedLocale.length);
      return arrayOfLocale;
    } 
    return supportedLocale;
  }
  
  private static boolean isSupportedCalendarLocale(Locale paramLocale) {
    Locale locale = paramLocale;
    if (locale.hasExtensions() || locale.getVariant() != "")
      locale = (new Locale.Builder()).setLocale(paramLocale).clearExtensions().build(); 
    if (!supportedLocaleSet.contains(locale))
      return false; 
    int i = getCalendarID(locale.toLanguageTag());
    if (i <= 0 || i >= calIDToLDML.length)
      return false; 
    String str1 = paramLocale.getUnicodeLocaleType("ca");
    String str2 = calIDToLDML[i].replaceFirst("_.*", "");
    return (str1 == null) ? Calendar.getAvailableCalendarTypes().contains(str2) : str1.equals(str2);
  }
  
  private static Locale[] getSupportedNativeDigitLocales() {
    if (supportedLocale.length != 0 && supportedLocaleSet.contains(JRELocaleConstants.TH_TH) && isNativeDigit("th-TH")) {
      Locale[] arrayOfLocale = new Locale[supportedLocale.length + 1];
      arrayOfLocale[0] = JRELocaleConstants.TH_TH_TH;
      System.arraycopy(supportedLocale, 0, arrayOfLocale, 1, supportedLocale.length);
      return arrayOfLocale;
    } 
    return supportedLocale;
  }
  
  private static boolean isSupportedNativeDigitLocale(Locale paramLocale) {
    if (JRELocaleConstants.TH_TH_TH.equals(paramLocale))
      return isNativeDigit("th-TH"); 
    String str = null;
    Locale locale = paramLocale;
    if (paramLocale.hasExtensions()) {
      str = paramLocale.getUnicodeLocaleType("nu");
      locale = paramLocale.stripExtensions();
    } 
    if (supportedLocaleSet.contains(locale)) {
      if (str == null || str.equals("latn"))
        return true; 
      if (paramLocale.getLanguage().equals("th"))
        return ("thai".equals(str) && isNativeDigit(paramLocale.toLanguageTag())); 
    } 
    return false;
  }
  
  private static Locale removeExtensions(Locale paramLocale) { return (new Locale.Builder()).setLocale(paramLocale).clearExtensions().build(); }
  
  private static boolean isJapaneseCalendar() { return (getCalendarID("ja-JP") == 3); }
  
  private static Locale getCalendarLocale(Locale paramLocale) {
    int i = getCalendarID(paramLocale.toLanguageTag());
    if (i > 0 && i < calIDToLDML.length) {
      Locale.Builder builder = new Locale.Builder();
      String[] arrayOfString = calIDToLDML[i].split("_");
      if (arrayOfString.length > 1) {
        builder.setLocale(Locale.forLanguageTag(arrayOfString[1]));
      } else {
        builder.setLocale(paramLocale);
      } 
      builder.setUnicodeLocaleKeyword("ca", arrayOfString[0]);
      return builder.build();
    } 
    return paramLocale;
  }
  
  private static Locale getNumberLocale(Locale paramLocale) {
    if (JRELocaleConstants.TH_TH.equals(paramLocale) && isNativeDigit("th-TH")) {
      Locale.Builder builder = (new Locale.Builder()).setLocale(paramLocale);
      builder.setUnicodeLocaleKeyword("nu", "thai");
      return builder.build();
    } 
    return paramLocale;
  }
  
  private static native boolean initialize();
  
  private static native String getDefaultLocale(int paramInt);
  
  private static native String getDateTimePattern(int paramInt1, int paramInt2, String paramString);
  
  private static native int getCalendarID(String paramString);
  
  private static native String[] getAmPmStrings(String paramString, String[] paramArrayOfString);
  
  private static native String[] getEras(String paramString, String[] paramArrayOfString);
  
  private static native String[] getMonths(String paramString, String[] paramArrayOfString);
  
  private static native String[] getShortMonths(String paramString, String[] paramArrayOfString);
  
  private static native String[] getWeekdays(String paramString, String[] paramArrayOfString);
  
  private static native String[] getShortWeekdays(String paramString, String[] paramArrayOfString);
  
  private static native String getNumberPattern(int paramInt, String paramString);
  
  private static native boolean isNativeDigit(String paramString);
  
  private static native String getCurrencySymbol(String paramString1, String paramString2);
  
  private static native char getDecimalSeparator(String paramString, char paramChar);
  
  private static native char getGroupingSeparator(String paramString, char paramChar);
  
  private static native String getInfinity(String paramString1, String paramString2);
  
  private static native String getInternationalCurrencySymbol(String paramString1, String paramString2);
  
  private static native char getMinusSign(String paramString, char paramChar);
  
  private static native char getMonetaryDecimalSeparator(String paramString, char paramChar);
  
  private static native String getNaN(String paramString1, String paramString2);
  
  private static native char getPercent(String paramString, char paramChar);
  
  private static native char getPerMill(String paramString, char paramChar);
  
  private static native char getZeroDigit(String paramString, char paramChar);
  
  private static native int getCalendarDataValue(String paramString, int paramInt);
  
  private static native String getDisplayString(String paramString1, int paramInt, String paramString2);
  
  static  {
    HashSet hashSet = new HashSet();
    if (initialize()) {
      ResourceBundle.Control control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
      String str1 = getDefaultLocale(0);
      Locale locale = Locale.forLanguageTag(str1.replace('_', '-'));
      hashSet.addAll(control.getCandidateLocales("", locale));
      nativeDisplayLanguage = locale.getLanguage();
      String str2 = getDefaultLocale(1);
      if (!str2.equals(str1)) {
        locale = Locale.forLanguageTag(str2.replace('_', '-'));
        hashSet.addAll(control.getCandidateLocales("", locale));
      } 
    } else {
      nativeDisplayLanguage = "";
    } 
    supportedLocaleSet = Collections.unmodifiableSet(hashSet);
    supportedLocale = (Locale[])supportedLocaleSet.toArray(new Locale[0]);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\HostLocaleProviderAdapterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */