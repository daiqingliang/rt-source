package com.sun.org.apache.regexp.internal;

public final class CharacterArrayCharacterIterator implements CharacterIterator {
  private final char[] src;
  
  private final int off;
  
  private final int len;
  
  public CharacterArrayCharacterIterator(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    this.src = paramArrayOfChar;
    this.off = paramInt1;
    this.len = paramInt2;
  }
  
  public String substring(int paramInt1, int paramInt2) {
    if (paramInt2 > this.len)
      throw new IndexOutOfBoundsException("endIndex=" + paramInt2 + "; sequence size=" + this.len); 
    if (paramInt1 < 0 || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException("beginIndex=" + paramInt1 + "; endIndex=" + paramInt2); 
    return new String(this.src, this.off + paramInt1, paramInt2 - paramInt1);
  }
  
  public String substring(int paramInt) { return substring(paramInt, this.len); }
  
  public char charAt(int paramInt) { return this.src[this.off + paramInt]; }
  
  public boolean isEnd(int paramInt) { return (paramInt >= this.len); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\CharacterArrayCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */