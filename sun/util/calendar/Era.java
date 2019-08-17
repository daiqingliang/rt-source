package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

public final class Era {
  private final String name;
  
  private final String abbr;
  
  private final long since;
  
  private final CalendarDate sinceDate;
  
  private final boolean localTime;
  
  private int hash = 0;
  
  public Era(String paramString1, String paramString2, long paramLong, boolean paramBoolean) {
    this.name = paramString1;
    this.abbr = paramString2;
    this.since = paramLong;
    this.localTime = paramBoolean;
    Gregorian gregorian = CalendarSystem.getGregorianCalendar();
    Gregorian.Date date = gregorian.newCalendarDate(null);
    gregorian.getCalendarDate(paramLong, date);
    this.sinceDate = new ImmutableGregorianDate(date);
  }
  
  public String getName() { return this.name; }
  
  public String getDisplayName(Locale paramLocale) { return this.name; }
  
  public String getAbbreviation() { return this.abbr; }
  
  public String getDiaplayAbbreviation(Locale paramLocale) { return this.abbr; }
  
  public long getSince(TimeZone paramTimeZone) {
    if (paramTimeZone == null || !this.localTime)
      return this.since; 
    int i = paramTimeZone.getOffset(this.since);
    return this.since - i;
  }
  
  public CalendarDate getSinceDate() { return this.sinceDate; }
  
  public boolean isLocalTime() { return this.localTime; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Era))
      return false; 
    Era era = (Era)paramObject;
    return (this.name.equals(era.name) && this.abbr.equals(era.abbr) && this.since == era.since && this.localTime == era.localTime);
  }
  
  public int hashCode() {
    if (this.hash == 0)
      this.hash = this.name.hashCode() ^ this.abbr.hashCode() ^ (int)this.since ^ (int)(this.since >> 32) ^ (this.localTime ? 1 : 0); 
    return this.hash;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    stringBuilder.append(getName()).append(" (");
    stringBuilder.append(getAbbreviation()).append(')');
    stringBuilder.append(" since ").append(getSinceDate());
    if (this.localTime) {
      stringBuilder.setLength(stringBuilder.length() - 1);
      stringBuilder.append(" local time");
    } 
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\Era.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */