package sun.reflect;

public interface FieldAccessor {
  Object get(Object paramObject) throws IllegalArgumentException;
  
  boolean getBoolean(Object paramObject) throws IllegalArgumentException;
  
  byte getByte(Object paramObject) throws IllegalArgumentException;
  
  char getChar(Object paramObject) throws IllegalArgumentException;
  
  short getShort(Object paramObject) throws IllegalArgumentException;
  
  int getInt(Object paramObject) throws IllegalArgumentException;
  
  long getLong(Object paramObject) throws IllegalArgumentException;
  
  float getFloat(Object paramObject) throws IllegalArgumentException;
  
  double getDouble(Object paramObject) throws IllegalArgumentException;
  
  void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException;
  
  void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException;
  
  void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException;
  
  void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException;
  
  void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException;
  
  void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException;
  
  void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException;
  
  void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException;
  
  void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\FieldAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */