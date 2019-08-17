package com.sun.beans.decoder;

final class LongElementHandler extends StringElementHandler {
  public Object getValue(String paramString) { return Long.decode(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\LongElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */