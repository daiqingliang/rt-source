package org.w3c.dom.html;

import org.w3c.dom.Document;

public interface HTMLObjectElement extends HTMLElement {
  HTMLFormElement getForm();
  
  String getCode();
  
  void setCode(String paramString);
  
  String getAlign();
  
  void setAlign(String paramString);
  
  String getArchive();
  
  void setArchive(String paramString);
  
  String getBorder();
  
  void setBorder(String paramString);
  
  String getCodeBase();
  
  void setCodeBase(String paramString);
  
  String getCodeType();
  
  void setCodeType(String paramString);
  
  String getData();
  
  void setData(String paramString);
  
  boolean getDeclare();
  
  void setDeclare(boolean paramBoolean);
  
  String getHeight();
  
  void setHeight(String paramString);
  
  String getHspace();
  
  void setHspace(String paramString);
  
  String getName();
  
  void setName(String paramString);
  
  String getStandby();
  
  void setStandby(String paramString);
  
  int getTabIndex();
  
  void setTabIndex(int paramInt);
  
  String getType();
  
  void setType(String paramString);
  
  String getUseMap();
  
  void setUseMap(String paramString);
  
  String getVspace();
  
  void setVspace(String paramString);
  
  String getWidth();
  
  void setWidth(String paramString);
  
  Document getContentDocument();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\html\HTMLObjectElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */