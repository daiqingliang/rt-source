package com.sun.jndi.ldap;

import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;

final class Filter {
  private static final boolean dbg = false;
  
  private static int dbgIndent = 0;
  
  static final int LDAP_FILTER_AND = 160;
  
  static final int LDAP_FILTER_OR = 161;
  
  static final int LDAP_FILTER_NOT = 162;
  
  static final int LDAP_FILTER_EQUALITY = 163;
  
  static final int LDAP_FILTER_SUBSTRINGS = 164;
  
  static final int LDAP_FILTER_GE = 165;
  
  static final int LDAP_FILTER_LE = 166;
  
  static final int LDAP_FILTER_PRESENT = 135;
  
  static final int LDAP_FILTER_APPROX = 168;
  
  static final int LDAP_FILTER_EXT = 169;
  
  static final int LDAP_FILTER_EXT_RULE = 129;
  
  static final int LDAP_FILTER_EXT_TYPE = 130;
  
  static final int LDAP_FILTER_EXT_VAL = 131;
  
  static final int LDAP_FILTER_EXT_DN = 132;
  
  static final int LDAP_SUBSTRING_INITIAL = 128;
  
  static final int LDAP_SUBSTRING_ANY = 129;
  
  static final int LDAP_SUBSTRING_FINAL = 130;
  
  static void encodeFilterString(BerEncoder paramBerEncoder, String paramString, boolean paramBoolean) throws IOException, NamingException {
    byte[] arrayOfByte;
    if (paramString == null || paramString.equals(""))
      throw new InvalidSearchFilterException("Empty filter"); 
    if (paramBoolean) {
      arrayOfByte = paramString.getBytes("UTF8");
    } else {
      arrayOfByte = paramString.getBytes("8859_1");
    } 
    int i = arrayOfByte.length;
    encodeFilter(paramBerEncoder, arrayOfByte, 0, i);
  }
  
  private static void encodeFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException, NamingException {
    if (paramInt2 - paramInt1 <= 0)
      throw new InvalidSearchFilterException("Empty filter"); 
    byte b = 0;
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramInt1;
    while (arrayOfInt[0] < paramInt2) {
      boolean bool;
      byte b1;
      int i;
      switch (paramArrayOfByte[arrayOfInt[0]]) {
        case 40:
          arrayOfInt[0] = arrayOfInt[0] + 1;
          b++;
          switch (paramArrayOfByte[arrayOfInt[0]]) {
            case 38:
              encodeComplexFilter(paramBerEncoder, paramArrayOfByte, 160, arrayOfInt, paramInt2);
              b--;
              break;
            case 124:
              encodeComplexFilter(paramBerEncoder, paramArrayOfByte, 161, arrayOfInt, paramInt2);
              b--;
              break;
            case 33:
              encodeComplexFilter(paramBerEncoder, paramArrayOfByte, 162, arrayOfInt, paramInt2);
              b--;
              break;
          } 
          b1 = 1;
          bool = false;
          i = arrayOfInt[0];
          while (i < paramInt2 && b1) {
            if (!bool)
              if (paramArrayOfByte[i] == 40) {
                b1++;
              } else if (paramArrayOfByte[i] == 41) {
                b1--;
              }  
            if (paramArrayOfByte[i] == 92 && !bool) {
              bool = true;
            } else {
              bool = false;
            } 
            if (b1 > 0)
              i++; 
          } 
          if (b1 != 0)
            throw new InvalidSearchFilterException("Unbalanced parenthesis"); 
          encodeSimpleFilter(paramBerEncoder, paramArrayOfByte, arrayOfInt[0], i);
          arrayOfInt[0] = i + 1;
          b--;
          break;
        case 41:
          paramBerEncoder.endSeq();
          arrayOfInt[0] = arrayOfInt[0] + 1;
          b--;
          break;
        case 32:
          arrayOfInt[0] = arrayOfInt[0] + 1;
          break;
        default:
          encodeSimpleFilter(paramBerEncoder, paramArrayOfByte, arrayOfInt[0], paramInt2);
          arrayOfInt[0] = paramInt2;
          break;
      } 
      if (b < 0)
        throw new InvalidSearchFilterException("Unbalanced parenthesis"); 
    } 
    if (b != 0)
      throw new InvalidSearchFilterException("Unbalanced parenthesis"); 
  }
  
  private static int hexchar2int(byte paramByte) { return (paramByte >= 48 && paramByte <= 57) ? (paramByte - 48) : ((paramByte >= 65 && paramByte <= 70) ? (paramByte - 65 + 10) : ((paramByte >= 97 && paramByte <= 102) ? (paramByte - 97 + 10) : -1)); }
  
  static byte[] unescapeFilterValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws NamingException {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = paramInt2 - paramInt1;
    byte[] arrayOfByte1 = new byte[i];
    byte b = 0;
    for (int j = paramInt1; j < paramInt2; j++) {
      byte b1 = paramArrayOfByte[j];
      if (bool1) {
        int k;
        if ((k = hexchar2int(b1)) < 0) {
          if (bool2) {
            bool1 = false;
            arrayOfByte1[b++] = b1;
          } else {
            throw new InvalidSearchFilterException("invalid escape sequence: " + paramArrayOfByte);
          } 
        } else if (bool2) {
          arrayOfByte1[b] = (byte)(k << 4);
          bool2 = false;
        } else {
          arrayOfByte1[b++] = (byte)(arrayOfByte1[b++] | (byte)k);
          bool1 = false;
        } 
      } else if (b1 != 92) {
        arrayOfByte1[b++] = b1;
        bool1 = false;
      } else {
        bool2 = bool1 = true;
      } 
    } 
    byte[] arrayOfByte2 = new byte[b];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, b);
    return arrayOfByte2;
  }
  
  private static int indexOf(byte[] paramArrayOfByte, char paramChar, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfByte[i] == paramChar)
        return i; 
    } 
    return -1;
  }
  
  private static int indexOf(byte[] paramArrayOfByte, String paramString, int paramInt1, int paramInt2) {
    int i = indexOf(paramArrayOfByte, paramString.charAt(0), paramInt1, paramInt2);
    if (i >= 0)
      for (int j = 1; j < paramString.length(); j++) {
        if (paramArrayOfByte[i + j] != paramString.charAt(j))
          return -1; 
      }  
    return i;
  }
  
  private static int findUnescaped(byte[] paramArrayOfByte, char paramChar, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      int i = indexOf(paramArrayOfByte, paramChar, paramInt1, paramInt2);
      byte b = 0;
      int j = i - 1;
      while (j >= paramInt1 && paramArrayOfByte[j] == 92) {
        j--;
        b++;
      } 
      if (i == paramInt1 || i == -1 || b % 2 == 0)
        return i; 
      paramInt1 = i + 1;
    } 
    return -1;
  }
  
  private static void encodeSimpleFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException, NamingException {
    char c;
    int m;
    int n;
    if ((n = indexOf(paramArrayOfByte, '=', paramInt1, paramInt2)) == -1)
      throw new InvalidSearchFilterException("Missing 'equals'"); 
    int i = n + 1;
    int j = paramInt2;
    int k = paramInt1;
    switch (paramArrayOfByte[n - 1]) {
      case 60:
        c = '¦';
        m = n - 1;
        break;
      case 62:
        c = '¥';
        m = n - 1;
        break;
      case 126:
        c = '¨';
        m = n - 1;
        break;
      case 58:
        c = '©';
        m = n - 1;
        break;
      default:
        m = n;
        c = Character.MIN_VALUE;
        break;
    } 
    int i1 = -1;
    int i2 = -1;
    if ((paramArrayOfByte[k] >= 48 && paramArrayOfByte[k] <= 57) || (paramArrayOfByte[k] >= 65 && paramArrayOfByte[k] <= 90) || (paramArrayOfByte[k] >= 97 && paramArrayOfByte[k] <= 122)) {
      boolean bool = (paramArrayOfByte[k] >= 48 && paramArrayOfByte[k] <= 57) ? 1 : 0;
      for (int i3 = k + 1; i3 < m; i3++) {
        if (paramArrayOfByte[i3] == 59) {
          if (bool && paramArrayOfByte[i3 - 1] == 46)
            throw new InvalidSearchFilterException("invalid attribute description"); 
          i1 = i3;
          break;
        } 
        if (paramArrayOfByte[i3] == 58 && c == '©') {
          if (bool && paramArrayOfByte[i3 - 1] == 46)
            throw new InvalidSearchFilterException("invalid attribute description"); 
          i2 = i3;
          break;
        } 
        if (bool) {
          if ((paramArrayOfByte[i3] == 46 && paramArrayOfByte[i3 - 1] == 46) || (paramArrayOfByte[i3] != 46 && (paramArrayOfByte[i3] < 48 || paramArrayOfByte[i3] > 57)))
            throw new InvalidSearchFilterException("invalid attribute description"); 
        } else if (paramArrayOfByte[i3] != 45 && paramArrayOfByte[i3] != 95 && (paramArrayOfByte[i3] < 48 || paramArrayOfByte[i3] > 57) && (paramArrayOfByte[i3] < 65 || paramArrayOfByte[i3] > 90) && (paramArrayOfByte[i3] < 97 || paramArrayOfByte[i3] > 122)) {
          throw new InvalidSearchFilterException("invalid attribute description");
        } 
      } 
    } else if (c == '©' && paramArrayOfByte[k] == 58) {
      i2 = k;
    } else {
      throw new InvalidSearchFilterException("invalid attribute description");
    } 
    if (i1 > 0)
      for (int i3 = i1 + 1; i3 < m; i3++) {
        if (paramArrayOfByte[i3] == 59) {
          if (paramArrayOfByte[i3 - 1] == 59)
            throw new InvalidSearchFilterException("invalid attribute description"); 
        } else {
          if (paramArrayOfByte[i3] == 58 && c == '©') {
            if (paramArrayOfByte[i3 - 1] == 59)
              throw new InvalidSearchFilterException("invalid attribute description"); 
            i2 = i3;
            break;
          } 
          if (paramArrayOfByte[i3] != 45 && paramArrayOfByte[i3] != 95 && (paramArrayOfByte[i3] < 48 || paramArrayOfByte[i3] > 57) && (paramArrayOfByte[i3] < 65 || paramArrayOfByte[i3] > 90) && (paramArrayOfByte[i3] < 97 || paramArrayOfByte[i3] > 122))
            throw new InvalidSearchFilterException("invalid attribute description"); 
        } 
      }  
    if (i2 > 0) {
      boolean bool = false;
      for (int i3 = i2 + 1; i3 < m; i3++) {
        if (paramArrayOfByte[i3] == 58)
          throw new InvalidSearchFilterException("invalid attribute description"); 
        if ((paramArrayOfByte[i3] >= 48 && paramArrayOfByte[i3] <= 57) || (paramArrayOfByte[i3] >= 65 && paramArrayOfByte[i3] <= 90) || (paramArrayOfByte[i3] >= 97 && paramArrayOfByte[i3] <= 122)) {
          boolean bool1 = (paramArrayOfByte[i3] >= 48 && paramArrayOfByte[i3] <= 57) ? 1 : 0;
          int i4 = ++i3;
          while (i4 < m) {
            if (paramArrayOfByte[i4] == 58) {
              if (bool)
                throw new InvalidSearchFilterException("invalid attribute description"); 
              if (bool1 && paramArrayOfByte[i4 - 1] == 46)
                throw new InvalidSearchFilterException("invalid attribute description"); 
              bool = true;
              break;
            } 
            if (bool1) {
              if ((paramArrayOfByte[i4] == 46 && paramArrayOfByte[i4 - 1] == 46) || (paramArrayOfByte[i4] != 46 && (paramArrayOfByte[i4] < 48 || paramArrayOfByte[i4] > 57)))
                throw new InvalidSearchFilterException("invalid attribute description"); 
            } else if (paramArrayOfByte[i4] != 45 && paramArrayOfByte[i4] != 95 && (paramArrayOfByte[i4] < 48 || paramArrayOfByte[i4] > 57) && (paramArrayOfByte[i4] < 65 || paramArrayOfByte[i4] > 90) && (paramArrayOfByte[i4] < 97 || paramArrayOfByte[i4] > 122)) {
              throw new InvalidSearchFilterException("invalid attribute description");
            } 
            i4++;
            i3++;
          } 
        } else {
          throw new InvalidSearchFilterException("invalid attribute description");
        } 
      } 
    } 
    if (paramArrayOfByte[m - 1] == 46 || paramArrayOfByte[m - 1] == 59 || paramArrayOfByte[m - 1] == 58)
      throw new InvalidSearchFilterException("invalid attribute description"); 
    if (m == n)
      if (findUnescaped(paramArrayOfByte, '*', i, j) == -1) {
        c = '£';
      } else if (paramArrayOfByte[i] == 42 && i == j - 1) {
        c = '';
      } else {
        encodeSubstringFilter(paramBerEncoder, paramArrayOfByte, k, m, i, j);
        return;
      }  
    if (c == '') {
      paramBerEncoder.encodeOctetString(paramArrayOfByte, c, k, m - k);
    } else if (c == '©') {
      encodeExtensibleMatch(paramBerEncoder, paramArrayOfByte, k, m, i, j);
    } else {
      paramBerEncoder.beginSeq(c);
      paramBerEncoder.encodeOctetString(paramArrayOfByte, 4, k, m - k);
      paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, i, j), 4);
      paramBerEncoder.endSeq();
    } 
  }
  
  private static void encodeSubstringFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IOException, NamingException {
    paramBerEncoder.beginSeq(164);
    paramBerEncoder.encodeOctetString(paramArrayOfByte, 4, paramInt1, paramInt2 - paramInt1);
    paramBerEncoder.beginSeq(48);
    int i;
    int j;
    for (j = paramInt3; (i = findUnescaped(paramArrayOfByte, '*', j, paramInt4)) != -1; j = i + 1) {
      if (j == paramInt3) {
        if (j < i)
          paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, j, i), 128); 
      } else if (j < i) {
        paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, j, i), 129);
      } 
    } 
    if (j < paramInt4)
      paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, j, paramInt4), 130); 
    paramBerEncoder.endSeq();
    paramBerEncoder.endSeq();
  }
  
  private static void encodeComplexFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt, int paramInt2) throws IOException, NamingException {
    paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
    paramBerEncoder.beginSeq(paramInt1);
    int[] arrayOfInt = findRightParen(paramArrayOfByte, paramArrayOfInt, paramInt2);
    encodeFilterList(paramBerEncoder, paramArrayOfByte, paramInt1, arrayOfInt[0], arrayOfInt[1]);
    paramBerEncoder.endSeq();
  }
  
  private static int[] findRightParen(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt) throws IOException, NamingException {
    byte b = 1;
    boolean bool = false;
    int i = paramArrayOfInt[0];
    while (i < paramInt && b) {
      if (!bool)
        if (paramArrayOfByte[i] == 40) {
          b++;
        } else if (paramArrayOfByte[i] == 41) {
          b--;
        }  
      if (paramArrayOfByte[i] == 92 && !bool) {
        bool = true;
      } else {
        bool = false;
      } 
      if (b > 0)
        i++; 
    } 
    if (b != 0)
      throw new InvalidSearchFilterException("Unbalanced parenthesis"); 
    int[] arrayOfInt = { paramArrayOfInt[0], i };
    paramArrayOfInt[0] = i + 1;
    return arrayOfInt;
  }
  
  private static void encodeFilterList(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) throws IOException, NamingException {
    int[] arrayOfInt = new int[1];
    byte b = 0;
    arrayOfInt[0] = paramInt2;
    while (arrayOfInt[0] < paramInt3) {
      if (!Character.isSpaceChar((char)paramArrayOfByte[arrayOfInt[0]])) {
        if (paramInt1 == 162 && b)
          throw new InvalidSearchFilterException("Filter (!) cannot be followed by more than one filters"); 
        if (paramArrayOfByte[arrayOfInt[0]] != 40) {
          int[] arrayOfInt1 = findRightParen(paramArrayOfByte, arrayOfInt, paramInt3);
          int i = arrayOfInt1[1] - arrayOfInt1[0];
          byte[] arrayOfByte = new byte[i + 2];
          System.arraycopy(paramArrayOfByte, arrayOfInt1[0], arrayOfByte, 1, i);
          arrayOfByte[0] = 40;
          arrayOfByte[i + 1] = 41;
          encodeFilter(paramBerEncoder, arrayOfByte, 0, arrayOfByte.length);
          b++;
        } 
      } 
      arrayOfInt[0] = arrayOfInt[0] + 1;
    } 
  }
  
  private static void encodeExtensibleMatch(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IOException, NamingException {
    boolean bool = false;
    paramBerEncoder.beginSeq(169);
    int i;
    if ((i = indexOf(paramArrayOfByte, ':', paramInt1, paramInt2)) >= 0) {
      int k;
      if ((k = indexOf(paramArrayOfByte, ":dn", i, paramInt2)) >= 0)
        bool = true; 
      int j;
      if ((j = indexOf(paramArrayOfByte, ':', i + 1, paramInt2)) >= 0 || k == -1)
        if (k == i) {
          paramBerEncoder.encodeOctetString(paramArrayOfByte, 129, j + 1, paramInt2 - j + 1);
        } else if (k == j && k >= 0) {
          paramBerEncoder.encodeOctetString(paramArrayOfByte, 129, i + 1, j - i + 1);
        } else {
          paramBerEncoder.encodeOctetString(paramArrayOfByte, 129, i + 1, paramInt2 - i + 1);
        }  
      if (i > paramInt1)
        paramBerEncoder.encodeOctetString(paramArrayOfByte, 130, paramInt1, i - paramInt1); 
    } else {
      paramBerEncoder.encodeOctetString(paramArrayOfByte, 130, paramInt1, paramInt2 - paramInt1);
    } 
    paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, paramInt3, paramInt4), 131);
    paramBerEncoder.encodeBoolean(bool, 132);
    paramBerEncoder.endSeq();
  }
  
  private static void dprint(String paramString) { dprint(paramString, new byte[0], 0, 0); }
  
  private static void dprint(String paramString, byte[] paramArrayOfByte) { dprint(paramString, paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  private static void dprint(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    String str = "  ";
    int i = dbgIndent;
    while (i-- > 0)
      str = str + "  "; 
    str = str + paramString;
    System.err.print(str);
    for (int j = paramInt1; j < paramInt2; j++)
      System.err.print((char)paramArrayOfByte[j]); 
    System.err.println();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */