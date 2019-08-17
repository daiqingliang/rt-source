package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedStaticFloatFieldAccessorImpl extends UnsafeQualifiedStaticFieldAccessorImpl {
  UnsafeQualifiedStaticFloatFieldAccessorImpl(Field paramField, boolean paramBoolean) { super(paramField, paramBoolean); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Float(getFloat(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException { throw newGetBooleanIllegalArgumentException(); }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { throw newGetByteIllegalArgumentException(); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException { throw newGetCharIllegalArgumentException(); }
  
  public short getShort(Object paramObject) throws IllegalArgumentException { throw newGetShortIllegalArgumentException(); }
  
  public int getInt(Object paramObject) throws IllegalArgumentException { throw newGetIntIllegalArgumentException(); }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { throw newGetLongIllegalArgumentException(); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { return unsafe.getFloatVolatile(this.base, this.fieldOffset); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException { return getFloat(paramObject); }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Byte) {
      unsafe.putFloatVolatile(this.base, this.fieldOffset, ((Byte)paramObject2).byteValue());
      return;
    } 
    if (paramObject2 instanceof Short) {
      unsafe.putFloatVolatile(this.base, this.fieldOffset, ((Short)paramObject2).shortValue());
      return;
    } 
    if (paramObject2 instanceof Character) {
      unsafe.putFloatVolatile(this.base, this.fieldOffset, ((Character)paramObject2).charValue());
      return;
    } 
    if (paramObject2 instanceof Integer) {
      unsafe.putFloatVolatile(this.base, this.fieldOffset, ((Integer)paramObject2).intValue());
      return;
    } 
    if (paramObject2 instanceof Long) {
      unsafe.putFloatVolatile(this.base, this.fieldOffset, (float)((Long)paramObject2).longValue());
      return;
    } 
    if (paramObject2 instanceof Float) {
      unsafe.putFloatVolatile(this.base, this.fieldOffset, ((Float)paramObject2).floatValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramBoolean); }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException { setFloat(paramObject, paramByte); }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException { setFloat(paramObject, paramChar); }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException { setFloat(paramObject, paramShort); }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException { setFloat(paramObject, paramInt); }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { setFloat(paramObject, (float)paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException {
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramFloat); 
    unsafe.putFloatVolatile(this.base, this.fieldOffset, paramFloat);
  }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeQualifiedStaticFloatFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */