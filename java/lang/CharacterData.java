package java.lang;

abstract class CharacterData {
  abstract int getProperties(int paramInt);
  
  abstract int getType(int paramInt);
  
  abstract boolean isWhitespace(int paramInt);
  
  abstract boolean isMirrored(int paramInt);
  
  abstract boolean isJavaIdentifierStart(int paramInt);
  
  abstract boolean isJavaIdentifierPart(int paramInt);
  
  abstract boolean isUnicodeIdentifierStart(int paramInt);
  
  abstract boolean isUnicodeIdentifierPart(int paramInt);
  
  abstract boolean isIdentifierIgnorable(int paramInt);
  
  abstract int toLowerCase(int paramInt);
  
  abstract int toUpperCase(int paramInt);
  
  abstract int toTitleCase(int paramInt);
  
  abstract int digit(int paramInt1, int paramInt2);
  
  abstract int getNumericValue(int paramInt);
  
  abstract byte getDirectionality(int paramInt);
  
  int toUpperCaseEx(int paramInt) { return toUpperCase(paramInt); }
  
  char[] toUpperCaseCharArray(int paramInt) { return null; }
  
  boolean isOtherLowercase(int paramInt) { return false; }
  
  boolean isOtherUppercase(int paramInt) { return false; }
  
  boolean isOtherAlphabetic(int paramInt) { return false; }
  
  boolean isIdeographic(int paramInt) { return false; }
  
  static final CharacterData of(int paramInt) {
    if (paramInt >>> 8 == 0)
      return CharacterDataLatin1.instance; 
    switch (paramInt >>> 16) {
      case 0:
        return CharacterData00.instance;
      case 1:
        return CharacterData01.instance;
      case 2:
        return CharacterData02.instance;
      case 14:
        return CharacterData0E.instance;
      case 15:
      case 16:
        return CharacterDataPrivateUse.instance;
    } 
    return CharacterDataUndefined.instance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\CharacterData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */