package sun.net.idn;

import java.text.ParseException;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UTF16;

public final class Punycode {
  private static final int BASE = 36;
  
  private static final int TMIN = 1;
  
  private static final int TMAX = 26;
  
  private static final int SKEW = 38;
  
  private static final int DAMP = 700;
  
  private static final int INITIAL_BIAS = 72;
  
  private static final int INITIAL_N = 128;
  
  private static final int HYPHEN = 45;
  
  private static final int DELIMITER = 45;
  
  private static final int ZERO = 48;
  
  private static final int NINE = 57;
  
  private static final int SMALL_A = 97;
  
  private static final int SMALL_Z = 122;
  
  private static final int CAPITAL_A = 65;
  
  private static final int CAPITAL_Z = 90;
  
  private static final int MAX_CP_COUNT = 256;
  
  private static final int UINT_MAGIC = -2147483648;
  
  private static final long ULONG_MAGIC = -9223372036854775808L;
  
  static final int[] basicToDigit = { 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 
      28, 29, 30, 31, 32, 33, 34, 35, -1, -1, 
      -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
      15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
      25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 
      3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 
      13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 
      23, 24, 25, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1 };
  
  private static int adaptBias(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramBoolean) {
      paramInt1 /= 700;
    } else {
      paramInt1 /= 2;
    } 
    paramInt1 += paramInt1 / paramInt2;
    int i;
    for (i = 0; paramInt1 > 455; i += true)
      paramInt1 /= 35; 
    return i + 36 * paramInt1 / (paramInt1 + 38);
  }
  
  private static char asciiCaseMap(char paramChar, boolean paramBoolean) {
    if (paramBoolean) {
      if ('a' <= paramChar && paramChar <= 'z')
        paramChar = (char)(paramChar - ' '); 
    } else if ('A' <= paramChar && paramChar <= 'Z') {
      paramChar = (char)(paramChar + ' ');
    } 
    return paramChar;
  }
  
  private static char digitToBasic(int paramInt, boolean paramBoolean) { return (paramInt < 26) ? (paramBoolean ? (char)(65 + paramInt) : (char)(97 + paramInt)) : (char)(22 + paramInt); }
  
  public static StringBuffer encode(StringBuffer paramStringBuffer, boolean[] paramArrayOfBoolean) throws ParseException {
    int[] arrayOfInt = new int[256];
    int m = paramStringBuffer.length();
    char c = 'Ā';
    char[] arrayOfChar = new char[c];
    StringBuffer stringBuffer = new StringBuffer();
    byte b3 = 0;
    byte b5 = b3;
    byte b4;
    for (b4 = 0; b4 < m; b4++) {
      if (b5 == 'Ā')
        throw new IndexOutOfBoundsException(); 
      char c1 = paramStringBuffer.charAt(b4);
      if (isBasic(c1)) {
        if (b3 < c) {
          arrayOfInt[b5++] = 0;
          arrayOfChar[b3] = (paramArrayOfBoolean != null) ? asciiCaseMap(c1, paramArrayOfBoolean[b4]) : c1;
        } 
        b3++;
      } else {
        int n = ((paramArrayOfBoolean != null && paramArrayOfBoolean[b4]) ? 1 : 0) << 31;
        if (!UTF16.isSurrogate(c1)) {
          n |= c1;
        } else {
          char c2;
          if (UTF16.isLeadSurrogate(c1) && b4 + 1 < m && UTF16.isTrailSurrogate(c2 = paramStringBuffer.charAt(b4 + 1))) {
            b4++;
            n |= UCharacter.getCodePoint(c1, c2);
          } else {
            throw new ParseException("Illegal char found", -1);
          } 
        } 
        arrayOfInt[b5++] = n;
      } 
    } 
    byte b2 = b3;
    if (b2 > 0) {
      if (b3 < c)
        arrayOfChar[b3] = '-'; 
      b3++;
    } 
    int i = 128;
    int j = 0;
    int k = 72;
    byte b1 = b2;
    while (b1 < b5) {
      int n = Integer.MAX_VALUE;
      for (b4 = 0; b4 < b5; b4++) {
        int i1 = arrayOfInt[b4] & 0x7FFFFFFF;
        if (i <= i1 && i1 < n)
          n = i1; 
      } 
      if (n - i > (2147483391 - j) / (b1 + 1))
        throw new RuntimeException("Internal program error"); 
      j += (n - i) * (b1 + 1);
      i = n;
      for (b4 = 0; b4 < b5; b4++) {
        int i1 = arrayOfInt[b4] & 0x7FFFFFFF;
        if (i1 < i) {
          j++;
        } else if (i1 == i) {
          i1 = j;
          for (byte b = 36;; b += 36) {
            int i2 = b - k;
            if (i2 < 1) {
              i2 = 1;
            } else if (b >= k + 26) {
              i2 = 26;
            } 
            if (i1 < i2)
              break; 
            if (b3 < c)
              arrayOfChar[b3++] = digitToBasic(i2 + (i1 - i2) % (36 - i2), false); 
            i1 = (i1 - i2) / (36 - i2);
          } 
          if (b3 < c)
            arrayOfChar[b3++] = digitToBasic(i1, (arrayOfInt[b4] < 0)); 
          k = adaptBias(j, b1 + 1, (b1 == b2));
          j = 0;
          b1++;
        } 
      } 
      j++;
      i++;
    } 
    return stringBuffer.append(arrayOfChar, 0, b3);
  }
  
  private static boolean isBasic(int paramInt) { return (paramInt < 128); }
  
  private static boolean isBasicUpperCase(int paramInt) { return (65 <= paramInt && paramInt <= 90); }
  
  private static boolean isSurrogate(int paramInt) { return ((paramInt & 0xFFFFF800) == 55296); }
  
  public static StringBuffer decode(StringBuffer paramStringBuffer, boolean[] paramArrayOfBoolean) throws ParseException {
    int i = paramStringBuffer.length();
    StringBuffer stringBuffer = new StringBuffer();
    char c = 'Ā';
    char[] arrayOfChar = new char[c];
    int i2 = i;
    do {
    
    } while (i2 > 0 && paramStringBuffer.charAt(--i2) != '-');
    int i4 = i2;
    int i1 = i4;
    int k = i1;
    while (i2 > 0) {
      char c1 = paramStringBuffer.charAt(--i2);
      if (!isBasic(c1))
        throw new ParseException("Illegal char found", -1); 
      if (i2 < c) {
        arrayOfChar[i2] = c1;
        if (paramArrayOfBoolean != null)
          paramArrayOfBoolean[i2] = isBasicUpperCase(c1); 
      } 
    } 
    int j = 128;
    int m = 0;
    int n = 72;
    int i5 = 1000000000;
    int i3 = (i1 > 0) ? (i1 + 1) : 0;
    while (i3 < i) {
      int i6 = m;
      int i7 = 1;
      for (byte b = 36;; b += 36) {
        if (i3 >= i)
          throw new ParseException("Illegal char found", -1); 
        int i9 = basicToDigit[(byte)paramStringBuffer.charAt(i3++)];
        if (i9 < 0)
          throw new ParseException("Invalid char found", -1); 
        if (i9 > (Integer.MAX_VALUE - m) / i7)
          throw new ParseException("Illegal char found", -1); 
        m += i9 * i7;
        byte b1 = b - n;
        if (b1 < 1) {
          b1 = 1;
        } else if (b >= n + 26) {
          b1 = 26;
        } 
        if (i9 < b1)
          break; 
        if (i7 > Integer.MAX_VALUE / (36 - b1))
          throw new ParseException("Illegal char found", -1); 
        i7 *= (36 - b1);
      } 
      n = adaptBias(m - i6, ++i4, (i6 == 0));
      if (m / i4 > Integer.MAX_VALUE - j)
        throw new ParseException("Illegal char found", -1); 
      j += m / i4;
      m %= i4;
      if (j > 1114111 || isSurrogate(j))
        throw new ParseException("Illegal char found", -1); 
      int i8 = UTF16.getCharCount(j);
      if (k + i8 < c) {
        int i9;
        if (m <= i5) {
          i9 = m;
          if (i8 > 1) {
            i5 = i9;
          } else {
            i5++;
          } 
        } else {
          i9 = i5;
          i9 = UTF16.moveCodePointOffset(arrayOfChar, 0, k, i9, m - i9);
        } 
        if (i9 < k) {
          System.arraycopy(arrayOfChar, i9, arrayOfChar, i9 + i8, k - i9);
          if (paramArrayOfBoolean != null)
            System.arraycopy(paramArrayOfBoolean, i9, paramArrayOfBoolean, i9 + i8, k - i9); 
        } 
        if (i8 == 1) {
          arrayOfChar[i9] = (char)j;
        } else {
          arrayOfChar[i9] = UTF16.getLeadSurrogate(j);
          arrayOfChar[i9 + 1] = UTF16.getTrailSurrogate(j);
        } 
        if (paramArrayOfBoolean != null) {
          paramArrayOfBoolean[i9] = isBasicUpperCase(paramStringBuffer.charAt(i3 - 1));
          if (i8 == 2)
            paramArrayOfBoolean[i9 + 1] = false; 
        } 
      } 
      k += i8;
      m++;
    } 
    stringBuffer.append(arrayOfChar, 0, k);
    return stringBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\idn\Punycode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */