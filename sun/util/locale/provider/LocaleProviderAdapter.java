package sun.util.locale.provider;

import java.security.AccessController;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.cldr.CLDRLocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class LocaleProviderAdapter {
  private static final List<Type> adapterPreference;
  
  private static LocaleProviderAdapter jreLocaleProviderAdapter = new JRELocaleProviderAdapter();
  
  private static LocaleProviderAdapter spiLocaleProviderAdapter = new SPILocaleProviderAdapter();
  
  private static LocaleProviderAdapter cldrLocaleProviderAdapter = null;
  
  private static LocaleProviderAdapter hostLocaleProviderAdapter = null;
  
  private static LocaleProviderAdapter fallbackLocaleProviderAdapter = null;
  
  static Type defaultLocaleProviderAdapter = null;
  
  private static ConcurrentMap<Class<? extends LocaleServiceProvider>, ConcurrentMap<Locale, LocaleProviderAdapter>> adapterCache = new ConcurrentHashMap();
  
  public static LocaleProviderAdapter forType(Type paramType) {
    switch (paramType) {
      case JRE:
        return jreLocaleProviderAdapter;
      case CLDR:
        return cldrLocaleProviderAdapter;
      case SPI:
        return spiLocaleProviderAdapter;
      case HOST:
        return hostLocaleProviderAdapter;
      case FALLBACK:
        return fallbackLocaleProviderAdapter;
    } 
    throw new InternalError("unknown locale data adapter type");
  }
  
  public static LocaleProviderAdapter forJRE() { return jreLocaleProviderAdapter; }
  
  public static LocaleProviderAdapter getResourceBundleBased() {
    for (Type type : getAdapterPreference()) {
      if (type == Type.JRE || type == Type.CLDR || type == Type.FALLBACK)
        return forType(type); 
    } 
    throw new InternalError();
  }
  
  public static List<Type> getAdapterPreference() { return adapterPreference; }
  
  public static LocaleProviderAdapter getAdapter(Class<? extends LocaleServiceProvider> paramClass, Locale paramLocale) {
    ConcurrentMap concurrentMap = (ConcurrentMap)adapterCache.get(paramClass);
    if (concurrentMap != null) {
      LocaleProviderAdapter localeProviderAdapter1;
      if ((localeProviderAdapter1 = (LocaleProviderAdapter)concurrentMap.get(paramLocale)) != null)
        return localeProviderAdapter1; 
    } else {
      concurrentMap = new ConcurrentHashMap();
      adapterCache.putIfAbsent(paramClass, concurrentMap);
    } 
    LocaleProviderAdapter localeProviderAdapter = findAdapter(paramClass, paramLocale);
    if (localeProviderAdapter != null) {
      concurrentMap.putIfAbsent(paramLocale, localeProviderAdapter);
      return localeProviderAdapter;
    } 
    List list = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", paramLocale);
    for (Locale locale : list) {
      if (locale.equals(paramLocale))
        continue; 
      localeProviderAdapter = findAdapter(paramClass, locale);
      if (localeProviderAdapter != null) {
        concurrentMap.putIfAbsent(paramLocale, localeProviderAdapter);
        return localeProviderAdapter;
      } 
    } 
    concurrentMap.putIfAbsent(paramLocale, fallbackLocaleProviderAdapter);
    return fallbackLocaleProviderAdapter;
  }
  
  private static LocaleProviderAdapter findAdapter(Class<? extends LocaleServiceProvider> paramClass, Locale paramLocale) {
    for (Type type : getAdapterPreference()) {
      LocaleProviderAdapter localeProviderAdapter = forType(type);
      LocaleServiceProvider localeServiceProvider = localeProviderAdapter.getLocaleServiceProvider(paramClass);
      if (localeServiceProvider != null && localeServiceProvider.isSupportedLocale(paramLocale))
        return localeProviderAdapter; 
    } 
    return null;
  }
  
  public static boolean isSupportedLocale(Locale paramLocale, Type paramType, Set<String> paramSet) {
    assert paramType == Type.JRE || paramType == Type.CLDR || paramType == Type.FALLBACK;
    if (Locale.ROOT.equals(paramLocale))
      return true; 
    if (paramType == Type.FALLBACK)
      return false; 
    paramLocale = paramLocale.stripExtensions();
    if (paramSet.contains(paramLocale.toLanguageTag()))
      return true; 
    if (paramType == Type.JRE) {
      String str = paramLocale.toString().replace('_', '-');
      return (paramSet.contains(str) || "ja-JP-JP".equals(str) || "th-TH-TH".equals(str) || "no-NO-NY".equals(str));
    } 
    return false;
  }
  
  public static Locale[] toLocaleArray(Set<String> paramSet) {
    Locale[] arrayOfLocale = new Locale[paramSet.size() + 1];
    byte b = 0;
    arrayOfLocale[b++] = Locale.ROOT;
    for (String str : paramSet) {
      switch (str) {
        case "ja-JP-JP":
          arrayOfLocale[b++] = JRELocaleConstants.JA_JP_JP;
          continue;
        case "th-TH-TH":
          arrayOfLocale[b++] = JRELocaleConstants.TH_TH_TH;
          continue;
      } 
      arrayOfLocale[b++] = Locale.forLanguageTag(str);
    } 
    return arrayOfLocale;
  }
  
  public abstract Type getAdapterType();
  
  public abstract <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass);
  
  public abstract BreakIteratorProvider getBreakIteratorProvider();
  
  public abstract CollatorProvider getCollatorProvider();
  
  public abstract DateFormatProvider getDateFormatProvider();
  
  public abstract DateFormatSymbolsProvider getDateFormatSymbolsProvider();
  
  public abstract DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider();
  
  public abstract NumberFormatProvider getNumberFormatProvider();
  
  public abstract CurrencyNameProvider getCurrencyNameProvider();
  
  public abstract LocaleNameProvider getLocaleNameProvider();
  
  public abstract TimeZoneNameProvider getTimeZoneNameProvider();
  
  public abstract CalendarDataProvider getCalendarDataProvider();
  
  public abstract CalendarNameProvider getCalendarNameProvider();
  
  public abstract CalendarProvider getCalendarProvider();
  
  public abstract LocaleResources getLocaleResources(Locale paramLocale);
  
  public abstract Locale[] getAvailableLocales();
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.locale.providers"));
    ArrayList arrayList = new ArrayList();
    if (str != null && str.length() != 0) {
      String[] arrayOfString = str.split(",");
      for (String str1 : arrayOfString) {
        try {
          Type type = Type.valueOf(str1.trim().toUpperCase(Locale.ROOT));
          switch (type) {
            case CLDR:
              if (cldrLocaleProviderAdapter == null)
                cldrLocaleProviderAdapter = new CLDRLocaleProviderAdapter(); 
              break;
            case HOST:
              if (hostLocaleProviderAdapter == null)
                hostLocaleProviderAdapter = new HostLocaleProviderAdapter(); 
              break;
          } 
          if (!arrayList.contains(type))
            arrayList.add(type); 
        } catch (IllegalArgumentException|UnsupportedOperationException illegalArgumentException) {
          LocaleServiceProviderPool.config(LocaleProviderAdapter.class, illegalArgumentException.toString());
        } 
      } 
    } 
    if (!arrayList.isEmpty()) {
      if (!arrayList.contains(Type.JRE)) {
        fallbackLocaleProviderAdapter = new FallbackLocaleProviderAdapter();
        arrayList.add(Type.FALLBACK);
        defaultLocaleProviderAdapter = Type.FALLBACK;
      } else {
        defaultLocaleProviderAdapter = Type.JRE;
      } 
    } else {
      arrayList.add(Type.JRE);
      arrayList.add(Type.SPI);
      defaultLocaleProviderAdapter = Type.JRE;
    } 
    adapterPreference = Collections.unmodifiableList(arrayList);
  }
  
  public enum Type {
    JRE("sun.util.resources", "sun.text.resources"),
    CLDR("sun.util.resources.cldr", "sun.text.resources.cldr"),
    SPI,
    HOST,
    FALLBACK("sun.util.resources", "sun.text.resources");
    
    private final String UTIL_RESOURCES_PACKAGE;
    
    private final String TEXT_RESOURCES_PACKAGE;
    
    Type(String param1String1, String param1String2) {
      this.UTIL_RESOURCES_PACKAGE = param1String1;
      this.TEXT_RESOURCES_PACKAGE = param1String2;
    }
    
    public String getUtilResourcesPackage() { return this.UTIL_RESOURCES_PACKAGE; }
    
    public String getTextResourcesPackage() { return this.TEXT_RESOURCES_PACKAGE; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\LocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */