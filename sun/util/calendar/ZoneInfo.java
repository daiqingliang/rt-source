package sun.util.calendar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class ZoneInfo extends TimeZone {
  private static final int UTC_TIME = 0;
  
  private static final int STANDARD_TIME = 1;
  
  private static final int WALL_TIME = 2;
  
  private static final long OFFSET_MASK = 15L;
  
  private static final long DST_MASK = 240L;
  
  private static final int DST_NSHIFT = 4;
  
  private static final long ABBR_MASK = 3840L;
  
  private static final int TRANSITION_NSHIFT = 12;
  
  private static final CalendarSystem gcal = CalendarSystem.getGregorianCalendar();
  
  private int rawOffset;
  
  private int rawOffsetDiff = 0;
  
  private int checksum;
  
  private int dstSavings;
  
  private long[] transitions;
  
  private int[] offsets;
  
  private int[] simpleTimeZoneParams;
  
  private boolean willGMTOffsetChange = false;
  
  private boolean dirty = false;
  
  private static final long serialVersionUID = 2653134537216586139L;
  
  private SimpleTimeZone lastRule;
  
  public ZoneInfo() {}
  
  public ZoneInfo(String paramString, int paramInt) { this(paramString, paramInt, 0, 0, null, null, null, false); }
  
  ZoneInfo(String paramString, int paramInt1, int paramInt2, int paramInt3, long[] paramArrayOfLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean) {
    setID(paramString);
    this.rawOffset = paramInt1;
    this.dstSavings = paramInt2;
    this.checksum = paramInt3;
    this.transitions = paramArrayOfLong;
    this.offsets = paramArrayOfInt1;
    this.simpleTimeZoneParams = paramArrayOfInt2;
    this.willGMTOffsetChange = paramBoolean;
  }
  
  public int getOffset(long paramLong) { return getOffsets(paramLong, null, 0); }
  
  public int getOffsets(long paramLong, int[] paramArrayOfInt) { return getOffsets(paramLong, paramArrayOfInt, 0); }
  
  public int getOffsetsByStandard(long paramLong, int[] paramArrayOfInt) { return getOffsets(paramLong, paramArrayOfInt, 1); }
  
  public int getOffsetsByWall(long paramLong, int[] paramArrayOfInt) { return getOffsets(paramLong, paramArrayOfInt, 2); }
  
  private int getOffsets(long paramLong, int[] paramArrayOfInt, int paramInt) {
    if (this.transitions == null) {
      int k = getLastRawOffset();
      if (paramArrayOfInt != null) {
        paramArrayOfInt[0] = k;
        paramArrayOfInt[1] = 0;
      } 
      return k;
    } 
    paramLong -= this.rawOffsetDiff;
    int i = getTransitionIndex(paramLong, paramInt);
    if (i < 0) {
      int k = getLastRawOffset();
      if (paramArrayOfInt != null) {
        paramArrayOfInt[0] = k;
        paramArrayOfInt[1] = 0;
      } 
      return k;
    } 
    if (i < this.transitions.length) {
      long l = this.transitions[i];
      int k = this.offsets[(int)(l & 0xFL)] + this.rawOffsetDiff;
      if (paramArrayOfInt != null) {
        int m = (int)(l >>> 4 & 0xFL);
        int n = (m == 0) ? 0 : this.offsets[m];
        paramArrayOfInt[0] = k - n;
        paramArrayOfInt[1] = n;
      } 
      return k;
    } 
    SimpleTimeZone simpleTimeZone = getLastRule();
    if (simpleTimeZone != null) {
      int k = simpleTimeZone.getRawOffset();
      long l = paramLong;
      if (paramInt != 0)
        l -= this.rawOffset; 
      int m = simpleTimeZone.getOffset(l) - this.rawOffset;
      if (m > 0 && simpleTimeZone.getOffset(l - m) == k)
        m = 0; 
      if (paramArrayOfInt != null) {
        paramArrayOfInt[0] = k;
        paramArrayOfInt[1] = m;
      } 
      return k + m;
    } 
    int j = getLastRawOffset();
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = j;
      paramArrayOfInt[1] = 0;
    } 
    return j;
  }
  
  private int getTransitionIndex(long paramLong, int paramInt) {
    int i = 0;
    int j = this.transitions.length - 1;
    while (i <= j) {
      int k = (i + j) / 2;
      long l1 = this.transitions[k];
      long l2 = l1 >> 12;
      if (paramInt != 0)
        l2 += this.offsets[(int)(l1 & 0xFL)]; 
      if (paramInt == 1) {
        int m = (int)(l1 >>> 4 & 0xFL);
        if (m != 0)
          l2 -= this.offsets[m]; 
      } 
      if (l2 < paramLong) {
        i = k + 1;
        continue;
      } 
      if (l2 > paramLong) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return (i >= this.transitions.length) ? i : (i - 1);
  }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (paramInt6 < 0 || paramInt6 >= 86400000)
      throw new IllegalArgumentException(); 
    if (paramInt1 == 0) {
      paramInt2 = 1 - paramInt2;
    } else if (paramInt1 != 1) {
      throw new IllegalArgumentException();
    } 
    CalendarDate calendarDate = gcal.newCalendarDate(null);
    calendarDate.setDate(paramInt2, paramInt3 + 1, paramInt4);
    if (!gcal.validate(calendarDate))
      throw new IllegalArgumentException(); 
    if (paramInt5 < 1 || paramInt5 > 7)
      throw new IllegalArgumentException(); 
    if (this.transitions == null)
      return getLastRawOffset(); 
    long l = gcal.getTime(calendarDate) + paramInt6;
    l -= this.rawOffset;
    return getOffsets(l, null, 0);
  }
  
  public void setRawOffset(int paramInt) {
    if (paramInt == this.rawOffset + this.rawOffsetDiff)
      return; 
    this.rawOffsetDiff = paramInt - this.rawOffset;
    if (this.lastRule != null)
      this.lastRule.setRawOffset(paramInt); 
    this.dirty = true;
  }
  
  public int getRawOffset() {
    if (!this.willGMTOffsetChange)
      return this.rawOffset + this.rawOffsetDiff; 
    int[] arrayOfInt = new int[2];
    getOffsets(System.currentTimeMillis(), arrayOfInt, 0);
    return arrayOfInt[0];
  }
  
  public boolean isDirty() { return this.dirty; }
  
  private int getLastRawOffset() { return this.rawOffset + this.rawOffsetDiff; }
  
  public boolean useDaylightTime() { return (this.simpleTimeZoneParams != null); }
  
  public boolean observesDaylightTime() {
    if (this.simpleTimeZoneParams != null)
      return true; 
    if (this.transitions == null)
      return false; 
    long l = System.currentTimeMillis() - this.rawOffsetDiff;
    int i = getTransitionIndex(l, 0);
    if (i < 0)
      return false; 
    for (int j = i; j < this.transitions.length; j++) {
      if ((this.transitions[j] & 0xF0L) != 0L)
        return true; 
    } 
    return false;
  }
  
  public boolean inDaylightTime(Date paramDate) {
    if (paramDate == null)
      throw new NullPointerException(); 
    if (this.transitions == null)
      return false; 
    long l = paramDate.getTime() - this.rawOffsetDiff;
    int i = getTransitionIndex(l, 0);
    if (i < 0)
      return false; 
    if (i < this.transitions.length)
      return ((this.transitions[i] & 0xF0L) != 0L); 
    SimpleTimeZone simpleTimeZone = getLastRule();
    return (simpleTimeZone != null) ? simpleTimeZone.inDaylightTime(paramDate) : 0;
  }
  
  public int getDSTSavings() { return this.dstSavings; }
  
  public String toString() { return getClass().getName() + "[id=\"" + getID() + "\",offset=" + getLastRawOffset() + ",dstSavings=" + this.dstSavings + ",useDaylight=" + useDaylightTime() + ",transitions=" + ((this.transitions != null) ? this.transitions.length : 0) + ",lastRule=" + ((this.lastRule == null) ? getLastRuleInstance() : this.lastRule) + "]"; }
  
  public static String[] getAvailableIDs() { return ZoneInfoFile.getZoneIds(); }
  
  public static String[] getAvailableIDs(int paramInt) { return ZoneInfoFile.getZoneIds(paramInt); }
  
  public static TimeZone getTimeZone(String paramString) { return ZoneInfoFile.getZoneInfo(paramString); }
  
  private SimpleTimeZone getLastRule() {
    if (this.lastRule == null)
      this.lastRule = getLastRuleInstance(); 
    return this.lastRule;
  }
  
  public SimpleTimeZone getLastRuleInstance() { return (this.simpleTimeZoneParams == null) ? null : ((this.simpleTimeZoneParams.length == 10) ? new SimpleTimeZone(getLastRawOffset(), getID(), this.simpleTimeZoneParams[0], this.simpleTimeZoneParams[1], this.simpleTimeZoneParams[2], this.simpleTimeZoneParams[3], this.simpleTimeZoneParams[4], this.simpleTimeZoneParams[5], this.simpleTimeZoneParams[6], this.simpleTimeZoneParams[7], this.simpleTimeZoneParams[8], this.simpleTimeZoneParams[9], this.dstSavings) : new SimpleTimeZone(getLastRawOffset(), getID(), this.simpleTimeZoneParams[0], this.simpleTimeZoneParams[1], this.simpleTimeZoneParams[2], this.simpleTimeZoneParams[3], this.simpleTimeZoneParams[4], this.simpleTimeZoneParams[5], this.simpleTimeZoneParams[6], this.simpleTimeZoneParams[7], this.dstSavings)); }
  
  public Object clone() {
    ZoneInfo zoneInfo = (ZoneInfo)super.clone();
    zoneInfo.lastRule = null;
    return zoneInfo;
  }
  
  public int hashCode() { return getLastRawOffset() ^ this.checksum; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ZoneInfo))
      return false; 
    ZoneInfo zoneInfo = (ZoneInfo)paramObject;
    return (getID().equals(zoneInfo.getID()) && getLastRawOffset() == zoneInfo.getLastRawOffset() && this.checksum == zoneInfo.checksum);
  }
  
  public boolean hasSameRules(TimeZone paramTimeZone) { return (this == paramTimeZone) ? true : ((paramTimeZone == null) ? false : (!(paramTimeZone instanceof ZoneInfo) ? ((getRawOffset() != paramTimeZone.getRawOffset()) ? false : ((this.transitions == null && !useDaylightTime() && !paramTimeZone.useDaylightTime()))) : ((getLastRawOffset() != ((ZoneInfo)paramTimeZone).getLastRawOffset()) ? false : ((this.checksum == ((ZoneInfo)paramTimeZone).checksum))))); }
  
  public static Map<String, String> getAliasTable() { return ZoneInfoFile.getAliasMap(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.dirty = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\ZoneInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */