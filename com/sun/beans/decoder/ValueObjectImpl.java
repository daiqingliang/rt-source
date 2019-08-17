package com.sun.beans.decoder;

final class ValueObjectImpl implements ValueObject {
  static final ValueObject NULL = new ValueObjectImpl(null);
  
  static final ValueObject VOID = new ValueObjectImpl();
  
  private Object value;
  
  private boolean isVoid;
  
  static ValueObject create(Object paramObject) { return (paramObject != null) ? new ValueObjectImpl(paramObject) : NULL; }
  
  private ValueObjectImpl() { this.isVoid = true; }
  
  private ValueObjectImpl(Object paramObject) { this.value = paramObject; }
  
  public Object getValue() { return this.value; }
  
  public boolean isVoid() { return this.isVoid; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\ValueObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */