package java.time.format;

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.time.zone.ZoneRulesProvider;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.TimeZoneNameUtility;

public final class DateTimeFormatterBuilder {
  private static final TemporalQuery<ZoneId> QUERY_REGION_ONLY = paramTemporalAccessor -> {
      ZoneId zoneId = (ZoneId)paramTemporalAccessor.query(TemporalQueries.zoneId());
      return (zoneId != null && !(zoneId instanceof ZoneOffset)) ? zoneId : null;
    };
  
  private DateTimeFormatterBuilder active = this;
  
  private final DateTimeFormatterBuilder parent = null;
  
  private final List<DateTimePrinterParser> printerParsers = new ArrayList();
  
  private final boolean optional = false;
  
  private int padNextWidth;
  
  private char padNextChar;
  
  private int valueParserIndex = -1;
  
  private static final Map<Character, TemporalField> FIELD_MAP = new HashMap();
  
  static final Comparator<String> LENGTH_SORT;
  
  public static String getLocalizedDateTimePattern(FormatStyle paramFormatStyle1, FormatStyle paramFormatStyle2, Chronology paramChronology, Locale paramLocale) {
    Objects.requireNonNull(paramLocale, "locale");
    Objects.requireNonNull(paramChronology, "chrono");
    if (paramFormatStyle1 == null && paramFormatStyle2 == null)
      throw new IllegalArgumentException("Either dateStyle or timeStyle must be non-null"); 
    LocaleResources localeResources = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(paramLocale);
    return localeResources.getJavaTimeDateTimePattern(convertStyle(paramFormatStyle2), convertStyle(paramFormatStyle1), paramChronology.getCalendarType());
  }
  
  private static int convertStyle(FormatStyle paramFormatStyle) { return (paramFormatStyle == null) ? -1 : paramFormatStyle.ordinal(); }
  
  public DateTimeFormatterBuilder() {}
  
  private DateTimeFormatterBuilder(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, boolean paramBoolean) {}
  
  public DateTimeFormatterBuilder parseCaseSensitive() {
    appendInternal(SettingsParser.SENSITIVE);
    return this;
  }
  
  public DateTimeFormatterBuilder parseCaseInsensitive() {
    appendInternal(SettingsParser.INSENSITIVE);
    return this;
  }
  
  public DateTimeFormatterBuilder parseStrict() {
    appendInternal(SettingsParser.STRICT);
    return this;
  }
  
  public DateTimeFormatterBuilder parseLenient() {
    appendInternal(SettingsParser.LENIENT);
    return this;
  }
  
  public DateTimeFormatterBuilder parseDefaulting(TemporalField paramTemporalField, long paramLong) {
    Objects.requireNonNull(paramTemporalField, "field");
    appendInternal(new DefaultValueParser(paramTemporalField, paramLong));
    return this;
  }
  
  public DateTimeFormatterBuilder appendValue(TemporalField paramTemporalField) {
    Objects.requireNonNull(paramTemporalField, "field");
    appendValue(new NumberPrinterParser(paramTemporalField, 1, 19, SignStyle.NORMAL));
    return this;
  }
  
  public DateTimeFormatterBuilder appendValue(TemporalField paramTemporalField, int paramInt) {
    Objects.requireNonNull(paramTemporalField, "field");
    if (paramInt < 1 || paramInt > 19)
      throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + paramInt); 
    NumberPrinterParser numberPrinterParser = new NumberPrinterParser(paramTemporalField, paramInt, paramInt, SignStyle.NOT_NEGATIVE);
    appendValue(numberPrinterParser);
    return this;
  }
  
  public DateTimeFormatterBuilder appendValue(TemporalField paramTemporalField, int paramInt1, int paramInt2, SignStyle paramSignStyle) {
    if (paramInt1 == paramInt2 && paramSignStyle == SignStyle.NOT_NEGATIVE)
      return appendValue(paramTemporalField, paramInt2); 
    Objects.requireNonNull(paramTemporalField, "field");
    Objects.requireNonNull(paramSignStyle, "signStyle");
    if (paramInt1 < 1 || paramInt1 > 19)
      throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + paramInt1); 
    if (paramInt2 < 1 || paramInt2 > 19)
      throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + paramInt2); 
    if (paramInt2 < paramInt1)
      throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + paramInt2 + " < " + paramInt1); 
    NumberPrinterParser numberPrinterParser = new NumberPrinterParser(paramTemporalField, paramInt1, paramInt2, paramSignStyle);
    appendValue(numberPrinterParser);
    return this;
  }
  
  public DateTimeFormatterBuilder appendValueReduced(TemporalField paramTemporalField, int paramInt1, int paramInt2, int paramInt3) {
    Objects.requireNonNull(paramTemporalField, "field");
    ReducedPrinterParser reducedPrinterParser = new ReducedPrinterParser(paramTemporalField, paramInt1, paramInt2, paramInt3, null);
    appendValue(reducedPrinterParser);
    return this;
  }
  
  public DateTimeFormatterBuilder appendValueReduced(TemporalField paramTemporalField, int paramInt1, int paramInt2, ChronoLocalDate paramChronoLocalDate) {
    Objects.requireNonNull(paramTemporalField, "field");
    Objects.requireNonNull(paramChronoLocalDate, "baseDate");
    ReducedPrinterParser reducedPrinterParser = new ReducedPrinterParser(paramTemporalField, paramInt1, paramInt2, 0, paramChronoLocalDate);
    appendValue(reducedPrinterParser);
    return this;
  }
  
  private DateTimeFormatterBuilder appendValue(NumberPrinterParser paramNumberPrinterParser) {
    if (this.active.valueParserIndex >= 0) {
      int i = this.active.valueParserIndex;
      NumberPrinterParser numberPrinterParser;
      if (paramNumberPrinterParser.minWidth == paramNumberPrinterParser.maxWidth && paramNumberPrinterParser.signStyle == SignStyle.NOT_NEGATIVE) {
        numberPrinterParser = numberPrinterParser.withSubsequentWidth(paramNumberPrinterParser.maxWidth);
        appendInternal(paramNumberPrinterParser.withFixedWidth());
        this.active.valueParserIndex = i;
      } else {
        numberPrinterParser = numberPrinterParser.withFixedWidth();
        this.active.valueParserIndex = appendInternal(paramNumberPrinterParser);
      } 
      this.active.printerParsers.set(i, numberPrinterParser);
    } else {
      this.active.valueParserIndex = appendInternal(paramNumberPrinterParser);
    } 
    return this;
  }
  
  public DateTimeFormatterBuilder appendFraction(TemporalField paramTemporalField, int paramInt1, int paramInt2, boolean paramBoolean) {
    appendInternal(new FractionPrinterParser(paramTemporalField, paramInt1, paramInt2, paramBoolean));
    return this;
  }
  
  public DateTimeFormatterBuilder appendText(TemporalField paramTemporalField) { return appendText(paramTemporalField, TextStyle.FULL); }
  
  public DateTimeFormatterBuilder appendText(TemporalField paramTemporalField, TextStyle paramTextStyle) {
    Objects.requireNonNull(paramTemporalField, "field");
    Objects.requireNonNull(paramTextStyle, "textStyle");
    appendInternal(new TextPrinterParser(paramTemporalField, paramTextStyle, DateTimeTextProvider.getInstance()));
    return this;
  }
  
  public DateTimeFormatterBuilder appendText(TemporalField paramTemporalField, Map<Long, String> paramMap) {
    Objects.requireNonNull(paramTemporalField, "field");
    Objects.requireNonNull(paramMap, "textLookup");
    LinkedHashMap linkedHashMap = new LinkedHashMap(paramMap);
    Map map = Collections.singletonMap(TextStyle.FULL, linkedHashMap);
    final DateTimeTextProvider.LocaleStore store = new DateTimeTextProvider.LocaleStore(map);
    DateTimeTextProvider dateTimeTextProvider = new DateTimeTextProvider() {
        public String getText(TemporalField param1TemporalField, long param1Long, TextStyle param1TextStyle, Locale param1Locale) { return store.getText(param1Long, param1TextStyle); }
        
        public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField param1TemporalField, TextStyle param1TextStyle, Locale param1Locale) { return store.getTextIterator(param1TextStyle); }
      };
    appendInternal(new TextPrinterParser(paramTemporalField, TextStyle.FULL, dateTimeTextProvider));
    return this;
  }
  
  public DateTimeFormatterBuilder appendInstant() {
    appendInternal(new InstantPrinterParser(-2));
    return this;
  }
  
  public DateTimeFormatterBuilder appendInstant(int paramInt) {
    if (paramInt < -1 || paramInt > 9)
      throw new IllegalArgumentException("The fractional digits must be from -1 to 9 inclusive but was " + paramInt); 
    appendInternal(new InstantPrinterParser(paramInt));
    return this;
  }
  
  public DateTimeFormatterBuilder appendOffsetId() {
    appendInternal(OffsetIdPrinterParser.INSTANCE_ID_Z);
    return this;
  }
  
  public DateTimeFormatterBuilder appendOffset(String paramString1, String paramString2) {
    appendInternal(new OffsetIdPrinterParser(paramString1, paramString2));
    return this;
  }
  
  public DateTimeFormatterBuilder appendLocalizedOffset(TextStyle paramTextStyle) {
    Objects.requireNonNull(paramTextStyle, "style");
    if (paramTextStyle != TextStyle.FULL && paramTextStyle != TextStyle.SHORT)
      throw new IllegalArgumentException("Style must be either full or short"); 
    appendInternal(new LocalizedOffsetIdPrinterParser(paramTextStyle));
    return this;
  }
  
  public DateTimeFormatterBuilder appendZoneId() {
    appendInternal(new ZoneIdPrinterParser(TemporalQueries.zoneId(), "ZoneId()"));
    return this;
  }
  
  public DateTimeFormatterBuilder appendZoneRegionId() {
    appendInternal(new ZoneIdPrinterParser(QUERY_REGION_ONLY, "ZoneRegionId()"));
    return this;
  }
  
  public DateTimeFormatterBuilder appendZoneOrOffsetId() {
    appendInternal(new ZoneIdPrinterParser(TemporalQueries.zone(), "ZoneOrOffsetId()"));
    return this;
  }
  
  public DateTimeFormatterBuilder appendZoneText(TextStyle paramTextStyle) {
    appendInternal(new ZoneTextPrinterParser(paramTextStyle, null));
    return this;
  }
  
  public DateTimeFormatterBuilder appendZoneText(TextStyle paramTextStyle, Set<ZoneId> paramSet) {
    Objects.requireNonNull(paramSet, "preferredZones");
    appendInternal(new ZoneTextPrinterParser(paramTextStyle, paramSet));
    return this;
  }
  
  public DateTimeFormatterBuilder appendChronologyId() {
    appendInternal(new ChronoPrinterParser(null));
    return this;
  }
  
  public DateTimeFormatterBuilder appendChronologyText(TextStyle paramTextStyle) {
    Objects.requireNonNull(paramTextStyle, "textStyle");
    appendInternal(new ChronoPrinterParser(paramTextStyle));
    return this;
  }
  
  public DateTimeFormatterBuilder appendLocalized(FormatStyle paramFormatStyle1, FormatStyle paramFormatStyle2) {
    if (paramFormatStyle1 == null && paramFormatStyle2 == null)
      throw new IllegalArgumentException("Either the date or time style must be non-null"); 
    appendInternal(new LocalizedPrinterParser(paramFormatStyle1, paramFormatStyle2));
    return this;
  }
  
  public DateTimeFormatterBuilder appendLiteral(char paramChar) {
    appendInternal(new CharLiteralPrinterParser(paramChar));
    return this;
  }
  
  public DateTimeFormatterBuilder appendLiteral(String paramString) {
    Objects.requireNonNull(paramString, "literal");
    if (paramString.length() > 0)
      if (paramString.length() == 1) {
        appendInternal(new CharLiteralPrinterParser(paramString.charAt(0)));
      } else {
        appendInternal(new StringLiteralPrinterParser(paramString));
      }  
    return this;
  }
  
  public DateTimeFormatterBuilder append(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    appendInternal(paramDateTimeFormatter.toPrinterParser(false));
    return this;
  }
  
  public DateTimeFormatterBuilder appendOptional(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    appendInternal(paramDateTimeFormatter.toPrinterParser(true));
    return this;
  }
  
  public DateTimeFormatterBuilder appendPattern(String paramString) {
    Objects.requireNonNull(paramString, "pattern");
    parsePattern(paramString);
    return this;
  }
  
  private void parsePattern(String paramString) {
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
        byte b1 = b++;
        while (b < paramString.length() && paramString.charAt(b) == c)
          b++; 
        byte b2 = b - b1;
        if (c == 'p') {
          byte b3 = 0;
          if (b < paramString.length()) {
            c = paramString.charAt(b);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
              b3 = b2;
              b1 = b++;
              while (b < paramString.length() && paramString.charAt(b) == c)
                b++; 
              b2 = b - b1;
            } 
          } 
          if (b3 == 0)
            throw new IllegalArgumentException("Pad letter 'p' must be followed by valid pad pattern: " + paramString); 
          padNext(b3);
        } 
        TemporalField temporalField = (TemporalField)FIELD_MAP.get(Character.valueOf(c));
        if (temporalField != null) {
          parseField(c, b2, temporalField);
        } else if (c == 'z') {
          if (b2 > 4)
            throw new IllegalArgumentException("Too many pattern letters: " + c); 
          if (b2 == 4) {
            appendZoneText(TextStyle.FULL);
          } else {
            appendZoneText(TextStyle.SHORT);
          } 
        } else if (c == 'V') {
          if (b2 != 2)
            throw new IllegalArgumentException("Pattern letter count must be 2: " + c); 
          appendZoneId();
        } else if (c == 'Z') {
          if (b2 < 4) {
            appendOffset("+HHMM", "+0000");
          } else if (b2 == 4) {
            appendLocalizedOffset(TextStyle.FULL);
          } else if (b2 == 5) {
            appendOffset("+HH:MM:ss", "Z");
          } else {
            throw new IllegalArgumentException("Too many pattern letters: " + c);
          } 
        } else if (c == 'O') {
          if (b2 == 1) {
            appendLocalizedOffset(TextStyle.SHORT);
          } else if (b2 == 4) {
            appendLocalizedOffset(TextStyle.FULL);
          } else {
            throw new IllegalArgumentException("Pattern letter count must be 1 or 4: " + c);
          } 
        } else if (c == 'X') {
          if (b2 > 5)
            throw new IllegalArgumentException("Too many pattern letters: " + c); 
          appendOffset(OffsetIdPrinterParser.PATTERNS[b2 + ((b2 == 1) ? 0 : 1)], "Z");
        } else if (c == 'x') {
          if (b2 > 5)
            throw new IllegalArgumentException("Too many pattern letters: " + c); 
          String str = (b2 == 1) ? "+00" : ((b2 % 2 == 0) ? "+0000" : "+00:00");
          appendOffset(OffsetIdPrinterParser.PATTERNS[b2 + ((b2 == 1) ? 0 : 1)], str);
        } else if (c == 'W') {
          if (b2 > 1)
            throw new IllegalArgumentException("Too many pattern letters: " + c); 
          appendInternal(new WeekBasedFieldPrinterParser(c, b2));
        } else if (c == 'w') {
          if (b2 > 2)
            throw new IllegalArgumentException("Too many pattern letters: " + c); 
          appendInternal(new WeekBasedFieldPrinterParser(c, b2));
        } else if (c == 'Y') {
          appendInternal(new WeekBasedFieldPrinterParser(c, b2));
        } else {
          throw new IllegalArgumentException("Unknown pattern letter: " + c);
        } 
        b--;
      } else if (c == '\'') {
        byte b1 = b++;
        while (b < paramString.length()) {
          if (paramString.charAt(b) == '\'')
            if (b + 1 < paramString.length() && paramString.charAt(b + 1) == '\'') {
              b++;
            } else {
              break;
            }  
          b++;
        } 
        if (b >= paramString.length())
          throw new IllegalArgumentException("Pattern ends with an incomplete string literal: " + paramString); 
        String str = paramString.substring(b1 + 1, b);
        if (str.length() == 0) {
          appendLiteral('\'');
        } else {
          appendLiteral(str.replace("''", "'"));
        } 
      } else if (c == '[') {
        optionalStart();
      } else if (c == ']') {
        if (this.active.parent == null)
          throw new IllegalArgumentException("Pattern invalid as it contains ] without previous ["); 
        optionalEnd();
      } else {
        if (c == '{' || c == '}' || c == '#')
          throw new IllegalArgumentException("Pattern includes reserved character: '" + c + "'"); 
        appendLiteral(c);
      } 
    } 
  }
  
  private void parseField(char paramChar, int paramInt, TemporalField paramTemporalField) {
    boolean bool = false;
    switch (paramChar) {
      case 'u':
      case 'y':
        if (paramInt == 2) {
          appendValueReduced(paramTemporalField, 2, 2, ReducedPrinterParser.BASE_DATE);
        } else if (paramInt < 4) {
          appendValue(paramTemporalField, paramInt, 19, SignStyle.NORMAL);
        } else {
          appendValue(paramTemporalField, paramInt, 19, SignStyle.EXCEEDS_PAD);
        } 
        return;
      case 'c':
        if (paramInt == 2)
          throw new IllegalArgumentException("Invalid pattern \"cc\""); 
      case 'L':
      case 'q':
        bool = true;
      case 'E':
      case 'M':
      case 'Q':
      case 'e':
        switch (paramInt) {
          case 1:
          case 2:
            if (paramChar == 'c' || paramChar == 'e') {
              appendInternal(new WeekBasedFieldPrinterParser(paramChar, paramInt));
            } else if (paramChar == 'E') {
              appendText(paramTemporalField, TextStyle.SHORT);
            } else if (paramInt == 1) {
              appendValue(paramTemporalField);
            } else {
              appendValue(paramTemporalField, 2);
            } 
            return;
          case 3:
            appendText(paramTemporalField, bool ? TextStyle.SHORT_STANDALONE : TextStyle.SHORT);
            return;
          case 4:
            appendText(paramTemporalField, bool ? TextStyle.FULL_STANDALONE : TextStyle.FULL);
            return;
          case 5:
            appendText(paramTemporalField, bool ? TextStyle.NARROW_STANDALONE : TextStyle.NARROW);
            return;
        } 
        throw new IllegalArgumentException("Too many pattern letters: " + paramChar);
      case 'a':
        if (paramInt == 1) {
          appendText(paramTemporalField, TextStyle.SHORT);
        } else {
          throw new IllegalArgumentException("Too many pattern letters: " + paramChar);
        } 
        return;
      case 'G':
        switch (paramInt) {
          case 1:
          case 2:
          case 3:
            appendText(paramTemporalField, TextStyle.SHORT);
            return;
          case 4:
            appendText(paramTemporalField, TextStyle.FULL);
            return;
          case 5:
            appendText(paramTemporalField, TextStyle.NARROW);
            return;
        } 
        throw new IllegalArgumentException("Too many pattern letters: " + paramChar);
      case 'S':
        appendFraction(ChronoField.NANO_OF_SECOND, paramInt, paramInt, false);
        return;
      case 'F':
        if (paramInt == 1) {
          appendValue(paramTemporalField);
        } else {
          throw new IllegalArgumentException("Too many pattern letters: " + paramChar);
        } 
        return;
      case 'H':
      case 'K':
      case 'd':
      case 'h':
      case 'k':
      case 'm':
      case 's':
        if (paramInt == 1) {
          appendValue(paramTemporalField);
        } else if (paramInt == 2) {
          appendValue(paramTemporalField, paramInt);
        } else {
          throw new IllegalArgumentException("Too many pattern letters: " + paramChar);
        } 
        return;
      case 'D':
        if (paramInt == 1) {
          appendValue(paramTemporalField);
        } else if (paramInt <= 3) {
          appendValue(paramTemporalField, paramInt);
        } else {
          throw new IllegalArgumentException("Too many pattern letters: " + paramChar);
        } 
        return;
    } 
    if (paramInt == 1) {
      appendValue(paramTemporalField);
    } else {
      appendValue(paramTemporalField, paramInt);
    } 
  }
  
  public DateTimeFormatterBuilder padNext(int paramInt) { return padNext(paramInt, ' '); }
  
  public DateTimeFormatterBuilder padNext(int paramInt, char paramChar) {
    if (paramInt < 1)
      throw new IllegalArgumentException("The pad width must be at least one but was " + paramInt); 
    this.active.padNextWidth = paramInt;
    this.active.padNextChar = paramChar;
    this.active.valueParserIndex = -1;
    return this;
  }
  
  public DateTimeFormatterBuilder optionalStart() {
    this.active.valueParserIndex = -1;
    this.active = new DateTimeFormatterBuilder(this.active, true);
    return this;
  }
  
  public DateTimeFormatterBuilder optionalEnd() {
    if (this.active.parent == null)
      throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()"); 
    if (this.active.printerParsers.size() > 0) {
      CompositePrinterParser compositePrinterParser = new CompositePrinterParser(this.active.printerParsers, this.active.optional);
      this.active = this.active.parent;
      appendInternal(compositePrinterParser);
    } else {
      this.active = this.active.parent;
    } 
    return this;
  }
  
  private int appendInternal(DateTimePrinterParser paramDateTimePrinterParser) {
    Objects.requireNonNull(paramDateTimePrinterParser, "pp");
    if (this.active.padNextWidth > 0) {
      if (paramDateTimePrinterParser != null)
        paramDateTimePrinterParser = new PadPrinterParserDecorator(paramDateTimePrinterParser, this.active.padNextWidth, this.active.padNextChar); 
      this.active.padNextWidth = 0;
      this.active.padNextChar = Character.MIN_VALUE;
    } 
    this.active.printerParsers.add(paramDateTimePrinterParser);
    this.active.valueParserIndex = -1;
    return this.active.printerParsers.size() - 1;
  }
  
  public DateTimeFormatter toFormatter() { return toFormatter(Locale.getDefault(Locale.Category.FORMAT)); }
  
  public DateTimeFormatter toFormatter(Locale paramLocale) { return toFormatter(paramLocale, ResolverStyle.SMART, null); }
  
  DateTimeFormatter toFormatter(ResolverStyle paramResolverStyle, Chronology paramChronology) { return toFormatter(Locale.getDefault(Locale.Category.FORMAT), paramResolverStyle, paramChronology); }
  
  private DateTimeFormatter toFormatter(Locale paramLocale, ResolverStyle paramResolverStyle, Chronology paramChronology) {
    Objects.requireNonNull(paramLocale, "locale");
    while (this.active.parent != null)
      optionalEnd(); 
    CompositePrinterParser compositePrinterParser = new CompositePrinterParser(this.printerParsers, false);
    return new DateTimeFormatter(compositePrinterParser, paramLocale, DecimalStyle.STANDARD, paramResolverStyle, null, paramChronology, null);
  }
  
  static  {
    FIELD_MAP.put(Character.valueOf('G'), ChronoField.ERA);
    FIELD_MAP.put(Character.valueOf('y'), ChronoField.YEAR_OF_ERA);
    FIELD_MAP.put(Character.valueOf('u'), ChronoField.YEAR);
    FIELD_MAP.put(Character.valueOf('Q'), IsoFields.QUARTER_OF_YEAR);
    FIELD_MAP.put(Character.valueOf('q'), IsoFields.QUARTER_OF_YEAR);
    FIELD_MAP.put(Character.valueOf('M'), ChronoField.MONTH_OF_YEAR);
    FIELD_MAP.put(Character.valueOf('L'), ChronoField.MONTH_OF_YEAR);
    FIELD_MAP.put(Character.valueOf('D'), ChronoField.DAY_OF_YEAR);
    FIELD_MAP.put(Character.valueOf('d'), ChronoField.DAY_OF_MONTH);
    FIELD_MAP.put(Character.valueOf('F'), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
    FIELD_MAP.put(Character.valueOf('E'), ChronoField.DAY_OF_WEEK);
    FIELD_MAP.put(Character.valueOf('c'), ChronoField.DAY_OF_WEEK);
    FIELD_MAP.put(Character.valueOf('e'), ChronoField.DAY_OF_WEEK);
    FIELD_MAP.put(Character.valueOf('a'), ChronoField.AMPM_OF_DAY);
    FIELD_MAP.put(Character.valueOf('H'), ChronoField.HOUR_OF_DAY);
    FIELD_MAP.put(Character.valueOf('k'), ChronoField.CLOCK_HOUR_OF_DAY);
    FIELD_MAP.put(Character.valueOf('K'), ChronoField.HOUR_OF_AMPM);
    FIELD_MAP.put(Character.valueOf('h'), ChronoField.CLOCK_HOUR_OF_AMPM);
    FIELD_MAP.put(Character.valueOf('m'), ChronoField.MINUTE_OF_HOUR);
    FIELD_MAP.put(Character.valueOf('s'), ChronoField.SECOND_OF_MINUTE);
    FIELD_MAP.put(Character.valueOf('S'), ChronoField.NANO_OF_SECOND);
    FIELD_MAP.put(Character.valueOf('A'), ChronoField.MILLI_OF_DAY);
    FIELD_MAP.put(Character.valueOf('n'), ChronoField.NANO_OF_SECOND);
    FIELD_MAP.put(Character.valueOf('N'), ChronoField.NANO_OF_DAY);
    LENGTH_SORT = new Comparator<String>() {
        public int compare(String param1String1, String param1String2) { return (param1String1.length() == param1String2.length()) ? param1String1.compareTo(param1String2) : (param1String1.length() - param1String2.length()); }
      };
  }
  
  static final class CharLiteralPrinterParser implements DateTimePrinterParser {
    private final char literal;
    
    CharLiteralPrinterParser(char param1Char) { this.literal = param1Char; }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      param1StringBuilder.append(this.literal);
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1CharSequence.length();
      if (param1Int == i)
        return param1Int ^ 0xFFFFFFFF; 
      char c = param1CharSequence.charAt(param1Int);
      return (c != this.literal && (param1DateTimeParseContext.isCaseSensitive() || (Character.toUpperCase(c) != Character.toUpperCase(this.literal) && Character.toLowerCase(c) != Character.toLowerCase(this.literal)))) ? (param1Int ^ 0xFFFFFFFF) : (param1Int + 1);
    }
    
    public String toString() { return (this.literal == '\'') ? "''" : ("'" + this.literal + "'"); }
  }
  
  static final class ChronoPrinterParser implements DateTimePrinterParser {
    private final TextStyle textStyle;
    
    ChronoPrinterParser(TextStyle param1TextStyle) { this.textStyle = param1TextStyle; }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Chronology chronology = (Chronology)param1DateTimePrintContext.getValue(TemporalQueries.chronology());
      if (chronology == null)
        return false; 
      if (this.textStyle == null) {
        param1StringBuilder.append(chronology.getId());
      } else {
        param1StringBuilder.append(getChronologyName(chronology, param1DateTimePrintContext.getLocale()));
      } 
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      if (param1Int < 0 || param1Int > param1CharSequence.length())
        throw new IndexOutOfBoundsException(); 
      Set set = Chronology.getAvailableChronologies();
      Chronology chronology = null;
      int i = -1;
      for (Chronology chronology1 : set) {
        String str;
        if (this.textStyle == null) {
          str = chronology1.getId();
        } else {
          str = getChronologyName(chronology1, param1DateTimeParseContext.getLocale());
        } 
        int j = str.length();
        if (j > i && param1DateTimeParseContext.subSequenceEquals(param1CharSequence, param1Int, str, 0, j)) {
          chronology = chronology1;
          i = j;
        } 
      } 
      if (chronology == null)
        return param1Int ^ 0xFFFFFFFF; 
      param1DateTimeParseContext.setParsed(chronology);
      return param1Int + i;
    }
    
    private String getChronologyName(Chronology param1Chronology, Locale param1Locale) {
      String str1 = "calendarname." + param1Chronology.getCalendarType();
      String str2 = (String)DateTimeTextProvider.getLocalizedResource(str1, param1Locale);
      return (str2 != null) ? str2 : param1Chronology.getId();
    }
  }
  
  static final class CompositePrinterParser implements DateTimePrinterParser {
    private final DateTimeFormatterBuilder.DateTimePrinterParser[] printerParsers;
    
    private final boolean optional;
    
    CompositePrinterParser(List<DateTimeFormatterBuilder.DateTimePrinterParser> param1List, boolean param1Boolean) { this((DateTimePrinterParser[])param1List.toArray(new DateTimeFormatterBuilder.DateTimePrinterParser[param1List.size()]), param1Boolean); }
    
    CompositePrinterParser(DateTimeFormatterBuilder.DateTimePrinterParser[] param1ArrayOfDateTimePrinterParser, boolean param1Boolean) {
      this.printerParsers = param1ArrayOfDateTimePrinterParser;
      this.optional = param1Boolean;
    }
    
    public CompositePrinterParser withOptional(boolean param1Boolean) { return (param1Boolean == this.optional) ? this : new CompositePrinterParser(this.printerParsers, param1Boolean); }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      int i = param1StringBuilder.length();
      if (this.optional)
        param1DateTimePrintContext.startOptional(); 
      try {
        for (DateTimeFormatterBuilder.DateTimePrinterParser dateTimePrinterParser : this.printerParsers) {
          if (!dateTimePrinterParser.format(param1DateTimePrintContext, param1StringBuilder)) {
            param1StringBuilder.setLength(i);
            return true;
          } 
        } 
      } finally {
        if (this.optional)
          param1DateTimePrintContext.endOptional(); 
      } 
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      if (this.optional) {
        param1DateTimeParseContext.startOptional();
        int i = param1Int;
        for (DateTimeFormatterBuilder.DateTimePrinterParser dateTimePrinterParser : this.printerParsers) {
          i = dateTimePrinterParser.parse(param1DateTimeParseContext, param1CharSequence, i);
          if (i < 0) {
            param1DateTimeParseContext.endOptional(false);
            return param1Int;
          } 
        } 
        param1DateTimeParseContext.endOptional(true);
        return i;
      } 
      for (DateTimeFormatterBuilder.DateTimePrinterParser dateTimePrinterParser : this.printerParsers) {
        param1Int = dateTimePrinterParser.parse(param1DateTimeParseContext, param1CharSequence, param1Int);
        if (param1Int < 0)
          break; 
      } 
      return param1Int;
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.printerParsers != null) {
        stringBuilder.append(this.optional ? "[" : "(");
        for (DateTimeFormatterBuilder.DateTimePrinterParser dateTimePrinterParser : this.printerParsers)
          stringBuilder.append(dateTimePrinterParser); 
        stringBuilder.append(this.optional ? "]" : ")");
      } 
      return stringBuilder.toString();
    }
  }
  
  static interface DateTimePrinterParser {
    boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder);
    
    int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int);
  }
  
  static class DefaultValueParser implements DateTimePrinterParser {
    private final TemporalField field;
    
    private final long value;
    
    DefaultValueParser(TemporalField param1TemporalField, long param1Long) {
      this.field = param1TemporalField;
      this.value = param1Long;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) { return true; }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      if (param1DateTimeParseContext.getParsed(this.field) == null)
        param1DateTimeParseContext.setParsedField(this.field, this.value, param1Int, param1Int); 
      return param1Int;
    }
  }
  
  static final class FractionPrinterParser implements DateTimePrinterParser {
    private final TemporalField field;
    
    private final int minWidth;
    
    private final int maxWidth;
    
    private final boolean decimalPoint;
    
    FractionPrinterParser(TemporalField param1TemporalField, int param1Int1, int param1Int2, boolean param1Boolean) {
      Objects.requireNonNull(param1TemporalField, "field");
      if (!param1TemporalField.range().isFixed())
        throw new IllegalArgumentException("Field must have a fixed set of values: " + param1TemporalField); 
      if (param1Int1 < 0 || param1Int1 > 9)
        throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + param1Int1); 
      if (param1Int2 < 1 || param1Int2 > 9)
        throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + param1Int2); 
      if (param1Int2 < param1Int1)
        throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + param1Int2 + " < " + param1Int1); 
      this.field = param1TemporalField;
      this.minWidth = param1Int1;
      this.maxWidth = param1Int2;
      this.decimalPoint = param1Boolean;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Long long = param1DateTimePrintContext.getValue(this.field);
      if (long == null)
        return false; 
      DecimalStyle decimalStyle = param1DateTimePrintContext.getDecimalStyle();
      BigDecimal bigDecimal = convertToFraction(long.longValue());
      if (bigDecimal.scale() == 0) {
        if (this.minWidth > 0) {
          if (this.decimalPoint)
            param1StringBuilder.append(decimalStyle.getDecimalSeparator()); 
          for (byte b = 0; b < this.minWidth; b++)
            param1StringBuilder.append(decimalStyle.getZeroDigit()); 
        } 
      } else {
        int i = Math.min(Math.max(bigDecimal.scale(), this.minWidth), this.maxWidth);
        bigDecimal = bigDecimal.setScale(i, RoundingMode.FLOOR);
        String str = bigDecimal.toPlainString().substring(2);
        str = decimalStyle.convertNumberToI18N(str);
        if (this.decimalPoint)
          param1StringBuilder.append(decimalStyle.getDecimalSeparator()); 
        param1StringBuilder.append(str);
      } 
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1DateTimeParseContext.isStrict() ? this.minWidth : 0;
      int j = param1DateTimeParseContext.isStrict() ? this.maxWidth : 9;
      int k = param1CharSequence.length();
      if (param1Int == k)
        return (i > 0) ? (param1Int ^ 0xFFFFFFFF) : param1Int; 
      if (this.decimalPoint) {
        if (param1CharSequence.charAt(param1Int) != param1DateTimeParseContext.getDecimalStyle().getDecimalSeparator())
          return (i > 0) ? (param1Int ^ 0xFFFFFFFF) : param1Int; 
        param1Int++;
      } 
      int m = param1Int + i;
      if (m > k)
        return param1Int ^ 0xFFFFFFFF; 
      int n = Math.min(param1Int + j, k);
      int i1 = 0;
      int i2 = param1Int;
      while (i2 < n) {
        char c = param1CharSequence.charAt(i2++);
        int i3 = param1DateTimeParseContext.getDecimalStyle().convertToDigit(c);
        if (i3 < 0) {
          if (i2 < m)
            return param1Int ^ 0xFFFFFFFF; 
          i2--;
          break;
        } 
        i1 = i1 * 10 + i3;
      } 
      BigDecimal bigDecimal = (new BigDecimal(i1)).movePointLeft(i2 - param1Int);
      long l = convertFromFraction(bigDecimal);
      return param1DateTimeParseContext.setParsedField(this.field, l, param1Int, i2);
    }
    
    private BigDecimal convertToFraction(long param1Long) {
      ValueRange valueRange = this.field.range();
      valueRange.checkValidValue(param1Long, this.field);
      BigDecimal bigDecimal1;
      BigDecimal bigDecimal2;
      BigDecimal bigDecimal3 = (bigDecimal2 = (bigDecimal1 = BigDecimal.valueOf(valueRange.getMinimum())).valueOf(valueRange.getMaximum()).subtract(bigDecimal1).add(BigDecimal.ONE)).valueOf(param1Long).subtract(bigDecimal1);
      BigDecimal bigDecimal4 = bigDecimal3.divide(bigDecimal2, 9, RoundingMode.FLOOR);
      return (bigDecimal4.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO : bigDecimal4.stripTrailingZeros();
    }
    
    private long convertFromFraction(BigDecimal param1BigDecimal) {
      ValueRange valueRange = this.field.range();
      BigDecimal bigDecimal1;
      BigDecimal bigDecimal2 = (bigDecimal1 = BigDecimal.valueOf(valueRange.getMinimum())).valueOf(valueRange.getMaximum()).subtract(bigDecimal1).add(BigDecimal.ONE);
      BigDecimal bigDecimal3 = param1BigDecimal.multiply(bigDecimal2).setScale(0, RoundingMode.FLOOR).add(bigDecimal1);
      return bigDecimal3.longValueExact();
    }
    
    public String toString() {
      String str = this.decimalPoint ? ",DecimalPoint" : "";
      return "Fraction(" + this.field + "," + this.minWidth + "," + this.maxWidth + str + ")";
    }
  }
  
  static final class InstantPrinterParser implements DateTimePrinterParser {
    private static final long SECONDS_PER_10000_YEARS = 315569520000L;
    
    private static final long SECONDS_0000_TO_1970 = 62167219200L;
    
    private final int fractionalDigits;
    
    InstantPrinterParser(int param1Int) { this.fractionalDigits = param1Int; }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Long long1 = param1DateTimePrintContext.getValue(ChronoField.INSTANT_SECONDS);
      Long long2 = null;
      if (param1DateTimePrintContext.getTemporal().isSupported(ChronoField.NANO_OF_SECOND))
        long2 = Long.valueOf(param1DateTimePrintContext.getTemporal().getLong(ChronoField.NANO_OF_SECOND)); 
      if (long1 == null)
        return false; 
      long l = long1.longValue();
      int i = ChronoField.NANO_OF_SECOND.checkValidIntValue((long2 != null) ? long2.longValue() : 0L);
      if (l >= -62167219200L) {
        long l1 = l - 315569520000L + 62167219200L;
        long l2 = Math.floorDiv(l1, 315569520000L) + 1L;
        long l3 = Math.floorMod(l1, 315569520000L);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(l3 - 62167219200L, 0, ZoneOffset.UTC);
        if (l2 > 0L)
          param1StringBuilder.append('+').append(l2); 
        param1StringBuilder.append(localDateTime);
        if (localDateTime.getSecond() == 0)
          param1StringBuilder.append(":00"); 
      } else {
        long l1 = l + 62167219200L;
        long l2 = l1 / 315569520000L;
        long l3 = l1 % 315569520000L;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(l3 - 62167219200L, 0, ZoneOffset.UTC);
        int j = param1StringBuilder.length();
        param1StringBuilder.append(localDateTime);
        if (localDateTime.getSecond() == 0)
          param1StringBuilder.append(":00"); 
        if (l2 < 0L)
          if (localDateTime.getYear() == -10000) {
            param1StringBuilder.replace(j, j + 2, Long.toString(l2 - 1L));
          } else if (l3 == 0L) {
            param1StringBuilder.insert(j, l2);
          } else {
            param1StringBuilder.insert(j + 1, Math.abs(l2));
          }  
      } 
      if ((this.fractionalDigits < 0 && i > 0) || this.fractionalDigits > 0) {
        param1StringBuilder.append('.');
        int j = 100000000;
        for (byte b = 0; (this.fractionalDigits == -1 && i > 0) || (this.fractionalDigits == -2 && (i > 0 || b % 3 != 0)) || b < this.fractionalDigits; b++) {
          int k = i / j;
          param1StringBuilder.append((char)(k + 48));
          i -= k * j;
          j /= 10;
        } 
      } 
      param1StringBuilder.append('Z');
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      long l2;
      byte b1 = (this.fractionalDigits < 0) ? 0 : this.fractionalDigits;
      byte b2 = (this.fractionalDigits < 0) ? 9 : this.fractionalDigits;
      DateTimeFormatterBuilder.CompositePrinterParser compositePrinterParser = (new DateTimeFormatterBuilder()).append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).appendFraction(ChronoField.NANO_OF_SECOND, b1, b2, true).appendLiteral('Z').toFormatter().toPrinterParser(false);
      DateTimeParseContext dateTimeParseContext = param1DateTimeParseContext.copy();
      int i = compositePrinterParser.parse(dateTimeParseContext, param1CharSequence, param1Int);
      if (i < 0)
        return i; 
      long l1 = dateTimeParseContext.getParsed(ChronoField.YEAR).longValue();
      int j = dateTimeParseContext.getParsed(ChronoField.MONTH_OF_YEAR).intValue();
      int k = dateTimeParseContext.getParsed(ChronoField.DAY_OF_MONTH).intValue();
      int m = dateTimeParseContext.getParsed(ChronoField.HOUR_OF_DAY).intValue();
      int n = dateTimeParseContext.getParsed(ChronoField.MINUTE_OF_HOUR).intValue();
      Long long1 = dateTimeParseContext.getParsed(ChronoField.SECOND_OF_MINUTE);
      Long long2 = dateTimeParseContext.getParsed(ChronoField.NANO_OF_SECOND);
      int i1 = (long1 != null) ? long1.intValue() : 0;
      int i2 = (long2 != null) ? long2.intValue() : 0;
      boolean bool = false;
      if (m == 24 && n == 0 && i1 == 0 && i2 == 0) {
        m = 0;
        bool = true;
      } else if (m == 23 && n == 59 && i1 == 60) {
        param1DateTimeParseContext.setParsedLeapSecond();
        i1 = 59;
      } 
      int i3 = (int)l1 % 10000;
      try {
        LocalDateTime localDateTime = LocalDateTime.of(i3, j, k, m, n, i1, 0).plusDays(bool);
        l2 = localDateTime.toEpochSecond(ZoneOffset.UTC);
        l2 += Math.multiplyExact(l1 / 10000L, 315569520000L);
      } catch (RuntimeException runtimeException) {
        return param1Int ^ 0xFFFFFFFF;
      } 
      int i4 = i;
      i4 = param1DateTimeParseContext.setParsedField(ChronoField.INSTANT_SECONDS, l2, param1Int, i4);
      return param1DateTimeParseContext.setParsedField(ChronoField.NANO_OF_SECOND, i2, param1Int, i4);
    }
    
    public String toString() { return "Instant()"; }
  }
  
  static final class LocalizedOffsetIdPrinterParser implements DateTimePrinterParser {
    private final TextStyle style;
    
    LocalizedOffsetIdPrinterParser(TextStyle param1TextStyle) { this.style = param1TextStyle; }
    
    private static StringBuilder appendHMS(StringBuilder param1StringBuilder, int param1Int) { return param1StringBuilder.append((char)(param1Int / 10 + 48)).append((char)(param1Int % 10 + 48)); }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Long long = param1DateTimePrintContext.getValue(ChronoField.OFFSET_SECONDS);
      if (long == null)
        return false; 
      String str = "GMT";
      if (str != null)
        param1StringBuilder.append(str); 
      int i = Math.toIntExact(long.longValue());
      if (i != 0) {
        int j = Math.abs(i / 3600 % 100);
        int k = Math.abs(i / 60 % 60);
        int m = Math.abs(i % 60);
        param1StringBuilder.append((i < 0) ? "-" : "+");
        if (this.style == TextStyle.FULL) {
          appendHMS(param1StringBuilder, j);
          param1StringBuilder.append(':');
          appendHMS(param1StringBuilder, k);
          if (m != 0) {
            param1StringBuilder.append(':');
            appendHMS(param1StringBuilder, m);
          } 
        } else {
          if (j >= 10)
            param1StringBuilder.append((char)(j / 10 + 48)); 
          param1StringBuilder.append((char)(j % 10 + 48));
          if (k != 0 || m != 0) {
            param1StringBuilder.append(':');
            appendHMS(param1StringBuilder, k);
            if (m != 0) {
              param1StringBuilder.append(':');
              appendHMS(param1StringBuilder, m);
            } 
          } 
        } 
      } 
      return true;
    }
    
    int getDigit(CharSequence param1CharSequence, int param1Int) {
      char c = param1CharSequence.charAt(param1Int);
      return (c < '0' || c > '9') ? -1 : (c - '0');
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1Int;
      int j = i + param1CharSequence.length();
      String str = "GMT";
      if (str != null) {
        if (!param1DateTimeParseContext.subSequenceEquals(param1CharSequence, i, str, 0, str.length()))
          return param1Int ^ 0xFFFFFFFF; 
        i += str.length();
      } 
      byte b = 0;
      if (i == j)
        return param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, 0L, param1Int, i); 
      char c = param1CharSequence.charAt(i);
      if (c == '+') {
        b = 1;
      } else if (c == '-') {
        b = -1;
      } else {
        return param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, 0L, param1Int, i);
      } 
      i++;
      int k = 0;
      int m = 0;
      int n = 0;
      if (this.style == TextStyle.FULL) {
        int i1 = getDigit(param1CharSequence, i++);
        int i2 = getDigit(param1CharSequence, i++);
        if (i1 < 0 || i2 < 0 || param1CharSequence.charAt(i++) != ':')
          return param1Int ^ 0xFFFFFFFF; 
        k = i1 * 10 + i2;
        int i3 = getDigit(param1CharSequence, i++);
        int i4 = getDigit(param1CharSequence, i++);
        if (i3 < 0 || i4 < 0)
          return param1Int ^ 0xFFFFFFFF; 
        m = i3 * 10 + i4;
        if (i + 2 < j && param1CharSequence.charAt(i) == ':') {
          int i5 = getDigit(param1CharSequence, i + 1);
          int i6 = getDigit(param1CharSequence, i + 2);
          if (i5 >= 0 && i6 >= 0) {
            n = i5 * 10 + i6;
            i += 3;
          } 
        } 
      } else {
        k = getDigit(param1CharSequence, i++);
        if (k < 0)
          return param1Int ^ 0xFFFFFFFF; 
        if (i < j) {
          int i1 = getDigit(param1CharSequence, i);
          if (i1 >= 0) {
            k = k * 10 + i1;
            i++;
          } 
          if (i + 2 < j && param1CharSequence.charAt(i) == ':' && i + 2 < j && param1CharSequence.charAt(i) == ':') {
            int i2 = getDigit(param1CharSequence, i + 1);
            int i3 = getDigit(param1CharSequence, i + 2);
            if (i2 >= 0 && i3 >= 0) {
              m = i2 * 10 + i3;
              i += 3;
              if (i + 2 < j && param1CharSequence.charAt(i) == ':') {
                int i4 = getDigit(param1CharSequence, i + 1);
                int i5 = getDigit(param1CharSequence, i + 2);
                if (i4 >= 0 && i5 >= 0) {
                  n = i4 * 10 + i5;
                  i += 3;
                } 
              } 
            } 
          } 
        } 
      } 
      long l = b * (k * 3600L + m * 60L + n);
      return param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, l, param1Int, i);
    }
    
    public String toString() { return "LocalizedOffset(" + this.style + ")"; }
  }
  
  static final class LocalizedPrinterParser implements DateTimePrinterParser {
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap(16, 0.75F, 2);
    
    private final FormatStyle dateStyle;
    
    private final FormatStyle timeStyle;
    
    LocalizedPrinterParser(FormatStyle param1FormatStyle1, FormatStyle param1FormatStyle2) {
      this.dateStyle = param1FormatStyle1;
      this.timeStyle = param1FormatStyle2;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Chronology chronology = Chronology.from(param1DateTimePrintContext.getTemporal());
      return formatter(param1DateTimePrintContext.getLocale(), chronology).toPrinterParser(false).format(param1DateTimePrintContext, param1StringBuilder);
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      Chronology chronology = param1DateTimeParseContext.getEffectiveChronology();
      return formatter(param1DateTimeParseContext.getLocale(), chronology).toPrinterParser(false).parse(param1DateTimeParseContext, param1CharSequence, param1Int);
    }
    
    private DateTimeFormatter formatter(Locale param1Locale, Chronology param1Chronology) {
      String str = param1Chronology.getId() + '|' + param1Locale.toString() + '|' + this.dateStyle + this.timeStyle;
      DateTimeFormatter dateTimeFormatter = (DateTimeFormatter)FORMATTER_CACHE.get(str);
      if (dateTimeFormatter == null) {
        String str1 = DateTimeFormatterBuilder.getLocalizedDateTimePattern(this.dateStyle, this.timeStyle, param1Chronology, param1Locale);
        dateTimeFormatter = (new DateTimeFormatterBuilder()).appendPattern(str1).toFormatter(param1Locale);
        DateTimeFormatter dateTimeFormatter1 = (DateTimeFormatter)FORMATTER_CACHE.putIfAbsent(str, dateTimeFormatter);
        if (dateTimeFormatter1 != null)
          dateTimeFormatter = dateTimeFormatter1; 
      } 
      return dateTimeFormatter;
    }
    
    public String toString() { return "Localized(" + ((this.dateStyle != null) ? this.dateStyle : "") + "," + ((this.timeStyle != null) ? this.timeStyle : "") + ")"; }
  }
  
  static class NumberPrinterParser implements DateTimePrinterParser {
    static final long[] EXCEED_POINTS = { 
        0L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 
        10000000000L };
    
    final TemporalField field;
    
    final int minWidth;
    
    final int maxWidth;
    
    private final SignStyle signStyle;
    
    final int subsequentWidth;
    
    NumberPrinterParser(TemporalField param1TemporalField, int param1Int1, int param1Int2, SignStyle param1SignStyle) {
      this.field = param1TemporalField;
      this.minWidth = param1Int1;
      this.maxWidth = param1Int2;
      this.signStyle = param1SignStyle;
      this.subsequentWidth = 0;
    }
    
    protected NumberPrinterParser(TemporalField param1TemporalField, int param1Int1, int param1Int2, SignStyle param1SignStyle, int param1Int3) {
      this.field = param1TemporalField;
      this.minWidth = param1Int1;
      this.maxWidth = param1Int2;
      this.signStyle = param1SignStyle;
      this.subsequentWidth = param1Int3;
    }
    
    NumberPrinterParser withFixedWidth() { return (this.subsequentWidth == -1) ? this : new NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, -1); }
    
    NumberPrinterParser withSubsequentWidth(int param1Int) { return new NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, this.subsequentWidth + param1Int); }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Long long = param1DateTimePrintContext.getValue(this.field);
      if (long == null)
        return false; 
      long l = getValue(param1DateTimePrintContext, long.longValue());
      DecimalStyle decimalStyle = param1DateTimePrintContext.getDecimalStyle();
      String str = (l == Float.MIN_VALUE) ? "9223372036854775808" : Long.toString(Math.abs(l));
      if (str.length() > this.maxWidth)
        throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + l + " exceeds the maximum print width of " + this.maxWidth); 
      str = decimalStyle.convertNumberToI18N(str);
      if (l >= 0L) {
        switch (DateTimeFormatterBuilder.null.$SwitchMap$java$time$format$SignStyle[this.signStyle.ordinal()]) {
          case 1:
            if (this.minWidth < 19 && l >= EXCEED_POINTS[this.minWidth])
              param1StringBuilder.append(decimalStyle.getPositiveSign()); 
            break;
          case 2:
            param1StringBuilder.append(decimalStyle.getPositiveSign());
            break;
        } 
      } else {
        switch (DateTimeFormatterBuilder.null.$SwitchMap$java$time$format$SignStyle[this.signStyle.ordinal()]) {
          case 1:
          case 2:
          case 3:
            param1StringBuilder.append(decimalStyle.getNegativeSign());
            break;
          case 4:
            throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + l + " cannot be negative according to the SignStyle");
        } 
      } 
      for (byte b = 0; b < this.minWidth - str.length(); b++)
        param1StringBuilder.append(decimalStyle.getZeroDigit()); 
      param1StringBuilder.append(str);
      return true;
    }
    
    long getValue(DateTimePrintContext param1DateTimePrintContext, long param1Long) { return param1Long; }
    
    boolean isFixedWidth(DateTimeParseContext param1DateTimeParseContext) { return (this.subsequentWidth == -1 || (this.subsequentWidth > 0 && this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE)); }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1CharSequence.length();
      if (param1Int == i)
        return param1Int ^ 0xFFFFFFFF; 
      char c = param1CharSequence.charAt(param1Int);
      boolean bool1 = false;
      boolean bool2 = false;
      if (c == param1DateTimeParseContext.getDecimalStyle().getPositiveSign()) {
        if (!this.signStyle.parse(true, param1DateTimeParseContext.isStrict(), (this.minWidth == this.maxWidth)))
          return param1Int ^ 0xFFFFFFFF; 
        bool2 = true;
        param1Int++;
      } else if (c == param1DateTimeParseContext.getDecimalStyle().getNegativeSign()) {
        if (!this.signStyle.parse(false, param1DateTimeParseContext.isStrict(), (this.minWidth == this.maxWidth)))
          return param1Int ^ 0xFFFFFFFF; 
        bool1 = true;
        param1Int++;
      } else if (this.signStyle == SignStyle.ALWAYS && param1DateTimeParseContext.isStrict()) {
        return param1Int ^ 0xFFFFFFFF;
      } 
      int j = (param1DateTimeParseContext.isStrict() || isFixedWidth(param1DateTimeParseContext)) ? this.minWidth : 1;
      int k = param1Int + j;
      if (k > i)
        return param1Int ^ 0xFFFFFFFF; 
      int m = ((param1DateTimeParseContext.isStrict() || isFixedWidth(param1DateTimeParseContext)) ? this.maxWidth : 9) + Math.max(this.subsequentWidth, 0);
      long l = 0L;
      BigInteger bigInteger = null;
      int n = param1Int;
      int i1 = 0;
      while (i1 < 2) {
        int i2 = Math.min(n + m, i);
        while (n < i2) {
          char c1 = param1CharSequence.charAt(n++);
          int i3 = param1DateTimeParseContext.getDecimalStyle().convertToDigit(c1);
          if (i3 < 0) {
            if (--n < k)
              return param1Int ^ 0xFFFFFFFF; 
            break;
          } 
          if (n - param1Int > 18) {
            if (bigInteger == null)
              bigInteger = BigInteger.valueOf(l); 
            bigInteger = bigInteger.multiply(BigInteger.TEN).add(BigInteger.valueOf(i3));
            continue;
          } 
          l = l * 10L + i3;
        } 
        if (this.subsequentWidth > 0 && !i1) {
          int i3 = n - param1Int;
          m = Math.max(j, i3 - this.subsequentWidth);
          n = param1Int;
          l = 0L;
          bigInteger = null;
          i1++;
        } 
      } 
      if (bool1) {
        if (bigInteger != null) {
          if (bigInteger.equals(BigInteger.ZERO) && param1DateTimeParseContext.isStrict())
            return param1Int - 1 ^ 0xFFFFFFFF; 
          bigInteger = bigInteger.negate();
        } else {
          if (l == 0L && param1DateTimeParseContext.isStrict())
            return param1Int - 1 ^ 0xFFFFFFFF; 
          l = -l;
        } 
      } else if (this.signStyle == SignStyle.EXCEEDS_PAD && param1DateTimeParseContext.isStrict()) {
        i1 = n - param1Int;
        if (bool2) {
          if (i1 <= this.minWidth)
            return param1Int - 1 ^ 0xFFFFFFFF; 
        } else if (i1 > this.minWidth) {
          return param1Int ^ 0xFFFFFFFF;
        } 
      } 
      if (bigInteger != null) {
        if (bigInteger.bitLength() > 63) {
          bigInteger = bigInteger.divide(BigInteger.TEN);
          n--;
        } 
        return setValue(param1DateTimeParseContext, bigInteger.longValue(), param1Int, n);
      } 
      return setValue(param1DateTimeParseContext, l, param1Int, n);
    }
    
    int setValue(DateTimeParseContext param1DateTimeParseContext, long param1Long, int param1Int1, int param1Int2) { return param1DateTimeParseContext.setParsedField(this.field, param1Long, param1Int1, param1Int2); }
    
    public String toString() { return (this.minWidth == 1 && this.maxWidth == 19 && this.signStyle == SignStyle.NORMAL) ? ("Value(" + this.field + ")") : ((this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE) ? ("Value(" + this.field + "," + this.minWidth + ")") : ("Value(" + this.field + "," + this.minWidth + "," + this.maxWidth + "," + this.signStyle + ")")); }
  }
  
  static final class OffsetIdPrinterParser implements DateTimePrinterParser {
    static final String[] PATTERNS = { "+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS" };
    
    static final OffsetIdPrinterParser INSTANCE_ID_Z = new OffsetIdPrinterParser("+HH:MM:ss", "Z");
    
    static final OffsetIdPrinterParser INSTANCE_ID_ZERO = new OffsetIdPrinterParser("+HH:MM:ss", "0");
    
    private final String noOffsetText;
    
    private final int type;
    
    OffsetIdPrinterParser(String param1String1, String param1String2) {
      Objects.requireNonNull(param1String1, "pattern");
      Objects.requireNonNull(param1String2, "noOffsetText");
      this.type = checkPattern(param1String1);
      this.noOffsetText = param1String2;
    }
    
    private int checkPattern(String param1String) {
      for (byte b = 0; b < PATTERNS.length; b++) {
        if (PATTERNS[b].equals(param1String))
          return b; 
      } 
      throw new IllegalArgumentException("Invalid zone offset pattern: " + param1String);
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      Long long = param1DateTimePrintContext.getValue(ChronoField.OFFSET_SECONDS);
      if (long == null)
        return false; 
      int i = Math.toIntExact(long.longValue());
      if (i == 0) {
        param1StringBuilder.append(this.noOffsetText);
      } else {
        int j = Math.abs(i / 3600 % 100);
        int k = Math.abs(i / 60 % 60);
        int m = Math.abs(i % 60);
        int n = param1StringBuilder.length();
        int i1 = j;
        param1StringBuilder.append((i < 0) ? "-" : "+").append((char)(j / 10 + 48)).append((char)(j % 10 + 48));
        if (this.type >= 3 || (this.type >= 1 && k > 0)) {
          param1StringBuilder.append((this.type % 2 == 0) ? ":" : "").append((char)(k / 10 + 48)).append((char)(k % 10 + 48));
          i1 += k;
          if (this.type >= 7 || (this.type >= 5 && m > 0)) {
            param1StringBuilder.append((this.type % 2 == 0) ? ":" : "").append((char)(m / 10 + 48)).append((char)(m % 10 + 48));
            i1 += m;
          } 
        } 
        if (i1 == 0) {
          param1StringBuilder.setLength(n);
          param1StringBuilder.append(this.noOffsetText);
        } 
      } 
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1CharSequence.length();
      int j = this.noOffsetText.length();
      if (j == 0) {
        if (param1Int == i)
          return param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, 0L, param1Int, param1Int); 
      } else {
        if (param1Int == i)
          return param1Int ^ 0xFFFFFFFF; 
        if (param1DateTimeParseContext.subSequenceEquals(param1CharSequence, param1Int, this.noOffsetText, 0, j))
          return param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, 0L, param1Int, param1Int + j); 
      } 
      char c = param1CharSequence.charAt(param1Int);
      if (c == '+' || c == '-') {
        byte b = (c == '-') ? -1 : 1;
        int[] arrayOfInt = new int[4];
        arrayOfInt[0] = param1Int + 1;
        if (!((parseNumber(arrayOfInt, 1, param1CharSequence, true) || parseNumber(arrayOfInt, 2, param1CharSequence, (this.type >= 3)) || parseNumber(arrayOfInt, 3, param1CharSequence, false)) ? 1 : 0)) {
          long l = b * (arrayOfInt[1] * 3600L + arrayOfInt[2] * 60L + arrayOfInt[3]);
          return param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, l, param1Int, arrayOfInt[0]);
        } 
      } 
      return (j == 0) ? param1DateTimeParseContext.setParsedField(ChronoField.OFFSET_SECONDS, 0L, param1Int, param1Int + j) : (param1Int ^ 0xFFFFFFFF);
    }
    
    private boolean parseNumber(int[] param1ArrayOfInt, int param1Int, CharSequence param1CharSequence, boolean param1Boolean) {
      if ((this.type + 3) / 2 < param1Int)
        return false; 
      int i = param1ArrayOfInt[0];
      if (this.type % 2 == 0 && param1Int > 1) {
        if (i + 1 > param1CharSequence.length() || param1CharSequence.charAt(i) != ':')
          return param1Boolean; 
        i++;
      } 
      if (i + 2 > param1CharSequence.length())
        return param1Boolean; 
      char c1 = param1CharSequence.charAt(i++);
      char c2 = param1CharSequence.charAt(i++);
      if (c1 < '0' || c1 > '9' || c2 < '0' || c2 > '9')
        return param1Boolean; 
      char c3 = (c1 - '0') * '\n' + c2 - '0';
      if (c3 < '\000' || c3 > ';')
        return param1Boolean; 
      param1ArrayOfInt[param1Int] = c3;
      param1ArrayOfInt[0] = i;
      return false;
    }
    
    public String toString() {
      String str = this.noOffsetText.replace("'", "''");
      return "Offset(" + PATTERNS[this.type] + ",'" + str + "')";
    }
  }
  
  static final class PadPrinterParserDecorator implements DateTimePrinterParser {
    private final DateTimeFormatterBuilder.DateTimePrinterParser printerParser;
    
    private final int padWidth;
    
    private final char padChar;
    
    PadPrinterParserDecorator(DateTimeFormatterBuilder.DateTimePrinterParser param1DateTimePrinterParser, int param1Int, char param1Char) {
      this.printerParser = param1DateTimePrinterParser;
      this.padWidth = param1Int;
      this.padChar = param1Char;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      int i = param1StringBuilder.length();
      if (!this.printerParser.format(param1DateTimePrintContext, param1StringBuilder))
        return false; 
      int j = param1StringBuilder.length() - i;
      if (j > this.padWidth)
        throw new DateTimeException("Cannot print as output of " + j + " characters exceeds pad width of " + this.padWidth); 
      for (byte b = 0; b < this.padWidth - j; b++)
        param1StringBuilder.insert(i, this.padChar); 
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      boolean bool = param1DateTimeParseContext.isStrict();
      if (param1Int > param1CharSequence.length())
        throw new IndexOutOfBoundsException(); 
      if (param1Int == param1CharSequence.length())
        return param1Int ^ 0xFFFFFFFF; 
      int i = param1Int + this.padWidth;
      if (i > param1CharSequence.length()) {
        if (bool)
          return param1Int ^ 0xFFFFFFFF; 
        i = param1CharSequence.length();
      } 
      int j;
      for (j = param1Int; j < i && param1DateTimeParseContext.charEquals(param1CharSequence.charAt(j), this.padChar); j++);
      param1CharSequence = param1CharSequence.subSequence(0, i);
      int k = this.printerParser.parse(param1DateTimeParseContext, param1CharSequence, j);
      return (k != i && bool) ? (param1Int + j ^ 0xFFFFFFFF) : k;
    }
    
    public String toString() { return "Pad(" + this.printerParser + "," + this.padWidth + ((this.padChar == ' ') ? ")" : (",'" + this.padChar + "')")); }
  }
  
  static class PrefixTree {
    protected String key;
    
    protected String value;
    
    protected char c0;
    
    protected PrefixTree child;
    
    protected PrefixTree sibling;
    
    private PrefixTree(String param1String1, String param1String2, PrefixTree param1PrefixTree) {
      this.key = param1String1;
      this.value = param1String2;
      this.child = param1PrefixTree;
      if (param1String1.length() == 0) {
        this.c0 = Character.MAX_VALUE;
      } else {
        this.c0 = this.key.charAt(0);
      } 
    }
    
    public static PrefixTree newTree(DateTimeParseContext param1DateTimeParseContext) { return param1DateTimeParseContext.isCaseSensitive() ? new PrefixTree("", null, null) : new CI("", null, null, null); }
    
    public static PrefixTree newTree(Set<String> param1Set, DateTimeParseContext param1DateTimeParseContext) {
      PrefixTree prefixTree = newTree(param1DateTimeParseContext);
      for (String str : param1Set)
        prefixTree.add0(str, str); 
      return prefixTree;
    }
    
    public PrefixTree copyTree() {
      PrefixTree prefixTree = new PrefixTree(this.key, this.value, null);
      if (this.child != null)
        prefixTree.child = this.child.copyTree(); 
      if (this.sibling != null)
        prefixTree.sibling = this.sibling.copyTree(); 
      return prefixTree;
    }
    
    public boolean add(String param1String1, String param1String2) { return add0(param1String1, param1String2); }
    
    private boolean add0(String param1String1, String param1String2) {
      param1String1 = toKey(param1String1);
      int i = prefixLength(param1String1);
      if (i == this.key.length()) {
        if (i < param1String1.length()) {
          String str = param1String1.substring(i);
          PrefixTree prefixTree1;
          for (prefixTree1 = this.child; prefixTree1 != null; prefixTree1 = prefixTree1.sibling) {
            if (isEqual(prefixTree1.c0, str.charAt(0)))
              return prefixTree1.add0(str, param1String2); 
          } 
          prefixTree1 = newNode(str, param1String2, null);
          prefixTree1.sibling = this.child;
          this.child = prefixTree1;
          return true;
        } 
        this.value = param1String2;
        return true;
      } 
      PrefixTree prefixTree = newNode(this.key.substring(i), this.value, this.child);
      this.key = param1String1.substring(0, i);
      this.child = prefixTree;
      if (i < param1String1.length()) {
        PrefixTree prefixTree1 = newNode(param1String1.substring(i), param1String2, null);
        this.child.sibling = prefixTree1;
        this.value = null;
      } else {
        this.value = param1String2;
      } 
      return true;
    }
    
    public String match(CharSequence param1CharSequence, int param1Int1, int param1Int2) {
      if (!prefixOf(param1CharSequence, param1Int1, param1Int2))
        return null; 
      if (this.child != null && param1Int1 += this.key.length() != param1Int2) {
        PrefixTree prefixTree = this.child;
        do {
          if (isEqual(prefixTree.c0, param1CharSequence.charAt(param1Int1))) {
            String str = prefixTree.match(param1CharSequence, param1Int1, param1Int2);
            return (str != null) ? str : this.value;
          } 
          prefixTree = prefixTree.sibling;
        } while (prefixTree != null);
      } 
      return this.value;
    }
    
    public String match(CharSequence param1CharSequence, ParsePosition param1ParsePosition) {
      int i = param1ParsePosition.getIndex();
      int j = param1CharSequence.length();
      if (!prefixOf(param1CharSequence, i, j))
        return null; 
      i += this.key.length();
      if (this.child != null && i != j) {
        PrefixTree prefixTree = this.child;
        do {
          if (isEqual(prefixTree.c0, param1CharSequence.charAt(i))) {
            param1ParsePosition.setIndex(i);
            String str = prefixTree.match(param1CharSequence, param1ParsePosition);
            if (str != null)
              return str; 
            break;
          } 
          prefixTree = prefixTree.sibling;
        } while (prefixTree != null);
      } 
      param1ParsePosition.setIndex(i);
      return this.value;
    }
    
    protected String toKey(String param1String) { return param1String; }
    
    protected PrefixTree newNode(String param1String1, String param1String2, PrefixTree param1PrefixTree) { return new PrefixTree(param1String1, param1String2, param1PrefixTree); }
    
    protected boolean isEqual(char param1Char1, char param1Char2) { return (param1Char1 == param1Char2); }
    
    protected boolean prefixOf(CharSequence param1CharSequence, int param1Int1, int param1Int2) {
      if (param1CharSequence instanceof String)
        return ((String)param1CharSequence).startsWith(this.key, param1Int1); 
      int i = this.key.length();
      if (i > param1Int2 - param1Int1)
        return false; 
      byte b = 0;
      while (i-- > 0) {
        if (!isEqual(this.key.charAt(b++), param1CharSequence.charAt(param1Int1++)))
          return false; 
      } 
      return true;
    }
    
    private int prefixLength(String param1String) {
      byte b;
      for (b = 0; b < param1String.length() && b < this.key.length(); b++) {
        if (!isEqual(param1String.charAt(b), this.key.charAt(b)))
          return b; 
      } 
      return b;
    }
    
    private static class CI extends PrefixTree {
      private CI(String param2String1, String param2String2, DateTimeFormatterBuilder.PrefixTree param2PrefixTree) { super(param2String1, param2String2, param2PrefixTree, null); }
      
      protected CI newNode(String param2String1, String param2String2, DateTimeFormatterBuilder.PrefixTree param2PrefixTree) { return new CI(param2String1, param2String2, param2PrefixTree); }
      
      protected boolean isEqual(char param2Char1, char param2Char2) { return DateTimeParseContext.charEqualsIgnoreCase(param2Char1, param2Char2); }
      
      protected boolean prefixOf(CharSequence param2CharSequence, int param2Int1, int param2Int2) {
        int i = this.key.length();
        if (i > param2Int2 - param2Int1)
          return false; 
        byte b = 0;
        while (i-- > 0) {
          if (!isEqual(this.key.charAt(b++), param2CharSequence.charAt(param2Int1++)))
            return false; 
        } 
        return true;
      }
    }
    
    private static class LENIENT extends CI {
      private LENIENT(String param2String1, String param2String2, DateTimeFormatterBuilder.PrefixTree param2PrefixTree) { super(param2String1, param2String2, param2PrefixTree, null); }
      
      protected DateTimeFormatterBuilder.PrefixTree.CI newNode(String param2String1, String param2String2, DateTimeFormatterBuilder.PrefixTree param2PrefixTree) { return new LENIENT(param2String1, param2String2, param2PrefixTree); }
      
      private boolean isLenientChar(char param2Char) { return (param2Char == ' ' || param2Char == '_' || param2Char == '/'); }
      
      protected String toKey(String param2String) {
        for (byte b = 0; b < param2String.length(); b++) {
          if (isLenientChar(param2String.charAt(b))) {
            StringBuilder stringBuilder = new StringBuilder(param2String.length());
            stringBuilder.append(param2String, 0, b);
            while (++b < param2String.length()) {
              if (!isLenientChar(param2String.charAt(b)))
                stringBuilder.append(param2String.charAt(b)); 
              b++;
            } 
            return stringBuilder.toString();
          } 
        } 
        return param2String;
      }
      
      public String match(CharSequence param2CharSequence, ParsePosition param2ParsePosition) {
        int i = param2ParsePosition.getIndex();
        int j = param2CharSequence.length();
        int k = this.key.length();
        byte b = 0;
        while (b < k && i < j) {
          if (isLenientChar(param2CharSequence.charAt(i))) {
            i++;
            continue;
          } 
          if (!isEqual(this.key.charAt(b++), param2CharSequence.charAt(i++)))
            return null; 
        } 
        if (b != k)
          return null; 
        if (this.child != null && i != j) {
          int m;
          for (m = i; m < j && isLenientChar(param2CharSequence.charAt(m)); m++);
          if (m < j) {
            DateTimeFormatterBuilder.PrefixTree prefixTree = this.child;
            do {
              if (isEqual(prefixTree.c0, param2CharSequence.charAt(m))) {
                param2ParsePosition.setIndex(m);
                String str = prefixTree.match(param2CharSequence, param2ParsePosition);
                if (str != null)
                  return str; 
                break;
              } 
              prefixTree = prefixTree.sibling;
            } while (prefixTree != null);
          } 
        } 
        param2ParsePosition.setIndex(i);
        return this.value;
      }
    }
  }
  
  static final class ReducedPrinterParser extends NumberPrinterParser {
    static final LocalDate BASE_DATE = LocalDate.of(2000, 1, 1);
    
    private final int baseValue;
    
    private final ChronoLocalDate baseDate;
    
    ReducedPrinterParser(TemporalField param1TemporalField, int param1Int1, int param1Int2, int param1Int3, ChronoLocalDate param1ChronoLocalDate) {
      this(param1TemporalField, param1Int1, param1Int2, param1Int3, param1ChronoLocalDate, 0);
      if (param1Int1 < 1 || param1Int1 > 10)
        throw new IllegalArgumentException("The minWidth must be from 1 to 10 inclusive but was " + param1Int1); 
      if (param1Int2 < 1 || param1Int2 > 10)
        throw new IllegalArgumentException("The maxWidth must be from 1 to 10 inclusive but was " + param1Int1); 
      if (param1Int2 < param1Int1)
        throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + param1Int2 + " < " + param1Int1); 
      if (param1ChronoLocalDate == null) {
        if (!param1TemporalField.range().isValidValue(param1Int3))
          throw new IllegalArgumentException("The base value must be within the range of the field"); 
        if (param1Int3 + EXCEED_POINTS[param1Int2] > 2147483647L)
          throw new DateTimeException("Unable to add printer-parser as the range exceeds the capacity of an int"); 
      } 
    }
    
    private ReducedPrinterParser(TemporalField param1TemporalField, int param1Int1, int param1Int2, int param1Int3, ChronoLocalDate param1ChronoLocalDate, int param1Int4) {
      super(param1TemporalField, param1Int1, param1Int2, SignStyle.NOT_NEGATIVE, param1Int4);
      this.baseValue = param1Int3;
      this.baseDate = param1ChronoLocalDate;
    }
    
    long getValue(DateTimePrintContext param1DateTimePrintContext, long param1Long) {
      long l = Math.abs(param1Long);
      int i = this.baseValue;
      if (this.baseDate != null) {
        Chronology chronology = Chronology.from(param1DateTimePrintContext.getTemporal());
        i = chronology.date(this.baseDate).get(this.field);
      } 
      return (param1Long >= i && param1Long < i + EXCEED_POINTS[this.minWidth]) ? (l % EXCEED_POINTS[this.minWidth]) : (l % EXCEED_POINTS[this.maxWidth]);
    }
    
    int setValue(DateTimeParseContext param1DateTimeParseContext, long param1Long, int param1Int1, int param1Int2) {
      int i = this.baseValue;
      if (this.baseDate != null) {
        Chronology chronology = param1DateTimeParseContext.getEffectiveChronology();
        i = chronology.date(this.baseDate).get(this.field);
        long l = param1Long;
        param1DateTimeParseContext.addChronoChangedListener(param1Chronology -> setValue(param1DateTimeParseContext, param1Long, param1Int1, param1Int2));
      } 
      int j = param1Int2 - param1Int1;
      if (j == this.minWidth && param1Long >= 0L) {
        long l1 = EXCEED_POINTS[this.minWidth];
        long l2 = i % l1;
        long l3 = i - l2;
        if (i > 0) {
          param1Long = l3 + param1Long;
        } else {
          param1Long = l3 - param1Long;
        } 
        if (param1Long < i)
          param1Long += l1; 
      } 
      return param1DateTimeParseContext.setParsedField(this.field, param1Long, param1Int1, param1Int2);
    }
    
    ReducedPrinterParser withFixedWidth() { return (this.subsequentWidth == -1) ? this : new ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, -1); }
    
    ReducedPrinterParser withSubsequentWidth(int param1Int) { return new ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, this.subsequentWidth + param1Int); }
    
    boolean isFixedWidth(DateTimeParseContext param1DateTimeParseContext) { return !param1DateTimeParseContext.isStrict() ? false : super.isFixedWidth(param1DateTimeParseContext); }
    
    public String toString() { return "ReducedValue(" + this.field + "," + this.minWidth + "," + this.maxWidth + "," + ((this.baseDate != null) ? this.baseDate : Integer.valueOf(this.baseValue)) + ")"; }
  }
  
  enum SettingsParser implements DateTimePrinterParser {
    SENSITIVE, INSENSITIVE, STRICT, LENIENT;
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) { return true; }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      switch (ordinal()) {
        case 0:
          param1DateTimeParseContext.setCaseSensitive(true);
          break;
        case 1:
          param1DateTimeParseContext.setCaseSensitive(false);
          break;
        case 2:
          param1DateTimeParseContext.setStrict(true);
          break;
        case 3:
          param1DateTimeParseContext.setStrict(false);
          break;
      } 
      return param1Int;
    }
    
    public String toString() {
      switch (ordinal()) {
        case 0:
          return "ParseCaseSensitive(true)";
        case 1:
          return "ParseCaseSensitive(false)";
        case 2:
          return "ParseStrict(true)";
        case 3:
          return "ParseStrict(false)";
      } 
      throw new IllegalStateException("Unreachable");
    }
  }
  
  static final class StringLiteralPrinterParser implements DateTimePrinterParser {
    private final String literal;
    
    StringLiteralPrinterParser(String param1String) { this.literal = param1String; }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      param1StringBuilder.append(this.literal);
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1CharSequence.length();
      if (param1Int > i || param1Int < 0)
        throw new IndexOutOfBoundsException(); 
      return !param1DateTimeParseContext.subSequenceEquals(param1CharSequence, param1Int, this.literal, 0, this.literal.length()) ? (param1Int ^ 0xFFFFFFFF) : (param1Int + this.literal.length());
    }
    
    public String toString() {
      String str = this.literal.replace("'", "''");
      return "'" + str + "'";
    }
  }
  
  static final class TextPrinterParser implements DateTimePrinterParser {
    private final TemporalField field;
    
    private final TextStyle textStyle;
    
    private final DateTimeTextProvider provider;
    
    TextPrinterParser(TemporalField param1TemporalField, TextStyle param1TextStyle, DateTimeTextProvider param1DateTimeTextProvider) {
      this.field = param1TemporalField;
      this.textStyle = param1TextStyle;
      this.provider = param1DateTimeTextProvider;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      String str;
      Long long = param1DateTimePrintContext.getValue(this.field);
      if (long == null)
        return false; 
      Chronology chronology = (Chronology)param1DateTimePrintContext.getTemporal().query(TemporalQueries.chronology());
      if (chronology == null || chronology == IsoChronology.INSTANCE) {
        str = this.provider.getText(this.field, long.longValue(), this.textStyle, param1DateTimePrintContext.getLocale());
      } else {
        str = this.provider.getText(chronology, this.field, long.longValue(), this.textStyle, param1DateTimePrintContext.getLocale());
      } 
      if (str == null)
        return numberPrinterParser().format(param1DateTimePrintContext, param1StringBuilder); 
      param1StringBuilder.append(str);
      return true;
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      Iterator iterator;
      int i = param1CharSequence.length();
      if (param1Int < 0 || param1Int > i)
        throw new IndexOutOfBoundsException(); 
      TextStyle textStyle1 = param1DateTimeParseContext.isStrict() ? this.textStyle : null;
      Chronology chronology = param1DateTimeParseContext.getEffectiveChronology();
      if (chronology == null || chronology == IsoChronology.INSTANCE) {
        iterator = this.provider.getTextIterator(this.field, textStyle1, param1DateTimeParseContext.getLocale());
      } else {
        iterator = this.provider.getTextIterator(chronology, this.field, textStyle1, param1DateTimeParseContext.getLocale());
      } 
      if (iterator != null) {
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry)iterator.next();
          String str = (String)entry.getKey();
          if (param1DateTimeParseContext.subSequenceEquals(str, 0, param1CharSequence, param1Int, str.length()))
            return param1DateTimeParseContext.setParsedField(this.field, ((Long)entry.getValue()).longValue(), param1Int, param1Int + str.length()); 
        } 
        if (param1DateTimeParseContext.isStrict())
          return param1Int ^ 0xFFFFFFFF; 
      } 
      return numberPrinterParser().parse(param1DateTimeParseContext, param1CharSequence, param1Int);
    }
    
    private DateTimeFormatterBuilder.NumberPrinterParser numberPrinterParser() {
      if (this.numberPrinterParser == null)
        this.numberPrinterParser = new DateTimeFormatterBuilder.NumberPrinterParser(this.field, 1, 19, SignStyle.NORMAL); 
      return this.numberPrinterParser;
    }
    
    public String toString() { return (this.textStyle == TextStyle.FULL) ? ("Text(" + this.field + ")") : ("Text(" + this.field + "," + this.textStyle + ")"); }
  }
  
  static final class WeekBasedFieldPrinterParser implements DateTimePrinterParser {
    private char chr;
    
    private int count;
    
    WeekBasedFieldPrinterParser(char param1Char, int param1Int) {
      this.chr = param1Char;
      this.count = param1Int;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) { return printerParser(param1DateTimePrintContext.getLocale()).format(param1DateTimePrintContext, param1StringBuilder); }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) { return printerParser(param1DateTimeParseContext.getLocale()).parse(param1DateTimeParseContext, param1CharSequence, param1Int); }
    
    private DateTimeFormatterBuilder.DateTimePrinterParser printerParser(Locale param1Locale) {
      WeekFields weekFields = WeekFields.of(param1Locale);
      TemporalField temporalField = null;
      switch (this.chr) {
        case 'Y':
          temporalField = weekFields.weekBasedYear();
          return (this.count == 2) ? new DateTimeFormatterBuilder.ReducedPrinterParser(temporalField, 2, 2, 0, DateTimeFormatterBuilder.ReducedPrinterParser.BASE_DATE, 0, null) : new DateTimeFormatterBuilder.NumberPrinterParser(temporalField, this.count, 19, (this.count < 4) ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD, -1);
        case 'c':
        case 'e':
          temporalField = weekFields.dayOfWeek();
          break;
        case 'w':
          temporalField = weekFields.weekOfWeekBasedYear();
          break;
        case 'W':
          temporalField = weekFields.weekOfMonth();
          break;
        default:
          throw new IllegalStateException("unreachable");
      } 
      return new DateTimeFormatterBuilder.NumberPrinterParser(temporalField, (this.count == 2) ? 2 : 1, 2, SignStyle.NOT_NEGATIVE);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder(30);
      stringBuilder.append("Localized(");
      if (this.chr == 'Y') {
        if (this.count == 1) {
          stringBuilder.append("WeekBasedYear");
        } else if (this.count == 2) {
          stringBuilder.append("ReducedValue(WeekBasedYear,2,2,2000-01-01)");
        } else {
          stringBuilder.append("WeekBasedYear,").append(this.count).append(",").append(19).append(",").append((this.count < 4) ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD);
        } 
      } else {
        switch (this.chr) {
          case 'c':
          case 'e':
            stringBuilder.append("DayOfWeek");
            break;
          case 'w':
            stringBuilder.append("WeekOfWeekBasedYear");
            break;
          case 'W':
            stringBuilder.append("WeekOfMonth");
            break;
        } 
        stringBuilder.append(",");
        stringBuilder.append(this.count);
      } 
      stringBuilder.append(")");
      return stringBuilder.toString();
    }
  }
  
  static class ZoneIdPrinterParser implements DateTimePrinterParser {
    private final TemporalQuery<ZoneId> query;
    
    private final String description;
    
    ZoneIdPrinterParser(TemporalQuery<ZoneId> param1TemporalQuery, String param1String) {
      this.query = param1TemporalQuery;
      this.description = param1String;
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      ZoneId zoneId = (ZoneId)param1DateTimePrintContext.getValue(this.query);
      if (zoneId == null)
        return false; 
      param1StringBuilder.append(zoneId.getId());
      return true;
    }
    
    protected DateTimeFormatterBuilder.PrefixTree getTree(DateTimeParseContext param1DateTimeParseContext) {
      Set set = ZoneRulesProvider.getAvailableZoneIds();
      int i = set.size();
      Map.Entry entry = param1DateTimeParseContext.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
      if (entry == null || ((Integer)entry.getKey()).intValue() != i)
        synchronized (this) {
          entry = param1DateTimeParseContext.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
          if (entry == null || ((Integer)entry.getKey()).intValue() != i) {
            entry = new AbstractMap.SimpleImmutableEntry(Integer.valueOf(i), DateTimeFormatterBuilder.PrefixTree.newTree(set, param1DateTimeParseContext));
            if (param1DateTimeParseContext.isCaseSensitive()) {
              cachedPrefixTree = entry;
            } else {
              cachedPrefixTreeCI = entry;
            } 
          } 
        }  
      return (DateTimeFormatterBuilder.PrefixTree)entry.getValue();
    }
    
    public int parse(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int) {
      int i = param1CharSequence.length();
      if (param1Int > i)
        throw new IndexOutOfBoundsException(); 
      if (param1Int == i)
        return param1Int ^ 0xFFFFFFFF; 
      char c = param1CharSequence.charAt(param1Int);
      if (c == '+' || c == '-')
        return parseOffsetBased(param1DateTimeParseContext, param1CharSequence, param1Int, param1Int, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_Z); 
      if (i >= param1Int + 2) {
        char c1 = param1CharSequence.charAt(param1Int + 1);
        if (param1DateTimeParseContext.charEquals(c, 'U') && param1DateTimeParseContext.charEquals(c1, 'T'))
          return (i >= param1Int + 3 && param1DateTimeParseContext.charEquals(param1CharSequence.charAt(param1Int + 2), 'C')) ? parseOffsetBased(param1DateTimeParseContext, param1CharSequence, param1Int, param1Int + 3, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_ZERO) : parseOffsetBased(param1DateTimeParseContext, param1CharSequence, param1Int, param1Int + 2, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_ZERO); 
        if (param1DateTimeParseContext.charEquals(c, 'G') && i >= param1Int + 3 && param1DateTimeParseContext.charEquals(c1, 'M') && param1DateTimeParseContext.charEquals(param1CharSequence.charAt(param1Int + 2), 'T'))
          return parseOffsetBased(param1DateTimeParseContext, param1CharSequence, param1Int, param1Int + 3, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_ZERO); 
      } 
      DateTimeFormatterBuilder.PrefixTree prefixTree = getTree(param1DateTimeParseContext);
      ParsePosition parsePosition = new ParsePosition(param1Int);
      String str = prefixTree.match(param1CharSequence, parsePosition);
      if (str == null) {
        if (param1DateTimeParseContext.charEquals(c, 'Z')) {
          param1DateTimeParseContext.setParsed(ZoneOffset.UTC);
          return param1Int + 1;
        } 
        return param1Int ^ 0xFFFFFFFF;
      } 
      param1DateTimeParseContext.setParsed(ZoneId.of(str));
      return parsePosition.getIndex();
    }
    
    private int parseOffsetBased(DateTimeParseContext param1DateTimeParseContext, CharSequence param1CharSequence, int param1Int1, int param1Int2, DateTimeFormatterBuilder.OffsetIdPrinterParser param1OffsetIdPrinterParser) {
      String str = param1CharSequence.toString().substring(param1Int1, param1Int2).toUpperCase();
      if (param1Int2 >= param1CharSequence.length()) {
        param1DateTimeParseContext.setParsed(ZoneId.of(str));
        return param1Int2;
      } 
      if (param1CharSequence.charAt(param1Int2) == '0' || param1DateTimeParseContext.charEquals(param1CharSequence.charAt(param1Int2), 'Z')) {
        param1DateTimeParseContext.setParsed(ZoneId.of(str));
        return param1Int2;
      } 
      DateTimeParseContext dateTimeParseContext = param1DateTimeParseContext.copy();
      int i = param1OffsetIdPrinterParser.parse(dateTimeParseContext, param1CharSequence, param1Int2);
      try {
        if (i < 0) {
          if (param1OffsetIdPrinterParser == DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_Z)
            return param1Int1 ^ 0xFFFFFFFF; 
          param1DateTimeParseContext.setParsed(ZoneId.of(str));
          return param1Int2;
        } 
        int j = (int)dateTimeParseContext.getParsed(ChronoField.OFFSET_SECONDS).longValue();
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(j);
        param1DateTimeParseContext.setParsed(ZoneId.ofOffset(str, zoneOffset));
        return i;
      } catch (DateTimeException dateTimeException) {
        return param1Int1 ^ 0xFFFFFFFF;
      } 
    }
    
    public String toString() { return this.description; }
  }
  
  static final class ZoneTextPrinterParser extends ZoneIdPrinterParser {
    private final TextStyle textStyle;
    
    private Set<String> preferredZones;
    
    private static final int STD = 0;
    
    private static final int DST = 1;
    
    private static final int GENERIC = 2;
    
    private static final Map<String, SoftReference<Map<Locale, String[]>>> cache = new ConcurrentHashMap();
    
    private final Map<Locale, Map.Entry<Integer, SoftReference<DateTimeFormatterBuilder.PrefixTree>>> cachedTree = new HashMap();
    
    private final Map<Locale, Map.Entry<Integer, SoftReference<DateTimeFormatterBuilder.PrefixTree>>> cachedTreeCI = new HashMap();
    
    ZoneTextPrinterParser(TextStyle param1TextStyle, Set<ZoneId> param1Set) {
      super(TemporalQueries.zone(), "ZoneText(" + param1TextStyle + ")");
      this.textStyle = (TextStyle)Objects.requireNonNull(param1TextStyle, "textStyle");
      if (param1Set != null && param1Set.size() != 0) {
        this.preferredZones = new HashSet();
        for (ZoneId zoneId : param1Set)
          this.preferredZones.add(zoneId.getId()); 
      } 
    }
    
    private String getDisplayName(String param1String, int param1Int, Locale param1Locale) {
      if (this.textStyle == TextStyle.NARROW)
        return null; 
      SoftReference softReference = (SoftReference)cache.get(param1String);
      Map map = null;
      String[] arrayOfString;
      if (softReference == null || (map = (Map)softReference.get()) == null || (arrayOfString = (String[])map.get(param1Locale)) == null) {
        arrayOfString = TimeZoneNameUtility.retrieveDisplayNames(param1String, param1Locale);
        if (arrayOfString == null)
          return null; 
        arrayOfString = (String[])Arrays.copyOfRange(arrayOfString, 0, 7);
        arrayOfString[5] = TimeZoneNameUtility.retrieveGenericDisplayName(param1String, 1, param1Locale);
        if (arrayOfString[5] == null)
          arrayOfString[5] = arrayOfString[0]; 
        arrayOfString[6] = TimeZoneNameUtility.retrieveGenericDisplayName(param1String, 0, param1Locale);
        if (arrayOfString[6] == null)
          arrayOfString[6] = arrayOfString[0]; 
        if (map == null)
          map = new ConcurrentHashMap(); 
        map.put(param1Locale, arrayOfString);
        cache.put(param1String, new SoftReference(map));
      } 
      switch (param1Int) {
        case 0:
          return arrayOfString[this.textStyle.zoneNameStyleIndex() + 1];
        case 1:
          return arrayOfString[this.textStyle.zoneNameStyleIndex() + 3];
      } 
      return arrayOfString[this.textStyle.zoneNameStyleIndex() + 5];
    }
    
    public boolean format(DateTimePrintContext param1DateTimePrintContext, StringBuilder param1StringBuilder) {
      ZoneId zoneId = (ZoneId)param1DateTimePrintContext.getValue(TemporalQueries.zoneId());
      if (zoneId == null)
        return false; 
      String str = zoneId.getId();
      if (!(zoneId instanceof ZoneOffset)) {
        TemporalAccessor temporalAccessor = param1DateTimePrintContext.getTemporal();
        String str1 = getDisplayName(str, temporalAccessor.isSupported(ChronoField.INSTANT_SECONDS) ? (zoneId.getRules().isDaylightSavings(Instant.from(temporalAccessor)) ? 1 : 0) : 2, param1DateTimePrintContext.getLocale());
        if (str1 != null)
          str = str1; 
      } 
      param1StringBuilder.append(str);
      return true;
    }
    
    protected DateTimeFormatterBuilder.PrefixTree getTree(DateTimeParseContext param1DateTimeParseContext) {
      if (this.textStyle == TextStyle.NARROW)
        return super.getTree(param1DateTimeParseContext); 
      Locale locale = param1DateTimeParseContext.getLocale();
      boolean bool = param1DateTimeParseContext.isCaseSensitive();
      Set set = ZoneRulesProvider.getAvailableZoneIds();
      int i = set.size();
      Map map = bool ? this.cachedTree : this.cachedTreeCI;
      Map.Entry entry = null;
      DateTimeFormatterBuilder.PrefixTree prefixTree = null;
      String[][] arrayOfString = (String[][])null;
      if ((entry = (Map.Entry)map.get(locale)) == null || ((Integer)entry.getKey()).intValue() != i || (prefixTree = (DateTimeFormatterBuilder.PrefixTree)((SoftReference)entry.getValue()).get()) == null) {
        prefixTree = DateTimeFormatterBuilder.PrefixTree.newTree(param1DateTimeParseContext);
        arrayOfString = TimeZoneNameUtility.getZoneStrings(locale);
        for (String[] arrayOfString1 : arrayOfString) {
          String str = arrayOfString1[0];
          if (set.contains(str)) {
            prefixTree.add(str, str);
            str = ZoneName.toZid(str, locale);
            for (boolean bool1 = (this.textStyle == TextStyle.FULL) ? 1 : 2; bool1 < arrayOfString1.length; bool1 += true)
              prefixTree.add(arrayOfString1[bool1], str); 
          } 
        } 
        if (this.preferredZones != null)
          for (String[] arrayOfString1 : arrayOfString) {
            String str = arrayOfString1[0];
            if (this.preferredZones.contains(str) && set.contains(str))
              for (boolean bool1 = (this.textStyle == TextStyle.FULL) ? 1 : 2; bool1 < arrayOfString1.length; bool1 += true)
                prefixTree.add(arrayOfString1[bool1], str);  
          }  
        map.put(locale, new AbstractMap.SimpleImmutableEntry(Integer.valueOf(i), new SoftReference(prefixTree)));
      } 
      return prefixTree;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\DateTimeFormatterBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */