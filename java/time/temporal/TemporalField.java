package java.time.temporal;

import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public interface TemporalField {
  default String getDisplayName(Locale paramLocale) {
    Objects.requireNonNull(paramLocale, "locale");
    return toString();
  }
  
  TemporalUnit getBaseUnit();
  
  TemporalUnit getRangeUnit();
  
  ValueRange range();
  
  boolean isDateBased();
  
  boolean isTimeBased();
  
  boolean isSupportedBy(TemporalAccessor paramTemporalAccessor);
  
  ValueRange rangeRefinedBy(TemporalAccessor paramTemporalAccessor);
  
  long getFrom(TemporalAccessor paramTemporalAccessor);
  
  <R extends Temporal> R adjustInto(R paramR, long paramLong);
  
  default TemporalAccessor resolve(Map<TemporalField, Long> paramMap, TemporalAccessor paramTemporalAccessor, ResolverStyle paramResolverStyle) { return null; }
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\TemporalField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */