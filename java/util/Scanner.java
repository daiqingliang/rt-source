package java.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.LRUCache;

public final class Scanner extends Object implements Iterator<String>, Closeable {
  private CharBuffer buf;
  
  private static final int BUFFER_SIZE = 1024;
  
  private int position;
  
  private Matcher matcher;
  
  private Pattern delimPattern;
  
  private Pattern hasNextPattern;
  
  private int hasNextPosition;
  
  private String hasNextResult;
  
  private Readable source;
  
  private boolean sourceClosed = false;
  
  private boolean needInput = false;
  
  private boolean skipped = false;
  
  private int savedScannerPosition = -1;
  
  private Object typeCache = null;
  
  private boolean matchValid = false;
  
  private boolean closed = false;
  
  private int radix = 10;
  
  private int defaultRadix = 10;
  
  private Locale locale = null;
  
  private LRUCache<String, Pattern> patternCache = new LRUCache<String, Pattern>(7) {
      protected Pattern create(String param1String) { return Pattern.compile(param1String); }
      
      protected boolean hasName(Pattern param1Pattern, String param1String) { return param1Pattern.pattern().equals(param1String); }
    };
  
  private IOException lastException;
  
  private static Pattern WHITESPACE_PATTERN;
  
  private static Pattern FIND_ANY_PATTERN;
  
  private static Pattern NON_ASCII_DIGIT = (FIND_ANY_PATTERN = (WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+")).compile("(?s).*")).compile("[\\p{javaDigit}&&[^0-9]]");
  
  private String groupSeparator = "\\,";
  
  private String decimalSeparator = "\\.";
  
  private String nanString = "NaN";
  
  private String infinityString = "Infinity";
  
  private String positivePrefix = "";
  
  private String negativePrefix = "\\-";
  
  private String positiveSuffix = "";
  
  private String negativeSuffix = "";
  
  private static final String BOOLEAN_PATTERN = "true|false";
  
  private Pattern integerPattern;
  
  private String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
  
  private String non0Digit = "[\\p{javaDigit}&&[^0]]";
  
  private int SIMPLE_GROUP_INDEX = 5;
  
  private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r  ]";
  
  private static final String LINE_PATTERN = ".*(\r\n|[\n\r  ])|.+$";
  
  private Pattern floatPattern;
  
  private Pattern decimalPattern;
  
  private static Pattern boolPattern() {
    Pattern pattern = boolPattern;
    if (pattern == null)
      boolPattern = pattern = Pattern.compile("true|false", 2); 
    return pattern;
  }
  
  private String buildIntegerPatternString() {
    String str1 = this.digits.substring(0, this.radix);
    String str2 = "((?i)[" + str1 + "]|\\p{javaDigit})";
    String str3 = "(" + this.non0Digit + str2 + "?" + str2 + "?(" + this.groupSeparator + str2 + str2 + str2 + ")+)";
    String str4 = "((" + str2 + "++)|" + str3 + ")";
    String str5 = "([-+]?(" + str4 + "))";
    String str6 = this.negativePrefix + str4 + this.negativeSuffix;
    String str7 = this.positivePrefix + str4 + this.positiveSuffix;
    return "(" + str5 + ")|(" + str7 + ")|(" + str6 + ")";
  }
  
  private Pattern integerPattern() {
    if (this.integerPattern == null)
      this.integerPattern = (Pattern)this.patternCache.forName(buildIntegerPatternString()); 
    return this.integerPattern;
  }
  
  private static Pattern separatorPattern() {
    Pattern pattern = separatorPattern;
    if (pattern == null)
      separatorPattern = pattern = Pattern.compile("\r\n|[\n\r  ]"); 
    return pattern;
  }
  
  private static Pattern linePattern() {
    Pattern pattern = linePattern;
    if (pattern == null)
      linePattern = pattern = Pattern.compile(".*(\r\n|[\n\r  ])|.+$"); 
    return pattern;
  }
  
  private void buildFloatAndDecimalPattern() {
    String str1 = "([0-9]|(\\p{javaDigit}))";
    String str2 = "([eE][+-]?" + str1 + "+)?";
    String str3 = "(" + this.non0Digit + str1 + "?" + str1 + "?(" + this.groupSeparator + str1 + str1 + str1 + ")+)";
    String str4 = "((" + str1 + "++)|" + str3 + ")";
    String str5 = "(" + str4 + "|" + str4 + this.decimalSeparator + str1 + "*+|" + this.decimalSeparator + str1 + "++)";
    String str6 = "(NaN|" + this.nanString + "|Infinity|" + this.infinityString + ")";
    String str7 = "(" + this.positivePrefix + str5 + this.positiveSuffix + str2 + ")";
    String str8 = "(" + this.negativePrefix + str5 + this.negativeSuffix + str2 + ")";
    String str9 = "(([-+]?" + str5 + str2 + ")|" + str7 + "|" + str8 + ")";
    String str10 = "[-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?";
    String str11 = "(" + this.positivePrefix + str6 + this.positiveSuffix + ")";
    String str12 = "(" + this.negativePrefix + str6 + this.negativeSuffix + ")";
    String str13 = "(([-+]?" + str6 + ")|" + str11 + "|" + str12 + ")";
    this.decimalPattern = (this.floatPattern = Pattern.compile(str9 + "|" + str10 + "|" + str13)).compile(str9);
  }
  
  private Pattern floatPattern() {
    if (this.floatPattern == null)
      buildFloatAndDecimalPattern(); 
    return this.floatPattern;
  }
  
  private Pattern decimalPattern() {
    if (this.decimalPattern == null)
      buildFloatAndDecimalPattern(); 
    return this.decimalPattern;
  }
  
  private Scanner(Readable paramReadable, Pattern paramPattern) {
    assert paramReadable != null : "source should not be null";
    assert paramPattern != null : "pattern should not be null";
    this.source = paramReadable;
    this.delimPattern = paramPattern;
    this.buf = CharBuffer.allocate(1024);
    this.buf.limit(0);
    this.matcher = this.delimPattern.matcher(this.buf);
    this.matcher.useTransparentBounds(true);
    this.matcher.useAnchoringBounds(false);
    useLocale(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public Scanner(Readable paramReadable) { this((Readable)Objects.requireNonNull(paramReadable, "source"), WHITESPACE_PATTERN); }
  
  public Scanner(InputStream paramInputStream) { this(new InputStreamReader(paramInputStream), WHITESPACE_PATTERN); }
  
  public Scanner(InputStream paramInputStream, String paramString) { this(makeReadable((InputStream)Objects.requireNonNull(paramInputStream, "source"), toCharset(paramString)), WHITESPACE_PATTERN); }
  
  private static Charset toCharset(String paramString) {
    Objects.requireNonNull(paramString, "charsetName");
    try {
      return Charset.forName(paramString);
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new IllegalArgumentException(illegalCharsetNameException);
    } 
  }
  
  private static Readable makeReadable(InputStream paramInputStream, Charset paramCharset) { return new InputStreamReader(paramInputStream, paramCharset); }
  
  public Scanner(File paramFile) throws FileNotFoundException { this((new FileInputStream(paramFile)).getChannel()); }
  
  public Scanner(File paramFile, String paramString) throws FileNotFoundException { this((File)Objects.requireNonNull(paramFile), toDecoder(paramString)); }
  
  private Scanner(File paramFile, CharsetDecoder paramCharsetDecoder) throws FileNotFoundException { this(makeReadable((new FileInputStream(paramFile)).getChannel(), paramCharsetDecoder)); }
  
  private static CharsetDecoder toDecoder(String paramString) {
    Objects.requireNonNull(paramString, "charsetName");
    try {
      return Charset.forName(paramString).newDecoder();
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new IllegalArgumentException(paramString);
    } 
  }
  
  private static Readable makeReadable(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder) { return Channels.newReader(paramReadableByteChannel, paramCharsetDecoder, -1); }
  
  public Scanner(Path paramPath) throws IOException { this(Files.newInputStream(paramPath, new java.nio.file.OpenOption[0])); }
  
  public Scanner(Path paramPath, String paramString) throws IOException { this((Path)Objects.requireNonNull(paramPath), toCharset(paramString)); }
  
  private Scanner(Path paramPath, Charset paramCharset) throws IOException { this(makeReadable(Files.newInputStream(paramPath, new java.nio.file.OpenOption[0]), paramCharset)); }
  
  public Scanner(String paramString) { this(new StringReader(paramString), WHITESPACE_PATTERN); }
  
  public Scanner(ReadableByteChannel paramReadableByteChannel) { this(makeReadable((ReadableByteChannel)Objects.requireNonNull(paramReadableByteChannel, "source")), WHITESPACE_PATTERN); }
  
  private static Readable makeReadable(ReadableByteChannel paramReadableByteChannel) { return makeReadable(paramReadableByteChannel, Charset.defaultCharset().newDecoder()); }
  
  public Scanner(ReadableByteChannel paramReadableByteChannel, String paramString) { this(makeReadable((ReadableByteChannel)Objects.requireNonNull(paramReadableByteChannel, "source"), toDecoder(paramString)), WHITESPACE_PATTERN); }
  
  private void saveState() { this.savedScannerPosition = this.position; }
  
  private void revertState() {
    this.position = this.savedScannerPosition;
    this.savedScannerPosition = -1;
    this.skipped = false;
  }
  
  private boolean revertState(boolean paramBoolean) {
    this.position = this.savedScannerPosition;
    this.savedScannerPosition = -1;
    this.skipped = false;
    return paramBoolean;
  }
  
  private void cacheResult() {
    this.hasNextResult = this.matcher.group();
    this.hasNextPosition = this.matcher.end();
    this.hasNextPattern = this.matcher.pattern();
  }
  
  private void cacheResult(String paramString) {
    this.hasNextResult = paramString;
    this.hasNextPosition = this.matcher.end();
    this.hasNextPattern = this.matcher.pattern();
  }
  
  private void clearCaches() {
    this.hasNextPattern = null;
    this.typeCache = null;
  }
  
  private String getCachedResult() {
    this.position = this.hasNextPosition;
    this.hasNextPattern = null;
    this.typeCache = null;
    return this.hasNextResult;
  }
  
  private void useTypeCache() {
    if (this.closed)
      throw new IllegalStateException("Scanner closed"); 
    this.position = this.hasNextPosition;
    this.hasNextPattern = null;
    this.typeCache = null;
  }
  
  private void readInput() {
    if (this.buf.limit() == this.buf.capacity())
      makeSpace(); 
    int i = this.buf.position();
    this.buf.position(this.buf.limit());
    this.buf.limit(this.buf.capacity());
    int j = 0;
    try {
      j = this.source.read(this.buf);
    } catch (IOException iOException) {
      this.lastException = iOException;
      j = -1;
    } 
    if (j == -1) {
      this.sourceClosed = true;
      this.needInput = false;
    } 
    if (j > 0)
      this.needInput = false; 
    this.buf.limit(this.buf.position());
    this.buf.position(i);
  }
  
  private boolean makeSpace() {
    clearCaches();
    int i = (this.savedScannerPosition == -1) ? this.position : this.savedScannerPosition;
    this.buf.position(i);
    if (i > 0) {
      this.buf.compact();
      translateSavedIndexes(i);
      this.position -= i;
      this.buf.flip();
      return true;
    } 
    int j = this.buf.capacity() * 2;
    CharBuffer charBuffer = CharBuffer.allocate(j);
    charBuffer.put(this.buf);
    charBuffer.flip();
    translateSavedIndexes(i);
    this.position -= i;
    this.buf = charBuffer;
    this.matcher.reset(this.buf);
    return true;
  }
  
  private void translateSavedIndexes(int paramInt) {
    if (this.savedScannerPosition != -1)
      this.savedScannerPosition -= paramInt; 
  }
  
  private void throwFor() {
    this.skipped = false;
    if (this.sourceClosed && this.position == this.buf.limit())
      throw new NoSuchElementException(); 
    throw new InputMismatchException();
  }
  
  private boolean hasTokenInBuffer() {
    this.matchValid = false;
    this.matcher.usePattern(this.delimPattern);
    this.matcher.region(this.position, this.buf.limit());
    if (this.matcher.lookingAt())
      this.position = this.matcher.end(); 
    return !(this.position == this.buf.limit());
  }
  
  private String getCompleteTokenInBuffer(Pattern paramPattern) {
    this.matchValid = false;
    this.matcher.usePattern(this.delimPattern);
    if (!this.skipped) {
      this.matcher.region(this.position, this.buf.limit());
      if (this.matcher.lookingAt()) {
        if (this.matcher.hitEnd() && !this.sourceClosed) {
          this.needInput = true;
          return null;
        } 
        this.skipped = true;
        this.position = this.matcher.end();
      } 
    } 
    if (this.position == this.buf.limit()) {
      if (this.sourceClosed)
        return null; 
      this.needInput = true;
      return null;
    } 
    this.matcher.region(this.position, this.buf.limit());
    boolean bool = this.matcher.find();
    if (bool && this.matcher.end() == this.position)
      bool = this.matcher.find(); 
    if (bool) {
      if (this.matcher.requireEnd() && !this.sourceClosed) {
        this.needInput = true;
        return null;
      } 
      int i = this.matcher.start();
      if (paramPattern == null)
        paramPattern = FIND_ANY_PATTERN; 
      this.matcher.usePattern(paramPattern);
      this.matcher.region(this.position, i);
      if (this.matcher.matches()) {
        String str = this.matcher.group();
        this.position = this.matcher.end();
        return str;
      } 
      return null;
    } 
    if (this.sourceClosed) {
      if (paramPattern == null)
        paramPattern = FIND_ANY_PATTERN; 
      this.matcher.usePattern(paramPattern);
      this.matcher.region(this.position, this.buf.limit());
      if (this.matcher.matches()) {
        String str = this.matcher.group();
        this.position = this.matcher.end();
        return str;
      } 
      return null;
    } 
    this.needInput = true;
    return null;
  }
  
  private String findPatternInBuffer(Pattern paramPattern, int paramInt) {
    this.matchValid = false;
    this.matcher.usePattern(paramPattern);
    int i = this.buf.limit();
    int j = -1;
    int k = i;
    if (paramInt > 0) {
      j = this.position + paramInt;
      if (j < i)
        k = j; 
    } 
    this.matcher.region(this.position, k);
    if (this.matcher.find()) {
      if (this.matcher.hitEnd() && !this.sourceClosed) {
        if (k != j) {
          this.needInput = true;
          return null;
        } 
        if (k == j && this.matcher.requireEnd()) {
          this.needInput = true;
          return null;
        } 
      } 
      this.position = this.matcher.end();
      return this.matcher.group();
    } 
    if (this.sourceClosed)
      return null; 
    if (paramInt == 0 || k != j)
      this.needInput = true; 
    return null;
  }
  
  private String matchPatternInBuffer(Pattern paramPattern) {
    this.matchValid = false;
    this.matcher.usePattern(paramPattern);
    this.matcher.region(this.position, this.buf.limit());
    if (this.matcher.lookingAt()) {
      if (this.matcher.hitEnd() && !this.sourceClosed) {
        this.needInput = true;
        return null;
      } 
      this.position = this.matcher.end();
      return this.matcher.group();
    } 
    if (this.sourceClosed)
      return null; 
    this.needInput = true;
    return null;
  }
  
  private void ensureOpen() {
    if (this.closed)
      throw new IllegalStateException("Scanner closed"); 
  }
  
  public void close() {
    if (this.closed)
      return; 
    if (this.source instanceof Closeable)
      try {
        ((Closeable)this.source).close();
      } catch (IOException iOException) {
        this.lastException = iOException;
      }  
    this.sourceClosed = true;
    this.source = null;
    this.closed = true;
  }
  
  public IOException ioException() { return this.lastException; }
  
  public Pattern delimiter() { return this.delimPattern; }
  
  public Scanner useDelimiter(Pattern paramPattern) {
    this.delimPattern = paramPattern;
    return this;
  }
  
  public Scanner useDelimiter(String paramString) {
    this.delimPattern = (Pattern)this.patternCache.forName(paramString);
    return this;
  }
  
  public Locale locale() { return this.locale; }
  
  public Scanner useLocale(Locale paramLocale) {
    if (paramLocale.equals(this.locale))
      return this; 
    this.locale = paramLocale;
    DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(paramLocale);
    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
    this.groupSeparator = "\\" + decimalFormatSymbols.getGroupingSeparator();
    this.decimalSeparator = "\\" + decimalFormatSymbols.getDecimalSeparator();
    this.nanString = "\\Q" + decimalFormatSymbols.getNaN() + "\\E";
    this.infinityString = "\\Q" + decimalFormatSymbols.getInfinity() + "\\E";
    this.positivePrefix = decimalFormat.getPositivePrefix();
    if (this.positivePrefix.length() > 0)
      this.positivePrefix = "\\Q" + this.positivePrefix + "\\E"; 
    this.negativePrefix = decimalFormat.getNegativePrefix();
    if (this.negativePrefix.length() > 0)
      this.negativePrefix = "\\Q" + this.negativePrefix + "\\E"; 
    this.positiveSuffix = decimalFormat.getPositiveSuffix();
    if (this.positiveSuffix.length() > 0)
      this.positiveSuffix = "\\Q" + this.positiveSuffix + "\\E"; 
    this.negativeSuffix = decimalFormat.getNegativeSuffix();
    if (this.negativeSuffix.length() > 0)
      this.negativeSuffix = "\\Q" + this.negativeSuffix + "\\E"; 
    this.integerPattern = null;
    this.floatPattern = null;
    return this;
  }
  
  public int radix() { return this.defaultRadix; }
  
  public Scanner useRadix(int paramInt) {
    if (paramInt < 2 || paramInt > 36)
      throw new IllegalArgumentException("radix:" + paramInt); 
    if (this.defaultRadix == paramInt)
      return this; 
    this.defaultRadix = paramInt;
    this.integerPattern = null;
    return this;
  }
  
  private void setRadix(int paramInt) {
    if (this.radix != paramInt) {
      this.integerPattern = null;
      this.radix = paramInt;
    } 
  }
  
  public MatchResult match() {
    if (!this.matchValid)
      throw new IllegalStateException("No match result available"); 
    return this.matcher.toMatchResult();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("java.util.Scanner");
    stringBuilder.append("[delimiters=" + this.delimPattern + "]");
    stringBuilder.append("[position=" + this.position + "]");
    stringBuilder.append("[match valid=" + this.matchValid + "]");
    stringBuilder.append("[need input=" + this.needInput + "]");
    stringBuilder.append("[source closed=" + this.sourceClosed + "]");
    stringBuilder.append("[skipped=" + this.skipped + "]");
    stringBuilder.append("[group separator=" + this.groupSeparator + "]");
    stringBuilder.append("[decimal separator=" + this.decimalSeparator + "]");
    stringBuilder.append("[positive prefix=" + this.positivePrefix + "]");
    stringBuilder.append("[negative prefix=" + this.negativePrefix + "]");
    stringBuilder.append("[positive suffix=" + this.positiveSuffix + "]");
    stringBuilder.append("[negative suffix=" + this.negativeSuffix + "]");
    stringBuilder.append("[NaN string=" + this.nanString + "]");
    stringBuilder.append("[infinity string=" + this.infinityString + "]");
    return stringBuilder.toString();
  }
  
  public boolean hasNext() {
    ensureOpen();
    saveState();
    while (!this.sourceClosed) {
      if (hasTokenInBuffer())
        return revertState(true); 
      readInput();
    } 
    boolean bool = hasTokenInBuffer();
    return revertState(bool);
  }
  
  public String next() {
    ensureOpen();
    clearCaches();
    while (true) {
      String str = getCompleteTokenInBuffer(null);
      if (str != null) {
        this.matchValid = true;
        this.skipped = false;
        return str;
      } 
      if (this.needInput) {
        readInput();
        continue;
      } 
      throwFor();
    } 
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
  
  public boolean hasNext(String paramString) { return hasNext((Pattern)this.patternCache.forName(paramString)); }
  
  public String next(String paramString) { return next((Pattern)this.patternCache.forName(paramString)); }
  
  public boolean hasNext(Pattern paramPattern) {
    ensureOpen();
    if (paramPattern == null)
      throw new NullPointerException(); 
    this.hasNextPattern = null;
    saveState();
    while (true) {
      if (getCompleteTokenInBuffer(paramPattern) != null) {
        this.matchValid = true;
        cacheResult();
        return revertState(true);
      } 
      if (this.needInput) {
        readInput();
        continue;
      } 
      break;
    } 
    return revertState(false);
  }
  
  public String next(Pattern paramPattern) {
    ensureOpen();
    if (paramPattern == null)
      throw new NullPointerException(); 
    if (this.hasNextPattern == paramPattern)
      return getCachedResult(); 
    clearCaches();
    while (true) {
      String str = getCompleteTokenInBuffer(paramPattern);
      if (str != null) {
        this.matchValid = true;
        this.skipped = false;
        return str;
      } 
      if (this.needInput) {
        readInput();
        continue;
      } 
      throwFor();
    } 
  }
  
  public boolean hasNextLine() {
    saveState();
    String str = findWithinHorizon(linePattern(), 0);
    if (str != null) {
      MatchResult matchResult = match();
      String str1 = matchResult.group(1);
      if (str1 != null) {
        str = str.substring(0, str.length() - str1.length());
        cacheResult(str);
      } else {
        cacheResult();
      } 
    } 
    revertState();
    return (str != null);
  }
  
  public String nextLine() {
    if (this.hasNextPattern == linePattern())
      return getCachedResult(); 
    clearCaches();
    String str1 = findWithinHorizon(linePattern, 0);
    if (str1 == null)
      throw new NoSuchElementException("No line found"); 
    MatchResult matchResult = match();
    String str2 = matchResult.group(1);
    if (str2 != null)
      str1 = str1.substring(0, str1.length() - str2.length()); 
    if (str1 == null)
      throw new NoSuchElementException(); 
    return str1;
  }
  
  public String findInLine(String paramString) { return findInLine((Pattern)this.patternCache.forName(paramString)); }
  
  public String findInLine(Pattern paramPattern) {
    ensureOpen();
    if (paramPattern == null)
      throw new NullPointerException(); 
    clearCaches();
    int i = 0;
    saveState();
    while (true) {
      String str = findPatternInBuffer(separatorPattern(), 0);
      if (str != null) {
        i = this.matcher.start();
        break;
      } 
      if (this.needInput) {
        readInput();
        continue;
      } 
      i = this.buf.limit();
      break;
    } 
    revertState();
    int j = i - this.position;
    return (j == 0) ? null : findWithinHorizon(paramPattern, j);
  }
  
  public String findWithinHorizon(String paramString, int paramInt) { return findWithinHorizon((Pattern)this.patternCache.forName(paramString), paramInt); }
  
  public String findWithinHorizon(Pattern paramPattern, int paramInt) {
    ensureOpen();
    if (paramPattern == null)
      throw new NullPointerException(); 
    if (paramInt < 0)
      throw new IllegalArgumentException("horizon < 0"); 
    clearCaches();
    while (true) {
      String str = findPatternInBuffer(paramPattern, paramInt);
      if (str != null) {
        this.matchValid = true;
        return str;
      } 
      if (this.needInput) {
        readInput();
        continue;
      } 
      break;
    } 
    return null;
  }
  
  public Scanner skip(Pattern paramPattern) {
    ensureOpen();
    if (paramPattern == null)
      throw new NullPointerException(); 
    clearCaches();
    while (true) {
      String str = matchPatternInBuffer(paramPattern);
      if (str != null) {
        this.matchValid = true;
        this.position = this.matcher.end();
        return this;
      } 
      if (this.needInput) {
        readInput();
        continue;
      } 
      break;
    } 
    throw new NoSuchElementException();
  }
  
  public Scanner skip(String paramString) { return skip((Pattern)this.patternCache.forName(paramString)); }
  
  public boolean hasNextBoolean() { return hasNext(boolPattern()); }
  
  public boolean nextBoolean() {
    clearCaches();
    return Boolean.parseBoolean(next(boolPattern()));
  }
  
  public boolean hasNextByte() { return hasNextByte(this.defaultRadix); }
  
  public boolean hasNextByte(int paramInt) {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool)
      try {
        String str = (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) ? processIntegerToken(this.hasNextResult) : this.hasNextResult;
        this.typeCache = Byte.valueOf(Byte.parseByte(str, paramInt));
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public byte nextByte() { return nextByte(this.defaultRadix); }
  
  public byte nextByte(int paramInt) {
    if (this.typeCache != null && this.typeCache instanceof Byte && this.radix == paramInt) {
      byte b = ((Byte)this.typeCache).byteValue();
      useTypeCache();
      return b;
    } 
    setRadix(paramInt);
    clearCaches();
    try {
      String str = next(integerPattern());
      if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null)
        str = processIntegerToken(str); 
      return Byte.parseByte(str, paramInt);
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public boolean hasNextShort() { return hasNextShort(this.defaultRadix); }
  
  public boolean hasNextShort(int paramInt) {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool)
      try {
        String str = (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) ? processIntegerToken(this.hasNextResult) : this.hasNextResult;
        this.typeCache = Short.valueOf(Short.parseShort(str, paramInt));
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public short nextShort() { return nextShort(this.defaultRadix); }
  
  public short nextShort(int paramInt) {
    if (this.typeCache != null && this.typeCache instanceof Short && this.radix == paramInt) {
      short s = ((Short)this.typeCache).shortValue();
      useTypeCache();
      return s;
    } 
    setRadix(paramInt);
    clearCaches();
    try {
      String str = next(integerPattern());
      if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null)
        str = processIntegerToken(str); 
      return Short.parseShort(str, paramInt);
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public boolean hasNextInt() { return hasNextInt(this.defaultRadix); }
  
  public boolean hasNextInt(int paramInt) {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool)
      try {
        String str = (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) ? processIntegerToken(this.hasNextResult) : this.hasNextResult;
        this.typeCache = Integer.valueOf(Integer.parseInt(str, paramInt));
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  private String processIntegerToken(String paramString) {
    String str = paramString.replaceAll("" + this.groupSeparator, "");
    boolean bool = false;
    int i = this.negativePrefix.length();
    if (i > 0 && str.startsWith(this.negativePrefix)) {
      bool = true;
      str = str.substring(i);
    } 
    int j = this.negativeSuffix.length();
    if (j > 0 && str.endsWith(this.negativeSuffix)) {
      bool = true;
      str = str.substring(str.length() - j, str.length());
    } 
    if (bool)
      str = "-" + str; 
    return str;
  }
  
  public int nextInt() { return nextInt(this.defaultRadix); }
  
  public int nextInt(int paramInt) {
    if (this.typeCache != null && this.typeCache instanceof Integer && this.radix == paramInt) {
      int i = ((Integer)this.typeCache).intValue();
      useTypeCache();
      return i;
    } 
    setRadix(paramInt);
    clearCaches();
    try {
      String str = next(integerPattern());
      if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null)
        str = processIntegerToken(str); 
      return Integer.parseInt(str, paramInt);
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public boolean hasNextLong() { return hasNextLong(this.defaultRadix); }
  
  public boolean hasNextLong(int paramInt) {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool)
      try {
        String str = (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) ? processIntegerToken(this.hasNextResult) : this.hasNextResult;
        this.typeCache = Long.valueOf(Long.parseLong(str, paramInt));
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public long nextLong() { return nextLong(this.defaultRadix); }
  
  public long nextLong(int paramInt) {
    if (this.typeCache != null && this.typeCache instanceof Long && this.radix == paramInt) {
      long l = ((Long)this.typeCache).longValue();
      useTypeCache();
      return l;
    } 
    setRadix(paramInt);
    clearCaches();
    try {
      String str = next(integerPattern());
      if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null)
        str = processIntegerToken(str); 
      return Long.parseLong(str, paramInt);
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  private String processFloatToken(String paramString) {
    String str = paramString.replaceAll(this.groupSeparator, "");
    if (!this.decimalSeparator.equals("\\."))
      str = str.replaceAll(this.decimalSeparator, "."); 
    boolean bool = false;
    int i = this.negativePrefix.length();
    if (i > 0 && str.startsWith(this.negativePrefix)) {
      bool = true;
      str = str.substring(i);
    } 
    int j = this.negativeSuffix.length();
    if (j > 0 && str.endsWith(this.negativeSuffix)) {
      bool = true;
      str = str.substring(str.length() - j, str.length());
    } 
    if (str.equals(this.nanString))
      str = "NaN"; 
    if (str.equals(this.infinityString))
      str = "Infinity"; 
    if (bool)
      str = "-" + str; 
    Matcher matcher1 = NON_ASCII_DIGIT.matcher(str);
    if (matcher1.find()) {
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < str.length(); b++) {
        char c = str.charAt(b);
        if (Character.isDigit(c)) {
          int k = Character.digit(c, 10);
          if (k != -1) {
            stringBuilder.append(k);
          } else {
            stringBuilder.append(c);
          } 
        } else {
          stringBuilder.append(c);
        } 
      } 
      str = stringBuilder.toString();
    } 
    return str;
  }
  
  public boolean hasNextFloat() {
    setRadix(10);
    boolean bool = hasNext(floatPattern());
    if (bool)
      try {
        String str = processFloatToken(this.hasNextResult);
        this.typeCache = Float.valueOf(Float.parseFloat(str));
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public float nextFloat() {
    if (this.typeCache != null && this.typeCache instanceof Float) {
      float f = ((Float)this.typeCache).floatValue();
      useTypeCache();
      return f;
    } 
    setRadix(10);
    clearCaches();
    try {
      return Float.parseFloat(processFloatToken(next(floatPattern())));
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public boolean hasNextDouble() {
    setRadix(10);
    boolean bool = hasNext(floatPattern());
    if (bool)
      try {
        String str = processFloatToken(this.hasNextResult);
        this.typeCache = Double.valueOf(Double.parseDouble(str));
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public double nextDouble() {
    if (this.typeCache != null && this.typeCache instanceof Double) {
      double d = ((Double)this.typeCache).doubleValue();
      useTypeCache();
      return d;
    } 
    setRadix(10);
    clearCaches();
    try {
      return Double.parseDouble(processFloatToken(next(floatPattern())));
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public boolean hasNextBigInteger() { return hasNextBigInteger(this.defaultRadix); }
  
  public boolean hasNextBigInteger(int paramInt) {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool)
      try {
        String str = (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) ? processIntegerToken(this.hasNextResult) : this.hasNextResult;
        this.typeCache = new BigInteger(str, paramInt);
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public BigInteger nextBigInteger() { return nextBigInteger(this.defaultRadix); }
  
  public BigInteger nextBigInteger(int paramInt) {
    if (this.typeCache != null && this.typeCache instanceof BigInteger && this.radix == paramInt) {
      BigInteger bigInteger = (BigInteger)this.typeCache;
      useTypeCache();
      return bigInteger;
    } 
    setRadix(paramInt);
    clearCaches();
    try {
      String str = next(integerPattern());
      if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null)
        str = processIntegerToken(str); 
      return new BigInteger(str, paramInt);
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public boolean hasNextBigDecimal() {
    setRadix(10);
    boolean bool = hasNext(decimalPattern());
    if (bool)
      try {
        String str = processFloatToken(this.hasNextResult);
        this.typeCache = new BigDecimal(str);
      } catch (NumberFormatException numberFormatException) {
        bool = false;
      }  
    return bool;
  }
  
  public BigDecimal nextBigDecimal() {
    if (this.typeCache != null && this.typeCache instanceof BigDecimal) {
      BigDecimal bigDecimal = (BigDecimal)this.typeCache;
      useTypeCache();
      return bigDecimal;
    } 
    setRadix(10);
    clearCaches();
    try {
      String str = processFloatToken(next(decimalPattern()));
      return new BigDecimal(str);
    } catch (NumberFormatException numberFormatException) {
      this.position = this.matcher.start();
      throw new InputMismatchException(numberFormatException.getMessage());
    } 
  }
  
  public Scanner reset() {
    this.delimPattern = WHITESPACE_PATTERN;
    useLocale(Locale.getDefault(Locale.Category.FORMAT));
    useRadix(10);
    clearCaches();
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Scanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */