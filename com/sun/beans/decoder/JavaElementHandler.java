package com.sun.beans.decoder;

import java.beans.XMLDecoder;

final class JavaElementHandler extends ElementHandler {
  private Class<?> type;
  
  private ValueObject value;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (!paramString1.equals("version"))
      if (paramString1.equals("class")) {
        this.type = getOwner().findClass(paramString2);
      } else {
        super.addAttribute(paramString1, paramString2);
      }  
  }
  
  protected void addArgument(Object paramObject) { getOwner().addObject(paramObject); }
  
  protected boolean isArgument() { return false; }
  
  protected ValueObject getValueObject() {
    if (this.value == null)
      this.value = ValueObjectImpl.create(getValue()); 
    return this.value;
  }
  
  private Object getValue() {
    Object object = getOwner().getOwner();
    if (this.type == null || isValid(object))
      return object; 
    if (object instanceof XMLDecoder) {
      XMLDecoder xMLDecoder = (XMLDecoder)object;
      object = xMLDecoder.getOwner();
      if (isValid(object))
        return object; 
    } 
    throw new IllegalStateException("Unexpected owner class: " + object.getClass().getName());
  }
  
  private boolean isValid(Object paramObject) { return (paramObject == null || this.type.isInstance(paramObject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\JavaElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */