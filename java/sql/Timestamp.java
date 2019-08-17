package java.sql;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class Timestamp extends Date {
  private int nanos;
  
  static final long serialVersionUID = 2745179027874758501L;
  
  private static final int MILLIS_PER_SECOND = 1000;
  
  @Deprecated
  public Timestamp(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    if (paramInt7 > 999999999 || paramInt7 < 0)
      throw new IllegalArgumentException("nanos > 999999999 or < 0"); 
    this.nanos = paramInt7;
  }
  
  public Timestamp(long paramLong) {
    super(paramLong / 1000L * 1000L);
    this.nanos = (int)(paramLong % 1000L * 1000000L);
    if (this.nanos < 0) {
      this.nanos = 1000000000 + this.nanos;
      super.setTime((paramLong / 1000L - 1L) * 1000L);
    } 
  }
  
  public void setTime(long paramLong) {
    super.setTime(paramLong / 1000L * 1000L);
    this.nanos = (int)(paramLong % 1000L * 1000000L);
    if (this.nanos < 0) {
      this.nanos = 1000000000 + this.nanos;
      super.setTime((paramLong / 1000L - 1L) * 1000L);
    } 
  }
  
  public long getTime() {
    long l = super.getTime();
    return l + (this.nanos / 1000000);
  }
  
  public static Timestamp valueOf(String paramString) {
    int i1;
    int n;
    int m;
    String str2;
    String str1;
    int i = 0;
    int j = 0;
    int k = 0;
    int i2 = 0;
    int i6 = 0;
    int i7 = 0;
    int i8 = 0;
    String str3 = "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]";
    String str4 = "000000000";
    String str5 = "-";
    String str6 = ":";
    if (paramString == null)
      throw new IllegalArgumentException("null string"); 
    paramString = paramString.trim();
    int i5 = paramString.indexOf(' ');
    if (i5 > 0) {
      str1 = paramString.substring(0, i5);
      str2 = paramString.substring(i5 + 1);
    } else {
      throw new IllegalArgumentException(str3);
    } 
    int i3 = str1.indexOf('-');
    int i4 = str1.indexOf('-', i3 + 1);
    if (str2 == null)
      throw new IllegalArgumentException(str3); 
    i6 = str2.indexOf(':');
    i7 = str2.indexOf(':', i6 + 1);
    i8 = str2.indexOf('.', i7 + 1);
    boolean bool = false;
    if (i3 > 0 && i4 > 0 && i4 < str1.length() - 1) {
      String str7 = str1.substring(0, i3);
      String str8 = str1.substring(i3 + 1, i4);
      String str9 = str1.substring(i4 + 1);
      if (str7.length() == 4 && str8.length() >= 1 && str8.length() <= 2 && str9.length() >= 1 && str9.length() <= 2) {
        i = Integer.parseInt(str7);
        j = Integer.parseInt(str8);
        k = Integer.parseInt(str9);
        if (j >= 1 && j <= 12 && k >= 1 && k <= 31)
          bool = true; 
      } 
    } 
    if (!bool)
      throw new IllegalArgumentException(str3); 
    if (((i6 > 0) ? 1 : 0) & ((i7 > 0) ? 1 : 0) & ((i7 < str2.length() - 1) ? 1 : 0)) {
      m = Integer.parseInt(str2.substring(0, i6));
      n = Integer.parseInt(str2.substring(i6 + 1, i7));
      if (((i8 > 0) ? 1 : 0) & ((i8 < str2.length() - 1) ? 1 : 0)) {
        i1 = Integer.parseInt(str2.substring(i7 + 1, i8));
        String str = str2.substring(i8 + 1);
        if (str.length() > 9)
          throw new IllegalArgumentException(str3); 
        if (!Character.isDigit(str.charAt(0)))
          throw new IllegalArgumentException(str3); 
        str = str + str4.substring(0, 9 - str.length());
        i2 = Integer.parseInt(str);
      } else {
        if (i8 > 0)
          throw new IllegalArgumentException(str3); 
        i1 = Integer.parseInt(str2.substring(i7 + 1));
      } 
    } else {
      throw new IllegalArgumentException(str3);
    } 
    return new Timestamp(i - 1900, j - 1, k, m, n, i1, i2);
  }
  
  public String toString() {
    String str7;
    String str6;
    String str5;
    String str4;
    String str3;
    String str2;
    String str1;
    int i = getYear() + 1900;
    int j = getMonth() + 1;
    int k = getDate();
    int m = getHours();
    int n = getMinutes();
    int i1 = getSeconds();
    String str8 = "000000000";
    String str9 = "0000";
    if (i < 1000) {
      str1 = "" + i;
      str1 = str9.substring(0, 4 - str1.length()) + str1;
    } else {
      str1 = "" + i;
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
    if (m < 10) {
      str4 = "0" + m;
    } else {
      str4 = Integer.toString(m);
    } 
    if (n < 10) {
      str5 = "0" + n;
    } else {
      str5 = Integer.toString(n);
    } 
    if (i1 < 10) {
      str6 = "0" + i1;
    } else {
      str6 = Integer.toString(i1);
    } 
    if (this.nanos == 0) {
      str7 = "0";
    } else {
      str7 = Integer.toString(this.nanos);
      str7 = str8.substring(0, 9 - str7.length()) + str7;
      char[] arrayOfChar = new char[str7.length()];
      str7.getChars(0, str7.length(), arrayOfChar, 0);
      byte b;
      for (b = 8; arrayOfChar[b] == '0'; b--);
      str7 = new String(arrayOfChar, 0, b + 1);
    } 
    StringBuffer stringBuffer = new StringBuffer(20 + str7.length());
    stringBuffer.append(str1);
    stringBuffer.append("-");
    stringBuffer.append(str2);
    stringBuffer.append("-");
    stringBuffer.append(str3);
    stringBuffer.append(" ");
    stringBuffer.append(str4);
    stringBuffer.append(":");
    stringBuffer.append(str5);
    stringBuffer.append(":");
    stringBuffer.append(str6);
    stringBuffer.append(".");
    stringBuffer.append(str7);
    return stringBuffer.toString();
  }
  
  public int getNanos() { return this.nanos; }
  
  public void setNanos(int paramInt) {
    if (paramInt > 999999999 || paramInt < 0)
      throw new IllegalArgumentException("nanos > 999999999 or < 0"); 
    this.nanos = paramInt;
  }
  
  public boolean equals(Timestamp paramTimestamp) { return super.equals(paramTimestamp) ? ((this.nanos == paramTimestamp.nanos)) : false; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Timestamp) ? equals((Timestamp)paramObject) : 0; }
  
  public boolean before(Timestamp paramTimestamp) { return (compareTo(paramTimestamp) < 0); }
  
  public boolean after(Timestamp paramTimestamp) { return (compareTo(paramTimestamp) > 0); }
  
  public int compareTo(Timestamp paramTimestamp) {
    long l1 = getTime();
    long l2 = paramTimestamp.getTime();
    byte b = (l1 < l2) ? -1 : ((l1 == l2) ? 0 : 1);
    if (b == 0) {
      if (this.nanos > paramTimestamp.nanos)
        return 1; 
      if (this.nanos < paramTimestamp.nanos)
        return -1; 
    } 
    return b;
  }
  
  public int compareTo(Date paramDate) {
    if (paramDate instanceof Timestamp)
      return compareTo((Timestamp)paramDate); 
    Timestamp timestamp = new Timestamp(paramDate.getTime());
    return compareTo(timestamp);
  }
  
  public int hashCode() { return super.hashCode(); }
  
  public static Timestamp valueOf(LocalDateTime paramLocalDateTime) { return new Timestamp(paramLocalDateTime.getYear() - 1900, paramLocalDateTime.getMonthValue() - 1, paramLocalDateTime.getDayOfMonth(), paramLocalDateTime.getHour(), paramLocalDateTime.getMinute(), paramLocalDateTime.getSecond(), paramLocalDateTime.getNano()); }
  
  public LocalDateTime toLocalDateTime() { return LocalDateTime.of(getYear() + 1900, getMonth() + 1, getDate(), getHours(), getMinutes(), getSeconds(), getNanos()); }
  
  public static Timestamp from(Instant paramInstant) {
    try {
      Timestamp timestamp = new Timestamp(paramInstant.getEpochSecond() * 1000L);
      timestamp.nanos = paramInstant.getNano();
      return timestamp;
    } catch (ArithmeticException arithmeticException) {
      throw new IllegalArgumentException(arithmeticException);
    } 
  }
  
  public Instant toInstant() { return Instant.ofEpochSecond(super.getTime() / 1000L, this.nanos); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Timestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */