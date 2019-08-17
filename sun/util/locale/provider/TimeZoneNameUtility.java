package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.calendar.ZoneInfo;

public final class TimeZoneNameUtility {
  private static ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData = new ConcurrentHashMap();
  
  private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames = new ConcurrentHashMap();
  
  public static String[][] getZoneStrings(Locale paramLocale) {
    SoftReference softReference = (SoftReference)cachedZoneData.get(paramLocale);
    String[][] arrayOfString;
    if (softReference == null || (arrayOfString = (String[][])softReference.get()) == null) {
      arrayOfString = loadZoneStrings(paramLocale);
      softReference = new SoftReference(arrayOfString);
      cachedZoneData.put(paramLocale, softReference);
    } 
    return arrayOfString;
  }
  
  private static String[][] loadZoneStrings(Locale paramLocale) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(TimeZoneNameProvider.class, paramLocale);
    TimeZoneNameProvider timeZoneNameProvider = localeProviderAdapter.getTimeZoneNameProvider();
    if (timeZoneNameProvider instanceof TimeZoneNameProviderImpl)
      return ((TimeZoneNameProviderImpl)timeZoneNameProvider).getZoneStrings(paramLocale); 
    Set set = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale).getZoneIDs();
    LinkedList linkedList = new LinkedList();
    for (String str : set) {
      String[] arrayOfString1 = retrieveDisplayNamesImpl(str, paramLocale);
      if (arrayOfString1 != null)
        linkedList.add(arrayOfString1); 
    } 
    String[][] arrayOfString = new String[linkedList.size()][];
    return (String[][])linkedList.toArray(arrayOfString);
  }
  
  public static String[] retrieveDisplayNames(String paramString, Locale paramLocale) {
    Objects.requireNonNull(paramString);
    Objects.requireNonNull(paramLocale);
    return retrieveDisplayNamesImpl(paramString, paramLocale);
  }
  
  public static String retrieveGenericDisplayName(String paramString, int paramInt, Locale paramLocale) {
    String[] arrayOfString = retrieveDisplayNamesImpl(paramString, paramLocale);
    return Objects.nonNull(arrayOfString) ? arrayOfString[6 - paramInt] : null;
  }
  
  public static String retrieveDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale) {
    String[] arrayOfString = retrieveDisplayNamesImpl(paramString, paramLocale);
    return Objects.nonNull(arrayOfString) ? arrayOfString[(paramBoolean ? 4 : 2) - paramInt] : null;
  }
  
  private static String[] retrieveDisplayNamesImpl(String paramString, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(TimeZoneNameProvider.class);
    Map map = null;
    SoftReference softReference = (SoftReference)cachedDisplayNames.get(paramString);
    if (Objects.nonNull(softReference)) {
      map = (Map)softReference.get();
      if (Objects.nonNull(map)) {
        String[] arrayOfString1 = (String[])map.get(paramLocale);
        if (Objects.nonNull(arrayOfString1))
          return arrayOfString1; 
      } 
    } 
    String[] arrayOfString = new String[7];
    arrayOfString[0] = paramString;
    for (byte b = 1; b <= 6; b++) {
      arrayOfString[b] = (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, (b < 5) ? ((b < 3) ? "std" : "dst") : "generic", new Object[] { Integer.valueOf(b % 2), paramString });
    } 
    if (Objects.isNull(map))
      map = new ConcurrentHashMap(); 
    map.put(paramLocale, arrayOfString);
    softReference = new SoftReference(map);
    cachedDisplayNames.put(paramString, softReference);
    return arrayOfString;
  }
  
  private static class TimeZoneNameGetter extends Object implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider, String> {
    private static final TimeZoneNameGetter INSTANCE = new TimeZoneNameGetter();
    
    public String getObject(TimeZoneNameProvider param1TimeZoneNameProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      assert param1VarArgs.length == 2;
      int i = ((Integer)param1VarArgs[0]).intValue();
      String str1 = (String)param1VarArgs[1];
      String str2 = getName(param1TimeZoneNameProvider, param1Locale, param1String, i, str1);
      if (str2 == null) {
        Map map = ZoneInfo.getAliasTable();
        if (map != null) {
          String str = (String)map.get(str1);
          if (str != null)
            str2 = getName(param1TimeZoneNameProvider, param1Locale, param1String, i, str); 
          if (str2 == null)
            str2 = examineAliases(param1TimeZoneNameProvider, param1Locale, param1String, (str != null) ? str : str1, i, map); 
        } 
      } 
      return str2;
    }
    
    private static String examineAliases(TimeZoneNameProvider param1TimeZoneNameProvider, Locale param1Locale, String param1String1, String param1String2, int param1Int, Map<String, String> param1Map) {
      for (Map.Entry entry : param1Map.entrySet()) {
        if (((String)entry.getValue()).equals(param1String2)) {
          String str1 = (String)entry.getKey();
          String str2 = getName(param1TimeZoneNameProvider, param1Locale, param1String1, param1Int, str1);
          if (str2 != null)
            return str2; 
          str2 = examineAliases(param1TimeZoneNameProvider, param1Locale, param1String1, str1, param1Int, param1Map);
          if (str2 != null)
            return str2; 
        } 
      } 
      return null;
    }
    
    private static String getName(TimeZoneNameProvider param1TimeZoneNameProvider, Locale param1Locale, String param1String1, int param1Int, String param1String2) {
      String str = null;
      switch (param1String1) {
        case "std":
          str = param1TimeZoneNameProvider.getDisplayName(param1String2, false, param1Int, param1Locale);
          break;
        case "dst":
          str = param1TimeZoneNameProvider.getDisplayName(param1String2, true, param1Int, param1Locale);
          break;
        case "generic":
          str = param1TimeZoneNameProvider.getGenericDisplayName(param1String2, param1Int, param1Locale);
          break;
      } 
      return str;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\TimeZoneNameUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */