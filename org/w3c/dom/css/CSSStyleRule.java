package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSStyleRule extends CSSRule {
  String getSelectorText();
  
  void setSelectorText(String paramString) throws DOMException;
  
  CSSStyleDeclaration getStyle();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\CSSStyleRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */