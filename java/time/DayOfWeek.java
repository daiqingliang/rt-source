package java.time;

import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Locale;

public static enum DayOfWeek implements TemporalAccessor, TemporalAdjuster {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
  
  private static final DayOfWeek[] ENUMS;
  
  public static DayOfWeek of(int paramInt) {
    if (paramInt < 1 || paramInt > 7)
      throw new DateTimeException("Invalid value for DayOfWeek: " + paramInt); 
    return ENUMS[paramInt - 1];
  }
  
  public static DayOfWeek from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof DayOfWeek)
      return (DayOfWeek)paramTemporalAccessor; 
    try {
      return of(paramTemporalAccessor.get(ChronoField.DAY_OF_WEEK));
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain DayOfWeek from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public int getValue() { return ordinal() + 1; }
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale) { return (new DateTimeFormatterBuilder()).appendText(ChronoField.DAY_OF_WEEK, paramTextStyle).toFormatter(paramLocale).format(this); }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.DAY_OF_WEEK)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.DAY_OF_WEEK) ? paramTemporalField.range() : super.range(paramTemporalField); }
  
  public int get(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.DAY_OF_WEEK) ? getValue() : super.get(paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField == ChronoField.DAY_OF_WEEK)
      return getValue(); 
    if (paramTemporalField instanceof ChronoField)
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField); 
    return paramTemporalField.getFrom(this);
  }
  
  public DayOfWeek plus(long paramLong) {
    int i = (int)(paramLong % 7L);
    return ENUMS[(ordinal() + i + 7) % 7];
  }
  
  public DayOfWeek minus(long paramLong) { return plus(-(paramLong % 7L)); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.DAYS : (R)super.query(paramTemporalQuery); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.DAY_OF_WEEK, getValue()); }
  
  static  {
    ENUMS = values();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\DayOfWeek.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */