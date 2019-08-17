package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSRule {
  public static final short UNKNOWN_RULE = 0;
  
  public static final short STYLE_RULE = 1;
  
  public static final short CHARSET_RULE = 2;
  
  public static final short IMPORT_RULE = 3;
  
  public static final short MEDIA_RULE = 4;
  
  public static final short FONT_FACE_RULE = 5;
  
  public static final short PAGE_RULE = 6;
  
  short getType();
  
  String getCssText();
  
  void setCssText(String paramString) throws DOMException;
  
  CSSStyleSheet getParentStyleSheet();
  
  CSSRule getParentRule();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\CSSRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */