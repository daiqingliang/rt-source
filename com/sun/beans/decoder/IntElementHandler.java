package com.sun.beans.decoder;

final class IntElementHandler extends StringElementHandler {
  public Object getValue(String paramString) { return Integer.decode(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\IntElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */