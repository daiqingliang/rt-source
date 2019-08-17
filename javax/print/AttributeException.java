package javax.print;

import javax.print.attribute.Attribute;

public interface AttributeException {
  Class[] getUnsupportedAttributes();
  
  Attribute[] getUnsupportedValues();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\AttributeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */