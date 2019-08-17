package sun.text.normalizer;

import java.text.CharacterIterator;

public class CharacterIteratorWrapper extends UCharacterIterator {
  private CharacterIterator iterator;
  
  public CharacterIteratorWrapper(CharacterIterator paramCharacterIterator) {
    if (paramCharacterIterator == null)
      throw new IllegalArgumentException(); 
    this.iterator = paramCharacterIterator;
  }
  
  public int current() {
    char c = this.iterator.current();
    return (c == Character.MAX_VALUE) ? -1 : c;
  }
  
  public int getLength() { return this.iterator.getEndIndex() - this.iterator.getBeginIndex(); }
  
  public int getIndex() { return this.iterator.getIndex(); }
  
  public int next() {
    char c = this.iterator.current();
    this.iterator.next();
    return (c == Character.MAX_VALUE) ? -1 : c;
  }
  
  public int previous() {
    char c = this.iterator.previous();
    return (c == Character.MAX_VALUE) ? -1 : c;
  }
  
  public void setIndex(int paramInt) { this.iterator.setIndex(paramInt); }
  
  public int getText(char[] paramArrayOfChar, int paramInt) {
    int i = this.iterator.getEndIndex() - this.iterator.getBeginIndex();
    int j = this.iterator.getIndex();
    if (paramInt < 0 || paramInt + i > paramArrayOfChar.length)
      throw new IndexOutOfBoundsException(Integer.toString(i)); 
    char c;
    for (c = this.iterator.first(); c != Character.MAX_VALUE; c = this.iterator.next())
      paramArrayOfChar[paramInt++] = c; 
    this.iterator.setIndex(j);
    return i;
  }
  
  public Object clone() {
    try {
      CharacterIteratorWrapper characterIteratorWrapper = (CharacterIteratorWrapper)super.clone();
      characterIteratorWrapper.iterator = (CharacterIterator)this.iterator.clone();
      return characterIteratorWrapper;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\CharacterIteratorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */