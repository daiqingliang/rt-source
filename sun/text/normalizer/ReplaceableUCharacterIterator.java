package sun.text.normalizer;

public class ReplaceableUCharacterIterator extends UCharacterIterator {
  private Replaceable replaceable;
  
  private int currentIndex;
  
  public ReplaceableUCharacterIterator(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    this.replaceable = new ReplaceableString(paramString);
    this.currentIndex = 0;
  }
  
  public ReplaceableUCharacterIterator(StringBuffer paramStringBuffer) {
    if (paramStringBuffer == null)
      throw new IllegalArgumentException(); 
    this.replaceable = new ReplaceableString(paramStringBuffer);
    this.currentIndex = 0;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public int current() { return (this.currentIndex < this.replaceable.length()) ? this.replaceable.charAt(this.currentIndex) : -1; }
  
  public int getLength() { return this.replaceable.length(); }
  
  public int getIndex() { return this.currentIndex; }
  
  public int next() { return (this.currentIndex < this.replaceable.length()) ? this.replaceable.charAt(this.currentIndex++) : -1; }
  
  public int previous() { return (this.currentIndex > 0) ? this.replaceable.charAt(--this.currentIndex) : -1; }
  
  public void setIndex(int paramInt) {
    if (paramInt < 0 || paramInt > this.replaceable.length())
      throw new IllegalArgumentException(); 
    this.currentIndex = paramInt;
  }
  
  public int getText(char[] paramArrayOfChar, int paramInt) {
    int i = this.replaceable.length();
    if (paramInt < 0 || paramInt + i > paramArrayOfChar.length)
      throw new IndexOutOfBoundsException(Integer.toString(i)); 
    this.replaceable.getChars(0, i, paramArrayOfChar, paramInt);
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\ReplaceableUCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */