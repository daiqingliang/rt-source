package sun.text;

import java.text.CharacterIterator;

final class CharacterIteratorCodePointIterator extends CodePointIterator {
  private CharacterIterator iter;
  
  public CharacterIteratorCodePointIterator(CharacterIterator paramCharacterIterator) { this.iter = paramCharacterIterator; }
  
  public void setToStart() { this.iter.setIndex(this.iter.getBeginIndex()); }
  
  public void setToLimit() { this.iter.setIndex(this.iter.getEndIndex()); }
  
  public int next() {
    char c = this.iter.current();
    if (c != Character.MAX_VALUE) {
      char c1 = this.iter.next();
      if (Character.isHighSurrogate(c) && c1 != Character.MAX_VALUE && Character.isLowSurrogate(c1)) {
        this.iter.next();
        return Character.toCodePoint(c, c1);
      } 
      return c;
    } 
    return -1;
  }
  
  public int prev() {
    char c = this.iter.previous();
    if (c != Character.MAX_VALUE) {
      if (Character.isLowSurrogate(c)) {
        char c1 = this.iter.previous();
        if (Character.isHighSurrogate(c1))
          return Character.toCodePoint(c1, c); 
        this.iter.next();
      } 
      return c;
    } 
    return -1;
  }
  
  public int charIndex() { return this.iter.getIndex(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\CharacterIteratorCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */