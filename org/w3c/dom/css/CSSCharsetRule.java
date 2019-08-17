package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSCharsetRule extends CSSRule {
  String getEncoding();
  
  void setEncoding(String paramString) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\CSSCharsetRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */