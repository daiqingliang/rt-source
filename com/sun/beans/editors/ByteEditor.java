package com.sun.beans.editors;

public class ByteEditor extends NumberEditor {
  public String getJavaInitializationString() {
    Object object = getValue();
    return (object != null) ? ("((byte)" + object + ")") : "null";
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException { setValue((paramString == null) ? null : Byte.decode(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\ByteEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */