package java.text;

import java.io.Serializable;

public abstract class Format implements Serializable, Cloneable {
  private static final long serialVersionUID = -299282585814624189L;
  
  public final String format(Object paramObject) { return format(paramObject, new StringBuffer(), new FieldPosition(0)).toString(); }
  
  public abstract StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject) { return createAttributedCharacterIterator(format(paramObject)); }
  
  public abstract Object parseObject(String paramString, ParsePosition paramParsePosition);
  
  public Object parseObject(String paramString) throws ParseException {
    ParsePosition parsePosition = new ParsePosition(0);
    Object object = parseObject(paramString, parsePosition);
    if (parsePosition.index == 0)
      throw new ParseException("Format.parseObject(String) failed", parsePosition.errorIndex); 
    return object;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(String paramString) {
    AttributedString attributedString = new AttributedString(paramString);
    return attributedString.getIterator();
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(AttributedCharacterIterator[] paramArrayOfAttributedCharacterIterator) {
    AttributedString attributedString = new AttributedString(paramArrayOfAttributedCharacterIterator);
    return attributedString.getIterator();
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(String paramString, AttributedCharacterIterator.Attribute paramAttribute, Object paramObject) {
    AttributedString attributedString = new AttributedString(paramString);
    attributedString.addAttribute(paramAttribute, paramObject);
    return attributedString.getIterator();
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(AttributedCharacterIterator paramAttributedCharacterIterator, AttributedCharacterIterator.Attribute paramAttribute, Object paramObject) {
    AttributedString attributedString = new AttributedString(paramAttributedCharacterIterator);
    attributedString.addAttribute(paramAttribute, paramObject);
    return attributedString.getIterator();
  }
  
  public static class Field extends AttributedCharacterIterator.Attribute {
    private static final long serialVersionUID = 276966692217360283L;
    
    protected Field(String param1String) { super(param1String); }
  }
  
  static interface FieldDelegate {
    void formatted(Format.Field param1Field, Object param1Object, int param1Int1, int param1Int2, StringBuffer param1StringBuffer);
    
    void formatted(int param1Int1, Format.Field param1Field, Object param1Object, int param1Int2, int param1Int3, StringBuffer param1StringBuffer);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\Format.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */