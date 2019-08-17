package java.time.chrono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import sun.util.calendar.BaseCalendar;
import sun.util.logging.PlatformLogger;

public final class HijrahChronology extends AbstractChronology implements Serializable {
  private final String typeId;
  
  private final String calendarType;
  
  private static final long serialVersionUID = 3127340209035924785L;
  
  public static final HijrahChronology INSTANCE;
  
  private int[] hijrahEpochMonthStartDays;
  
  private int minEpochDay;
  
  private int maxEpochDay;
  
  private int hijrahStartEpochMonth;
  
  private int minMonthLength;
  
  private int maxMonthLength;
  
  private int minYearLength;
  
  private int maxYearLength;
  
  private static final Properties calendarProperties;
  
  private static final String PROP_PREFIX = "calendar.hijrah.";
  
  private static final String PROP_TYPE_SUFFIX = ".type";
  
  private static final String KEY_ID = "id";
  
  private static final String KEY_TYPE = "type";
  
  private static final String KEY_VERSION = "version";
  
  private static final String KEY_ISO_START = "iso-start";
  
  private static void registerVariants() {
    for (String str : calendarProperties.stringPropertyNames()) {
      if (str.startsWith("calendar.hijrah.")) {
        String str1 = str.substring("calendar.hijrah.".length());
        if (str1.indexOf('.') >= 0 || str1.equals(INSTANCE.getId()))
          continue; 
        try {
          HijrahChronology hijrahChronology = new HijrahChronology(str1);
          AbstractChronology.registerChrono(hijrahChronology);
        } catch (DateTimeException dateTimeException) {
          PlatformLogger platformLogger = PlatformLogger.getLogger("java.time.chrono");
          platformLogger.severe("Unable to initialize Hijrah calendar: " + str1, dateTimeException);
        } 
      } 
    } 
  }
  
  private HijrahChronology(String paramString) throws DateTimeException {
    if (paramString.isEmpty())
      throw new IllegalArgumentException("calendar id is empty"); 
    String str1 = "calendar.hijrah." + paramString + ".type";
    String str2 = calendarProperties.getProperty(str1);
    if (str2 == null || str2.isEmpty())
      throw new DateTimeException("calendarType is missing or empty for: " + str1); 
    this.typeId = paramString;
    this.calendarType = str2;
  }
  
  private void checkCalendarInit() {
    if (!this.initComplete) {
      loadCalendarData();
      this.initComplete = true;
    } 
  }
  
  public String getId() { return this.typeId; }
  
  public String getCalendarType() { return this.calendarType; }
  
  public HijrahDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3) { return date(prolepticYear(paramEra, paramInt1), paramInt2, paramInt3); }
  
  public HijrahDate date(int paramInt1, int paramInt2, int paramInt3) { return HijrahDate.of(this, paramInt1, paramInt2, paramInt3); }
  
  public HijrahDate dateYearDay(Era paramEra, int paramInt1, int paramInt2) { return dateYearDay(prolepticYear(paramEra, paramInt1), paramInt2); }
  
  public HijrahDate dateYearDay(int paramInt1, int paramInt2) {
    HijrahDate hijrahDate = HijrahDate.of(this, paramInt1, 1, 1);
    if (paramInt2 > hijrahDate.lengthOfYear())
      throw new DateTimeException("Invalid dayOfYear: " + paramInt2); 
    return hijrahDate.plusDays((paramInt2 - 1));
  }
  
  public HijrahDate dateEpochDay(long paramLong) { return HijrahDate.ofEpochDay(this, paramLong); }
  
  public HijrahDate dateNow() { return dateNow(Clock.systemDefaultZone()); }
  
  public HijrahDate dateNow(ZoneId paramZoneId) { return dateNow(Clock.system(paramZoneId)); }
  
  public HijrahDate dateNow(Clock paramClock) { return date(LocalDate.now(paramClock)); }
  
  public HijrahDate date(TemporalAccessor paramTemporalAccessor) { return (paramTemporalAccessor instanceof HijrahDate) ? (HijrahDate)paramTemporalAccessor : HijrahDate.ofEpochDay(this, paramTemporalAccessor.getLong(ChronoField.EPOCH_DAY)); }
  
  public ChronoLocalDateTime<HijrahDate> localDateTime(TemporalAccessor paramTemporalAccessor) { return super.localDateTime(paramTemporalAccessor); }
  
  public ChronoZonedDateTime<HijrahDate> zonedDateTime(TemporalAccessor paramTemporalAccessor) { return super.zonedDateTime(paramTemporalAccessor); }
  
  public ChronoZonedDateTime<HijrahDate> zonedDateTime(Instant paramInstant, ZoneId paramZoneId) { return super.zonedDateTime(paramInstant, paramZoneId); }
  
  public boolean isLeapYear(long paramLong) {
    checkCalendarInit();
    if (paramLong < getMinimumYear() || paramLong > getMaximumYear())
      return false; 
    int i = getYearLength((int)paramLong);
    return (i > 354);
  }
  
  public int prolepticYear(Era paramEra, int paramInt) {
    if (!(paramEra instanceof HijrahEra))
      throw new ClassCastException("Era must be HijrahEra"); 
    return paramInt;
  }
  
  public HijrahEra eraOf(int paramInt) {
    switch (paramInt) {
      case 1:
        return HijrahEra.AH;
    } 
    throw new DateTimeException("invalid Hijrah era");
  }
  
  public List<Era> eras() { return Arrays.asList(HijrahEra.values()); }
  
  public ValueRange range(ChronoField paramChronoField) {
    checkCalendarInit();
    if (paramChronoField instanceof ChronoField) {
      ChronoField chronoField = paramChronoField;
      switch (chronoField) {
        case DAY_OF_MONTH:
          return ValueRange.of(1L, 1L, getMinimumMonthLength(), getMaximumMonthLength());
        case DAY_OF_YEAR:
          return ValueRange.of(1L, getMaximumDayOfYear());
        case ALIGNED_WEEK_OF_MONTH:
          return ValueRange.of(1L, 5L);
        case YEAR:
        case YEAR_OF_ERA:
          return ValueRange.of(getMinimumYear(), getMaximumYear());
        case ERA:
          return ValueRange.of(1L, 1L);
      } 
      return paramChronoField.range();
    } 
    return paramChronoField.range();
  }
  
  public HijrahDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) { return (HijrahDate)super.resolveDate(paramMap, paramResolverStyle); }
  
  int checkValidYear(long paramLong) {
    if (paramLong < getMinimumYear() || paramLong > getMaximumYear())
      throw new DateTimeException("Invalid Hijrah year: " + paramLong); 
    return (int)paramLong;
  }
  
  void checkValidDayOfYear(int paramInt) {
    if (paramInt < 1 || paramInt > getMaximumDayOfYear())
      throw new DateTimeException("Invalid Hijrah day of year: " + paramInt); 
  }
  
  void checkValidMonth(int paramInt) {
    if (paramInt < 1 || paramInt > 12)
      throw new DateTimeException("Invalid Hijrah month: " + paramInt); 
  }
  
  int[] getHijrahDateInfo(int paramInt) {
    checkCalendarInit();
    if (paramInt < this.minEpochDay || paramInt >= this.maxEpochDay)
      throw new DateTimeException("Hijrah date out of range"); 
    int i = epochDayToEpochMonth(paramInt);
    int j = epochMonthToYear(i);
    int k = epochMonthToMonth(i);
    int m = epochMonthToEpochDay(i);
    int n = paramInt - m;
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = j;
    arrayOfInt[1] = k + 1;
    arrayOfInt[2] = n + 1;
    return arrayOfInt;
  }
  
  long getEpochDay(int paramInt1, int paramInt2, int paramInt3) {
    checkCalendarInit();
    checkValidMonth(paramInt2);
    int i = yearToEpochMonth(paramInt1) + paramInt2 - 1;
    if (i < 0 || i >= this.hijrahEpochMonthStartDays.length)
      throw new DateTimeException("Invalid Hijrah date, year: " + paramInt1 + ", month: " + paramInt2); 
    if (paramInt3 < 1 || paramInt3 > getMonthLength(paramInt1, paramInt2))
      throw new DateTimeException("Invalid Hijrah day of month: " + paramInt3); 
    return (epochMonthToEpochDay(i) + paramInt3 - 1);
  }
  
  int getDayOfYear(int paramInt1, int paramInt2) { return yearMonthToDayOfYear(paramInt1, paramInt2 - 1); }
  
  int getMonthLength(int paramInt1, int paramInt2) {
    int i = yearToEpochMonth(paramInt1) + paramInt2 - 1;
    if (i < 0 || i >= this.hijrahEpochMonthStartDays.length)
      throw new DateTimeException("Invalid Hijrah date, year: " + paramInt1 + ", month: " + paramInt2); 
    return epochMonthLength(i);
  }
  
  int getYearLength(int paramInt) { return yearMonthToDayOfYear(paramInt, 12); }
  
  int getMinimumYear() { return epochMonthToYear(0); }
  
  int getMaximumYear() { return epochMonthToYear(this.hijrahEpochMonthStartDays.length - 1) - 1; }
  
  int getMaximumMonthLength() { return this.maxMonthLength; }
  
  int getMinimumMonthLength() { return this.minMonthLength; }
  
  int getMaximumDayOfYear() { return this.maxYearLength; }
  
  int getSmallestMaximumDayOfYear() { return this.minYearLength; }
  
  private int epochDayToEpochMonth(int paramInt) {
    int i = Arrays.binarySearch(this.hijrahEpochMonthStartDays, paramInt);
    if (i < 0)
      i = -i - 2; 
    return i;
  }
  
  private int epochMonthToYear(int paramInt) { return (paramInt + this.hijrahStartEpochMonth) / 12; }
  
  private int yearToEpochMonth(int paramInt) { return paramInt * 12 - this.hijrahStartEpochMonth; }
  
  private int epochMonthToMonth(int paramInt) { return (paramInt + this.hijrahStartEpochMonth) % 12; }
  
  private int epochMonthToEpochDay(int paramInt) { return this.hijrahEpochMonthStartDays[paramInt]; }
  
  private int yearMonthToDayOfYear(int paramInt1, int paramInt2) {
    int i = yearToEpochMonth(paramInt1);
    return epochMonthToEpochDay(i + paramInt2) - epochMonthToEpochDay(i);
  }
  
  private int epochMonthLength(int paramInt) { return this.hijrahEpochMonthStartDays[paramInt + 1] - this.hijrahEpochMonthStartDays[paramInt]; }
  
  private static Properties readConfigProperties(String paramString) throws Exception {
    try {
      return (Properties)AccessController.doPrivileged(() -> {
            String str = System.getProperty("java.home") + File.separator + "lib";
            File file = new File(str, paramString);
            Properties properties = new Properties();
            try (FileInputStream null = new FileInputStream(file)) {
              properties.load(fileInputStream);
            } 
            return properties;
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw privilegedActionException.getException();
    } 
  }
  
  private void loadCalendarData() {
    try {
      String str1 = calendarProperties.getProperty("calendar.hijrah." + this.typeId);
      Objects.requireNonNull(str1, "Resource missing for calendar: calendar.hijrah." + this.typeId);
      Properties properties = readConfigProperties(str1);
      HashMap hashMap = new HashMap();
      int i = Integer.MAX_VALUE;
      int j = Integer.MIN_VALUE;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      int k = 0;
      for (Map.Entry entry : properties.entrySet()) {
        String str = (String)entry.getKey();
        switch (str) {
          case "id":
            str2 = (String)entry.getValue();
            continue;
          case "type":
            str3 = (String)entry.getValue();
            continue;
          case "version":
            str4 = (String)entry.getValue();
            continue;
          case "iso-start":
            arrayOfInt = parseYMD((String)entry.getValue());
            k = (int)LocalDate.of(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2]).toEpochDay();
            continue;
        } 
        try {
          int n = Integer.valueOf(str).intValue();
          int[] arrayOfInt1 = parseMonths((String)entry.getValue());
          hashMap.put(Integer.valueOf(n), arrayOfInt1);
          j = Math.max(j, n);
          i = Math.min(i, n);
        } catch (NumberFormatException arrayOfInt) {
          throw new IllegalArgumentException("bad key: " + str);
        } 
      } 
      if (!getId().equals(str2))
        throw new IllegalArgumentException("Configuration is for a different calendar: " + str2); 
      if (!getCalendarType().equals(str3))
        throw new IllegalArgumentException("Configuration is for a different calendar type: " + str3); 
      if (str4 == null || str4.isEmpty())
        throw new IllegalArgumentException("Configuration does not contain a version"); 
      if (k == 0)
        throw new IllegalArgumentException("Configuration does not contain a ISO start date"); 
      this.hijrahStartEpochMonth = i * 12;
      this.minEpochDay = k;
      this.hijrahEpochMonthStartDays = createEpochMonths(this.minEpochDay, i, j, hashMap);
      this.maxEpochDay = this.hijrahEpochMonthStartDays[this.hijrahEpochMonthStartDays.length - 1];
      for (int m = i; m < j; m++) {
        int n = getYearLength(m);
        this.minYearLength = Math.min(this.minYearLength, n);
        this.maxYearLength = Math.max(this.maxYearLength, n);
      } 
    } catch (Exception exception) {
      PlatformLogger platformLogger = PlatformLogger.getLogger("java.time.chrono");
      platformLogger.severe("Unable to initialize Hijrah calendar proxy: " + this.typeId, exception);
      throw new DateTimeException("Unable to initialize HijrahCalendar: " + this.typeId, exception);
    } 
  }
  
  private int[] createEpochMonths(int paramInt1, int paramInt2, int paramInt3, Map<Integer, int[]> paramMap) {
    int i = (paramInt3 - paramInt2 + 1) * 12 + 1;
    byte b = 0;
    int[] arrayOfInt = new int[i];
    this.minMonthLength = Integer.MAX_VALUE;
    this.maxMonthLength = Integer.MIN_VALUE;
    for (int j = paramInt2; j <= paramInt3; j++) {
      int[] arrayOfInt1 = (int[])paramMap.get(Integer.valueOf(j));
      for (byte b1 = 0; b1 < 12; b1++) {
        int k = arrayOfInt1[b1];
        arrayOfInt[b++] = paramInt1;
        if (k < 29 || k > 32)
          throw new IllegalArgumentException("Invalid month length in year: " + paramInt2); 
        paramInt1 += k;
        this.minMonthLength = Math.min(this.minMonthLength, k);
        this.maxMonthLength = Math.max(this.maxMonthLength, k);
      } 
    } 
    arrayOfInt[b++] = paramInt1;
    if (b != arrayOfInt.length)
      throw new IllegalStateException("Did not fill epochMonths exactly: ndx = " + b + " should be " + arrayOfInt.length); 
    return arrayOfInt;
  }
  
  private int[] parseMonths(String paramString) {
    int[] arrayOfInt = new int[12];
    String[] arrayOfString = paramString.split("\\s");
    if (arrayOfString.length != 12)
      throw new IllegalArgumentException("wrong number of months on line: " + Arrays.toString(arrayOfString) + "; count: " + arrayOfString.length); 
    for (byte b = 0; b < 12; b++) {
      try {
        arrayOfInt[b] = Integer.valueOf(arrayOfString[b]).intValue();
      } catch (NumberFormatException numberFormatException) {
        throw new IllegalArgumentException("bad key: " + arrayOfString[b]);
      } 
    } 
    return arrayOfInt;
  }
  
  private int[] parseYMD(String paramString) {
    paramString = paramString.trim();
    try {
      if (paramString.charAt(4) != '-' || paramString.charAt(7) != '-')
        throw new IllegalArgumentException("date must be yyyy-MM-dd"); 
      int[] arrayOfInt = new int[3];
      arrayOfInt[0] = Integer.valueOf(paramString.substring(0, 4)).intValue();
      arrayOfInt[1] = Integer.valueOf(paramString.substring(5, 7)).intValue();
      arrayOfInt[2] = Integer.valueOf(paramString.substring(8, 10)).intValue();
      return arrayOfInt;
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("date must be yyyy-MM-dd", numberFormatException);
    } 
  }
  
  Object writeReplace() { return super.writeReplace(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  static  {
    try {
      calendarProperties = BaseCalendar.getCalendarProperties();
    } catch (IOException iOException) {
      throw new InternalError("Can't initialize lib/calendars.properties", iOException);
    } 
    try {
      INSTANCE = new HijrahChronology("Hijrah-umalqura");
      AbstractChronology.registerChrono(INSTANCE, "Hijrah");
      AbstractChronology.registerChrono(INSTANCE, "islamic");
    } catch (DateTimeException dateTimeException) {
      PlatformLogger platformLogger = PlatformLogger.getLogger("java.time.chrono");
      platformLogger.severe("Unable to initialize Hijrah calendar: Hijrah-umalqura", dateTimeException);
      throw new RuntimeException("Unable to initialize Hijrah-umalqura calendar", dateTimeException.getCause());
    } 
    registerVariants();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\HijrahChronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */