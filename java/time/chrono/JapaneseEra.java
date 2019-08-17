package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.Era;

public final class JapaneseEra implements Era, Serializable {
  static final int ERA_OFFSET = 2;
  
  static final Era[] ERA_CONFIG;
  
  public static final JapaneseEra MEIJI = new JapaneseEra(-1, LocalDate.of(1868, 1, 1));
  
  public static final JapaneseEra TAISHO = new JapaneseEra(0, LocalDate.of(1912, 7, 30));
  
  public static final JapaneseEra SHOWA = new JapaneseEra(1, LocalDate.of(1926, 12, 25));
  
  public static final JapaneseEra HEISEI = new JapaneseEra(2, LocalDate.of(1989, 1, 8));
  
  private static final int N_ERA_CONSTANTS = HEISEI.getValue() + 2;
  
  private static final long serialVersionUID = 1466499369062886794L;
  
  private static final JapaneseEra[] KNOWN_ERAS;
  
  private final int eraValue;
  
  private final LocalDate since;
  
  private JapaneseEra(int paramInt, LocalDate paramLocalDate) {
    this.eraValue = paramInt;
    this.since = paramLocalDate;
  }
  
  Era getPrivateEra() { return ERA_CONFIG[ordinal(this.eraValue)]; }
  
  public static JapaneseEra of(int paramInt) {
    if (paramInt < MEIJI.eraValue || paramInt + 2 > KNOWN_ERAS.length)
      throw new DateTimeException("Invalid era: " + paramInt); 
    return KNOWN_ERAS[ordinal(paramInt)];
  }
  
  public static JapaneseEra valueOf(String paramString) {
    Objects.requireNonNull(paramString, "japaneseEra");
    for (JapaneseEra japaneseEra : KNOWN_ERAS) {
      if (japaneseEra.getName().equals(paramString))
        return japaneseEra; 
    } 
    throw new IllegalArgumentException("japaneseEra is invalid");
  }
  
  public static JapaneseEra[] values() { return (JapaneseEra[])Arrays.copyOf(KNOWN_ERAS, KNOWN_ERAS.length); }
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale) {
    if (getValue() > N_ERA_CONSTANTS - 2) {
      Objects.requireNonNull(paramLocale, "locale");
      return (paramTextStyle.asNormal() == TextStyle.NARROW) ? getAbbreviation() : getName();
    } 
    return super.getDisplayName(paramTextStyle, paramLocale);
  }
  
  static JapaneseEra from(LocalDate paramLocalDate) {
    if (paramLocalDate.isBefore(JapaneseDate.MEIJI_6_ISODATE))
      throw new DateTimeException("JapaneseDate before Meiji 6 are not supported"); 
    for (int i = KNOWN_ERAS.length - 1; i > 0; i--) {
      JapaneseEra japaneseEra = KNOWN_ERAS[i];
      if (paramLocalDate.compareTo(japaneseEra.since) >= 0)
        return japaneseEra; 
    } 
    return null;
  }
  
  static JapaneseEra toJapaneseEra(Era paramEra) {
    for (int i = ERA_CONFIG.length - 1; i >= 0; i--) {
      if (ERA_CONFIG[i].equals(paramEra))
        return KNOWN_ERAS[i]; 
    } 
    return null;
  }
  
  static Era privateEraFrom(LocalDate paramLocalDate) {
    for (int i = KNOWN_ERAS.length - 1; i > 0; i--) {
      JapaneseEra japaneseEra = KNOWN_ERAS[i];
      if (paramLocalDate.compareTo(japaneseEra.since) >= 0)
        return ERA_CONFIG[i]; 
    } 
    return null;
  }
  
  private static int ordinal(int paramInt) { return paramInt + 2 - 1; }
  
  public int getValue() { return this.eraValue; }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.ERA) ? JapaneseChronology.INSTANCE.range(ChronoField.ERA) : super.range(paramTemporalField); }
  
  String getAbbreviation() { return ERA_CONFIG[ordinal(getValue())].getAbbreviation(); }
  
  String getName() { return ERA_CONFIG[ordinal(getValue())].getName(); }
  
  public String toString() { return getName(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)5, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException { paramDataOutput.writeByte(getValue()); }
  
  static JapaneseEra readExternal(DataInput paramDataInput) throws IOException {
    byte b = paramDataInput.readByte();
    return of(b);
  }
  
  static  {
    ERA_CONFIG = JapaneseChronology.JCAL.getEras();
    KNOWN_ERAS = new JapaneseEra[ERA_CONFIG.length];
    KNOWN_ERAS[0] = MEIJI;
    KNOWN_ERAS[1] = TAISHO;
    KNOWN_ERAS[2] = SHOWA;
    KNOWN_ERAS[3] = HEISEI;
    for (int i = N_ERA_CONSTANTS; i < ERA_CONFIG.length; i++) {
      CalendarDate calendarDate = ERA_CONFIG[i].getSinceDate();
      LocalDate localDate = LocalDate.of(calendarDate.getYear(), calendarDate.getMonth(), calendarDate.getDayOfMonth());
      KNOWN_ERAS[i] = new JapaneseEra(i - 2 + 1, localDate);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\JapaneseEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */