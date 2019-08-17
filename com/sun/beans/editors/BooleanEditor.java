package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class BooleanEditor extends PropertyEditorSupport {
  public String getJavaInitializationString() {
    Object object = getValue();
    return (object != null) ? object.toString() : "null";
  }
  
  public String getAsText() {
    Object object = getValue();
    return (object instanceof Boolean) ? getValidName(((Boolean)object).booleanValue()) : null;
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException {
    if (paramString == null) {
      setValue(null);
    } else if (isValidName(true, paramString)) {
      setValue(Boolean.TRUE);
    } else if (isValidName(false, paramString)) {
      setValue(Boolean.FALSE);
    } else {
      throw new IllegalArgumentException(paramString);
    } 
  }
  
  public String[] getTags() { return new String[] { getValidName(true), getValidName(false) }; }
  
  private String getValidName(boolean paramBoolean) { return paramBoolean ? "True" : "False"; }
  
  private boolean isValidName(boolean paramBoolean, String paramString) { return getValidName(paramBoolean).equalsIgnoreCase(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\BooleanEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */