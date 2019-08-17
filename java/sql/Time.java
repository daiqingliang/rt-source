package java.sql;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;

public class Time extends Date {
  static final long serialVersionUID = 8397324403548013681L;
  
  @Deprecated
  public Time(int paramInt1, int paramInt2, int paramInt3) { super(70, 0, 1, paramInt1, paramInt2, paramInt3); }
  
  public Time(long paramLong) { super(paramLong); }
  
  public void setTime(long paramLong) { super.setTime(paramLong); }
  
  public static Time valueOf(String paramString) {
    int k;
    int j;
    int i;
    if (paramString == null)
      throw new IllegalArgumentException(); 
    int m = paramString.indexOf(':');
    int n = paramString.indexOf(':', m + 1);
    if (((m > 0) ? 1 : 0) & ((n > 0) ? 1 : 0) & ((n < paramString.length() - 1) ? 1 : 0)) {
      i = Integer.parseInt(paramString.substring(0, m));
      j = Integer.parseInt(paramString.substring(m + 1, n));
      k = Integer.parseInt(paramString.substring(n + 1));
    } else {
      throw new IllegalArgumentException();
    } 
    return new Time(i, j, k);
  }
  
  public String toString() {
    String str3;
    String str2;
    String str1;
    int i = getHours();
    int j = getMinutes();
    int k = getSeconds();
    if (i < 10) {
      str1 = "0" + i;
    } else {
      str1 = Integer.toString(i);
    } 
    if (j < 10) {
      str2 = "0" + j;
    } else {
      str2 = Integer.toString(j);
    } 
    if (k < 10) {
      str3 = "0" + k;
    } else {
      str3 = Integer.toString(k);
    } 
    return str1 + ":" + str2 + ":" + str3;
  }
  
  @Deprecated
  public int getYear() { throw new IllegalArgumentException(); }
  
  @Deprecated
  public int getMonth() { throw new IllegalArgumentException(); }
  
  @Deprecated
  public int getDay() { throw new IllegalArgumentException(); }
  
  @Deprecated
  public int getDate() { throw new IllegalArgumentException(); }
  
  @Deprecated
  public void setYear(int paramInt) { throw new IllegalArgumentException(); }
  
  @Deprecated
  public void setMonth(int paramInt) { throw new IllegalArgumentException(); }
  
  @Deprecated
  public void setDate(int paramInt) { throw new IllegalArgumentException(); }
  
  public static Time valueOf(LocalTime paramLocalTime) { return new Time(paramLocalTime.getHour(), paramLocalTime.getMinute(), paramLocalTime.getSecond()); }
  
  public LocalTime toLocalTime() { return LocalTime.of(getHours(), getMinutes(), getSeconds()); }
  
  public Instant toInstant() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Time.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */