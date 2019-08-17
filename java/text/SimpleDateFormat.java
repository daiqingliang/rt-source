package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfoFile;
import sun.util.locale.provider.LocaleProviderAdapter;

public class SimpleDateFormat extends DateFormat {
  static final long serialVersionUID = 4774881970558875024L;
  
  static final int currentSerialVersion = 1;
  
  private int serialVersionOnStream = 1;
  
  private String pattern;
  
  private NumberFormat originalNumberFormat;
  
  private String originalNumberPattern;
  
  private char minusSign = '-';
  
  private boolean hasFollowingMinusSign = false;
  
  private boolean forceStandaloneForm = false;
  
  private char[] compiledPattern;
  
  private static final int TAG_QUOTE_ASCII_CHAR = 100;
  
  private static final int TAG_QUOTE_CHARS = 101;
  
  private char zeroDigit;
  
  private DateFormatSymbols formatData;
  
  private Date defaultCenturyStart;
  
  private int defaultCenturyStartYear;
  
  private static final int MILLIS_PER_MINUTE = 60000;
  
  private static final String GMT = "GMT";
  
  private static final ConcurrentMap<Locale, NumberFormat> cachedNumberFormatData = new ConcurrentHashMap(3);
  
  private Locale locale;
  
  boolean useDateFormatSymbols;
  
  private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = { 
      0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 
      6, 8, 3, 4, 9, 10, 10, 15, 15, 17, 
      1000, 15, 2 };
  
  private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = { 
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
      10, 11, 12, 13, 14, 15, 16, 17, 17, 1, 
      9, 17, 2 };
  
  private static final DateFormat.Field[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID = { 
      DateFormat.Field.ERA, DateFormat.Field.YEAR, DateFormat.Field.MONTH, DateFormat.Field.DAY_OF_MONTH, DateFormat.Field.HOUR_OF_DAY1, DateFormat.Field.HOUR_OF_DAY0, DateFormat.Field.MINUTE, DateFormat.Field.SECOND, DateFormat.Field.MILLISECOND, DateFormat.Field.DAY_OF_WEEK, 
      DateFormat.Field.DAY_OF_YEAR, DateFormat.Field.DAY_OF_WEEK_IN_MONTH, DateFormat.Field.WEEK_OF_YEAR, DateFormat.Field.WEEK_OF_MONTH, DateFormat.Field.AM_PM, DateFormat.Field.HOUR1, DateFormat.Field.HOUR0, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR, 
      DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.TIME_ZONE, DateFormat.Field.MONTH };
  
  private static final int[] REST_OF_STYLES = { 32769, 2, 32770 };
  
  public SimpleDateFormat() {
    this("", Locale.getDefault(Locale.Category.FORMAT));
    applyPatternImpl(LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(this.locale).getDateTimePattern(3, 3, this.calendar));
  }
  
  public SimpleDateFormat(String paramString) { this(paramString, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public SimpleDateFormat(String paramString, Locale paramLocale) {
    if (paramString == null || paramLocale == null)
      throw new NullPointerException(); 
    initializeCalendar(paramLocale);
    this.pattern = paramString;
    this.formatData = DateFormatSymbols.getInstanceRef(paramLocale);
    this.locale = paramLocale;
    initialize(paramLocale);
  }
  
  public SimpleDateFormat(String paramString, DateFormatSymbols paramDateFormatSymbols) {
    if (paramString == null || paramDateFormatSymbols == null)
      throw new NullPointerException(); 
    this.pattern = paramString;
    this.formatData = (DateFormatSymbols)paramDateFormatSymbols.clone();
    this.locale = Locale.getDefault(Locale.Category.FORMAT);
    initializeCalendar(this.locale);
    initialize(this.locale);
    this.useDateFormatSymbols = true;
  }
  
  private void initialize(Locale paramLocale) {
    this.compiledPattern = compile(this.pattern);
    this.numberFormat = (NumberFormat)cachedNumberFormatData.get(paramLocale);
    if (this.numberFormat == null) {
      this.numberFormat = NumberFormat.getIntegerInstance(paramLocale);
      this.numberFormat.setGroupingUsed(false);
      cachedNumberFormatData.putIfAbsent(paramLocale, this.numberFormat);
    } 
    this.numberFormat = (NumberFormat)this.numberFormat.clone();
    initializeDefaultCentury();
  }
  
  private void initializeCalendar(Locale paramLocale) {
    if (this.calendar == null) {
      assert paramLocale != null;
      this.calendar = Calendar.getInstance(TimeZone.getDefault(), paramLocale);
    } 
  }
  
  private char[] compile(String paramString) {
    int i = paramString.length();
    boolean bool = false;
    StringBuilder stringBuilder1 = new StringBuilder(i * 2);
    StringBuilder stringBuilder2 = null;
    byte b1 = 0;
    byte b2 = 0;
    int j = -1;
    int k = -1;
    int m;
    for (m = 0; m < i; m++) {
      char c = paramString.charAt(m);
      if (c == '\'') {
        if (m + 1 < i) {
          c = paramString.charAt(m + 1);
          if (c == '\'') {
            m++;
            if (b1) {
              encode(j, b1, stringBuilder1);
              b2++;
              k = j;
              j = -1;
              b1 = 0;
            } 
            if (bool) {
              stringBuilder2.append(c);
            } else {
              stringBuilder1.append((char)(0x6400 | c));
            } 
            continue;
          } 
        } 
        if (!bool) {
          if (b1 != 0) {
            encode(j, b1, stringBuilder1);
            b2++;
            k = j;
            j = -1;
            b1 = 0;
          } 
          if (stringBuilder2 == null) {
            stringBuilder2 = new StringBuilder(i);
          } else {
            stringBuilder2.setLength(0);
          } 
          bool = true;
        } else {
          int n = stringBuilder2.length();
          if (n == 1) {
            char c1 = stringBuilder2.charAt(0);
            if (c1 < '') {
              stringBuilder1.append((char)(0x6400 | c1));
            } else {
              stringBuilder1.append('攁');
              stringBuilder1.append(c1);
            } 
          } else {
            encode(101, n, stringBuilder1);
            stringBuilder1.append(stringBuilder2);
          } 
          bool = false;
        } 
        continue;
      } 
      if (bool) {
        stringBuilder2.append(c);
      } else if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
        if (b1 != 0) {
          encode(j, b1, stringBuilder1);
          b2++;
          k = j;
          j = -1;
          b1 = 0;
        } 
        if (c < '') {
          stringBuilder1.append((char)(0x6400 | c));
        } else {
          byte b;
          for (b = m + 1; b < i; b++) {
            char c1 = paramString.charAt(b);
            if (c1 == '\'' || (c1 >= 'a' && c1 <= 'z') || (c1 >= 'A' && c1 <= 'Z'))
              break; 
          } 
          stringBuilder1.append((char)(0x6500 | b - m));
          while (m < b) {
            stringBuilder1.append(paramString.charAt(m));
            m++;
          } 
          m--;
        } 
      } else {
        int n;
        if ((n = "GyMdkHmsSEDFwWahKzZYuXL".indexOf(c)) == -1)
          throw new IllegalArgumentException("Illegal pattern character '" + c + "'"); 
        if (j == -1 || j == n) {
          j = n;
          b1++;
        } else {
          encode(j, b1, stringBuilder1);
          b2++;
          k = j;
          j = n;
          b1 = 1;
        } 
      } 
      continue;
    } 
    if (bool)
      throw new IllegalArgumentException("Unterminated quote"); 
    if (b1 != 0) {
      encode(j, b1, stringBuilder1);
      b2++;
      k = j;
    } 
    this.forceStandaloneForm = (b2 == 1 && k == 2);
    m = stringBuilder1.length();
    char[] arrayOfChar = new char[m];
    stringBuilder1.getChars(0, m, arrayOfChar, 0);
    return arrayOfChar;
  }
  
  private static void encode(int paramInt1, int paramInt2, StringBuilder paramStringBuilder) {
    if (paramInt1 == 21 && paramInt2 >= 4)
      throw new IllegalArgumentException("invalid ISO 8601 format: length=" + paramInt2); 
    if (paramInt2 < 255) {
      paramStringBuilder.append((char)(paramInt1 << 8 | paramInt2));
    } else {
      paramStringBuilder.append((char)(paramInt1 << 8 | 0xFF));
      paramStringBuilder.append((char)(paramInt2 >>> 16));
      paramStringBuilder.append((char)(paramInt2 & 0xFFFF));
    } 
  }
  
  private void initializeDefaultCentury() {
    this.calendar.setTimeInMillis(System.currentTimeMillis());
    this.calendar.add(1, -80);
    parseAmbiguousDatesAsAfter(this.calendar.getTime());
  }
  
  private void parseAmbiguousDatesAsAfter(Date paramDate) {
    this.defaultCenturyStart = paramDate;
    this.calendar.setTime(paramDate);
    this.defaultCenturyStartYear = this.calendar.get(1);
  }
  
  public void set2DigitYearStart(Date paramDate) { parseAmbiguousDatesAsAfter(new Date(paramDate.getTime())); }
  
  public Date get2DigitYearStart() { return (Date)this.defaultCenturyStart.clone(); }
  
  public StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    paramFieldPosition.beginIndex = paramFieldPosition.endIndex = 0;
    return format(paramDate, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate) {
    this.calendar.setTime(paramDate);
    boolean bool = useDateFormatSymbols();
    char c = Character.MIN_VALUE;
    while (c < this.compiledPattern.length) {
      char c1 = this.compiledPattern[c] >>> '\b';
      char c2 = this.compiledPattern[c++] & 0xFF;
      if (c2 == 'ÿ') {
        c2 = this.compiledPattern[c++] << '\020';
        c2 |= this.compiledPattern[c++];
      } 
      switch (c1) {
        case 'd':
          paramStringBuffer.append((char)c2);
          continue;
        case 'e':
          paramStringBuffer.append(this.compiledPattern, c, c2);
          c += c2;
          continue;
      } 
      subFormat(c1, c2, paramFieldDelegate, paramStringBuffer, bool);
    } 
    return paramStringBuffer;
  }
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject) {
    StringBuffer stringBuffer = new StringBuffer();
    CharacterIteratorFieldDelegate characterIteratorFieldDelegate = new CharacterIteratorFieldDelegate();
    if (paramObject instanceof Date) {
      format((Date)paramObject, stringBuffer, characterIteratorFieldDelegate);
    } else if (paramObject instanceof Number) {
      format(new Date(((Number)paramObject).longValue()), stringBuffer, characterIteratorFieldDelegate);
    } else {
      if (paramObject == null)
        throw new NullPointerException("formatToCharacterIterator must be passed non-null object"); 
      throw new IllegalArgumentException("Cannot format given Object as a Date");
    } 
    return characterIteratorFieldDelegate.getIterator(stringBuffer.toString());
  }
  
  private void subFormat(int paramInt1, int paramInt2, Format.FieldDelegate paramFieldDelegate, StringBuffer paramStringBuffer, boolean paramBoolean) {
    int i1;
    int m;
    int i = Integer.MAX_VALUE;
    String str = null;
    int j = paramStringBuffer.length();
    int k = PATTERN_INDEX_TO_CALENDAR_FIELD[paramInt1];
    if (k == 17) {
      if (this.calendar.isWeekDateSupported()) {
        m = this.calendar.getWeekYear();
      } else {
        paramInt1 = 1;
        k = PATTERN_INDEX_TO_CALENDAR_FIELD[paramInt1];
        m = this.calendar.get(k);
      } 
    } else if (k == 1000) {
      m = CalendarBuilder.toISODayOfWeek(this.calendar.get(7));
    } else {
      m = this.calendar.get(k);
    } 
    byte b = (paramInt2 >= 4) ? 2 : 1;
    if (!paramBoolean && k < 15 && paramInt1 != 22)
      str = this.calendar.getDisplayName(k, b, this.locale); 
    switch (paramInt1) {
      case 0:
        if (paramBoolean) {
          String[] arrayOfString = this.formatData.getEras();
          if (m < arrayOfString.length)
            str = arrayOfString[m]; 
        } 
        if (str == null)
          str = ""; 
        break;
      case 1:
      case 19:
        if (this.calendar instanceof java.util.GregorianCalendar) {
          if (paramInt2 != 2) {
            zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
            break;
          } 
          zeroPaddingNumber(m, 2, 2, paramStringBuffer);
          break;
        } 
        if (str == null)
          zeroPaddingNumber(m, (b == 2) ? 1 : paramInt2, i, paramStringBuffer); 
        break;
      case 2:
        if (paramBoolean) {
          if (paramInt2 >= 4) {
            String[] arrayOfString = this.formatData.getMonths();
            str = arrayOfString[m];
          } else if (paramInt2 == 3) {
            String[] arrayOfString = this.formatData.getShortMonths();
            str = arrayOfString[m];
          } 
        } else if (paramInt2 < 3) {
          str = null;
        } else if (this.forceStandaloneForm) {
          str = this.calendar.getDisplayName(k, b | 0x8000, this.locale);
          if (str == null)
            str = this.calendar.getDisplayName(k, b, this.locale); 
        } 
        if (str == null)
          zeroPaddingNumber(m + 1, paramInt2, i, paramStringBuffer); 
        break;
      case 22:
        assert str == null;
        if (this.locale == null) {
          if (paramInt2 >= 4) {
            String[] arrayOfString = this.formatData.getMonths();
            str = arrayOfString[m];
          } else if (paramInt2 == 3) {
            String[] arrayOfString = this.formatData.getShortMonths();
            str = arrayOfString[m];
          } 
        } else if (paramInt2 >= 3) {
          str = this.calendar.getDisplayName(k, b | 0x8000, this.locale);
        } 
        if (str == null)
          zeroPaddingNumber(m + 1, paramInt2, i, paramStringBuffer); 
        break;
      case 4:
        if (str == null) {
          if (m == 0) {
            zeroPaddingNumber(this.calendar.getMaximum(11) + 1, paramInt2, i, paramStringBuffer);
            break;
          } 
          zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
        } 
        break;
      case 9:
        if (paramBoolean) {
          if (paramInt2 >= 4) {
            String[] arrayOfString1 = this.formatData.getWeekdays();
            str = arrayOfString1[m];
            break;
          } 
          String[] arrayOfString = this.formatData.getShortWeekdays();
          str = arrayOfString[m];
        } 
        break;
      case 14:
        if (paramBoolean) {
          String[] arrayOfString = this.formatData.getAmPmStrings();
          str = arrayOfString[m];
        } 
        break;
      case 15:
        if (str == null) {
          if (m == 0) {
            zeroPaddingNumber(this.calendar.getLeastMaximum(10) + 1, paramInt2, i, paramStringBuffer);
            break;
          } 
          zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
        } 
        break;
      case 17:
        if (str == null) {
          if (this.formatData.locale == null || this.formatData.isZoneStringsSet) {
            int i2 = this.formatData.getZoneIndex(this.calendar.getTimeZone().getID());
            if (i2 == -1) {
              m = this.calendar.get(15) + this.calendar.get(16);
              paramStringBuffer.append(ZoneInfoFile.toCustomID(m));
              break;
            } 
            byte b2 = (this.calendar.get(16) == 0) ? 1 : 3;
            if (paramInt2 < 4)
              b2++; 
            String[][] arrayOfString = this.formatData.getZoneStringsWrapper();
            paramStringBuffer.append(arrayOfString[i2][b2]);
            break;
          } 
          TimeZone timeZone = this.calendar.getTimeZone();
          boolean bool = (this.calendar.get(16) != 0);
          byte b1 = (paramInt2 < 4) ? 0 : 1;
          paramStringBuffer.append(timeZone.getDisplayName(bool, b1, this.formatData.locale));
        } 
        break;
      case 18:
        m = (this.calendar.get(15) + this.calendar.get(16)) / 60000;
        n = 4;
        if (m >= 0) {
          paramStringBuffer.append('+');
        } else {
          n++;
        } 
        i1 = m / 60 * 100 + m % 60;
        CalendarUtils.sprintf0d(paramStringBuffer, i1, n);
        break;
      case 21:
        m = this.calendar.get(15) + this.calendar.get(16);
        if (m == 0) {
          paramStringBuffer.append('Z');
          break;
        } 
        m /= 60000;
        if (m >= 0) {
          paramStringBuffer.append('+');
        } else {
          paramStringBuffer.append('-');
          m = -m;
        } 
        CalendarUtils.sprintf0d(paramStringBuffer, m / 60, 2);
        if (paramInt2 == 1)
          break; 
        if (paramInt2 == 3)
          paramStringBuffer.append(':'); 
        CalendarUtils.sprintf0d(paramStringBuffer, m % 60, 2);
        break;
      default:
        if (str == null)
          zeroPaddingNumber(m, paramInt2, i, paramStringBuffer); 
        break;
    } 
    if (str != null)
      paramStringBuffer.append(str); 
    int n = PATTERN_INDEX_TO_DATE_FORMAT_FIELD[paramInt1];
    DateFormat.Field field = PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID[paramInt1];
    paramFieldDelegate.formatted(n, field, field, j, paramStringBuffer.length(), paramStringBuffer);
  }
  
  private void zeroPaddingNumber(int paramInt1, int paramInt2, int paramInt3, StringBuffer paramStringBuffer) {
    try {
      if (this.zeroDigit == '\000')
        this.zeroDigit = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols().getZeroDigit(); 
      if (paramInt1 >= 0) {
        if (paramInt1 < 100 && paramInt2 >= 1 && paramInt2 <= 2) {
          if (paramInt1 < 10) {
            if (paramInt2 == 2)
              paramStringBuffer.append(this.zeroDigit); 
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1));
          } else {
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1 / 10));
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1 % 10));
          } 
          return;
        } 
        if (paramInt1 >= 1000 && paramInt1 < 10000) {
          if (paramInt2 == 4) {
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1 / 1000));
            paramInt1 %= 1000;
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1 / 100));
            paramInt1 %= 100;
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1 / 10));
            paramStringBuffer.append((char)(this.zeroDigit + paramInt1 % 10));
            return;
          } 
          if (paramInt2 == 2 && paramInt3 == 2) {
            zeroPaddingNumber(paramInt1 % 100, 2, 2, paramStringBuffer);
            return;
          } 
        } 
      } 
    } catch (Exception exception) {}
    this.numberFormat.setMinimumIntegerDigits(paramInt2);
    this.numberFormat.setMaximumIntegerDigits(paramInt3);
    this.numberFormat.format(paramInt1, paramStringBuffer, DontCareFieldPosition.INSTANCE);
  }
  
  public Date parse(String paramString, ParsePosition paramParsePosition) {
    Date date;
    checkNegativeNumberExpression();
    int i = paramParsePosition.index;
    int j = i;
    int k = paramString.length();
    boolean[] arrayOfBoolean = { false };
    CalendarBuilder calendarBuilder = new CalendarBuilder();
    byte b = 0;
    while (b < this.compiledPattern.length) {
      char c1 = this.compiledPattern[b] >>> '\b';
      char c2 = this.compiledPattern[b++] & 0xFF;
      if (c2 == 'ÿ') {
        c2 = this.compiledPattern[b++] << '\020';
        c2 |= this.compiledPattern[b++];
      } 
      switch (c1) {
        case 'd':
          if (i >= k || paramString.charAt(i) != (char)c2) {
            paramParsePosition.index = j;
            paramParsePosition.errorIndex = i;
            return null;
          } 
          i++;
          continue;
        case 'e':
          while (c2-- > '\000') {
            if (i >= k || paramString.charAt(i) != this.compiledPattern[b++]) {
              paramParsePosition.index = j;
              paramParsePosition.errorIndex = i;
              return null;
            } 
            i++;
          } 
          continue;
      } 
      boolean bool1 = false;
      boolean bool2 = false;
      if (b < this.compiledPattern.length) {
        char c = this.compiledPattern[b] >>> '\b';
        if (c != 'd' && c != 'e')
          bool1 = true; 
        if (this.hasFollowingMinusSign && (c == 'd' || c == 'e')) {
          char c3;
          if (c == 'd') {
            c3 = this.compiledPattern[b] & 0xFF;
          } else {
            c3 = this.compiledPattern[b + 1];
          } 
          if (c3 == this.minusSign)
            bool2 = true; 
        } 
      } 
      i = subParse(paramString, i, c1, c2, bool1, arrayOfBoolean, paramParsePosition, bool2, calendarBuilder);
      if (i < 0) {
        paramParsePosition.index = j;
        return null;
      } 
    } 
    paramParsePosition.index = i;
    try {
      date = calendarBuilder.establish(this.calendar).getTime();
      if (arrayOfBoolean[0] && date.before(this.defaultCenturyStart))
        date = calendarBuilder.addYear(100).establish(this.calendar).getTime(); 
    } catch (IllegalArgumentException illegalArgumentException) {
      paramParsePosition.errorIndex = i;
      paramParsePosition.index = j;
      return null;
    } 
    return date;
  }
  
  private int matchString(String paramString, int paramInt1, int paramInt2, String[] paramArrayOfString, CalendarBuilder paramCalendarBuilder) {
    byte b1 = 0;
    int i = paramArrayOfString.length;
    if (paramInt2 == 7)
      b1 = 1; 
    int j = 0;
    byte b2 = -1;
    while (b1 < i) {
      int k = paramArrayOfString[b1].length();
      if (k > j && paramString.regionMatches(true, paramInt1, paramArrayOfString[b1], 0, k)) {
        b2 = b1;
        j = k;
      } 
      b1++;
    } 
    if (b2 >= 0) {
      paramCalendarBuilder.set(paramInt2, b2);
      return paramInt1 + j;
    } 
    return -paramInt1;
  }
  
  private int matchString(String paramString, int paramInt1, int paramInt2, Map<String, Integer> paramMap, CalendarBuilder paramCalendarBuilder) {
    if (paramMap != null) {
      if (paramMap instanceof java.util.SortedMap) {
        for (String str1 : paramMap.keySet()) {
          if (paramString.regionMatches(true, paramInt1, str1, 0, str1.length())) {
            paramCalendarBuilder.set(paramInt2, ((Integer)paramMap.get(str1)).intValue());
            return paramInt1 + str1.length();
          } 
        } 
        return -paramInt1;
      } 
      String str = null;
      for (String str1 : paramMap.keySet()) {
        int i = str1.length();
        if ((str == null || i > str.length()) && paramString.regionMatches(true, paramInt1, str1, 0, i))
          str = str1; 
      } 
      if (str != null) {
        paramCalendarBuilder.set(paramInt2, ((Integer)paramMap.get(str)).intValue());
        return paramInt1 + str.length();
      } 
    } 
    return -paramInt1;
  }
  
  private int matchZoneString(String paramString, int paramInt, String[] paramArrayOfString) {
    for (byte b = 1; b <= 4; b++) {
      String str = paramArrayOfString[b];
      if (paramString.regionMatches(true, paramInt, str, 0, str.length()))
        return b; 
    } 
    return -1;
  }
  
  private boolean matchDSTString(String paramString, int paramInt1, int paramInt2, int paramInt3, String[][] paramArrayOfString) {
    int i = paramInt3 + 2;
    String str = paramArrayOfString[paramInt2][i];
    return paramString.regionMatches(true, paramInt1, str, 0, str.length());
  }
  
  private int subParseZoneString(String paramString, int paramInt, CalendarBuilder paramCalendarBuilder) {
    boolean bool = false;
    TimeZone timeZone1 = getTimeZone();
    int i = this.formatData.getZoneIndex(timeZone1.getID());
    TimeZone timeZone2 = null;
    String[][] arrayOfString = this.formatData.getZoneStringsWrapper();
    String[] arrayOfString1 = null;
    int j = 0;
    arrayOfString1 = arrayOfString[i];
    if (i != -1 && (j = matchZoneString(paramString, paramInt, arrayOfString1)) > 0) {
      if (j <= 2)
        bool = arrayOfString1[j].equalsIgnoreCase(arrayOfString1[j + 2]); 
      timeZone2 = TimeZone.getTimeZone(arrayOfString1[0]);
    } 
    if (timeZone2 == null) {
      i = this.formatData.getZoneIndex(TimeZone.getDefault().getID());
      arrayOfString1 = arrayOfString[i];
      if (i != -1 && (j = matchZoneString(paramString, paramInt, arrayOfString1)) > 0) {
        if (j <= 2)
          bool = arrayOfString1[j].equalsIgnoreCase(arrayOfString1[j + 2]); 
        timeZone2 = TimeZone.getTimeZone(arrayOfString1[0]);
      } 
    } 
    if (timeZone2 == null) {
      int k = arrayOfString.length;
      for (byte b = 0; b < k; b++) {
        arrayOfString1 = arrayOfString[b];
        if ((j = matchZoneString(paramString, paramInt, arrayOfString1)) > 0) {
          if (j <= 2)
            bool = arrayOfString1[j].equalsIgnoreCase(arrayOfString1[j + 2]); 
          timeZone2 = TimeZone.getTimeZone(arrayOfString1[0]);
          break;
        } 
      } 
    } 
    if (timeZone2 != null) {
      if (!timeZone2.equals(timeZone1))
        setTimeZone(timeZone2); 
      int k = (j >= 3) ? timeZone2.getDSTSavings() : 0;
      if (!bool && (j < 3 || k != 0))
        paramCalendarBuilder.clear(15).set(16, k); 
      return paramInt + arrayOfString1[j].length();
    } 
    return -paramInt;
  }
  
  private int subParseNumericZone(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, CalendarBuilder paramCalendarBuilder) {
    int i = paramInt1;
    try {
      char c = paramString.charAt(i++);
      if (isDigit(c)) {
        char c1 = c - '0';
        c = paramString.charAt(i++);
        if (isDigit(c)) {
          c1 = c1 * '\n' + c - '0';
        } else {
          if (paramInt3 > 0 || !paramBoolean)
            return 1 - i; 
          i--;
        } 
        if (c1 <= '\027') {
          char c2 = Character.MIN_VALUE;
          if (paramInt3 != 1) {
            c = paramString.charAt(i++);
            if (paramBoolean) {
              if (c != ':')
                return 1 - i; 
              c = paramString.charAt(i++);
            } 
            if (!isDigit(c))
              return 1 - i; 
            c2 = c - '0';
            c = paramString.charAt(i++);
            if (!isDigit(c))
              return 1 - i; 
            c2 = c2 * '\n' + c - '0';
            if (c2 > ';')
              return 1 - i; 
          } 
          c2 += c1 * '<';
          paramCalendarBuilder.set(15, c2 * '' * paramInt2).set(16, 0);
          return i;
        } 
      } 
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
    return 1 - i;
  }
  
  private boolean isDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  private int subParse(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean[] paramArrayOfBoolean, ParsePosition paramParsePosition, boolean paramBoolean2, CalendarBuilder paramCalendarBuilder) {
    byte b;
    Number number;
    int i = 0;
    ParsePosition parsePosition = new ParsePosition(0);
    parsePosition.index = paramInt1;
    if (paramInt2 == 19 && !this.calendar.isWeekDateSupported())
      paramInt2 = 1; 
    int j = PATTERN_INDEX_TO_CALENDAR_FIELD[paramInt2];
    while (true) {
      if (parsePosition.index >= paramString.length()) {
        paramParsePosition.errorIndex = paramInt1;
        return -1;
      } 
      char c = paramString.charAt(parsePosition.index);
      if (c != ' ' && c != '\t')
        break; 
      parsePosition.index++;
    } 
    int k = parsePosition.index;
    if (paramInt2 == 4 || paramInt2 == 15 || (paramInt2 == 2 && paramInt3 <= 2) || paramInt2 == 1 || paramInt2 == 19) {
      if (paramBoolean1) {
        if (paramInt1 + paramInt3 > paramString.length()) {
          paramParsePosition.errorIndex = parsePosition.index;
          return -1;
        } 
        number = this.numberFormat.parse(paramString.substring(0, paramInt1 + paramInt3), parsePosition);
      } else {
        number = this.numberFormat.parse(paramString, parsePosition);
      } 
      if (number == null) {
        if (paramInt2 != 1 || this.calendar instanceof java.util.GregorianCalendar) {
          paramParsePosition.errorIndex = parsePosition.index;
          return -1;
        } 
      } else {
        i = number.intValue();
        if (paramBoolean2 && i < 0 && ((parsePosition.index < paramString.length() && paramString.charAt(parsePosition.index) != this.minusSign) || (parsePosition.index == paramString.length() && paramString.charAt(parsePosition.index - 1) == this.minusSign))) {
          i = -i;
          parsePosition.index--;
        } 
      } 
    } 
    boolean bool = useDateFormatSymbols();
    switch (paramInt2) {
      case 0:
        if (bool) {
          int m;
          if ((m = matchString(paramString, paramInt1, 0, this.formatData.getEras(), paramCalendarBuilder)) > 0)
            return m; 
        } else {
          Map map = getDisplayNamesMap(j, this.locale);
          int m;
          if ((m = matchString(paramString, paramInt1, j, map, paramCalendarBuilder)) > 0)
            return m; 
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 1:
      case 19:
        if (!(this.calendar instanceof java.util.GregorianCalendar)) {
          byte b1 = (paramInt3 >= 4) ? 2 : 1;
          Map map = this.calendar.getDisplayNames(j, b1, this.locale);
          int m;
          if (map != null && (m = matchString(paramString, paramInt1, j, map, paramCalendarBuilder)) > 0)
            return m; 
          paramCalendarBuilder.set(j, i);
          return parsePosition.index;
        } 
        if (paramInt3 <= 2 && parsePosition.index - k == 2 && Character.isDigit(paramString.charAt(k)) && Character.isDigit(paramString.charAt(k + 1))) {
          int m = this.defaultCenturyStartYear % 100;
          paramArrayOfBoolean[0] = (i == m);
          i += this.defaultCenturyStartYear / 100 * 100 + ((i < m) ? 100 : 0);
        } 
        paramCalendarBuilder.set(j, i);
        return parsePosition.index;
      case 2:
        if (paramInt3 <= 2) {
          paramCalendarBuilder.set(2, i - 1);
          return parsePosition.index;
        } 
        if (bool) {
          int n;
          if ((n = matchString(paramString, paramInt1, 2, this.formatData.getMonths(), paramCalendarBuilder)) > 0)
            return n; 
          int m;
          if ((m = matchString(paramString, paramInt1, 2, this.formatData.getShortMonths(), paramCalendarBuilder)) > 0)
            return m; 
        } else {
          Map map = getDisplayNamesMap(j, this.locale);
          int m;
          if ((m = matchString(paramString, paramInt1, j, map, paramCalendarBuilder)) > 0)
            return m; 
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 4:
        if (isLenient() || (i >= 1 && i <= 24)) {
          if (i == this.calendar.getMaximum(11) + 1)
            i = 0; 
          paramCalendarBuilder.set(11, i);
          return parsePosition.index;
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 9:
        if (bool) {
          int n;
          if ((n = matchString(paramString, paramInt1, 7, this.formatData.getWeekdays(), paramCalendarBuilder)) > 0)
            return n; 
          int m;
          if ((m = matchString(paramString, paramInt1, 7, this.formatData.getShortWeekdays(), paramCalendarBuilder)) > 0)
            return m; 
        } else {
          int[] arrayOfInt = { 2, 1 };
          for (int n : arrayOfInt) {
            Map map = this.calendar.getDisplayNames(j, n, this.locale);
            int m;
            if ((m = matchString(paramString, paramInt1, j, map, paramCalendarBuilder)) > 0)
              return m; 
          } 
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 14:
        if (bool) {
          int m;
          if ((m = matchString(paramString, paramInt1, 9, this.formatData.getAmPmStrings(), paramCalendarBuilder)) > 0)
            return m; 
        } else {
          Map map = getDisplayNamesMap(j, this.locale);
          int m;
          if ((m = matchString(paramString, paramInt1, j, map, paramCalendarBuilder)) > 0)
            return m; 
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 15:
        if (isLenient() || (i >= 1 && i <= 12)) {
          if (i == this.calendar.getLeastMaximum(10) + 1)
            i = 0; 
          paramCalendarBuilder.set(10, i);
          return parsePosition.index;
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 17:
      case 18:
        b = 0;
        try {
          char c = paramString.charAt(parsePosition.index);
          if (c == '+') {
            b = 1;
          } else if (c == '-') {
            b = -1;
          } 
          if (b == 0) {
            if ((c == 'G' || c == 'g') && paramString.length() - paramInt1 >= "GMT".length() && paramString.regionMatches(true, paramInt1, "GMT", 0, "GMT".length())) {
              parsePosition.index = paramInt1 + "GMT".length();
              if (paramString.length() - parsePosition.index > 0) {
                c = paramString.charAt(parsePosition.index);
                if (c == '+') {
                  b = 1;
                } else if (c == '-') {
                  b = -1;
                } 
              } 
              if (b == 0) {
                paramCalendarBuilder.set(15, 0).set(16, 0);
                return parsePosition.index;
              } 
              int m = subParseNumericZone(paramString, ++parsePosition.index, b, 0, true, paramCalendarBuilder);
              if (m > 0)
                return m; 
              parsePosition.index = -m;
            } else {
              int m = subParseZoneString(paramString, parsePosition.index, paramCalendarBuilder);
              if (m > 0)
                return m; 
              parsePosition.index = -m;
            } 
          } else {
            int m = subParseNumericZone(paramString, ++parsePosition.index, b, 0, false, paramCalendarBuilder);
            if (m > 0)
              return m; 
            parsePosition.index = -m;
          } 
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      case 21:
        if (paramString.length() - parsePosition.index > 0) {
          char c = paramString.charAt(parsePosition.index);
          if (c == 'Z') {
            paramCalendarBuilder.set(15, 0).set(16, 0);
            return ++parsePosition.index;
          } 
          if (c == '+') {
            b = 1;
          } else if (c == '-') {
            b = -1;
          } else {
            paramParsePosition.errorIndex = ++parsePosition.index;
            return -1;
          } 
          int m = subParseNumericZone(paramString, ++parsePosition.index, b, paramInt3, (paramInt3 == 3), paramCalendarBuilder);
          if (m > 0)
            return m; 
          parsePosition.index = -m;
        } 
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
    } 
    if (paramBoolean1) {
      if (paramInt1 + paramInt3 > paramString.length()) {
        paramParsePosition.errorIndex = parsePosition.index;
        return -1;
      } 
      number = this.numberFormat.parse(paramString.substring(0, paramInt1 + paramInt3), parsePosition);
    } else {
      number = this.numberFormat.parse(paramString, parsePosition);
    } 
    if (number != null) {
      i = number.intValue();
      if (paramBoolean2 && i < 0 && ((parsePosition.index < paramString.length() && paramString.charAt(parsePosition.index) != this.minusSign) || (parsePosition.index == paramString.length() && paramString.charAt(parsePosition.index - 1) == this.minusSign))) {
        i = -i;
        parsePosition.index--;
      } 
      paramCalendarBuilder.set(j, i);
      return parsePosition.index;
    } 
    paramParsePosition.errorIndex = parsePosition.index;
    return -1;
  }
  
  private boolean useDateFormatSymbols() { return (this.useDateFormatSymbols || this.locale == null); }
  
  private String translatePattern(String paramString1, String paramString2, String paramString3) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = false;
    for (byte b = 0; b < paramString1.length(); b++) {
      char c = paramString1.charAt(b);
      if (bool) {
        if (c == '\'')
          bool = false; 
      } else if (c == '\'') {
        bool = true;
      } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
        int i = paramString2.indexOf(c);
        if (i >= 0) {
          if (i < paramString3.length())
            c = paramString3.charAt(i); 
        } else {
          throw new IllegalArgumentException("Illegal pattern  character '" + c + "'");
        } 
      } 
      stringBuilder.append(c);
    } 
    if (bool)
      throw new IllegalArgumentException("Unfinished quote in pattern"); 
    return stringBuilder.toString();
  }
  
  public String toPattern() { return this.pattern; }
  
  public String toLocalizedPattern() { return translatePattern(this.pattern, "GyMdkHmsSEDFwWahKzZYuXL", this.formatData.getLocalPatternChars()); }
  
  public void applyPattern(String paramString) { applyPatternImpl(paramString); }
  
  private void applyPatternImpl(String paramString) {
    this.compiledPattern = compile(paramString);
    this.pattern = paramString;
  }
  
  public void applyLocalizedPattern(String paramString) {
    String str = translatePattern(paramString, this.formatData.getLocalPatternChars(), "GyMdkHmsSEDFwWahKzZYuXL");
    this.compiledPattern = compile(str);
    this.pattern = str;
  }
  
  public DateFormatSymbols getDateFormatSymbols() { return (DateFormatSymbols)this.formatData.clone(); }
  
  public void setDateFormatSymbols(DateFormatSymbols paramDateFormatSymbols) {
    this.formatData = (DateFormatSymbols)paramDateFormatSymbols.clone();
    this.useDateFormatSymbols = true;
  }
  
  public Object clone() {
    SimpleDateFormat simpleDateFormat = (SimpleDateFormat)super.clone();
    simpleDateFormat.formatData = (DateFormatSymbols)this.formatData.clone();
    return simpleDateFormat;
  }
  
  public int hashCode() { return this.pattern.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (!super.equals(paramObject))
      return false; 
    SimpleDateFormat simpleDateFormat = (SimpleDateFormat)paramObject;
    return (this.pattern.equals(simpleDateFormat.pattern) && this.formatData.equals(simpleDateFormat.formatData));
  }
  
  private Map<String, Integer> getDisplayNamesMap(int paramInt, Locale paramLocale) {
    Map map = this.calendar.getDisplayNames(paramInt, 1, paramLocale);
    for (int i : REST_OF_STYLES) {
      Map map1 = this.calendar.getDisplayNames(paramInt, i, paramLocale);
      if (map1 != null)
        map.putAll(map1); 
    } 
    return map;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.compiledPattern = compile(this.pattern);
    } catch (Exception exception) {
      throw new InvalidObjectException("invalid pattern");
    } 
    if (this.serialVersionOnStream < 1) {
      initializeDefaultCentury();
    } else {
      parseAmbiguousDatesAsAfter(this.defaultCenturyStart);
    } 
    this.serialVersionOnStream = 1;
    TimeZone timeZone = getTimeZone();
    if (timeZone instanceof java.util.SimpleTimeZone) {
      String str = timeZone.getID();
      TimeZone timeZone1 = TimeZone.getTimeZone(str);
      if (timeZone1 != null && timeZone1.hasSameRules(timeZone) && timeZone1.getID().equals(str))
        setTimeZone(timeZone1); 
    } 
  }
  
  private void checkNegativeNumberExpression() {
    if (this.numberFormat instanceof DecimalFormat && !this.numberFormat.equals(this.originalNumberFormat)) {
      String str = ((DecimalFormat)this.numberFormat).toPattern();
      if (!str.equals(this.originalNumberPattern)) {
        this.hasFollowingMinusSign = false;
        int i = str.indexOf(';');
        if (i > -1) {
          int j = str.indexOf('-', i);
          if (j > str.lastIndexOf('0') && j > str.lastIndexOf('#')) {
            this.hasFollowingMinusSign = true;
            this.minusSign = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols().getMinusSign();
          } 
        } 
        this.originalNumberPattern = str;
      } 
      this.originalNumberFormat = this.numberFormat;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\SimpleDateFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */