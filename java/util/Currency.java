package java.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleServiceProvider;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.logging.PlatformLogger;

public final class Currency implements Serializable {
  private static final long serialVersionUID = -158308464356906721L;
  
  private final String currencyCode;
  
  private final int defaultFractionDigits;
  
  private final int numericCode;
  
  private static ConcurrentMap<String, Currency> instances = new ConcurrentHashMap(7);
  
  private static HashSet<Currency> available;
  
  static int formatVersion;
  
  static int dataVersion;
  
  static int[] mainTable;
  
  static long[] scCutOverTimes;
  
  static String[] scOldCurrencies;
  
  static String[] scNewCurrencies;
  
  static int[] scOldCurrenciesDFD;
  
  static int[] scNewCurrenciesDFD;
  
  static int[] scOldCurrenciesNumericCode;
  
  static int[] scNewCurrenciesNumericCode;
  
  static String otherCurrencies;
  
  static int[] otherCurrenciesDFD;
  
  static int[] otherCurrenciesNumericCode;
  
  private static final int MAGIC_NUMBER = 1131770436;
  
  private static final int A_TO_Z = 26;
  
  private static final int INVALID_COUNTRY_ENTRY = 127;
  
  private static final int COUNTRY_WITHOUT_CURRENCY_ENTRY = 512;
  
  private static final int SIMPLE_CASE_COUNTRY_MASK = 0;
  
  private static final int SIMPLE_CASE_COUNTRY_FINAL_CHAR_MASK = 31;
  
  private static final int SIMPLE_CASE_COUNTRY_DEFAULT_DIGITS_MASK = 480;
  
  private static final int SIMPLE_CASE_COUNTRY_DEFAULT_DIGITS_SHIFT = 5;
  
  private static final int SIMPLE_CASE_COUNTRY_MAX_DEFAULT_DIGITS = 9;
  
  private static final int SPECIAL_CASE_COUNTRY_MASK = 512;
  
  private static final int SPECIAL_CASE_COUNTRY_INDEX_MASK = 31;
  
  private static final int SPECIAL_CASE_COUNTRY_INDEX_DELTA = 1;
  
  private static final int COUNTRY_TYPE_MASK = 512;
  
  private static final int NUMERIC_CODE_MASK = 1047552;
  
  private static final int NUMERIC_CODE_SHIFT = 10;
  
  private static final int VALID_FORMAT_VERSION = 2;
  
  private static final int SYMBOL = 0;
  
  private static final int DISPLAYNAME = 1;
  
  private Currency(String paramString, int paramInt1, int paramInt2) {
    this.currencyCode = paramString;
    this.defaultFractionDigits = paramInt1;
    this.numericCode = paramInt2;
  }
  
  public static Currency getInstance(String paramString) { return getInstance(paramString, -2147483648, 0); }
  
  private static Currency getInstance(String paramString, int paramInt1, int paramInt2) {
    Currency currency1 = (Currency)instances.get(paramString);
    if (currency1 != null)
      return currency1; 
    if (paramInt1 == Integer.MIN_VALUE) {
      if (paramString.length() != 3)
        throw new IllegalArgumentException(); 
      char c1 = paramString.charAt(0);
      char c2 = paramString.charAt(1);
      int i = getMainTableEntry(c1, c2);
      if ((i & 0x200) == 0 && i != 127 && paramString.charAt(2) - 'A' == (i & 0x1F)) {
        paramInt1 = (i & 0x1E0) >> 5;
        paramInt2 = (i & 0xFFC00) >> 10;
      } else {
        if (paramString.charAt(2) == '-')
          throw new IllegalArgumentException(); 
        int j = otherCurrencies.indexOf(paramString);
        if (j == -1)
          throw new IllegalArgumentException(); 
        paramInt1 = otherCurrenciesDFD[j / 4];
        paramInt2 = otherCurrenciesNumericCode[j / 4];
      } 
    } 
    Currency currency2 = new Currency(paramString, paramInt1, paramInt2);
    currency1 = (Currency)instances.putIfAbsent(paramString, currency2);
    return (currency1 != null) ? currency1 : currency2;
  }
  
  public static Currency getInstance(Locale paramLocale) {
    String str = paramLocale.getCountry();
    if (str == null)
      throw new NullPointerException(); 
    if (str.length() != 2)
      throw new IllegalArgumentException(); 
    char c1 = str.charAt(0);
    char c2 = str.charAt(1);
    int i = getMainTableEntry(c1, c2);
    if ((i & 0x200) == 0 && i != 127) {
      char c = (char)((i & 0x1F) + 65);
      int k = (i & 0x1E0) >> 5;
      int m = (i & 0xFFC00) >> 10;
      StringBuilder stringBuilder = new StringBuilder(str);
      stringBuilder.append(c);
      return getInstance(stringBuilder.toString(), k, m);
    } 
    if (i == 127)
      throw new IllegalArgumentException(); 
    if (i == 512)
      return null; 
    int j = (i & 0x1F) - 1;
    return (scCutOverTimes[j] == Float.MAX_VALUE || System.currentTimeMillis() < scCutOverTimes[j]) ? getInstance(scOldCurrencies[j], scOldCurrenciesDFD[j], scOldCurrenciesNumericCode[j]) : getInstance(scNewCurrencies[j], scNewCurrenciesDFD[j], scNewCurrenciesNumericCode[j]);
  }
  
  public static Set<Currency> getAvailableCurrencies() {
    synchronized (Currency.class) {
      if (available == null) {
        available = new HashSet(256);
        for (char c = 'A'; c <= 'Z'; c = (char)(c + '\001')) {
          for (char c1 = 'A'; c1 <= 'Z'; c1 = (char)(c1 + '\001')) {
            int i = getMainTableEntry(c, c1);
            if ((i & 0x200) == 0 && i != 127) {
              char c2 = (char)((i & 0x1F) + 65);
              int j = (i & 0x1E0) >> 5;
              int k = (i & 0xFFC00) >> 10;
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append(c);
              stringBuilder.append(c1);
              stringBuilder.append(c2);
              available.add(getInstance(stringBuilder.toString(), j, k));
            } 
          } 
        } 
        StringTokenizer stringTokenizer = new StringTokenizer(otherCurrencies, "-");
        while (stringTokenizer.hasMoreElements())
          available.add(getInstance((String)stringTokenizer.nextElement())); 
      } 
    } 
    return (Set)available.clone();
  }
  
  public String getCurrencyCode() { return this.currencyCode; }
  
  public String getSymbol() { return getSymbol(Locale.getDefault(Locale.Category.DISPLAY)); }
  
  public String getSymbol(Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CurrencyNameProvider.class);
    String str = (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, this.currencyCode, new Object[] { Integer.valueOf(0) });
    return (str != null) ? str : this.currencyCode;
  }
  
  public int getDefaultFractionDigits() { return this.defaultFractionDigits; }
  
  public int getNumericCode() { return this.numericCode; }
  
  public String getDisplayName() { return getDisplayName(Locale.getDefault(Locale.Category.DISPLAY)); }
  
  public String getDisplayName(Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CurrencyNameProvider.class);
    String str = (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, this.currencyCode, new Object[] { Integer.valueOf(1) });
    return (str != null) ? str : this.currencyCode;
  }
  
  public String toString() { return this.currencyCode; }
  
  private Object readResolve() { return getInstance(this.currencyCode); }
  
  private static int getMainTableEntry(char paramChar1, char paramChar2) {
    if (paramChar1 < 'A' || paramChar1 > 'Z' || paramChar2 < 'A' || paramChar2 > 'Z')
      throw new IllegalArgumentException(); 
    return mainTable[(paramChar1 - 'A') * '\032' + paramChar2 - 'A'];
  }
  
  private static void setMainTableEntry(char paramChar1, char paramChar2, int paramInt) {
    if (paramChar1 < 'A' || paramChar1 > 'Z' || paramChar2 < 'A' || paramChar2 > 'Z')
      throw new IllegalArgumentException(); 
    mainTable[(paramChar1 - 'A') * '\032' + paramChar2 - 'A'] = paramInt;
  }
  
  private static int[] readIntArray(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = paramDataInputStream.readInt(); 
    return arrayOfInt;
  }
  
  private static long[] readLongArray(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    long[] arrayOfLong = new long[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfLong[b] = paramDataInputStream.readLong(); 
    return arrayOfLong;
  }
  
  private static String[] readStringArray(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    String[] arrayOfString = new String[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfString[b] = paramDataInputStream.readUTF(); 
    return arrayOfString;
  }
  
  private static void replaceCurrencyData(Pattern paramPattern, String paramString1, String paramString2) {
    if (paramString1.length() != 2) {
      info("currency.properties entry for " + paramString1 + " is ignored because of the invalid country code.", null);
      return;
    } 
    Matcher matcher = paramPattern.matcher(paramString2);
    if (!matcher.find() || (matcher.group(4) == null && countOccurrences(paramString2, ',') >= 3)) {
      info("currency.properties entry for " + paramString1 + " ignored because the value format is not recognized.", null);
      return;
    } 
    try {
      if (matcher.group(4) != null && !isPastCutoverDate(matcher.group(4))) {
        info("currency.properties entry for " + paramString1 + " ignored since cutover date has not passed :" + paramString2, null);
        return;
      } 
    } catch (ParseException parseException) {
      info("currency.properties entry for " + paramString1 + " ignored since exception encountered :" + parseException.getMessage(), null);
      return;
    } 
    String str = matcher.group(1);
    int i = Integer.parseInt(matcher.group(2));
    int j = i << 10;
    int k = Integer.parseInt(matcher.group(3));
    if (k > 9) {
      info("currency.properties entry for " + paramString1 + " ignored since the fraction is more than " + '\t' + ":" + paramString2, null);
      return;
    } 
    byte b;
    for (b = 0; b < scOldCurrencies.length && !scOldCurrencies[b].equals(str); b++);
    if (b == scOldCurrencies.length) {
      j |= k << 5 | str.charAt(2) - 'A';
    } else {
      j |= 0x200 | b + 1;
    } 
    setMainTableEntry(paramString1.charAt(0), paramString1.charAt(1), j);
  }
  
  private static boolean isPastCutoverDate(String paramString) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    simpleDateFormat.setLenient(false);
    long l = simpleDateFormat.parse(paramString.trim()).getTime();
    return (System.currentTimeMillis() > l);
  }
  
  private static int countOccurrences(String paramString, char paramChar) {
    byte b = 0;
    for (char c : paramString.toCharArray()) {
      if (c == paramChar)
        b++; 
    } 
    return b;
  }
  
  private static void info(String paramString, Throwable paramThrowable) {
    PlatformLogger platformLogger = PlatformLogger.getLogger("java.util.Currency");
    if (platformLogger.isLoggable(PlatformLogger.Level.INFO))
      if (paramThrowable != null) {
        platformLogger.info(paramString, paramThrowable);
      } else {
        platformLogger.info(paramString);
      }  
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            String str1 = System.getProperty("java.home");
            try {
              String str = str1 + File.separator + "lib" + File.separator + "currency.data";
              try (DataInputStream null = new DataInputStream(new BufferedInputStream(new FileInputStream(str)))) {
                if (dataInputStream.readInt() != 1131770436)
                  throw new InternalError("Currency data is possibly corrupted"); 
                Currency.formatVersion = dataInputStream.readInt();
                if (Currency.formatVersion != 2)
                  throw new InternalError("Currency data format is incorrect"); 
                Currency.dataVersion = dataInputStream.readInt();
                Currency.mainTable = Currency.readIntArray(dataInputStream, 676);
                i = dataInputStream.readInt();
                Currency.scCutOverTimes = Currency.readLongArray(dataInputStream, i);
                Currency.scOldCurrencies = Currency.readStringArray(dataInputStream, i);
                Currency.scNewCurrencies = Currency.readStringArray(dataInputStream, i);
                Currency.scOldCurrenciesDFD = Currency.readIntArray(dataInputStream, i);
                Currency.scNewCurrenciesDFD = Currency.readIntArray(dataInputStream, i);
                Currency.scOldCurrenciesNumericCode = Currency.readIntArray(dataInputStream, i);
                Currency.scNewCurrenciesNumericCode = Currency.readIntArray(dataInputStream, i);
                int j = dataInputStream.readInt();
                Currency.otherCurrencies = dataInputStream.readUTF();
                Currency.otherCurrenciesDFD = Currency.readIntArray(dataInputStream, j);
                Currency.otherCurrenciesNumericCode = Currency.readIntArray(dataInputStream, j);
              } 
            } catch (IOException iOException) {
              throw new InternalError(iOException);
            } 
            String str2 = System.getProperty("java.util.currency.data");
            if (str2 == null)
              str2 = str1 + File.separator + "lib" + File.separator + "currency.properties"; 
            try {
              File file = new File(str2);
              if (file.exists()) {
                Properties properties = new Properties();
                try (FileReader null = new FileReader(file)) {
                  properties.load(fileReader);
                } 
                Set set = properties.stringPropertyNames();
                Pattern pattern = Pattern.compile("([A-Z]{3})\\s*,\\s*(\\d{3})\\s*,\\s*(\\d+)\\s*,?\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?");
                for (String str : set)
                  Currency.replaceCurrencyData(pattern, str.toUpperCase(Locale.ROOT), properties.getProperty(str).toUpperCase(Locale.ROOT)); 
              } 
            } catch (IOException iOException) {
              Currency.info("currency.properties is ignored because of an IOException", iOException);
            } 
            return null;
          }
        });
  }
  
  private static class CurrencyNameGetter extends Object implements LocaleServiceProviderPool.LocalizedObjectGetter<CurrencyNameProvider, String> {
    private static final CurrencyNameGetter INSTANCE = new CurrencyNameGetter();
    
    public String getObject(CurrencyNameProvider param1CurrencyNameProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      assert param1VarArgs.length == 1;
      int i = ((Integer)param1VarArgs[0]).intValue();
      switch (i) {
        case 0:
          return param1CurrencyNameProvider.getSymbol(param1String, param1Locale);
        case 1:
          return param1CurrencyNameProvider.getDisplayName(param1String, param1Locale);
      } 
      assert false;
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Currency.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */