package java.time;

import java.io.Serializable;
import java.util.Objects;

public abstract class Clock {
  public static Clock systemUTC() { return new SystemClock(ZoneOffset.UTC); }
  
  public static Clock systemDefaultZone() { return new SystemClock(ZoneId.systemDefault()); }
  
  public static Clock system(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    return new SystemClock(paramZoneId);
  }
  
  public static Clock tickSeconds(ZoneId paramZoneId) { return new TickClock(system(paramZoneId), 1000000000L); }
  
  public static Clock tickMinutes(ZoneId paramZoneId) { return new TickClock(system(paramZoneId), 60000000000L); }
  
  public static Clock tick(Clock paramClock, Duration paramDuration) {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "tickDuration");
    if (paramDuration.isNegative())
      throw new IllegalArgumentException("Tick duration must not be negative"); 
    long l = paramDuration.toNanos();
    if (l % 1000000L == 0L || 1000000000L % l == 0L)
      return (l <= 1L) ? paramClock : new TickClock(paramClock, l); 
    throw new IllegalArgumentException("Invalid tick duration");
  }
  
  public static Clock fixed(Instant paramInstant, ZoneId paramZoneId) {
    Objects.requireNonNull(paramInstant, "fixedInstant");
    Objects.requireNonNull(paramZoneId, "zone");
    return new FixedClock(paramInstant, paramZoneId);
  }
  
  public static Clock offset(Clock paramClock, Duration paramDuration) {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "offsetDuration");
    return paramDuration.equals(Duration.ZERO) ? paramClock : new OffsetClock(paramClock, paramDuration);
  }
  
  public abstract ZoneId getZone();
  
  public abstract Clock withZone(ZoneId paramZoneId);
  
  public long millis() { return instant().toEpochMilli(); }
  
  public abstract Instant instant();
  
  public boolean equals(Object paramObject) { return super.equals(paramObject); }
  
  public int hashCode() { return super.hashCode(); }
  
  static final class FixedClock extends Clock implements Serializable {
    private static final long serialVersionUID = 7430389292664866958L;
    
    private final Instant instant;
    
    private final ZoneId zone;
    
    FixedClock(Instant param1Instant, ZoneId param1ZoneId) {
      this.instant = param1Instant;
      this.zone = param1ZoneId;
    }
    
    public ZoneId getZone() { return this.zone; }
    
    public Clock withZone(ZoneId param1ZoneId) { return param1ZoneId.equals(this.zone) ? this : new FixedClock(this.instant, param1ZoneId); }
    
    public long millis() { return this.instant.toEpochMilli(); }
    
    public Instant instant() { return this.instant; }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof FixedClock) {
        FixedClock fixedClock = (FixedClock)param1Object;
        return (this.instant.equals(fixedClock.instant) && this.zone.equals(fixedClock.zone));
      } 
      return false;
    }
    
    public int hashCode() { return this.instant.hashCode() ^ this.zone.hashCode(); }
    
    public String toString() { return "FixedClock[" + this.instant + "," + this.zone + "]"; }
  }
  
  static final class OffsetClock extends Clock implements Serializable {
    private static final long serialVersionUID = 2007484719125426256L;
    
    private final Clock baseClock;
    
    private final Duration offset;
    
    OffsetClock(Clock param1Clock, Duration param1Duration) {
      this.baseClock = param1Clock;
      this.offset = param1Duration;
    }
    
    public ZoneId getZone() { return this.baseClock.getZone(); }
    
    public Clock withZone(ZoneId param1ZoneId) { return param1ZoneId.equals(this.baseClock.getZone()) ? this : new OffsetClock(this.baseClock.withZone(param1ZoneId), this.offset); }
    
    public long millis() { return Math.addExact(this.baseClock.millis(), this.offset.toMillis()); }
    
    public Instant instant() { return this.baseClock.instant().plus(this.offset); }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof OffsetClock) {
        OffsetClock offsetClock = (OffsetClock)param1Object;
        return (this.baseClock.equals(offsetClock.baseClock) && this.offset.equals(offsetClock.offset));
      } 
      return false;
    }
    
    public int hashCode() { return this.baseClock.hashCode() ^ this.offset.hashCode(); }
    
    public String toString() { return "OffsetClock[" + this.baseClock + "," + this.offset + "]"; }
  }
  
  static final class SystemClock extends Clock implements Serializable {
    private static final long serialVersionUID = 6740630888130243051L;
    
    private final ZoneId zone;
    
    SystemClock(ZoneId param1ZoneId) { this.zone = param1ZoneId; }
    
    public ZoneId getZone() { return this.zone; }
    
    public Clock withZone(ZoneId param1ZoneId) { return param1ZoneId.equals(this.zone) ? this : new SystemClock(param1ZoneId); }
    
    public long millis() { return System.currentTimeMillis(); }
    
    public Instant instant() { return Instant.ofEpochMilli(millis()); }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof SystemClock) ? this.zone.equals(((SystemClock)param1Object).zone) : 0; }
    
    public int hashCode() { return this.zone.hashCode() + 1; }
    
    public String toString() { return "SystemClock[" + this.zone + "]"; }
  }
  
  static final class TickClock extends Clock implements Serializable {
    private static final long serialVersionUID = 6504659149906368850L;
    
    private final Clock baseClock;
    
    private final long tickNanos;
    
    TickClock(Clock param1Clock, long param1Long) {
      this.baseClock = param1Clock;
      this.tickNanos = param1Long;
    }
    
    public ZoneId getZone() { return this.baseClock.getZone(); }
    
    public Clock withZone(ZoneId param1ZoneId) { return param1ZoneId.equals(this.baseClock.getZone()) ? this : new TickClock(this.baseClock.withZone(param1ZoneId), this.tickNanos); }
    
    public long millis() {
      long l = this.baseClock.millis();
      return l - Math.floorMod(l, this.tickNanos / 1000000L);
    }
    
    public Instant instant() {
      if (this.tickNanos % 1000000L == 0L) {
        long l = this.baseClock.millis();
        return Instant.ofEpochMilli(l - Math.floorMod(l, this.tickNanos / 1000000L));
      } 
      Instant instant = this.baseClock.instant();
      long l1 = instant.getNano();
      long l2 = Math.floorMod(l1, this.tickNanos);
      return instant.minusNanos(l2);
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof TickClock) {
        TickClock tickClock = (TickClock)param1Object;
        return (this.baseClock.equals(tickClock.baseClock) && this.tickNanos == tickClock.tickNanos);
      } 
      return false;
    }
    
    public int hashCode() { return this.baseClock.hashCode() ^ (int)(this.tickNanos ^ this.tickNanos >>> 32); }
    
    public String toString() { return "TickClock[" + this.baseClock + "," + Duration.ofNanos(this.tickNanos) + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\Clock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */