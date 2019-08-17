package com.sun.beans.decoder;

final class VarElementHandler extends ElementHandler {
  private ValueObject value;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("idref")) {
      this.value = ValueObjectImpl.create(getVariable(paramString2));
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  protected ValueObject getValueObject() {
    if (this.value == null)
      throw new IllegalArgumentException("Variable name is not set"); 
    return this.value;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\VarElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */