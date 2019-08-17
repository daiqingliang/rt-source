package sun.util.calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.security.action.GetPropertyAction;

public abstract class CalendarSystem {
  private static ConcurrentMap<String, String> names;
  
  private static ConcurrentMap<String, CalendarSystem> calendars;
  
  private static final String PACKAGE_NAME = "sun.util.calendar.";
  
  private static final String[] namePairs = { "gregorian", "Gregorian", "japanese", "LocalGregorianCalendar", "julian", "JulianCalendar" };
  
  private static final Gregorian GREGORIAN_INSTANCE = new Gregorian();
  
  private static void initNames() {
    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
    StringBuilder stringBuilder = new StringBuilder();
    for (boolean bool = false; bool < namePairs.length; bool += true) {
      stringBuilder.setLength(0);
      String str = stringBuilder.append("sun.util.calendar.").append(namePairs[bool + true]).toString();
      concurrentHashMap.put(namePairs[bool], str);
    } 
    synchronized (CalendarSystem.class) {
      if (!initialized) {
        names = concurrentHashMap;
        calendars = new ConcurrentHashMap();
        initialized = true;
      } 
    } 
  }
  
  public static Gregorian getGregorianCalendar() { return GREGORIAN_INSTANCE; }
  
  public static CalendarSystem forName(String paramString) {
    if ("gregorian".equals(paramString))
      return GREGORIAN_INSTANCE; 
    if (!initialized)
      initNames(); 
    CalendarSystem calendarSystem1 = (CalendarSystem)calendars.get(paramString);
    if (calendarSystem1 != null)
      return calendarSystem1; 
    String str = (String)names.get(paramString);
    if (str == null)
      return null; 
    if (str.endsWith("LocalGregorianCalendar")) {
      calendarSystem1 = LocalGregorianCalendar.getLocalGregorianCalendar(paramString);
    } else {
      try {
        Class clazz = Class.forName(str);
        calendarSystem1 = (CalendarSystem)clazz.newInstance();
      } catch (Exception exception) {
        throw new InternalError(exception);
      } 
    } 
    if (calendarSystem1 == null)
      return null; 
    CalendarSystem calendarSystem2 = (CalendarSystem)calendars.putIfAbsent(paramString, calendarSystem1);
    return (calendarSystem2 == null) ? calendarSystem1 : calendarSystem2;
  }
  
  public static Properties getCalendarProperties() throws IOException {
    Properties properties = null;
    try {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
      final String fname = str1 + File.separator + "lib" + File.separator + "calendars.properties";
      properties = (Properties)AccessController.doPrivileged(new PrivilegedExceptionAction<Properties>() {
            public Properties run() throws IOException {
              Properties properties = new Properties();
              try (FileInputStream null = new FileInputStream(fname)) {
                properties.load(fileInputStream);
              } 
              return properties;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      Throwable throwable = privilegedActionException.getCause();
      if (throwable instanceof IOException)
        throw (IOException)throwable; 
      if (throwable instanceof IllegalArgumentException)
        throw (IllegalArgumentException)throwable; 
      throw new InternalError(throwable);
    } 
    return properties;
  }
  
  public abstract String getName();
  
  public abstract CalendarDate getCalendarDate();
  
  public abstract CalendarDate getCalendarDate(long paramLong);
  
  public abstract CalendarDate getCalendarDate(long paramLong, CalendarDate paramCalendarDate);
  
  public abstract CalendarDate getCalendarDate(long paramLong, TimeZone paramTimeZone);
  
  public abstract CalendarDate newCalendarDate();
  
  public abstract CalendarDate newCalendarDate(TimeZone paramTimeZone);
  
  public abstract long getTime(CalendarDate paramCalendarDate);
  
  public abstract int getYearLength(CalendarDate paramCalendarDate);
  
  public abstract int getYearLengthInMonths(CalendarDate paramCalendarDate);
  
  public abstract int getMonthLength(CalendarDate paramCalendarDate);
  
  public abstract int getWeekLength();
  
  public abstract Era getEra(String paramString);
  
  public abstract Era[] getEras();
  
  public abstract void setEra(CalendarDate paramCalendarDate, String paramString);
  
  public abstract CalendarDate getNthDayOfWeek(int paramInt1, int paramInt2, CalendarDate paramCalendarDate);
  
  public abstract CalendarDate setTimeOfDay(CalendarDate paramCalendarDate, int paramInt);
  
  public abstract boolean validate(CalendarDate paramCalendarDate);
  
  public abstract boolean normalize(CalendarDate paramCalendarDate);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\CalendarSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */