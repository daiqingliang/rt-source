package com.sun.beans.editors;

public class LongEditor extends NumberEditor {
  public String getJavaInitializationString() {
    Object object = getValue();
    return (object != null) ? (object + "L") : "null";
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException { setValue((paramString == null) ? null : Long.decode(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\LongEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */