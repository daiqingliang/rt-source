package sun.text.normalizer;

import java.text.CharacterIterator;

public abstract class UCharacterIterator implements Cloneable {
  public static final int DONE = -1;
  
  public static final UCharacterIterator getInstance(String paramString) { return new ReplaceableUCharacterIterator(paramString); }
  
  public static final UCharacterIterator getInstance(StringBuffer paramStringBuffer) { return new ReplaceableUCharacterIterator(paramStringBuffer); }
  
  public static final UCharacterIterator getInstance(CharacterIterator paramCharacterIterator) { return new CharacterIteratorWrapper(paramCharacterIterator); }
  
  public abstract int current();
  
  public abstract int getLength();
  
  public abstract int getIndex();
  
  public abstract int next();
  
  public int nextCodePoint() {
    int i = next();
    if (UTF16.isLeadSurrogate((char)i)) {
      int j = next();
      if (UTF16.isTrailSurrogate((char)j))
        return UCharacterProperty.getRawSupplementary((char)i, (char)j); 
      if (j != -1)
        previous(); 
    } 
    return i;
  }
  
  public abstract int previous();
  
  public abstract void setIndex(int paramInt);
  
  public abstract int getText(char[] paramArrayOfChar, int paramInt);
  
  public final int getText(char[] paramArrayOfChar) { return getText(paramArrayOfChar, 0); }
  
  public String getText() {
    char[] arrayOfChar = new char[getLength()];
    getText(arrayOfChar);
    return new String(arrayOfChar);
  }
  
  public int moveIndex(int paramInt) {
    int i = Math.max(0, Math.min(getIndex() + paramInt, getLength()));
    setIndex(i);
    return i;
  }
  
  public Object clone() throws CloneNotSupportedException { return super.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\UCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */