package sun.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import sun.util.locale.provider.CalendarDataUtility;

public class BuddhistCalendar extends GregorianCalendar {
  private static final long serialVersionUID = -8527488697350388578L;
  
  private static final int BUDDHIST_YEAR_OFFSET = 543;
  
  private int yearOffset = 543;
  
  public BuddhistCalendar() {}
  
  public BuddhistCalendar(TimeZone paramTimeZone) { super(paramTimeZone); }
  
  public BuddhistCalendar(Locale paramLocale) { super(paramLocale); }
  
  public BuddhistCalendar(TimeZone paramTimeZone, Locale paramLocale) { super(paramTimeZone, paramLocale); }
  
  public String getCalendarType() { return "buddhist"; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof BuddhistCalendar && super.equals(paramObject)); }
  
  public int hashCode() { return super.hashCode() ^ 0x21F; }
  
  public int get(int paramInt) { return (paramInt == 1) ? (super.get(paramInt) + this.yearOffset) : super.get(paramInt); }
  
  public void set(int paramInt1, int paramInt2) {
    if (paramInt1 == 1) {
      super.set(paramInt1, paramInt2 - this.yearOffset);
    } else {
      super.set(paramInt1, paramInt2);
    } 
  }
  
  public void add(int paramInt1, int paramInt2) {
    i = this.yearOffset;
    this.yearOffset = 0;
    try {
      super.add(paramInt1, paramInt2);
    } finally {
      this.yearOffset = i;
    } 
  }
  
  public void roll(int paramInt1, int paramInt2) {
    i = this.yearOffset;
    this.yearOffset = 0;
    try {
      super.roll(paramInt1, paramInt2);
    } finally {
      this.yearOffset = i;
    } 
  }
  
  public String getDisplayName(int paramInt1, int paramInt2, Locale paramLocale) { return (paramInt1 != 0) ? super.getDisplayName(paramInt1, paramInt2, paramLocale) : CalendarDataUtility.retrieveFieldValueName("buddhist", paramInt1, get(paramInt1), paramInt2, paramLocale); }
  
  public Map<String, Integer> getDisplayNames(int paramInt1, int paramInt2, Locale paramLocale) { return (paramInt1 != 0) ? super.getDisplayNames(paramInt1, paramInt2, paramLocale) : CalendarDataUtility.retrieveFieldValueNames("buddhist", paramInt1, paramInt2, paramLocale); }
  
  public int getActualMaximum(int paramInt) {
    i = this.yearOffset;
    this.yearOffset = 0;
    try {
      return super.getActualMaximum(paramInt);
    } finally {
      this.yearOffset = i;
    } 
  }
  
  public String toString() {
    String str = super.toString();
    if (!isSet(1))
      return str; 
    int i = str.indexOf("YEAR=");
    if (i == -1)
      return str; 
    i += "YEAR=".length();
    StringBuilder stringBuilder = new StringBuilder(str.substring(0, i));
    while (Character.isDigit(str.charAt(i++)));
    int j = internalGet(1) + 543;
    stringBuilder.append(j).append(str.substring(i - 1));
    return stringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.yearOffset = 543;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\BuddhistCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */