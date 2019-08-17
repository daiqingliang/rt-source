package java.text;

import java.io.InvalidObjectException;
import java.text.spi.DateFormatProvider;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class DateFormat extends Format {
  protected Calendar calendar;
  
  protected NumberFormat numberFormat;
  
  public static final int ERA_FIELD = 0;
  
  public static final int YEAR_FIELD = 1;
  
  public static final int MONTH_FIELD = 2;
  
  public static final int DATE_FIELD = 3;
  
  public static final int HOUR_OF_DAY1_FIELD = 4;
  
  public static final int HOUR_OF_DAY0_FIELD = 5;
  
  public static final int MINUTE_FIELD = 6;
  
  public static final int SECOND_FIELD = 7;
  
  public static final int MILLISECOND_FIELD = 8;
  
  public static final int DAY_OF_WEEK_FIELD = 9;
  
  public static final int DAY_OF_YEAR_FIELD = 10;
  
  public static final int DAY_OF_WEEK_IN_MONTH_FIELD = 11;
  
  public static final int WEEK_OF_YEAR_FIELD = 12;
  
  public static final int WEEK_OF_MONTH_FIELD = 13;
  
  public static final int AM_PM_FIELD = 14;
  
  public static final int HOUR1_FIELD = 15;
  
  public static final int HOUR0_FIELD = 16;
  
  public static final int TIMEZONE_FIELD = 17;
  
  private static final long serialVersionUID = 7218322306649953788L;
  
  public static final int FULL = 0;
  
  public static final int LONG = 1;
  
  public static final int MEDIUM = 2;
  
  public static final int SHORT = 3;
  
  public static final int DEFAULT = 2;
  
  public final StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    if (paramObject instanceof Date)
      return format((Date)paramObject, paramStringBuffer, paramFieldPosition); 
    if (paramObject instanceof Number)
      return format(new Date(((Number)paramObject).longValue()), paramStringBuffer, paramFieldPosition); 
    throw new IllegalArgumentException("Cannot format given Object as a Date");
  }
  
  public abstract StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public final String format(Date paramDate) { return format(paramDate, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString(); }
  
  public Date parse(String paramString) throws ParseException {
    ParsePosition parsePosition = new ParsePosition(0);
    Date date = parse(paramString, parsePosition);
    if (parsePosition.index == 0)
      throw new ParseException("Unparseable date: \"" + paramString + "\"", parsePosition.errorIndex); 
    return date;
  }
  
  public abstract Date parse(String paramString, ParsePosition paramParsePosition);
  
  public Object parseObject(String paramString, ParsePosition paramParsePosition) { return parse(paramString, paramParsePosition); }
  
  public static final DateFormat getTimeInstance() { return get(2, 0, 1, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormat getTimeInstance(int paramInt) { return get(paramInt, 0, 1, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormat getTimeInstance(int paramInt, Locale paramLocale) { return get(paramInt, 0, 1, paramLocale); }
  
  public static final DateFormat getDateInstance() { return get(0, 2, 2, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormat getDateInstance(int paramInt) { return get(0, paramInt, 2, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormat getDateInstance(int paramInt, Locale paramLocale) { return get(0, paramInt, 2, paramLocale); }
  
  public static final DateFormat getDateTimeInstance() { return get(2, 2, 3, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormat getDateTimeInstance(int paramInt1, int paramInt2) { return get(paramInt2, paramInt1, 3, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale) { return get(paramInt2, paramInt1, 3, paramLocale); }
  
  public static final DateFormat getInstance() { return getDateTimeInstance(3, 3); }
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(DateFormatProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  public void setCalendar(Calendar paramCalendar) { this.calendar = paramCalendar; }
  
  public Calendar getCalendar() { return this.calendar; }
  
  public void setNumberFormat(NumberFormat paramNumberFormat) { this.numberFormat = paramNumberFormat; }
  
  public NumberFormat getNumberFormat() { return this.numberFormat; }
  
  public void setTimeZone(TimeZone paramTimeZone) { this.calendar.setTimeZone(paramTimeZone); }
  
  public TimeZone getTimeZone() { return this.calendar.getTimeZone(); }
  
  public void setLenient(boolean paramBoolean) { this.calendar.setLenient(paramBoolean); }
  
  public boolean isLenient() { return this.calendar.isLenient(); }
  
  public int hashCode() { return this.numberFormat.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    DateFormat dateFormat = (DateFormat)paramObject;
    return (this.calendar.getFirstDayOfWeek() == dateFormat.calendar.getFirstDayOfWeek() && this.calendar.getMinimalDaysInFirstWeek() == dateFormat.calendar.getMinimalDaysInFirstWeek() && this.calendar.isLenient() == dateFormat.calendar.isLenient() && this.calendar.getTimeZone().equals(dateFormat.calendar.getTimeZone()) && this.numberFormat.equals(dateFormat.numberFormat));
  }
  
  public Object clone() {
    DateFormat dateFormat = (DateFormat)super.clone();
    dateFormat.calendar = (Calendar)this.calendar.clone();
    dateFormat.numberFormat = (NumberFormat)this.numberFormat.clone();
    return dateFormat;
  }
  
  private static DateFormat get(int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
    if ((paramInt3 & true) != 0) {
      if (paramInt1 < 0 || paramInt1 > 3)
        throw new IllegalArgumentException("Illegal time style " + paramInt1); 
    } else {
      paramInt1 = -1;
    } 
    if ((paramInt3 & 0x2) != 0) {
      if (paramInt2 < 0 || paramInt2 > 3)
        throw new IllegalArgumentException("Illegal date style " + paramInt2); 
    } else {
      paramInt2 = -1;
    } 
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatProvider.class, paramLocale);
    DateFormat dateFormat = get(localeProviderAdapter, paramInt1, paramInt2, paramLocale);
    if (dateFormat == null)
      dateFormat = get(LocaleProviderAdapter.forJRE(), paramInt1, paramInt2, paramLocale); 
    return dateFormat;
  }
  
  private static DateFormat get(LocaleProviderAdapter paramLocaleProviderAdapter, int paramInt1, int paramInt2, Locale paramLocale) {
    DateFormat dateFormat;
    DateFormatProvider dateFormatProvider = paramLocaleProviderAdapter.getDateFormatProvider();
    if (paramInt1 == -1) {
      dateFormat = dateFormatProvider.getDateInstance(paramInt2, paramLocale);
    } else if (paramInt2 == -1) {
      dateFormat = dateFormatProvider.getTimeInstance(paramInt1, paramLocale);
    } else {
      dateFormat = dateFormatProvider.getDateTimeInstance(paramInt2, paramInt1, paramLocale);
    } 
    return dateFormat;
  }
  
  public static class Field extends Format.Field {
    private static final long serialVersionUID = 7441350119349544720L;
    
    private static final Map<String, Field> instanceMap = new HashMap(18);
    
    private static final Field[] calendarToFieldMapping = new Field[17];
    
    private int calendarField;
    
    public static final Field ERA = new Field("era", 0);
    
    public static final Field YEAR = new Field("year", 1);
    
    public static final Field MONTH = new Field("month", 2);
    
    public static final Field DAY_OF_MONTH = new Field("day of month", 5);
    
    public static final Field HOUR_OF_DAY1 = new Field("hour of day 1", -1);
    
    public static final Field HOUR_OF_DAY0 = new Field("hour of day", 11);
    
    public static final Field MINUTE = new Field("minute", 12);
    
    public static final Field SECOND = new Field("second", 13);
    
    public static final Field MILLISECOND = new Field("millisecond", 14);
    
    public static final Field DAY_OF_WEEK = new Field("day of week", 7);
    
    public static final Field DAY_OF_YEAR = new Field("day of year", 6);
    
    public static final Field DAY_OF_WEEK_IN_MONTH = new Field("day of week in month", 8);
    
    public static final Field WEEK_OF_YEAR = new Field("week of year", 3);
    
    public static final Field WEEK_OF_MONTH = new Field("week of month", 4);
    
    public static final Field AM_PM = new Field("am pm", 9);
    
    public static final Field HOUR1 = new Field("hour 1", -1);
    
    public static final Field HOUR0 = new Field("hour", 10);
    
    public static final Field TIME_ZONE = new Field("time zone", -1);
    
    public static Field ofCalendarField(int param1Int) {
      if (param1Int < 0 || param1Int >= calendarToFieldMapping.length)
        throw new IllegalArgumentException("Unknown Calendar constant " + param1Int); 
      return calendarToFieldMapping[param1Int];
    }
    
    protected Field(String param1String, int param1Int) {
      super(param1String);
      this.calendarField = param1Int;
      if (getClass() == Field.class) {
        instanceMap.put(param1String, this);
        if (param1Int >= 0)
          calendarToFieldMapping[param1Int] = this; 
      } 
    }
    
    public int getCalendarField() { return this.calendarField; }
    
    protected Object readResolve() {
      if (getClass() != Field.class)
        throw new InvalidObjectException("subclass didn't correctly implement readResolve"); 
      Object object = instanceMap.get(getName());
      if (object != null)
        return object; 
      throw new InvalidObjectException("unknown attribute name");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\DateFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */