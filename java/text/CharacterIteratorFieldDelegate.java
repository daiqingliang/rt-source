package java.text;

import java.util.ArrayList;

class CharacterIteratorFieldDelegate implements Format.FieldDelegate {
  private ArrayList<AttributedString> attributedStrings = new ArrayList();
  
  private int size;
  
  public void formatted(Format.Field paramField, Object paramObject, int paramInt1, int paramInt2, StringBuffer paramStringBuffer) {
    if (paramInt1 != paramInt2) {
      if (paramInt1 < this.size) {
        int i = this.size;
        int j = this.attributedStrings.size() - 1;
        while (paramInt1 < i) {
          AttributedString attributedString = (AttributedString)this.attributedStrings.get(j--);
          int k = i - attributedString.length();
          int m = Math.max(0, paramInt1 - k);
          attributedString.addAttribute(paramField, paramObject, m, Math.min(paramInt2 - paramInt1, attributedString.length() - m) + m);
          i = k;
        } 
      } 
      if (this.size < paramInt1) {
        this.attributedStrings.add(new AttributedString(paramStringBuffer.substring(this.size, paramInt1)));
        this.size = paramInt1;
      } 
      if (this.size < paramInt2) {
        int i = Math.max(paramInt1, this.size);
        AttributedString attributedString = new AttributedString(paramStringBuffer.substring(i, paramInt2));
        attributedString.addAttribute(paramField, paramObject);
        this.attributedStrings.add(attributedString);
        this.size = paramInt2;
      } 
    } 
  }
  
  public void formatted(int paramInt1, Format.Field paramField, Object paramObject, int paramInt2, int paramInt3, StringBuffer paramStringBuffer) { formatted(paramField, paramObject, paramInt2, paramInt3, paramStringBuffer); }
  
  public AttributedCharacterIterator getIterator(String paramString) {
    if (paramString.length() > this.size) {
      this.attributedStrings.add(new AttributedString(paramString.substring(this.size)));
      this.size = paramString.length();
    } 
    int i = this.attributedStrings.size();
    AttributedCharacterIterator[] arrayOfAttributedCharacterIterator = new AttributedCharacterIterator[i];
    for (byte b = 0; b < i; b++)
      arrayOfAttributedCharacterIterator[b] = ((AttributedString)this.attributedStrings.get(b)).getIterator(); 
    return (new AttributedString(arrayOfAttributedCharacterIterator)).getIterator();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\CharacterIteratorFieldDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */