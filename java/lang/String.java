package java.lang;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class String extends Object implements Serializable, Comparable<String>, CharSequence {
  private final char[] value;
  
  private int hash;
  
  private static final long serialVersionUID = -6849794470754667710L;
  
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
  
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator(null);
  
  public String() { this.value = "".value; }
  
  public String(String paramString) {
    this.value = paramString.value;
    this.hash = paramString.hash;
  }
  
  public String(char[] paramArrayOfChar) { this.value = Arrays.copyOf(paramArrayOfChar, paramArrayOfChar.length); }
  
  public String(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 <= 0) {
      if (paramInt2 < 0)
        throw new StringIndexOutOfBoundsException(paramInt2); 
      if (paramInt1 <= paramArrayOfChar.length) {
        this.value = "".value;
        return;
      } 
    } 
    if (paramInt1 > paramArrayOfChar.length - paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt1 + paramInt2); 
    this.value = Arrays.copyOfRange(paramArrayOfChar, paramInt1, paramInt1 + paramInt2);
  }
  
  public String(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 <= 0) {
      if (paramInt2 < 0)
        throw new StringIndexOutOfBoundsException(paramInt2); 
      if (paramInt1 <= paramArrayOfInt.length) {
        this.value = "".value;
        return;
      } 
    } 
    if (paramInt1 > paramArrayOfInt.length - paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt1 + paramInt2); 
    int i = paramInt1 + paramInt2;
    int j = paramInt2;
    for (int k = paramInt1; k < i; k++) {
      int n = paramArrayOfInt[k];
      if (!Character.isBmpCodePoint(n))
        if (Character.isValidCodePoint(n)) {
          j++;
        } else {
          throw new IllegalArgumentException(Integer.toString(n));
        }  
    } 
    char[] arrayOfChar = new char[j];
    int m = paramInt1;
    for (byte b = 0; m < i; b++) {
      int n = paramArrayOfInt[m];
      if (Character.isBmpCodePoint(n)) {
        arrayOfChar[b] = (char)n;
      } else {
        Character.toSurrogates(n, arrayOfChar, b++);
      } 
      m++;
    } 
    this.value = arrayOfChar;
  }
  
  @Deprecated
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
    checkBounds(paramArrayOfByte, paramInt2, paramInt3);
    char[] arrayOfChar = new char[paramInt3];
    if (paramInt1 == 0) {
      int i = paramInt3;
      while (i-- > 0)
        arrayOfChar[i] = (char)(paramArrayOfByte[i + paramInt2] & 0xFF); 
    } else {
      paramInt1 <<= 8;
      int i = paramInt3;
      while (i-- > 0)
        arrayOfChar[i] = (char)(paramInt1 | paramArrayOfByte[i + paramInt2] & 0xFF); 
    } 
    this.value = arrayOfChar;
  }
  
  @Deprecated
  public String(byte[] paramArrayOfByte, int paramInt) { this(paramArrayOfByte, paramInt, 0, paramArrayOfByte.length); }
  
  private static void checkBounds(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 < 0)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt1 + paramInt2); 
  }
  
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString) throws UnsupportedEncodingException {
    if (paramString == null)
      throw new NullPointerException("charsetName"); 
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    this.value = StringCoding.decode(paramString, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Charset paramCharset) {
    if (paramCharset == null)
      throw new NullPointerException("charset"); 
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    this.value = StringCoding.decode(paramCharset, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String(byte[] paramArrayOfByte, String paramString) throws UnsupportedEncodingException { this(paramArrayOfByte, 0, paramArrayOfByte.length, paramString); }
  
  public String(byte[] paramArrayOfByte, Charset paramCharset) { this(paramArrayOfByte, 0, paramArrayOfByte.length, paramCharset); }
  
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    this.value = StringCoding.decode(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String(byte[] paramArrayOfByte) { this(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public String(StringBuffer paramStringBuffer) {
    synchronized (paramStringBuffer) {
      this.value = Arrays.copyOf(paramStringBuffer.getValue(), paramStringBuffer.length());
    } 
  }
  
  public String(StringBuilder paramStringBuilder) { this.value = Arrays.copyOf(paramStringBuilder.getValue(), paramStringBuilder.length()); }
  
  String(char[] paramArrayOfChar, boolean paramBoolean) { this.value = paramArrayOfChar; }
  
  public int length() { return this.value.length; }
  
  public boolean isEmpty() { return (this.value.length == 0); }
  
  public char charAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.value.length)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return this.value[paramInt];
  }
  
  public int codePointAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.value.length)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return Character.codePointAtImpl(this.value, paramInt, this.value.length);
  }
  
  public int codePointBefore(int paramInt) {
    int i = paramInt - 1;
    if (i < 0 || i >= this.value.length)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return Character.codePointBeforeImpl(this.value, paramInt, 0);
  }
  
  public int codePointCount(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 > this.value.length || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException(); 
    return Character.codePointCountImpl(this.value, paramInt1, paramInt2 - paramInt1);
  }
  
  public int offsetByCodePoints(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > this.value.length)
      throw new IndexOutOfBoundsException(); 
    return Character.offsetByCodePointsImpl(this.value, 0, this.value.length, paramInt1, paramInt2);
  }
  
  void getChars(char[] paramArrayOfChar, int paramInt) { System.arraycopy(this.value, 0, paramArrayOfChar, paramInt, this.value.length); }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > this.value.length)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1); 
    System.arraycopy(this.value, paramInt1, paramArrayOfChar, paramInt3, paramInt2 - paramInt1);
  }
  
  @Deprecated
  public void getBytes(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > this.value.length)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1); 
    Objects.requireNonNull(paramArrayOfByte);
    int i = paramInt3;
    int j = paramInt2;
    int k = paramInt1;
    char[] arrayOfChar = this.value;
    while (k < j)
      paramArrayOfByte[i++] = (byte)arrayOfChar[k++]; 
  }
  
  public byte[] getBytes(String paramString) throws UnsupportedEncodingException {
    if (paramString == null)
      throw new NullPointerException(); 
    return StringCoding.encode(paramString, this.value, 0, this.value.length);
  }
  
  public byte[] getBytes(Charset paramCharset) {
    if (paramCharset == null)
      throw new NullPointerException(); 
    return StringCoding.encode(paramCharset, this.value, 0, this.value.length);
  }
  
  public byte[] getBytes() { return StringCoding.encode(this.value, 0, this.value.length); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof String) {
      String str = (String)paramObject;
      int i = this.value.length;
      if (i == str.value.length) {
        char[] arrayOfChar1 = this.value;
        char[] arrayOfChar2 = str.value;
        for (byte b = 0; i-- != 0; b++) {
          if (arrayOfChar1[b] != arrayOfChar2[b])
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public boolean contentEquals(StringBuffer paramStringBuffer) { return contentEquals(paramStringBuffer); }
  
  private boolean nonSyncContentEquals(AbstractStringBuilder paramAbstractStringBuilder) {
    char[] arrayOfChar1 = this.value;
    char[] arrayOfChar2 = paramAbstractStringBuilder.getValue();
    int i = arrayOfChar1.length;
    if (i != paramAbstractStringBuilder.length())
      return false; 
    for (byte b = 0; b < i; b++) {
      if (arrayOfChar1[b] != arrayOfChar2[b])
        return false; 
    } 
    return true;
  }
  
  public boolean contentEquals(CharSequence paramCharSequence) {
    if (paramCharSequence instanceof AbstractStringBuilder) {
      if (paramCharSequence instanceof StringBuffer)
        synchronized (paramCharSequence) {
          return nonSyncContentEquals((AbstractStringBuilder)paramCharSequence);
        }  
      return nonSyncContentEquals((AbstractStringBuilder)paramCharSequence);
    } 
    if (paramCharSequence instanceof String)
      return equals(paramCharSequence); 
    char[] arrayOfChar = this.value;
    int i = arrayOfChar.length;
    if (i != paramCharSequence.length())
      return false; 
    for (byte b = 0; b < i; b++) {
      if (arrayOfChar[b] != paramCharSequence.charAt(b))
        return false; 
    } 
    return true;
  }
  
  public boolean equalsIgnoreCase(String paramString) { return (this == paramString) ? true : ((paramString != null && paramString.value.length == this.value.length && regionMatches(true, 0, paramString, 0, this.value.length))); }
  
  public int compareTo(String paramString) {
    int i = this.value.length;
    int j = paramString.value.length;
    int k = Math.min(i, j);
    char[] arrayOfChar1 = this.value;
    char[] arrayOfChar2 = paramString.value;
    for (byte b = 0; b < k; b++) {
      char c1 = arrayOfChar1[b];
      char c2 = arrayOfChar2[b];
      if (c1 != c2)
        return c1 - c2; 
    } 
    return i - j;
  }
  
  public int compareToIgnoreCase(String paramString) { return CASE_INSENSITIVE_ORDER.compare(this, paramString); }
  
  public boolean regionMatches(int paramInt1, String paramString, int paramInt2, int paramInt3) {
    char[] arrayOfChar1 = this.value;
    int i = paramInt1;
    char[] arrayOfChar2 = paramString.value;
    int j = paramInt2;
    if (paramInt2 < 0 || paramInt1 < 0 || paramInt1 > this.value.length - paramInt3 || paramInt2 > paramString.value.length - paramInt3)
      return false; 
    while (paramInt3-- > 0) {
      if (arrayOfChar1[i++] != arrayOfChar2[j++])
        return false; 
    } 
    return true;
  }
  
  public boolean regionMatches(boolean paramBoolean, int paramInt1, String paramString, int paramInt2, int paramInt3) {
    char[] arrayOfChar1 = this.value;
    int i = paramInt1;
    char[] arrayOfChar2 = paramString.value;
    int j = paramInt2;
    if (paramInt2 < 0 || paramInt1 < 0 || paramInt1 > this.value.length - paramInt3 || paramInt2 > paramString.value.length - paramInt3)
      return false; 
    while (paramInt3-- > 0) {
      char c1 = arrayOfChar1[i++];
      char c2 = arrayOfChar2[j++];
      if (c1 == c2)
        continue; 
      if (paramBoolean) {
        char c3 = Character.toUpperCase(c1);
        char c4 = Character.toUpperCase(c2);
        if (c3 == c4 || Character.toLowerCase(c3) == Character.toLowerCase(c4))
          continue; 
      } 
      return false;
    } 
    return true;
  }
  
  public boolean startsWith(String paramString, int paramInt) {
    char[] arrayOfChar1 = this.value;
    int i = paramInt;
    char[] arrayOfChar2 = paramString.value;
    byte b = 0;
    int j = paramString.value.length;
    if (paramInt < 0 || paramInt > this.value.length - j)
      return false; 
    while (--j >= 0) {
      if (arrayOfChar1[i++] != arrayOfChar2[b++])
        return false; 
    } 
    return true;
  }
  
  public boolean startsWith(String paramString) { return startsWith(paramString, 0); }
  
  public boolean endsWith(String paramString) { return startsWith(paramString, this.value.length - paramString.value.length); }
  
  public int hashCode() {
    int i = this.hash;
    if (i == 0 && this.value.length > 0) {
      char[] arrayOfChar = this.value;
      for (byte b = 0; b < this.value.length; b++)
        i = 31 * i + arrayOfChar[b]; 
      this.hash = i;
    } 
    return i;
  }
  
  public int indexOf(int paramInt) { return indexOf(paramInt, 0); }
  
  public int indexOf(int paramInt1, int paramInt2) {
    int i = this.value.length;
    if (paramInt2 < 0) {
      paramInt2 = 0;
    } else if (paramInt2 >= i) {
      return -1;
    } 
    if (paramInt1 < 65536) {
      char[] arrayOfChar = this.value;
      for (int j = paramInt2; j < i; j++) {
        if (arrayOfChar[j] == paramInt1)
          return j; 
      } 
      return -1;
    } 
    return indexOfSupplementary(paramInt1, paramInt2);
  }
  
  private int indexOfSupplementary(int paramInt1, int paramInt2) {
    if (Character.isValidCodePoint(paramInt1)) {
      char[] arrayOfChar = this.value;
      char c1 = Character.highSurrogate(paramInt1);
      char c2 = Character.lowSurrogate(paramInt1);
      int i = arrayOfChar.length - 1;
      for (int j = paramInt2; j < i; j++) {
        if (arrayOfChar[j] == c1 && arrayOfChar[j + 1] == c2)
          return j; 
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(int paramInt) { return lastIndexOf(paramInt, this.value.length - 1); }
  
  public int lastIndexOf(int paramInt1, int paramInt2) {
    if (paramInt1 < 65536) {
      char[] arrayOfChar = this.value;
      for (int i = Math.min(paramInt2, arrayOfChar.length - 1); i >= 0; i--) {
        if (arrayOfChar[i] == paramInt1)
          return i; 
      } 
      return -1;
    } 
    return lastIndexOfSupplementary(paramInt1, paramInt2);
  }
  
  private int lastIndexOfSupplementary(int paramInt1, int paramInt2) {
    if (Character.isValidCodePoint(paramInt1)) {
      char[] arrayOfChar = this.value;
      char c1 = Character.highSurrogate(paramInt1);
      char c2 = Character.lowSurrogate(paramInt1);
      for (int i = Math.min(paramInt2, arrayOfChar.length - 2); i >= 0; i--) {
        if (arrayOfChar[i] == c1 && arrayOfChar[i + 1] == c2)
          return i; 
      } 
    } 
    return -1;
  }
  
  public int indexOf(String paramString) { return indexOf(paramString, 0); }
  
  public int indexOf(String paramString, int paramInt) { return indexOf(this.value, 0, this.value.length, paramString.value, 0, paramString.value.length, paramInt); }
  
  static int indexOf(char[] paramArrayOfChar, int paramInt1, int paramInt2, String paramString, int paramInt3) { return indexOf(paramArrayOfChar, paramInt1, paramInt2, paramString.value, 0, paramString.value.length, paramInt3); }
  
  static int indexOf(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5) {
    if (paramInt5 >= paramInt2)
      return (paramInt4 == 0) ? paramInt2 : -1; 
    if (paramInt5 < 0)
      paramInt5 = 0; 
    if (paramInt4 == 0)
      return paramInt5; 
    char c = paramArrayOfChar2[paramInt3];
    int i = paramInt1 + paramInt2 - paramInt4;
    for (int j = paramInt1 + paramInt5; j <= i; j++) {
      if (paramArrayOfChar1[j] != c)
        while (++j <= i && paramArrayOfChar1[j] != c); 
      if (j <= i) {
        int k = j + 1;
        int m = k + paramInt4 - 1;
        for (int n = paramInt3 + 1; k < m && paramArrayOfChar1[k] == paramArrayOfChar2[n]; n++)
          k++; 
        if (k == m)
          return j - paramInt1; 
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(String paramString) { return lastIndexOf(paramString, this.value.length); }
  
  public int lastIndexOf(String paramString, int paramInt) { return lastIndexOf(this.value, 0, this.value.length, paramString.value, 0, paramString.value.length, paramInt); }
  
  static int lastIndexOf(char[] paramArrayOfChar, int paramInt1, int paramInt2, String paramString, int paramInt3) { return lastIndexOf(paramArrayOfChar, paramInt1, paramInt2, paramString.value, 0, paramString.value.length, paramInt3); }
  
  static int lastIndexOf(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5) {
    int n;
    int i = paramInt2 - paramInt4;
    if (paramInt5 < 0)
      return -1; 
    if (paramInt5 > i)
      paramInt5 = i; 
    if (paramInt4 == 0)
      return paramInt5; 
    int j = paramInt3 + paramInt4 - 1;
    char c = paramArrayOfChar2[j];
    int k = paramInt1 + paramInt4 - 1;
    int m = k + paramInt5;
    while (true) {
      if (m >= k && paramArrayOfChar1[m] != c) {
        m--;
        continue;
      } 
      if (m < k)
        return -1; 
      int i1 = m - 1;
      n = i1 - paramInt4 - 1;
      int i2 = j - 1;
      while (i1 > n) {
        if (paramArrayOfChar1[i1--] != paramArrayOfChar2[i2--])
          m--; 
      } 
      break;
    } 
    return n - paramInt1 + 1;
  }
  
  public String substring(int paramInt) {
    if (paramInt < 0)
      throw new StringIndexOutOfBoundsException(paramInt); 
    int i = this.value.length - paramInt;
    if (i < 0)
      throw new StringIndexOutOfBoundsException(i); 
    return (paramInt == 0) ? this : new String(this.value, paramInt, i);
  }
  
  public String substring(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > this.value.length)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new StringIndexOutOfBoundsException(i); 
    return (paramInt1 == 0 && paramInt2 == this.value.length) ? this : new String(this.value, paramInt1, i);
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) { return substring(paramInt1, paramInt2); }
  
  public String concat(String paramString) {
    int i = paramString.length();
    if (i == 0)
      return this; 
    int j = this.value.length;
    char[] arrayOfChar = Arrays.copyOf(this.value, j + i);
    paramString.getChars(arrayOfChar, j);
    return new String(arrayOfChar, true);
  }
  
  public String replace(char paramChar1, char paramChar2) {
    if (paramChar1 != paramChar2) {
      int i = this.value.length;
      byte b = -1;
      char[] arrayOfChar = this.value;
      do {
      
      } while (++b < i && arrayOfChar[b] != paramChar1);
      if (b < i) {
        char[] arrayOfChar1 = new char[i];
        char c;
        for (c = Character.MIN_VALUE; c < b; c++)
          arrayOfChar1[c] = arrayOfChar[c]; 
        while (b < i) {
          c = arrayOfChar[b];
          arrayOfChar1[b] = (c == paramChar1) ? paramChar2 : c;
          b++;
        } 
        return new String(arrayOfChar1, true);
      } 
    } 
    return this;
  }
  
  public boolean matches(String paramString) { return Pattern.matches(paramString, this); }
  
  public boolean contains(CharSequence paramCharSequence) { return (indexOf(paramCharSequence.toString()) > -1); }
  
  public String replaceFirst(String paramString1, String paramString2) { return Pattern.compile(paramString1).matcher(this).replaceFirst(paramString2); }
  
  public String replaceAll(String paramString1, String paramString2) { return Pattern.compile(paramString1).matcher(this).replaceAll(paramString2); }
  
  public String replace(CharSequence paramCharSequence1, CharSequence paramCharSequence2) { return Pattern.compile(paramCharSequence1.toString(), 16).matcher(this).replaceAll(Matcher.quoteReplacement(paramCharSequence2.toString())); }
  
  public String[] split(String paramString, int paramInt) {
    char c = Character.MIN_VALUE;
    if (((paramString.value.length == 1 && ".$|()[{^?*+\\".indexOf(c = paramString.charAt(0)) == -1) || (paramString.length() == 2 && paramString.charAt(0) == '\\' && ((c = paramString.charAt(1)) - '0' | '9' - c) < '\000' && (c - 'a' | 'z' - c) < '\000' && (c - 'A' | 'Z' - c) < '\000')) && (c < '?' || c > '?')) {
      int i = 0;
      int j = 0;
      boolean bool = (paramInt > 0) ? 1 : 0;
      ArrayList arrayList = new ArrayList();
      while ((j = indexOf(c, i)) != -1) {
        if (!bool || arrayList.size() < paramInt - 1) {
          arrayList.add(substring(i, j));
          i = j + 1;
          continue;
        } 
        arrayList.add(substring(i, this.value.length));
        i = this.value.length;
      } 
      if (i == 0)
        return new String[] { this }; 
      if (!bool || arrayList.size() < paramInt)
        arrayList.add(substring(i, this.value.length)); 
      int k = arrayList.size();
      if (paramInt == 0)
        while (k > 0 && ((String)arrayList.get(k - 1)).length() == 0)
          k--;  
      String[] arrayOfString = new String[k];
      return (String[])arrayList.subList(0, k).toArray(arrayOfString);
    } 
    return Pattern.compile(paramString).split(this, paramInt);
  }
  
  public String[] split(String paramString) { return split(paramString, 0); }
  
  public static String join(CharSequence paramCharSequence, CharSequence... paramVarArgs) {
    Objects.requireNonNull(paramCharSequence);
    Objects.requireNonNull(paramVarArgs);
    StringJoiner stringJoiner = new StringJoiner(paramCharSequence);
    for (CharSequence charSequence : paramVarArgs)
      stringJoiner.add(charSequence); 
    return stringJoiner.toString();
  }
  
  public static String join(CharSequence paramCharSequence, Iterable<? extends CharSequence> paramIterable) {
    Objects.requireNonNull(paramCharSequence);
    Objects.requireNonNull(paramIterable);
    StringJoiner stringJoiner = new StringJoiner(paramCharSequence);
    for (CharSequence charSequence : paramIterable)
      stringJoiner.add(charSequence); 
    return stringJoiner.toString();
  }
  
  public String toLowerCase(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    int j = this.value.length;
    int i = 0;
    while (i < j) {
      char c = this.value[i];
      if (c >= '?' && c <= '?') {
        int n = codePointAt(i);
        if (n == Character.toLowerCase(n)) {
          i += Character.charCount(n);
          continue;
        } 
      } else if (c == Character.toLowerCase(c)) {
        i++;
        continue;
      } 
      char[] arrayOfChar = new char[j];
      int k = 0;
      System.arraycopy(this.value, 0, arrayOfChar, 0, i);
      String str = paramLocale.getLanguage();
      boolean bool = (str == "tr" || str == "az" || str == "lt") ? 1 : 0;
      int m = i;
      while (true) {
        char[] arrayOfChar1;
        if (m < j) {
          int i3;
          int i1;
          int i2 = this.value[m];
          if ((char)i2 >= '?' && (char)i2 <= '?') {
            i2 = codePointAt(m);
            i3 = Character.charCount(i2);
          } else {
            i3 = 1;
          } 
          if (bool || i2 == 931 || i2 == 304) {
            i1 = ConditionalSpecialCasing.toLowerCaseEx(this, m, paramLocale);
          } else {
            i1 = Character.toLowerCase(i2);
          } 
          if (i1 == -1 || i1 >= 65536) {
            if (i1 == -1) {
              arrayOfChar1 = ConditionalSpecialCasing.toLowerCaseCharArray(this, m, paramLocale);
            } else {
              if (i3 == 2) {
                k += Character.toChars(i1, arrayOfChar, m + k) - i3;
              } else {
                arrayOfChar1 = Character.toChars(i1);
                int i4 = arrayOfChar1.length;
              } 
              m += i3;
            } 
          } else {
            arrayOfChar[m + k] = (char)i1;
            m += i3;
          } 
        } else {
          break;
        } 
        int n = arrayOfChar1.length;
      } 
      return new String(arrayOfChar, 0, j + k);
    } 
    return this;
  }
  
  public String toLowerCase() { return toLowerCase(Locale.getDefault()); }
  
  public String toUpperCase(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    int i = this.value.length;
    for (char c = Character.MIN_VALUE; c < i; c += arrayOfChar) {
      char[] arrayOfChar;
      int j = this.value[c];
      if (j >= 55296 && j <= 56319) {
        j = codePointAt(c);
        int m = Character.charCount(j);
      } else {
        boolean bool = true;
      } 
      int k = Character.toUpperCaseEx(j);
      if (k == -1 || j != k) {
        j = 0;
        arrayOfChar = new char[i];
        System.arraycopy(this.value, 0, arrayOfChar, 0, c);
        String str = paramLocale.getLanguage();
        boolean bool = (str == "tr" || str == "az" || str == "lt") ? 1 : 0;
        int m = c;
        while (true) {
          char[] arrayOfChar1;
          if (m < i) {
            int i3;
            int i1;
            int i2 = this.value[m];
            if ((char)i2 >= '?' && (char)i2 <= '?') {
              i2 = codePointAt(m);
              i3 = Character.charCount(i2);
            } else {
              i3 = 1;
            } 
            if (bool) {
              i1 = ConditionalSpecialCasing.toUpperCaseEx(this, m, paramLocale);
            } else {
              i1 = Character.toUpperCaseEx(i2);
            } 
            if (i1 == -1 || i1 >= 65536) {
              if (i1 == -1) {
                if (bool) {
                  arrayOfChar1 = ConditionalSpecialCasing.toUpperCaseCharArray(this, m, paramLocale);
                } else {
                  arrayOfChar1 = Character.toUpperCaseCharArray(i2);
                } 
              } else {
                if (i3 == 2) {
                  j += Character.toChars(i1, arrayOfChar, m + j) - i3;
                } else {
                  arrayOfChar1 = Character.toChars(i1);
                  int i4 = arrayOfChar1.length;
                } 
                m += i3;
              } 
            } else {
              arrayOfChar[m + j] = (char)i1;
              m += i3;
            } 
          } else {
            break;
          } 
          int n = arrayOfChar1.length;
        } 
        return new String(arrayOfChar, 0, i + j);
      } 
    } 
    return this;
  }
  
  public String toUpperCase() { return toUpperCase(Locale.getDefault()); }
  
  public String trim() {
    int i = this.value.length;
    byte b = 0;
    char[] arrayOfChar = this.value;
    while (b < i && arrayOfChar[b] <= ' ')
      b++; 
    while (b < i && arrayOfChar[i - 1] <= ' ')
      i--; 
    return (b > 0 || i < this.value.length) ? substring(b, i) : this;
  }
  
  public String toString() { return this; }
  
  public char[] toCharArray() {
    char[] arrayOfChar = new char[this.value.length];
    System.arraycopy(this.value, 0, arrayOfChar, 0, this.value.length);
    return arrayOfChar;
  }
  
  public static String format(String paramString, Object... paramVarArgs) { return (new Formatter()).format(paramString, paramVarArgs).toString(); }
  
  public static String format(Locale paramLocale, String paramString, Object... paramVarArgs) { return (new Formatter(paramLocale)).format(paramString, paramVarArgs).toString(); }
  
  public static String valueOf(Object paramObject) { return (paramObject == null) ? "null" : paramObject.toString(); }
  
  public static String valueOf(char[] paramArrayOfChar) { return new String(paramArrayOfChar); }
  
  public static String valueOf(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return new String(paramArrayOfChar, paramInt1, paramInt2); }
  
  public static String copyValueOf(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return new String(paramArrayOfChar, paramInt1, paramInt2); }
  
  public static String copyValueOf(char[] paramArrayOfChar) { return new String(paramArrayOfChar); }
  
  public static String valueOf(boolean paramBoolean) { return paramBoolean ? "true" : "false"; }
  
  public static String valueOf(char paramChar) {
    char[] arrayOfChar = { paramChar };
    return new String(arrayOfChar, true);
  }
  
  public static String valueOf(int paramInt) { return Integer.toString(paramInt); }
  
  public static String valueOf(long paramLong) { return Long.toString(paramLong); }
  
  public static String valueOf(float paramFloat) { return Float.toString(paramFloat); }
  
  public static String valueOf(double paramDouble) { return Double.toString(paramDouble); }
  
  public native String intern();
  
  private static class CaseInsensitiveComparator extends Object implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 8575799808933029326L;
    
    private CaseInsensitiveComparator() {}
    
    public int compare(String param1String1, String param1String2) {
      int i = param1String1.length();
      int j = param1String2.length();
      int k = Math.min(i, j);
      for (byte b = 0; b < k; b++) {
        char c1 = param1String1.charAt(b);
        char c2 = param1String2.charAt(b);
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2) {
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);
            if (c1 != c2)
              return c1 - c2; 
          } 
        } 
      } 
      return i - j;
    }
    
    private Object readResolve() { return String.CASE_INSENSITIVE_ORDER; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\String.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */