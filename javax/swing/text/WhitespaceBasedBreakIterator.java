package javax.swing.text;

import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;

class WhitespaceBasedBreakIterator extends BreakIterator {
  private char[] text = new char[0];
  
  private int[] breaks = { 0 };
  
  private int pos = 0;
  
  public void setText(CharacterIterator paramCharacterIterator) {
    int i = paramCharacterIterator.getBeginIndex();
    this.text = new char[paramCharacterIterator.getEndIndex() - i];
    int[] arrayOfInt = new int[this.text.length + 1];
    byte b = 0;
    arrayOfInt[b++] = i;
    int j = 0;
    boolean bool = false;
    char c;
    for (c = paramCharacterIterator.first(); c != Character.MAX_VALUE; c = paramCharacterIterator.next()) {
      this.text[j] = c;
      boolean bool1 = Character.isWhitespace(c);
      if (bool && !bool1)
        arrayOfInt[b++] = j + i; 
      bool = bool1;
      j++;
    } 
    if (this.text.length > 0)
      arrayOfInt[b++] = this.text.length + i; 
    System.arraycopy(arrayOfInt, 0, this.breaks = new int[b], 0, b);
  }
  
  public CharacterIterator getText() { return new StringCharacterIterator(new String(this.text)); }
  
  public int first() { return this.breaks[this.pos = 0]; }
  
  public int last() { return this.breaks[this.pos = this.breaks.length - 1]; }
  
  public int current() { return this.breaks[this.pos]; }
  
  public int next() { return (this.pos == this.breaks.length - 1) ? -1 : this.breaks[++this.pos]; }
  
  public int previous() { return (this.pos == 0) ? -1 : this.breaks[--this.pos]; }
  
  public int next(int paramInt) { return checkhit(this.pos + paramInt); }
  
  public int following(int paramInt) { return adjacent(paramInt, 1); }
  
  public int preceding(int paramInt) { return adjacent(paramInt, -1); }
  
  private int checkhit(int paramInt) { return (paramInt < 0 || paramInt >= this.breaks.length) ? -1 : this.breaks[this.pos = paramInt]; }
  
  private int adjacent(int paramInt1, int paramInt2) {
    int i = Arrays.binarySearch(this.breaks, paramInt1);
    int j = (i < 0) ? ((paramInt2 < 0) ? -1 : -2) : 0;
    return checkhit(Math.abs(i) + paramInt2 + j);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\WhitespaceBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */