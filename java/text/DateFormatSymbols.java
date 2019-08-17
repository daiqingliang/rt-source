package java.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.locale.provider.TimeZoneNameUtility;

public class DateFormatSymbols implements Serializable, Cloneable {
  String[] eras = null;
  
  String[] months = null;
  
  String[] shortMonths = null;
  
  String[] weekdays = null;
  
  String[] shortWeekdays = null;
  
  String[] ampms = null;
  
  String[][] zoneStrings = (String[][])null;
  
  boolean isZoneStringsSet = false;
  
  static final String patternChars = "GyMdkHmsSEDFwWahKzZYuXL";
  
  static final int PATTERN_ERA = 0;
  
  static final int PATTERN_YEAR = 1;
  
  static final int PATTERN_MONTH = 2;
  
  static final int PATTERN_DAY_OF_MONTH = 3;
  
  static final int PATTERN_HOUR_OF_DAY1 = 4;
  
  static final int PATTERN_HOUR_OF_DAY0 = 5;
  
  static final int PATTERN_MINUTE = 6;
  
  static final int PATTERN_SECOND = 7;
  
  static final int PATTERN_MILLISECOND = 8;
  
  static final int PATTERN_DAY_OF_WEEK = 9;
  
  static final int PATTERN_DAY_OF_YEAR = 10;
  
  static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11;
  
  static final int PATTERN_WEEK_OF_YEAR = 12;
  
  static final int PATTERN_WEEK_OF_MONTH = 13;
  
  static final int PATTERN_AM_PM = 14;
  
  static final int PATTERN_HOUR1 = 15;
  
  static final int PATTERN_HOUR0 = 16;
  
  static final int PATTERN_ZONE_NAME = 17;
  
  static final int PATTERN_ZONE_VALUE = 18;
  
  static final int PATTERN_WEEK_YEAR = 19;
  
  static final int PATTERN_ISO_DAY_OF_WEEK = 20;
  
  static final int PATTERN_ISO_ZONE = 21;
  
  static final int PATTERN_MONTH_STANDALONE = 22;
  
  String localPatternChars = null;
  
  Locale locale = null;
  
  static final long serialVersionUID = -5987973545549424702L;
  
  static final int millisPerHour = 3600000;
  
  private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances = new ConcurrentHashMap(3);
  
  private int lastZoneIndex = 0;
  
  public DateFormatSymbols() { initializeData(Locale.getDefault(Locale.Category.FORMAT)); }
  
  public DateFormatSymbols(Locale paramLocale) { initializeData(paramLocale); }
  
  private DateFormatSymbols(boolean paramBoolean) {}
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  public static final DateFormatSymbols getInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormatSymbols getInstance(Locale paramLocale) {
    DateFormatSymbols dateFormatSymbols = getProviderInstance(paramLocale);
    if (dateFormatSymbols != null)
      return dateFormatSymbols; 
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  static final DateFormatSymbols getInstanceRef(Locale paramLocale) {
    DateFormatSymbols dateFormatSymbols = getProviderInstance(paramLocale);
    if (dateFormatSymbols != null)
      return dateFormatSymbols; 
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  private static DateFormatSymbols getProviderInstance(Locale paramLocale) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, paramLocale);
    DateFormatSymbolsProvider dateFormatSymbolsProvider = localeProviderAdapter.getDateFormatSymbolsProvider();
    DateFormatSymbols dateFormatSymbols = dateFormatSymbolsProvider.getInstance(paramLocale);
    if (dateFormatSymbols == null) {
      dateFormatSymbolsProvider = LocaleProviderAdapter.forJRE().getDateFormatSymbolsProvider();
      dateFormatSymbols = dateFormatSymbolsProvider.getInstance(paramLocale);
    } 
    return dateFormatSymbols;
  }
  
  public String[] getEras() { return (String[])Arrays.copyOf(this.eras, this.eras.length); }
  
  public void setEras(String[] paramArrayOfString) {
    this.eras = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getMonths() { return (String[])Arrays.copyOf(this.months, this.months.length); }
  
  public void setMonths(String[] paramArrayOfString) {
    this.months = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getShortMonths() { return (String[])Arrays.copyOf(this.shortMonths, this.shortMonths.length); }
  
  public void setShortMonths(String[] paramArrayOfString) {
    this.shortMonths = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getWeekdays() { return (String[])Arrays.copyOf(this.weekdays, this.weekdays.length); }
  
  public void setWeekdays(String[] paramArrayOfString) {
    this.weekdays = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getShortWeekdays() { return (String[])Arrays.copyOf(this.shortWeekdays, this.shortWeekdays.length); }
  
  public void setShortWeekdays(String[] paramArrayOfString) {
    this.shortWeekdays = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getAmPmStrings() { return (String[])Arrays.copyOf(this.ampms, this.ampms.length); }
  
  public void setAmPmStrings(String[] paramArrayOfString) {
    this.ampms = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[][] getZoneStrings() { return getZoneStringsImpl(true); }
  
  public void setZoneStrings(String[][] paramArrayOfString) {
    String[][] arrayOfString = new String[paramArrayOfString.length][];
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      int i = paramArrayOfString[b].length;
      if (i < 5)
        throw new IllegalArgumentException(); 
      arrayOfString[b] = (String[])Arrays.copyOf(paramArrayOfString[b], i);
    } 
    this.zoneStrings = arrayOfString;
    this.isZoneStringsSet = true;
    this.cachedHashCode = 0;
  }
  
  public String getLocalPatternChars() { return this.localPatternChars; }
  
  public void setLocalPatternChars(String paramString) {
    this.localPatternChars = paramString.toString();
    this.cachedHashCode = 0;
  }
  
  public Object clone() {
    try {
      DateFormatSymbols dateFormatSymbols = (DateFormatSymbols)super.clone();
      copyMembers(this, dateFormatSymbols);
      return dateFormatSymbols;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public int hashCode() {
    int i = this.cachedHashCode;
    if (i == 0) {
      i = 5;
      i = 11 * i + Arrays.hashCode(this.eras);
      i = 11 * i + Arrays.hashCode(this.months);
      i = 11 * i + Arrays.hashCode(this.shortMonths);
      i = 11 * i + Arrays.hashCode(this.weekdays);
      i = 11 * i + Arrays.hashCode(this.shortWeekdays);
      i = 11 * i + Arrays.hashCode(this.ampms);
      i = 11 * i + Arrays.deepHashCode(getZoneStringsWrapper());
      i = 11 * i + Objects.hashCode(this.localPatternChars);
      this.cachedHashCode = i;
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    DateFormatSymbols dateFormatSymbols = (DateFormatSymbols)paramObject;
    return (Arrays.equals(this.eras, dateFormatSymbols.eras) && Arrays.equals(this.months, dateFormatSymbols.months) && Arrays.equals(this.shortMonths, dateFormatSymbols.shortMonths) && Arrays.equals(this.weekdays, dateFormatSymbols.weekdays) && Arrays.equals(this.shortWeekdays, dateFormatSymbols.shortWeekdays) && Arrays.equals(this.ampms, dateFormatSymbols.ampms) && Arrays.deepEquals(getZoneStringsWrapper(), dateFormatSymbols.getZoneStringsWrapper()) && ((this.localPatternChars != null && this.localPatternChars.equals(dateFormatSymbols.localPatternChars)) || (this.localPatternChars == null && dateFormatSymbols.localPatternChars == null)));
  }
  
  private void initializeData(Locale paramLocale) {
    SoftReference softReference = (SoftReference)cachedInstances.get(paramLocale);
    DateFormatSymbols dateFormatSymbols;
    if (softReference == null || (dateFormatSymbols = (DateFormatSymbols)softReference.get()) == null) {
      if (softReference != null)
        cachedInstances.remove(paramLocale, softReference); 
      dateFormatSymbols = new DateFormatSymbols(false);
      LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, paramLocale);
      if (!(localeProviderAdapter instanceof ResourceBundleBasedAdapter))
        localeProviderAdapter = LocaleProviderAdapter.getResourceBundleBased(); 
      ResourceBundle resourceBundle = ((ResourceBundleBasedAdapter)localeProviderAdapter).getLocaleData().getDateFormatData(paramLocale);
      dateFormatSymbols.locale = paramLocale;
      if (resourceBundle.containsKey("Eras")) {
        dateFormatSymbols.eras = resourceBundle.getStringArray("Eras");
      } else if (resourceBundle.containsKey("long.Eras")) {
        dateFormatSymbols.eras = resourceBundle.getStringArray("long.Eras");
      } else if (resourceBundle.containsKey("short.Eras")) {
        dateFormatSymbols.eras = resourceBundle.getStringArray("short.Eras");
      } 
      dateFormatSymbols.months = resourceBundle.getStringArray("MonthNames");
      dateFormatSymbols.shortMonths = resourceBundle.getStringArray("MonthAbbreviations");
      dateFormatSymbols.ampms = resourceBundle.getStringArray("AmPmMarkers");
      dateFormatSymbols.localPatternChars = resourceBundle.getString("DateTimePatternChars");
      dateFormatSymbols.weekdays = toOneBasedArray(resourceBundle.getStringArray("DayNames"));
      dateFormatSymbols.shortWeekdays = toOneBasedArray(resourceBundle.getStringArray("DayAbbreviations"));
      softReference = new SoftReference(dateFormatSymbols);
      SoftReference softReference1 = (SoftReference)cachedInstances.putIfAbsent(paramLocale, softReference);
      if (softReference1 != null) {
        DateFormatSymbols dateFormatSymbols1 = (DateFormatSymbols)softReference1.get();
        if (dateFormatSymbols1 == null) {
          cachedInstances.replace(paramLocale, softReference1, softReference);
        } else {
          softReference = softReference1;
          dateFormatSymbols = dateFormatSymbols1;
        } 
      } 
      Locale locale1 = resourceBundle.getLocale();
      if (!locale1.equals(paramLocale)) {
        SoftReference softReference2 = (SoftReference)cachedInstances.putIfAbsent(locale1, softReference);
        if (softReference2 != null && softReference2.get() == null)
          cachedInstances.replace(locale1, softReference2, softReference); 
      } 
    } 
    copyMembers(dateFormatSymbols, this);
  }
  
  private static String[] toOneBasedArray(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i + 1];
    arrayOfString[0] = "";
    for (byte b = 0; b < i; b++)
      arrayOfString[b + true] = paramArrayOfString[b]; 
    return arrayOfString;
  }
  
  final int getZoneIndex(String paramString) {
    String[][] arrayOfString = getZoneStringsWrapper();
    if (this.lastZoneIndex < arrayOfString.length && paramString.equals(arrayOfString[this.lastZoneIndex][0]))
      return this.lastZoneIndex; 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.equals(arrayOfString[b][0])) {
        this.lastZoneIndex = b;
        return b;
      } 
    } 
    return -1;
  }
  
  final String[][] getZoneStringsWrapper() { return isSubclassObject() ? getZoneStrings() : getZoneStringsImpl(false); }
  
  private String[][] getZoneStringsImpl(boolean paramBoolean) {
    if (this.zoneStrings == null)
      this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale); 
    if (!paramBoolean)
      return this.zoneStrings; 
    int i = this.zoneStrings.length;
    String[][] arrayOfString = new String[i][];
    for (byte b = 0; b < i; b++)
      arrayOfString[b] = (String[])Arrays.copyOf(this.zoneStrings[b], this.zoneStrings[b].length); 
    return arrayOfString;
  }
  
  private boolean isSubclassObject() { return !getClass().getName().equals("java.text.DateFormatSymbols"); }
  
  private void copyMembers(DateFormatSymbols paramDateFormatSymbols1, DateFormatSymbols paramDateFormatSymbols2) {
    paramDateFormatSymbols2.locale = paramDateFormatSymbols1.locale;
    paramDateFormatSymbols2.eras = (String[])Arrays.copyOf(paramDateFormatSymbols1.eras, paramDateFormatSymbols1.eras.length);
    paramDateFormatSymbols2.months = (String[])Arrays.copyOf(paramDateFormatSymbols1.months, paramDateFormatSymbols1.months.length);
    paramDateFormatSymbols2.shortMonths = (String[])Arrays.copyOf(paramDateFormatSymbols1.shortMonths, paramDateFormatSymbols1.shortMonths.length);
    paramDateFormatSymbols2.weekdays = (String[])Arrays.copyOf(paramDateFormatSymbols1.weekdays, paramDateFormatSymbols1.weekdays.length);
    paramDateFormatSymbols2.shortWeekdays = (String[])Arrays.copyOf(paramDateFormatSymbols1.shortWeekdays, paramDateFormatSymbols1.shortWeekdays.length);
    paramDateFormatSymbols2.ampms = (String[])Arrays.copyOf(paramDateFormatSymbols1.ampms, paramDateFormatSymbols1.ampms.length);
    if (paramDateFormatSymbols1.zoneStrings != null) {
      paramDateFormatSymbols2.zoneStrings = paramDateFormatSymbols1.getZoneStringsImpl(true);
    } else {
      paramDateFormatSymbols2.zoneStrings = (String[][])null;
    } 
    paramDateFormatSymbols2.localPatternChars = paramDateFormatSymbols1.localPatternChars;
    paramDateFormatSymbols2.cachedHashCode = 0;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.zoneStrings == null)
      this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale); 
    paramObjectOutputStream.defaultWriteObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\DateFormatSymbols.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */