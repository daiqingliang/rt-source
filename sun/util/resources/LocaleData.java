package sun.util.resources;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter;

public class LocaleData {
  private final LocaleProviderAdapter.Type type;
  
  public LocaleData(LocaleProviderAdapter.Type paramType) { this.type = paramType; }
  
  public ResourceBundle getCalendarData(Locale paramLocale) { return getBundle(this.type.getUtilResourcesPackage() + ".CalendarData", paramLocale); }
  
  public OpenListResourceBundle getCurrencyNames(Locale paramLocale) { return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".CurrencyNames", paramLocale); }
  
  public OpenListResourceBundle getLocaleNames(Locale paramLocale) { return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".LocaleNames", paramLocale); }
  
  public TimeZoneNamesBundle getTimeZoneNames(Locale paramLocale) { return (TimeZoneNamesBundle)getBundle(this.type.getUtilResourcesPackage() + ".TimeZoneNames", paramLocale); }
  
  public ResourceBundle getBreakIteratorInfo(Locale paramLocale) { return getBundle(this.type.getTextResourcesPackage() + ".BreakIteratorInfo", paramLocale); }
  
  public ResourceBundle getCollationData(Locale paramLocale) { return getBundle(this.type.getTextResourcesPackage() + ".CollationData", paramLocale); }
  
  public ResourceBundle getDateFormatData(Locale paramLocale) { return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale); }
  
  public void setSupplementary(ParallelListResourceBundle paramParallelListResourceBundle) {
    if (!paramParallelListResourceBundle.areParallelContentsComplete()) {
      String str = this.type.getTextResourcesPackage() + ".JavaTimeSupplementary";
      setSupplementary(str, paramParallelListResourceBundle);
    } 
  }
  
  private boolean setSupplementary(String paramString, ParallelListResourceBundle paramParallelListResourceBundle) {
    ParallelListResourceBundle parallelListResourceBundle = (ParallelListResourceBundle)paramParallelListResourceBundle.getParent();
    boolean bool = false;
    if (parallelListResourceBundle != null)
      bool = setSupplementary(paramString, parallelListResourceBundle); 
    OpenListResourceBundle openListResourceBundle = getSupplementary(paramString, paramParallelListResourceBundle.getLocale());
    paramParallelListResourceBundle.setParallelContents(openListResourceBundle);
    bool |= ((openListResourceBundle != null));
    if (bool)
      paramParallelListResourceBundle.resetKeySet(); 
    return bool;
  }
  
  public ResourceBundle getNumberFormatData(Locale paramLocale) { return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale); }
  
  public static ResourceBundle getBundle(final String baseName, final Locale locale) { return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
          public ResourceBundle run() { return ResourceBundle.getBundle(baseName, locale, INSTANCE); }
        }); }
  
  private static OpenListResourceBundle getSupplementary(final String baseName, final Locale locale) { return (OpenListResourceBundle)AccessController.doPrivileged(new PrivilegedAction<OpenListResourceBundle>() {
          public OpenListResourceBundle run() {
            OpenListResourceBundle openListResourceBundle = null;
            try {
              openListResourceBundle = (OpenListResourceBundle)ResourceBundle.getBundle(baseName, locale, INSTANCE);
            } catch (MissingResourceException missingResourceException) {}
            return openListResourceBundle;
          }
        }); }
  
  private static class LocaleDataResourceBundleControl extends ResourceBundle.Control {
    private static final LocaleDataResourceBundleControl INSTANCE = new LocaleDataResourceBundleControl();
    
    private static final String DOTCLDR = ".cldr";
    
    private LocaleDataResourceBundleControl() {}
    
    public List<Locale> getCandidateLocales(String param1String, Locale param1Locale) {
      List list = super.getCandidateLocales(param1String, param1Locale);
      int i = param1String.lastIndexOf('.');
      String str = (i >= 0) ? param1String.substring(i + 1) : param1String;
      LocaleProviderAdapter.Type type = param1String.contains(".cldr") ? LocaleProviderAdapter.Type.CLDR : LocaleProviderAdapter.Type.JRE;
      LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.forType(type);
      Set set = ((JRELocaleProviderAdapter)localeProviderAdapter).getLanguageTagSet(str);
      if (!set.isEmpty()) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          if (!LocaleProviderAdapter.isSupportedLocale((Locale)iterator.next(), type, set))
            iterator.remove(); 
        } 
      } 
      if (param1Locale.getLanguage() != "en" && type == LocaleProviderAdapter.Type.CLDR && str.equals("TimeZoneNames"))
        list.add(list.size() - 1, Locale.ENGLISH); 
      return list;
    }
    
    public Locale getFallbackLocale(String param1String, Locale param1Locale) {
      if (param1String == null || param1Locale == null)
        throw new NullPointerException(); 
      return null;
    }
    
    public String toBundleName(String param1String, Locale param1Locale) {
      String str1 = param1String;
      String str2 = param1Locale.getLanguage();
      if (str2.length() > 0 && (param1String.startsWith(LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage()) || param1String.startsWith(LocaleProviderAdapter.Type.JRE.getTextResourcesPackage()))) {
        assert LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length() == LocaleProviderAdapter.Type.JRE.getTextResourcesPackage().length();
        int i = LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length();
        if (param1String.indexOf(".cldr", i) > 0)
          i += ".cldr".length(); 
        str1 = param1String.substring(0, i + 1) + str2 + param1String.substring(i);
      } 
      return super.toBundleName(str1, param1Locale);
    }
  }
  
  private static class SupplementaryResourceBundleControl extends LocaleDataResourceBundleControl {
    private static final SupplementaryResourceBundleControl INSTANCE = new SupplementaryResourceBundleControl();
    
    private SupplementaryResourceBundleControl() { super(null); }
    
    public List<Locale> getCandidateLocales(String param1String, Locale param1Locale) { return Arrays.asList(new Locale[] { param1Locale }); }
    
    public long getTimeToLive(String param1String, Locale param1Locale) {
      assert param1String.contains("JavaTimeSupplementary");
      return -1L;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\resources\LocaleData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */