package java.lang;

import java.io.IOException;
import java.util.Arrays;
import sun.misc.FloatingDecimal;

abstract class AbstractStringBuilder implements Appendable, CharSequence {
  char[] value;
  
  int count;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  AbstractStringBuilder() {}
  
  AbstractStringBuilder(int paramInt) { this.value = new char[paramInt]; }
  
  public int length() { return this.count; }
  
  public int capacity() { return this.value.length; }
  
  public void ensureCapacity(int paramInt) {
    if (paramInt > 0)
      ensureCapacityInternal(paramInt); 
  }
  
  private void ensureCapacityInternal(int paramInt) {
    if (paramInt - this.value.length > 0)
      this.value = Arrays.copyOf(this.value, newCapacity(paramInt)); 
  }
  
  private int newCapacity(int paramInt) {
    int i = (this.value.length << 1) + 2;
    if (i - paramInt < 0)
      i = paramInt; 
    return (i <= 0 || 2147483639 - i < 0) ? hugeCapacity(paramInt) : i;
  }
  
  private int hugeCapacity(int paramInt) {
    if (Integer.MAX_VALUE - paramInt < 0)
      throw new OutOfMemoryError(); 
    return (paramInt > 2147483639) ? paramInt : 2147483639;
  }
  
  public void trimToSize() {
    if (this.count < this.value.length)
      this.value = Arrays.copyOf(this.value, this.count); 
  }
  
  public void setLength(int paramInt) {
    if (paramInt < 0)
      throw new StringIndexOutOfBoundsException(paramInt); 
    ensureCapacityInternal(paramInt);
    if (this.count < paramInt)
      Arrays.fill(this.value, this.count, paramInt, false); 
    this.count = paramInt;
  }
  
  public char charAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return this.value[paramInt];
  }
  
  public int codePointAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return Character.codePointAtImpl(this.value, paramInt, this.count);
  }
  
  public int codePointBefore(int paramInt) {
    int i = paramInt - 1;
    if (i < 0 || i >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return Character.codePointBeforeImpl(this.value, paramInt, 0);
  }
  
  public int codePointCount(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 > this.count || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException(); 
    return Character.codePointCountImpl(this.value, paramInt1, paramInt2 - paramInt1);
  }
  
  public int offsetByCodePoints(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > this.count)
      throw new IndexOutOfBoundsException(); 
    return Character.offsetByCodePointsImpl(this.value, 0, this.count, paramInt1, paramInt2);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 < 0 || paramInt2 > this.count)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException("srcBegin > srcEnd"); 
    System.arraycopy(this.value, paramInt1, paramArrayOfChar, paramInt3, paramInt2 - paramInt1);
  }
  
  public void setCharAt(int paramInt, char paramChar) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    this.value[paramInt] = paramChar;
  }
  
  public AbstractStringBuilder append(Object paramObject) { return append(String.valueOf(paramObject)); }
  
  public AbstractStringBuilder append(String paramString) {
    if (paramString == null)
      return appendNull(); 
    int i = paramString.length();
    ensureCapacityInternal(this.count + i);
    paramString.getChars(0, i, this.value, this.count);
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder append(StringBuffer paramStringBuffer) {
    if (paramStringBuffer == null)
      return appendNull(); 
    int i = paramStringBuffer.length();
    ensureCapacityInternal(this.count + i);
    paramStringBuffer.getChars(0, i, this.value, this.count);
    this.count += i;
    return this;
  }
  
  AbstractStringBuilder append(AbstractStringBuilder paramAbstractStringBuilder) {
    if (paramAbstractStringBuilder == null)
      return appendNull(); 
    int i = paramAbstractStringBuilder.length();
    ensureCapacityInternal(this.count + i);
    paramAbstractStringBuilder.getChars(0, i, this.value, this.count);
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder append(CharSequence paramCharSequence) { return (paramCharSequence == null) ? appendNull() : ((paramCharSequence instanceof String) ? append((String)paramCharSequence) : ((paramCharSequence instanceof AbstractStringBuilder) ? append((AbstractStringBuilder)paramCharSequence) : append(paramCharSequence, 0, paramCharSequence.length()))); }
  
  private AbstractStringBuilder appendNull() {
    int i = this.count;
    ensureCapacityInternal(i + 4);
    char[] arrayOfChar = this.value;
    arrayOfChar[i++] = 'n';
    arrayOfChar[i++] = 'u';
    arrayOfChar[i++] = 'l';
    arrayOfChar[i++] = 'l';
    this.count = i;
    return this;
  }
  
  public AbstractStringBuilder append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    String str;
    if (paramCharSequence == null)
      str = "null"; 
    if (paramInt1 < 0 || paramInt1 > paramInt2 || paramInt2 > str.length())
      throw new IndexOutOfBoundsException("start " + paramInt1 + ", end " + paramInt2 + ", s.length() " + str.length()); 
    int i = paramInt2 - paramInt1;
    ensureCapacityInternal(this.count + i);
    int j = paramInt1;
    for (int k = this.count; j < paramInt2; k++) {
      this.value[k] = str.charAt(j);
      j++;
    } 
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder append(char[] paramArrayOfChar) {
    int i = paramArrayOfChar.length;
    ensureCapacityInternal(this.count + i);
    System.arraycopy(paramArrayOfChar, 0, this.value, this.count, i);
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt2 > 0)
      ensureCapacityInternal(this.count + paramInt2); 
    System.arraycopy(paramArrayOfChar, paramInt1, this.value, this.count, paramInt2);
    this.count += paramInt2;
    return this;
  }
  
  public AbstractStringBuilder append(boolean paramBoolean) {
    if (paramBoolean) {
      ensureCapacityInternal(this.count + 4);
      this.value[this.count++] = 't';
      this.value[this.count++] = 'r';
      this.value[this.count++] = 'u';
      this.value[this.count++] = 'e';
    } else {
      ensureCapacityInternal(this.count + 5);
      this.value[this.count++] = 'f';
      this.value[this.count++] = 'a';
      this.value[this.count++] = 'l';
      this.value[this.count++] = 's';
      this.value[this.count++] = 'e';
    } 
    return this;
  }
  
  public AbstractStringBuilder append(char paramChar) {
    ensureCapacityInternal(this.count + 1);
    this.value[this.count++] = paramChar;
    return this;
  }
  
  public AbstractStringBuilder append(int paramInt) {
    if (paramInt == Integer.MIN_VALUE) {
      append("-2147483648");
      return this;
    } 
    int i = (paramInt < 0) ? (Integer.stringSize(-paramInt) + 1) : Integer.stringSize(paramInt);
    int j = this.count + i;
    ensureCapacityInternal(j);
    Integer.getChars(paramInt, j, this.value);
    this.count = j;
    return this;
  }
  
  public AbstractStringBuilder append(long paramLong) {
    if (paramLong == Float.MIN_VALUE) {
      append("-9223372036854775808");
      return this;
    } 
    int i = (paramLong < 0L) ? (Long.stringSize(-paramLong) + 1) : Long.stringSize(paramLong);
    int j = this.count + i;
    ensureCapacityInternal(j);
    Long.getChars(paramLong, j, this.value);
    this.count = j;
    return this;
  }
  
  public AbstractStringBuilder append(float paramFloat) {
    FloatingDecimal.appendTo(paramFloat, this);
    return this;
  }
  
  public AbstractStringBuilder append(double paramDouble) {
    FloatingDecimal.appendTo(paramDouble, this);
    return this;
  }
  
  public AbstractStringBuilder delete(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > this.count)
      paramInt2 = this.count; 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException(); 
    int i = paramInt2 - paramInt1;
    if (i > 0) {
      System.arraycopy(this.value, paramInt1 + i, this.value, paramInt1, this.count - paramInt2);
      this.count -= i;
    } 
    return this;
  }
  
  public AbstractStringBuilder appendCodePoint(int paramInt) {
    int i = this.count;
    if (Character.isBmpCodePoint(paramInt)) {
      ensureCapacityInternal(i + 1);
      this.value[i] = (char)paramInt;
      this.count = i + 1;
    } else if (Character.isValidCodePoint(paramInt)) {
      ensureCapacityInternal(i + 2);
      Character.toSurrogates(paramInt, this.value, i);
      this.count = i + 2;
    } else {
      throw new IllegalArgumentException();
    } 
    return this;
  }
  
  public AbstractStringBuilder deleteCharAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    System.arraycopy(this.value, paramInt + 1, this.value, paramInt, this.count - paramInt - 1);
    this.count--;
    return this;
  }
  
  public AbstractStringBuilder replace(int paramInt1, int paramInt2, String paramString) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt1 > this.count)
      throw new StringIndexOutOfBoundsException("start > length()"); 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException("start > end"); 
    if (paramInt2 > this.count)
      paramInt2 = this.count; 
    int i = paramString.length();
    int j = this.count + i - paramInt2 - paramInt1;
    ensureCapacityInternal(j);
    System.arraycopy(this.value, paramInt2, this.value, paramInt1 + i, this.count - paramInt2);
    paramString.getChars(this.value, paramInt1);
    this.count = j;
    return this;
  }
  
  public String substring(int paramInt) { return substring(paramInt, this.count); }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) { return substring(paramInt1, paramInt2); }
  
  public String substring(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > this.count)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1); 
    return new String(this.value, paramInt1, paramInt2 - paramInt1);
  }
  
  public AbstractStringBuilder insert(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramInt1 > length())
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 < 0 || paramInt3 < 0 || paramInt2 > paramArrayOfChar.length - paramInt3)
      throw new StringIndexOutOfBoundsException("offset " + paramInt2 + ", len " + paramInt3 + ", str.length " + paramArrayOfChar.length); 
    ensureCapacityInternal(this.count + paramInt3);
    System.arraycopy(this.value, paramInt1, this.value, paramInt1 + paramInt3, this.count - paramInt1);
    System.arraycopy(paramArrayOfChar, paramInt2, this.value, paramInt1, paramInt3);
    this.count += paramInt3;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, Object paramObject) { return insert(paramInt, String.valueOf(paramObject)); }
  
  public AbstractStringBuilder insert(int paramInt, String paramString) {
    if (paramInt < 0 || paramInt > length())
      throw new StringIndexOutOfBoundsException(paramInt); 
    if (paramString == null)
      paramString = "null"; 
    int i = paramString.length();
    ensureCapacityInternal(this.count + i);
    System.arraycopy(this.value, paramInt, this.value, paramInt + i, this.count - paramInt);
    paramString.getChars(this.value, paramInt);
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, char[] paramArrayOfChar) {
    if (paramInt < 0 || paramInt > length())
      throw new StringIndexOutOfBoundsException(paramInt); 
    int i = paramArrayOfChar.length;
    ensureCapacityInternal(this.count + i);
    System.arraycopy(this.value, paramInt, this.value, paramInt + i, this.count - paramInt);
    System.arraycopy(paramArrayOfChar, 0, this.value, paramInt, i);
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, CharSequence paramCharSequence) {
    String str;
    if (paramCharSequence == null)
      str = "null"; 
    return (str instanceof String) ? insert(paramInt, (String)str) : insert(paramInt, str, 0, str.length());
  }
  
  public AbstractStringBuilder insert(int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3) {
    String str;
    if (paramCharSequence == null)
      str = "null"; 
    if (paramInt1 < 0 || paramInt1 > length())
      throw new IndexOutOfBoundsException("dstOffset " + paramInt1); 
    if (paramInt2 < 0 || paramInt3 < 0 || paramInt2 > paramInt3 || paramInt3 > str.length())
      throw new IndexOutOfBoundsException("start " + paramInt2 + ", end " + paramInt3 + ", s.length() " + str.length()); 
    int i = paramInt3 - paramInt2;
    ensureCapacityInternal(this.count + i);
    System.arraycopy(this.value, paramInt1, this.value, paramInt1 + i, this.count - paramInt1);
    for (int j = paramInt2; j < paramInt3; j++)
      this.value[paramInt1++] = str.charAt(j); 
    this.count += i;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, boolean paramBoolean) { return insert(paramInt, String.valueOf(paramBoolean)); }
  
  public AbstractStringBuilder insert(int paramInt, char paramChar) {
    ensureCapacityInternal(this.count + 1);
    System.arraycopy(this.value, paramInt, this.value, paramInt + 1, this.count - paramInt);
    this.value[paramInt] = paramChar;
    this.count++;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt1, int paramInt2) { return insert(paramInt1, String.valueOf(paramInt2)); }
  
  public AbstractStringBuilder insert(int paramInt, long paramLong) { return insert(paramInt, String.valueOf(paramLong)); }
  
  public AbstractStringBuilder insert(int paramInt, float paramFloat) { return insert(paramInt, String.valueOf(paramFloat)); }
  
  public AbstractStringBuilder insert(int paramInt, double paramDouble) { return insert(paramInt, String.valueOf(paramDouble)); }
  
  public int indexOf(String paramString) { return indexOf(paramString, 0); }
  
  public int indexOf(String paramString, int paramInt) { return String.indexOf(this.value, 0, this.count, paramString, paramInt); }
  
  public int lastIndexOf(String paramString) { return lastIndexOf(paramString, this.count); }
  
  public int lastIndexOf(String paramString, int paramInt) { return String.lastIndexOf(this.value, 0, this.count, paramString, paramInt); }
  
  public AbstractStringBuilder reverse() {
    boolean bool = false;
    int i = this.count - 1;
    for (int j = i - 1 >> 1; j >= 0; j--) {
      int k = i - j;
      char c1 = this.value[j];
      char c2 = this.value[k];
      this.value[j] = c2;
      this.value[k] = c1;
      if (Character.isSurrogate(c1) || Character.isSurrogate(c2))
        bool = true; 
    } 
    if (bool)
      reverseAllValidSurrogatePairs(); 
    return this;
  }
  
  private void reverseAllValidSurrogatePairs() {
    for (byte b = 0; b < this.count - 1; b++) {
      char c = this.value[b];
      if (Character.isLowSurrogate(c)) {
        char c1 = this.value[b + true];
        if (Character.isHighSurrogate(c1)) {
          this.value[b++] = c1;
          this.value[b] = c;
        } 
      } 
    } 
  }
  
  public abstract String toString();
  
  final char[] getValue() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\AbstractStringBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */