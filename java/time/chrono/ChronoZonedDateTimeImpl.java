package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

final class ChronoZonedDateTimeImpl<D extends ChronoLocalDate> extends Object implements ChronoZonedDateTime<D>, Serializable {
  private static final long serialVersionUID = -5261813987200935591L;
  
  private final ChronoLocalDateTimeImpl<D> dateTime;
  
  private final ZoneOffset offset;
  
  private final ZoneId zone;
  
  static <R extends ChronoLocalDate> ChronoZonedDateTime<R> ofBest(ChronoLocalDateTimeImpl<R> paramChronoLocalDateTimeImpl, ZoneId paramZoneId, ZoneOffset paramZoneOffset) {
    ZoneOffset zoneOffset;
    Objects.requireNonNull(paramChronoLocalDateTimeImpl, "localDateTime");
    Objects.requireNonNull(paramZoneId, "zone");
    if (paramZoneId instanceof ZoneOffset)
      return new ChronoZonedDateTimeImpl(paramChronoLocalDateTimeImpl, (ZoneOffset)paramZoneId, paramZoneId); 
    ZoneRules zoneRules = paramZoneId.getRules();
    LocalDateTime localDateTime = LocalDateTime.from(paramChronoLocalDateTimeImpl);
    List list = zoneRules.getValidOffsets(localDateTime);
    if (list.size() == 1) {
      zoneOffset = (ZoneOffset)list.get(0);
    } else if (list.size() == 0) {
      ZoneOffsetTransition zoneOffsetTransition = zoneRules.getTransition(localDateTime);
      paramChronoLocalDateTimeImpl = paramChronoLocalDateTimeImpl.plusSeconds(zoneOffsetTransition.getDuration().getSeconds());
      zoneOffset = zoneOffsetTransition.getOffsetAfter();
    } else if (paramZoneOffset != null && list.contains(paramZoneOffset)) {
      zoneOffset = paramZoneOffset;
    } else {
      zoneOffset = (ZoneOffset)list.get(0);
    } 
    Objects.requireNonNull(zoneOffset, "offset");
    return new ChronoZonedDateTimeImpl(paramChronoLocalDateTimeImpl, zoneOffset, paramZoneId);
  }
  
  static ChronoZonedDateTimeImpl<?> ofInstant(Chronology paramChronology, Instant paramInstant, ZoneId paramZoneId) {
    ZoneRules zoneRules = paramZoneId.getRules();
    ZoneOffset zoneOffset = zoneRules.getOffset(paramInstant);
    Objects.requireNonNull(zoneOffset, "offset");
    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(paramInstant.getEpochSecond(), paramInstant.getNano(), zoneOffset);
    ChronoLocalDateTimeImpl chronoLocalDateTimeImpl = (ChronoLocalDateTimeImpl)paramChronology.localDateTime(localDateTime);
    return new ChronoZonedDateTimeImpl(chronoLocalDateTimeImpl, zoneOffset, paramZoneId);
  }
  
  private ChronoZonedDateTimeImpl<D> create(Instant paramInstant, ZoneId paramZoneId) { return ofInstant(getChronology(), paramInstant, paramZoneId); }
  
  static <R extends ChronoLocalDate> ChronoZonedDateTimeImpl<R> ensureValid(Chronology paramChronology, Temporal paramTemporal) {
    ChronoZonedDateTimeImpl chronoZonedDateTimeImpl = (ChronoZonedDateTimeImpl)paramTemporal;
    if (!paramChronology.equals(chronoZonedDateTimeImpl.getChronology()))
      throw new ClassCastException("Chronology mismatch, required: " + paramChronology.getId() + ", actual: " + chronoZonedDateTimeImpl.getChronology().getId()); 
    return chronoZonedDateTimeImpl;
  }
  
  private ChronoZonedDateTimeImpl(ChronoLocalDateTimeImpl<D> paramChronoLocalDateTimeImpl, ZoneOffset paramZoneOffset, ZoneId paramZoneId) {
    this.dateTime = (ChronoLocalDateTimeImpl)Objects.requireNonNull(paramChronoLocalDateTimeImpl, "dateTime");
    this.offset = (ZoneOffset)Objects.requireNonNull(paramZoneOffset, "offset");
    this.zone = (ZoneId)Objects.requireNonNull(paramZoneId, "zone");
  }
  
  public ZoneOffset getOffset() { return this.offset; }
  
  public ChronoZonedDateTime<D> withEarlierOffsetAtOverlap() {
    ZoneOffsetTransition zoneOffsetTransition = getZone().getRules().getTransition(LocalDateTime.from(this));
    if (zoneOffsetTransition != null && zoneOffsetTransition.isOverlap()) {
      ZoneOffset zoneOffset = zoneOffsetTransition.getOffsetBefore();
      if (!zoneOffset.equals(this.offset))
        return new ChronoZonedDateTimeImpl(this.dateTime, zoneOffset, this.zone); 
    } 
    return this;
  }
  
  public ChronoZonedDateTime<D> withLaterOffsetAtOverlap() {
    ZoneOffsetTransition zoneOffsetTransition = getZone().getRules().getTransition(LocalDateTime.from(this));
    if (zoneOffsetTransition != null) {
      ZoneOffset zoneOffset = zoneOffsetTransition.getOffsetAfter();
      if (!zoneOffset.equals(getOffset()))
        return new ChronoZonedDateTimeImpl(this.dateTime, zoneOffset, this.zone); 
    } 
    return this;
  }
  
  public ChronoLocalDateTime<D> toLocalDateTime() { return this.dateTime; }
  
  public ZoneId getZone() { return this.zone; }
  
  public ChronoZonedDateTime<D> withZoneSameLocal(ZoneId paramZoneId) { return ofBest(this.dateTime, paramZoneId, this.offset); }
  
  public ChronoZonedDateTime<D> withZoneSameInstant(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    return this.zone.equals(paramZoneId) ? this : create(this.dateTime.toInstant(this.offset), paramZoneId);
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField || (paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public ChronoZonedDateTime<D> with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ZoneOffset zoneOffset;
      ChronoField chronoField = (ChronoField)paramTemporalField;
      switch (chronoField) {
        case INSTANT_SECONDS:
          return plus(paramLong - toEpochSecond(), ChronoUnit.SECONDS);
        case OFFSET_SECONDS:
          zoneOffset = ZoneOffset.ofTotalSeconds(chronoField.checkValidIntValue(paramLong));
          return create(this.dateTime.toInstant(zoneOffset), this.zone);
      } 
      return ofBest(this.dateTime.with(paramTemporalField, paramLong), this.zone, this.offset);
    } 
    return ensureValid(getChronology(), paramTemporalField.adjustInto(this, paramLong));
  }
  
  public ChronoZonedDateTime<D> plus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? with(this.dateTime.plus(paramLong, paramTemporalUnit)) : ensureValid(getChronology(), paramTemporalUnit.addTo(this, paramLong)); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    Objects.requireNonNull(paramTemporal, "endExclusive");
    ChronoZonedDateTime chronoZonedDateTime = getChronology().zonedDateTime(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      chronoZonedDateTime = chronoZonedDateTime.withZoneSameInstant(this.offset);
      return this.dateTime.until(chronoZonedDateTime.toLocalDateTime(), paramTemporalUnit);
    } 
    Objects.requireNonNull(paramTemporalUnit, "unit");
    return paramTemporalUnit.between(this, chronoZonedDateTime);
  }
  
  private Object writeReplace() { return new Ser((byte)3, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    paramObjectOutput.writeObject(this.dateTime);
    paramObjectOutput.writeObject(this.offset);
    paramObjectOutput.writeObject(this.zone);
  }
  
  static ChronoZonedDateTime<?> readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    ChronoLocalDateTime chronoLocalDateTime = (ChronoLocalDateTime)paramObjectInput.readObject();
    ZoneOffset zoneOffset = (ZoneOffset)paramObjectInput.readObject();
    ZoneId zoneId = (ZoneId)paramObjectInput.readObject();
    return chronoLocalDateTime.atZone(zoneOffset).withZoneSameLocal(zoneId);
  }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof ChronoZonedDateTime) ? ((compareTo((ChronoZonedDateTime)paramObject) == 0)) : false); }
  
  public int hashCode() { return toLocalDateTime().hashCode() ^ getOffset().hashCode() ^ Integer.rotateLeft(getZone().hashCode(), 3); }
  
  public String toString() {
    String str = toLocalDateTime().toString() + getOffset().toString();
    if (getOffset() != getZone())
      str = str + '[' + getZone().toString() + ']'; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoZonedDateTimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */