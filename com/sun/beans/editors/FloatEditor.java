package com.sun.beans.editors;

public class FloatEditor extends NumberEditor {
  public String getJavaInitializationString() {
    Object object = getValue();
    return (object != null) ? (object + "F") : "null";
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException { setValue((paramString == null) ? null : Float.valueOf(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\FloatEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */