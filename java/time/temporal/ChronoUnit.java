package java.time.temporal;

import java.time.Duration;

public static enum ChronoUnit implements TemporalUnit {
  NANOS("Nanos", Duration.ofNanos(1L)),
  MICROS("Micros", Duration.ofNanos(1000L)),
  MILLIS("Millis", Duration.ofNanos(1000000L)),
  SECONDS("Seconds", Duration.ofSeconds(1L)),
  MINUTES("Minutes", Duration.ofSeconds(60L)),
  HOURS("Hours", Duration.ofSeconds(3600L)),
  HALF_DAYS("HalfDays", Duration.ofSeconds(43200L)),
  DAYS("Days", Duration.ofSeconds(86400L)),
  WEEKS("Weeks", Duration.ofSeconds(604800L)),
  MONTHS("Months", Duration.ofSeconds(2629746L)),
  YEARS("Years", Duration.ofSeconds(31556952L)),
  DECADES("Decades", Duration.ofSeconds(315569520L)),
  CENTURIES("Centuries", Duration.ofSeconds(3155695200L)),
  MILLENNIA("Millennia", Duration.ofSeconds(31556952000L)),
  ERAS("Eras", Duration.ofSeconds(31556952000000000L)),
  FOREVER("Forever", Duration.ofSeconds(Float.MAX_VALUE, 999999999L));
  
  private final String name;
  
  private final Duration duration;
  
  ChronoUnit(Duration paramDuration1, Duration paramDuration2) {
    this.name = paramDuration1;
    this.duration = paramDuration2;
  }
  
  public Duration getDuration() { return this.duration; }
  
  public boolean isDurationEstimated() { return (compareTo(DAYS) >= 0); }
  
  public boolean isDateBased() { return (compareTo(DAYS) >= 0 && this != FOREVER); }
  
  public boolean isTimeBased() { return (compareTo(DAYS) < 0); }
  
  public boolean isSupportedBy(Temporal paramTemporal) { return paramTemporal.isSupported(this); }
  
  public <R extends Temporal> R addTo(R paramR, long paramLong) { return (R)paramR.plus(paramLong, this); }
  
  public long between(Temporal paramTemporal1, Temporal paramTemporal2) { return paramTemporal1.until(paramTemporal2, this); }
  
  public String toString() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\ChronoUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */