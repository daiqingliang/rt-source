package sun.reflect;

import java.lang.reflect.Field;

class UnsafeCharacterFieldAccessorImpl extends UnsafeFieldAccessorImpl {
  UnsafeCharacterFieldAccessorImpl(Field paramField) { super(paramField); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Character(getChar(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException { throw newGetBooleanIllegalArgumentException(); }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { throw newGetByteIllegalArgumentException(); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException {
    ensureObj(paramObject);
    return unsafe.getChar(paramObject, this.fieldOffset);
  }
  
  public short getShort(Object paramObject) throws IllegalArgumentException { throw newGetShortIllegalArgumentException(); }
  
  public int getInt(Object paramObject) throws IllegalArgumentException { return getChar(paramObject); }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { return getChar(paramObject); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { return getChar(paramObject); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException { return getChar(paramObject); }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject1);
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Character) {
      unsafe.putChar(paramObject1, this.fieldOffset, ((Character)paramObject2).charValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramBoolean); }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramByte); }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject);
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramChar); 
    unsafe.putChar(paramObject, this.fieldOffset, paramChar);
  }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramShort); }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramInt); }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramFloat); }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeCharacterFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */