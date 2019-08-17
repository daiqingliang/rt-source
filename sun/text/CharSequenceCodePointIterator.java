package sun.text;

final class CharSequenceCodePointIterator extends CodePointIterator {
  private CharSequence text;
  
  private int index;
  
  public CharSequenceCodePointIterator(CharSequence paramCharSequence) { this.text = paramCharSequence; }
  
  public void setToStart() { this.index = 0; }
  
  public void setToLimit() { this.index = this.text.length(); }
  
  public int next() {
    if (this.index < this.text.length()) {
      char c = this.text.charAt(this.index++);
      if (Character.isHighSurrogate(c) && this.index < this.text.length()) {
        char c1 = this.text.charAt(this.index + 1);
        if (Character.isLowSurrogate(c1)) {
          this.index++;
          return Character.toCodePoint(c, c1);
        } 
      } 
      return c;
    } 
    return -1;
  }
  
  public int prev() {
    if (this.index > 0) {
      char c = this.text.charAt(--this.index);
      if (Character.isLowSurrogate(c) && this.index > 0) {
        char c1 = this.text.charAt(this.index - 1);
        if (Character.isHighSurrogate(c1)) {
          this.index--;
          return Character.toCodePoint(c1, c);
        } 
      } 
      return c;
    } 
    return -1;
  }
  
  public int charIndex() { return this.index; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\CharSequenceCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */