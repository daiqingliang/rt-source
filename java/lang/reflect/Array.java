package java.lang.reflect;

import java.lang.reflect.Array;

public final class Array {
  public static Object newInstance(Class<?> paramClass, int paramInt) throws NegativeArraySizeException { return newArray(paramClass, paramInt); }
  
  public static Object newInstance(Class<?> paramClass, int... paramVarArgs) throws IllegalArgumentException, NegativeArraySizeException { return multiNewArray(paramClass, paramVarArgs); }
  
  public static native int getLength(Object paramObject) throws IllegalArgumentException;
  
  public static native Object get(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native boolean getBoolean(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native byte getByte(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native char getChar(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native short getShort(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native int getInt(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native long getLong(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native float getFloat(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native double getDouble(Object paramObject, int paramInt) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void set(Object paramObject1, int paramInt, Object paramObject2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setBoolean(Object paramObject, int paramInt, boolean paramBoolean) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setByte(Object paramObject, int paramInt, byte paramByte) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setChar(Object paramObject, int paramInt, char paramChar) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setShort(Object paramObject, int paramInt, short paramShort) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setInt(Object paramObject, int paramInt1, int paramInt2) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setLong(Object paramObject, int paramInt, long paramLong) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setFloat(Object paramObject, int paramInt, float paramFloat) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  public static native void setDouble(Object paramObject, int paramInt, double paramDouble) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
  
  private static native Object newArray(Class<?> paramClass, int paramInt) throws NegativeArraySizeException;
  
  private static native Object multiNewArray(Class<?> paramClass, int[] paramArrayOfInt) throws IllegalArgumentException, NegativeArraySizeException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Array.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */