package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedStaticByteFieldAccessorImpl extends UnsafeQualifiedStaticFieldAccessorImpl {
  UnsafeQualifiedStaticByteFieldAccessorImpl(Field paramField, boolean paramBoolean) { super(paramField, paramBoolean); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Byte(getByte(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException { throw newGetBooleanIllegalArgumentException(); }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { return unsafe.getByteVolatile(this.base, this.fieldOffset); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException { throw newGetCharIllegalArgumentException(); }
  
  public short getShort(Object paramObject) throws IllegalArgumentException { return (short)getByte(paramObject); }
  
  public int getInt(Object paramObject) throws IllegalArgumentException { return getByte(paramObject); }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { return getByte(paramObject); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { return getByte(paramObject); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException { return getByte(paramObject); }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Byte) {
      unsafe.putByteVolatile(this.base, this.fieldOffset, ((Byte)paramObject2).byteValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramBoolean); }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException {
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramByte); 
    unsafe.putByteVolatile(this.base, this.fieldOffset, paramByte);
  }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramChar); }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramShort); }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramInt); }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramFloat); }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeQualifiedStaticByteFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */