package java.time.temporal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;

public final class TemporalQueries {
  static final TemporalQuery<ZoneId> ZONE_ID = paramTemporalAccessor -> (ZoneId)paramTemporalAccessor.query(ZONE_ID);
  
  static final TemporalQuery<Chronology> CHRONO = paramTemporalAccessor -> (Chronology)paramTemporalAccessor.query(CHRONO);
  
  static final TemporalQuery<TemporalUnit> PRECISION = paramTemporalAccessor -> (TemporalUnit)paramTemporalAccessor.query(PRECISION);
  
  static final TemporalQuery<ZoneOffset> OFFSET = paramTemporalAccessor -> paramTemporalAccessor.isSupported(ChronoField.OFFSET_SECONDS) ? ZoneOffset.ofTotalSeconds(paramTemporalAccessor.get(ChronoField.OFFSET_SECONDS)) : null;
  
  static final TemporalQuery<ZoneId> ZONE = paramTemporalAccessor -> {
      ZoneId zoneId = (ZoneId)paramTemporalAccessor.query(ZONE_ID);
      return (zoneId != null) ? zoneId : (ZoneId)paramTemporalAccessor.query(OFFSET);
    };
  
  static final TemporalQuery<LocalDate> LOCAL_DATE = paramTemporalAccessor -> paramTemporalAccessor.isSupported(ChronoField.EPOCH_DAY) ? LocalDate.ofEpochDay(paramTemporalAccessor.getLong(ChronoField.EPOCH_DAY)) : null;
  
  static final TemporalQuery<LocalTime> LOCAL_TIME = paramTemporalAccessor -> paramTemporalAccessor.isSupported(ChronoField.NANO_OF_DAY) ? LocalTime.ofNanoOfDay(paramTemporalAccessor.getLong(ChronoField.NANO_OF_DAY)) : null;
  
  public static TemporalQuery<ZoneId> zoneId() { return ZONE_ID; }
  
  public static TemporalQuery<Chronology> chronology() { return CHRONO; }
  
  public static TemporalQuery<TemporalUnit> precision() { return PRECISION; }
  
  public static TemporalQuery<ZoneId> zone() { return ZONE; }
  
  public static TemporalQuery<ZoneOffset> offset() { return OFFSET; }
  
  public static TemporalQuery<LocalDate> localDate() { return LOCAL_DATE; }
  
  public static TemporalQuery<LocalTime> localTime() { return LOCAL_TIME; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\TemporalQueries.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */