package com.sun.beans.decoder;

final class BooleanElementHandler extends StringElementHandler {
  public Object getValue(String paramString) {
    if (Boolean.TRUE.toString().equalsIgnoreCase(paramString))
      return Boolean.TRUE; 
    if (Boolean.FALSE.toString().equalsIgnoreCase(paramString))
      return Boolean.FALSE; 
    throw new IllegalArgumentException("Unsupported boolean argument: " + paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\BooleanElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */