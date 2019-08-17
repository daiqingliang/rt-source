package java.lang;

class CharacterDataUndefined extends CharacterData {
  static final CharacterData instance = new CharacterDataUndefined();
  
  int getProperties(int paramInt) { return 0; }
  
  int getType(int paramInt) { return 0; }
  
  boolean isJavaIdentifierStart(int paramInt) { return false; }
  
  boolean isJavaIdentifierPart(int paramInt) { return false; }
  
  boolean isUnicodeIdentifierStart(int paramInt) { return false; }
  
  boolean isUnicodeIdentifierPart(int paramInt) { return false; }
  
  boolean isIdentifierIgnorable(int paramInt) { return false; }
  
  int toLowerCase(int paramInt) { return paramInt; }
  
  int toUpperCase(int paramInt) { return paramInt; }
  
  int toTitleCase(int paramInt) { return paramInt; }
  
  int digit(int paramInt1, int paramInt2) { return -1; }
  
  int getNumericValue(int paramInt) { return -1; }
  
  boolean isWhitespace(int paramInt) { return false; }
  
  byte getDirectionality(int paramInt) { return -1; }
  
  boolean isMirrored(int paramInt) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\CharacterDataUndefined.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */