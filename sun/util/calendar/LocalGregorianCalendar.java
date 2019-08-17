package sun.util.calendar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class LocalGregorianCalendar extends BaseCalendar {
  private String name;
  
  private Era[] eras;
  
  static LocalGregorianCalendar getLocalGregorianCalendar(String paramString) {
    Properties properties;
    try {
      properties = CalendarSystem.getCalendarProperties();
    } catch (IOException|IllegalArgumentException iOException) {
      throw new InternalError(iOException);
    } 
    String str = properties.getProperty("calendar." + paramString + ".eras");
    if (str == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(str, ";");
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken().trim();
      StringTokenizer stringTokenizer1 = new StringTokenizer(str1, ",");
      String str2 = null;
      boolean bool = true;
      long l = 0L;
      String str3 = null;
      while (stringTokenizer1.hasMoreTokens()) {
        String str4 = stringTokenizer1.nextToken();
        int i = str4.indexOf('=');
        if (i == -1)
          return null; 
        String str5 = str4.substring(0, i);
        String str6 = str4.substring(i + 1);
        if ("name".equals(str5)) {
          str2 = str6;
          continue;
        } 
        if ("since".equals(str5)) {
          if (str6.endsWith("u")) {
            bool = false;
            l = Long.parseLong(str6.substring(0, str6.length() - 1));
            continue;
          } 
          l = Long.parseLong(str6);
          continue;
        } 
        if ("abbr".equals(str5)) {
          str3 = str6;
          continue;
        } 
        throw new RuntimeException("Unknown key word: " + str5);
      } 
      Era era = new Era(str2, str3, l, bool);
      arrayList.add(era);
    } 
    Era[] arrayOfEra = new Era[arrayList.size()];
    arrayList.toArray(arrayOfEra);
    return new LocalGregorianCalendar(paramString, arrayOfEra);
  }
  
  private LocalGregorianCalendar(String paramString, Era[] paramArrayOfEra) {
    this.name = paramString;
    this.eras = paramArrayOfEra;
    setEras(paramArrayOfEra);
  }
  
  public String getName() { return this.name; }
  
  public Date getCalendarDate() { return getCalendarDate(System.currentTimeMillis(), newCalendarDate()); }
  
  public Date getCalendarDate(long paramLong) { return getCalendarDate(paramLong, newCalendarDate()); }
  
  public Date getCalendarDate(long paramLong, TimeZone paramTimeZone) { return getCalendarDate(paramLong, newCalendarDate(paramTimeZone)); }
  
  public Date getCalendarDate(long paramLong, CalendarDate paramCalendarDate) {
    Date date = (Date)super.getCalendarDate(paramLong, paramCalendarDate);
    return adjustYear(date, paramLong, date.getZoneOffset());
  }
  
  private Date adjustYear(Date paramDate, long paramLong, int paramInt) {
    int i;
    for (i = this.eras.length - 1; i >= 0; i--) {
      Era era = this.eras[i];
      long l = era.getSince(null);
      if (era.isLocalTime())
        l -= paramInt; 
      if (paramLong >= l) {
        paramDate.setLocalEra(era);
        int j = paramDate.getNormalizedYear() - era.getSinceDate().getYear() + 1;
        paramDate.setLocalYear(j);
        break;
      } 
    } 
    if (i < 0) {
      paramDate.setLocalEra(null);
      paramDate.setLocalYear(paramDate.getNormalizedYear());
    } 
    paramDate.setNormalized(true);
    return paramDate;
  }
  
  public Date newCalendarDate() { return new Date(); }
  
  public Date newCalendarDate(TimeZone paramTimeZone) { return new Date(paramTimeZone); }
  
  public boolean validate(CalendarDate paramCalendarDate) {
    Date date = (Date)paramCalendarDate;
    Era era = date.getEra();
    if (era != null) {
      if (!validateEra(era))
        return false; 
      date.setNormalizedYear(era.getSinceDate().getYear() + date.getYear() - 1);
      Date date1 = newCalendarDate(paramCalendarDate.getZone());
      date1.setEra(era).setDate(paramCalendarDate.getYear(), paramCalendarDate.getMonth(), paramCalendarDate.getDayOfMonth());
      normalize(date1);
      if (date1.getEra() != era)
        return false; 
    } else {
      if (paramCalendarDate.getYear() >= this.eras[0].getSinceDate().getYear())
        return false; 
      date.setNormalizedYear(date.getYear());
    } 
    return super.validate(date);
  }
  
  private boolean validateEra(Era paramEra) {
    for (byte b = 0; b < this.eras.length; b++) {
      if (paramEra == this.eras[b])
        return true; 
    } 
    return false;
  }
  
  public boolean normalize(CalendarDate paramCalendarDate) {
    if (paramCalendarDate.isNormalized())
      return true; 
    normalizeYear(paramCalendarDate);
    Date date = (Date)paramCalendarDate;
    super.normalize(date);
    boolean bool = false;
    long l = 0L;
    int i = date.getNormalizedYear();
    Era era = null;
    int j;
    for (j = this.eras.length - 1; j >= 0; j--) {
      era = this.eras[j];
      if (era.isLocalTime()) {
        CalendarDate calendarDate = era.getSinceDate();
        int k = calendarDate.getYear();
        if (i > k)
          break; 
        if (i == k) {
          int m = date.getMonth();
          int n = calendarDate.getMonth();
          if (m > n)
            break; 
          if (m == n) {
            int i1 = date.getDayOfMonth();
            int i2 = calendarDate.getDayOfMonth();
            if (i1 > i2)
              break; 
            if (i1 == i2) {
              long l1 = date.getTimeOfDay();
              long l2 = calendarDate.getTimeOfDay();
              if (l1 >= l2)
                break; 
              j--;
              break;
            } 
          } 
        } 
      } else {
        if (!bool) {
          l = getTime(paramCalendarDate);
          bool = true;
        } 
        long l1 = era.getSince(paramCalendarDate.getZone());
        if (l >= l1)
          break; 
      } 
    } 
    if (j >= 0) {
      date.setLocalEra(era);
      int k = date.getNormalizedYear() - era.getSinceDate().getYear() + 1;
      date.setLocalYear(k);
    } else {
      date.setEra(null);
      date.setLocalYear(i);
      date.setNormalizedYear(i);
    } 
    date.setNormalized(true);
    return true;
  }
  
  void normalizeMonth(CalendarDate paramCalendarDate) {
    normalizeYear(paramCalendarDate);
    super.normalizeMonth(paramCalendarDate);
  }
  
  void normalizeYear(CalendarDate paramCalendarDate) {
    Date date = (Date)paramCalendarDate;
    Era era = date.getEra();
    if (era == null || !validateEra(era)) {
      date.setNormalizedYear(date.getYear());
    } else {
      date.setNormalizedYear(era.getSinceDate().getYear() + date.getYear() - 1);
    } 
  }
  
  public boolean isLeapYear(int paramInt) { return CalendarUtils.isGregorianLeapYear(paramInt); }
  
  public boolean isLeapYear(Era paramEra, int paramInt) {
    if (paramEra == null)
      return isLeapYear(paramInt); 
    int i = paramEra.getSinceDate().getYear() + paramInt - 1;
    return isLeapYear(i);
  }
  
  public void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong) {
    Date date = (Date)paramCalendarDate;
    super.getCalendarDateFromFixedDate(date, paramLong);
    adjustYear(date, (paramLong - 719163L) * 86400000L, 0);
  }
  
  public static class Date extends BaseCalendar.Date {
    private int gregorianYear = Integer.MIN_VALUE;
    
    protected Date() {}
    
    protected Date(TimeZone param1TimeZone) { super(param1TimeZone); }
    
    public Date setEra(Era param1Era) {
      if (getEra() != param1Era) {
        super.setEra(param1Era);
        this.gregorianYear = Integer.MIN_VALUE;
      } 
      return this;
    }
    
    public Date addYear(int param1Int) {
      super.addYear(param1Int);
      this.gregorianYear += param1Int;
      return this;
    }
    
    public Date setYear(int param1Int) {
      if (getYear() != param1Int) {
        super.setYear(param1Int);
        this.gregorianYear = Integer.MIN_VALUE;
      } 
      return this;
    }
    
    public int getNormalizedYear() { return this.gregorianYear; }
    
    public void setNormalizedYear(int param1Int) { this.gregorianYear = param1Int; }
    
    void setLocalEra(Era param1Era) { super.setEra(param1Era); }
    
    void setLocalYear(int param1Int) { super.setYear(param1Int); }
    
    public String toString() {
      String str = super.toString();
      str = str.substring(str.indexOf('T'));
      StringBuffer stringBuffer = new StringBuffer();
      Era era = getEra();
      if (era != null) {
        String str1 = era.getAbbreviation();
        if (str1 != null)
          stringBuffer.append(str1); 
      } 
      stringBuffer.append(getYear()).append('.');
      CalendarUtils.sprintf0d(stringBuffer, getMonth(), 2).append('.');
      CalendarUtils.sprintf0d(stringBuffer, getDayOfMonth(), 2);
      stringBuffer.append(str);
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\LocalGregorianCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */