package org.w3c.dom.html;

import org.w3c.dom.Document;

public interface HTMLFrameElement extends HTMLElement {
  String getFrameBorder();
  
  void setFrameBorder(String paramString);
  
  String getLongDesc();
  
  void setLongDesc(String paramString);
  
  String getMarginHeight();
  
  void setMarginHeight(String paramString);
  
  String getMarginWidth();
  
  void setMarginWidth(String paramString);
  
  String getName();
  
  void setName(String paramString);
  
  boolean getNoResize();
  
  void setNoResize(boolean paramBoolean);
  
  String getScrolling();
  
  void setScrolling(String paramString);
  
  String getSrc();
  
  void setSrc(String paramString);
  
  Document getContentDocument();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\html\HTMLFrameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */