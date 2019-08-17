package com.sun.org.apache.xalan.internal.xsltc.runtime;

public class Parameter {
  public String _name;
  
  public Object _value;
  
  public boolean _isDefault;
  
  public Parameter(String paramString, Object paramObject) {
    this._name = paramString;
    this._value = paramObject;
    this._isDefault = true;
  }
  
  public Parameter(String paramString, Object paramObject, boolean paramBoolean) {
    this._name = paramString;
    this._value = paramObject;
    this._isDefault = paramBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */