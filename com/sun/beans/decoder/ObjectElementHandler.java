package com.sun.beans.decoder;

import java.beans.Expression;
import java.util.Locale;

class ObjectElementHandler extends NewElementHandler {
  private String idref;
  
  private String field;
  
  private Integer index;
  
  private String property;
  
  private String method;
  
  public final void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("idref")) {
      this.idref = paramString2;
    } else if (paramString1.equals("field")) {
      this.field = paramString2;
    } else if (paramString1.equals("index")) {
      this.index = Integer.valueOf(paramString2);
      addArgument(this.index);
    } else if (paramString1.equals("property")) {
      this.property = paramString2;
    } else if (paramString1.equals("method")) {
      this.method = paramString2;
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  public final void startElement() {
    if (this.field != null || this.idref != null)
      getValueObject(); 
  }
  
  protected boolean isArgument() { return true; }
  
  protected final ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject) throws Exception {
    String str;
    if (this.field != null)
      return ValueObjectImpl.create(FieldElementHandler.getFieldValue(getContextBean(), this.field)); 
    if (this.idref != null)
      return ValueObjectImpl.create(getVariable(this.idref)); 
    Object object = getContextBean();
    if (this.index != null) {
      str = (paramArrayOfObject.length == 2) ? "set" : "get";
    } else if (this.property != null) {
      str = (paramArrayOfObject.length == 1) ? "set" : "get";
      if (0 < this.property.length())
        str = str + this.property.substring(0, 1).toUpperCase(Locale.ENGLISH) + this.property.substring(1); 
    } else {
      str = (this.method != null && 0 < this.method.length()) ? this.method : "new";
    } 
    Expression expression = new Expression(object, str, paramArrayOfObject);
    return ValueObjectImpl.create(expression.getValue());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\ObjectElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */