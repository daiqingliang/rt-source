package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSStyleDeclaration {
  String getCssText();
  
  void setCssText(String paramString) throws DOMException;
  
  String getPropertyValue(String paramString);
  
  CSSValue getPropertyCSSValue(String paramString);
  
  String removeProperty(String paramString);
  
  String getPropertyPriority(String paramString);
  
  void setProperty(String paramString1, String paramString2, String paramString3) throws DOMException;
  
  int getLength();
  
  String item(int paramInt);
  
  CSSRule getParentRule();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\CSSStyleDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */