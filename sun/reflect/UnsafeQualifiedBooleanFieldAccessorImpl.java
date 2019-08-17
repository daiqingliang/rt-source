package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedBooleanFieldAccessorImpl extends UnsafeQualifiedFieldAccessorImpl {
  UnsafeQualifiedBooleanFieldAccessorImpl(Field paramField, boolean paramBoolean) { super(paramField, paramBoolean); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Boolean(getBoolean(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException {
    ensureObj(paramObject);
    return unsafe.getBooleanVolatile(paramObject, this.fieldOffset);
  }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { throw newGetByteIllegalArgumentException(); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException { throw newGetCharIllegalArgumentException(); }
  
  public short getShort(Object paramObject) throws IllegalArgumentException { throw newGetShortIllegalArgumentException(); }
  
  public int getInt(Object paramObject) throws IllegalArgumentException { throw newGetIntIllegalArgumentException(); }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { throw newGetLongIllegalArgumentException(); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { throw newGetFloatIllegalArgumentException(); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException { throw newGetDoubleIllegalArgumentException(); }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject1);
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Boolean) {
      unsafe.putBooleanVolatile(paramObject1, this.fieldOffset, ((Boolean)paramObject2).booleanValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject);
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramBoolean); 
    unsafe.putBooleanVolatile(paramObject, this.fieldOffset, paramBoolean);
  }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramByte); }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramChar); }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramShort); }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramInt); }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramFloat); }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeQualifiedBooleanFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */