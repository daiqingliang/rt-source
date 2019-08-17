package sun.util.locale.provider;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.calendar.ZoneInfo;
import sun.util.resources.LocaleData;
import sun.util.resources.OpenListResourceBundle;
import sun.util.resources.ParallelListResourceBundle;
import sun.util.resources.TimeZoneNamesBundle;

public class LocaleResources {
  private final Locale locale;
  
  private final LocaleData localeData;
  
  private final LocaleProviderAdapter.Type type;
  
  private ConcurrentMap<String, ResourceReference> cache = new ConcurrentHashMap();
  
  private ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
  
  private static final String BREAK_ITERATOR_INFO = "BII.";
  
  private static final String CALENDAR_DATA = "CALD.";
  
  private static final String COLLATION_DATA_CACHEKEY = "COLD";
  
  private static final String DECIMAL_FORMAT_SYMBOLS_DATA_CACHEKEY = "DFSD";
  
  private static final String CURRENCY_NAMES = "CN.";
  
  private static final String LOCALE_NAMES = "LN.";
  
  private static final String TIME_ZONE_NAMES = "TZN.";
  
  private static final String ZONE_IDS_CACHEKEY = "ZID";
  
  private static final String CALENDAR_NAMES = "CALN.";
  
  private static final String NUMBER_PATTERNS_CACHEKEY = "NP";
  
  private static final String DATE_TIME_PATTERN = "DTP.";
  
  private static final Object NULLOBJECT = new Object();
  
  LocaleResources(ResourceBundleBasedAdapter paramResourceBundleBasedAdapter, Locale paramLocale) {
    this.locale = paramLocale;
    this.localeData = paramResourceBundleBasedAdapter.getLocaleData();
    this.type = ((LocaleProviderAdapter)paramResourceBundleBasedAdapter).getAdapterType();
  }
  
  private void removeEmptyReferences() {
    Reference reference;
    while ((reference = this.referenceQueue.poll()) != null)
      this.cache.remove(((ResourceReference)reference).getCacheKey()); 
  }
  
  Object getBreakIteratorInfo(String paramString) {
    String str = "BII." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    Object object;
    if (resourceReference == null || (object = resourceReference.get()) == null) {
      object = this.localeData.getBreakIteratorInfo(this.locale).getObject(paramString);
      this.cache.put(str, new ResourceReference(str, object, this.referenceQueue));
    } 
    return object;
  }
  
  int getCalendarData(String paramString) {
    String str = "CALD." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    Integer integer;
    if (resourceReference == null || (integer = (Integer)resourceReference.get()) == null) {
      ResourceBundle resourceBundle = this.localeData.getCalendarData(this.locale);
      if (resourceBundle.containsKey(paramString)) {
        integer = Integer.valueOf(Integer.parseInt(resourceBundle.getString(paramString)));
      } else {
        integer = Integer.valueOf(0);
      } 
      this.cache.put(str, new ResourceReference(str, integer, this.referenceQueue));
    } 
    return integer.intValue();
  }
  
  public String getCollationData() {
    String str1 = "Rule";
    String str2 = "";
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get("COLD");
    if (resourceReference == null || (str2 = (String)resourceReference.get()) == null) {
      ResourceBundle resourceBundle = this.localeData.getCollationData(this.locale);
      if (resourceBundle.containsKey(str1))
        str2 = resourceBundle.getString(str1); 
      this.cache.put("COLD", new ResourceReference("COLD", str2, this.referenceQueue));
    } 
    return str2;
  }
  
  public Object[] getDecimalFormatSymbolsData() {
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get("DFSD");
    Object[] arrayOfObject;
    if (resourceReference == null || (arrayOfObject = (Object[])resourceReference.get()) == null) {
      ResourceBundle resourceBundle = this.localeData.getNumberFormatData(this.locale);
      arrayOfObject = new Object[3];
      String str = this.locale.getUnicodeLocaleType("nu");
      if (str != null) {
        String str1 = str + ".NumberElements";
        if (resourceBundle.containsKey(str1))
          arrayOfObject[0] = resourceBundle.getStringArray(str1); 
      } 
      if (arrayOfObject[false] == null && resourceBundle.containsKey("DefaultNumberingSystem")) {
        String str1 = resourceBundle.getString("DefaultNumberingSystem") + ".NumberElements";
        if (resourceBundle.containsKey(str1))
          arrayOfObject[0] = resourceBundle.getStringArray(str1); 
      } 
      if (arrayOfObject[false] == null)
        arrayOfObject[0] = resourceBundle.getStringArray("NumberElements"); 
      this.cache.put("DFSD", new ResourceReference("DFSD", arrayOfObject, this.referenceQueue));
    } 
    return arrayOfObject;
  }
  
  public String getCurrencyName(String paramString) {
    Object object = null;
    String str = "CN." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    if (resourceReference != null && (object = resourceReference.get()) != null) {
      if (object.equals(NULLOBJECT))
        object = null; 
      return (String)object;
    } 
    OpenListResourceBundle openListResourceBundle = this.localeData.getCurrencyNames(this.locale);
    if (openListResourceBundle.containsKey(paramString)) {
      object = openListResourceBundle.getObject(paramString);
      this.cache.put(str, new ResourceReference(str, object, this.referenceQueue));
    } 
    return (String)object;
  }
  
  public String getLocaleName(String paramString) {
    Object object = null;
    String str = "LN." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    if (resourceReference != null && (object = resourceReference.get()) != null) {
      if (object.equals(NULLOBJECT))
        object = null; 
      return (String)object;
    } 
    OpenListResourceBundle openListResourceBundle = this.localeData.getLocaleNames(this.locale);
    if (openListResourceBundle.containsKey(paramString)) {
      object = openListResourceBundle.getObject(paramString);
      this.cache.put(str, new ResourceReference(str, object, this.referenceQueue));
    } 
    return (String)object;
  }
  
  String[] getTimeZoneNames(String paramString) {
    String[] arrayOfString = null;
    String str = "TZN.." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    if (Objects.isNull(resourceReference) || Objects.isNull(arrayOfString = (String[])resourceReference.get())) {
      TimeZoneNamesBundle timeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
      if (timeZoneNamesBundle.containsKey(paramString)) {
        arrayOfString = timeZoneNamesBundle.getStringArray(paramString);
        this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
      } 
    } 
    return arrayOfString;
  }
  
  Set<String> getZoneIDs() {
    Set set = null;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get("ZID");
    if (resourceReference == null || (set = (Set)resourceReference.get()) == null) {
      TimeZoneNamesBundle timeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
      set = timeZoneNamesBundle.keySet();
      this.cache.put("ZID", new ResourceReference("ZID", set, this.referenceQueue));
    } 
    return set;
  }
  
  String[][] getZoneStrings() {
    TimeZoneNamesBundle timeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
    Set set = getZoneIDs();
    LinkedHashSet linkedHashSet = new LinkedHashSet();
    for (String str : set)
      linkedHashSet.add(timeZoneNamesBundle.getStringArray(str)); 
    if (this.type == LocaleProviderAdapter.Type.CLDR) {
      Map map = ZoneInfo.getAliasTable();
      for (String str : map.keySet()) {
        if (!set.contains(str)) {
          String str1 = (String)map.get(str);
          if (set.contains(str1)) {
            String[] arrayOfString = timeZoneNamesBundle.getStringArray(str1);
            arrayOfString[0] = str;
            linkedHashSet.add(arrayOfString);
          } 
        } 
      } 
    } 
    return (String[][])linkedHashSet.toArray(new String[0][]);
  }
  
  String[] getCalendarNames(String paramString) {
    String[] arrayOfString = null;
    String str = "CALN." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    if (resourceReference == null || (arrayOfString = (String[])resourceReference.get()) == null) {
      ResourceBundle resourceBundle = this.localeData.getDateFormatData(this.locale);
      if (resourceBundle.containsKey(paramString)) {
        arrayOfString = resourceBundle.getStringArray(paramString);
        this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
      } 
    } 
    return arrayOfString;
  }
  
  String[] getJavaTimeNames(String paramString) {
    String[] arrayOfString = null;
    String str = "CALN." + paramString;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str);
    if (resourceReference == null || (arrayOfString = (String[])resourceReference.get()) == null) {
      ResourceBundle resourceBundle = getJavaTimeFormatData();
      if (resourceBundle.containsKey(paramString)) {
        arrayOfString = resourceBundle.getStringArray(paramString);
        this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
      } 
    } 
    return arrayOfString;
  }
  
  public String getDateTimePattern(int paramInt1, int paramInt2, Calendar paramCalendar) {
    if (paramCalendar == null)
      paramCalendar = Calendar.getInstance(this.locale); 
    return getDateTimePattern(null, paramInt1, paramInt2, paramCalendar.getCalendarType());
  }
  
  public String getJavaTimeDateTimePattern(int paramInt1, int paramInt2, String paramString) {
    paramString = CalendarDataUtility.normalizeCalendarType(paramString);
    String str = getDateTimePattern("java.time.", paramInt1, paramInt2, paramString);
    if (str == null)
      str = getDateTimePattern(null, paramInt1, paramInt2, paramString); 
    return str;
  }
  
  private String getDateTimePattern(String paramString1, int paramInt1, int paramInt2, String paramString2) {
    String str1;
    String str2 = null;
    String str3 = null;
    if (paramInt1 >= 0) {
      if (paramString1 != null)
        str2 = getDateTimePattern(paramString1, "TimePatterns", paramInt1, paramString2); 
      if (str2 == null)
        str2 = getDateTimePattern(null, "TimePatterns", paramInt1, paramString2); 
    } 
    if (paramInt2 >= 0) {
      if (paramString1 != null)
        str3 = getDateTimePattern(paramString1, "DatePatterns", paramInt2, paramString2); 
      if (str3 == null)
        str3 = getDateTimePattern(null, "DatePatterns", paramInt2, paramString2); 
    } 
    if (paramInt1 >= 0) {
      if (paramInt2 >= 0) {
        String str = null;
        if (paramString1 != null)
          str = getDateTimePattern(paramString1, "DateTimePatterns", 0, paramString2); 
        if (str == null)
          str = getDateTimePattern(null, "DateTimePatterns", 0, paramString2); 
        switch (str) {
          case "{1} {0}":
            return str3 + " " + str2;
          case "{0} {1}":
            return str2 + " " + str3;
        } 
        str1 = MessageFormat.format(str, new Object[] { str2, str3 });
      } else {
        str1 = str2;
      } 
    } else if (paramInt2 >= 0) {
      str1 = str3;
    } else {
      throw new IllegalArgumentException("No date or time style specified");
    } 
    return str1;
  }
  
  public String[] getNumberPatterns() {
    String[] arrayOfString = null;
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get("NP");
    if (resourceReference == null || (arrayOfString = (String[])resourceReference.get()) == null) {
      ResourceBundle resourceBundle = this.localeData.getNumberFormatData(this.locale);
      arrayOfString = resourceBundle.getStringArray("NumberPatterns");
      this.cache.put("NP", new ResourceReference("NP", arrayOfString, this.referenceQueue));
    } 
    return arrayOfString;
  }
  
  public ResourceBundle getJavaTimeFormatData() {
    ResourceBundle resourceBundle = this.localeData.getDateFormatData(this.locale);
    if (resourceBundle instanceof ParallelListResourceBundle)
      this.localeData.setSupplementary((ParallelListResourceBundle)resourceBundle); 
    return resourceBundle;
  }
  
  private String getDateTimePattern(String paramString1, String paramString2, int paramInt, String paramString3) {
    StringBuilder stringBuilder = new StringBuilder();
    if (paramString1 != null)
      stringBuilder.append(paramString1); 
    if (!"gregory".equals(paramString3))
      stringBuilder.append(paramString3).append('.'); 
    stringBuilder.append(paramString2);
    String str1 = stringBuilder.toString();
    String str2 = stringBuilder.insert(0, "DTP.").toString();
    removeEmptyReferences();
    ResourceReference resourceReference = (ResourceReference)this.cache.get(str2);
    String[] arrayOfString = NULLOBJECT;
    if (resourceReference == null || (arrayOfString = resourceReference.get()) == null) {
      ResourceBundle resourceBundle = (paramString1 != null) ? getJavaTimeFormatData() : this.localeData.getDateFormatData(this.locale);
      if (resourceBundle.containsKey(str1)) {
        arrayOfString = resourceBundle.getStringArray(str1);
      } else {
        assert !str1.equals(paramString2);
        if (resourceBundle.containsKey(paramString2))
          arrayOfString = resourceBundle.getStringArray(paramString2); 
      } 
      this.cache.put(str2, new ResourceReference(str2, arrayOfString, this.referenceQueue));
    } 
    if (arrayOfString == NULLOBJECT) {
      assert paramString1 != null;
      return null;
    } 
    return (String[])arrayOfString[paramInt];
  }
  
  private static class ResourceReference extends SoftReference<Object> {
    private final String cacheKey;
    
    ResourceReference(String param1String, Object param1Object, ReferenceQueue<Object> param1ReferenceQueue) {
      super(param1Object, param1ReferenceQueue);
      this.cacheKey = param1String;
    }
    
    String getCacheKey() { return this.cacheKey; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\LocaleResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */