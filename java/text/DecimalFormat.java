package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;

public class DecimalFormat extends NumberFormat {
  private BigInteger bigIntegerMultiplier;
  
  private BigDecimal bigDecimalMultiplier;
  
  private static final int STATUS_INFINITE = 0;
  
  private static final int STATUS_POSITIVE = 1;
  
  private static final int STATUS_LENGTH = 2;
  
  private DigitList digitList = new DigitList();
  
  private String positivePrefix = "";
  
  private String positiveSuffix = "";
  
  private String negativePrefix = "-";
  
  private String negativeSuffix = "";
  
  private String posPrefixPattern;
  
  private String posSuffixPattern;
  
  private String negPrefixPattern;
  
  private String negSuffixPattern;
  
  private int multiplier = 1;
  
  private byte groupingSize = 3;
  
  private boolean decimalSeparatorAlwaysShown = false;
  
  private boolean parseBigDecimal = false;
  
  private boolean isCurrencyFormat = false;
  
  private DecimalFormatSymbols symbols = null;
  
  private boolean useExponentialNotation;
  
  private FieldPosition[] positivePrefixFieldPositions;
  
  private FieldPosition[] positiveSuffixFieldPositions;
  
  private FieldPosition[] negativePrefixFieldPositions;
  
  private FieldPosition[] negativeSuffixFieldPositions;
  
  private byte minExponentDigits;
  
  private int maximumIntegerDigits = super.getMaximumIntegerDigits();
  
  private int minimumIntegerDigits = super.getMinimumIntegerDigits();
  
  private int maximumFractionDigits = super.getMaximumFractionDigits();
  
  private int minimumFractionDigits = super.getMinimumFractionDigits();
  
  private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
  
  private boolean isFastPath = false;
  
  private boolean fastPathCheckNeeded = true;
  
  private FastPathData fastPathData;
  
  static final int currentSerialVersion = 4;
  
  private int serialVersionOnStream = 4;
  
  private static final double MAX_INT_AS_DOUBLE = 2.147483647E9D;
  
  private static final char PATTERN_ZERO_DIGIT = '0';
  
  private static final char PATTERN_GROUPING_SEPARATOR = ',';
  
  private static final char PATTERN_DECIMAL_SEPARATOR = '.';
  
  private static final char PATTERN_PER_MILLE = '‰';
  
  private static final char PATTERN_PERCENT = '%';
  
  private static final char PATTERN_DIGIT = '#';
  
  private static final char PATTERN_SEPARATOR = ';';
  
  private static final String PATTERN_EXPONENT = "E";
  
  private static final char PATTERN_MINUS = '-';
  
  private static final char CURRENCY_SIGN = '¤';
  
  private static final char QUOTE = '\'';
  
  private static FieldPosition[] EmptyFieldPositionArray = new FieldPosition[0];
  
  static final int DOUBLE_INTEGER_DIGITS = 309;
  
  static final int DOUBLE_FRACTION_DIGITS = 340;
  
  static final int MAXIMUM_INTEGER_DIGITS = 2147483647;
  
  static final int MAXIMUM_FRACTION_DIGITS = 2147483647;
  
  static final long serialVersionUID = 864413376551465018L;
  
  public DecimalFormat() {
    Locale locale = Locale.getDefault(Locale.Category.FORMAT);
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(java.text.spi.NumberFormatProvider.class, locale);
    if (!(localeProviderAdapter instanceof sun.util.locale.provider.ResourceBundleBasedAdapter))
      localeProviderAdapter = LocaleProviderAdapter.getResourceBundleBased(); 
    String[] arrayOfString = localeProviderAdapter.getLocaleResources(locale).getNumberPatterns();
    this.symbols = DecimalFormatSymbols.getInstance(locale);
    applyPattern(arrayOfString[0], false);
  }
  
  public DecimalFormat(String paramString) {
    this.symbols = DecimalFormatSymbols.getInstance(Locale.getDefault(Locale.Category.FORMAT));
    applyPattern(paramString, false);
  }
  
  public DecimalFormat(String paramString, DecimalFormatSymbols paramDecimalFormatSymbols) {
    this.symbols = (DecimalFormatSymbols)paramDecimalFormatSymbols.clone();
    applyPattern(paramString, false);
  }
  
  public final StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    if (paramObject instanceof Long || paramObject instanceof Integer || paramObject instanceof Short || paramObject instanceof Byte || paramObject instanceof java.util.concurrent.atomic.AtomicInteger || paramObject instanceof java.util.concurrent.atomic.AtomicLong || (paramObject instanceof BigInteger && ((BigInteger)paramObject).bitLength() < 64))
      return format(((Number)paramObject).longValue(), paramStringBuffer, paramFieldPosition); 
    if (paramObject instanceof BigDecimal)
      return format((BigDecimal)paramObject, paramStringBuffer, paramFieldPosition); 
    if (paramObject instanceof BigInteger)
      return format((BigInteger)paramObject, paramStringBuffer, paramFieldPosition); 
    if (paramObject instanceof Number)
      return format(((Number)paramObject).doubleValue(), paramStringBuffer, paramFieldPosition); 
    throw new IllegalArgumentException("Cannot format given Object as a Number");
  }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    boolean bool = false;
    if (paramFieldPosition == DontCareFieldPosition.INSTANCE) {
      bool = true;
    } else {
      paramFieldPosition.setBeginIndex(0);
      paramFieldPosition.setEndIndex(0);
    } 
    if (bool) {
      String str = fastFormat(paramDouble);
      if (str != null) {
        paramStringBuffer.append(str);
        return paramStringBuffer;
      } 
    } 
    return format(paramDouble, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate) {
    if (Double.isNaN(paramDouble) || (Double.isInfinite(paramDouble) && this.multiplier == 0)) {
      int i = paramStringBuffer.length();
      paramStringBuffer.append(this.symbols.getNaN());
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, i, paramStringBuffer.length(), paramStringBuffer);
      return paramStringBuffer;
    } 
    boolean bool = ((paramDouble < 0.0D || (paramDouble == 0.0D && 1.0D / paramDouble < 0.0D)) ? 1 : 0) ^ ((this.multiplier < 0) ? 1 : 0);
    if (this.multiplier != 1)
      paramDouble *= this.multiplier; 
    if (Double.isInfinite(paramDouble)) {
      if (bool) {
        append(paramStringBuffer, this.negativePrefix, paramFieldDelegate, getNegativePrefixFieldPositions(), NumberFormat.Field.SIGN);
      } else {
        append(paramStringBuffer, this.positivePrefix, paramFieldDelegate, getPositivePrefixFieldPositions(), NumberFormat.Field.SIGN);
      } 
      int i = paramStringBuffer.length();
      paramStringBuffer.append(this.symbols.getInfinity());
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, i, paramStringBuffer.length(), paramStringBuffer);
      if (bool) {
        append(paramStringBuffer, this.negativeSuffix, paramFieldDelegate, getNegativeSuffixFieldPositions(), NumberFormat.Field.SIGN);
      } else {
        append(paramStringBuffer, this.positiveSuffix, paramFieldDelegate, getPositiveSuffixFieldPositions(), NumberFormat.Field.SIGN);
      } 
      return paramStringBuffer;
    } 
    if (bool)
      paramDouble = -paramDouble; 
    assert paramDouble >= 0.0D && !Double.isInfinite(paramDouble);
    synchronized (this.digitList) {
      int i = super.getMaximumIntegerDigits();
      int j = super.getMinimumIntegerDigits();
      int k = super.getMaximumFractionDigits();
      int m = super.getMinimumFractionDigits();
      this.digitList.set(bool, paramDouble, this.useExponentialNotation ? (i + k) : k, !this.useExponentialNotation);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, false, i, j, k, m);
    } 
  }
  
  public StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    return format(paramLong, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(long paramLong, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate) {
    boolean bool = (paramLong < 0L);
    if (bool)
      paramLong = -paramLong; 
    boolean bool1 = false;
    if (paramLong < 0L) {
      if (this.multiplier != 0)
        bool1 = true; 
    } else if (this.multiplier != 1 && this.multiplier != 0) {
      long l = Float.MAX_VALUE / this.multiplier;
      if (l < 0L)
        l = -l; 
      bool1 = (paramLong > l) ? 1 : 0;
    } 
    if (bool1) {
      if (bool)
        paramLong = -paramLong; 
      BigInteger bigInteger = BigInteger.valueOf(paramLong);
      return format(bigInteger, paramStringBuffer, paramFieldDelegate, true);
    } 
    paramLong *= this.multiplier;
    if (paramLong == 0L) {
      bool = false;
    } else if (this.multiplier < 0) {
      paramLong = -paramLong;
      bool = !bool;
    } 
    synchronized (this.digitList) {
      int i = super.getMaximumIntegerDigits();
      int j = super.getMinimumIntegerDigits();
      int k = super.getMaximumFractionDigits();
      int m = super.getMinimumFractionDigits();
      this.digitList.set(bool, paramLong, this.useExponentialNotation ? (i + k) : 0);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, true, i, j, k, m);
    } 
  }
  
  private StringBuffer format(BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    return format(paramBigDecimal, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate) {
    if (this.multiplier != 1)
      paramBigDecimal = paramBigDecimal.multiply(getBigDecimalMultiplier()); 
    boolean bool = (paramBigDecimal.signum() == -1);
    if (bool)
      paramBigDecimal = paramBigDecimal.negate(); 
    synchronized (this.digitList) {
      int i = getMaximumIntegerDigits();
      int j = getMinimumIntegerDigits();
      int k = getMaximumFractionDigits();
      int m = getMinimumFractionDigits();
      int n = i + k;
      this.digitList.set(bool, paramBigDecimal, this.useExponentialNotation ? ((n < 0) ? Integer.MAX_VALUE : n) : k, !this.useExponentialNotation);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, false, i, j, k, m);
    } 
  }
  
  private StringBuffer format(BigInteger paramBigInteger, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    return format(paramBigInteger, paramStringBuffer, paramFieldPosition.getFieldDelegate(), false);
  }
  
  private StringBuffer format(BigInteger paramBigInteger, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate, boolean paramBoolean) {
    if (this.multiplier != 1)
      paramBigInteger = paramBigInteger.multiply(getBigIntegerMultiplier()); 
    boolean bool = (paramBigInteger.signum() == -1);
    if (bool)
      paramBigInteger = paramBigInteger.negate(); 
    synchronized (this.digitList) {
      int n;
      int m;
      int k;
      int j;
      int i;
      if (paramBoolean) {
        i = super.getMaximumIntegerDigits();
        j = super.getMinimumIntegerDigits();
        k = super.getMaximumFractionDigits();
        m = super.getMinimumFractionDigits();
        n = i + k;
      } else {
        i = getMaximumIntegerDigits();
        j = getMinimumIntegerDigits();
        k = getMaximumFractionDigits();
        m = getMinimumFractionDigits();
        n = i + k;
        if (n < 0)
          n = Integer.MAX_VALUE; 
      } 
      this.digitList.set(bool, paramBigInteger, this.useExponentialNotation ? n : 0);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, true, i, j, k, m);
    } 
  }
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject) {
    CharacterIteratorFieldDelegate characterIteratorFieldDelegate = new CharacterIteratorFieldDelegate();
    StringBuffer stringBuffer = new StringBuffer();
    if (paramObject instanceof Double || paramObject instanceof Float) {
      format(((Number)paramObject).doubleValue(), stringBuffer, characterIteratorFieldDelegate);
    } else if (paramObject instanceof Long || paramObject instanceof Integer || paramObject instanceof Short || paramObject instanceof Byte || paramObject instanceof java.util.concurrent.atomic.AtomicInteger || paramObject instanceof java.util.concurrent.atomic.AtomicLong) {
      format(((Number)paramObject).longValue(), stringBuffer, characterIteratorFieldDelegate);
    } else if (paramObject instanceof BigDecimal) {
      format((BigDecimal)paramObject, stringBuffer, characterIteratorFieldDelegate);
    } else if (paramObject instanceof BigInteger) {
      format((BigInteger)paramObject, stringBuffer, characterIteratorFieldDelegate, false);
    } else {
      if (paramObject == null)
        throw new NullPointerException("formatToCharacterIterator must be passed non-null object"); 
      throw new IllegalArgumentException("Cannot format given Object as a Number");
    } 
    return characterIteratorFieldDelegate.getIterator(stringBuffer.toString());
  }
  
  private boolean checkAndSetFastPathStatus() {
    boolean bool = this.isFastPath;
    if (this.roundingMode == RoundingMode.HALF_EVEN && isGroupingUsed() && this.groupingSize == 3 && this.multiplier == 1 && !this.decimalSeparatorAlwaysShown && !this.useExponentialNotation) {
      this.isFastPath = (this.minimumIntegerDigits == 1 && this.maximumIntegerDigits >= 10);
      if (this.isFastPath)
        if (this.isCurrencyFormat) {
          if (this.minimumFractionDigits != 2 || this.maximumFractionDigits != 2)
            this.isFastPath = false; 
        } else if (this.minimumFractionDigits != 0 || this.maximumFractionDigits != 3) {
          this.isFastPath = false;
        }  
    } else {
      this.isFastPath = false;
    } 
    resetFastPathData(bool);
    this.fastPathCheckNeeded = false;
    return true;
  }
  
  private void resetFastPathData(boolean paramBoolean) {
    if (this.isFastPath) {
      if (this.fastPathData == null)
        this.fastPathData = new FastPathData(null); 
      this.fastPathData.zeroDelta = this.symbols.getZeroDigit() - '0';
      this.fastPathData.groupingChar = this.symbols.getGroupingSeparator();
      this.fastPathData.fractionalMaxIntBound = this.isCurrencyFormat ? 99 : 999;
      this.fastPathData.fractionalScaleFactor = this.isCurrencyFormat ? 100.0D : 1000.0D;
      this.fastPathData.positiveAffixesRequired = (this.positivePrefix.length() != 0 || this.positiveSuffix.length() != 0);
      this.fastPathData.negativeAffixesRequired = (this.negativePrefix.length() != 0 || this.negativeSuffix.length() != 0);
      int i = 10;
      int j = 3;
      int k = Math.max(this.positivePrefix.length(), this.negativePrefix.length()) + i + j + 1 + this.maximumFractionDigits + Math.max(this.positiveSuffix.length(), this.negativeSuffix.length());
      this.fastPathData.fastPathContainer = new char[k];
      this.fastPathData.charsPositiveSuffix = this.positiveSuffix.toCharArray();
      this.fastPathData.charsNegativeSuffix = this.negativeSuffix.toCharArray();
      this.fastPathData.charsPositivePrefix = this.positivePrefix.toCharArray();
      this.fastPathData.charsNegativePrefix = this.negativePrefix.toCharArray();
      int m = Math.max(this.positivePrefix.length(), this.negativePrefix.length());
      int n = i + j + m;
      this.fastPathData.integralLastIndex = n - 1;
      this.fastPathData.fractionalFirstIndex = n + 1;
      this.fastPathData.fastPathContainer[n] = this.isCurrencyFormat ? this.symbols.getMonetaryDecimalSeparator() : this.symbols.getDecimalSeparator();
    } else if (paramBoolean) {
      this.fastPathData.fastPathContainer = null;
      this.fastPathData.charsPositiveSuffix = null;
      this.fastPathData.charsNegativeSuffix = null;
      this.fastPathData.charsPositivePrefix = null;
      this.fastPathData.charsNegativePrefix = null;
    } 
  }
  
  private boolean exactRoundUp(double paramDouble, int paramInt) {
    double d3;
    double d2;
    double d1;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    if (this.isCurrencyFormat) {
      d1 = paramDouble * 128.0D;
      d2 = -(paramDouble * 32.0D);
      d3 = paramDouble * 4.0D;
    } else {
      d1 = paramDouble * 1024.0D;
      d2 = -(paramDouble * 16.0D);
      d3 = -(paramDouble * 8.0D);
    } 
    assert -d2 >= Math.abs(d3);
    d4 = d2 + d3;
    d6 = d4 - d2;
    d5 = d3 - d6;
    double d7 = d4;
    double d8 = d5;
    assert d1 >= Math.abs(d7);
    d4 = d1 + d7;
    d6 = d4 - d1;
    d5 = d7 - d6;
    double d9 = d5;
    double d10 = d4;
    double d11 = d8 + d9;
    assert d10 >= Math.abs(d11);
    d4 = d10 + d11;
    d6 = d4 - d10;
    double d12 = d11 - d6;
    return (d12 > 0.0D) ? true : ((d12 < 0.0D) ? false : (((paramInt & true) != 0)));
  }
  
  private void collectIntegralDigits(int paramInt1, char[] paramArrayOfChar, int paramInt2) {
    int i = paramInt2;
    while (paramInt1 > 999) {
      int j = paramInt1 / 1000;
      int k = paramInt1 - (j << 10) + (j << 4) + (j << 3);
      paramInt1 = j;
      paramArrayOfChar[i--] = DigitArrays.DigitOnes1000[k];
      paramArrayOfChar[i--] = DigitArrays.DigitTens1000[k];
      paramArrayOfChar[i--] = DigitArrays.DigitHundreds1000[k];
      paramArrayOfChar[i--] = this.fastPathData.groupingChar;
    } 
    paramArrayOfChar[i] = DigitArrays.DigitOnes1000[paramInt1];
    if (paramInt1 > 9) {
      paramArrayOfChar[--i] = DigitArrays.DigitTens1000[paramInt1];
      if (paramInt1 > 99)
        paramArrayOfChar[--i] = DigitArrays.DigitHundreds1000[paramInt1]; 
    } 
    this.fastPathData.firstUsedIndex = i;
  }
  
  private void collectFractionalDigits(int paramInt1, char[] paramArrayOfChar, int paramInt2) {
    int i = paramInt2;
    char c1 = DigitArrays.DigitOnes1000[paramInt1];
    char c2 = DigitArrays.DigitTens1000[paramInt1];
    if (this.isCurrencyFormat) {
      paramArrayOfChar[i++] = c2;
      paramArrayOfChar[i++] = c1;
    } else if (paramInt1 != 0) {
      paramArrayOfChar[i++] = DigitArrays.DigitHundreds1000[paramInt1];
      if (c1 != '0') {
        paramArrayOfChar[i++] = c2;
        paramArrayOfChar[i++] = c1;
      } else if (c2 != '0') {
        paramArrayOfChar[i++] = c2;
      } 
    } else {
      i--;
    } 
    this.fastPathData.lastFreeIndex = i;
  }
  
  private void addAffixes(char[] paramArrayOfChar1, char[] paramArrayOfChar2, char[] paramArrayOfChar3) {
    int i = paramArrayOfChar2.length;
    int j = paramArrayOfChar3.length;
    if (i != 0)
      prependPrefix(paramArrayOfChar2, i, paramArrayOfChar1); 
    if (j != 0)
      appendSuffix(paramArrayOfChar3, j, paramArrayOfChar1); 
  }
  
  private void prependPrefix(char[] paramArrayOfChar1, int paramInt, char[] paramArrayOfChar2) {
    this.fastPathData.firstUsedIndex -= paramInt;
    int i = this.fastPathData.firstUsedIndex;
    if (paramInt == 1) {
      paramArrayOfChar2[i] = paramArrayOfChar1[0];
    } else if (paramInt <= 4) {
      int j = i;
      int k = j + paramInt - 1;
      int m = paramInt - 1;
      paramArrayOfChar2[j] = paramArrayOfChar1[0];
      paramArrayOfChar2[k] = paramArrayOfChar1[m];
      if (paramInt > 2)
        paramArrayOfChar2[++j] = paramArrayOfChar1[1]; 
      if (paramInt == 4)
        paramArrayOfChar2[--k] = paramArrayOfChar1[2]; 
    } else {
      System.arraycopy(paramArrayOfChar1, 0, paramArrayOfChar2, i, paramInt);
    } 
  }
  
  private void appendSuffix(char[] paramArrayOfChar1, int paramInt, char[] paramArrayOfChar2) {
    int i = this.fastPathData.lastFreeIndex;
    if (paramInt == 1) {
      paramArrayOfChar2[i] = paramArrayOfChar1[0];
    } else if (paramInt <= 4) {
      int j = i;
      int k = j + paramInt - 1;
      int m = paramInt - 1;
      paramArrayOfChar2[j] = paramArrayOfChar1[0];
      paramArrayOfChar2[k] = paramArrayOfChar1[m];
      if (paramInt > 2)
        paramArrayOfChar2[++j] = paramArrayOfChar1[1]; 
      if (paramInt == 4)
        paramArrayOfChar2[--k] = paramArrayOfChar1[2]; 
    } else {
      System.arraycopy(paramArrayOfChar1, 0, paramArrayOfChar2, i, paramInt);
    } 
    this.fastPathData.lastFreeIndex += paramInt;
  }
  
  private void localizeDigits(char[] paramArrayOfChar) {
    int i = this.fastPathData.lastFreeIndex - this.fastPathData.fractionalFirstIndex;
    if (i < 0)
      i = this.groupingSize; 
    for (int j = this.fastPathData.lastFreeIndex - 1; j >= this.fastPathData.firstUsedIndex; j--) {
      if (i != 0) {
        paramArrayOfChar[j] = (char)(paramArrayOfChar[j] + this.fastPathData.zeroDelta);
        i--;
      } else {
        i = this.groupingSize;
      } 
    } 
  }
  
  private void fastDoubleFormat(double paramDouble, boolean paramBoolean) {
    char[] arrayOfChar = this.fastPathData.fastPathContainer;
    int i = (int)paramDouble;
    double d1 = paramDouble - i;
    double d2 = d1 * this.fastPathData.fractionalScaleFactor;
    int j = (int)d2;
    d2 -= j;
    boolean bool = false;
    if (d2 >= 0.5D) {
      if (d2 == 0.5D) {
        bool = exactRoundUp(d1, j);
      } else {
        bool = true;
      } 
      if (bool)
        if (j < this.fastPathData.fractionalMaxIntBound) {
          j++;
        } else {
          j = 0;
          i++;
        }  
    } 
    collectFractionalDigits(j, arrayOfChar, this.fastPathData.fractionalFirstIndex);
    collectIntegralDigits(i, arrayOfChar, this.fastPathData.integralLastIndex);
    if (this.fastPathData.zeroDelta != 0)
      localizeDigits(arrayOfChar); 
    if (paramBoolean) {
      if (this.fastPathData.negativeAffixesRequired)
        addAffixes(arrayOfChar, this.fastPathData.charsNegativePrefix, this.fastPathData.charsNegativeSuffix); 
    } else if (this.fastPathData.positiveAffixesRequired) {
      addAffixes(arrayOfChar, this.fastPathData.charsPositivePrefix, this.fastPathData.charsPositiveSuffix);
    } 
  }
  
  String fastFormat(double paramDouble) {
    boolean bool1 = false;
    if (this.fastPathCheckNeeded)
      bool1 = checkAndSetFastPathStatus(); 
    if (!this.isFastPath)
      return null; 
    if (!Double.isFinite(paramDouble))
      return null; 
    boolean bool2 = false;
    if (paramDouble < 0.0D) {
      bool2 = true;
      paramDouble = -paramDouble;
    } else if (paramDouble == 0.0D) {
      bool2 = (Math.copySign(1.0D, paramDouble) == -1.0D);
      paramDouble = 0.0D;
    } 
    if (paramDouble > 2.147483647E9D)
      return null; 
    if (!bool1)
      resetFastPathData(this.isFastPath); 
    fastDoubleFormat(paramDouble, bool2);
    return new String(this.fastPathData.fastPathContainer, this.fastPathData.firstUsedIndex, this.fastPathData.lastFreeIndex - this.fastPathData.firstUsedIndex);
  }
  
  private StringBuffer subformat(StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    char c1 = this.symbols.getZeroDigit();
    char c2 = c1 - '0';
    char c3 = this.symbols.getGroupingSeparator();
    char c4 = this.isCurrencyFormat ? this.symbols.getMonetaryDecimalSeparator() : this.symbols.getDecimalSeparator();
    if (this.digitList.isZero())
      this.digitList.decimalAt = 0; 
    if (paramBoolean1) {
      append(paramStringBuffer, this.negativePrefix, paramFieldDelegate, getNegativePrefixFieldPositions(), NumberFormat.Field.SIGN);
    } else {
      append(paramStringBuffer, this.positivePrefix, paramFieldDelegate, getPositivePrefixFieldPositions(), NumberFormat.Field.SIGN);
    } 
    if (this.useExponentialNotation) {
      int i = paramStringBuffer.length();
      int j = -1;
      int k = -1;
      int m = this.digitList.decimalAt;
      int n = paramInt1;
      int i1 = paramInt2;
      if (n > 1 && n > paramInt2) {
        if (m >= 1) {
          m = (m - 1) / n * n;
        } else {
          m = (m - n) / n * n;
        } 
        i1 = 1;
      } else {
        m -= i1;
      } 
      int i2 = paramInt2 + paramInt4;
      if (i2 < 0)
        i2 = Integer.MAX_VALUE; 
      int i3 = this.digitList.isZero() ? i1 : (this.digitList.decimalAt - m);
      if (i2 < i3)
        i2 = i3; 
      int i4 = this.digitList.count;
      if (i2 > i4)
        i4 = i2; 
      boolean bool = false;
      int i5;
      for (i5 = 0; i5 < i4; i5++) {
        if (i5 == i3) {
          j = paramStringBuffer.length();
          paramStringBuffer.append(c4);
          bool = true;
          k = paramStringBuffer.length();
        } 
        paramStringBuffer.append((i5 < this.digitList.count) ? (char)(this.digitList.digits[i5] + c2) : c1);
      } 
      if (this.decimalSeparatorAlwaysShown && i4 == i3) {
        j = paramStringBuffer.length();
        paramStringBuffer.append(c4);
        bool = true;
        k = paramStringBuffer.length();
      } 
      if (j == -1)
        j = paramStringBuffer.length(); 
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, i, j, paramStringBuffer);
      if (bool)
        paramFieldDelegate.formatted(NumberFormat.Field.DECIMAL_SEPARATOR, NumberFormat.Field.DECIMAL_SEPARATOR, j, k, paramStringBuffer); 
      if (k == -1)
        k = paramStringBuffer.length(); 
      paramFieldDelegate.formatted(1, NumberFormat.Field.FRACTION, NumberFormat.Field.FRACTION, k, paramStringBuffer.length(), paramStringBuffer);
      i5 = paramStringBuffer.length();
      paramStringBuffer.append(this.symbols.getExponentSeparator());
      paramFieldDelegate.formatted(NumberFormat.Field.EXPONENT_SYMBOL, NumberFormat.Field.EXPONENT_SYMBOL, i5, paramStringBuffer.length(), paramStringBuffer);
      if (this.digitList.isZero())
        m = 0; 
      boolean bool1 = (m < 0);
      if (bool1) {
        m = -m;
        i5 = paramStringBuffer.length();
        paramStringBuffer.append(this.symbols.getMinusSign());
        paramFieldDelegate.formatted(NumberFormat.Field.EXPONENT_SIGN, NumberFormat.Field.EXPONENT_SIGN, i5, paramStringBuffer.length(), paramStringBuffer);
      } 
      this.digitList.set(bool1, m);
      int i6 = paramStringBuffer.length();
      int i7;
      for (i7 = this.digitList.decimalAt; i7 < this.minExponentDigits; i7++)
        paramStringBuffer.append(c1); 
      for (i7 = 0; i7 < this.digitList.decimalAt; i7++)
        paramStringBuffer.append((i7 < this.digitList.count) ? (char)(this.digitList.digits[i7] + c2) : c1); 
      paramFieldDelegate.formatted(NumberFormat.Field.EXPONENT, NumberFormat.Field.EXPONENT, i6, paramStringBuffer.length(), paramStringBuffer);
    } else {
      int i = paramStringBuffer.length();
      int j = paramInt2;
      int k = 0;
      if (this.digitList.decimalAt > 0 && j < this.digitList.decimalAt)
        j = this.digitList.decimalAt; 
      if (j > paramInt1) {
        j = paramInt1;
        k = this.digitList.decimalAt - j;
      } 
      int m = paramStringBuffer.length();
      int n;
      for (n = j - 1; n >= 0; n--) {
        if (n < this.digitList.decimalAt && k < this.digitList.count) {
          paramStringBuffer.append((char)(this.digitList.digits[k++] + c2));
        } else {
          paramStringBuffer.append(c1);
        } 
        if (isGroupingUsed() && n > 0 && this.groupingSize != 0 && n % this.groupingSize == 0) {
          int i3 = paramStringBuffer.length();
          paramStringBuffer.append(c3);
          paramFieldDelegate.formatted(NumberFormat.Field.GROUPING_SEPARATOR, NumberFormat.Field.GROUPING_SEPARATOR, i3, paramStringBuffer.length(), paramStringBuffer);
        } 
      } 
      n = (paramInt4 > 0 || (!paramBoolean2 && k < this.digitList.count)) ? 1 : 0;
      if (n == 0 && paramStringBuffer.length() == m)
        paramStringBuffer.append(c1); 
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, i, paramStringBuffer.length(), paramStringBuffer);
      int i1 = paramStringBuffer.length();
      if (this.decimalSeparatorAlwaysShown || n != 0)
        paramStringBuffer.append(c4); 
      if (i1 != paramStringBuffer.length())
        paramFieldDelegate.formatted(NumberFormat.Field.DECIMAL_SEPARATOR, NumberFormat.Field.DECIMAL_SEPARATOR, i1, paramStringBuffer.length(), paramStringBuffer); 
      int i2 = paramStringBuffer.length();
      for (byte b = 0; b < paramInt3 && (b < paramInt4 || (!paramBoolean2 && k < this.digitList.count)); b++) {
        if (-1 - b > this.digitList.decimalAt - 1) {
          paramStringBuffer.append(c1);
        } else if (!paramBoolean2 && k < this.digitList.count) {
          paramStringBuffer.append((char)(this.digitList.digits[k++] + c2));
        } else {
          paramStringBuffer.append(c1);
        } 
      } 
      paramFieldDelegate.formatted(1, NumberFormat.Field.FRACTION, NumberFormat.Field.FRACTION, i2, paramStringBuffer.length(), paramStringBuffer);
    } 
    if (paramBoolean1) {
      append(paramStringBuffer, this.negativeSuffix, paramFieldDelegate, getNegativeSuffixFieldPositions(), NumberFormat.Field.SIGN);
    } else {
      append(paramStringBuffer, this.positiveSuffix, paramFieldDelegate, getPositiveSuffixFieldPositions(), NumberFormat.Field.SIGN);
    } 
    return paramStringBuffer;
  }
  
  private void append(StringBuffer paramStringBuffer, String paramString, Format.FieldDelegate paramFieldDelegate, FieldPosition[] paramArrayOfFieldPosition, Format.Field paramField) {
    int i = paramStringBuffer.length();
    if (paramString.length() > 0) {
      paramStringBuffer.append(paramString);
      byte b = 0;
      int j = paramArrayOfFieldPosition.length;
      while (b < j) {
        FieldPosition fieldPosition = paramArrayOfFieldPosition[b];
        Format.Field field = fieldPosition.getFieldAttribute();
        if (field == NumberFormat.Field.SIGN)
          field = paramField; 
        paramFieldDelegate.formatted(field, field, i + fieldPosition.getBeginIndex(), i + fieldPosition.getEndIndex(), paramStringBuffer);
        b++;
      } 
    } 
  }
  
  public Number parse(String paramString, ParsePosition paramParsePosition) {
    if (paramString.regionMatches(paramParsePosition.index, this.symbols.getNaN(), 0, this.symbols.getNaN().length())) {
      paramParsePosition.index += this.symbols.getNaN().length();
      return new Double(NaND);
    } 
    boolean[] arrayOfBoolean = new boolean[2];
    if (!subparse(paramString, paramParsePosition, this.positivePrefix, this.negativePrefix, this.digitList, false, arrayOfBoolean))
      return null; 
    if (arrayOfBoolean[0])
      return (arrayOfBoolean[1] == ((this.multiplier >= 0))) ? new Double(Double.POSITIVE_INFINITY) : new Double(Double.NEGATIVE_INFINITY); 
    if (this.multiplier == 0)
      return this.digitList.isZero() ? new Double(NaND) : (arrayOfBoolean[1] ? new Double(Double.POSITIVE_INFINITY) : new Double(Double.NEGATIVE_INFINITY)); 
    if (isParseBigDecimal()) {
      BigDecimal bigDecimal = this.digitList.getBigDecimal();
      if (this.multiplier != 1)
        try {
          bigDecimal = bigDecimal.divide(getBigDecimalMultiplier());
        } catch (ArithmeticException arithmeticException) {
          bigDecimal = bigDecimal.divide(getBigDecimalMultiplier(), this.roundingMode);
        }  
      if (!arrayOfBoolean[1])
        bigDecimal = bigDecimal.negate(); 
      return bigDecimal;
    } 
    boolean bool1 = true;
    boolean bool2 = false;
    double d = 0.0D;
    long l = 0L;
    if (this.digitList.fitsIntoLong(arrayOfBoolean[1], isParseIntegerOnly())) {
      bool1 = false;
      l = this.digitList.getLong();
      if (l < 0L)
        bool2 = true; 
    } else {
      d = this.digitList.getDouble();
    } 
    if (this.multiplier != 1)
      if (bool1) {
        d /= this.multiplier;
      } else if (l % this.multiplier == 0L) {
        l /= this.multiplier;
      } else {
        d = l / this.multiplier;
        bool1 = true;
      }  
    if (!arrayOfBoolean[1] && !bool2) {
      d = -d;
      l = -l;
    } 
    if (this.multiplier != 1 && bool1) {
      l = (long)d;
      bool1 = ((d != l || (d == 0.0D && 1.0D / d < 0.0D)) && !isParseIntegerOnly()) ? 1 : 0;
    } 
    return bool1 ? new Double(d) : new Long(l);
  }
  
  private BigInteger getBigIntegerMultiplier() {
    if (this.bigIntegerMultiplier == null)
      this.bigIntegerMultiplier = BigInteger.valueOf(this.multiplier); 
    return this.bigIntegerMultiplier;
  }
  
  private BigDecimal getBigDecimalMultiplier() {
    if (this.bigDecimalMultiplier == null)
      this.bigDecimalMultiplier = new BigDecimal(this.multiplier); 
    return this.bigDecimalMultiplier;
  }
  
  private final boolean subparse(String paramString1, ParsePosition paramParsePosition, String paramString2, String paramString3, DigitList paramDigitList, boolean paramBoolean, boolean[] paramArrayOfBoolean) {
    int i = paramParsePosition.index;
    int j = paramParsePosition.index;
    boolean bool1 = paramString1.regionMatches(i, paramString2, 0, paramString2.length());
    boolean bool2 = paramString1.regionMatches(i, paramString3, 0, paramString3.length());
    if (bool1 && bool2)
      if (paramString2.length() > paramString3.length()) {
        bool2 = false;
      } else if (paramString2.length() < paramString3.length()) {
        bool1 = false;
      }  
    if (bool1) {
      i += paramString2.length();
    } else if (bool2) {
      i += paramString3.length();
    } else {
      paramParsePosition.errorIndex = i;
      return false;
    } 
    paramArrayOfBoolean[0] = false;
    if (!paramBoolean && paramString1.regionMatches(i, this.symbols.getInfinity(), 0, this.symbols.getInfinity().length())) {
      i += this.symbols.getInfinity().length();
      paramArrayOfBoolean[0] = true;
    } else {
      paramDigitList.decimalAt = paramDigitList.count = 0;
      char c1 = this.symbols.getZeroDigit();
      char c2 = this.isCurrencyFormat ? this.symbols.getMonetaryDecimalSeparator() : this.symbols.getDecimalSeparator();
      char c3 = this.symbols.getGroupingSeparator();
      String str = this.symbols.getExponentSeparator();
      boolean bool3 = false;
      boolean bool4 = false;
      boolean bool5 = false;
      int m = 0;
      byte b = 0;
      int k = -1;
      while (i < paramString1.length()) {
        char c = paramString1.charAt(i);
        int n = c - c1;
        if (n < 0 || n > 9)
          n = Character.digit(c, 10); 
        if (n == 0) {
          k = -1;
          bool5 = true;
          if (paramDigitList.count == 0) {
            if (bool3)
              paramDigitList.decimalAt--; 
          } else {
            b++;
            paramDigitList.append((char)(n + 48));
          } 
        } else if (n > 0 && n <= 9) {
          bool5 = true;
          b++;
          paramDigitList.append((char)(n + 48));
          k = -1;
        } else if (!paramBoolean && c == c2) {
          if (isParseIntegerOnly() || bool3)
            break; 
          paramDigitList.decimalAt = b;
          bool3 = true;
        } else if (!paramBoolean && c == c3 && isGroupingUsed()) {
          if (bool3)
            break; 
          k = i;
        } else {
          if (!paramBoolean && paramString1.regionMatches(i, str, 0, str.length()) && !bool4) {
            ParsePosition parsePosition = new ParsePosition(i + str.length());
            boolean[] arrayOfBoolean = new boolean[2];
            DigitList digitList1 = new DigitList();
            if (subparse(paramString1, parsePosition, "", Character.toString(this.symbols.getMinusSign()), digitList1, true, arrayOfBoolean) && digitList1.fitsIntoLong(arrayOfBoolean[1], true)) {
              i = parsePosition.index;
              m = (int)digitList1.getLong();
              if (!arrayOfBoolean[1])
                m = -m; 
              bool4 = true;
            } 
          } 
          break;
        } 
        i++;
      } 
      if (k != -1)
        i = k; 
      if (!bool3)
        paramDigitList.decimalAt = b; 
      paramDigitList.decimalAt += m;
      if (!bool5 && b == 0) {
        paramParsePosition.index = j;
        paramParsePosition.errorIndex = j;
        return false;
      } 
    } 
    if (!paramBoolean) {
      if (bool1)
        bool1 = paramString1.regionMatches(i, this.positiveSuffix, 0, this.positiveSuffix.length()); 
      if (bool2)
        bool2 = paramString1.regionMatches(i, this.negativeSuffix, 0, this.negativeSuffix.length()); 
      if (bool1 && bool2)
        if (this.positiveSuffix.length() > this.negativeSuffix.length()) {
          bool2 = false;
        } else if (this.positiveSuffix.length() < this.negativeSuffix.length()) {
          bool1 = false;
        }  
      if (bool1 == bool2) {
        paramParsePosition.errorIndex = i;
        return false;
      } 
      paramParsePosition.index = i + (bool1 ? this.positiveSuffix.length() : this.negativeSuffix.length());
    } else {
      paramParsePosition.index = i;
    } 
    paramArrayOfBoolean[1] = bool1;
    if (paramParsePosition.index == j) {
      paramParsePosition.errorIndex = i;
      return false;
    } 
    return true;
  }
  
  public DecimalFormatSymbols getDecimalFormatSymbols() {
    try {
      return (DecimalFormatSymbols)this.symbols.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void setDecimalFormatSymbols(DecimalFormatSymbols paramDecimalFormatSymbols) {
    try {
      this.symbols = (DecimalFormatSymbols)paramDecimalFormatSymbols.clone();
      expandAffixes();
      this.fastPathCheckNeeded = true;
    } catch (Exception exception) {}
  }
  
  public String getPositivePrefix() { return this.positivePrefix; }
  
  public void setPositivePrefix(String paramString) {
    this.positivePrefix = paramString;
    this.posPrefixPattern = null;
    this.positivePrefixFieldPositions = null;
    this.fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getPositivePrefixFieldPositions() {
    if (this.positivePrefixFieldPositions == null)
      if (this.posPrefixPattern != null) {
        this.positivePrefixFieldPositions = expandAffix(this.posPrefixPattern);
      } else {
        this.positivePrefixFieldPositions = EmptyFieldPositionArray;
      }  
    return this.positivePrefixFieldPositions;
  }
  
  public String getNegativePrefix() { return this.negativePrefix; }
  
  public void setNegativePrefix(String paramString) {
    this.negativePrefix = paramString;
    this.negPrefixPattern = null;
    this.fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getNegativePrefixFieldPositions() {
    if (this.negativePrefixFieldPositions == null)
      if (this.negPrefixPattern != null) {
        this.negativePrefixFieldPositions = expandAffix(this.negPrefixPattern);
      } else {
        this.negativePrefixFieldPositions = EmptyFieldPositionArray;
      }  
    return this.negativePrefixFieldPositions;
  }
  
  public String getPositiveSuffix() { return this.positiveSuffix; }
  
  public void setPositiveSuffix(String paramString) {
    this.positiveSuffix = paramString;
    this.posSuffixPattern = null;
    this.fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getPositiveSuffixFieldPositions() {
    if (this.positiveSuffixFieldPositions == null)
      if (this.posSuffixPattern != null) {
        this.positiveSuffixFieldPositions = expandAffix(this.posSuffixPattern);
      } else {
        this.positiveSuffixFieldPositions = EmptyFieldPositionArray;
      }  
    return this.positiveSuffixFieldPositions;
  }
  
  public String getNegativeSuffix() { return this.negativeSuffix; }
  
  public void setNegativeSuffix(String paramString) {
    this.negativeSuffix = paramString;
    this.negSuffixPattern = null;
    this.fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getNegativeSuffixFieldPositions() {
    if (this.negativeSuffixFieldPositions == null)
      if (this.negSuffixPattern != null) {
        this.negativeSuffixFieldPositions = expandAffix(this.negSuffixPattern);
      } else {
        this.negativeSuffixFieldPositions = EmptyFieldPositionArray;
      }  
    return this.negativeSuffixFieldPositions;
  }
  
  public int getMultiplier() { return this.multiplier; }
  
  public void setMultiplier(int paramInt) {
    this.multiplier = paramInt;
    this.bigDecimalMultiplier = null;
    this.bigIntegerMultiplier = null;
    this.fastPathCheckNeeded = true;
  }
  
  public void setGroupingUsed(boolean paramBoolean) {
    super.setGroupingUsed(paramBoolean);
    this.fastPathCheckNeeded = true;
  }
  
  public int getGroupingSize() { return this.groupingSize; }
  
  public void setGroupingSize(int paramInt) {
    this.groupingSize = (byte)paramInt;
    this.fastPathCheckNeeded = true;
  }
  
  public boolean isDecimalSeparatorAlwaysShown() { return this.decimalSeparatorAlwaysShown; }
  
  public void setDecimalSeparatorAlwaysShown(boolean paramBoolean) {
    this.decimalSeparatorAlwaysShown = paramBoolean;
    this.fastPathCheckNeeded = true;
  }
  
  public boolean isParseBigDecimal() { return this.parseBigDecimal; }
  
  public void setParseBigDecimal(boolean paramBoolean) { this.parseBigDecimal = paramBoolean; }
  
  public Object clone() {
    DecimalFormat decimalFormat = (DecimalFormat)super.clone();
    decimalFormat.symbols = (DecimalFormatSymbols)this.symbols.clone();
    decimalFormat.digitList = (DigitList)this.digitList.clone();
    decimalFormat.fastPathCheckNeeded = true;
    decimalFormat.isFastPath = false;
    decimalFormat.fastPathData = null;
    return decimalFormat;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!super.equals(paramObject))
      return false; 
    DecimalFormat decimalFormat = (DecimalFormat)paramObject;
    return (((this.posPrefixPattern == decimalFormat.posPrefixPattern && this.positivePrefix.equals(decimalFormat.positivePrefix)) || (this.posPrefixPattern != null && this.posPrefixPattern.equals(decimalFormat.posPrefixPattern))) && ((this.posSuffixPattern == decimalFormat.posSuffixPattern && this.positiveSuffix.equals(decimalFormat.positiveSuffix)) || (this.posSuffixPattern != null && this.posSuffixPattern.equals(decimalFormat.posSuffixPattern))) && ((this.negPrefixPattern == decimalFormat.negPrefixPattern && this.negativePrefix.equals(decimalFormat.negativePrefix)) || (this.negPrefixPattern != null && this.negPrefixPattern.equals(decimalFormat.negPrefixPattern))) && ((this.negSuffixPattern == decimalFormat.negSuffixPattern && this.negativeSuffix.equals(decimalFormat.negativeSuffix)) || (this.negSuffixPattern != null && this.negSuffixPattern.equals(decimalFormat.negSuffixPattern))) && this.multiplier == decimalFormat.multiplier && this.groupingSize == decimalFormat.groupingSize && this.decimalSeparatorAlwaysShown == decimalFormat.decimalSeparatorAlwaysShown && this.parseBigDecimal == decimalFormat.parseBigDecimal && this.useExponentialNotation == decimalFormat.useExponentialNotation && (!this.useExponentialNotation || this.minExponentDigits == decimalFormat.minExponentDigits) && this.maximumIntegerDigits == decimalFormat.maximumIntegerDigits && this.minimumIntegerDigits == decimalFormat.minimumIntegerDigits && this.maximumFractionDigits == decimalFormat.maximumFractionDigits && this.minimumFractionDigits == decimalFormat.minimumFractionDigits && this.roundingMode == decimalFormat.roundingMode && this.symbols.equals(decimalFormat.symbols));
  }
  
  public int hashCode() { return super.hashCode() * 37 + this.positivePrefix.hashCode(); }
  
  public String toPattern() { return toPattern(false); }
  
  public String toLocalizedPattern() { return toPattern(true); }
  
  private void expandAffixes() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.posPrefixPattern != null) {
      this.positivePrefix = expandAffix(this.posPrefixPattern, stringBuffer);
      this.positivePrefixFieldPositions = null;
    } 
    if (this.posSuffixPattern != null) {
      this.positiveSuffix = expandAffix(this.posSuffixPattern, stringBuffer);
      this.positiveSuffixFieldPositions = null;
    } 
    if (this.negPrefixPattern != null) {
      this.negativePrefix = expandAffix(this.negPrefixPattern, stringBuffer);
      this.negativePrefixFieldPositions = null;
    } 
    if (this.negSuffixPattern != null) {
      this.negativeSuffix = expandAffix(this.negSuffixPattern, stringBuffer);
      this.negativeSuffixFieldPositions = null;
    } 
  }
  
  private String expandAffix(String paramString, StringBuffer paramStringBuffer) {
    paramStringBuffer.setLength(0);
    byte b = 0;
    while (b < paramString.length()) {
      char c = paramString.charAt(b++);
      if (c == '\'') {
        c = paramString.charAt(b++);
        switch (c) {
          case '¤':
            if (b < paramString.length() && paramString.charAt(b) == '¤') {
              b++;
              paramStringBuffer.append(this.symbols.getInternationalCurrencySymbol());
              continue;
            } 
            paramStringBuffer.append(this.symbols.getCurrencySymbol());
            continue;
          case '%':
            c = this.symbols.getPercent();
            break;
          case '‰':
            c = this.symbols.getPerMill();
            break;
          case '-':
            c = this.symbols.getMinusSign();
            break;
        } 
      } 
      paramStringBuffer.append(c);
    } 
    return paramStringBuffer.toString();
  }
  
  private FieldPosition[] expandAffix(String paramString) {
    ArrayList arrayList = null;
    int i = 0;
    byte b = 0;
    while (b < paramString.length()) {
      char c = paramString.charAt(b++);
      if (c == '\'') {
        String str;
        byte b1 = -1;
        NumberFormat.Field field = null;
        c = paramString.charAt(b++);
        switch (c) {
          case '¤':
            if (b < paramString.length() && paramString.charAt(b) == '¤') {
              b++;
              str = this.symbols.getInternationalCurrencySymbol();
            } else {
              str = this.symbols.getCurrencySymbol();
            } 
            if (str.length() > 0) {
              if (arrayList == null)
                arrayList = new ArrayList(2); 
              FieldPosition fieldPosition = new FieldPosition(NumberFormat.Field.CURRENCY);
              fieldPosition.setBeginIndex(i);
              fieldPosition.setEndIndex(i + str.length());
              arrayList.add(fieldPosition);
              i += str.length();
            } 
            continue;
          case '%':
            c = this.symbols.getPercent();
            b1 = -1;
            field = NumberFormat.Field.PERCENT;
            break;
          case '‰':
            c = this.symbols.getPerMill();
            b1 = -1;
            field = NumberFormat.Field.PERMILLE;
            break;
          case '-':
            c = this.symbols.getMinusSign();
            b1 = -1;
            field = NumberFormat.Field.SIGN;
            break;
        } 
        if (field != null) {
          if (arrayList == null)
            arrayList = new ArrayList(2); 
          FieldPosition fieldPosition = new FieldPosition(field, b1);
          fieldPosition.setBeginIndex(i);
          fieldPosition.setEndIndex(i + 1);
          arrayList.add(fieldPosition);
        } 
      } 
      i++;
    } 
    return (arrayList != null) ? (FieldPosition[])arrayList.toArray(EmptyFieldPositionArray) : EmptyFieldPositionArray;
  }
  
  private void appendAffix(StringBuffer paramStringBuffer, String paramString1, String paramString2, boolean paramBoolean) {
    if (paramString1 == null) {
      appendAffix(paramStringBuffer, paramString2, paramBoolean);
    } else {
      int i;
      for (i = 0; i < paramString1.length(); i = j) {
        int j = paramString1.indexOf('\'', i);
        if (j < 0) {
          appendAffix(paramStringBuffer, paramString1.substring(i), paramBoolean);
          break;
        } 
        if (j > i)
          appendAffix(paramStringBuffer, paramString1.substring(i, j), paramBoolean); 
        char c = paramString1.charAt(++j);
        j++;
        if (c == '\'') {
          paramStringBuffer.append(c);
        } else if (c == '¤' && j < paramString1.length() && paramString1.charAt(j) == '¤') {
          j++;
          paramStringBuffer.append(c);
        } else if (paramBoolean) {
          switch (c) {
            case '%':
              c = this.symbols.getPercent();
              break;
            case '‰':
              c = this.symbols.getPerMill();
              break;
            case '-':
              c = this.symbols.getMinusSign();
              break;
          } 
        } 
        paramStringBuffer.append(c);
      } 
    } 
  }
  
  private void appendAffix(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean) {
    boolean bool;
    if (paramBoolean) {
      bool = (paramString.indexOf(this.symbols.getZeroDigit()) >= 0 || paramString.indexOf(this.symbols.getGroupingSeparator()) >= 0 || paramString.indexOf(this.symbols.getDecimalSeparator()) >= 0 || paramString.indexOf(this.symbols.getPercent()) >= 0 || paramString.indexOf(this.symbols.getPerMill()) >= 0 || paramString.indexOf(this.symbols.getDigit()) >= 0 || paramString.indexOf(this.symbols.getPatternSeparator()) >= 0 || paramString.indexOf(this.symbols.getMinusSign()) >= 0 || paramString.indexOf('¤') >= 0) ? 1 : 0;
    } else {
      bool = (paramString.indexOf('0') >= 0 || paramString.indexOf(',') >= 0 || paramString.indexOf('.') >= 0 || paramString.indexOf('%') >= 0 || paramString.indexOf('‰') >= 0 || paramString.indexOf('#') >= 0 || paramString.indexOf(';') >= 0 || paramString.indexOf('-') >= 0 || paramString.indexOf('¤') >= 0) ? 1 : 0;
    } 
    if (bool)
      paramStringBuffer.append('\''); 
    if (paramString.indexOf('\'') < 0) {
      paramStringBuffer.append(paramString);
    } else {
      for (byte b = 0; b < paramString.length(); b++) {
        char c = paramString.charAt(b);
        paramStringBuffer.append(c);
        if (c == '\'')
          paramStringBuffer.append(c); 
      } 
    } 
    if (bool)
      paramStringBuffer.append('\''); 
  }
  
  private String toPattern(boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 1; b; b--) {
      if (b == 1) {
        appendAffix(stringBuffer, this.posPrefixPattern, this.positivePrefix, paramBoolean);
      } else {
        appendAffix(stringBuffer, this.negPrefixPattern, this.negativePrefix, paramBoolean);
      } 
      int j = this.useExponentialNotation ? getMaximumIntegerDigits() : (Math.max(this.groupingSize, getMinimumIntegerDigits()) + 1);
      int i;
      for (i = j; i > 0; i--) {
        if (i != j && isGroupingUsed() && this.groupingSize != 0 && i % this.groupingSize == 0)
          stringBuffer.append(paramBoolean ? this.symbols.getGroupingSeparator() : 44); 
        stringBuffer.append((i <= getMinimumIntegerDigits()) ? (paramBoolean ? this.symbols.getZeroDigit() : 48) : (paramBoolean ? this.symbols.getDigit() : 35));
      } 
      if (getMaximumFractionDigits() > 0 || this.decimalSeparatorAlwaysShown)
        stringBuffer.append(paramBoolean ? this.symbols.getDecimalSeparator() : 46); 
      for (i = 0; i < getMaximumFractionDigits(); i++) {
        if (i < getMinimumFractionDigits()) {
          stringBuffer.append(paramBoolean ? this.symbols.getZeroDigit() : 48);
        } else {
          stringBuffer.append(paramBoolean ? this.symbols.getDigit() : 35);
        } 
      } 
      if (this.useExponentialNotation) {
        stringBuffer.append(paramBoolean ? this.symbols.getExponentSeparator() : "E");
        for (i = 0; i < this.minExponentDigits; i++)
          stringBuffer.append(paramBoolean ? this.symbols.getZeroDigit() : 48); 
      } 
      if (b == 1) {
        appendAffix(stringBuffer, this.posSuffixPattern, this.positiveSuffix, paramBoolean);
        if (((this.negSuffixPattern == this.posSuffixPattern && this.negativeSuffix.equals(this.positiveSuffix)) || (this.negSuffixPattern != null && this.negSuffixPattern.equals(this.posSuffixPattern))) && ((this.negPrefixPattern != null && this.posPrefixPattern != null && this.negPrefixPattern.equals("'-" + this.posPrefixPattern)) || (this.negPrefixPattern == this.posPrefixPattern && this.negativePrefix.equals(this.symbols.getMinusSign() + this.positivePrefix))))
          break; 
        stringBuffer.append(paramBoolean ? this.symbols.getPatternSeparator() : 59);
      } else {
        appendAffix(stringBuffer, this.negSuffixPattern, this.negativeSuffix, paramBoolean);
      } 
    } 
    return stringBuffer.toString();
  }
  
  public void applyPattern(String paramString) { applyPattern(paramString, false); }
  
  public void applyLocalizedPattern(String paramString) { applyPattern(paramString, true); }
  
  private void applyPattern(String paramString, boolean paramBoolean) {
    char c1 = '0';
    char c2 = ',';
    char c3 = '.';
    char c4 = '%';
    char c5 = '‰';
    char c6 = '#';
    char c7 = ';';
    String str = "E";
    char c8 = '-';
    if (paramBoolean) {
      c1 = this.symbols.getZeroDigit();
      c2 = this.symbols.getGroupingSeparator();
      c3 = this.symbols.getDecimalSeparator();
      c4 = this.symbols.getPercent();
      c5 = this.symbols.getPerMill();
      c6 = this.symbols.getDigit();
      c7 = this.symbols.getPatternSeparator();
      str = this.symbols.getExponentSeparator();
      c8 = this.symbols.getMinusSign();
    } 
    boolean bool = false;
    this.decimalSeparatorAlwaysShown = false;
    this.isCurrencyFormat = false;
    this.useExponentialNotation = false;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    for (byte b4 = 1; b4 && b3 < paramString.length(); b4--) {
      boolean bool1 = false;
      StringBuffer stringBuffer1 = new StringBuffer();
      StringBuffer stringBuffer2 = new StringBuffer();
      int i = -1;
      char c = '\001';
      int j = 0;
      int k = 0;
      int m = 0;
      byte b = -1;
      byte b5 = 0;
      StringBuffer stringBuffer3 = stringBuffer1;
      int n;
      for (n = b3; n < paramString.length(); n++) {
        char c9 = paramString.charAt(n);
        switch (b5) {
          case false:
          case true:
            if (bool1) {
              if (c9 == '\'') {
                if (n + 1 < paramString.length() && paramString.charAt(n + 1) == '\'') {
                  n++;
                  stringBuffer3.append("''");
                  break;
                } 
                bool1 = false;
                break;
              } 
            } else {
              if (c9 == c6 || c9 == c1 || c9 == c2 || c9 == c3) {
                b5 = 1;
                if (b4 == 1)
                  b1 = n; 
                n--;
                break;
              } 
              if (c9 == '¤') {
                boolean bool2 = (n + 1 < paramString.length() && paramString.charAt(n + 1) == '¤') ? 1 : 0;
                if (bool2)
                  n++; 
                this.isCurrencyFormat = true;
                stringBuffer3.append(bool2 ? "'¤¤" : "'¤");
                break;
              } 
              if (c9 == '\'') {
                if (c9 == '\'') {
                  if (n + 1 < paramString.length() && paramString.charAt(n + 1) == '\'') {
                    n++;
                    stringBuffer3.append("''");
                    break;
                  } 
                  bool1 = true;
                  break;
                } 
              } else {
                if (c9 == c7) {
                  if (!b5 || !b4)
                    throw new IllegalArgumentException("Unquoted special character '" + c9 + "' in pattern \"" + paramString + '"'); 
                  b3 = n + 1;
                  n = paramString.length();
                  break;
                } 
                if (c9 == c4) {
                  if (c != '\001')
                    throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + paramString + '"'); 
                  c = 'd';
                  stringBuffer3.append("'%");
                  break;
                } 
                if (c9 == c5) {
                  if (c != '\001')
                    throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + paramString + '"'); 
                  c = 'Ϩ';
                  stringBuffer3.append("'‰");
                  break;
                } 
                if (c9 == c8) {
                  stringBuffer3.append("'-");
                  break;
                } 
              } 
            } 
            stringBuffer3.append(c9);
            break;
          case true:
            if (b4 == 1) {
              b2++;
            } else {
              if (--b2 == 0) {
                b5 = 2;
                stringBuffer3 = stringBuffer2;
              } 
              break;
            } 
            if (c9 == c6) {
              if (k) {
                m++;
              } else {
                j++;
              } 
              if (b >= 0 && i < 0)
                b = (byte)(b + 1); 
              break;
            } 
            if (c9 == c1) {
              if (m > 0)
                throw new IllegalArgumentException("Unexpected '0' in pattern \"" + paramString + '"'); 
              k++;
              if (b >= 0 && i < 0)
                b = (byte)(b + 1); 
              break;
            } 
            if (c9 == c2) {
              b = 0;
              break;
            } 
            if (c9 == c3) {
              if (i >= 0)
                throw new IllegalArgumentException("Multiple decimal separators in pattern \"" + paramString + '"'); 
              i = j + k + m;
              break;
            } 
            if (paramString.regionMatches(n, str, 0, str.length())) {
              if (this.useExponentialNotation)
                throw new IllegalArgumentException("Multiple exponential symbols in pattern \"" + paramString + '"'); 
              this.useExponentialNotation = true;
              this.minExponentDigits = 0;
              for (n += str.length(); n < paramString.length() && paramString.charAt(n) == c1; n++) {
                this.minExponentDigits = (byte)(this.minExponentDigits + 1);
                b2++;
              } 
              if (j + k < 1 || this.minExponentDigits < 1)
                throw new IllegalArgumentException("Malformed exponential pattern \"" + paramString + '"'); 
              b5 = 2;
              stringBuffer3 = stringBuffer2;
              n--;
              break;
            } 
            b5 = 2;
            stringBuffer3 = stringBuffer2;
            n--;
            b2--;
            break;
        } 
      } 
      if (k == 0 && j > 0 && i >= 0) {
        n = i;
        if (n == 0)
          n++; 
        m = j - n;
        j = n - 1;
        k = 1;
      } 
      if ((i < 0 && m > 0) || (i >= 0 && (i < j || i > j + k)) || b == 0 || bool1)
        throw new IllegalArgumentException("Malformed pattern \"" + paramString + '"'); 
      if (b4 == 1) {
        this.posPrefixPattern = stringBuffer1.toString();
        this.posSuffixPattern = stringBuffer2.toString();
        this.negPrefixPattern = this.posPrefixPattern;
        this.negSuffixPattern = this.posSuffixPattern;
        n = j + k + m;
        int i1 = (i >= 0) ? i : n;
        setMinimumIntegerDigits(i1 - j);
        setMaximumIntegerDigits(this.useExponentialNotation ? (j + getMinimumIntegerDigits()) : Integer.MAX_VALUE);
        setMaximumFractionDigits((i >= 0) ? (n - i) : 0);
        setMinimumFractionDigits((i >= 0) ? (j + k - i) : 0);
        setGroupingUsed((b > 0));
        this.groupingSize = (b > 0) ? b : 0;
        this.multiplier = c;
        setDecimalSeparatorAlwaysShown((i == 0 || i == n));
      } else {
        this.negPrefixPattern = stringBuffer1.toString();
        this.negSuffixPattern = stringBuffer2.toString();
        bool = true;
      } 
    } 
    if (paramString.length() == 0) {
      this.posPrefixPattern = this.posSuffixPattern = "";
      setMinimumIntegerDigits(0);
      setMaximumIntegerDigits(2147483647);
      setMinimumFractionDigits(0);
      setMaximumFractionDigits(2147483647);
    } 
    if (!bool || (this.negPrefixPattern.equals(this.posPrefixPattern) && this.negSuffixPattern.equals(this.posSuffixPattern))) {
      this.negSuffixPattern = this.posSuffixPattern;
      this.negPrefixPattern = "'-" + this.posPrefixPattern;
    } 
    expandAffixes();
  }
  
  public void setMaximumIntegerDigits(int paramInt) {
    this.maximumIntegerDigits = Math.min(Math.max(0, paramInt), 2147483647);
    super.setMaximumIntegerDigits((this.maximumIntegerDigits > 309) ? 309 : this.maximumIntegerDigits);
    if (this.minimumIntegerDigits > this.maximumIntegerDigits) {
      this.minimumIntegerDigits = this.maximumIntegerDigits;
      super.setMinimumIntegerDigits((this.minimumIntegerDigits > 309) ? 309 : this.minimumIntegerDigits);
    } 
    this.fastPathCheckNeeded = true;
  }
  
  public void setMinimumIntegerDigits(int paramInt) {
    this.minimumIntegerDigits = Math.min(Math.max(0, paramInt), 2147483647);
    super.setMinimumIntegerDigits((this.minimumIntegerDigits > 309) ? 309 : this.minimumIntegerDigits);
    if (this.minimumIntegerDigits > this.maximumIntegerDigits) {
      this.maximumIntegerDigits = this.minimumIntegerDigits;
      super.setMaximumIntegerDigits((this.maximumIntegerDigits > 309) ? 309 : this.maximumIntegerDigits);
    } 
    this.fastPathCheckNeeded = true;
  }
  
  public void setMaximumFractionDigits(int paramInt) {
    this.maximumFractionDigits = Math.min(Math.max(0, paramInt), 2147483647);
    super.setMaximumFractionDigits((this.maximumFractionDigits > 340) ? 340 : this.maximumFractionDigits);
    if (this.minimumFractionDigits > this.maximumFractionDigits) {
      this.minimumFractionDigits = this.maximumFractionDigits;
      super.setMinimumFractionDigits((this.minimumFractionDigits > 340) ? 340 : this.minimumFractionDigits);
    } 
    this.fastPathCheckNeeded = true;
  }
  
  public void setMinimumFractionDigits(int paramInt) {
    this.minimumFractionDigits = Math.min(Math.max(0, paramInt), 2147483647);
    super.setMinimumFractionDigits((this.minimumFractionDigits > 340) ? 340 : this.minimumFractionDigits);
    if (this.minimumFractionDigits > this.maximumFractionDigits) {
      this.maximumFractionDigits = this.minimumFractionDigits;
      super.setMaximumFractionDigits((this.maximumFractionDigits > 340) ? 340 : this.maximumFractionDigits);
    } 
    this.fastPathCheckNeeded = true;
  }
  
  public int getMaximumIntegerDigits() { return this.maximumIntegerDigits; }
  
  public int getMinimumIntegerDigits() { return this.minimumIntegerDigits; }
  
  public int getMaximumFractionDigits() { return this.maximumFractionDigits; }
  
  public int getMinimumFractionDigits() { return this.minimumFractionDigits; }
  
  public Currency getCurrency() { return this.symbols.getCurrency(); }
  
  public void setCurrency(Currency paramCurrency) {
    if (paramCurrency != this.symbols.getCurrency()) {
      this.symbols.setCurrency(paramCurrency);
      if (this.isCurrencyFormat)
        expandAffixes(); 
    } 
    this.fastPathCheckNeeded = true;
  }
  
  public RoundingMode getRoundingMode() { return this.roundingMode; }
  
  public void setRoundingMode(RoundingMode paramRoundingMode) {
    if (paramRoundingMode == null)
      throw new NullPointerException(); 
    this.roundingMode = paramRoundingMode;
    this.digitList.setRoundingMode(paramRoundingMode);
    this.fastPathCheckNeeded = true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.digitList = new DigitList();
    this.fastPathCheckNeeded = true;
    this.isFastPath = false;
    this.fastPathData = null;
    if (this.serialVersionOnStream < 4) {
      setRoundingMode(RoundingMode.HALF_EVEN);
    } else {
      setRoundingMode(getRoundingMode());
    } 
    if (super.getMaximumIntegerDigits() > 309 || super.getMaximumFractionDigits() > 340)
      throw new InvalidObjectException("Digit count out of range"); 
    if (this.serialVersionOnStream < 3) {
      setMaximumIntegerDigits(super.getMaximumIntegerDigits());
      setMinimumIntegerDigits(super.getMinimumIntegerDigits());
      setMaximumFractionDigits(super.getMaximumFractionDigits());
      setMinimumFractionDigits(super.getMinimumFractionDigits());
    } 
    if (this.serialVersionOnStream < 1)
      this.useExponentialNotation = false; 
    this.serialVersionOnStream = 4;
  }
  
  private static class DigitArrays {
    static final char[] DigitOnes1000 = new char[1000];
    
    static final char[] DigitTens1000 = new char[1000];
    
    static final char[] DigitHundreds1000 = new char[1000];
    
    static  {
      boolean bool1 = false;
      boolean bool2 = false;
      char c1 = '0';
      char c2 = '0';
      char c3 = '0';
      for (byte b = 0; b < 'Ϩ'; b++) {
        DigitOnes1000[b] = c1;
        if (c1 == '9') {
          c1 = '0';
        } else {
          c1 = (char)(c1 + 1);
        } 
        DigitTens1000[b] = c2;
        if (b == bool1 + 9) {
          bool1 += true;
          if (c2 == '9') {
            c2 = '0';
          } else {
            c2 = (char)(c2 + 1);
          } 
        } 
        DigitHundreds1000[b] = c3;
        if (b == bool2 + 99) {
          c3 = (char)(c3 + 1);
          bool2 += true;
        } 
      } 
    }
  }
  
  private static class FastPathData {
    int lastFreeIndex;
    
    int firstUsedIndex;
    
    int zeroDelta;
    
    char groupingChar;
    
    int integralLastIndex;
    
    int fractionalFirstIndex;
    
    double fractionalScaleFactor;
    
    int fractionalMaxIntBound;
    
    char[] fastPathContainer;
    
    char[] charsPositivePrefix;
    
    char[] charsNegativePrefix;
    
    char[] charsPositiveSuffix;
    
    char[] charsNegativeSuffix;
    
    boolean positiveAffixesRequired = true;
    
    boolean negativeAffixesRequired = true;
    
    private FastPathData() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\DecimalFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */