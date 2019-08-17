package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public final class ZoneOffsetTransitionRule implements Serializable {
  private static final long serialVersionUID = 6889046316657758795L;
  
  private final Month month;
  
  private final byte dom;
  
  private final DayOfWeek dow;
  
  private final LocalTime time;
  
  private final boolean timeEndOfDay;
  
  private final TimeDefinition timeDefinition;
  
  private final ZoneOffset standardOffset;
  
  private final ZoneOffset offsetBefore;
  
  private final ZoneOffset offsetAfter;
  
  public static ZoneOffsetTransitionRule of(Month paramMonth, int paramInt, DayOfWeek paramDayOfWeek, LocalTime paramLocalTime, boolean paramBoolean, TimeDefinition paramTimeDefinition, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, ZoneOffset paramZoneOffset3) {
    Objects.requireNonNull(paramMonth, "month");
    Objects.requireNonNull(paramLocalTime, "time");
    Objects.requireNonNull(paramTimeDefinition, "timeDefnition");
    Objects.requireNonNull(paramZoneOffset1, "standardOffset");
    Objects.requireNonNull(paramZoneOffset2, "offsetBefore");
    Objects.requireNonNull(paramZoneOffset3, "offsetAfter");
    if (paramInt < -28 || paramInt > 31 || paramInt == 0)
      throw new IllegalArgumentException("Day of month indicator must be between -28 and 31 inclusive excluding zero"); 
    if (paramBoolean && !paramLocalTime.equals(LocalTime.MIDNIGHT))
      throw new IllegalArgumentException("Time must be midnight when end of day flag is true"); 
    return new ZoneOffsetTransitionRule(paramMonth, paramInt, paramDayOfWeek, paramLocalTime, paramBoolean, paramTimeDefinition, paramZoneOffset1, paramZoneOffset2, paramZoneOffset3);
  }
  
  ZoneOffsetTransitionRule(Month paramMonth, int paramInt, DayOfWeek paramDayOfWeek, LocalTime paramLocalTime, boolean paramBoolean, TimeDefinition paramTimeDefinition, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, ZoneOffset paramZoneOffset3) {
    this.month = paramMonth;
    this.dom = (byte)paramInt;
    this.dow = paramDayOfWeek;
    this.time = paramLocalTime;
    this.timeEndOfDay = paramBoolean;
    this.timeDefinition = paramTimeDefinition;
    this.standardOffset = paramZoneOffset1;
    this.offsetBefore = paramZoneOffset2;
    this.offsetAfter = paramZoneOffset3;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)3, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    int i = this.timeEndOfDay ? 86400 : this.time.toSecondOfDay();
    int j = this.standardOffset.getTotalSeconds();
    int k = this.offsetBefore.getTotalSeconds() - j;
    int m = this.offsetAfter.getTotalSeconds() - j;
    byte b = (i % 3600 == 0) ? (this.timeEndOfDay ? 24 : this.time.getHour()) : 31;
    int n = (j % 900 == 0) ? (j / 900 + 128) : 255;
    int i1 = (k == 0 || k == 1800 || k == 3600) ? (k / 1800) : 3;
    int i2 = (m == 0 || m == 1800 || m == 3600) ? (m / 1800) : 3;
    boolean bool = (this.dow == null) ? 0 : this.dow.getValue();
    int i3 = (this.month.getValue() << 28) + (this.dom + 32 << 22) + (bool << 19) + (b << 14) + (this.timeDefinition.ordinal() << 12) + (n << 4) + (i1 << 2) + i2;
    paramDataOutput.writeInt(i3);
    if (b == 31)
      paramDataOutput.writeInt(i); 
    if (n == 255)
      paramDataOutput.writeInt(j); 
    if (i1 == 3)
      paramDataOutput.writeInt(this.offsetBefore.getTotalSeconds()); 
    if (i2 == 3)
      paramDataOutput.writeInt(this.offsetAfter.getTotalSeconds()); 
  }
  
  static ZoneOffsetTransitionRule readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    Month month1 = Month.of(i >>> 28);
    int j = ((i & 0xFC00000) >>> 22) - 32;
    int k = (i & 0x380000) >>> 19;
    DayOfWeek dayOfWeek = (k == 0) ? null : DayOfWeek.of(k);
    int m = (i & 0x7C000) >>> 14;
    TimeDefinition timeDefinition1 = TimeDefinition.values()[(i & 0x3000) >>> 12];
    int n = (i & 0xFF0) >>> 4;
    int i1 = (i & 0xC) >>> 2;
    int i2 = i & 0x3;
    LocalTime localTime = (m == 31) ? LocalTime.ofSecondOfDay(paramDataInput.readInt()) : LocalTime.of(m % 24, 0);
    ZoneOffset zoneOffset1;
    ZoneOffset zoneOffset2;
    ZoneOffset zoneOffset3 = (i2 == 3) ? (zoneOffset2 = (i1 == 3) ? (zoneOffset1 = (n == 255) ? ZoneOffset.ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds((n - 128) * 900)).ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds(zoneOffset1.getTotalSeconds() + i1 * 1800)).ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds(zoneOffset1.getTotalSeconds() + i2 * 1800);
    return of(month1, j, dayOfWeek, localTime, (m == 24), timeDefinition1, zoneOffset1, zoneOffset2, zoneOffset3);
  }
  
  public Month getMonth() { return this.month; }
  
  public int getDayOfMonthIndicator() { return this.dom; }
  
  public DayOfWeek getDayOfWeek() { return this.dow; }
  
  public LocalTime getLocalTime() { return this.time; }
  
  public boolean isMidnightEndOfDay() { return this.timeEndOfDay; }
  
  public TimeDefinition getTimeDefinition() { return this.timeDefinition; }
  
  public ZoneOffset getStandardOffset() { return this.standardOffset; }
  
  public ZoneOffset getOffsetBefore() { return this.offsetBefore; }
  
  public ZoneOffset getOffsetAfter() { return this.offsetAfter; }
  
  public ZoneOffsetTransition createTransition(int paramInt) {
    LocalDate localDate;
    if (this.dom < 0) {
      localDate = LocalDate.of(paramInt, this.month, this.month.length(IsoChronology.INSTANCE.isLeapYear(paramInt)) + 1 + this.dom);
      if (this.dow != null)
        localDate = localDate.with(TemporalAdjusters.previousOrSame(this.dow)); 
    } else {
      localDate = LocalDate.of(paramInt, this.month, this.dom);
      if (this.dow != null)
        localDate = localDate.with(TemporalAdjusters.nextOrSame(this.dow)); 
    } 
    if (this.timeEndOfDay)
      localDate = localDate.plusDays(1L); 
    LocalDateTime localDateTime1 = LocalDateTime.of(localDate, this.time);
    LocalDateTime localDateTime2 = this.timeDefinition.createDateTime(localDateTime1, this.standardOffset, this.offsetBefore);
    return new ZoneOffsetTransition(localDateTime2, this.offsetBefore, this.offsetAfter);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof ZoneOffsetTransitionRule) {
      ZoneOffsetTransitionRule zoneOffsetTransitionRule = (ZoneOffsetTransitionRule)paramObject;
      return (this.month == zoneOffsetTransitionRule.month && this.dom == zoneOffsetTransitionRule.dom && this.dow == zoneOffsetTransitionRule.dow && this.timeDefinition == zoneOffsetTransitionRule.timeDefinition && this.time.equals(zoneOffsetTransitionRule.time) && this.timeEndOfDay == zoneOffsetTransitionRule.timeEndOfDay && this.standardOffset.equals(zoneOffsetTransitionRule.standardOffset) && this.offsetBefore.equals(zoneOffsetTransitionRule.offsetBefore) && this.offsetAfter.equals(zoneOffsetTransitionRule.offsetAfter));
    } 
    return false;
  }
  
  public int hashCode() {
    int i = (this.time.toSecondOfDay() + (this.timeEndOfDay ? 1 : 0) << 15) + (this.month.ordinal() << 11) + (this.dom + 32 << 5) + (((this.dow == null) ? 7 : this.dow.ordinal()) << 2) + this.timeDefinition.ordinal();
    return i ^ this.standardOffset.hashCode() ^ this.offsetBefore.hashCode() ^ this.offsetAfter.hashCode();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("TransitionRule[").append((this.offsetBefore.compareTo(this.offsetAfter) > 0) ? "Gap " : "Overlap ").append(this.offsetBefore).append(" to ").append(this.offsetAfter).append(", ");
    if (this.dow != null) {
      if (this.dom == -1) {
        stringBuilder.append(this.dow.name()).append(" on or before last day of ").append(this.month.name());
      } else if (this.dom < 0) {
        stringBuilder.append(this.dow.name()).append(" on or before last day minus ").append(-this.dom - 1).append(" of ").append(this.month.name());
      } else {
        stringBuilder.append(this.dow.name()).append(" on or after ").append(this.month.name()).append(' ').append(this.dom);
      } 
    } else {
      stringBuilder.append(this.month.name()).append(' ').append(this.dom);
    } 
    stringBuilder.append(" at ").append(this.timeEndOfDay ? "24:00" : this.time.toString()).append(" ").append(this.timeDefinition).append(", standard offset ").append(this.standardOffset).append(']');
    return stringBuilder.toString();
  }
  
  public enum TimeDefinition {
    UTC, WALL, STANDARD;
    
    public LocalDateTime createDateTime(LocalDateTime param1LocalDateTime, ZoneOffset param1ZoneOffset1, ZoneOffset param1ZoneOffset2) {
      int i;
      switch (ZoneOffsetTransitionRule.null.$SwitchMap$java$time$zone$ZoneOffsetTransitionRule$TimeDefinition[ordinal()]) {
        case 1:
          i = param1ZoneOffset2.getTotalSeconds() - ZoneOffset.UTC.getTotalSeconds();
          return param1LocalDateTime.plusSeconds(i);
        case 2:
          i = param1ZoneOffset2.getTotalSeconds() - param1ZoneOffset1.getTotalSeconds();
          return param1LocalDateTime.plusSeconds(i);
      } 
      return param1LocalDateTime;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\zone\ZoneOffsetTransitionRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */