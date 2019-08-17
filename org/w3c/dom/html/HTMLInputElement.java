package org.w3c.dom.html;

public interface HTMLInputElement extends HTMLElement {
  String getDefaultValue();
  
  void setDefaultValue(String paramString);
  
  boolean getDefaultChecked();
  
  void setDefaultChecked(boolean paramBoolean);
  
  HTMLFormElement getForm();
  
  String getAccept();
  
  void setAccept(String paramString);
  
  String getAccessKey();
  
  void setAccessKey(String paramString);
  
  String getAlign();
  
  void setAlign(String paramString);
  
  String getAlt();
  
  void setAlt(String paramString);
  
  boolean getChecked();
  
  void setChecked(boolean paramBoolean);
  
  boolean getDisabled();
  
  void setDisabled(boolean paramBoolean);
  
  int getMaxLength();
  
  void setMaxLength(int paramInt);
  
  String getName();
  
  void setName(String paramString);
  
  boolean getReadOnly();
  
  void setReadOnly(boolean paramBoolean);
  
  String getSize();
  
  void setSize(String paramString);
  
  String getSrc();
  
  void setSrc(String paramString);
  
  int getTabIndex();
  
  void setTabIndex(int paramInt);
  
  String getType();
  
  String getUseMap();
  
  void setUseMap(String paramString);
  
  String getValue();
  
  void setValue(String paramString);
  
  void blur();
  
  void focus();
  
  void select();
  
  void click();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\html\HTMLInputElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */