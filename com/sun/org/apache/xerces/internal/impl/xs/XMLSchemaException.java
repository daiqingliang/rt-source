package com.sun.org.apache.xerces.internal.impl.xs;

public class XMLSchemaException extends Exception {
  static final long serialVersionUID = -9096984648537046218L;
  
  String key;
  
  Object[] args;
  
  public XMLSchemaException(String paramString, Object[] paramArrayOfObject) {
    this.key = paramString;
    this.args = paramArrayOfObject;
  }
  
  public String getKey() { return this.key; }
  
  public Object[] getArgs() { return this.args; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XMLSchemaException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */