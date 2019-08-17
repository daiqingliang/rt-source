package sun.reflect;

import java.lang.reflect.Field;

class UnsafeDoubleFieldAccessorImpl extends UnsafeFieldAccessorImpl {
  UnsafeDoubleFieldAccessorImpl(Field paramField) { super(paramField); }
  
  public Object get(Object paramObject) throws IllegalArgumentException { return new Double(getDouble(paramObject)); }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException { throw newGetBooleanIllegalArgumentException(); }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException { throw newGetByteIllegalArgumentException(); }
  
  public char getChar(Object paramObject) throws IllegalArgumentException { throw newGetCharIllegalArgumentException(); }
  
  public short getShort(Object paramObject) throws IllegalArgumentException { throw newGetShortIllegalArgumentException(); }
  
  public int getInt(Object paramObject) throws IllegalArgumentException { throw newGetIntIllegalArgumentException(); }
  
  public long getLong(Object paramObject) throws IllegalArgumentException { throw newGetLongIllegalArgumentException(); }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException { throw newGetFloatIllegalArgumentException(); }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException {
    ensureObj(paramObject);
    return unsafe.getDouble(paramObject, this.fieldOffset);
  }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject1);
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Byte) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Byte)paramObject2).byteValue());
      return;
    } 
    if (paramObject2 instanceof Short) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Short)paramObject2).shortValue());
      return;
    } 
    if (paramObject2 instanceof Character) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Character)paramObject2).charValue());
      return;
    } 
    if (paramObject2 instanceof Integer) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Integer)paramObject2).intValue());
      return;
    } 
    if (paramObject2 instanceof Long) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Long)paramObject2).longValue());
      return;
    } 
    if (paramObject2 instanceof Float) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Float)paramObject2).floatValue());
      return;
    } 
    if (paramObject2 instanceof Double) {
      unsafe.putDouble(paramObject1, this.fieldOffset, ((Double)paramObject2).doubleValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException { throwSetIllegalArgumentException(paramBoolean); }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException { setDouble(paramObject, paramByte); }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException { setDouble(paramObject, paramChar); }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException { setDouble(paramObject, paramShort); }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException { setDouble(paramObject, paramInt); }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException { setDouble(paramObject, paramLong); }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException { setDouble(paramObject, paramFloat); }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(paramObject);
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramDouble); 
    unsafe.putDouble(paramObject, this.fieldOffset, paramDouble);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeDoubleFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */