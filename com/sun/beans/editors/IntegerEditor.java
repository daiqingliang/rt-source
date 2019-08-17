package com.sun.beans.editors;

public class IntegerEditor extends NumberEditor {
  public void setAsText(String paramString) throws IllegalArgumentException { setValue((paramString == null) ? null : Integer.decode(paramString)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\IntegerEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */