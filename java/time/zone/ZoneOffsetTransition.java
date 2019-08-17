package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ZoneOffsetTransition extends Object implements Comparable<ZoneOffsetTransition>, Serializable {
  private static final long serialVersionUID = -6946044323557704546L;
  
  private final LocalDateTime transition;
  
  private final ZoneOffset offsetBefore;
  
  private final ZoneOffset offsetAfter;
  
  public static ZoneOffsetTransition of(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2) {
    Objects.requireNonNull(paramLocalDateTime, "transition");
    Objects.requireNonNull(paramZoneOffset1, "offsetBefore");
    Objects.requireNonNull(paramZoneOffset2, "offsetAfter");
    if (paramZoneOffset1.equals(paramZoneOffset2))
      throw new IllegalArgumentException("Offsets must not be equal"); 
    if (paramLocalDateTime.getNano() != 0)
      throw new IllegalArgumentException("Nano-of-second must be zero"); 
    return new ZoneOffsetTransition(paramLocalDateTime, paramZoneOffset1, paramZoneOffset2);
  }
  
  ZoneOffsetTransition(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2) {
    this.transition = paramLocalDateTime;
    this.offsetBefore = paramZoneOffset1;
    this.offsetAfter = paramZoneOffset2;
  }
  
  ZoneOffsetTransition(long paramLong, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2) {
    this.transition = LocalDateTime.ofEpochSecond(paramLong, 0, paramZoneOffset1);
    this.offsetBefore = paramZoneOffset1;
    this.offsetAfter = paramZoneOffset2;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)2, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    Ser.writeEpochSec(toEpochSecond(), paramDataOutput);
    Ser.writeOffset(this.offsetBefore, paramDataOutput);
    Ser.writeOffset(this.offsetAfter, paramDataOutput);
  }
  
  static ZoneOffsetTransition readExternal(DataInput paramDataInput) throws IOException {
    long l = Ser.readEpochSec(paramDataInput);
    ZoneOffset zoneOffset1 = Ser.readOffset(paramDataInput);
    ZoneOffset zoneOffset2 = Ser.readOffset(paramDataInput);
    if (zoneOffset1.equals(zoneOffset2))
      throw new IllegalArgumentException("Offsets must not be equal"); 
    return new ZoneOffsetTransition(l, zoneOffset1, zoneOffset2);
  }
  
  public Instant getInstant() { return this.transition.toInstant(this.offsetBefore); }
  
  public long toEpochSecond() { return this.transition.toEpochSecond(this.offsetBefore); }
  
  public LocalDateTime getDateTimeBefore() { return this.transition; }
  
  public LocalDateTime getDateTimeAfter() { return this.transition.plusSeconds(getDurationSeconds()); }
  
  public ZoneOffset getOffsetBefore() { return this.offsetBefore; }
  
  public ZoneOffset getOffsetAfter() { return this.offsetAfter; }
  
  public Duration getDuration() { return Duration.ofSeconds(getDurationSeconds()); }
  
  private int getDurationSeconds() { return getOffsetAfter().getTotalSeconds() - getOffsetBefore().getTotalSeconds(); }
  
  public boolean isGap() { return (getOffsetAfter().getTotalSeconds() > getOffsetBefore().getTotalSeconds()); }
  
  public boolean isOverlap() { return (getOffsetAfter().getTotalSeconds() < getOffsetBefore().getTotalSeconds()); }
  
  public boolean isValidOffset(ZoneOffset paramZoneOffset) { return isGap() ? false : ((getOffsetBefore().equals(paramZoneOffset) || getOffsetAfter().equals(paramZoneOffset))); }
  
  List<ZoneOffset> getValidOffsets() { return isGap() ? Collections.emptyList() : Arrays.asList(new ZoneOffset[] { getOffsetBefore(), getOffsetAfter() }); }
  
  public int compareTo(ZoneOffsetTransition paramZoneOffsetTransition) { return getInstant().compareTo(paramZoneOffsetTransition.getInstant()); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof ZoneOffsetTransition) {
      ZoneOffsetTransition zoneOffsetTransition = (ZoneOffsetTransition)paramObject;
      return (this.transition.equals(zoneOffsetTransition.transition) && this.offsetBefore.equals(zoneOffsetTransition.offsetBefore) && this.offsetAfter.equals(zoneOffsetTransition.offsetAfter));
    } 
    return false;
  }
  
  public int hashCode() { return this.transition.hashCode() ^ this.offsetBefore.hashCode() ^ Integer.rotateLeft(this.offsetAfter.hashCode(), 16); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Transition[").append(isGap() ? "Gap" : "Overlap").append(" at ").append(this.transition).append(this.offsetBefore).append(" to ").append(this.offsetAfter).append(']');
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\zone\ZoneOffsetTransition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */