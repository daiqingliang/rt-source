package com.sun.beans.decoder;

public class StringElementHandler extends ElementHandler {
  private StringBuilder sb = new StringBuilder();
  
  private ValueObject value = ValueObjectImpl.NULL;
  
  public final void addCharacter(char paramChar) {
    if (this.sb == null)
      throw new IllegalStateException("Could not add chararcter to evaluated string element"); 
    this.sb.append(paramChar);
  }
  
  protected final void addArgument(Object paramObject) {
    if (this.sb == null)
      throw new IllegalStateException("Could not add argument to evaluated string element"); 
    this.sb.append(paramObject);
  }
  
  protected final ValueObject getValueObject() {
    if (this.sb != null)
      try {
        this.value = ValueObjectImpl.create(getValue(this.sb.toString()));
      } catch (RuntimeException runtimeException) {
        getOwner().handleException(runtimeException);
      } finally {
        this.sb = null;
      }  
    return this.value;
  }
  
  protected Object getValue(String paramString) { return paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\StringElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */