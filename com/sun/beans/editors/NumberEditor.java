package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public abstract class NumberEditor extends PropertyEditorSupport {
  public String getJavaInitializationString() {
    Object object = getValue();
    return (object != null) ? object.toString() : "null";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\NumberEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */