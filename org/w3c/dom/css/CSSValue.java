package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSValue {
  public static final short CSS_INHERIT = 0;
  
  public static final short CSS_PRIMITIVE_VALUE = 1;
  
  public static final short CSS_VALUE_LIST = 2;
  
  public static final short CSS_CUSTOM = 3;
  
  String getCssText();
  
  void setCssText(String paramString) throws DOMException;
  
  short getCssValueType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\CSSValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */