package java.time.chrono;

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

public interface Era extends TemporalAccessor, TemporalAdjuster {
  int getValue();
  
  default boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.ERA)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  default ValueRange range(TemporalField paramTemporalField) { return super.range(paramTemporalField); }
  
  default int get(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.ERA) ? getValue() : super.get(paramTemporalField); }
  
  default long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField == ChronoField.ERA)
      return getValue(); 
    if (paramTemporalField instanceof ChronoField)
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField); 
    return paramTemporalField.getFrom(this);
  }
  
  default <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.ERAS : (R)super.query(paramTemporalQuery); }
  
  default Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.ERA, getValue()); }
  
  default String getDisplayName(TextStyle paramTextStyle, Locale paramLocale) { return (new DateTimeFormatterBuilder()).appendText(ChronoField.ERA, paramTextStyle).toFormatter(paramLocale).format(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\Era.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */