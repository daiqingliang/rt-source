package com.sun.beans.decoder;

class NullElementHandler extends ElementHandler implements ValueObject {
  protected final ValueObject getValueObject() { return this; }
  
  public Object getValue() { return null; }
  
  public final boolean isVoid() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\NullElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */