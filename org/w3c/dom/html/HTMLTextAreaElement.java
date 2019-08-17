package org.w3c.dom.html;

public interface HTMLTextAreaElement extends HTMLElement {
  String getDefaultValue();
  
  void setDefaultValue(String paramString);
  
  HTMLFormElement getForm();
  
  String getAccessKey();
  
  void setAccessKey(String paramString);
  
  int getCols();
  
  void setCols(int paramInt);
  
  boolean getDisabled();
  
  void setDisabled(boolean paramBoolean);
  
  String getName();
  
  void setName(String paramString);
  
  boolean getReadOnly();
  
  void setReadOnly(boolean paramBoolean);
  
  int getRows();
  
  void setRows(int paramInt);
  
  int getTabIndex();
  
  void setTabIndex(int paramInt);
  
  String getType();
  
  String getValue();
  
  void setValue(String paramString);
  
  void blur();
  
  void focus();
  
  void select();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\html\HTMLTextAreaElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */