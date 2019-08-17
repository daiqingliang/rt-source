package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedIntegerFieldAccessorImpl extends UnsafeQualifiedFieldAccessorImpl {
  UnsafeQualifiedIntegerFieldAccessorImpl(Field paramField, boolean paramBoolean) { super(paramField, paramBoolean); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Integer(getInt(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException { throw newGetBooleanIllegalArgumentException(); }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { throw newGetByteIllegalArgumentException(); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException { throw newGetCharIllegalArgumentException(); }
  
  public short getShort(Object paramObject) throws IllegalArgumentException { throw newGetShortIllegalArgumentException(); }
  
  public int getInt(Object paramObject) throws IllegalArgumentException {
    ensureObj(paramObject);
    return unsafe.getIntVolatile(paramObject, this.fieldOffset);
  }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { return getInt(paramObject); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { return getInt(paramObject); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException { return getInt(paramObject); }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject1);
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Byte) {
      unsafe.putIntVolatile(paramObject1, this.fieldOffset, ((Byte)paramObject2).byteValue());
      return;
    } 
    if (paramObject2 instanceof Short) {
      unsafe.putIntVolatile(paramObject1, this.fieldOffset, ((Short)paramObject2).shortValue());
      return;
    } 
    if (paramObject2 instanceof Character) {
      unsafe.putIntVolatile(paramObject1, this.fieldOffset, ((Character)paramObject2).charValue());
      return;
    } 
    if (paramObject2 instanceof Integer) {
      unsafe.putIntVolatile(paramObject1, this.fieldOffset, ((Integer)paramObject2).intValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramBoolean); }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException { setInt(paramObject, paramByte); }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException { setInt(paramObject, paramChar); }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException { setInt(paramObject, paramShort); }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject);
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramInt); 
    unsafe.putIntVolatile(paramObject, this.fieldOffset, paramInt);
  }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramFloat); }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeQualifiedIntegerFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */