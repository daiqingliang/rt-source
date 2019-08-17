package javax.swing.text;

import java.text.CharacterIterator;

public class Segment implements Cloneable, CharacterIterator, CharSequence {
  public char[] array;
  
  public int offset;
  
  public int count;
  
  private boolean partialReturn;
  
  private int pos;
  
  public Segment() { this(null, 0, 0); }
  
  public Segment(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    this.array = paramArrayOfChar;
    this.offset = paramInt1;
    this.count = paramInt2;
    this.partialReturn = false;
  }
  
  public void setPartialReturn(boolean paramBoolean) { this.partialReturn = paramBoolean; }
  
  public boolean isPartialReturn() { return this.partialReturn; }
  
  public String toString() { return (this.array != null) ? new String(this.array, this.offset, this.count) : ""; }
  
  public char first() {
    this.pos = this.offset;
    return (this.count != 0) ? this.array[this.pos] : Character.MAX_VALUE;
  }
  
  public char last() {
    this.pos = this.offset + this.count;
    if (this.count != 0) {
      this.pos--;
      return this.array[this.pos];
    } 
    return Character.MAX_VALUE;
  }
  
  public char current() { return (this.count != 0 && this.pos < this.offset + this.count) ? this.array[this.pos] : Character.MAX_VALUE; }
  
  public char next() {
    this.pos++;
    int i = this.offset + this.count;
    if (this.pos >= i) {
      this.pos = i;
      return Character.MAX_VALUE;
    } 
    return current();
  }
  
  public char previous() {
    if (this.pos == this.offset)
      return Character.MAX_VALUE; 
    this.pos--;
    return current();
  }
  
  public char setIndex(int paramInt) {
    int i = this.offset + this.count;
    if (paramInt < this.offset || paramInt > i)
      throw new IllegalArgumentException("bad position: " + paramInt); 
    this.pos = paramInt;
    return (this.pos != i && this.count != 0) ? this.array[this.pos] : Character.MAX_VALUE;
  }
  
  public int getBeginIndex() { return this.offset; }
  
  public int getEndIndex() { return this.offset + this.count; }
  
  public int getIndex() { return this.pos; }
  
  public char charAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.count)
      throw new StringIndexOutOfBoundsException(paramInt); 
    return this.array[this.offset + paramInt];
  }
  
  public int length() { return this.count; }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > this.count)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt1 > paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1); 
    Segment segment = new Segment();
    segment.array = this.array;
    this.offset += paramInt1;
    segment.count = paramInt2 - paramInt1;
    return segment;
  }
  
  public Object clone() {
    Object object;
    try {
      object = super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      object = null;
    } 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\Segment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */