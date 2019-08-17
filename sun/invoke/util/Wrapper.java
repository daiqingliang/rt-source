package sun.invoke.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public static enum Wrapper {
  BOOLEAN(Boolean.class, boolean.class, 'Z', Boolean.valueOf(false), new boolean[0], Format.unsigned(1)),
  BYTE(Byte.class, byte.class, 'B', Byte.valueOf((byte)0), new byte[0], Format.signed(8)),
  SHORT(Short.class, short.class, 'S', Short.valueOf((short)0), new short[0], Format.signed(16)),
  CHAR(Character.class, char.class, 'C', Character.valueOf(false), new char[0], Format.unsigned(16)),
  INT(Integer.class, int.class, 'I', Integer.valueOf(0), new int[0], Format.signed(32)),
  LONG(Long.class, long.class, 'J', Long.valueOf(0L), new long[0], Format.signed(64)),
  FLOAT(Float.class, float.class, 'F', Float.valueOf(0.0F), new float[0], Format.floating(32)),
  DOUBLE(Double.class, double.class, 'D', Double.valueOf(0.0D), new double[0], Format.floating(64)),
  OBJECT(Object.class, Object.class, 'L', null, new Object[0], Format.other(1)),
  VOID(Void.class, void.class, 'V', null, null, Format.other(0));
  
  private final Class<?> wrapperType;
  
  private final Class<?> primitiveType;
  
  private final char basicTypeChar;
  
  private final Object zero;
  
  private final Object emptyArray;
  
  private final int format;
  
  private final String wrapperSimpleName;
  
  private final String primitiveSimpleName;
  
  private static final Wrapper[] FROM_PRIM;
  
  private static final Wrapper[] FROM_WRAP;
  
  private static final Wrapper[] FROM_CHAR;
  
  Wrapper(Class<?> paramClass1, char paramChar, Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) {
    this.wrapperType = paramClass1;
    this.primitiveType = paramChar;
    this.basicTypeChar = paramObject1;
    this.zero = paramObject2;
    this.emptyArray = paramInt1;
    this.format = paramInt2;
    this.wrapperSimpleName = paramClass1.getSimpleName();
    this.primitiveSimpleName = paramChar.getSimpleName();
  }
  
  public String detailString() { return this.wrapperSimpleName + Arrays.asList(new Object[] { this.wrapperType, this.primitiveType, Character.valueOf(this.basicTypeChar), this.zero, "0x" + Integer.toHexString(this.format) }); }
  
  public int bitWidth() { return this.format >> 2 & 0x3FF; }
  
  public int stackSlots() { return this.format >> 0 & 0x3; }
  
  public boolean isSingleWord() { return ((this.format & true) != 0); }
  
  public boolean isDoubleWord() { return ((this.format & 0x2) != 0); }
  
  public boolean isNumeric() { return ((this.format & 0xFFFFFFFC) != 0); }
  
  public boolean isIntegral() { return (isNumeric() && this.format < 4225); }
  
  public boolean isSubwordOrInt() { return (isIntegral() && isSingleWord()); }
  
  public boolean isSigned() { return (this.format < 0); }
  
  public boolean isUnsigned() { return (this.format >= 5 && this.format < 4225); }
  
  public boolean isFloating() { return (this.format >= 4225); }
  
  public boolean isOther() { return ((this.format & 0xFFFFFFFC) == 0); }
  
  public boolean isConvertibleFrom(Wrapper paramWrapper) {
    if (this == paramWrapper)
      return true; 
    if (compareTo(paramWrapper) < 0)
      return false; 
    boolean bool = ((this.format & paramWrapper.format & 0xFFFFF000) != 0) ? 1 : 0;
    if (!bool)
      return isOther() ? true : ((paramWrapper.format == 65)); 
    assert isFloating() || isSigned();
    assert paramWrapper.isFloating() || paramWrapper.isSigned();
    return true;
  }
  
  private static boolean checkConvertibleFrom() {
    for (Wrapper wrapper : values()) {
      assert wrapper.isConvertibleFrom(wrapper);
      assert VOID.isConvertibleFrom(wrapper);
      if (wrapper != VOID) {
        assert OBJECT.isConvertibleFrom(wrapper);
        assert !wrapper.isConvertibleFrom(VOID);
      } 
      if (wrapper != CHAR) {
        assert !CHAR.isConvertibleFrom(wrapper);
        if (!wrapper.isConvertibleFrom(INT) && !$assertionsDisabled && wrapper.isConvertibleFrom(CHAR))
          throw new AssertionError(); 
      } 
      if (wrapper != BOOLEAN) {
        assert !BOOLEAN.isConvertibleFrom(wrapper);
        if (wrapper != VOID && wrapper != OBJECT && !$assertionsDisabled && wrapper.isConvertibleFrom(BOOLEAN))
          throw new AssertionError(); 
      } 
      if (wrapper.isSigned())
        for (Wrapper wrapper1 : values()) {
          if (wrapper != wrapper1)
            if (wrapper1.isFloating()) {
              assert !wrapper.isConvertibleFrom(wrapper1);
            } else if (wrapper1.isSigned()) {
              if (wrapper.compareTo(wrapper1) < 0) {
                assert !wrapper.isConvertibleFrom(wrapper1);
              } else {
                assert wrapper.isConvertibleFrom(wrapper1);
              } 
            }  
        }  
      if (wrapper.isFloating())
        for (Wrapper wrapper1 : values()) {
          if (wrapper != wrapper1)
            if (wrapper1.isSigned()) {
              assert wrapper.isConvertibleFrom(wrapper1);
            } else if (wrapper1.isFloating()) {
              if (wrapper.compareTo(wrapper1) < 0) {
                assert !wrapper.isConvertibleFrom(wrapper1);
              } else {
                assert wrapper.isConvertibleFrom(wrapper1);
              } 
            }  
        }  
    } 
    return true;
  }
  
  public Object zero() { return this.zero; }
  
  public <T> T zero(Class<T> paramClass) { return (T)convert(this.zero, paramClass); }
  
  public static Wrapper forPrimitiveType(Class<?> paramClass) {
    Wrapper wrapper = findPrimitiveType(paramClass);
    if (wrapper != null)
      return wrapper; 
    if (paramClass.isPrimitive())
      throw new InternalError(); 
    throw newIllegalArgumentException("not primitive: " + paramClass);
  }
  
  static Wrapper findPrimitiveType(Class<?> paramClass) {
    Wrapper wrapper = FROM_PRIM[hashPrim(paramClass)];
    return (wrapper != null && wrapper.primitiveType == paramClass) ? wrapper : null;
  }
  
  public static Wrapper forWrapperType(Class<?> paramClass) {
    Wrapper wrapper = findWrapperType(paramClass);
    if (wrapper != null)
      return wrapper; 
    for (Wrapper wrapper1 : values()) {
      if (wrapper1.wrapperType == paramClass)
        throw new InternalError(); 
    } 
    throw newIllegalArgumentException("not wrapper: " + paramClass);
  }
  
  static Wrapper findWrapperType(Class<?> paramClass) {
    Wrapper wrapper = FROM_WRAP[hashWrap(paramClass)];
    return (wrapper != null && wrapper.wrapperType == paramClass) ? wrapper : null;
  }
  
  public static Wrapper forBasicType(char paramChar) {
    Wrapper wrapper = FROM_CHAR[hashChar(paramChar)];
    if (wrapper != null && wrapper.basicTypeChar == paramChar)
      return wrapper; 
    for (Wrapper wrapper1 : values()) {
      if (wrapper.basicTypeChar == paramChar)
        throw new InternalError(); 
    } 
    throw newIllegalArgumentException("not basic type char: " + paramChar);
  }
  
  public static Wrapper forBasicType(Class<?> paramClass) { return paramClass.isPrimitive() ? forPrimitiveType(paramClass) : OBJECT; }
  
  private static int hashPrim(Class<?> paramClass) {
    String str = paramClass.getName();
    return (str.length() < 3) ? 0 : ((str.charAt(0) + str.charAt(2)) % '\020');
  }
  
  private static int hashWrap(Class<?> paramClass) {
    String str = paramClass.getName();
    assert 10 == "java.lang.".length();
    return (str.length() < 13) ? 0 : (('\003' * str.charAt(11) + str.charAt(12)) % '\020');
  }
  
  private static int hashChar(char paramChar) { return (paramChar + (paramChar >> '\001')) % '\020'; }
  
  public Class<?> primitiveType() { return this.primitiveType; }
  
  public Class<?> wrapperType() { return this.wrapperType; }
  
  public <T> Class<T> wrapperType(Class<T> paramClass) {
    if (paramClass == this.wrapperType)
      return paramClass; 
    if (paramClass == this.primitiveType || this.wrapperType == Object.class || paramClass.isInterface())
      return forceType(this.wrapperType, paramClass); 
    throw newClassCastException(paramClass, this.primitiveType);
  }
  
  private static ClassCastException newClassCastException(Class<?> paramClass1, Class<?> paramClass2) { return new ClassCastException(paramClass1 + " is not compatible with " + paramClass2); }
  
  public static <T> Class<T> asWrapperType(Class<T> paramClass) { return paramClass.isPrimitive() ? forPrimitiveType(paramClass).wrapperType(paramClass) : paramClass; }
  
  public static <T> Class<T> asPrimitiveType(Class<T> paramClass) {
    Wrapper wrapper = findWrapperType(paramClass);
    return (wrapper != null) ? forceType(wrapper.primitiveType(), paramClass) : paramClass;
  }
  
  public static boolean isWrapperType(Class<?> paramClass) { return (findWrapperType(paramClass) != null); }
  
  public static boolean isPrimitiveType(Class<?> paramClass) { return paramClass.isPrimitive(); }
  
  public static char basicTypeChar(Class<?> paramClass) { return !paramClass.isPrimitive() ? 'L' : forPrimitiveType(paramClass).basicTypeChar(); }
  
  public char basicTypeChar() { return this.basicTypeChar; }
  
  public String wrapperSimpleName() { return this.wrapperSimpleName; }
  
  public String primitiveSimpleName() { return this.primitiveSimpleName; }
  
  public <T> T cast(Object paramObject, Class<T> paramClass) { return (T)convert(paramObject, paramClass, true); }
  
  public <T> T convert(Object paramObject, Class<T> paramClass) { return (T)convert(paramObject, paramClass, false); }
  
  private <T> T convert(Object paramObject, Class<T> paramClass, boolean paramBoolean) {
    if (this == OBJECT) {
      assert !paramClass.isPrimitive();
      if (!paramClass.isInterface())
        paramClass.cast(paramObject); 
      return (T)paramObject;
    } 
    Class clazz = wrapperType(paramClass);
    if (clazz.isInstance(paramObject))
      return (T)clazz.cast(paramObject); 
    if (!paramBoolean) {
      Class clazz1 = paramObject.getClass();
      Wrapper wrapper = findWrapperType(clazz1);
      if (wrapper == null || !isConvertibleFrom(wrapper))
        throw newClassCastException(clazz, clazz1); 
    } else if (paramObject == null) {
      return (T)this.zero;
    } 
    Object object = wrap(paramObject);
    assert ((object == null) ? Void.class : object.getClass()) == clazz;
    return (T)object;
  }
  
  static <T> Class<T> forceType(Class<?> paramClass1, Class<T> paramClass2) {
    boolean bool = (paramClass1 == paramClass2 || (paramClass1.isPrimitive() && forPrimitiveType(paramClass1) == findWrapperType(paramClass2)) || (paramClass2.isPrimitive() && forPrimitiveType(paramClass2) == findWrapperType(paramClass1)) || (paramClass1 == Object.class && !paramClass2.isPrimitive())) ? 1 : 0;
    if (!bool)
      System.out.println(paramClass1 + " <= " + paramClass2); 
    assert (paramClass2.isPrimitive() && forPrimitiveType(paramClass2) == findWrapperType(paramClass1)) || (paramClass1 == Object.class && !paramClass2.isPrimitive());
    return paramClass1;
  }
  
  public Object wrap(Object paramObject) {
    switch (this.basicTypeChar) {
      case 'L':
        return paramObject;
      case 'V':
        return null;
    } 
    Number number = numberValue(paramObject);
    switch (this.basicTypeChar) {
      case 'I':
        return Integer.valueOf(number.intValue());
      case 'J':
        return Long.valueOf(number.longValue());
      case 'F':
        return Float.valueOf(number.floatValue());
      case 'D':
        return Double.valueOf(number.doubleValue());
      case 'S':
        return Short.valueOf((short)number.intValue());
      case 'B':
        return Byte.valueOf((byte)number.intValue());
      case 'C':
        return Character.valueOf((char)number.intValue());
      case 'Z':
        return Boolean.valueOf(boolValue(number.byteValue()));
    } 
    throw new InternalError("bad wrapper");
  }
  
  public Object wrap(int paramInt) {
    if (this.basicTypeChar == 'L')
      return Integer.valueOf(paramInt); 
    switch (this.basicTypeChar) {
      case 'L':
        throw newIllegalArgumentException("cannot wrap to object type");
      case 'V':
        return null;
      case 'I':
        return Integer.valueOf(paramInt);
      case 'J':
        return Long.valueOf(paramInt);
      case 'F':
        return Float.valueOf(paramInt);
      case 'D':
        return Double.valueOf(paramInt);
      case 'S':
        return Short.valueOf((short)paramInt);
      case 'B':
        return Byte.valueOf((byte)paramInt);
      case 'C':
        return Character.valueOf((char)paramInt);
      case 'Z':
        return Boolean.valueOf(boolValue((byte)paramInt));
    } 
    throw new InternalError("bad wrapper");
  }
  
  private static Number numberValue(Object paramObject) { return (paramObject instanceof Number) ? (Number)paramObject : ((paramObject instanceof Character) ? Integer.valueOf(((Character)paramObject).charValue()) : ((paramObject instanceof Boolean) ? Integer.valueOf(((Boolean)paramObject).booleanValue() ? 1 : 0) : (Number)paramObject)); }
  
  private static boolean boolValue(byte paramByte) {
    paramByte = (byte)(paramByte & true);
    return (paramByte != 0);
  }
  
  private static RuntimeException newIllegalArgumentException(String paramString, Object paramObject) { return newIllegalArgumentException(paramString + paramObject); }
  
  private static RuntimeException newIllegalArgumentException(String paramString) { return new IllegalArgumentException(paramString); }
  
  public Object makeArray(int paramInt) { return Array.newInstance(this.primitiveType, paramInt); }
  
  public Class<?> arrayType() { return this.emptyArray.getClass(); }
  
  public void copyArrayUnboxing(Object[] paramArrayOfObject, int paramInt1, Object paramObject, int paramInt2, int paramInt3) {
    if (paramObject.getClass() != arrayType())
      arrayType().cast(paramObject); 
    for (int i = 0; i < paramInt3; i++) {
      Object object = paramArrayOfObject[i + paramInt1];
      object = convert(object, this.primitiveType);
      Array.set(paramObject, i + paramInt2, object);
    } 
  }
  
  public void copyArrayBoxing(Object paramObject, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3) {
    if (paramObject.getClass() != arrayType())
      arrayType().cast(paramObject); 
    for (int i = 0; i < paramInt3; i++) {
      Object object = Array.get(paramObject, i + paramInt1);
      assert object.getClass() == this.wrapperType;
      paramArrayOfObject[i + paramInt2] = object;
    } 
  }
  
  static  {
    assert checkConvertibleFrom();
    FROM_PRIM = new Wrapper[16];
    FROM_WRAP = new Wrapper[16];
    FROM_CHAR = new Wrapper[16];
    Wrapper[] arrayOfWrapper = values();
    int i = arrayOfWrapper.length;
    for (byte b = 0; b < i; b++) {
      Wrapper wrapper;
      int j = (wrapper = arrayOfWrapper[b]).hashPrim(wrapper.primitiveType);
      int k = hashWrap(wrapper.wrapperType);
      int m = hashChar(wrapper.basicTypeChar);
      assert FROM_PRIM[j] == null;
      assert FROM_WRAP[k] == null;
      assert FROM_CHAR[m] == null;
      FROM_PRIM[j] = wrapper;
      FROM_WRAP[k] = wrapper;
      FROM_CHAR[m] = wrapper;
    } 
  }
  
  private static abstract class Format {
    static final int SLOT_SHIFT = 0;
    
    static final int SIZE_SHIFT = 2;
    
    static final int KIND_SHIFT = 12;
    
    static final int SIGNED = -4096;
    
    static final int UNSIGNED = 0;
    
    static final int FLOATING = 4096;
    
    static final int SLOT_MASK = 3;
    
    static final int SIZE_MASK = 1023;
    
    static final int INT = -3967;
    
    static final int SHORT = -4031;
    
    static final int BOOLEAN = 5;
    
    static final int CHAR = 65;
    
    static final int FLOAT = 4225;
    
    static final int VOID = 0;
    
    static final int NUM_MASK = -4;
    
    static int format(int param1Int1, int param1Int2, int param1Int3) {
      assert param1Int1 >> 12 << 12 == param1Int1;
      assert (param1Int2 & param1Int2 - 1) == 0;
      assert false;
      throw new AssertionError();
    }
    
    static int signed(int param1Int) { return format(-4096, param1Int, (param1Int > 32) ? 2 : 1); }
    
    static int unsigned(int param1Int) { return format(0, param1Int, (param1Int > 32) ? 2 : 1); }
    
    static int floating(int param1Int) { return format(4096, param1Int, (param1Int > 32) ? 2 : 1); }
    
    static int other(int param1Int) { return param1Int << 0; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\invok\\util\Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */