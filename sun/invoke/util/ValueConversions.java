package sun.invoke.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.EnumMap;

public class ValueConversions {
  private static final Class<?> THIS_CLASS = ValueConversions.class;
  
  private static final MethodHandles.Lookup IMPL_LOOKUP = MethodHandles.lookup();
  
  private static final WrapperCache[] UNBOX_CONVERSIONS = newWrapperCaches(4);
  
  private static final Integer ZERO_INT;
  
  private static final Integer ONE_INT = (ZERO_INT = Integer.valueOf(0)).valueOf(1);
  
  private static final WrapperCache[] BOX_CONVERSIONS = newWrapperCaches(1);
  
  private static final WrapperCache[] CONSTANT_FUNCTIONS = newWrapperCaches(2);
  
  private static final MethodHandle CAST_REFERENCE;
  
  private static final MethodHandle IGNORE;
  
  private static final MethodHandle EMPTY;
  
  private static final WrapperCache[] CONVERT_PRIMITIVE_FUNCTIONS;
  
  private static WrapperCache[] newWrapperCaches(int paramInt) {
    WrapperCache[] arrayOfWrapperCache = new WrapperCache[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfWrapperCache[b] = new WrapperCache(null); 
    return arrayOfWrapperCache;
  }
  
  static int unboxInteger(Integer paramInteger) { return paramInteger.intValue(); }
  
  static int unboxInteger(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Integer) ? ((Integer)paramObject).intValue() : primitiveConversion(Wrapper.INT, paramObject, paramBoolean).intValue(); }
  
  static byte unboxByte(Byte paramByte) { return paramByte.byteValue(); }
  
  static byte unboxByte(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Byte) ? ((Byte)paramObject).byteValue() : primitiveConversion(Wrapper.BYTE, paramObject, paramBoolean).byteValue(); }
  
  static short unboxShort(Short paramShort) { return paramShort.shortValue(); }
  
  static short unboxShort(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Short) ? ((Short)paramObject).shortValue() : primitiveConversion(Wrapper.SHORT, paramObject, paramBoolean).shortValue(); }
  
  static boolean unboxBoolean(Boolean paramBoolean) { return paramBoolean.booleanValue(); }
  
  static boolean unboxBoolean(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Boolean) ? ((Boolean)paramObject).booleanValue() : (((primitiveConversion(Wrapper.BOOLEAN, paramObject, paramBoolean).intValue() & true) != 0) ? 1 : 0); }
  
  static char unboxCharacter(Character paramCharacter) { return paramCharacter.charValue(); }
  
  static char unboxCharacter(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Character) ? ((Character)paramObject).charValue() : (char)primitiveConversion(Wrapper.CHAR, paramObject, paramBoolean).intValue(); }
  
  static long unboxLong(Long paramLong) { return paramLong.longValue(); }
  
  static long unboxLong(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Long) ? ((Long)paramObject).longValue() : primitiveConversion(Wrapper.LONG, paramObject, paramBoolean).longValue(); }
  
  static float unboxFloat(Float paramFloat) { return paramFloat.floatValue(); }
  
  static float unboxFloat(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Float) ? ((Float)paramObject).floatValue() : primitiveConversion(Wrapper.FLOAT, paramObject, paramBoolean).floatValue(); }
  
  static double unboxDouble(Double paramDouble) { return paramDouble.doubleValue(); }
  
  static double unboxDouble(Object paramObject, boolean paramBoolean) { return (paramObject instanceof Double) ? ((Double)paramObject).doubleValue() : primitiveConversion(Wrapper.DOUBLE, paramObject, paramBoolean).doubleValue(); }
  
  private static MethodType unboxType(Wrapper paramWrapper, int paramInt) { return (paramInt == 0) ? MethodType.methodType(paramWrapper.primitiveType(), paramWrapper.wrapperType()) : MethodType.methodType(paramWrapper.primitiveType(), Object.class, new Class[] { boolean.class }); }
  
  private static MethodHandle unbox(Wrapper paramWrapper, int paramInt) {
    WrapperCache wrapperCache = UNBOX_CONVERSIONS[paramInt];
    MethodHandle methodHandle = wrapperCache.get(paramWrapper);
    if (methodHandle != null)
      return methodHandle; 
    switch (paramWrapper) {
      case OBJECT:
      case VOID:
        throw new IllegalArgumentException("unbox " + paramWrapper);
    } 
    String str = "unbox" + paramWrapper.wrapperSimpleName();
    MethodType methodType = unboxType(paramWrapper, paramInt);
    try {
      methodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, methodType);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      methodHandle = null;
    } 
    if (methodHandle != null) {
      if (paramInt > 0) {
        boolean bool = (paramInt != 2);
        methodHandle = MethodHandles.insertArguments(methodHandle, 1, new Object[] { Boolean.valueOf(bool) });
      } 
      if (paramInt == 1)
        methodHandle = methodHandle.asType(unboxType(paramWrapper, 0)); 
      return wrapperCache.put(paramWrapper, methodHandle);
    } 
    throw new IllegalArgumentException("cannot find unbox adapter for " + paramWrapper + ((paramInt <= 1) ? " (exact)" : ((paramInt == 3) ? " (cast)" : "")));
  }
  
  public static MethodHandle unboxExact(Wrapper paramWrapper) { return unbox(paramWrapper, 0); }
  
  public static MethodHandle unboxExact(Wrapper paramWrapper, boolean paramBoolean) { return unbox(paramWrapper, paramBoolean ? 0 : 1); }
  
  public static MethodHandle unboxWiden(Wrapper paramWrapper) { return unbox(paramWrapper, 2); }
  
  public static MethodHandle unboxCast(Wrapper paramWrapper) { return unbox(paramWrapper, 3); }
  
  public static Number primitiveConversion(Wrapper paramWrapper, Object paramObject, boolean paramBoolean) {
    Number number;
    if (paramObject == null)
      return !paramBoolean ? null : ZERO_INT; 
    if (paramObject instanceof Number) {
      number = (Number)paramObject;
    } else if (paramObject instanceof Boolean) {
      number = ((Boolean)paramObject).booleanValue() ? ONE_INT : ZERO_INT;
    } else if (paramObject instanceof Character) {
      number = Integer.valueOf(((Character)paramObject).charValue());
    } else {
      number = (Number)paramObject;
    } 
    Wrapper wrapper = Wrapper.findWrapperType(paramObject.getClass());
    return (wrapper == null || (!paramBoolean && !paramWrapper.isConvertibleFrom(wrapper))) ? (Number)paramWrapper.wrapperType().cast(paramObject) : number;
  }
  
  public static int widenSubword(Object paramObject) { return (paramObject instanceof Integer) ? ((Integer)paramObject).intValue() : ((paramObject instanceof Boolean) ? fromBoolean(((Boolean)paramObject).booleanValue()) : ((paramObject instanceof Character) ? ((Character)paramObject).charValue() : ((paramObject instanceof Short) ? ((Short)paramObject).shortValue() : ((paramObject instanceof Byte) ? ((Byte)paramObject).byteValue() : ((Integer)paramObject).intValue())))); }
  
  static Integer boxInteger(int paramInt) { return Integer.valueOf(paramInt); }
  
  static Byte boxByte(byte paramByte) { return Byte.valueOf(paramByte); }
  
  static Short boxShort(short paramShort) { return Short.valueOf(paramShort); }
  
  static Boolean boxBoolean(boolean paramBoolean) { return Boolean.valueOf(paramBoolean); }
  
  static Character boxCharacter(char paramChar) { return Character.valueOf(paramChar); }
  
  static Long boxLong(long paramLong) { return Long.valueOf(paramLong); }
  
  static Float boxFloat(float paramFloat) { return Float.valueOf(paramFloat); }
  
  static Double boxDouble(double paramDouble) { return Double.valueOf(paramDouble); }
  
  private static MethodType boxType(Wrapper paramWrapper) {
    Class clazz = paramWrapper.wrapperType();
    return MethodType.methodType(clazz, paramWrapper.primitiveType());
  }
  
  public static MethodHandle boxExact(Wrapper paramWrapper) {
    WrapperCache wrapperCache = BOX_CONVERSIONS[0];
    MethodHandle methodHandle = wrapperCache.get(paramWrapper);
    if (methodHandle != null)
      return methodHandle; 
    String str = "box" + paramWrapper.wrapperSimpleName();
    MethodType methodType = boxType(paramWrapper);
    try {
      methodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, methodType);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      methodHandle = null;
    } 
    if (methodHandle != null)
      return wrapperCache.put(paramWrapper, methodHandle); 
    throw new IllegalArgumentException("cannot find box adapter for " + paramWrapper);
  }
  
  static void ignore(Object paramObject) {}
  
  static void empty() {}
  
  static Object zeroObject() { return null; }
  
  static int zeroInteger() { return 0; }
  
  static long zeroLong() { return 0L; }
  
  static float zeroFloat() { return 0.0F; }
  
  static double zeroDouble() { return 0.0D; }
  
  public static MethodHandle zeroConstantFunction(Wrapper paramWrapper) {
    WrapperCache wrapperCache = CONSTANT_FUNCTIONS[0];
    MethodHandle methodHandle = wrapperCache.get(paramWrapper);
    if (methodHandle != null)
      return methodHandle; 
    MethodType methodType = MethodType.methodType(paramWrapper.primitiveType());
    switch (paramWrapper) {
      case VOID:
        methodHandle = EMPTY;
        break;
      case OBJECT:
      case INT:
      case LONG:
      case FLOAT:
      case DOUBLE:
        try {
          methodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, "zero" + paramWrapper.wrapperSimpleName(), methodType);
        } catch (ReflectiveOperationException reflectiveOperationException) {
          methodHandle = null;
        } 
        break;
    } 
    if (methodHandle != null)
      return wrapperCache.put(paramWrapper, methodHandle); 
    if (paramWrapper.isSubwordOrInt() && paramWrapper != Wrapper.INT) {
      methodHandle = MethodHandles.explicitCastArguments(zeroConstantFunction(Wrapper.INT), methodType);
      return wrapperCache.put(paramWrapper, methodHandle);
    } 
    throw new IllegalArgumentException("cannot find zero constant for " + paramWrapper);
  }
  
  public static MethodHandle ignore() { return IGNORE; }
  
  public static MethodHandle cast() { return CAST_REFERENCE; }
  
  static float doubleToFloat(double paramDouble) { return (float)paramDouble; }
  
  static long doubleToLong(double paramDouble) { return (long)paramDouble; }
  
  static int doubleToInt(double paramDouble) { return (int)paramDouble; }
  
  static short doubleToShort(double paramDouble) { return (short)(int)paramDouble; }
  
  static char doubleToChar(double paramDouble) { return (char)(int)paramDouble; }
  
  static byte doubleToByte(double paramDouble) { return (byte)(int)paramDouble; }
  
  static boolean doubleToBoolean(double paramDouble) { return toBoolean((byte)(int)paramDouble); }
  
  static double floatToDouble(float paramFloat) { return paramFloat; }
  
  static long floatToLong(float paramFloat) { return (long)paramFloat; }
  
  static int floatToInt(float paramFloat) { return (int)paramFloat; }
  
  static short floatToShort(float paramFloat) { return (short)(int)paramFloat; }
  
  static char floatToChar(float paramFloat) { return (char)(int)paramFloat; }
  
  static byte floatToByte(float paramFloat) { return (byte)(int)paramFloat; }
  
  static boolean floatToBoolean(float paramFloat) { return toBoolean((byte)(int)paramFloat); }
  
  static double longToDouble(long paramLong) { return paramLong; }
  
  static float longToFloat(long paramLong) { return (float)paramLong; }
  
  static int longToInt(long paramLong) { return (int)paramLong; }
  
  static short longToShort(long paramLong) { return (short)(int)paramLong; }
  
  static char longToChar(long paramLong) { return (char)(int)paramLong; }
  
  static byte longToByte(long paramLong) { return (byte)(int)paramLong; }
  
  static boolean longToBoolean(long paramLong) { return toBoolean((byte)(int)paramLong); }
  
  static double intToDouble(int paramInt) { return paramInt; }
  
  static float intToFloat(int paramInt) { return paramInt; }
  
  static long intToLong(int paramInt) { return paramInt; }
  
  static short intToShort(int paramInt) { return (short)paramInt; }
  
  static char intToChar(int paramInt) { return (char)paramInt; }
  
  static byte intToByte(int paramInt) { return (byte)paramInt; }
  
  static boolean intToBoolean(int paramInt) { return toBoolean((byte)paramInt); }
  
  static double shortToDouble(short paramShort) { return paramShort; }
  
  static float shortToFloat(short paramShort) { return paramShort; }
  
  static long shortToLong(short paramShort) { return paramShort; }
  
  static int shortToInt(short paramShort) { return paramShort; }
  
  static char shortToChar(short paramShort) { return (char)paramShort; }
  
  static byte shortToByte(short paramShort) { return (byte)paramShort; }
  
  static boolean shortToBoolean(short paramShort) { return toBoolean((byte)paramShort); }
  
  static double charToDouble(char paramChar) { return paramChar; }
  
  static float charToFloat(char paramChar) { return paramChar; }
  
  static long charToLong(char paramChar) { return paramChar; }
  
  static int charToInt(char paramChar) { return paramChar; }
  
  static short charToShort(char paramChar) { return (short)paramChar; }
  
  static byte charToByte(char paramChar) { return (byte)paramChar; }
  
  static boolean charToBoolean(char paramChar) { return toBoolean((byte)paramChar); }
  
  static double byteToDouble(byte paramByte) { return paramByte; }
  
  static float byteToFloat(byte paramByte) { return paramByte; }
  
  static long byteToLong(byte paramByte) { return paramByte; }
  
  static int byteToInt(byte paramByte) { return paramByte; }
  
  static short byteToShort(byte paramByte) { return (short)paramByte; }
  
  static char byteToChar(byte paramByte) { return (char)paramByte; }
  
  static boolean byteToBoolean(byte paramByte) { return toBoolean(paramByte); }
  
  static double booleanToDouble(boolean paramBoolean) { return fromBoolean(paramBoolean); }
  
  static float booleanToFloat(boolean paramBoolean) { return fromBoolean(paramBoolean); }
  
  static long booleanToLong(boolean paramBoolean) { return fromBoolean(paramBoolean); }
  
  static int booleanToInt(boolean paramBoolean) { return fromBoolean(paramBoolean); }
  
  static short booleanToShort(boolean paramBoolean) { return (short)fromBoolean(paramBoolean); }
  
  static char booleanToChar(boolean paramBoolean) { return (char)fromBoolean(paramBoolean); }
  
  static byte booleanToByte(boolean paramBoolean) { return fromBoolean(paramBoolean); }
  
  static boolean toBoolean(byte paramByte) { return ((paramByte & true) != 0); }
  
  static byte fromBoolean(boolean paramBoolean) { return paramBoolean ? 1 : 0; }
  
  public static MethodHandle convertPrimitive(Wrapper paramWrapper1, Wrapper paramWrapper2) {
    WrapperCache wrapperCache = CONVERT_PRIMITIVE_FUNCTIONS[paramWrapper1.ordinal()];
    MethodHandle methodHandle = wrapperCache.get(paramWrapper2);
    if (methodHandle != null)
      return methodHandle; 
    Class clazz1 = paramWrapper1.primitiveType();
    Class clazz2 = paramWrapper2.primitiveType();
    MethodType methodType = MethodType.methodType(clazz2, clazz1);
    if (paramWrapper1 == paramWrapper2) {
      methodHandle = MethodHandles.identity(clazz1);
    } else {
      assert clazz1.isPrimitive() && clazz2.isPrimitive();
      try {
        methodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, clazz1.getSimpleName() + "To" + capitalize(clazz2.getSimpleName()), methodType);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        methodHandle = null;
      } 
    } 
    if (methodHandle != null) {
      assert methodHandle.type() == methodType : methodHandle;
      return wrapperCache.put(paramWrapper2, methodHandle);
    } 
    throw new IllegalArgumentException("cannot find primitive conversion function for " + clazz1.getSimpleName() + " -> " + clazz2.getSimpleName());
  }
  
  public static MethodHandle convertPrimitive(Class<?> paramClass1, Class<?> paramClass2) { return convertPrimitive(Wrapper.forPrimitiveType(paramClass1), Wrapper.forPrimitiveType(paramClass2)); }
  
  private static String capitalize(String paramString) { return Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1); }
  
  private static InternalError newInternalError(String paramString, Throwable paramThrowable) { return new InternalError(paramString, paramThrowable); }
  
  private static InternalError newInternalError(Throwable paramThrowable) { return new InternalError(paramThrowable); }
  
  static  {
    try {
      MethodType methodType1 = MethodType.genericMethodType(1);
      MethodType methodType2 = methodType1.changeReturnType(void.class);
      CAST_REFERENCE = IMPL_LOOKUP.findVirtual(Class.class, "cast", methodType1);
      IGNORE = IMPL_LOOKUP.findStatic(THIS_CLASS, "ignore", methodType2);
      EMPTY = IMPL_LOOKUP.findStatic(THIS_CLASS, "empty", methodType2.dropParameterTypes(0, 1));
    } catch (NoSuchMethodException|IllegalAccessException noSuchMethodException) {
      throw newInternalError("uncaught exception", noSuchMethodException);
    } 
    CONVERT_PRIMITIVE_FUNCTIONS = newWrapperCaches(Wrapper.values().length);
  }
  
  private static class WrapperCache {
    private final EnumMap<Wrapper, MethodHandle> map = new EnumMap(Wrapper.class);
    
    private WrapperCache() {}
    
    public MethodHandle get(Wrapper param1Wrapper) { return (MethodHandle)this.map.get(param1Wrapper); }
    
    public MethodHandle put(Wrapper param1Wrapper, MethodHandle param1MethodHandle) {
      MethodHandle methodHandle = (MethodHandle)this.map.putIfAbsent(param1Wrapper, param1MethodHandle);
      return (methodHandle != null) ? methodHandle : param1MethodHandle;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\invok\\util\ValueConversions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */