package javax.management.openmbean;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;

public class ArrayType<T> extends OpenType<T> {
  static final long serialVersionUID = 720504429830309770L;
  
  private int dimension;
  
  private OpenType<?> elementType;
  
  private boolean primitiveArray;
  
  private Integer myHashCode = null;
  
  private String myToString = null;
  
  private static final int PRIMITIVE_WRAPPER_NAME_INDEX = 0;
  
  private static final int PRIMITIVE_TYPE_NAME_INDEX = 1;
  
  private static final int PRIMITIVE_TYPE_KEY_INDEX = 2;
  
  private static final int PRIMITIVE_OPEN_TYPE_INDEX = 3;
  
  private static final Object[][] PRIMITIVE_ARRAY_TYPES = { { Boolean.class.getName(), boolean.class.getName(), "Z", SimpleType.BOOLEAN }, { Character.class.getName(), char.class.getName(), "C", SimpleType.CHARACTER }, { Byte.class.getName(), byte.class.getName(), "B", SimpleType.BYTE }, { Short.class.getName(), short.class.getName(), "S", SimpleType.SHORT }, { Integer.class.getName(), int.class.getName(), "I", SimpleType.INTEGER }, { Long.class.getName(), long.class.getName(), "J", SimpleType.LONG }, { Float.class.getName(), float.class.getName(), "F", SimpleType.FLOAT }, { Double.class.getName(), double.class.getName(), "D", SimpleType.DOUBLE } };
  
  static boolean isPrimitiveContentType(String paramString) {
    for (Object[] arrayOfObject : PRIMITIVE_ARRAY_TYPES) {
      if (arrayOfObject[2].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  static String getPrimitiveTypeKey(String paramString) {
    for (Object[] arrayOfObject : PRIMITIVE_ARRAY_TYPES) {
      if (paramString.equals(arrayOfObject[0]))
        return (String)arrayOfObject[2]; 
    } 
    return null;
  }
  
  static String getPrimitiveTypeName(String paramString) {
    for (Object[] arrayOfObject : PRIMITIVE_ARRAY_TYPES) {
      if (paramString.equals(arrayOfObject[0]))
        return (String)arrayOfObject[1]; 
    } 
    return null;
  }
  
  static SimpleType<?> getPrimitiveOpenType(String paramString) {
    for (Object[] arrayOfObject : PRIMITIVE_ARRAY_TYPES) {
      if (paramString.equals(arrayOfObject[1]))
        return (SimpleType)arrayOfObject[3]; 
    } 
    return null;
  }
  
  public ArrayType(int paramInt, OpenType<?> paramOpenType) throws OpenDataException {
    super(buildArrayClassName(paramInt, paramOpenType), buildArrayClassName(paramInt, paramOpenType), buildArrayDescription(paramInt, paramOpenType));
    if (paramOpenType.isArray()) {
      ArrayType arrayType = (ArrayType)paramOpenType;
      this.dimension = arrayType.getDimension() + paramInt;
      this.elementType = arrayType.getElementOpenType();
      this.primitiveArray = arrayType.isPrimitiveArray();
    } else {
      this.dimension = paramInt;
      this.elementType = paramOpenType;
      this.primitiveArray = false;
    } 
  }
  
  public ArrayType(SimpleType<?> paramSimpleType, boolean paramBoolean) throws OpenDataException {
    super(buildArrayClassName(1, paramSimpleType, paramBoolean), buildArrayClassName(1, paramSimpleType, paramBoolean), buildArrayDescription(1, paramSimpleType, paramBoolean), true);
    this.dimension = 1;
    this.elementType = paramSimpleType;
    this.primitiveArray = paramBoolean;
  }
  
  ArrayType(String paramString1, String paramString2, String paramString3, int paramInt, OpenType<?> paramOpenType, boolean paramBoolean) {
    super(paramString1, paramString2, paramString3, true);
    this.dimension = paramInt;
    this.elementType = paramOpenType;
    this.primitiveArray = paramBoolean;
  }
  
  private static String buildArrayClassName(int paramInt, OpenType<?> paramOpenType) throws OpenDataException {
    boolean bool = false;
    if (paramOpenType.isArray())
      bool = ((ArrayType)paramOpenType).isPrimitiveArray(); 
    return buildArrayClassName(paramInt, paramOpenType, bool);
  }
  
  private static String buildArrayClassName(int paramInt, OpenType<?> paramOpenType, boolean paramBoolean) throws OpenDataException {
    if (paramInt < 1)
      throw new IllegalArgumentException("Value of argument dimension must be greater than 0"); 
    StringBuilder stringBuilder = new StringBuilder();
    String str = paramOpenType.getClassName();
    for (byte b = 1; b <= paramInt; b++)
      stringBuilder.append('['); 
    if (paramOpenType.isArray()) {
      stringBuilder.append(str);
    } else if (paramBoolean) {
      String str1 = getPrimitiveTypeKey(str);
      if (str1 == null)
        throw new OpenDataException("Element type is not primitive: " + str); 
      stringBuilder.append(str1);
    } else {
      stringBuilder.append("L");
      stringBuilder.append(str);
      stringBuilder.append(';');
    } 
    return stringBuilder.toString();
  }
  
  private static String buildArrayDescription(int paramInt, OpenType<?> paramOpenType) throws OpenDataException {
    boolean bool = false;
    if (paramOpenType.isArray())
      bool = ((ArrayType)paramOpenType).isPrimitiveArray(); 
    return buildArrayDescription(paramInt, paramOpenType, bool);
  }
  
  private static String buildArrayDescription(int paramInt, OpenType<?> paramOpenType, boolean paramBoolean) throws OpenDataException {
    if (paramOpenType.isArray()) {
      ArrayType arrayType = (ArrayType)paramOpenType;
      paramInt += arrayType.getDimension();
      paramOpenType = arrayType.getElementOpenType();
      paramBoolean = arrayType.isPrimitiveArray();
    } 
    StringBuilder stringBuilder = new StringBuilder(paramInt + "-dimension array of ");
    String str = paramOpenType.getClassName();
    if (paramBoolean) {
      String str1 = getPrimitiveTypeName(str);
      if (str1 == null)
        throw new OpenDataException("Element is not a primitive type: " + str); 
      stringBuilder.append(str1);
    } else {
      stringBuilder.append(str);
    } 
    return stringBuilder.toString();
  }
  
  public int getDimension() { return this.dimension; }
  
  public OpenType<?> getElementOpenType() { return this.elementType; }
  
  public boolean isPrimitiveArray() { return this.primitiveArray; }
  
  public boolean isValue(Object paramObject) {
    if (paramObject == null)
      return false; 
    Class clazz = paramObject.getClass();
    String str = clazz.getName();
    if (!clazz.isArray())
      return false; 
    if (getClassName().equals(str))
      return true; 
    if (this.elementType.getClassName().equals(TabularData.class.getName()) || this.elementType.getClassName().equals(CompositeData.class.getName())) {
      boolean bool = this.elementType.getClassName().equals(TabularData.class.getName());
      int[] arrayOfInt = new int[getDimension()];
      Class clazz1 = bool ? TabularData.class : CompositeData.class;
      Class clazz2 = Array.newInstance(clazz1, arrayOfInt).getClass();
      return !clazz2.isAssignableFrom(clazz) ? false : (!!checkElementsType((Object[])paramObject, this.dimension));
    } 
    return false;
  }
  
  private boolean checkElementsType(Object[] paramArrayOfObject, int paramInt) {
    if (paramInt > 1) {
      for (byte b1 = 0; b1 < paramArrayOfObject.length; b1++) {
        if (!checkElementsType((Object[])paramArrayOfObject[b1], paramInt - 1))
          return false; 
      } 
      return true;
    } 
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] != null && !getElementOpenType().isValue(paramArrayOfObject[b]))
        return false; 
    } 
    return true;
  }
  
  boolean isAssignableFrom(OpenType<?> paramOpenType) {
    if (!(paramOpenType instanceof ArrayType))
      return false; 
    ArrayType arrayType = (ArrayType)paramOpenType;
    return (arrayType.getDimension() == getDimension() && arrayType.isPrimitiveArray() == isPrimitiveArray() && arrayType.getElementOpenType().isAssignableFrom(getElementOpenType()));
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof ArrayType))
      return false; 
    ArrayType arrayType = (ArrayType)paramObject;
    return (this.dimension != arrayType.dimension) ? false : (!this.elementType.equals(arrayType.elementType) ? false : ((this.primitiveArray == arrayType.primitiveArray)));
  }
  
  public int hashCode() {
    if (this.myHashCode == null) {
      int i = 0;
      i += this.dimension;
      i += this.elementType.hashCode();
      i += Boolean.valueOf(this.primitiveArray).hashCode();
      this.myHashCode = Integer.valueOf(i);
    } 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null)
      this.myToString = getClass().getName() + "(name=" + getTypeName() + ",dimension=" + this.dimension + ",elementType=" + this.elementType + ",primitiveArray=" + this.primitiveArray + ")"; 
    return this.myToString;
  }
  
  public static <E> ArrayType<E[]> getArrayType(OpenType<E> paramOpenType) throws OpenDataException { return new ArrayType(1, paramOpenType); }
  
  public static <T> ArrayType<T> getPrimitiveArrayType(Class<T> paramClass) {
    if (!paramClass.isArray())
      throw new IllegalArgumentException("arrayClass must be an array"); 
    byte b = 1;
    Class clazz;
    for (clazz = paramClass.getComponentType(); clazz.isArray(); clazz = clazz.getComponentType())
      b++; 
    String str = clazz.getName();
    if (!clazz.isPrimitive())
      throw new IllegalArgumentException("component type of the array must be a primitive type"); 
    SimpleType simpleType = getPrimitiveOpenType(str);
    try {
      ArrayType arrayType = new ArrayType(simpleType, true);
      if (b > 1)
        arrayType = new ArrayType(b - 1, arrayType); 
      return arrayType;
    } catch (OpenDataException openDataException) {
      throw new IllegalArgumentException(openDataException);
    } 
  }
  
  private Object readResolve() throws ObjectStreamException { return this.primitiveArray ? convertFromWrapperToPrimitiveTypes() : this; }
  
  private <T> ArrayType<T> convertFromWrapperToPrimitiveTypes() {
    String str1 = getClassName();
    String str2 = getTypeName();
    String str3 = getDescription();
    for (Object[] arrayOfObject : PRIMITIVE_ARRAY_TYPES) {
      if (str1.indexOf((String)arrayOfObject[0]) != -1) {
        str1 = str1.replaceFirst("L" + arrayOfObject[0] + ";", (String)arrayOfObject[2]);
        str2 = str2.replaceFirst("L" + arrayOfObject[0] + ";", (String)arrayOfObject[2]);
        str3 = str3.replaceFirst((String)arrayOfObject[0], (String)arrayOfObject[1]);
        break;
      } 
    } 
    return new ArrayType(str1, str2, str3, this.dimension, this.elementType, this.primitiveArray);
  }
  
  private Object writeReplace() throws ObjectStreamException { return this.primitiveArray ? convertFromPrimitiveToWrapperTypes() : this; }
  
  private <T> ArrayType<T> convertFromPrimitiveToWrapperTypes() {
    String str1 = getClassName();
    String str2 = getTypeName();
    String str3 = getDescription();
    for (Object[] arrayOfObject : PRIMITIVE_ARRAY_TYPES) {
      if (str1.indexOf((String)arrayOfObject[2]) != -1) {
        str1 = str1.replaceFirst((String)arrayOfObject[2], "L" + arrayOfObject[0] + ";");
        str2 = str2.replaceFirst((String)arrayOfObject[2], "L" + arrayOfObject[0] + ";");
        str3 = str3.replaceFirst((String)arrayOfObject[1], (String)arrayOfObject[0]);
        break;
      } 
    } 
    return new ArrayType(str1, str2, str3, this.dimension, this.elementType, this.primitiveArray);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\ArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */