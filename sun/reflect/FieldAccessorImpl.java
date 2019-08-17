package sun.reflect;

abstract class FieldAccessorImpl extends MagicAccessorImpl implements FieldAccessor {
  public abstract Object get(Object paramObject) throws IllegalArgumentException;
  
  public abstract boolean getBoolean(Object paramObject) throws IllegalArgumentException;
  
  public abstract byte getByte(Object paramObject) throws IllegalArgumentException;
  
  public abstract char getChar(Object paramObject) throws IllegalArgumentException;
  
  public abstract short getShort(Object paramObject) throws IllegalArgumentException;
  
  public abstract int getInt(Object paramObject) throws IllegalArgumentException;
  
  public abstract long getLong(Object paramObject) throws IllegalArgumentException;
  
  public abstract float getFloat(Object paramObject) throws IllegalArgumentException;
  
  public abstract double getDouble(Object paramObject) throws IllegalArgumentException;
  
  public abstract void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException;
  
  public abstract void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\FieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */