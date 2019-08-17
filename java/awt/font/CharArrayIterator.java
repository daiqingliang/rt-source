package java.awt.font;

import java.text.CharacterIterator;

class CharArrayIterator implements CharacterIterator {
  private char[] chars;
  
  private int pos;
  
  private int begin;
  
  CharArrayIterator(char[] paramArrayOfChar) { reset(paramArrayOfChar, 0); }
  
  CharArrayIterator(char[] paramArrayOfChar, int paramInt) { reset(paramArrayOfChar, paramInt); }
  
  public char first() {
    this.pos = 0;
    return current();
  }
  
  public char last() {
    if (this.chars.length > 0) {
      this.pos = this.chars.length - 1;
    } else {
      this.pos = 0;
    } 
    return current();
  }
  
  public char current() { return (this.pos >= 0 && this.pos < this.chars.length) ? this.chars[this.pos] : Character.MAX_VALUE; }
  
  public char next() {
    if (this.pos < this.chars.length - 1) {
      this.pos++;
      return this.chars[this.pos];
    } 
    this.pos = this.chars.length;
    return Character.MAX_VALUE;
  }
  
  public char previous() {
    if (this.pos > 0) {
      this.pos--;
      return this.chars[this.pos];
    } 
    this.pos = 0;
    return Character.MAX_VALUE;
  }
  
  public char setIndex(int paramInt) {
    paramInt -= this.begin;
    if (paramInt < 0 || paramInt > this.chars.length)
      throw new IllegalArgumentException("Invalid index"); 
    this.pos = paramInt;
    return current();
  }
  
  public int getBeginIndex() { return this.begin; }
  
  public int getEndIndex() { return this.begin + this.chars.length; }
  
  public int getIndex() { return this.begin + this.pos; }
  
  public Object clone() {
    CharArrayIterator charArrayIterator = new CharArrayIterator(this.chars, this.begin);
    charArrayIterator.pos = this.pos;
    return charArrayIterator;
  }
  
  void reset(char[] paramArrayOfChar) { reset(paramArrayOfChar, 0); }
  
  void reset(char[] paramArrayOfChar, int paramInt) {
    this.chars = paramArrayOfChar;
    this.begin = paramInt;
    this.pos = 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\CharArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */