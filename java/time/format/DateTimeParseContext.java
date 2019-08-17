package java.time.format;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class DateTimeParseContext {
  private DateTimeFormatter formatter;
  
  private boolean caseSensitive = true;
  
  private boolean strict = true;
  
  private final ArrayList<Parsed> parsed = new ArrayList();
  
  private ArrayList<Consumer<Chronology>> chronoListeners = null;
  
  DateTimeParseContext(DateTimeFormatter paramDateTimeFormatter) {
    this.formatter = paramDateTimeFormatter;
    this.parsed.add(new Parsed());
  }
  
  DateTimeParseContext copy() {
    DateTimeParseContext dateTimeParseContext = new DateTimeParseContext(this.formatter);
    dateTimeParseContext.caseSensitive = this.caseSensitive;
    dateTimeParseContext.strict = this.strict;
    return dateTimeParseContext;
  }
  
  Locale getLocale() { return this.formatter.getLocale(); }
  
  DecimalStyle getDecimalStyle() { return this.formatter.getDecimalStyle(); }
  
  Chronology getEffectiveChronology() {
    Chronology chronology = (currentParsed()).chrono;
    if (chronology == null) {
      chronology = this.formatter.getChronology();
      if (chronology == null)
        chronology = IsoChronology.INSTANCE; 
    } 
    return chronology;
  }
  
  boolean isCaseSensitive() { return this.caseSensitive; }
  
  void setCaseSensitive(boolean paramBoolean) { this.caseSensitive = paramBoolean; }
  
  boolean subSequenceEquals(CharSequence paramCharSequence1, int paramInt1, CharSequence paramCharSequence2, int paramInt2, int paramInt3) {
    if (paramInt1 + paramInt3 > paramCharSequence1.length() || paramInt2 + paramInt3 > paramCharSequence2.length())
      return false; 
    if (isCaseSensitive()) {
      for (int i = 0; i < paramInt3; i++) {
        char c1 = paramCharSequence1.charAt(paramInt1 + i);
        char c2 = paramCharSequence2.charAt(paramInt2 + i);
        if (c1 != c2)
          return false; 
      } 
    } else {
      for (int i = 0; i < paramInt3; i++) {
        char c1 = paramCharSequence1.charAt(paramInt1 + i);
        char c2 = paramCharSequence2.charAt(paramInt2 + i);
        if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2))
          return false; 
      } 
    } 
    return true;
  }
  
  boolean charEquals(char paramChar1, char paramChar2) { return isCaseSensitive() ? ((paramChar1 == paramChar2)) : charEqualsIgnoreCase(paramChar1, paramChar2); }
  
  static boolean charEqualsIgnoreCase(char paramChar1, char paramChar2) { return (paramChar1 == paramChar2 || Character.toUpperCase(paramChar1) == Character.toUpperCase(paramChar2) || Character.toLowerCase(paramChar1) == Character.toLowerCase(paramChar2)); }
  
  boolean isStrict() { return this.strict; }
  
  void setStrict(boolean paramBoolean) { this.strict = paramBoolean; }
  
  void startOptional() { this.parsed.add(currentParsed().copy()); }
  
  void endOptional(boolean paramBoolean) {
    if (paramBoolean) {
      this.parsed.remove(this.parsed.size() - 2);
    } else {
      this.parsed.remove(this.parsed.size() - 1);
    } 
  }
  
  private Parsed currentParsed() { return (Parsed)this.parsed.get(this.parsed.size() - 1); }
  
  Parsed toUnresolved() { return currentParsed(); }
  
  TemporalAccessor toResolved(ResolverStyle paramResolverStyle, Set<TemporalField> paramSet) {
    Parsed parsed1 = currentParsed();
    parsed1.chrono = getEffectiveChronology();
    parsed1.zone = (parsed1.zone != null) ? parsed1.zone : this.formatter.getZone();
    return parsed1.resolve(paramResolverStyle, paramSet);
  }
  
  Long getParsed(TemporalField paramTemporalField) { return (Long)(currentParsed()).fieldValues.get(paramTemporalField); }
  
  int setParsedField(TemporalField paramTemporalField, long paramLong, int paramInt1, int paramInt2) {
    Objects.requireNonNull(paramTemporalField, "field");
    Long long = (Long)(currentParsed()).fieldValues.put(paramTemporalField, Long.valueOf(paramLong));
    return (long != null && long.longValue() != paramLong) ? (paramInt1 ^ 0xFFFFFFFF) : paramInt2;
  }
  
  void setParsed(Chronology paramChronology) {
    Objects.requireNonNull(paramChronology, "chrono");
    (currentParsed()).chrono = paramChronology;
    if (this.chronoListeners != null && !this.chronoListeners.isEmpty()) {
      Consumer[] arrayOfConsumer1 = new Consumer[1];
      Consumer[] arrayOfConsumer2 = (Consumer[])this.chronoListeners.toArray(arrayOfConsumer1);
      this.chronoListeners.clear();
      for (Consumer consumer : arrayOfConsumer2)
        consumer.accept(paramChronology); 
    } 
  }
  
  void addChronoChangedListener(Consumer<Chronology> paramConsumer) {
    if (this.chronoListeners == null)
      this.chronoListeners = new ArrayList(); 
    this.chronoListeners.add(paramConsumer);
  }
  
  void setParsed(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    (currentParsed()).zone = paramZoneId;
  }
  
  void setParsedLeapSecond() { (currentParsed()).leapSecond = true; }
  
  public String toString() { return currentParsed().toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\DateTimeParseContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */