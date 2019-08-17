package com.sun.beans.editors;

public class ShortEditor extends NumberEditor {
  public String getJavaInitializationString() {
    Object object = getValue();
    return (object != null) ? ("((short)" + object + ")") : "null";
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException { setValue((paramString == null) ? null : Short.decode(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\ShortEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */