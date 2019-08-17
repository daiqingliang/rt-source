package java.lang;

class CharacterDataPrivateUse extends CharacterData {
  static final CharacterData instance = new CharacterDataPrivateUse();
  
  int getProperties(int paramInt) { return 0; }
  
  int getType(int paramInt) { return ((paramInt & 0xFFFE) == 65534) ? 0 : 18; }
  
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
  
  byte getDirectionality(int paramInt) { return ((paramInt & 0xFFFE) == 65534) ? -1 : 0; }
  
  boolean isMirrored(int paramInt) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\CharacterDataPrivateUse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */