package sun.print;

import javax.print.AttributeException;
import javax.print.PrintException;
import javax.print.attribute.Attribute;

class PrintJobAttributeException extends PrintException implements AttributeException {
  private Attribute attr;
  
  private Class category;
  
  PrintJobAttributeException(String paramString, Class paramClass, Attribute paramAttribute) {
    super(paramString);
    this.attr = paramAttribute;
    this.category = paramClass;
  }
  
  public Class[] getUnsupportedAttributes() { return (this.category == null) ? null : new Class[] { this.category }; }
  
  public Attribute[] getUnsupportedValues() { return (this.attr == null) ? null : new Attribute[] { this.attr }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PrintJobAttributeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */