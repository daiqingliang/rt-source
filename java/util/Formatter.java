package java.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.FormattedFloatingDecimal;

public final class Formatter implements Closeable, Flushable {
  private Appendable a;
  
  private final Locale l;
  
  private IOException lastException;
  
  private final char zero;
  
  private static double scaleUp;
  
  private static final int MAX_FD_CHARS = 30;
  
  private static final String formatSpecifier = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
  
  private static Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");
  
  private static Charset toCharset(String paramString) throws UnsupportedEncodingException {
    Objects.requireNonNull(paramString, "charsetName");
    try {
      return Charset.forName(paramString);
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new UnsupportedEncodingException(paramString);
    } 
  }
  
  private static final Appendable nonNullAppendable(Appendable paramAppendable) { return (paramAppendable == null) ? new StringBuilder() : paramAppendable; }
  
  private Formatter(Locale paramLocale, Appendable paramAppendable) {
    this.a = paramAppendable;
    this.l = paramLocale;
    this.zero = getZero(paramLocale);
  }
  
  private Formatter(Charset paramCharset, Locale paramLocale, File paramFile) throws FileNotFoundException { this(paramLocale, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile), paramCharset))); }
  
  public Formatter() { this(Locale.getDefault(Locale.Category.FORMAT), new StringBuilder()); }
  
  public Formatter(Appendable paramAppendable) { this(Locale.getDefault(Locale.Category.FORMAT), nonNullAppendable(paramAppendable)); }
  
  public Formatter(Locale paramLocale) { this(paramLocale, new StringBuilder()); }
  
  public Formatter(Appendable paramAppendable, Locale paramLocale) { this(paramLocale, nonNullAppendable(paramAppendable)); }
  
  public Formatter(String paramString) throws FileNotFoundException { this(Locale.getDefault(Locale.Category.FORMAT), new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramString)))); }
  
  public Formatter(String paramString1, String paramString2) throws FileNotFoundException, UnsupportedEncodingException { this(paramString1, paramString2, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public Formatter(String paramString1, String paramString2, Locale paramLocale) throws FileNotFoundException, UnsupportedEncodingException { this(toCharset(paramString2), paramLocale, new File(paramString1)); }
  
  public Formatter(File paramFile) throws FileNotFoundException { this(Locale.getDefault(Locale.Category.FORMAT), new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile)))); }
  
  public Formatter(File paramFile, String paramString) throws FileNotFoundException, UnsupportedEncodingException { this(paramFile, paramString, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public Formatter(File paramFile, String paramString, Locale paramLocale) throws FileNotFoundException, UnsupportedEncodingException { this(toCharset(paramString), paramLocale, paramFile); }
  
  public Formatter(PrintStream paramPrintStream) { this(Locale.getDefault(Locale.Category.FORMAT), (Appendable)Objects.requireNonNull(paramPrintStream)); }
  
  public Formatter(OutputStream paramOutputStream) { this(Locale.getDefault(Locale.Category.FORMAT), new BufferedWriter(new OutputStreamWriter(paramOutputStream))); }
  
  public Formatter(OutputStream paramOutputStream, String paramString) throws UnsupportedEncodingException { this(paramOutputStream, paramString, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public Formatter(OutputStream paramOutputStream, String paramString, Locale paramLocale) throws UnsupportedEncodingException { this(paramLocale, new BufferedWriter(new OutputStreamWriter(paramOutputStream, paramString))); }
  
  private static char getZero(Locale paramLocale) {
    if (paramLocale != null && !paramLocale.equals(Locale.US)) {
      DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
      return decimalFormatSymbols.getZeroDigit();
    } 
    return '0';
  }
  
  public Locale locale() {
    ensureOpen();
    return this.l;
  }
  
  public Appendable out() {
    ensureOpen();
    return this.a;
  }
  
  public String toString() {
    ensureOpen();
    return this.a.toString();
  }
  
  public void flush() {
    ensureOpen();
    if (this.a instanceof Flushable)
      try {
        ((Flushable)this.a).flush();
      } catch (IOException iOException) {
        this.lastException = iOException;
      }  
  }
  
  public void close() {
    if (this.a == null)
      return; 
    try {
      if (this.a instanceof Closeable)
        ((Closeable)this.a).close(); 
    } catch (IOException iOException) {
      this.lastException = iOException;
    } finally {
      this.a = null;
    } 
  }
  
  private void ensureOpen() {
    if (this.a == null)
      throw new FormatterClosedException(); 
  }
  
  public IOException ioException() { return this.lastException; }
  
  public Formatter format(String paramString, Object... paramVarArgs) { return format(this.l, paramString, paramVarArgs); }
  
  public Formatter format(Locale paramLocale, String paramString, Object... paramVarArgs) {
    ensureOpen();
    int i = -1;
    byte b = -1;
    FormatString[] arrayOfFormatString = parse(paramString);
    for (byte b1 = 0; b1 < arrayOfFormatString.length; b1++) {
      FormatString formatString = arrayOfFormatString[b1];
      int j = formatString.index();
      try {
        switch (j) {
          case -2:
            formatString.print(null, paramLocale);
            break;
          case -1:
            if (i < 0 || (paramVarArgs != null && i > paramVarArgs.length - 1))
              throw new MissingFormatArgumentException(formatString.toString()); 
            formatString.print((paramVarArgs == null) ? null : paramVarArgs[i], paramLocale);
            break;
          case 0:
            i = ++b;
            if (paramVarArgs != null && b > paramVarArgs.length - 1)
              throw new MissingFormatArgumentException(formatString.toString()); 
            formatString.print((paramVarArgs == null) ? null : paramVarArgs[b], paramLocale);
            break;
          default:
            i = j - 1;
            if (paramVarArgs != null && i > paramVarArgs.length - 1)
              throw new MissingFormatArgumentException(formatString.toString()); 
            formatString.print((paramVarArgs == null) ? null : paramVarArgs[i], paramLocale);
            break;
        } 
      } catch (IOException iOException) {
        this.lastException = iOException;
      } 
    } 
    return this;
  }
  
  private FormatString[] parse(String paramString) {
    ArrayList arrayList = new ArrayList();
    Matcher matcher = fsPattern.matcher(paramString);
    int i = 0;
    int j = paramString.length();
    while (i < j) {
      if (matcher.find(i)) {
        if (matcher.start() != i) {
          checkText(paramString, i, matcher.start());
          arrayList.add(new FixedString(paramString.substring(i, matcher.start())));
        } 
        arrayList.add(new FormatSpecifier(matcher));
        i = matcher.end();
        continue;
      } 
      checkText(paramString, i, j);
      arrayList.add(new FixedString(paramString.substring(i)));
    } 
    return (FormatString[])arrayList.toArray(new FormatString[arrayList.size()]);
  }
  
  private static void checkText(String paramString, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramString.charAt(i) == '%') {
        char c = (i == paramInt2 - 1) ? '%' : paramString.charAt(i + 1);
        throw new UnknownFormatConversionException(String.valueOf(c));
      } 
    } 
  }
  
  public enum BigDecimalLayoutForm {
    SCIENTIFIC, DECIMAL_FLOAT;
  }
  
  private static class Conversion {
    static final char DECIMAL_INTEGER = 'd';
    
    static final char OCTAL_INTEGER = 'o';
    
    static final char HEXADECIMAL_INTEGER = 'x';
    
    static final char HEXADECIMAL_INTEGER_UPPER = 'X';
    
    static final char SCIENTIFIC = 'e';
    
    static final char SCIENTIFIC_UPPER = 'E';
    
    static final char GENERAL = 'g';
    
    static final char GENERAL_UPPER = 'G';
    
    static final char DECIMAL_FLOAT = 'f';
    
    static final char HEXADECIMAL_FLOAT = 'a';
    
    static final char HEXADECIMAL_FLOAT_UPPER = 'A';
    
    static final char CHARACTER = 'c';
    
    static final char CHARACTER_UPPER = 'C';
    
    static final char DATE_TIME = 't';
    
    static final char DATE_TIME_UPPER = 'T';
    
    static final char BOOLEAN = 'b';
    
    static final char BOOLEAN_UPPER = 'B';
    
    static final char STRING = 's';
    
    static final char STRING_UPPER = 'S';
    
    static final char HASHCODE = 'h';
    
    static final char HASHCODE_UPPER = 'H';
    
    static final char LINE_SEPARATOR = 'n';
    
    static final char PERCENT_SIGN = '%';
    
    static boolean isValid(char param1Char) { return (isGeneral(param1Char) || isInteger(param1Char) || isFloat(param1Char) || isText(param1Char) || param1Char == 't' || isCharacter(param1Char)); }
    
    static boolean isGeneral(char param1Char) {
      switch (param1Char) {
        case 'B':
        case 'H':
        case 'S':
        case 'b':
        case 'h':
        case 's':
          return true;
      } 
      return false;
    }
    
    static boolean isCharacter(char param1Char) {
      switch (param1Char) {
        case 'C':
        case 'c':
          return true;
      } 
      return false;
    }
    
    static boolean isInteger(char param1Char) {
      switch (param1Char) {
        case 'X':
        case 'd':
        case 'o':
        case 'x':
          return true;
      } 
      return false;
    }
    
    static boolean isFloat(char param1Char) {
      switch (param1Char) {
        case 'A':
        case 'E':
        case 'G':
        case 'a':
        case 'e':
        case 'f':
        case 'g':
          return true;
      } 
      return false;
    }
    
    static boolean isText(char param1Char) {
      switch (param1Char) {
        case '%':
        case 'n':
          return true;
      } 
      return false;
    }
  }
  
  private static class DateTime {
    static final char HOUR_OF_DAY_0 = 'H';
    
    static final char HOUR_0 = 'I';
    
    static final char HOUR_OF_DAY = 'k';
    
    static final char HOUR = 'l';
    
    static final char MINUTE = 'M';
    
    static final char NANOSECOND = 'N';
    
    static final char MILLISECOND = 'L';
    
    static final char MILLISECOND_SINCE_EPOCH = 'Q';
    
    static final char AM_PM = 'p';
    
    static final char SECONDS_SINCE_EPOCH = 's';
    
    static final char SECOND = 'S';
    
    static final char TIME = 'T';
    
    static final char ZONE_NUMERIC = 'z';
    
    static final char ZONE = 'Z';
    
    static final char NAME_OF_DAY_ABBREV = 'a';
    
    static final char NAME_OF_DAY = 'A';
    
    static final char NAME_OF_MONTH_ABBREV = 'b';
    
    static final char NAME_OF_MONTH = 'B';
    
    static final char CENTURY = 'C';
    
    static final char DAY_OF_MONTH_0 = 'd';
    
    static final char DAY_OF_MONTH = 'e';
    
    static final char NAME_OF_MONTH_ABBREV_X = 'h';
    
    static final char DAY_OF_YEAR = 'j';
    
    static final char MONTH = 'm';
    
    static final char YEAR_2 = 'y';
    
    static final char YEAR_4 = 'Y';
    
    static final char TIME_12_HOUR = 'r';
    
    static final char TIME_24_HOUR = 'R';
    
    static final char DATE_TIME = 'c';
    
    static final char DATE = 'D';
    
    static final char ISO_STANDARD_DATE = 'F';
    
    static boolean isValid(char param1Char) {
      switch (param1Char) {
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'F':
        case 'H':
        case 'I':
        case 'L':
        case 'M':
        case 'N':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'Y':
        case 'Z':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'h':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'p':
        case 'r':
        case 's':
        case 'y':
        case 'z':
          return true;
      } 
      return false;
    }
  }
  
  private class FixedString implements FormatString {
    private String s;
    
    FixedString(String param1String) { this.s = param1String; }
    
    public int index() { return -2; }
    
    public void print(Object param1Object, Locale param1Locale) throws IOException { Formatter.this.a.append(this.s); }
    
    public String toString() { return this.s; }
  }
  
  private static class Flags {
    private int flags;
    
    static final Flags NONE = new Flags(0);
    
    static final Flags LEFT_JUSTIFY = new Flags(1);
    
    static final Flags UPPERCASE = new Flags(2);
    
    static final Flags ALTERNATE = new Flags(4);
    
    static final Flags PLUS = new Flags(8);
    
    static final Flags LEADING_SPACE = new Flags(16);
    
    static final Flags ZERO_PAD = new Flags(32);
    
    static final Flags GROUP = new Flags(64);
    
    static final Flags PARENTHESES = new Flags(128);
    
    static final Flags PREVIOUS = new Flags(256);
    
    private Flags(int param1Int) { this.flags = param1Int; }
    
    public int valueOf() { return this.flags; }
    
    public boolean contains(Flags param1Flags) { return ((this.flags & param1Flags.valueOf()) == param1Flags.valueOf()); }
    
    public Flags dup() { return new Flags(this.flags); }
    
    private Flags add(Flags param1Flags) {
      this.flags |= param1Flags.valueOf();
      return this;
    }
    
    public Flags remove(Flags param1Flags) {
      this.flags &= (param1Flags.valueOf() ^ 0xFFFFFFFF);
      return this;
    }
    
    public static Flags parse(String param1String) {
      char[] arrayOfChar = param1String.toCharArray();
      Flags flags1 = new Flags(0);
      for (byte b = 0; b < arrayOfChar.length; b++) {
        Flags flags2 = parse(arrayOfChar[b]);
        if (flags1.contains(flags2))
          throw new DuplicateFormatFlagsException(flags2.toString()); 
        flags1.add(flags2);
      } 
      return flags1;
    }
    
    private static Flags parse(char param1Char) {
      switch (param1Char) {
        case '-':
          return LEFT_JUSTIFY;
        case '#':
          return ALTERNATE;
        case '+':
          return PLUS;
        case ' ':
          return LEADING_SPACE;
        case '0':
          return ZERO_PAD;
        case ',':
          return GROUP;
        case '(':
          return PARENTHESES;
        case '<':
          return PREVIOUS;
      } 
      throw new UnknownFormatFlagsException(String.valueOf(param1Char));
    }
    
    public static String toString(Flags param1Flags) { return param1Flags.toString(); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (contains(LEFT_JUSTIFY))
        stringBuilder.append('-'); 
      if (contains(UPPERCASE))
        stringBuilder.append('^'); 
      if (contains(ALTERNATE))
        stringBuilder.append('#'); 
      if (contains(PLUS))
        stringBuilder.append('+'); 
      if (contains(LEADING_SPACE))
        stringBuilder.append(' '); 
      if (contains(ZERO_PAD))
        stringBuilder.append('0'); 
      if (contains(GROUP))
        stringBuilder.append(','); 
      if (contains(PARENTHESES))
        stringBuilder.append('('); 
      if (contains(PREVIOUS))
        stringBuilder.append('<'); 
      return stringBuilder.toString();
    }
  }
  
  private class FormatSpecifier implements FormatString {
    private int index = -1;
    
    private Formatter.Flags f = Formatter.Flags.NONE;
    
    private int width;
    
    private int precision;
    
    private boolean dt = false;
    
    private char c;
    
    private int index(String param1String) {
      if (param1String != null) {
        try {
          this.index = Integer.parseInt(param1String.substring(0, param1String.length() - 1));
        } catch (NumberFormatException numberFormatException) {
          assert false;
        } 
      } else {
        this.index = 0;
      } 
      return this.index;
    }
    
    public int index() { return this.index; }
    
    private Formatter.Flags flags(String param1String) {
      this.f = Formatter.Flags.parse(param1String);
      if (this.f.contains(Formatter.Flags.PREVIOUS))
        this.index = -1; 
      return this.f;
    }
    
    Formatter.Flags flags() { return this.f; }
    
    private int width(String param1String) {
      this.width = -1;
      if (param1String != null)
        try {
          this.width = Integer.parseInt(param1String);
          if (this.width < 0)
            throw new IllegalFormatWidthException(this.width); 
        } catch (NumberFormatException numberFormatException) {
          assert false;
        }  
      return this.width;
    }
    
    int width() { return this.width; }
    
    private int precision(String param1String) {
      this.precision = -1;
      if (param1String != null)
        try {
          this.precision = Integer.parseInt(param1String.substring(1));
          if (this.precision < 0)
            throw new IllegalFormatPrecisionException(this.precision); 
        } catch (NumberFormatException numberFormatException) {
          assert false;
        }  
      return this.precision;
    }
    
    int precision() { return this.precision; }
    
    private char conversion(String param1String) {
      this.c = param1String.charAt(0);
      if (!this.dt) {
        if (!Formatter.Conversion.isValid(this.c))
          throw new UnknownFormatConversionException(String.valueOf(this.c)); 
        if (Character.isUpperCase(this.c))
          this.f.add(Formatter.Flags.UPPERCASE); 
        this.c = Character.toLowerCase(this.c);
        if (Formatter.Conversion.isText(this.c))
          this.index = -2; 
      } 
      return this.c;
    }
    
    private char conversion() { return this.c; }
    
    FormatSpecifier(Matcher param1Matcher) {
      byte b = 1;
      index(param1Matcher.group(b++));
      flags(param1Matcher.group(b++));
      width(param1Matcher.group(b++));
      precision(param1Matcher.group(b++));
      String str = param1Matcher.group(b++);
      if (str != null) {
        this.dt = true;
        if (str.equals("T"))
          this.f.add(Formatter.Flags.UPPERCASE); 
      } 
      conversion(param1Matcher.group(b));
      if (this.dt) {
        checkDateTime();
      } else if (Formatter.Conversion.isGeneral(this.c)) {
        checkGeneral();
      } else if (Formatter.Conversion.isCharacter(this.c)) {
        checkCharacter();
      } else if (Formatter.Conversion.isInteger(this.c)) {
        checkInteger();
      } else if (Formatter.Conversion.isFloat(this.c)) {
        checkFloat();
      } else if (Formatter.Conversion.isText(this.c)) {
        checkText();
      } else {
        throw new UnknownFormatConversionException(String.valueOf(this.c));
      } 
    }
    
    public void print(Object param1Object, Locale param1Locale) throws IOException {
      if (this.dt) {
        printDateTime(param1Object, param1Locale);
        return;
      } 
      switch (this.c) {
        case 'd':
        case 'o':
        case 'x':
          printInteger(param1Object, param1Locale);
          return;
        case 'a':
        case 'e':
        case 'f':
        case 'g':
          printFloat(param1Object, param1Locale);
          return;
        case 'C':
        case 'c':
          printCharacter(param1Object);
          return;
        case 'b':
          printBoolean(param1Object);
          return;
        case 's':
          printString(param1Object, param1Locale);
          return;
        case 'h':
          printHashCode(param1Object);
          return;
        case 'n':
          Formatter.this.a.append(System.lineSeparator());
          return;
        case '%':
          Formatter.this.a.append('%');
          return;
      } 
      assert false;
    }
    
    private void printInteger(Object param1Object, Locale param1Locale) throws IOException {
      if (param1Object == null) {
        print("null");
      } else if (param1Object instanceof Byte) {
        print(((Byte)param1Object).byteValue(), param1Locale);
      } else if (param1Object instanceof Short) {
        print(((Short)param1Object).shortValue(), param1Locale);
      } else if (param1Object instanceof Integer) {
        print(((Integer)param1Object).intValue(), param1Locale);
      } else if (param1Object instanceof Long) {
        print(((Long)param1Object).longValue(), param1Locale);
      } else if (param1Object instanceof BigInteger) {
        print((BigInteger)param1Object, param1Locale);
      } else {
        failConversion(this.c, param1Object);
      } 
    }
    
    private void printFloat(Object param1Object, Locale param1Locale) throws IOException {
      if (param1Object == null) {
        print("null");
      } else if (param1Object instanceof Float) {
        print(((Float)param1Object).floatValue(), param1Locale);
      } else if (param1Object instanceof Double) {
        print(((Double)param1Object).doubleValue(), param1Locale);
      } else if (param1Object instanceof BigDecimal) {
        print((BigDecimal)param1Object, param1Locale);
      } else {
        failConversion(this.c, param1Object);
      } 
    }
    
    private void printDateTime(Object param1Object, Locale param1Locale) throws IOException {
      if (param1Object == null) {
        print("null");
        return;
      } 
      Calendar calendar = null;
      if (param1Object instanceof Long) {
        calendar = Calendar.getInstance((param1Locale == null) ? Locale.US : param1Locale);
        calendar.setTimeInMillis(((Long)param1Object).longValue());
      } else if (param1Object instanceof Date) {
        calendar = Calendar.getInstance((param1Locale == null) ? Locale.US : param1Locale);
        calendar.setTime((Date)param1Object);
      } else if (param1Object instanceof Calendar) {
        calendar = (Calendar)((Calendar)param1Object).clone();
        calendar.setLenient(true);
      } else {
        if (param1Object instanceof TemporalAccessor) {
          print((TemporalAccessor)param1Object, this.c, param1Locale);
          return;
        } 
        failConversion(this.c, param1Object);
      } 
      print(calendar, this.c, param1Locale);
    }
    
    private void printCharacter(Object param1Object) throws IOException {
      if (param1Object == null) {
        print("null");
        return;
      } 
      String str = null;
      if (param1Object instanceof Character) {
        str = ((Character)param1Object).toString();
      } else if (param1Object instanceof Byte) {
        byte b = ((Byte)param1Object).byteValue();
        if (Character.isValidCodePoint(b)) {
          str = new String(Character.toChars(b));
        } else {
          throw new IllegalFormatCodePointException(b);
        } 
      } else if (param1Object instanceof Short) {
        short s = ((Short)param1Object).shortValue();
        if (Character.isValidCodePoint(s)) {
          str = new String(Character.toChars(s));
        } else {
          throw new IllegalFormatCodePointException(s);
        } 
      } else if (param1Object instanceof Integer) {
        int i = ((Integer)param1Object).intValue();
        if (Character.isValidCodePoint(i)) {
          str = new String(Character.toChars(i));
        } else {
          throw new IllegalFormatCodePointException(i);
        } 
      } else {
        failConversion(this.c, param1Object);
      } 
      print(str);
    }
    
    private void printString(Object param1Object, Locale param1Locale) throws IOException {
      if (param1Object instanceof Formattable) {
        Formatter formatter = Formatter.this;
        if (formatter.locale() != param1Locale)
          formatter = new Formatter(formatter.out(), param1Locale); 
        ((Formattable)param1Object).formatTo(formatter, this.f.valueOf(), this.width, this.precision);
      } else {
        if (this.f.contains(Formatter.Flags.ALTERNATE))
          failMismatch(Formatter.Flags.ALTERNATE, 's'); 
        if (param1Object == null) {
          print("null");
        } else {
          print(param1Object.toString());
        } 
      } 
    }
    
    private void printBoolean(Object param1Object) throws IOException {
      String str;
      if (param1Object != null) {
        str = (param1Object instanceof Boolean) ? ((Boolean)param1Object).toString() : Boolean.toString(true);
      } else {
        str = Boolean.toString(false);
      } 
      print(str);
    }
    
    private void printHashCode(Object param1Object) throws IOException {
      String str = (param1Object == null) ? "null" : Integer.toHexString(param1Object.hashCode());
      print(str);
    }
    
    private void print(String param1String) throws FileNotFoundException {
      if (this.precision != -1 && this.precision < param1String.length())
        param1String = param1String.substring(0, this.precision); 
      if (this.f.contains(Formatter.Flags.UPPERCASE))
        param1String = param1String.toUpperCase(); 
      Formatter.this.a.append(justify(param1String));
    }
    
    private String justify(String param1String) {
      if (this.width == -1)
        return param1String; 
      StringBuilder stringBuilder = new StringBuilder();
      boolean bool = this.f.contains(Formatter.Flags.LEFT_JUSTIFY);
      int i = this.width - param1String.length();
      if (!bool)
        for (byte b = 0; b < i; b++)
          stringBuilder.append(' ');  
      stringBuilder.append(param1String);
      if (bool)
        for (byte b = 0; b < i; b++)
          stringBuilder.append(' ');  
      return stringBuilder.toString();
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder("%");
      Formatter.Flags flags = this.f.dup().remove(Formatter.Flags.UPPERCASE);
      stringBuilder.append(flags.toString());
      if (this.index > 0)
        stringBuilder.append(this.index).append('$'); 
      if (this.width != -1)
        stringBuilder.append(this.width); 
      if (this.precision != -1)
        stringBuilder.append('.').append(this.precision); 
      if (this.dt)
        stringBuilder.append(this.f.contains(Formatter.Flags.UPPERCASE) ? 84 : 116); 
      stringBuilder.append(this.f.contains(Formatter.Flags.UPPERCASE) ? Character.toUpperCase(this.c) : this.c);
      return stringBuilder.toString();
    }
    
    private void checkGeneral() {
      if ((this.c == 'b' || this.c == 'h') && this.f.contains(Formatter.Flags.ALTERNATE))
        failMismatch(Formatter.Flags.ALTERNATE, this.c); 
      if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY))
        throw new MissingFormatWidthException(toString()); 
      checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES });
    }
    
    private void checkDateTime() {
      if (this.precision != -1)
        throw new IllegalFormatPrecisionException(this.precision); 
      if (!Formatter.DateTime.isValid(this.c))
        throw new UnknownFormatConversionException("t" + this.c); 
      checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE, Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES });
      if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY))
        throw new MissingFormatWidthException(toString()); 
    }
    
    private void checkCharacter() {
      if (this.precision != -1)
        throw new IllegalFormatPrecisionException(this.precision); 
      checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE, Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES });
      if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY))
        throw new MissingFormatWidthException(toString()); 
    }
    
    private void checkInteger() {
      checkNumeric();
      if (this.precision != -1)
        throw new IllegalFormatPrecisionException(this.precision); 
      if (this.c == 'd') {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE });
      } else if (this.c == 'o') {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.GROUP });
      } else {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.GROUP });
      } 
    }
    
    private void checkBadFlags(Formatter.Flags... param1VarArgs) {
      for (byte b = 0; b < param1VarArgs.length; b++) {
        if (this.f.contains(param1VarArgs[b]))
          failMismatch(param1VarArgs[b], this.c); 
      } 
    }
    
    private void checkFloat() {
      checkNumeric();
      if (this.c != 'f')
        if (this.c == 'a') {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PARENTHESES, Formatter.Flags.GROUP });
        } else if (this.c == 'e') {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.GROUP });
        } else if (this.c == 'g') {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE });
        }  
    }
    
    private void checkNumeric() {
      if (this.width != -1 && this.width < 0)
        throw new IllegalFormatWidthException(this.width); 
      if (this.precision != -1 && this.precision < 0)
        throw new IllegalFormatPrecisionException(this.precision); 
      if (this.width == -1 && (this.f.contains(Formatter.Flags.LEFT_JUSTIFY) || this.f.contains(Formatter.Flags.ZERO_PAD)))
        throw new MissingFormatWidthException(toString()); 
      if ((this.f.contains(Formatter.Flags.PLUS) && this.f.contains(Formatter.Flags.LEADING_SPACE)) || (this.f.contains(Formatter.Flags.LEFT_JUSTIFY) && this.f.contains(Formatter.Flags.ZERO_PAD)))
        throw new IllegalFormatFlagsException(this.f.toString()); 
    }
    
    private void checkText() {
      if (this.precision != -1)
        throw new IllegalFormatPrecisionException(this.precision); 
      switch (this.c) {
        case '%':
          if (this.f.valueOf() != Formatter.Flags.LEFT_JUSTIFY.valueOf() && this.f.valueOf() != Formatter.Flags.NONE.valueOf())
            throw new IllegalFormatFlagsException(this.f.toString()); 
          if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY))
            throw new MissingFormatWidthException(toString()); 
          return;
        case 'n':
          if (this.width != -1)
            throw new IllegalFormatWidthException(this.width); 
          if (this.f.valueOf() != Formatter.Flags.NONE.valueOf())
            throw new IllegalFormatFlagsException(this.f.toString()); 
          return;
      } 
      assert false;
    }
    
    private void print(byte param1Byte, Locale param1Locale) throws IOException {
      long l = param1Byte;
      if (param1Byte < 0 && (this.c == 'o' || this.c == 'x')) {
        l += 256L;
        assert l >= 0L : l;
      } 
      print(l, param1Locale);
    }
    
    private void print(short param1Short, Locale param1Locale) throws IOException {
      long l = param1Short;
      if (param1Short < 0 && (this.c == 'o' || this.c == 'x')) {
        l += 65536L;
        assert l >= 0L : l;
      } 
      print(l, param1Locale);
    }
    
    private void print(int param1Int, Locale param1Locale) throws IOException {
      long l = param1Int;
      if (param1Int < 0 && (this.c == 'o' || this.c == 'x')) {
        l += 4294967296L;
        assert l >= 0L : l;
      } 
      print(l, param1Locale);
    }
    
    private void print(long param1Long, Locale param1Locale) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.c == 'd') {
        char[] arrayOfChar;
        boolean bool = (param1Long < 0L);
        if (param1Long < 0L) {
          arrayOfChar = Long.toString(param1Long, 10).substring(1).toCharArray();
        } else {
          arrayOfChar = Long.toString(param1Long, 10).toCharArray();
        } 
        leadingSign(stringBuilder, bool);
        localizedMagnitude(stringBuilder, arrayOfChar, this.f, adjustWidth(this.width, this.f, bool), param1Locale);
        trailingSign(stringBuilder, bool);
      } else if (this.c == 'o') {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PARENTHESES, Formatter.Flags.LEADING_SPACE, Formatter.Flags.PLUS });
        String str = Long.toOctalString(param1Long);
        int i = this.f.contains(Formatter.Flags.ALTERNATE) ? (str.length() + 1) : str.length();
        if (this.f.contains(Formatter.Flags.ALTERNATE))
          stringBuilder.append('0'); 
        if (this.f.contains(Formatter.Flags.ZERO_PAD))
          for (byte b = 0; b < this.width - i; b++)
            stringBuilder.append('0');  
        stringBuilder.append(str);
      } else if (this.c == 'x') {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PARENTHESES, Formatter.Flags.LEADING_SPACE, Formatter.Flags.PLUS });
        String str = Long.toHexString(param1Long);
        int i = this.f.contains(Formatter.Flags.ALTERNATE) ? (str.length() + 2) : str.length();
        if (this.f.contains(Formatter.Flags.ALTERNATE))
          stringBuilder.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "0X" : "0x"); 
        if (this.f.contains(Formatter.Flags.ZERO_PAD))
          for (byte b = 0; b < this.width - i; b++)
            stringBuilder.append('0');  
        if (this.f.contains(Formatter.Flags.UPPERCASE))
          str = str.toUpperCase(); 
        stringBuilder.append(str);
      } 
      Formatter.this.a.append(justify(stringBuilder.toString()));
    }
    
    private StringBuilder leadingSign(StringBuilder param1StringBuilder, boolean param1Boolean) {
      if (!param1Boolean) {
        if (this.f.contains(Formatter.Flags.PLUS)) {
          param1StringBuilder.append('+');
        } else if (this.f.contains(Formatter.Flags.LEADING_SPACE)) {
          param1StringBuilder.append(' ');
        } 
      } else if (this.f.contains(Formatter.Flags.PARENTHESES)) {
        param1StringBuilder.append('(');
      } else {
        param1StringBuilder.append('-');
      } 
      return param1StringBuilder;
    }
    
    private StringBuilder trailingSign(StringBuilder param1StringBuilder, boolean param1Boolean) {
      if (param1Boolean && this.f.contains(Formatter.Flags.PARENTHESES))
        param1StringBuilder.append(')'); 
      return param1StringBuilder;
    }
    
    private void print(BigInteger param1BigInteger, Locale param1Locale) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      boolean bool = (param1BigInteger.signum() == -1);
      BigInteger bigInteger = param1BigInteger.abs();
      leadingSign(stringBuilder, bool);
      if (this.c == 'd') {
        char[] arrayOfChar = bigInteger.toString().toCharArray();
        localizedMagnitude(stringBuilder, arrayOfChar, this.f, adjustWidth(this.width, this.f, bool), param1Locale);
      } else if (this.c == 'o') {
        String str = bigInteger.toString(8);
        int i = str.length() + stringBuilder.length();
        if (bool && this.f.contains(Formatter.Flags.PARENTHESES))
          i++; 
        if (this.f.contains(Formatter.Flags.ALTERNATE)) {
          i++;
          stringBuilder.append('0');
        } 
        if (this.f.contains(Formatter.Flags.ZERO_PAD))
          for (byte b = 0; b < this.width - i; b++)
            stringBuilder.append('0');  
        stringBuilder.append(str);
      } else if (this.c == 'x') {
        String str = bigInteger.toString(16);
        int i = str.length() + stringBuilder.length();
        if (bool && this.f.contains(Formatter.Flags.PARENTHESES))
          i++; 
        if (this.f.contains(Formatter.Flags.ALTERNATE)) {
          i += 2;
          stringBuilder.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "0X" : "0x");
        } 
        if (this.f.contains(Formatter.Flags.ZERO_PAD))
          for (byte b = 0; b < this.width - i; b++)
            stringBuilder.append('0');  
        if (this.f.contains(Formatter.Flags.UPPERCASE))
          str = str.toUpperCase(); 
        stringBuilder.append(str);
      } 
      trailingSign(stringBuilder, (param1BigInteger.signum() == -1));
      Formatter.this.a.append(justify(stringBuilder.toString()));
    }
    
    private void print(float param1Float, Locale param1Locale) throws IOException { print(param1Float, param1Locale); }
    
    private void print(double param1Double, Locale param1Locale) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      boolean bool = (Double.compare(param1Double, 0.0D) == -1);
      if (!Double.isNaN(param1Double)) {
        double d = Math.abs(param1Double);
        leadingSign(stringBuilder, bool);
        if (!Double.isInfinite(d)) {
          print(stringBuilder, d, param1Locale, this.f, this.c, this.precision, bool);
        } else {
          stringBuilder.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "INFINITY" : "Infinity");
        } 
        trailingSign(stringBuilder, bool);
      } else {
        stringBuilder.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "NAN" : "NaN");
      } 
      Formatter.this.a.append(justify(stringBuilder.toString()));
    }
    
    private void print(StringBuilder param1StringBuilder, double param1Double, Locale param1Locale, Formatter.Flags param1Flags, char param1Char, int param1Int, boolean param1Boolean) throws IOException {
      if (param1Char == 'e') {
        byte b = (param1Int == -1) ? 6 : param1Int;
        FormattedFloatingDecimal formattedFloatingDecimal = FormattedFloatingDecimal.valueOf(param1Double, b, FormattedFloatingDecimal.Form.SCIENTIFIC);
        char[] arrayOfChar1 = addZeros(formattedFloatingDecimal.getMantissa(), b);
        if (param1Flags.contains(Formatter.Flags.ALTERNATE) && b == 0)
          arrayOfChar1 = addDot(arrayOfChar1); 
        new char[3][0] = '+';
        new char[3][1] = '0';
        new char[3][2] = '0';
        char[] arrayOfChar2 = (param1Double == 0.0D) ? new char[3] : formattedFloatingDecimal.getExponent();
        int i = this.width;
        if (this.width != -1)
          i = adjustWidth(this.width - arrayOfChar2.length - 1, param1Flags, param1Boolean); 
        localizedMagnitude(param1StringBuilder, arrayOfChar1, param1Flags, i, param1Locale);
        param1StringBuilder.append(param1Flags.contains(Formatter.Flags.UPPERCASE) ? 69 : 101);
        Formatter.Flags flags = param1Flags.dup().remove(Formatter.Flags.GROUP);
        char c1 = arrayOfChar2[0];
        assert c1 == '+' || c1 == '-';
        param1StringBuilder.append(c1);
        char[] arrayOfChar3 = new char[arrayOfChar2.length - 1];
        System.arraycopy(arrayOfChar2, 1, arrayOfChar3, 0, arrayOfChar2.length - 1);
        param1StringBuilder.append(localizedMagnitude(null, arrayOfChar3, flags, -1, param1Locale));
      } else if (param1Char == 'f') {
        byte b = (param1Int == -1) ? 6 : param1Int;
        FormattedFloatingDecimal formattedFloatingDecimal = FormattedFloatingDecimal.valueOf(param1Double, b, FormattedFloatingDecimal.Form.DECIMAL_FLOAT);
        char[] arrayOfChar = addZeros(formattedFloatingDecimal.getMantissa(), b);
        if (param1Flags.contains(Formatter.Flags.ALTERNATE) && b == 0)
          arrayOfChar = addDot(arrayOfChar); 
        int i = this.width;
        if (this.width != -1)
          i = adjustWidth(this.width, param1Flags, param1Boolean); 
        localizedMagnitude(param1StringBuilder, arrayOfChar, param1Flags, i, param1Locale);
      } else if (param1Char == 'g') {
        int j;
        char[] arrayOfChar1;
        int i = param1Int;
        if (param1Int == -1) {
          i = 6;
        } else if (param1Int == 0) {
          i = 1;
        } 
        if (param1Double == 0.0D) {
          arrayOfChar1 = null;
          arrayOfChar2 = new char[] { '0' };
          j = 0;
        } else {
          FormattedFloatingDecimal formattedFloatingDecimal = FormattedFloatingDecimal.valueOf(param1Double, i, FormattedFloatingDecimal.Form.GENERAL);
          arrayOfChar1 = formattedFloatingDecimal.getExponent();
          arrayOfChar2 = formattedFloatingDecimal.getMantissa();
          j = formattedFloatingDecimal.getExponentRounded();
        } 
        if (arrayOfChar1 != null) {
          i--;
        } else {
          i -= j + 1;
        } 
        char[] arrayOfChar2 = addZeros(arrayOfChar2, i);
        if (param1Flags.contains(Formatter.Flags.ALTERNATE) && i == 0)
          arrayOfChar2 = addDot(arrayOfChar2); 
        int k = this.width;
        if (this.width != -1)
          if (arrayOfChar1 != null) {
            k = adjustWidth(this.width - arrayOfChar1.length - 1, param1Flags, param1Boolean);
          } else {
            k = adjustWidth(this.width, param1Flags, param1Boolean);
          }  
        localizedMagnitude(param1StringBuilder, arrayOfChar2, param1Flags, k, param1Locale);
        if (arrayOfChar1 != null) {
          param1StringBuilder.append(param1Flags.contains(Formatter.Flags.UPPERCASE) ? 69 : 101);
          Formatter.Flags flags = param1Flags.dup().remove(Formatter.Flags.GROUP);
          char c1 = arrayOfChar1[0];
          assert c1 == '+' || c1 == '-';
          param1StringBuilder.append(c1);
          char[] arrayOfChar = new char[arrayOfChar1.length - 1];
          System.arraycopy(arrayOfChar1, 1, arrayOfChar, 0, arrayOfChar1.length - 1);
          param1StringBuilder.append(localizedMagnitude(null, arrayOfChar, flags, -1, param1Locale));
        } 
      } else if (param1Char == 'a') {
        int i = param1Int;
        if (param1Int == -1) {
          i = 0;
        } else if (param1Int == 0) {
          i = 1;
        } 
        String str = hexDouble(param1Double, i);
        boolean bool = param1Flags.contains(Formatter.Flags.UPPERCASE);
        param1StringBuilder.append(bool ? "0X" : "0x");
        if (param1Flags.contains(Formatter.Flags.ZERO_PAD))
          for (byte b = 0; b < this.width - str.length() - 2; b++)
            param1StringBuilder.append('0');  
        int j = str.indexOf('p');
        char[] arrayOfChar = str.substring(0, j).toCharArray();
        if (bool) {
          String str1 = new String(arrayOfChar);
          str1 = str1.toUpperCase(Locale.US);
          arrayOfChar = str1.toCharArray();
        } 
        param1StringBuilder.append((i != 0) ? addZeros(arrayOfChar, i) : arrayOfChar);
        param1StringBuilder.append(bool ? 80 : 112);
        param1StringBuilder.append(str.substring(j + 1));
      } 
    }
    
    private char[] addZeros(char[] param1ArrayOfChar, int param1Int) {
      int i;
      for (i = 0; i < param1ArrayOfChar.length && param1ArrayOfChar[i] != '.'; i++);
      boolean bool = false;
      if (i == param1ArrayOfChar.length)
        bool = true; 
      int j = param1ArrayOfChar.length - i - (bool ? 0 : 1);
      assert j <= param1Int;
      if (j == param1Int)
        return param1ArrayOfChar; 
      char[] arrayOfChar = new char[param1ArrayOfChar.length + param1Int - j + (bool ? 1 : 0)];
      System.arraycopy(param1ArrayOfChar, 0, arrayOfChar, 0, param1ArrayOfChar.length);
      int k = param1ArrayOfChar.length;
      if (bool) {
        arrayOfChar[param1ArrayOfChar.length] = '.';
        k++;
      } 
      for (int m = k; m < arrayOfChar.length; m++)
        arrayOfChar[m] = '0'; 
      return arrayOfChar;
    }
    
    private String hexDouble(double param1Double, int param1Int) {
      if (!Double.isFinite(param1Double) || param1Double == 0.0D || param1Int == 0 || param1Int >= 13)
        return Double.toHexString(param1Double).substring(2); 
      assert param1Int >= 1 && param1Int <= 12;
      int i = Math.getExponent(param1Double);
      boolean bool1 = (i == -1023) ? 1 : 0;
      if (bool1) {
        scaleUp = Math.scalb(1.0D, 54);
        param1Double *= scaleUp;
        i = Math.getExponent(param1Double);
        assert i >= -1022 && i <= 1023 : i;
      } 
      int j = 1 + param1Int * 4;
      int k = 53 - j;
      assert k >= 1 && k < 53;
      long l1 = Double.doubleToLongBits(param1Double);
      long l2 = (l1 & Float.MAX_VALUE) >> k;
      long l3 = l1 & (-1L << k ^ 0xFFFFFFFFFFFFFFFFL);
      boolean bool2 = ((l2 & 0x1L) == 0L) ? 1 : 0;
      boolean bool3 = ((1L << k - 1 & l3) != 0L) ? 1 : 0;
      boolean bool4 = (k > 1 && ((1L << k - 1 ^ 0xFFFFFFFFFFFFFFFFL) & l3) != 0L) ? 1 : 0;
      if ((bool2 && bool3 && bool4) || (!bool2 && bool3))
        l2++; 
      long l4 = l1 & Float.MIN_VALUE;
      l2 = l4 | l2 << k;
      double d = Double.longBitsToDouble(l2);
      if (Double.isInfinite(d))
        return "1.0p1024"; 
      String str1 = Double.toHexString(d).substring(2);
      if (!bool1)
        return str1; 
      int m = str1.indexOf('p');
      if (m == -1) {
        assert false;
        return null;
      } 
      String str2 = str1.substring(m + 1);
      int n = Integer.parseInt(str2) - 54;
      return str1.substring(0, m) + "p" + Integer.toString(n);
    }
    
    private void print(BigDecimal param1BigDecimal, Locale param1Locale) throws IOException {
      if (this.c == 'a')
        failConversion(this.c, param1BigDecimal); 
      StringBuilder stringBuilder = new StringBuilder();
      boolean bool = (param1BigDecimal.signum() == -1);
      BigDecimal bigDecimal = param1BigDecimal.abs();
      leadingSign(stringBuilder, bool);
      print(stringBuilder, bigDecimal, param1Locale, this.f, this.c, this.precision, bool);
      trailingSign(stringBuilder, bool);
      Formatter.this.a.append(justify(stringBuilder.toString()));
    }
    
    private void print(StringBuilder param1StringBuilder, BigDecimal param1BigDecimal, Locale param1Locale, Formatter.Flags param1Flags, char param1Char, int param1Int, boolean param1Boolean) throws IOException {
      if (param1Char == 'e') {
        int n;
        int i = (param1Int == -1) ? 6 : param1Int;
        int j = param1BigDecimal.scale();
        int k = param1BigDecimal.precision();
        int m = 0;
        if (i > k - 1) {
          n = k;
          m = i - k - 1;
        } else {
          n = i + 1;
        } 
        MathContext mathContext = new MathContext(n);
        BigDecimal bigDecimal = new BigDecimal(param1BigDecimal.unscaledValue(), j, mathContext);
        BigDecimalLayout bigDecimalLayout = new BigDecimalLayout(bigDecimal.unscaledValue(), bigDecimal.scale(), Formatter.BigDecimalLayoutForm.SCIENTIFIC);
        char[] arrayOfChar1 = bigDecimalLayout.mantissa();
        if ((k == 1 || !bigDecimalLayout.hasDot()) && (m > 0 || param1Flags.contains(Formatter.Flags.ALTERNATE)))
          arrayOfChar1 = addDot(arrayOfChar1); 
        arrayOfChar1 = trailingZeros(arrayOfChar1, m);
        char[] arrayOfChar2 = bigDecimalLayout.exponent();
        int i1 = this.width;
        if (this.width != -1)
          i1 = adjustWidth(this.width - arrayOfChar2.length - 1, param1Flags, param1Boolean); 
        localizedMagnitude(param1StringBuilder, arrayOfChar1, param1Flags, i1, param1Locale);
        param1StringBuilder.append(param1Flags.contains(Formatter.Flags.UPPERCASE) ? 69 : 101);
        Formatter.Flags flags = param1Flags.dup().remove(Formatter.Flags.GROUP);
        char c1 = arrayOfChar2[0];
        assert c1 == '+' || c1 == '-';
        param1StringBuilder.append(arrayOfChar2[0]);
        char[] arrayOfChar3 = new char[arrayOfChar2.length - 1];
        System.arraycopy(arrayOfChar2, 1, arrayOfChar3, 0, arrayOfChar2.length - 1);
        param1StringBuilder.append(localizedMagnitude(null, arrayOfChar3, flags, -1, param1Locale));
      } else if (param1Char == 'f') {
        int i = (param1Int == -1) ? 6 : param1Int;
        int j = param1BigDecimal.scale();
        if (j > i) {
          int m = param1BigDecimal.precision();
          if (m <= j) {
            param1BigDecimal = param1BigDecimal.setScale(i, RoundingMode.HALF_UP);
          } else {
            m -= j - i;
            param1BigDecimal = new BigDecimal(param1BigDecimal.unscaledValue(), j, new MathContext(m));
          } 
        } 
        BigDecimalLayout bigDecimalLayout = new BigDecimalLayout(param1BigDecimal.unscaledValue(), param1BigDecimal.scale(), Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT);
        char[] arrayOfChar = bigDecimalLayout.mantissa();
        int k = (bigDecimalLayout.scale() < i) ? (i - bigDecimalLayout.scale()) : 0;
        if (bigDecimalLayout.scale() == 0 && (param1Flags.contains(Formatter.Flags.ALTERNATE) || k > 0))
          arrayOfChar = addDot(bigDecimalLayout.mantissa()); 
        arrayOfChar = trailingZeros(arrayOfChar, k);
        localizedMagnitude(param1StringBuilder, arrayOfChar, param1Flags, adjustWidth(this.width, param1Flags, param1Boolean), param1Locale);
      } else if (param1Char == 'g') {
        int i = param1Int;
        if (param1Int == -1) {
          i = 6;
        } else if (param1Int == 0) {
          i = 1;
        } 
        BigDecimal bigDecimal1;
        BigDecimal bigDecimal2 = (bigDecimal1 = BigDecimal.valueOf(1L, 4)).valueOf(1L, -i);
        if (param1BigDecimal.equals(BigDecimal.ZERO) || (param1BigDecimal.compareTo(bigDecimal1) != -1 && param1BigDecimal.compareTo(bigDecimal2) == -1)) {
          int j = -param1BigDecimal.scale() + param1BigDecimal.unscaledValue().toString().length() - 1;
          i = i - j - 1;
          print(param1StringBuilder, param1BigDecimal, param1Locale, param1Flags, 'f', i, param1Boolean);
        } else {
          print(param1StringBuilder, param1BigDecimal, param1Locale, param1Flags, 'e', i - 1, param1Boolean);
        } 
      } else if (param1Char == 'a' && !$assertionsDisabled) {
        throw new AssertionError();
      } 
    }
    
    private int adjustWidth(int param1Int, Formatter.Flags param1Flags, boolean param1Boolean) {
      int i = param1Int;
      if (i != -1 && param1Boolean && param1Flags.contains(Formatter.Flags.PARENTHESES))
        i--; 
      return i;
    }
    
    private char[] addDot(char[] param1ArrayOfChar) {
      char[] arrayOfChar = param1ArrayOfChar;
      arrayOfChar = new char[param1ArrayOfChar.length + 1];
      System.arraycopy(param1ArrayOfChar, 0, arrayOfChar, 0, param1ArrayOfChar.length);
      arrayOfChar[arrayOfChar.length - 1] = '.';
      return arrayOfChar;
    }
    
    private char[] trailingZeros(char[] param1ArrayOfChar, int param1Int) {
      char[] arrayOfChar = param1ArrayOfChar;
      if (param1Int > 0) {
        arrayOfChar = new char[param1ArrayOfChar.length + param1Int];
        System.arraycopy(param1ArrayOfChar, 0, arrayOfChar, 0, param1ArrayOfChar.length);
        for (int i = param1ArrayOfChar.length; i < arrayOfChar.length; i++)
          arrayOfChar[i] = '0'; 
      } 
      return arrayOfChar;
    }
    
    private void print(Calendar param1Calendar, char param1Char, Locale param1Locale) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      print(stringBuilder, param1Calendar, param1Char, param1Locale);
      String str = justify(stringBuilder.toString());
      if (this.f.contains(Formatter.Flags.UPPERCASE))
        str = str.toUpperCase(); 
      Formatter.this.a.append(str);
    }
    
    private Appendable print(StringBuilder param1StringBuilder, Calendar param1Calendar, char param1Char, Locale param1Locale) throws IOException {
      Formatter.Flags flags6;
      int n;
      int m;
      Formatter.Flags flags4;
      DateFormatSymbols dateFormatSymbols;
      Formatter.Flags flags5;
      StringBuilder stringBuilder;
      byte b;
      Formatter.Flags flags3;
      Formatter.Flags flags2;
      boolean bool;
      String str;
      Locale locale;
      Formatter.Flags flags1;
      int i;
      int k;
      char c1;
      int j;
      long l2;
      TimeZone timeZone;
      String[] arrayOfString;
      long l1;
      if (param1StringBuilder == null)
        param1StringBuilder = new StringBuilder(); 
      switch (param1Char) {
        case 'H':
        case 'I':
        case 'k':
        case 'l':
          k = param1Calendar.get(11);
          if (param1Char == 'I' || param1Char == 'l')
            k = (k == 0 || k == 12) ? 12 : (k % 12); 
          flags3 = (param1Char == 'H' || param1Char == 'I') ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
          param1StringBuilder.append(localizedMagnitude(null, k, flags3, 2, param1Locale));
          return param1StringBuilder;
        case 'M':
          k = param1Calendar.get(12);
          flags3 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, k, flags3, 2, param1Locale));
          return param1StringBuilder;
        case 'N':
          k = param1Calendar.get(14) * 1000000;
          flags3 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, k, flags3, 9, param1Locale));
          return param1StringBuilder;
        case 'L':
          k = param1Calendar.get(14);
          flags3 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, k, flags3, 3, param1Locale));
          return param1StringBuilder;
        case 'Q':
          l2 = param1Calendar.getTimeInMillis();
          flags5 = Formatter.Flags.NONE;
          param1StringBuilder.append(localizedMagnitude(null, l2, flags5, this.width, param1Locale));
          return param1StringBuilder;
        case 'p':
          arrayOfString = new String[] { "AM", "PM" };
          if (param1Locale != null && param1Locale != Locale.US) {
            DateFormatSymbols dateFormatSymbols1 = DateFormatSymbols.getInstance(param1Locale);
            arrayOfString = dateFormatSymbols1.getAmPmStrings();
          } 
          str = arrayOfString[param1Calendar.get(9)];
          param1StringBuilder.append(str.toLowerCase((param1Locale != null) ? param1Locale : Locale.US));
          return param1StringBuilder;
        case 's':
          l1 = param1Calendar.getTimeInMillis() / 1000L;
          flags5 = Formatter.Flags.NONE;
          param1StringBuilder.append(localizedMagnitude(null, l1, flags5, this.width, param1Locale));
          return param1StringBuilder;
        case 'S':
          j = param1Calendar.get(13);
          flags2 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, j, flags2, 2, param1Locale));
          return param1StringBuilder;
        case 'z':
          j = param1Calendar.get(15) + param1Calendar.get(16);
          bool = (j < 0) ? 1 : 0;
          param1StringBuilder.append(bool ? 45 : 43);
          if (bool)
            j = -j; 
          m = j / 60000;
          n = m / 60 * 100 + m % 60;
          flags6 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, n, flags6, 4, param1Locale));
          return param1StringBuilder;
        case 'Z':
          timeZone = param1Calendar.getTimeZone();
          param1StringBuilder.append(timeZone.getDisplayName((param1Calendar.get(16) != 0), 0, (param1Locale == null) ? Locale.US : param1Locale));
          return param1StringBuilder;
        case 'A':
        case 'a':
          i = param1Calendar.get(7);
          locale = (param1Locale == null) ? Locale.US : param1Locale;
          dateFormatSymbols = DateFormatSymbols.getInstance(locale);
          if (param1Char == 'A') {
            param1StringBuilder.append(dateFormatSymbols.getWeekdays()[i]);
          } else {
            param1StringBuilder.append(dateFormatSymbols.getShortWeekdays()[i]);
          } 
          return param1StringBuilder;
        case 'B':
        case 'b':
        case 'h':
          i = param1Calendar.get(2);
          locale = (param1Locale == null) ? Locale.US : param1Locale;
          dateFormatSymbols = DateFormatSymbols.getInstance(locale);
          if (param1Char == 'B') {
            param1StringBuilder.append(dateFormatSymbols.getMonths()[i]);
          } else {
            param1StringBuilder.append(dateFormatSymbols.getShortMonths()[i]);
          } 
          return param1StringBuilder;
        case 'C':
        case 'Y':
        case 'y':
          i = param1Calendar.get(1);
          b = 2;
          switch (param1Char) {
            case 'C':
              i /= 100;
              break;
            case 'y':
              i %= 100;
              break;
            case 'Y':
              b = 4;
              break;
          } 
          flags4 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, i, flags4, b, param1Locale));
          return param1StringBuilder;
        case 'd':
        case 'e':
          i = param1Calendar.get(5);
          flags1 = (param1Char == 'd') ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
          param1StringBuilder.append(localizedMagnitude(null, i, flags1, 2, param1Locale));
          return param1StringBuilder;
        case 'j':
          i = param1Calendar.get(6);
          flags1 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, i, flags1, 3, param1Locale));
          return param1StringBuilder;
        case 'm':
          i = param1Calendar.get(2) + 1;
          flags1 = Formatter.Flags.ZERO_PAD;
          param1StringBuilder.append(localizedMagnitude(null, i, flags1, 2, param1Locale));
          return param1StringBuilder;
        case 'R':
        case 'T':
          i = 58;
          print(param1StringBuilder, param1Calendar, 'H', param1Locale).append(i);
          print(param1StringBuilder, param1Calendar, 'M', param1Locale);
          if (param1Char == 'T') {
            param1StringBuilder.append(i);
            print(param1StringBuilder, param1Calendar, 'S', param1Locale);
          } 
          return param1StringBuilder;
        case 'r':
          c1 = ':';
          print(param1StringBuilder, param1Calendar, 'I', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'M', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'S', param1Locale).append(' ');
          stringBuilder = new StringBuilder();
          print(stringBuilder, param1Calendar, 'p', param1Locale);
          param1StringBuilder.append(stringBuilder.toString().toUpperCase((param1Locale != null) ? param1Locale : Locale.US));
          return param1StringBuilder;
        case 'c':
          c1 = ' ';
          print(param1StringBuilder, param1Calendar, 'a', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'b', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'd', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'T', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'Z', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'Y', param1Locale);
          return param1StringBuilder;
        case 'D':
          c1 = '/';
          print(param1StringBuilder, param1Calendar, 'm', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'd', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'y', param1Locale);
          return param1StringBuilder;
        case 'F':
          c1 = '-';
          print(param1StringBuilder, param1Calendar, 'Y', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'm', param1Locale).append(c1);
          print(param1StringBuilder, param1Calendar, 'd', param1Locale);
          return param1StringBuilder;
      } 
      assert false;
      return param1StringBuilder;
    }
    
    private void print(TemporalAccessor param1TemporalAccessor, char param1Char, Locale param1Locale) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      print(stringBuilder, param1TemporalAccessor, param1Char, param1Locale);
      String str = justify(stringBuilder.toString());
      if (this.f.contains(Formatter.Flags.UPPERCASE))
        str = str.toUpperCase(); 
      Formatter.this.a.append(str);
    }
    
    private Appendable print(StringBuilder param1StringBuilder, TemporalAccessor param1TemporalAccessor, char param1Char, Locale param1Locale) throws IOException {
      if (param1StringBuilder == null)
        param1StringBuilder = new StringBuilder(); 
      try {
        Formatter.Flags flags6;
        int n;
        DateFormatSymbols dateFormatSymbols;
        Formatter.Flags flags4;
        int m;
        Formatter.Flags flags5;
        String str;
        byte b;
        StringBuilder stringBuilder;
        boolean bool;
        Locale locale;
        Formatter.Flags flags1;
        Formatter.Flags flags2;
        Formatter.Flags flags3;
        int j;
        ZoneId zoneId;
        long l1;
        long l2;
        char c1;
        String[] arrayOfString;
        int i;
        int k;
        switch (param1Char) {
          case 'H':
            k = param1TemporalAccessor.get(ChronoField.HOUR_OF_DAY);
            param1StringBuilder.append(localizedMagnitude(null, k, Formatter.Flags.ZERO_PAD, 2, param1Locale));
            return param1StringBuilder;
          case 'k':
            k = param1TemporalAccessor.get(ChronoField.HOUR_OF_DAY);
            param1StringBuilder.append(localizedMagnitude(null, k, Formatter.Flags.NONE, 2, param1Locale));
            return param1StringBuilder;
          case 'I':
            k = param1TemporalAccessor.get(ChronoField.CLOCK_HOUR_OF_AMPM);
            param1StringBuilder.append(localizedMagnitude(null, k, Formatter.Flags.ZERO_PAD, 2, param1Locale));
            return param1StringBuilder;
          case 'l':
            k = param1TemporalAccessor.get(ChronoField.CLOCK_HOUR_OF_AMPM);
            param1StringBuilder.append(localizedMagnitude(null, k, Formatter.Flags.NONE, 2, param1Locale));
            return param1StringBuilder;
          case 'M':
            k = param1TemporalAccessor.get(ChronoField.MINUTE_OF_HOUR);
            flags3 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, k, flags3, 2, param1Locale));
            return param1StringBuilder;
          case 'N':
            k = param1TemporalAccessor.get(ChronoField.MILLI_OF_SECOND) * 1000000;
            flags3 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, k, flags3, 9, param1Locale));
            return param1StringBuilder;
          case 'L':
            k = param1TemporalAccessor.get(ChronoField.MILLI_OF_SECOND);
            flags3 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, k, flags3, 3, param1Locale));
            return param1StringBuilder;
          case 'Q':
            l2 = param1TemporalAccessor.getLong(ChronoField.INSTANT_SECONDS) * 1000L + param1TemporalAccessor.getLong(ChronoField.MILLI_OF_SECOND);
            flags5 = Formatter.Flags.NONE;
            param1StringBuilder.append(localizedMagnitude(null, l2, flags5, this.width, param1Locale));
            return param1StringBuilder;
          case 'p':
            arrayOfString = new String[] { "AM", "PM" };
            if (param1Locale != null && param1Locale != Locale.US) {
              DateFormatSymbols dateFormatSymbols1 = DateFormatSymbols.getInstance(param1Locale);
              arrayOfString = dateFormatSymbols1.getAmPmStrings();
            } 
            str = arrayOfString[param1TemporalAccessor.get(ChronoField.AMPM_OF_DAY)];
            param1StringBuilder.append(str.toLowerCase((param1Locale != null) ? param1Locale : Locale.US));
            return param1StringBuilder;
          case 's':
            l1 = param1TemporalAccessor.getLong(ChronoField.INSTANT_SECONDS);
            flags5 = Formatter.Flags.NONE;
            param1StringBuilder.append(localizedMagnitude(null, l1, flags5, this.width, param1Locale));
            return param1StringBuilder;
          case 'S':
            j = param1TemporalAccessor.get(ChronoField.SECOND_OF_MINUTE);
            flags2 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, j, flags2, 2, param1Locale));
            return param1StringBuilder;
          case 'z':
            j = param1TemporalAccessor.get(ChronoField.OFFSET_SECONDS);
            bool = (j < 0) ? 1 : 0;
            param1StringBuilder.append(bool ? 45 : 43);
            if (bool)
              j = -j; 
            m = j / 60;
            n = m / 60 * 100 + m % 60;
            flags6 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, n, flags6, 4, param1Locale));
            return param1StringBuilder;
          case 'Z':
            zoneId = (ZoneId)param1TemporalAccessor.query(TemporalQueries.zone());
            if (zoneId == null)
              throw new IllegalFormatConversionException(param1Char, param1TemporalAccessor.getClass()); 
            if (!(zoneId instanceof java.time.ZoneOffset) && param1TemporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)) {
              Instant instant = Instant.from(param1TemporalAccessor);
              param1StringBuilder.append(TimeZone.getTimeZone(zoneId.getId()).getDisplayName(zoneId.getRules().isDaylightSavings(instant), 0, (param1Locale == null) ? Locale.US : param1Locale));
            } else {
              param1StringBuilder.append(zoneId.getId());
            } 
            return param1StringBuilder;
          case 'A':
          case 'a':
            i = param1TemporalAccessor.get(ChronoField.DAY_OF_WEEK) % 7 + 1;
            locale = (param1Locale == null) ? Locale.US : param1Locale;
            dateFormatSymbols = DateFormatSymbols.getInstance(locale);
            if (param1Char == 'A') {
              param1StringBuilder.append(dateFormatSymbols.getWeekdays()[i]);
            } else {
              param1StringBuilder.append(dateFormatSymbols.getShortWeekdays()[i]);
            } 
            return param1StringBuilder;
          case 'B':
          case 'b':
          case 'h':
            i = param1TemporalAccessor.get(ChronoField.MONTH_OF_YEAR) - 1;
            locale = (param1Locale == null) ? Locale.US : param1Locale;
            dateFormatSymbols = DateFormatSymbols.getInstance(locale);
            if (param1Char == 'B') {
              param1StringBuilder.append(dateFormatSymbols.getMonths()[i]);
            } else {
              param1StringBuilder.append(dateFormatSymbols.getShortMonths()[i]);
            } 
            return param1StringBuilder;
          case 'C':
          case 'Y':
          case 'y':
            i = param1TemporalAccessor.get(ChronoField.YEAR_OF_ERA);
            b = 2;
            switch (param1Char) {
              case 'C':
                i /= 100;
                break;
              case 'y':
                i %= 100;
                break;
              case 'Y':
                b = 4;
                break;
            } 
            flags4 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, i, flags4, b, param1Locale));
            return param1StringBuilder;
          case 'd':
          case 'e':
            i = param1TemporalAccessor.get(ChronoField.DAY_OF_MONTH);
            flags1 = (param1Char == 'd') ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
            param1StringBuilder.append(localizedMagnitude(null, i, flags1, 2, param1Locale));
            return param1StringBuilder;
          case 'j':
            i = param1TemporalAccessor.get(ChronoField.DAY_OF_YEAR);
            flags1 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, i, flags1, 3, param1Locale));
            return param1StringBuilder;
          case 'm':
            i = param1TemporalAccessor.get(ChronoField.MONTH_OF_YEAR);
            flags1 = Formatter.Flags.ZERO_PAD;
            param1StringBuilder.append(localizedMagnitude(null, i, flags1, 2, param1Locale));
            return param1StringBuilder;
          case 'R':
          case 'T':
            i = 58;
            print(param1StringBuilder, param1TemporalAccessor, 'H', param1Locale).append(i);
            print(param1StringBuilder, param1TemporalAccessor, 'M', param1Locale);
            if (param1Char == 'T') {
              param1StringBuilder.append(i);
              print(param1StringBuilder, param1TemporalAccessor, 'S', param1Locale);
            } 
            return param1StringBuilder;
          case 'r':
            c1 = ':';
            print(param1StringBuilder, param1TemporalAccessor, 'I', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'M', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'S', param1Locale).append(' ');
            stringBuilder = new StringBuilder();
            print(stringBuilder, param1TemporalAccessor, 'p', param1Locale);
            param1StringBuilder.append(stringBuilder.toString().toUpperCase((param1Locale != null) ? param1Locale : Locale.US));
            return param1StringBuilder;
          case 'c':
            c1 = ' ';
            print(param1StringBuilder, param1TemporalAccessor, 'a', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'b', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'd', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'T', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'Z', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'Y', param1Locale);
            return param1StringBuilder;
          case 'D':
            c1 = '/';
            print(param1StringBuilder, param1TemporalAccessor, 'm', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'd', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'y', param1Locale);
            return param1StringBuilder;
          case 'F':
            c1 = '-';
            print(param1StringBuilder, param1TemporalAccessor, 'Y', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'm', param1Locale).append(c1);
            print(param1StringBuilder, param1TemporalAccessor, 'd', param1Locale);
            return param1StringBuilder;
        } 
        assert false;
      } catch (DateTimeException dateTimeException) {
        throw new IllegalFormatConversionException(param1Char, param1TemporalAccessor.getClass());
      } 
      return param1StringBuilder;
    }
    
    private void failMismatch(Formatter.Flags param1Flags, char param1Char) {
      String str = param1Flags.toString();
      throw new FormatFlagsConversionMismatchException(str, param1Char);
    }
    
    private void failConversion(char param1Char, Object param1Object) { throw new IllegalFormatConversionException(param1Char, param1Object.getClass()); }
    
    private char getZero(Locale param1Locale) {
      if (param1Locale != null && !param1Locale.equals(Formatter.this.locale())) {
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(param1Locale);
        return decimalFormatSymbols.getZeroDigit();
      } 
      return Formatter.this.zero;
    }
    
    private StringBuilder localizedMagnitude(StringBuilder param1StringBuilder, long param1Long, Formatter.Flags param1Flags, int param1Int, Locale param1Locale) {
      char[] arrayOfChar = Long.toString(param1Long, 10).toCharArray();
      return localizedMagnitude(param1StringBuilder, arrayOfChar, param1Flags, param1Int, param1Locale);
    }
    
    private StringBuilder localizedMagnitude(StringBuilder param1StringBuilder, char[] param1ArrayOfChar, Formatter.Flags param1Flags, int param1Int, Locale param1Locale) {
      if (param1StringBuilder == null)
        param1StringBuilder = new StringBuilder(); 
      int i = param1StringBuilder.length();
      char c1 = getZero(param1Locale);
      char c2 = Character.MIN_VALUE;
      int j = -1;
      char c3 = Character.MIN_VALUE;
      int k = param1ArrayOfChar.length;
      int m = k;
      int n;
      for (n = 0; n < k; n++) {
        if (param1ArrayOfChar[n] == '.') {
          m = n;
          break;
        } 
      } 
      if (m < k)
        if (param1Locale == null || param1Locale.equals(Locale.US)) {
          c3 = '.';
        } else {
          DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(param1Locale);
          c3 = decimalFormatSymbols.getDecimalSeparator();
        }  
      if (param1Flags.contains(Formatter.Flags.GROUP))
        if (param1Locale == null || param1Locale.equals(Locale.US)) {
          c2 = ',';
          j = 3;
        } else {
          DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(param1Locale);
          c2 = decimalFormatSymbols.getGroupingSeparator();
          DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getIntegerInstance(param1Locale);
          j = decimalFormat.getGroupingSize();
        }  
      for (n = 0; n < k; n++) {
        if (n == m) {
          param1StringBuilder.append(c3);
          c2 = Character.MIN_VALUE;
        } else {
          char c4 = param1ArrayOfChar[n];
          param1StringBuilder.append((char)(c4 - '0' + c1));
          if (c2 != Character.MIN_VALUE && n != m - 1 && (m - n) % j == 1)
            param1StringBuilder.append(c2); 
        } 
      } 
      k = param1StringBuilder.length();
      if (param1Int != -1 && param1Flags.contains(Formatter.Flags.ZERO_PAD))
        for (n = 0; n < param1Int - k; n++)
          param1StringBuilder.insert(i, c1);  
      return param1StringBuilder;
    }
    
    private class BigDecimalLayout {
      private StringBuilder mant;
      
      private StringBuilder exp;
      
      private boolean dot = false;
      
      private int scale;
      
      public BigDecimalLayout(BigInteger param2BigInteger, int param2Int, Formatter.BigDecimalLayoutForm param2BigDecimalLayoutForm) { layout(param2BigInteger, param2Int, param2BigDecimalLayoutForm); }
      
      public boolean hasDot() { return this.dot; }
      
      public int scale() { return this.scale; }
      
      public char[] layoutChars() {
        StringBuilder stringBuilder = new StringBuilder(this.mant);
        if (this.exp != null) {
          stringBuilder.append('E');
          stringBuilder.append(this.exp);
        } 
        return toCharArray(stringBuilder);
      }
      
      public char[] mantissa() { return toCharArray(this.mant); }
      
      public char[] exponent() { return toCharArray(this.exp); }
      
      private char[] toCharArray(StringBuilder param2StringBuilder) {
        if (param2StringBuilder == null)
          return null; 
        char[] arrayOfChar = new char[param2StringBuilder.length()];
        param2StringBuilder.getChars(0, arrayOfChar.length, arrayOfChar, 0);
        return arrayOfChar;
      }
      
      private void layout(BigInteger param2BigInteger, int param2Int, Formatter.BigDecimalLayoutForm param2BigDecimalLayoutForm) {
        char[] arrayOfChar = param2BigInteger.toString().toCharArray();
        this.scale = param2Int;
        this.mant = new StringBuilder(arrayOfChar.length + 14);
        if (param2Int == 0) {
          int i = arrayOfChar.length;
          if (i > 1) {
            this.mant.append(arrayOfChar[0]);
            if (param2BigDecimalLayoutForm == Formatter.BigDecimalLayoutForm.SCIENTIFIC) {
              this.mant.append('.');
              this.dot = true;
              this.mant.append(arrayOfChar, 1, i - 1);
              this.exp = new StringBuilder("+");
              if (i < 10) {
                this.exp.append("0").append(i - 1);
              } else {
                this.exp.append(i - 1);
              } 
            } else {
              this.mant.append(arrayOfChar, 1, i - 1);
            } 
          } else {
            this.mant.append(arrayOfChar);
            if (param2BigDecimalLayoutForm == Formatter.BigDecimalLayoutForm.SCIENTIFIC)
              this.exp = new StringBuilder("+00"); 
          } 
          return;
        } 
        long l = -(param2Int) + (arrayOfChar.length - 1);
        if (param2BigDecimalLayoutForm == Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT) {
          int i = param2Int - arrayOfChar.length;
          if (i >= 0) {
            this.mant.append("0.");
            this.dot = true;
            while (i > 0) {
              this.mant.append('0');
              i--;
            } 
            this.mant.append(arrayOfChar);
          } else if (-i < arrayOfChar.length) {
            this.mant.append(arrayOfChar, 0, -i);
            this.mant.append('.');
            this.dot = true;
            this.mant.append(arrayOfChar, -i, param2Int);
          } else {
            this.mant.append(arrayOfChar, 0, arrayOfChar.length);
            for (byte b = 0; b < -param2Int; b++)
              this.mant.append('0'); 
            this.scale = 0;
          } 
        } else {
          this.mant.append(arrayOfChar[0]);
          if (arrayOfChar.length > 1) {
            this.mant.append('.');
            this.dot = true;
            this.mant.append(arrayOfChar, 1, arrayOfChar.length - 1);
          } 
          this.exp = new StringBuilder();
          if (l != 0L) {
            long l1 = Math.abs(l);
            this.exp.append((l < 0L) ? 45 : 43);
            if (l1 < 10L)
              this.exp.append('0'); 
            this.exp.append(l1);
          } else {
            this.exp.append("+00");
          } 
        } 
      }
    }
  }
  
  private static interface FormatString {
    int index();
    
    void print(Object param1Object, Locale param1Locale) throws IOException;
    
    String toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Formatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */