package com.sun.xml.internal.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@Deprecated
public final class DatatypeConverterImpl implements DatatypeConverterInterface {
  @Deprecated
  public static final DatatypeConverterInterface theInstance = new DatatypeConverterImpl();
  
  private static final byte[] decodeMap = initDecodeMap();
  
  private static final byte PADDING = 127;
  
  private static final char[] encodeMap = initEncodeMap();
  
  private static final Map<ClassLoader, DatatypeFactory> DF_CACHE = Collections.synchronizedMap(new WeakHashMap());
  
  @Deprecated
  private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
  
  public static BigInteger _parseInteger(CharSequence paramCharSequence) { return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(paramCharSequence)).toString()); }
  
  public static String _printInteger(BigInteger paramBigInteger) { return paramBigInteger.toString(); }
  
  public static int _parseInt(CharSequence paramCharSequence) {
    int i = paramCharSequence.length();
    int j = 1;
    int k = 0;
    for (byte b = 0; b < i; b++) {
      char c = paramCharSequence.charAt(b);
      if (!WhiteSpaceProcessor.isWhiteSpace(c))
        if ('0' <= c && c <= '9') {
          k = k * 10 + c - '0';
        } else if (c == '-') {
          j = -1;
        } else if (c != '+') {
          throw new NumberFormatException("Not a number: " + paramCharSequence);
        }  
    } 
    return k * j;
  }
  
  public static long _parseLong(CharSequence paramCharSequence) { return Long.valueOf(removeOptionalPlus(WhiteSpaceProcessor.trim(paramCharSequence)).toString()).longValue(); }
  
  public static short _parseShort(CharSequence paramCharSequence) { return (short)_parseInt(paramCharSequence); }
  
  public static String _printShort(short paramShort) { return String.valueOf(paramShort); }
  
  public static BigDecimal _parseDecimal(CharSequence paramCharSequence) {
    paramCharSequence = WhiteSpaceProcessor.trim(paramCharSequence);
    return (paramCharSequence.length() <= 0) ? null : new BigDecimal(paramCharSequence.toString());
  }
  
  public static float _parseFloat(CharSequence paramCharSequence) {
    String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
    if (str.equals("NaN"))
      return NaNF; 
    if (str.equals("INF"))
      return Float.POSITIVE_INFINITY; 
    if (str.equals("-INF"))
      return Float.NEGATIVE_INFINITY; 
    if (str.length() == 0 || !isDigitOrPeriodOrSign(str.charAt(0)) || !isDigitOrPeriodOrSign(str.charAt(str.length() - 1)))
      throw new NumberFormatException(); 
    return Float.parseFloat(str);
  }
  
  public static String _printFloat(float paramFloat) { return Float.isNaN(paramFloat) ? "NaN" : ((paramFloat == Float.POSITIVE_INFINITY) ? "INF" : ((paramFloat == Float.NEGATIVE_INFINITY) ? "-INF" : String.valueOf(paramFloat))); }
  
  public static double _parseDouble(CharSequence paramCharSequence) {
    String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
    if (str.equals("NaN"))
      return NaND; 
    if (str.equals("INF"))
      return Double.POSITIVE_INFINITY; 
    if (str.equals("-INF"))
      return Double.NEGATIVE_INFINITY; 
    if (str.length() == 0 || !isDigitOrPeriodOrSign(str.charAt(0)) || !isDigitOrPeriodOrSign(str.charAt(str.length() - 1)))
      throw new NumberFormatException(str); 
    return Double.parseDouble(str);
  }
  
  public static Boolean _parseBoolean(CharSequence paramCharSequence) {
    String str2;
    String str1;
    char c;
    if (paramCharSequence == null)
      return null; 
    byte b1 = 0;
    int i = paramCharSequence.length();
    boolean bool = false;
    if (paramCharSequence.length() <= 0)
      return null; 
    do {
      c = paramCharSequence.charAt(b1++);
    } while (WhiteSpaceProcessor.isWhiteSpace(c) && b1 < i);
    byte b2 = 0;
    switch (c) {
      case '1':
        bool = true;
        break;
      case '0':
        bool = false;
        break;
      case 't':
        str1 = "rue";
        do {
          c = paramCharSequence.charAt(b1++);
        } while (str1.charAt(b2++) == c && b1 < i && b2 < 3);
        if (b2 == 3) {
          bool = true;
          break;
        } 
        return Boolean.valueOf(false);
      case 'f':
        str2 = "alse";
        do {
          c = paramCharSequence.charAt(b1++);
        } while (str2.charAt(b2++) == c && b1 < i && b2 < 4);
        if (b2 == 4) {
          bool = false;
          break;
        } 
        return Boolean.valueOf(false);
    } 
    if (b1 < i)
      do {
        c = paramCharSequence.charAt(b1++);
      } while (WhiteSpaceProcessor.isWhiteSpace(c) && b1 < i); 
    return (b1 == i) ? Boolean.valueOf(bool) : null;
  }
  
  public static String _printBoolean(boolean paramBoolean) { return paramBoolean ? "true" : "false"; }
  
  public static byte _parseByte(CharSequence paramCharSequence) { return (byte)_parseInt(paramCharSequence); }
  
  public static String _printByte(byte paramByte) { return String.valueOf(paramByte); }
  
  public static QName _parseQName(CharSequence paramCharSequence, NamespaceContext paramNamespaceContext) {
    String str3;
    String str2;
    String str1;
    int i = paramCharSequence.length();
    byte b1;
    for (b1 = 0; b1 < i && WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(b1)); b1++);
    int j;
    for (j = i; j > b1 && WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(j - 1)); j--);
    if (j == b1)
      throw new IllegalArgumentException("input is empty"); 
    byte b2;
    for (b2 = b1 + 1; b2 < j && paramCharSequence.charAt(b2) != ':'; b2++);
    if (b2 == j) {
      str1 = paramNamespaceContext.getNamespaceURI("");
      str2 = paramCharSequence.subSequence(b1, j).toString();
      str3 = "";
    } else {
      str3 = paramCharSequence.subSequence(b1, b2).toString();
      str2 = paramCharSequence.subSequence(b2 + 1, j).toString();
      str1 = paramNamespaceContext.getNamespaceURI(str3);
      if (str1 == null || str1.length() == 0)
        throw new IllegalArgumentException("prefix " + str3 + " is not bound to a namespace"); 
    } 
    return new QName(str1, str2, str3);
  }
  
  public static GregorianCalendar _parseDateTime(CharSequence paramCharSequence) {
    String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
    return getDatatypeFactory().newXMLGregorianCalendar(str).toGregorianCalendar();
  }
  
  public static String _printDateTime(Calendar paramCalendar) { return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", paramCalendar); }
  
  public static String _printDate(Calendar paramCalendar) { return CalendarFormatter.doFormat("%Y-%M-%D" + "%z", paramCalendar); }
  
  public static String _printInt(int paramInt) { return String.valueOf(paramInt); }
  
  public static String _printLong(long paramLong) { return String.valueOf(paramLong); }
  
  public static String _printDecimal(BigDecimal paramBigDecimal) { return paramBigDecimal.toPlainString(); }
  
  public static String _printDouble(double paramDouble) { return Double.isNaN(paramDouble) ? "NaN" : ((paramDouble == Double.POSITIVE_INFINITY) ? "INF" : ((paramDouble == Double.NEGATIVE_INFINITY) ? "-INF" : String.valueOf(paramDouble))); }
  
  public static String _printQName(QName paramQName, NamespaceContext paramNamespaceContext) {
    String str1;
    String str2 = paramNamespaceContext.getPrefix(paramQName.getNamespaceURI());
    String str3 = paramQName.getLocalPart();
    if (str2 == null || str2.length() == 0) {
      str1 = str3;
    } else {
      str1 = str2 + ':' + str3;
    } 
    return str1;
  }
  
  private static byte[] initDecodeMap() {
    byte[] arrayOfByte = new byte[128];
    byte b;
    for (b = 0; b < ''; b++)
      arrayOfByte[b] = -1; 
    for (b = 65; b <= 90; b++)
      arrayOfByte[b] = (byte)(b - 65); 
    for (b = 97; b <= 122; b++)
      arrayOfByte[b] = (byte)(b - 97 + 26); 
    for (b = 48; b <= 57; b++)
      arrayOfByte[b] = (byte)(b - 48 + 52); 
    arrayOfByte[43] = 62;
    arrayOfByte[47] = 63;
    arrayOfByte[61] = Byte.MAX_VALUE;
    return arrayOfByte;
  }
  
  private static int guessLength(String paramString) {
    int i = paramString.length();
    int j = i - 1;
    while (j >= 0) {
      byte b = decodeMap[paramString.charAt(j)];
      if (b == Byte.MAX_VALUE) {
        j--;
        continue;
      } 
      if (b == -1)
        return paramString.length() / 4 * 3; 
    } 
    int k = i - ++j;
    return (k > 2) ? (paramString.length() / 4 * 3) : (paramString.length() / 4 * 3 - k);
  }
  
  public static byte[] _parseBase64Binary(String paramString) {
    int i = guessLength(paramString);
    byte[] arrayOfByte1 = new byte[i];
    byte b1 = 0;
    int j = paramString.length();
    byte[] arrayOfByte2 = new byte[4];
    byte b3 = 0;
    for (byte b2 = 0; b2 < j; b2++) {
      char c = paramString.charAt(b2);
      byte b = decodeMap[c];
      if (b != -1)
        arrayOfByte2[b3++] = b; 
      if (b3 == 4) {
        arrayOfByte1[b1++] = (byte)(arrayOfByte2[0] << 2 | arrayOfByte2[1] >> 4);
        if (arrayOfByte2[2] != Byte.MAX_VALUE)
          arrayOfByte1[b1++] = (byte)(arrayOfByte2[1] << 4 | arrayOfByte2[2] >> 2); 
        if (arrayOfByte2[3] != Byte.MAX_VALUE)
          arrayOfByte1[b1++] = (byte)(arrayOfByte2[2] << 6 | arrayOfByte2[3]); 
        b3 = 0;
      } 
    } 
    if (i == b1)
      return arrayOfByte1; 
    byte[] arrayOfByte3 = new byte[b1];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, b1);
    return arrayOfByte3;
  }
  
  private static char[] initEncodeMap() {
    char[] arrayOfChar = new char[64];
    byte b;
    for (b = 0; b < 26; b++)
      arrayOfChar[b] = (char)(65 + b); 
    for (b = 26; b < 52; b++)
      arrayOfChar[b] = (char)(97 + b - 26); 
    for (b = 52; b < 62; b++)
      arrayOfChar[b] = (char)(48 + b - 52); 
    arrayOfChar[62] = '+';
    arrayOfChar[63] = '/';
    return arrayOfChar;
  }
  
  public static char encode(int paramInt) { return encodeMap[paramInt & 0x3F]; }
  
  public static byte encodeByte(int paramInt) { return (byte)encodeMap[paramInt & 0x3F]; }
  
  public static String _printBase64Binary(byte[] paramArrayOfByte) { return _printBase64Binary(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public static String _printBase64Binary(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    char[] arrayOfChar = new char[(paramInt2 + 2) / 3 * 4];
    int i = _printBase64Binary(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar, 0);
    assert i == arrayOfChar.length;
    return new String(arrayOfChar);
  }
  
  public static int _printBase64Binary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    int i = paramInt2;
    int j;
    for (j = paramInt1; i >= 3; j += 3) {
      paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[j] >> 2);
      paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[j + 1] >> 4 & 0xF);
      paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[j + 1] & 0xF) << 2 | paramArrayOfByte[j + 2] >> 6 & 0x3);
      paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[j + 2] & 0x3F);
      i -= 3;
    } 
    if (i == 1) {
      paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[j] >> 2);
      paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[j] & 0x3) << 4);
      paramArrayOfChar[paramInt3++] = '=';
      paramArrayOfChar[paramInt3++] = '=';
    } 
    if (i == 2) {
      paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[j] >> 2);
      paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[j + 1] >> 4 & 0xF);
      paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[j + 1] & 0xF) << 2);
      paramArrayOfChar[paramInt3++] = '=';
    } 
    return paramInt3;
  }
  
  public static void _printBase64Binary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    int i = paramInt2;
    char[] arrayOfChar = new char[4];
    int j;
    for (j = paramInt1; i >= 3; j += 3) {
      arrayOfChar[0] = encode(paramArrayOfByte[j] >> 2);
      arrayOfChar[1] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[j + 1] >> 4 & 0xF);
      arrayOfChar[2] = encode((paramArrayOfByte[j + 1] & 0xF) << 2 | paramArrayOfByte[j + 2] >> 6 & 0x3);
      arrayOfChar[3] = encode(paramArrayOfByte[j + 2] & 0x3F);
      paramXMLStreamWriter.writeCharacters(arrayOfChar, 0, 4);
      i -= 3;
    } 
    if (i == 1) {
      arrayOfChar[0] = encode(paramArrayOfByte[j] >> 2);
      arrayOfChar[1] = encode((paramArrayOfByte[j] & 0x3) << 4);
      arrayOfChar[2] = '=';
      arrayOfChar[3] = '=';
      paramXMLStreamWriter.writeCharacters(arrayOfChar, 0, 4);
    } 
    if (i == 2) {
      arrayOfChar[0] = encode(paramArrayOfByte[j] >> 2);
      arrayOfChar[1] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[j + 1] >> 4 & 0xF);
      arrayOfChar[2] = encode((paramArrayOfByte[j + 1] & 0xF) << 2);
      arrayOfChar[3] = '=';
      paramXMLStreamWriter.writeCharacters(arrayOfChar, 0, 4);
    } 
  }
  
  public static int _printBase64Binary(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3) {
    byte[] arrayOfByte = paramArrayOfByte2;
    int i = paramInt2;
    int j;
    for (j = paramInt1; i >= 3; j += 3) {
      arrayOfByte[paramInt3++] = encodeByte(paramArrayOfByte1[j] >> 2);
      arrayOfByte[paramInt3++] = encodeByte((paramArrayOfByte1[j] & 0x3) << 4 | paramArrayOfByte1[j + 1] >> 4 & 0xF);
      arrayOfByte[paramInt3++] = encodeByte((paramArrayOfByte1[j + 1] & 0xF) << 2 | paramArrayOfByte1[j + 2] >> 6 & 0x3);
      arrayOfByte[paramInt3++] = encodeByte(paramArrayOfByte1[j + 2] & 0x3F);
      i -= 3;
    } 
    if (i == 1) {
      arrayOfByte[paramInt3++] = encodeByte(paramArrayOfByte1[j] >> 2);
      arrayOfByte[paramInt3++] = encodeByte((paramArrayOfByte1[j] & 0x3) << 4);
      arrayOfByte[paramInt3++] = 61;
      arrayOfByte[paramInt3++] = 61;
    } 
    if (i == 2) {
      arrayOfByte[paramInt3++] = encodeByte(paramArrayOfByte1[j] >> 2);
      arrayOfByte[paramInt3++] = encodeByte((paramArrayOfByte1[j] & 0x3) << 4 | paramArrayOfByte1[j + 1] >> 4 & 0xF);
      arrayOfByte[paramInt3++] = encodeByte((paramArrayOfByte1[j + 1] & 0xF) << 2);
      arrayOfByte[paramInt3++] = 61;
    } 
    return paramInt3;
  }
  
  private static CharSequence removeOptionalPlus(CharSequence paramCharSequence) {
    int i = paramCharSequence.length();
    if (i <= 1 || paramCharSequence.charAt(0) != '+')
      return paramCharSequence; 
    paramCharSequence = paramCharSequence.subSequence(1, i);
    char c = paramCharSequence.charAt(0);
    if ('0' <= c && c <= '9')
      return paramCharSequence; 
    if ('.' == c)
      return paramCharSequence; 
    throw new NumberFormatException();
  }
  
  private static boolean isDigitOrPeriodOrSign(char paramChar) { return ('0' <= paramChar && paramChar <= '9') ? true : ((paramChar == '+' || paramChar == '-' || paramChar == '.')); }
  
  public static DatatypeFactory getDatatypeFactory() {
    ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return Thread.currentThread().getContextClassLoader(); }
        });
    DatatypeFactory datatypeFactory = (DatatypeFactory)DF_CACHE.get(classLoader);
    if (datatypeFactory == null)
      synchronized (DatatypeConverterImpl.class) {
        datatypeFactory = (DatatypeFactory)DF_CACHE.get(classLoader);
        if (datatypeFactory == null) {
          try {
            datatypeFactory = DatatypeFactory.newInstance();
          } catch (DatatypeConfigurationException datatypeConfigurationException) {
            throw new Error(Messages.FAILED_TO_INITIALE_DATATYPE_FACTORY.format(new Object[0]), datatypeConfigurationException);
          } 
          DF_CACHE.put(classLoader, datatypeFactory);
        } 
      }  
    return datatypeFactory;
  }
  
  @Deprecated
  public String parseString(String paramString) { return paramString; }
  
  @Deprecated
  public BigInteger parseInteger(String paramString) { return _parseInteger(paramString); }
  
  @Deprecated
  public String printInteger(BigInteger paramBigInteger) { return _printInteger(paramBigInteger); }
  
  @Deprecated
  public int parseInt(String paramString) { return _parseInt(paramString); }
  
  @Deprecated
  public long parseLong(String paramString) { return _parseLong(paramString); }
  
  @Deprecated
  public short parseShort(String paramString) { return _parseShort(paramString); }
  
  @Deprecated
  public String printShort(short paramShort) { return _printShort(paramShort); }
  
  @Deprecated
  public BigDecimal parseDecimal(String paramString) { return _parseDecimal(paramString); }
  
  @Deprecated
  public float parseFloat(String paramString) { return _parseFloat(paramString); }
  
  @Deprecated
  public String printFloat(float paramFloat) { return _printFloat(paramFloat); }
  
  @Deprecated
  public double parseDouble(String paramString) { return _parseDouble(paramString); }
  
  @Deprecated
  public boolean parseBoolean(String paramString) {
    Boolean bool = _parseBoolean(paramString);
    return (bool == null) ? false : bool.booleanValue();
  }
  
  @Deprecated
  public String printBoolean(boolean paramBoolean) { return paramBoolean ? "true" : "false"; }
  
  @Deprecated
  public byte parseByte(String paramString) { return _parseByte(paramString); }
  
  @Deprecated
  public String printByte(byte paramByte) { return _printByte(paramByte); }
  
  @Deprecated
  public QName parseQName(String paramString, NamespaceContext paramNamespaceContext) { return _parseQName(paramString, paramNamespaceContext); }
  
  @Deprecated
  public Calendar parseDateTime(String paramString) { return _parseDateTime(paramString); }
  
  @Deprecated
  public String printDateTime(Calendar paramCalendar) { return _printDateTime(paramCalendar); }
  
  @Deprecated
  public byte[] parseBase64Binary(String paramString) { return _parseBase64Binary(paramString); }
  
  @Deprecated
  public byte[] parseHexBinary(String paramString) {
    int i = paramString.length();
    if (i % 2 != 0)
      throw new IllegalArgumentException("hexBinary needs to be even-length: " + paramString); 
    byte[] arrayOfByte = new byte[i / 2];
    for (byte b = 0; b < i; b += 2) {
      int j = hexToBin(paramString.charAt(b));
      int k = hexToBin(paramString.charAt(b + 1));
      if (j == -1 || k == -1)
        throw new IllegalArgumentException("contains illegal character for hexBinary: " + paramString); 
      arrayOfByte[b / 2] = (byte)(j * 16 + k);
    } 
    return arrayOfByte;
  }
  
  @Deprecated
  private static int hexToBin(char paramChar) { return ('0' <= paramChar && paramChar <= '9') ? (paramChar - '0') : (('A' <= paramChar && paramChar <= 'F') ? (paramChar - 'A' + '\n') : (('a' <= paramChar && paramChar <= 'f') ? (paramChar - 'a' + '\n') : -1)); }
  
  @Deprecated
  public String printHexBinary(byte[] paramArrayOfByte) {
    StringBuilder stringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
    for (byte b : paramArrayOfByte) {
      stringBuilder.append(hexCode[b >> 4 & 0xF]);
      stringBuilder.append(hexCode[b & 0xF]);
    } 
    return stringBuilder.toString();
  }
  
  @Deprecated
  public long parseUnsignedInt(String paramString) { return _parseLong(paramString); }
  
  @Deprecated
  public String printUnsignedInt(long paramLong) { return _printLong(paramLong); }
  
  @Deprecated
  public int parseUnsignedShort(String paramString) { return _parseInt(paramString); }
  
  @Deprecated
  public Calendar parseTime(String paramString) { return getDatatypeFactory().newXMLGregorianCalendar(paramString).toGregorianCalendar(); }
  
  @Deprecated
  public String printTime(Calendar paramCalendar) { return CalendarFormatter.doFormat("%h:%m:%s%z", paramCalendar); }
  
  @Deprecated
  public Calendar parseDate(String paramString) { return getDatatypeFactory().newXMLGregorianCalendar(paramString).toGregorianCalendar(); }
  
  @Deprecated
  public String printDate(Calendar paramCalendar) { return _printDate(paramCalendar); }
  
  @Deprecated
  public String parseAnySimpleType(String paramString) { return paramString; }
  
  @Deprecated
  public String printString(String paramString) { return paramString; }
  
  @Deprecated
  public String printInt(int paramInt) { return _printInt(paramInt); }
  
  @Deprecated
  public String printLong(long paramLong) { return _printLong(paramLong); }
  
  @Deprecated
  public String printDecimal(BigDecimal paramBigDecimal) { return _printDecimal(paramBigDecimal); }
  
  @Deprecated
  public String printDouble(double paramDouble) { return _printDouble(paramDouble); }
  
  @Deprecated
  public String printQName(QName paramQName, NamespaceContext paramNamespaceContext) { return _printQName(paramQName, paramNamespaceContext); }
  
  @Deprecated
  public String printBase64Binary(byte[] paramArrayOfByte) { return _printBase64Binary(paramArrayOfByte); }
  
  @Deprecated
  public String printUnsignedShort(int paramInt) { return String.valueOf(paramInt); }
  
  @Deprecated
  public String printAnySimpleType(String paramString) { return paramString; }
  
  private static final class CalendarFormatter {
    public static String doFormat(String param1String, Calendar param1Calendar) throws IllegalArgumentException {
      byte b = 0;
      int i = param1String.length();
      StringBuilder stringBuilder = new StringBuilder();
      while (b < i) {
        char c = param1String.charAt(b++);
        if (c != '%') {
          stringBuilder.append(c);
          continue;
        } 
        switch (param1String.charAt(b++)) {
          case 'Y':
            formatYear(param1Calendar, stringBuilder);
            continue;
          case 'M':
            formatMonth(param1Calendar, stringBuilder);
            continue;
          case 'D':
            formatDays(param1Calendar, stringBuilder);
            continue;
          case 'h':
            formatHours(param1Calendar, stringBuilder);
            continue;
          case 'm':
            formatMinutes(param1Calendar, stringBuilder);
            continue;
          case 's':
            formatSeconds(param1Calendar, stringBuilder);
            continue;
          case 'z':
            formatTimeZone(param1Calendar, stringBuilder);
            continue;
        } 
        throw new InternalError();
      } 
      return stringBuilder.toString();
    }
    
    private static void formatYear(Calendar param1Calendar, StringBuilder param1StringBuilder) {
      String str;
      int i = param1Calendar.get(1);
      if (i <= 0) {
        str = Integer.toString(1 - i);
      } else {
        str = Integer.toString(i);
      } 
      while (str.length() < 4)
        str = '0' + str; 
      if (i <= 0)
        str = '-' + str; 
      param1StringBuilder.append(str);
    }
    
    private static void formatMonth(Calendar param1Calendar, StringBuilder param1StringBuilder) { formatTwoDigits(param1Calendar.get(2) + 1, param1StringBuilder); }
    
    private static void formatDays(Calendar param1Calendar, StringBuilder param1StringBuilder) { formatTwoDigits(param1Calendar.get(5), param1StringBuilder); }
    
    private static void formatHours(Calendar param1Calendar, StringBuilder param1StringBuilder) { formatTwoDigits(param1Calendar.get(11), param1StringBuilder); }
    
    private static void formatMinutes(Calendar param1Calendar, StringBuilder param1StringBuilder) { formatTwoDigits(param1Calendar.get(12), param1StringBuilder); }
    
    private static void formatSeconds(Calendar param1Calendar, StringBuilder param1StringBuilder) {
      formatTwoDigits(param1Calendar.get(13), param1StringBuilder);
      if (param1Calendar.isSet(14)) {
        int i = param1Calendar.get(14);
        if (i != 0) {
          String str;
          for (str = Integer.toString(i); str.length() < 3; str = '0' + str);
          param1StringBuilder.append('.');
          param1StringBuilder.append(str);
        } 
      } 
    }
    
    private static void formatTimeZone(Calendar param1Calendar, StringBuilder param1StringBuilder) {
      TimeZone timeZone = param1Calendar.getTimeZone();
      if (timeZone == null)
        return; 
      int i = timeZone.getOffset(param1Calendar.getTime().getTime());
      if (i == 0) {
        param1StringBuilder.append('Z');
        return;
      } 
      if (i >= 0) {
        param1StringBuilder.append('+');
      } else {
        param1StringBuilder.append('-');
        i *= -1;
      } 
      i /= 60000;
      formatTwoDigits(i / 60, param1StringBuilder);
      param1StringBuilder.append(':');
      formatTwoDigits(i % 60, param1StringBuilder);
    }
    
    private static void formatTwoDigits(int param1Int, StringBuilder param1StringBuilder) {
      if (param1Int < 10)
        param1StringBuilder.append('0'); 
      param1StringBuilder.append(param1Int);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\DatatypeConverterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */