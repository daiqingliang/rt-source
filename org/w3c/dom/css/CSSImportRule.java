package org.w3c.dom.css;

import org.w3c.dom.stylesheets.MediaList;

public interface CSSImportRule extends CSSRule {
  String getHref();
  
  MediaList getMedia();
  
  CSSStyleSheet getStyleSheet();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\css\CSSImportRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */