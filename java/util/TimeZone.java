package java.util;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.ZoneId;
import sun.security.action.GetPropertyAction;
import sun.util.calendar.ZoneInfo;
import sun.util.calendar.ZoneInfoFile;
import sun.util.locale.provider.TimeZoneNameUtility;

public abstract class TimeZone implements Serializable, Cloneable {
  public static final int SHORT = 0;
  
  public static final int LONG = 1;
  
  private static final int ONE_MINUTE = 60000;
  
  private static final int ONE_HOUR = 3600000;
  
  private static final int ONE_DAY = 86400000;
  
  static final long serialVersionUID = 3581463369166924961L;
  
  static final TimeZone NO_TIMEZONE = null;
  
  private String ID;
  
  static final String GMT_ID = "GMT";
  
  private static final int GMT_ID_LENGTH = 3;
  
  public abstract int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public int getOffset(long paramLong) { return inDaylightTime(new Date(paramLong)) ? (getRawOffset() + getDSTSavings()) : getRawOffset(); }
  
  int getOffsets(long paramLong, int[] paramArrayOfInt) {
    int i = getRawOffset();
    int j = 0;
    if (inDaylightTime(new Date(paramLong)))
      j = getDSTSavings(); 
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = i;
      paramArrayOfInt[1] = j;
    } 
    return i + j;
  }
  
  public abstract void setRawOffset(int paramInt);
  
  public abstract int getRawOffset();
  
  public String getID() { return this.ID; }
  
  public void setID(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.ID = paramString;
  }
  
  public final String getDisplayName() { return getDisplayName(false, 1, Locale.getDefault(Locale.Category.DISPLAY)); }
  
  public final String getDisplayName(Locale paramLocale) { return getDisplayName(false, 1, paramLocale); }
  
  public final String getDisplayName(boolean paramBoolean, int paramInt) { return getDisplayName(paramBoolean, paramInt, Locale.getDefault(Locale.Category.DISPLAY)); }
  
  public String getDisplayName(boolean paramBoolean, int paramInt, Locale paramLocale) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("Illegal style: " + paramInt); 
    String str1 = getID();
    String str2 = TimeZoneNameUtility.retrieveDisplayName(str1, paramBoolean, paramInt, paramLocale);
    if (str2 != null)
      return str2; 
    if (str1.startsWith("GMT") && str1.length() > 3) {
      char c = str1.charAt(3);
      if (c == '+' || c == '-')
        return str1; 
    } 
    int i = getRawOffset();
    if (paramBoolean)
      i += getDSTSavings(); 
    return ZoneInfoFile.toCustomID(i);
  }
  
  private static String[] getDisplayNames(String paramString, Locale paramLocale) { return TimeZoneNameUtility.retrieveDisplayNames(paramString, paramLocale); }
  
  public int getDSTSavings() { return useDaylightTime() ? 3600000 : 0; }
  
  public abstract boolean useDaylightTime();
  
  public boolean observesDaylightTime() { return (useDaylightTime() || inDaylightTime(new Date())); }
  
  public abstract boolean inDaylightTime(Date paramDate);
  
  public static TimeZone getTimeZone(String paramString) { return getTimeZone(paramString, true); }
  
  public static TimeZone getTimeZone(ZoneId paramZoneId) {
    String str = paramZoneId.getId();
    char c = str.charAt(0);
    if (c == '+' || c == '-') {
      str = "GMT" + str;
    } else if (c == 'Z' && str.length() == 1) {
      str = "UTC";
    } 
    return getTimeZone(str, true);
  }
  
  public ZoneId toZoneId() {
    String str = getID();
    if (ZoneInfoFile.useOldMapping() && str.length() == 3) {
      if ("EST".equals(str))
        return ZoneId.of("America/New_York"); 
      if ("MST".equals(str))
        return ZoneId.of("America/Denver"); 
      if ("HST".equals(str))
        return ZoneId.of("America/Honolulu"); 
    } 
    return ZoneId.of(str, ZoneId.SHORT_IDS);
  }
  
  private static TimeZone getTimeZone(String paramString, boolean paramBoolean) {
    TimeZone timeZone = ZoneInfo.getTimeZone(paramString);
    if (timeZone == null) {
      timeZone = parseCustomTimeZone(paramString);
      if (timeZone == null && paramBoolean)
        timeZone = new ZoneInfo("GMT", 0); 
    } 
    return timeZone;
  }
  
  public static String[] getAvailableIDs(int paramInt) { return ZoneInfo.getAvailableIDs(paramInt); }
  
  public static String[] getAvailableIDs() { return ZoneInfo.getAvailableIDs(); }
  
  private static native String getSystemTimeZoneID(String paramString);
  
  private static native String getSystemGMTOffsetID();
  
  public static TimeZone getDefault() { return (TimeZone)getDefaultRef().clone(); }
  
  static TimeZone getDefaultRef() {
    TimeZone timeZone = defaultTimeZone;
    if (timeZone == null) {
      timeZone = setDefaultZone();
      assert timeZone != null;
    } 
    return timeZone;
  }
  
  private static TimeZone setDefaultZone() {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.timezone"));
    if (str1 == null || str1.isEmpty()) {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
      try {
        str1 = getSystemTimeZoneID(str);
        if (str1 == null)
          str1 = "GMT"; 
      } catch (NullPointerException nullPointerException) {
        str1 = "GMT";
      } 
    } 
    TimeZone timeZone = getTimeZone(str1, false);
    if (timeZone == null) {
      String str = getSystemGMTOffsetID();
      if (str != null)
        str1 = str; 
      timeZone = getTimeZone(str1, true);
    } 
    assert timeZone != null;
    final String id = str1;
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.setProperty("user.timezone", id);
            return null;
          }
        });
    defaultTimeZone = timeZone;
    return timeZone;
  }
  
  public static void setDefault(TimeZone paramTimeZone) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new PropertyPermission("user.timezone", "write")); 
    defaultTimeZone = paramTimeZone;
  }
  
  public boolean hasSameRules(TimeZone paramTimeZone) { return (paramTimeZone != null && getRawOffset() == paramTimeZone.getRawOffset() && useDaylightTime() == paramTimeZone.useDaylightTime()); }
  
  public Object clone() {
    try {
      TimeZone timeZone = (TimeZone)super.clone();
      timeZone.ID = this.ID;
      return timeZone;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private static final TimeZone parseCustomTimeZone(String paramString) {
    int i;
    if ((i = paramString.length()) < 5 || paramString.indexOf("GMT") != 0)
      return null; 
    ZoneInfo zoneInfo = ZoneInfoFile.getZoneInfo(paramString);
    if (zoneInfo != null)
      return zoneInfo; 
    byte b1 = 3;
    boolean bool = false;
    char c = paramString.charAt(b1++);
    if (c == '-') {
      bool = true;
    } else if (c != '+') {
      return null;
    } 
    char c1 = Character.MIN_VALUE;
    char c2 = Character.MIN_VALUE;
    byte b2 = 0;
    byte b3;
    for (b3 = 0; b1 < i; b3++) {
      c = paramString.charAt(b1++);
      if (c == ':') {
        if (b2)
          return null; 
        if (b3 > 2)
          return null; 
        c1 = c2;
        b2++;
        c2 = Character.MIN_VALUE;
        b3 = 0;
        continue;
      } 
      if (c < '0' || c > '9')
        return null; 
      c2 = c2 * 10 + c - '0';
    } 
    if (b1 != i)
      return null; 
    if (b2 == 0) {
      if (b3 <= 2) {
        c1 = c2;
        c2 = Character.MIN_VALUE;
      } else {
        c1 = c2 / 'd';
        c2 %= 'd';
      } 
    } else if (b3 != 2) {
      return null;
    } 
    if (c1 > '\027' || c2 > ';')
      return null; 
    char c3 = (c1 * '<' + c2) * '<' * 'Ϩ';
    if (c3 == '\000') {
      zoneInfo = ZoneInfoFile.getZoneInfo("GMT");
      if (bool) {
        zoneInfo.setID("GMT-00:00");
      } else {
        zoneInfo.setID("GMT+00:00");
      } 
    } else {
      zoneInfo = ZoneInfoFile.getCustomTimeZone(paramString, bool ? -c3 : c3);
    } 
    return zoneInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\TimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */