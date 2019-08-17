package java.time.format;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.Period;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class DateTimeFormatter {
  private final DateTimeFormatterBuilder.CompositePrinterParser printerParser;
  
  private final Locale locale;
  
  private final DecimalStyle decimalStyle;
  
  private final ResolverStyle resolverStyle;
  
  private final Set<TemporalField> resolverFields;
  
  private final Chronology chrono;
  
  private final ZoneId zone;
  
  public static final DateTimeFormatter ISO_LOCAL_DATE = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_OFFSET_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_LOCAL_TIME = (new DateTimeFormatterBuilder()).appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter(ResolverStyle.STRICT, null);
  
  public static final DateTimeFormatter ISO_OFFSET_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_TIME).appendOffsetId().toFormatter(ResolverStyle.STRICT, null);
  
  public static final DateTimeFormatter ISO_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_TIME).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, null);
  
  public static final DateTimeFormatter ISO_LOCAL_DATE_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE).appendLiteral('T').append(ISO_LOCAL_TIME).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE_TIME).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_ZONED_DATE_TIME = (new DateTimeFormatterBuilder()).append(ISO_OFFSET_DATE_TIME).optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_DATE_TIME = (new DateTimeFormatterBuilder()).append(ISO_LOCAL_DATE_TIME).optionalStart().appendOffsetId().optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_ORDINAL_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.DAY_OF_YEAR, 3).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_WEEK_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendValue(IsoFields.WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral("-W").appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_WEEK, 1).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter ISO_INSTANT = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendInstant().toFormatter(ResolverStyle.STRICT, null);
  
  public static final DateTimeFormatter BASIC_ISO_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendValue(ChronoField.YEAR, 4).appendValue(ChronoField.MONTH_OF_YEAR, 2).appendValue(ChronoField.DAY_OF_MONTH, 2).optionalStart().appendOffset("+HHMMss", "Z").toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
  
  public static final DateTimeFormatter RFC_1123_DATE_TIME;
  
  private static final TemporalQuery<Period> PARSED_EXCESS_DAYS;
  
  private static final TemporalQuery<Boolean> PARSED_LEAP_SECOND;
  
  public static DateTimeFormatter ofPattern(String paramString) { return (new DateTimeFormatterBuilder()).appendPattern(paramString).toFormatter(); }
  
  public static DateTimeFormatter ofPattern(String paramString, Locale paramLocale) { return (new DateTimeFormatterBuilder()).appendPattern(paramString).toFormatter(paramLocale); }
  
  public static DateTimeFormatter ofLocalizedDate(FormatStyle paramFormatStyle) {
    Objects.requireNonNull(paramFormatStyle, "dateStyle");
    return (new DateTimeFormatterBuilder()).appendLocalized(paramFormatStyle, null).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
  }
  
  public static DateTimeFormatter ofLocalizedTime(FormatStyle paramFormatStyle) {
    Objects.requireNonNull(paramFormatStyle, "timeStyle");
    return (new DateTimeFormatterBuilder()).appendLocalized(null, paramFormatStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
  }
  
  public static DateTimeFormatter ofLocalizedDateTime(FormatStyle paramFormatStyle) {
    Objects.requireNonNull(paramFormatStyle, "dateTimeStyle");
    return (new DateTimeFormatterBuilder()).appendLocalized(paramFormatStyle, paramFormatStyle).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
  }
  
  public static DateTimeFormatter ofLocalizedDateTime(FormatStyle paramFormatStyle1, FormatStyle paramFormatStyle2) {
    Objects.requireNonNull(paramFormatStyle1, "dateStyle");
    Objects.requireNonNull(paramFormatStyle2, "timeStyle");
    return (new DateTimeFormatterBuilder()).appendLocalized(paramFormatStyle1, paramFormatStyle2).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
  }
  
  public static final TemporalQuery<Period> parsedExcessDays() { return PARSED_EXCESS_DAYS; }
  
  public static final TemporalQuery<Boolean> parsedLeapSecond() { return PARSED_LEAP_SECOND; }
  
  DateTimeFormatter(DateTimeFormatterBuilder.CompositePrinterParser paramCompositePrinterParser, Locale paramLocale, DecimalStyle paramDecimalStyle, ResolverStyle paramResolverStyle, Set<TemporalField> paramSet, Chronology paramChronology, ZoneId paramZoneId) {
    this.printerParser = (DateTimeFormatterBuilder.CompositePrinterParser)Objects.requireNonNull(paramCompositePrinterParser, "printerParser");
    this.resolverFields = paramSet;
    this.locale = (Locale)Objects.requireNonNull(paramLocale, "locale");
    this.decimalStyle = (DecimalStyle)Objects.requireNonNull(paramDecimalStyle, "decimalStyle");
    this.resolverStyle = (ResolverStyle)Objects.requireNonNull(paramResolverStyle, "resolverStyle");
    this.chrono = paramChronology;
    this.zone = paramZoneId;
  }
  
  public Locale getLocale() { return this.locale; }
  
  public DateTimeFormatter withLocale(Locale paramLocale) { return this.locale.equals(paramLocale) ? this : new DateTimeFormatter(this.printerParser, paramLocale, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, this.zone); }
  
  public DecimalStyle getDecimalStyle() { return this.decimalStyle; }
  
  public DateTimeFormatter withDecimalStyle(DecimalStyle paramDecimalStyle) { return this.decimalStyle.equals(paramDecimalStyle) ? this : new DateTimeFormatter(this.printerParser, this.locale, paramDecimalStyle, this.resolverStyle, this.resolverFields, this.chrono, this.zone); }
  
  public Chronology getChronology() { return this.chrono; }
  
  public DateTimeFormatter withChronology(Chronology paramChronology) { return Objects.equals(this.chrono, paramChronology) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, paramChronology, this.zone); }
  
  public ZoneId getZone() { return this.zone; }
  
  public DateTimeFormatter withZone(ZoneId paramZoneId) { return Objects.equals(this.zone, paramZoneId) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, paramZoneId); }
  
  public ResolverStyle getResolverStyle() { return this.resolverStyle; }
  
  public DateTimeFormatter withResolverStyle(ResolverStyle paramResolverStyle) {
    Objects.requireNonNull(paramResolverStyle, "resolverStyle");
    return Objects.equals(this.resolverStyle, paramResolverStyle) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, paramResolverStyle, this.resolverFields, this.chrono, this.zone);
  }
  
  public Set<TemporalField> getResolverFields() { return this.resolverFields; }
  
  public DateTimeFormatter withResolverFields(TemporalField... paramVarArgs) {
    Set set = null;
    if (paramVarArgs != null)
      set = Collections.unmodifiableSet(new HashSet(Arrays.asList(paramVarArgs))); 
    return Objects.equals(this.resolverFields, set) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, set, this.chrono, this.zone);
  }
  
  public DateTimeFormatter withResolverFields(Set<TemporalField> paramSet) {
    if (Objects.equals(this.resolverFields, paramSet))
      return this; 
    if (paramSet != null)
      paramSet = Collections.unmodifiableSet(new HashSet(paramSet)); 
    return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, paramSet, this.chrono, this.zone);
  }
  
  public String format(TemporalAccessor paramTemporalAccessor) {
    StringBuilder stringBuilder = new StringBuilder(32);
    formatTo(paramTemporalAccessor, stringBuilder);
    return stringBuilder.toString();
  }
  
  public void formatTo(TemporalAccessor paramTemporalAccessor, Appendable paramAppendable) {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Objects.requireNonNull(paramAppendable, "appendable");
    try {
      DateTimePrintContext dateTimePrintContext = new DateTimePrintContext(paramTemporalAccessor, this);
      if (paramAppendable instanceof StringBuilder) {
        this.printerParser.format(dateTimePrintContext, (StringBuilder)paramAppendable);
      } else {
        StringBuilder stringBuilder = new StringBuilder(32);
        this.printerParser.format(dateTimePrintContext, stringBuilder);
        paramAppendable.append(stringBuilder);
      } 
    } catch (IOException iOException) {
      throw new DateTimeException(iOException.getMessage(), iOException);
    } 
  }
  
  public TemporalAccessor parse(CharSequence paramCharSequence) {
    Objects.requireNonNull(paramCharSequence, "text");
    try {
      return parseResolved0(paramCharSequence, null);
    } catch (DateTimeParseException dateTimeParseException) {
      throw dateTimeParseException;
    } catch (RuntimeException runtimeException) {
      throw createError(paramCharSequence, runtimeException);
    } 
  }
  
  public TemporalAccessor parse(CharSequence paramCharSequence, ParsePosition paramParsePosition) {
    Objects.requireNonNull(paramCharSequence, "text");
    Objects.requireNonNull(paramParsePosition, "position");
    try {
      return parseResolved0(paramCharSequence, paramParsePosition);
    } catch (DateTimeParseException|IndexOutOfBoundsException dateTimeParseException) {
      throw dateTimeParseException;
    } catch (RuntimeException runtimeException) {
      throw createError(paramCharSequence, runtimeException);
    } 
  }
  
  public <T> T parse(CharSequence paramCharSequence, TemporalQuery<T> paramTemporalQuery) {
    Objects.requireNonNull(paramCharSequence, "text");
    Objects.requireNonNull(paramTemporalQuery, "query");
    try {
      return (T)parseResolved0(paramCharSequence, null).query(paramTemporalQuery);
    } catch (DateTimeParseException dateTimeParseException) {
      throw dateTimeParseException;
    } catch (RuntimeException runtimeException) {
      throw createError(paramCharSequence, runtimeException);
    } 
  }
  
  public TemporalAccessor parseBest(CharSequence paramCharSequence, TemporalQuery<?>... paramVarArgs) {
    Objects.requireNonNull(paramCharSequence, "text");
    Objects.requireNonNull(paramVarArgs, "queries");
    if (paramVarArgs.length < 2)
      throw new IllegalArgumentException("At least two queries must be specified"); 
    try {
      TemporalAccessor temporalAccessor = parseResolved0(paramCharSequence, null);
      TemporalQuery<?>[] arrayOfTemporalQuery = paramVarArgs;
      int i = arrayOfTemporalQuery.length;
      byte b = 0;
      while (b < i) {
        TemporalQuery<?> temporalQuery = arrayOfTemporalQuery[b];
        try {
          return (TemporalAccessor)temporalAccessor.query(temporalQuery);
        } catch (RuntimeException runtimeException) {
          b++;
        } 
      } 
      throw new DateTimeException("Unable to convert parsed text using any of the specified queries");
    } catch (DateTimeParseException dateTimeParseException) {
      throw dateTimeParseException;
    } catch (RuntimeException runtimeException) {
      throw createError(paramCharSequence, runtimeException);
    } 
  }
  
  private DateTimeParseException createError(CharSequence paramCharSequence, RuntimeException paramRuntimeException) {
    String str;
    if (paramCharSequence.length() > 64) {
      str = paramCharSequence.subSequence(0, 64).toString() + "...";
    } else {
      str = paramCharSequence.toString();
    } 
    return new DateTimeParseException("Text '" + str + "' could not be parsed: " + paramRuntimeException.getMessage(), paramCharSequence, 0, paramRuntimeException);
  }
  
  private TemporalAccessor parseResolved0(CharSequence paramCharSequence, ParsePosition paramParsePosition) {
    ParsePosition parsePosition = (paramParsePosition != null) ? paramParsePosition : new ParsePosition(0);
    DateTimeParseContext dateTimeParseContext = parseUnresolved0(paramCharSequence, parsePosition);
    if (dateTimeParseContext == null || parsePosition.getErrorIndex() >= 0 || (paramParsePosition == null && parsePosition.getIndex() < paramCharSequence.length())) {
      String str;
      if (paramCharSequence.length() > 64) {
        str = paramCharSequence.subSequence(0, 64).toString() + "...";
      } else {
        str = paramCharSequence.toString();
      } 
      if (parsePosition.getErrorIndex() >= 0)
        throw new DateTimeParseException("Text '" + str + "' could not be parsed at index " + parsePosition.getErrorIndex(), paramCharSequence, parsePosition.getErrorIndex()); 
      throw new DateTimeParseException("Text '" + str + "' could not be parsed, unparsed text found at index " + parsePosition.getIndex(), paramCharSequence, parsePosition.getIndex());
    } 
    return dateTimeParseContext.toResolved(this.resolverStyle, this.resolverFields);
  }
  
  public TemporalAccessor parseUnresolved(CharSequence paramCharSequence, ParsePosition paramParsePosition) {
    DateTimeParseContext dateTimeParseContext = parseUnresolved0(paramCharSequence, paramParsePosition);
    return (dateTimeParseContext == null) ? null : dateTimeParseContext.toUnresolved();
  }
  
  private DateTimeParseContext parseUnresolved0(CharSequence paramCharSequence, ParsePosition paramParsePosition) {
    Objects.requireNonNull(paramCharSequence, "text");
    Objects.requireNonNull(paramParsePosition, "position");
    DateTimeParseContext dateTimeParseContext = new DateTimeParseContext(this);
    int i = paramParsePosition.getIndex();
    i = this.printerParser.parse(dateTimeParseContext, paramCharSequence, i);
    if (i < 0) {
      paramParsePosition.setErrorIndex(i ^ 0xFFFFFFFF);
      return null;
    } 
    paramParsePosition.setIndex(i);
    return dateTimeParseContext;
  }
  
  DateTimeFormatterBuilder.CompositePrinterParser toPrinterParser(boolean paramBoolean) { return this.printerParser.withOptional(paramBoolean); }
  
  public Format toFormat() { return new ClassicFormat(this, null); }
  
  public Format toFormat(TemporalQuery<?> paramTemporalQuery) {
    Objects.requireNonNull(paramTemporalQuery, "parseQuery");
    return new ClassicFormat(this, paramTemporalQuery);
  }
  
  public String toString() {
    null = this.printerParser.toString();
    return null.startsWith("[") ? null : null.substring(1, null.length() - 1);
  }
  
  static  {
    HashMap hashMap1 = new HashMap();
    hashMap1.put(Long.valueOf(1L), "Mon");
    hashMap1.put(Long.valueOf(2L), "Tue");
    hashMap1.put(Long.valueOf(3L), "Wed");
    hashMap1.put(Long.valueOf(4L), "Thu");
    hashMap1.put(Long.valueOf(5L), "Fri");
    hashMap1.put(Long.valueOf(6L), "Sat");
    hashMap1.put(Long.valueOf(7L), "Sun");
    HashMap hashMap2 = new HashMap();
    hashMap2.put(Long.valueOf(1L), "Jan");
    hashMap2.put(Long.valueOf(2L), "Feb");
    hashMap2.put(Long.valueOf(3L), "Mar");
    hashMap2.put(Long.valueOf(4L), "Apr");
    hashMap2.put(Long.valueOf(5L), "May");
    hashMap2.put(Long.valueOf(6L), "Jun");
    hashMap2.put(Long.valueOf(7L), "Jul");
    hashMap2.put(Long.valueOf(8L), "Aug");
    hashMap2.put(Long.valueOf(9L), "Sep");
    hashMap2.put(Long.valueOf(10L), "Oct");
    hashMap2.put(Long.valueOf(11L), "Nov");
    hashMap2.put(Long.valueOf(12L), "Dec");
    RFC_1123_DATE_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().parseLenient().optionalStart().appendText(ChronoField.DAY_OF_WEEK, hashMap1).appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral(' ').appendText(ChronoField.MONTH_OF_YEAR, hashMap2).appendLiteral(' ').appendValue(ChronoField.YEAR, 4).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(' ').appendOffset("+HHMM", "GMT").toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
    PARSED_EXCESS_DAYS = (paramTemporalAccessor -> (paramTemporalAccessor instanceof Parsed) ? ((Parsed)paramTemporalAccessor).excessDays : Period.ZERO);
    PARSED_LEAP_SECOND = (paramTemporalAccessor -> (paramTemporalAccessor instanceof Parsed) ? Boolean.valueOf(((Parsed)paramTemporalAccessor).leapSecond) : Boolean.FALSE);
  }
  
  static class ClassicFormat extends Format {
    private final DateTimeFormatter formatter;
    
    private final TemporalQuery<?> parseType;
    
    public ClassicFormat(DateTimeFormatter param1DateTimeFormatter, TemporalQuery<?> param1TemporalQuery) {
      this.formatter = param1DateTimeFormatter;
      this.parseType = param1TemporalQuery;
    }
    
    public StringBuffer format(Object param1Object, StringBuffer param1StringBuffer, FieldPosition param1FieldPosition) {
      Objects.requireNonNull(param1Object, "obj");
      Objects.requireNonNull(param1StringBuffer, "toAppendTo");
      Objects.requireNonNull(param1FieldPosition, "pos");
      if (!(param1Object instanceof TemporalAccessor))
        throw new IllegalArgumentException("Format target must implement TemporalAccessor"); 
      param1FieldPosition.setBeginIndex(0);
      param1FieldPosition.setEndIndex(0);
      try {
        this.formatter.formatTo((TemporalAccessor)param1Object, param1StringBuffer);
      } catch (RuntimeException runtimeException) {
        throw new IllegalArgumentException(runtimeException.getMessage(), runtimeException);
      } 
      return param1StringBuffer;
    }
    
    public Object parseObject(String param1String) throws ParseException {
      Objects.requireNonNull(param1String, "text");
      try {
        return (this.parseType == null) ? this.formatter.parseResolved0(param1String, null) : this.formatter.parse(param1String, this.parseType);
      } catch (DateTimeParseException dateTimeParseException) {
        throw new ParseException(dateTimeParseException.getMessage(), dateTimeParseException.getErrorIndex());
      } catch (RuntimeException runtimeException) {
        throw (ParseException)(new ParseException(runtimeException.getMessage(), 0)).initCause(runtimeException);
      } 
    }
    
    public Object parseObject(String param1String, ParsePosition param1ParsePosition) {
      DateTimeParseContext dateTimeParseContext;
      Objects.requireNonNull(param1String, "text");
      try {
        dateTimeParseContext = this.formatter.parseUnresolved0(param1String, param1ParsePosition);
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        if (param1ParsePosition.getErrorIndex() < 0)
          param1ParsePosition.setErrorIndex(0); 
        return null;
      } 
      if (dateTimeParseContext == null) {
        if (param1ParsePosition.getErrorIndex() < 0)
          param1ParsePosition.setErrorIndex(0); 
        return null;
      } 
      try {
        TemporalAccessor temporalAccessor = dateTimeParseContext.toResolved(this.formatter.resolverStyle, this.formatter.resolverFields);
        return (this.parseType == null) ? temporalAccessor : temporalAccessor.query(this.parseType);
      } catch (RuntimeException runtimeException) {
        param1ParsePosition.setErrorIndex(0);
        return null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\DateTimeFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */