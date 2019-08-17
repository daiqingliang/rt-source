package sun.util.locale.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.LocaleServiceProvider;
import sun.util.logging.PlatformLogger;

public final class LocaleServiceProviderPool {
  private static ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProviderPool> poolOfPools = new ConcurrentHashMap();
  
  private ConcurrentMap<LocaleProviderAdapter.Type, LocaleServiceProvider> providers = new ConcurrentHashMap();
  
  private ConcurrentMap<Locale, List<LocaleProviderAdapter.Type>> providersCache = new ConcurrentHashMap();
  
  private Set<Locale> availableLocales = null;
  
  private Class<? extends LocaleServiceProvider> providerClass;
  
  static final Class<LocaleServiceProvider>[] spiClasses;
  
  private static List<LocaleProviderAdapter.Type> NULL_LIST;
  
  public static LocaleServiceProviderPool getPool(Class<? extends LocaleServiceProvider> paramClass) {
    LocaleServiceProviderPool localeServiceProviderPool = (LocaleServiceProviderPool)poolOfPools.get(paramClass);
    if (localeServiceProviderPool == null) {
      LocaleServiceProviderPool localeServiceProviderPool1 = new LocaleServiceProviderPool(paramClass);
      localeServiceProviderPool = (LocaleServiceProviderPool)poolOfPools.putIfAbsent(paramClass, localeServiceProviderPool1);
      if (localeServiceProviderPool == null)
        localeServiceProviderPool = localeServiceProviderPool1; 
    } 
    return localeServiceProviderPool;
  }
  
  private LocaleServiceProviderPool(Class<? extends LocaleServiceProvider> paramClass) {
    this.providerClass = paramClass;
    for (LocaleProviderAdapter.Type type : LocaleProviderAdapter.getAdapterPreference()) {
      LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.forType(type);
      if (localeProviderAdapter != null) {
        LocaleServiceProvider localeServiceProvider = localeProviderAdapter.getLocaleServiceProvider(paramClass);
        if (localeServiceProvider != null)
          this.providers.putIfAbsent(type, localeServiceProvider); 
      } 
    } 
  }
  
  static void config(Class<? extends Object> paramClass, String paramString) {
    PlatformLogger platformLogger = PlatformLogger.getLogger(paramClass.getCanonicalName());
    platformLogger.config(paramString);
  }
  
  public static Locale[] getAllAvailableLocales() { return (Locale[])AllAvailableLocales.allAvailableLocales.clone(); }
  
  public Locale[] getAvailableLocales() {
    HashSet hashSet = new HashSet();
    hashSet.addAll(getAvailableLocaleSet());
    hashSet.addAll(Arrays.asList(LocaleProviderAdapter.forJRE().getAvailableLocales()));
    Locale[] arrayOfLocale = new Locale[hashSet.size()];
    hashSet.toArray(arrayOfLocale);
    return arrayOfLocale;
  }
  
  private Set<Locale> getAvailableLocaleSet() {
    if (this.availableLocales == null) {
      this.availableLocales = new HashSet();
      for (LocaleServiceProvider localeServiceProvider : this.providers.values()) {
        Locale[] arrayOfLocale = localeServiceProvider.getAvailableLocales();
        for (Locale locale : arrayOfLocale)
          this.availableLocales.add(getLookupLocale(locale)); 
      } 
    } 
    return this.availableLocales;
  }
  
  boolean hasProviders() { return (this.providers.size() != 1 || (this.providers.get(LocaleProviderAdapter.Type.JRE) == null && this.providers.get(LocaleProviderAdapter.Type.FALLBACK) == null)); }
  
  public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, Object... paramVarArgs) { return (S)getLocalizedObjectImpl(paramLocalizedObjectGetter, paramLocale, true, null, paramVarArgs); }
  
  public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, String paramString, Object... paramVarArgs) { return (S)getLocalizedObjectImpl(paramLocalizedObjectGetter, paramLocale, false, paramString, paramVarArgs); }
  
  private <P extends LocaleServiceProvider, S> S getLocalizedObjectImpl(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, boolean paramBoolean, String paramString, Object... paramVarArgs) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    if (!hasProviders())
      return (S)paramLocalizedObjectGetter.getObject((LocaleServiceProvider)this.providers.get(LocaleProviderAdapter.defaultLocaleProviderAdapter), paramLocale, paramString, paramVarArgs); 
    List list = getLookupLocales(paramLocale);
    Set set = getAvailableLocaleSet();
    for (Locale locale : list) {
      if (set.contains(locale))
        for (LocaleProviderAdapter.Type type : findProviders(locale)) {
          LocaleServiceProvider localeServiceProvider = (LocaleServiceProvider)this.providers.get(type);
          Object object = paramLocalizedObjectGetter.getObject(localeServiceProvider, paramLocale, paramString, paramVarArgs);
          if (object != null)
            return (S)object; 
          if (paramBoolean)
            config(LocaleServiceProviderPool.class, "A locale sensitive service provider returned null for a localized objects,  which should not happen.  provider: " + localeServiceProvider + " locale: " + paramLocale); 
        }  
    } 
    return null;
  }
  
  private List<LocaleProviderAdapter.Type> findProviders(Locale paramLocale) {
    List list = (List)this.providersCache.get(paramLocale);
    if (list == null) {
      for (LocaleProviderAdapter.Type type : LocaleProviderAdapter.getAdapterPreference()) {
        LocaleServiceProvider localeServiceProvider = (LocaleServiceProvider)this.providers.get(type);
        if (localeServiceProvider != null && localeServiceProvider.isSupportedLocale(paramLocale)) {
          if (list == null)
            list = new ArrayList(2); 
          list.add(type);
        } 
      } 
      if (list == null)
        list = NULL_LIST; 
      List list1 = (List)this.providersCache.putIfAbsent(paramLocale, list);
      if (list1 != null)
        list = list1; 
    } 
    return list;
  }
  
  static List<Locale> getLookupLocales(Locale paramLocale) { return ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", paramLocale); }
  
  static Locale getLookupLocale(Locale paramLocale) {
    Locale locale = paramLocale;
    if (paramLocale.hasExtensions() && !paramLocale.equals(JRELocaleConstants.JA_JP_JP) && !paramLocale.equals(JRELocaleConstants.TH_TH_TH)) {
      Locale.Builder builder = new Locale.Builder();
      try {
        builder.setLocale(paramLocale);
        builder.clearExtensions();
        locale = builder.build();
      } catch (IllformedLocaleException illformedLocaleException) {
        config(LocaleServiceProviderPool.class, "A locale(" + paramLocale + ") has non-empty extensions, but has illformed fields.");
        locale = new Locale(paramLocale.getLanguage(), paramLocale.getCountry(), paramLocale.getVariant());
      } 
    } 
    return locale;
  }
  
  static  {
    new Class[10][0] = java.text.spi.BreakIteratorProvider.class;
    new Class[10][1] = java.text.spi.CollatorProvider.class;
    new Class[10][2] = java.text.spi.DateFormatProvider.class;
    new Class[10][3] = java.text.spi.DateFormatSymbolsProvider.class;
    new Class[10][4] = java.text.spi.DecimalFormatSymbolsProvider.class;
    new Class[10][5] = java.text.spi.NumberFormatProvider.class;
    new Class[10][6] = java.util.spi.CurrencyNameProvider.class;
    new Class[10][7] = java.util.spi.LocaleNameProvider.class;
    new Class[10][8] = java.util.spi.TimeZoneNameProvider.class;
    new Class[10][9] = java.util.spi.CalendarDataProvider.class;
    spiClasses = (Class[])new Class[10];
    NULL_LIST = Collections.emptyList();
  }
  
  private static class AllAvailableLocales {
    static final Locale[] allAvailableLocales;
    
    static  {
      HashSet hashSet = new HashSet();
      for (Class clazz : LocaleServiceProviderPool.spiClasses) {
        LocaleServiceProviderPool localeServiceProviderPool;
        hashSet.addAll(localeServiceProviderPool.getAvailableLocaleSet());
      } 
      allAvailableLocales = (Locale[])hashSet.toArray(new Locale[0]);
    }
  }
  
  public static interface LocalizedObjectGetter<P extends LocaleServiceProvider, S> {
    S getObject(P param1P, Locale param1Locale, String param1String, Object... param1VarArgs);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\LocaleServiceProviderPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */