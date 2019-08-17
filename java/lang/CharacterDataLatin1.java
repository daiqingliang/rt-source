package java.lang;

class CharacterDataLatin1 extends CharacterData {
  static char[] sharpsMap = { 'S', 'S' };
  
  static final CharacterDataLatin1 instance = new CharacterDataLatin1();
  
  static final int[] A = new int[256];
  
  static final String A_DATA = "䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ堀䀏倀䀏堀䀏怀䀏倀䀏䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ倀䀏倀䀏倀䀏堀䀏怀䀌栀\030栀\030⠀\030⠀怚⠀\030栀\030栀\030\025\026栀\030 \031㠀\030 \024㠀\030㠀\030᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉㠀\030栀\030\031栀\031\031栀\030栀\030翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡\025栀\030\026栀\033栀倗栀\033翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢\025栀\031\026栀\031䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ倀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ㠀\f栀\030⠀怚⠀怚⠀怚⠀怚栀\034栀\030栀\033栀\034\000瀅\035栀\031䠀တ栀\034栀\033⠀\034⠀\031᠀؋᠀؋栀\033߽瀂栀\030栀\030栀\033᠀ԋ\000瀅\036栀ࠋ栀ࠋ栀ࠋ栀\030瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁栀\031瀁瀁瀁瀁瀁瀁瀁߽瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂栀\031瀂瀂瀂瀂瀂瀂瀂؝瀂";
  
  static final char[] B = "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\001\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\001\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000".toCharArray();
  
  int getProperties(int paramInt) {
    char c = (char)paramInt;
    return A[c];
  }
  
  int getPropertiesEx(int paramInt) {
    char c = (char)paramInt;
    return B[c];
  }
  
  boolean isOtherLowercase(int paramInt) {
    int i = getPropertiesEx(paramInt);
    return ((i & true) != 0);
  }
  
  boolean isOtherUppercase(int paramInt) {
    int i = getPropertiesEx(paramInt);
    return ((i & 0x2) != 0);
  }
  
  boolean isOtherAlphabetic(int paramInt) {
    int i = getPropertiesEx(paramInt);
    return ((i & 0x4) != 0);
  }
  
  boolean isIdeographic(int paramInt) {
    int i = getPropertiesEx(paramInt);
    return ((i & 0x10) != 0);
  }
  
  int getType(int paramInt) {
    int i = getProperties(paramInt);
    return i & 0x1F;
  }
  
  boolean isJavaIdentifierStart(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x7000) >= 20480);
  }
  
  boolean isJavaIdentifierPart(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x3000) != 0);
  }
  
  boolean isUnicodeIdentifierStart(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x7000) == 28672);
  }
  
  boolean isUnicodeIdentifierPart(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x1000) != 0);
  }
  
  boolean isIdentifierIgnorable(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x7000) == 4096);
  }
  
  int toLowerCase(int paramInt) {
    int i = paramInt;
    int j = getProperties(paramInt);
    if ((j & 0x20000) != 0 && (j & 0x7FC0000) != 133955584) {
      int k = j << 5 >> 23;
      i = paramInt + k;
    } 
    return i;
  }
  
  int toUpperCase(int paramInt) {
    int i = paramInt;
    int j = getProperties(paramInt);
    if ((j & 0x10000) != 0)
      if ((j & 0x7FC0000) != 133955584) {
        int k = j << 5 >> 23;
        i = paramInt - k;
      } else if (paramInt == 181) {
        i = 924;
      }  
    return i;
  }
  
  int toTitleCase(int paramInt) { return toUpperCase(paramInt); }
  
  int digit(int paramInt1, int paramInt2) {
    int i = -1;
    if (paramInt2 >= 2 && paramInt2 <= 36) {
      int j = getProperties(paramInt1);
      int k = j & 0x1F;
      if (k == 9) {
        i = paramInt1 + ((j & 0x3E0) >> 5) & 0x1F;
      } else if ((j & 0xC00) == 3072) {
        i = (paramInt1 + ((j & 0x3E0) >> 5) & 0x1F) + 10;
      } 
    } 
    return (i < paramInt2) ? i : -1;
  }
  
  int getNumericValue(int paramInt) {
    int i = getProperties(paramInt);
    null = -1;
    switch (i & 0xC00) {
      default:
        return -1;
      case 1024:
        return paramInt + ((i & 0x3E0) >> 5) & 0x1F;
      case 2048:
        return -2;
      case 3072:
        break;
    } 
    return (paramInt + ((i & 0x3E0) >> 5) & 0x1F) + 10;
  }
  
  boolean isWhitespace(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x7000) == 16384);
  }
  
  byte getDirectionality(int paramInt) {
    int i = getProperties(paramInt);
    byte b = (byte)((i & 0x78000000) >> 27);
    if (b == 15)
      b = -1; 
    return b;
  }
  
  boolean isMirrored(int paramInt) {
    int i = getProperties(paramInt);
    return ((i & 0x80000000) != 0);
  }
  
  int toUpperCaseEx(int paramInt) {
    int i = paramInt;
    int j = getProperties(paramInt);
    if ((j & 0x10000) != 0)
      if ((j & 0x7FC0000) != 133955584) {
        int k = j << 5 >> 23;
        i = paramInt - k;
      } else {
        switch (paramInt) {
          case 181:
            return 924;
        } 
        i = -1;
      }  
    return i;
  }
  
  char[] toUpperCaseCharArray(int paramInt) {
    char[] arrayOfChar = { (char)paramInt };
    if (paramInt == 223)
      arrayOfChar = sharpsMap; 
    return arrayOfChar;
  }
  
  static  {
    char[] arrayOfChar = "䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ堀䀏倀䀏堀䀏怀䀏倀䀏䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ倀䀏倀䀏倀䀏堀䀏怀䀌栀\030栀\030⠀\030⠀怚⠀\030栀\030栀\030\025\026栀\030 \031㠀\030 \024㠀\030㠀\030᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉᠀㘉㠀\030栀\030\031栀\031\031栀\030栀\030翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡翡\025栀\030\026栀\033栀倗栀\033翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢翢\025栀\031\026栀\031䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ倀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ䠀ဏ㠀\f栀\030⠀怚⠀怚⠀怚⠀怚栀\034栀\030栀\033栀\034\000瀅\035栀\031䠀တ栀\034栀\033⠀\034⠀\031᠀؋᠀؋栀\033߽瀂栀\030栀\030栀\033᠀ԋ\000瀅\036栀ࠋ栀ࠋ栀ࠋ栀\030瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁瀁栀\031瀁瀁瀁瀁瀁瀁瀁߽瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂瀂栀\031瀂瀂瀂瀂瀂瀂瀂؝瀂".toCharArray();
    assert arrayOfChar.length == 512;
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < 'Ȁ') {
      char c = arrayOfChar[b1++] << '\020';
      A[b2++] = c | arrayOfChar[b1++];
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\CharacterDataLatin1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */