package sun.util.locale.provider;

import java.util.Locale;
import java.util.Map;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.LocaleServiceProvider;

public class CalendarDataUtility {
  public static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
  
  public static final String MINIMAL_DAYS_IN_FIRST_WEEK = "minimalDaysInFirstWeek";
  
  public static int retrieveFirstDayOfWeek(Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
    Integer integer = (Integer)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, "firstDayOfWeek", new Object[0]);
    return (integer != null && integer.intValue() >= 1 && integer.intValue() <= 7) ? integer.intValue() : 1;
  }
  
  public static int retrieveMinimalDaysInFirstWeek(Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
    Integer integer = (Integer)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, "minimalDaysInFirstWeek", new Object[0]);
    return (integer != null && integer.intValue() >= 1 && integer.intValue() <= 7) ? integer.intValue() : 1;
  }
  
  public static String retrieveFieldValueName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    return (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(false) });
  }
  
  public static String retrieveJavaTimeFieldValueName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    String str = (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(true) });
    if (str == null)
      str = (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(false) }); 
    return str;
  }
  
  public static Map<String, Integer> retrieveFieldValueNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    return (Map)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(false) });
  }
  
  public static Map<String, Integer> retrieveJavaTimeFieldValueNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    Map map = (Map)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(true) });
    if (map == null)
      map = (Map)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(false) }); 
    return map;
  }
  
  static String normalizeCalendarType(String paramString) {
    String str;
    if (paramString.equals("gregorian") || paramString.equals("iso8601")) {
      str = "gregory";
    } else if (paramString.startsWith("islamic")) {
      str = "islamic";
    } else {
      str = paramString;
    } 
    return str;
  }
  
  private static class CalendarFieldValueNameGetter extends Object implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, String> {
    private static final CalendarFieldValueNameGetter INSTANCE = new CalendarFieldValueNameGetter();
    
    public String getObject(CalendarNameProvider param1CalendarNameProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      assert param1VarArgs.length == 4;
      int i = ((Integer)param1VarArgs[0]).intValue();
      int j = ((Integer)param1VarArgs[1]).intValue();
      int k = ((Integer)param1VarArgs[2]).intValue();
      boolean bool = ((Boolean)param1VarArgs[3]).booleanValue();
      return (bool && param1CalendarNameProvider instanceof CalendarNameProviderImpl) ? ((CalendarNameProviderImpl)param1CalendarNameProvider).getJavaTimeDisplayName(param1String, i, j, k, param1Locale) : param1CalendarNameProvider.getDisplayName(param1String, i, j, k, param1Locale);
    }
  }
  
  private static class CalendarFieldValueNamesMapGetter extends Object implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, Map<String, Integer>> {
    private static final CalendarFieldValueNamesMapGetter INSTANCE = new CalendarFieldValueNamesMapGetter();
    
    public Map<String, Integer> getObject(CalendarNameProvider param1CalendarNameProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      assert param1VarArgs.length == 3;
      int i = ((Integer)param1VarArgs[0]).intValue();
      int j = ((Integer)param1VarArgs[1]).intValue();
      boolean bool = ((Boolean)param1VarArgs[2]).booleanValue();
      return (bool && param1CalendarNameProvider instanceof CalendarNameProviderImpl) ? ((CalendarNameProviderImpl)param1CalendarNameProvider).getJavaTimeDisplayNames(param1String, i, j, param1Locale) : param1CalendarNameProvider.getDisplayNames(param1String, i, j, param1Locale);
    }
  }
  
  private static class CalendarWeekParameterGetter extends Object implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarDataProvider, Integer> {
    private static final CalendarWeekParameterGetter INSTANCE = new CalendarWeekParameterGetter();
    
    public Integer getObject(CalendarDataProvider param1CalendarDataProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      int i;
      assert param1VarArgs.length == 0;
      switch (param1String) {
        case "firstDayOfWeek":
          i = param1CalendarDataProvider.getFirstDayOfWeek(param1Locale);
          break;
        case "minimalDaysInFirstWeek":
          i = param1CalendarDataProvider.getMinimalDaysInFirstWeek(param1Locale);
          break;
        default:
          throw new InternalError("invalid requestID: " + param1String);
      } 
      return (i != 0) ? Integer.valueOf(i) : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\CalendarDataUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */