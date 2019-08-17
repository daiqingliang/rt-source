package com.sun.beans.decoder;

import java.lang.reflect.Array;

final class ArrayElementHandler extends NewElementHandler {
  private Integer length;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("length")) {
      this.length = Integer.valueOf(paramString2);
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  public void startElement() {
    if (this.length != null)
      getValueObject(); 
  }
  
  protected boolean isArgument() { return true; }
  
  protected ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject) {
    if (paramClass == null)
      paramClass = Object.class; 
    if (this.length != null)
      return ValueObjectImpl.create(Array.newInstance(paramClass, this.length.intValue())); 
    Object object = Array.newInstance(paramClass, paramArrayOfObject.length);
    for (byte b = 0; b < paramArrayOfObject.length; b++)
      Array.set(object, b, paramArrayOfObject[b]); 
    return ValueObjectImpl.create(object);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\ArrayElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */