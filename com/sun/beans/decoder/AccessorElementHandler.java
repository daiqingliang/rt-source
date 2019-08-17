package com.sun.beans.decoder;

abstract class AccessorElementHandler extends ElementHandler {
  private String name;
  
  private ValueObject value;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("name")) {
      this.name = paramString2;
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  protected final void addArgument(Object paramObject) {
    if (this.value != null)
      throw new IllegalStateException("Could not add argument to evaluated element"); 
    setValue(this.name, paramObject);
    this.value = ValueObjectImpl.VOID;
  }
  
  protected final ValueObject getValueObject() {
    if (this.value == null)
      this.value = ValueObjectImpl.create(getValue(this.name)); 
    return this.value;
  }
  
  protected abstract Object getValue(String paramString);
  
  protected abstract void setValue(String paramString, Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\AccessorElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */