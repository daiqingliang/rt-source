package sun.reflect;

import java.lang.reflect.Field;

class UnsafeShortFieldAccessorImpl extends UnsafeFieldAccessorImpl {
  UnsafeShortFieldAccessorImpl(Field paramField) { super(paramField); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Short(getShort(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException { throw newGetBooleanIllegalArgumentException(); }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { throw newGetByteIllegalArgumentException(); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException { throw newGetCharIllegalArgumentException(); }
  
  public short getShort(Object paramObject) throws IllegalArgumentException {
    ensureObj(paramObject);
    return unsafe.getShort(paramObject, this.fieldOffset);
  }
  
  public int getInt(Object paramObject) throws IllegalArgumentException { return getShort(paramObject); }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { return getShort(paramObject); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { return getShort(paramObject); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException { return getShort(paramObject); }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject1);
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Byte) {
      unsafe.putShort(paramObject1, this.fieldOffset, (short)((Byte)paramObject2).byteValue());
      return;
    } 
    if (paramObject2 instanceof Short) {
      unsafe.putShort(paramObject1, this.fieldOffset, ((Short)paramObject2).shortValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramBoolean); }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException { setShort(paramObject, (short)paramByte); }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramChar); }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject);
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramShort); 
    unsafe.putShort(paramObject, this.fieldOffset, paramShort);
  }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramInt); }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramFloat); }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeShortFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */