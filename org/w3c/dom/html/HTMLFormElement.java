package org.w3c.dom.html;

public interface HTMLFormElement extends HTMLElement {
  HTMLCollection getElements();
  
  int getLength();
  
  String getName();
  
  void setName(String paramString);
  
  String getAcceptCharset();
  
  void setAcceptCharset(String paramString);
  
  String getAction();
  
  void setAction(String paramString);
  
  String getEnctype();
  
  void setEnctype(String paramString);
  
  String getMethod();
  
  void setMethod(String paramString);
  
  String getTarget();
  
  void setTarget(String paramString);
  
  void submit();
  
  void reset();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\html\HTMLFormElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */