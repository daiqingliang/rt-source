package java.time.temporal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class TemporalAdjusters {
  public static TemporalAdjuster ofDateAdjuster(UnaryOperator<LocalDate> paramUnaryOperator) {
    Objects.requireNonNull(paramUnaryOperator, "dateBasedAdjuster");
    return paramTemporal -> {
        LocalDate localDate1 = LocalDate.from(paramTemporal);
        LocalDate localDate2 = (LocalDate)paramUnaryOperator.apply(localDate1);
        return paramTemporal.with(localDate2);
      };
  }
  
  public static TemporalAdjuster firstDayOfMonth() { return paramTemporal -> paramTemporal.with(ChronoField.DAY_OF_MONTH, 1L); }
  
  public static TemporalAdjuster lastDayOfMonth() { return paramTemporal -> paramTemporal.with(ChronoField.DAY_OF_MONTH, paramTemporal.range(ChronoField.DAY_OF_MONTH).getMaximum()); }
  
  public static TemporalAdjuster firstDayOfNextMonth() { return paramTemporal -> paramTemporal.with(ChronoField.DAY_OF_MONTH, 1L).plus(1L, ChronoUnit.MONTHS); }
  
  public static TemporalAdjuster firstDayOfYear() { return paramTemporal -> paramTemporal.with(ChronoField.DAY_OF_YEAR, 1L); }
  
  public static TemporalAdjuster lastDayOfYear() { return paramTemporal -> paramTemporal.with(ChronoField.DAY_OF_YEAR, paramTemporal.range(ChronoField.DAY_OF_YEAR).getMaximum()); }
  
  public static TemporalAdjuster firstDayOfNextYear() { return paramTemporal -> paramTemporal.with(ChronoField.DAY_OF_YEAR, 1L).plus(1L, ChronoUnit.YEARS); }
  
  public static TemporalAdjuster firstInMonth(DayOfWeek paramDayOfWeek) { return dayOfWeekInMonth(1, paramDayOfWeek); }
  
  public static TemporalAdjuster lastInMonth(DayOfWeek paramDayOfWeek) { return dayOfWeekInMonth(-1, paramDayOfWeek); }
  
  public static TemporalAdjuster dayOfWeekInMonth(int paramInt, DayOfWeek paramDayOfWeek) {
    Objects.requireNonNull(paramDayOfWeek, "dayOfWeek");
    int i = paramDayOfWeek.getValue();
    return (paramInt >= 0) ? (paramTemporal -> {
        Temporal temporal = paramTemporal.with(ChronoField.DAY_OF_MONTH, 1L);
        int i = temporal.get(ChronoField.DAY_OF_WEEK);
        int j = (paramInt1 - i + 7) % 7;
        j = (int)(j + (paramInt2 - 1L) * 7L);
        return temporal.plus(j, ChronoUnit.DAYS);
      }) : (paramTemporal -> {
        Temporal temporal = paramTemporal.with(ChronoField.DAY_OF_MONTH, paramTemporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
        int i = temporal.get(ChronoField.DAY_OF_WEEK);
        int j = paramInt1 - i;
        j = (j == 0) ? 0 : ((j > 0) ? (j - 7) : j);
        j = (int)(j - (-paramInt2 - 1L) * 7L);
        return temporal.plus(j, ChronoUnit.DAYS);
      });
  }
  
  public static TemporalAdjuster next(DayOfWeek paramDayOfWeek) {
    int i = paramDayOfWeek.getValue();
    return paramTemporal -> {
        int i = paramTemporal.get(ChronoField.DAY_OF_WEEK);
        int j = i - paramInt;
        return paramTemporal.plus((j >= 0) ? (7 - j) : -j, ChronoUnit.DAYS);
      };
  }
  
  public static TemporalAdjuster nextOrSame(DayOfWeek paramDayOfWeek) {
    int i = paramDayOfWeek.getValue();
    return paramTemporal -> {
        int i = paramTemporal.get(ChronoField.DAY_OF_WEEK);
        if (i == paramInt)
          return paramTemporal; 
        int j = i - paramInt;
        return paramTemporal.plus((j >= 0) ? (7 - j) : -j, ChronoUnit.DAYS);
      };
  }
  
  public static TemporalAdjuster previous(DayOfWeek paramDayOfWeek) {
    int i = paramDayOfWeek.getValue();
    return paramTemporal -> {
        int i = paramTemporal.get(ChronoField.DAY_OF_WEEK);
        int j = paramInt - i;
        return paramTemporal.minus((j >= 0) ? (7 - j) : -j, ChronoUnit.DAYS);
      };
  }
  
  public static TemporalAdjuster previousOrSame(DayOfWeek paramDayOfWeek) {
    int i = paramDayOfWeek.getValue();
    return paramTemporal -> {
        int i = paramTemporal.get(ChronoField.DAY_OF_WEEK);
        if (i == paramInt)
          return paramTemporal; 
        int j = paramInt - i;
        return paramTemporal.minus((j >= 0) ? (7 - j) : -j, ChronoUnit.DAYS);
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\TemporalAdjusters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */