package com.sun.org.apache.regexp.internal;

public final class StringCharacterIterator implements CharacterIterator {
  private final String src;
  
  public StringCharacterIterator(String paramString) { this.src = paramString; }
  
  public String substring(int paramInt1, int paramInt2) { return this.src.substring(paramInt1, paramInt2); }
  
  public String substring(int paramInt) { return this.src.substring(paramInt); }
  
  public char charAt(int paramInt) { return this.src.charAt(paramInt); }
  
  public boolean isEnd(int paramInt) { return (paramInt >= this.src.length()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\StringCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */