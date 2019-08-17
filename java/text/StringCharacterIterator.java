package java.text;

public final class StringCharacterIterator implements CharacterIterator {
  private String text;
  
  private int begin;
  
  private int end;
  
  private int pos;
  
  public StringCharacterIterator(String paramString) { this(paramString, 0); }
  
  public StringCharacterIterator(String paramString, int paramInt) { this(paramString, 0, paramString.length(), paramInt); }
  
  public StringCharacterIterator(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.text = paramString;
    if (paramInt1 < 0 || paramInt1 > paramInt2 || paramInt2 > paramString.length())
      throw new IllegalArgumentException("Invalid substring range"); 
    if (paramInt3 < paramInt1 || paramInt3 > paramInt2)
      throw new IllegalArgumentException("Invalid position"); 
    this.begin = paramInt1;
    this.end = paramInt2;
    this.pos = paramInt3;
  }
  
  public void setText(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.text = paramString;
    this.begin = 0;
    this.end = paramString.length();
    this.pos = 0;
  }
  
  public char first() {
    this.pos = this.begin;
    return current();
  }
  
  public char last() {
    if (this.end != this.begin) {
      this.pos = this.end - 1;
    } else {
      this.pos = this.end;
    } 
    return current();
  }
  
  public char setIndex(int paramInt) {
    if (paramInt < this.begin || paramInt > this.end)
      throw new IllegalArgumentException("Invalid index"); 
    this.pos = paramInt;
    return current();
  }
  
  public char current() { return (this.pos >= this.begin && this.pos < this.end) ? this.text.charAt(this.pos) : 65535; }
  
  public char next() {
    if (this.pos < this.end - 1) {
      this.pos++;
      return this.text.charAt(this.pos);
    } 
    this.pos = this.end;
    return Character.MAX_VALUE;
  }
  
  public char previous() {
    if (this.pos > this.begin) {
      this.pos--;
      return this.text.charAt(this.pos);
    } 
    return Character.MAX_VALUE;
  }
  
  public int getBeginIndex() { return this.begin; }
  
  public int getEndIndex() { return this.end; }
  
  public int getIndex() { return this.pos; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof StringCharacterIterator))
      return false; 
    StringCharacterIterator stringCharacterIterator = (StringCharacterIterator)paramObject;
    return (hashCode() != stringCharacterIterator.hashCode()) ? false : (!this.text.equals(stringCharacterIterator.text) ? false : (!(this.pos != stringCharacterIterator.pos || this.begin != stringCharacterIterator.begin || this.end != stringCharacterIterator.end)));
  }
  
  public int hashCode() { return this.text.hashCode() ^ this.pos ^ this.begin ^ this.end; }
  
  public Object clone() {
    try {
      return (StringCharacterIterator)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\StringCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */