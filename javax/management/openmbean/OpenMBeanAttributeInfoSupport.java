package javax.management.openmbean;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.management.Descriptor;
import javax.management.DescriptorRead;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class OpenMBeanAttributeInfoSupport extends MBeanAttributeInfo implements OpenMBeanAttributeInfo {
  static final long serialVersionUID = -4867215622149721849L;
  
  private OpenType<?> openType;
  
  private final Object defaultValue;
  
  private final Set<?> legalValues;
  
  private final Comparable<?> minValue;
  
  private final Comparable<?> maxValue;
  
  private Integer myHashCode = null;
  
  private String myToString = null;
  
  public OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) { this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, (Descriptor)null); }
  
  public OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor) {
    super(paramString1, (paramOpenType == null) ? null : paramOpenType.getClassName(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, ImmutableDescriptor.union(new Descriptor[] { paramDescriptor, (paramOpenType == null) ? null : paramOpenType.getDescriptor() }));
    this.openType = paramOpenType;
    paramDescriptor = getDescriptor();
    this.defaultValue = valueFrom(paramDescriptor, "defaultValue", paramOpenType);
    this.legalValues = valuesFrom(paramDescriptor, "legalValues", paramOpenType);
    this.minValue = comparableValueFrom(paramDescriptor, "minValue", paramOpenType);
    this.maxValue = comparableValueFrom(paramDescriptor, "maxValue", paramOpenType);
    try {
      check(this);
    } catch (OpenDataException openDataException) {
      throw new IllegalArgumentException(openDataException.getMessage(), openDataException);
    } 
  }
  
  public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT) throws OpenDataException { this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, (Object[])null); }
  
  public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, T[] paramArrayOfT) throws OpenDataException { this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, paramArrayOfT, null, null); }
  
  public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, Comparable<T> paramComparable1, Comparable<T> paramComparable2) throws OpenDataException { this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, null, paramComparable1, paramComparable2); }
  
  private <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2) throws OpenDataException {
    super(paramString1, (paramOpenType == null) ? null : paramOpenType.getClassName(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, makeDescriptor(paramOpenType, paramT, paramArrayOfT, paramComparable1, paramComparable2));
    this.openType = paramOpenType;
    Descriptor descriptor = getDescriptor();
    this.defaultValue = paramT;
    this.minValue = paramComparable1;
    this.maxValue = paramComparable2;
    this.legalValues = (Set)descriptor.getFieldValue("legalValues");
    check(this);
  }
  
  private Object readResolve() {
    if (getDescriptor().getFieldNames().length == 0) {
      OpenType openType1 = (OpenType)cast(this.openType);
      Set set = (Set)cast(this.legalValues);
      Comparable comparable1 = (Comparable)cast(this.minValue);
      Comparable comparable2 = (Comparable)cast(this.maxValue);
      return new OpenMBeanAttributeInfoSupport(this.name, this.description, this.openType, isReadable(), isWritable(), isIs(), makeDescriptor(openType1, this.defaultValue, set, comparable1, comparable2));
    } 
    return this;
  }
  
  static void check(OpenMBeanParameterInfo paramOpenMBeanParameterInfo) throws OpenDataException {
    OpenType openType1 = paramOpenMBeanParameterInfo.getOpenType();
    if (openType1 == null)
      throw new IllegalArgumentException("OpenType cannot be null"); 
    if (paramOpenMBeanParameterInfo.getName() == null || paramOpenMBeanParameterInfo.getName().trim().equals(""))
      throw new IllegalArgumentException("Name cannot be null or empty"); 
    if (paramOpenMBeanParameterInfo.getDescription() == null || paramOpenMBeanParameterInfo.getDescription().trim().equals(""))
      throw new IllegalArgumentException("Description cannot be null or empty"); 
    if (paramOpenMBeanParameterInfo.hasDefaultValue()) {
      if (openType1.isArray() || openType1 instanceof TabularType)
        throw new OpenDataException("Default value not supported for ArrayType and TabularType"); 
      if (!openType1.isValue(paramOpenMBeanParameterInfo.getDefaultValue())) {
        String str = "Argument defaultValue's class [\"" + paramOpenMBeanParameterInfo.getDefaultValue().getClass().getName() + "\"] does not match the one defined in openType[\"" + openType1.getClassName() + "\"]";
        throw new OpenDataException(str);
      } 
    } 
    if (paramOpenMBeanParameterInfo.hasLegalValues() && (paramOpenMBeanParameterInfo.hasMinValue() || paramOpenMBeanParameterInfo.hasMaxValue()))
      throw new OpenDataException("cannot have both legalValue and minValue or maxValue"); 
    if (paramOpenMBeanParameterInfo.hasMinValue() && !openType1.isValue(paramOpenMBeanParameterInfo.getMinValue())) {
      String str = "Type of minValue [" + paramOpenMBeanParameterInfo.getMinValue().getClass().getName() + "] does not match OpenType [" + openType1.getClassName() + "]";
      throw new OpenDataException(str);
    } 
    if (paramOpenMBeanParameterInfo.hasMaxValue() && !openType1.isValue(paramOpenMBeanParameterInfo.getMaxValue())) {
      String str = "Type of maxValue [" + paramOpenMBeanParameterInfo.getMaxValue().getClass().getName() + "] does not match OpenType [" + openType1.getClassName() + "]";
      throw new OpenDataException(str);
    } 
    if (paramOpenMBeanParameterInfo.hasDefaultValue()) {
      Object object = paramOpenMBeanParameterInfo.getDefaultValue();
      if (paramOpenMBeanParameterInfo.hasLegalValues() && !paramOpenMBeanParameterInfo.getLegalValues().contains(object))
        throw new OpenDataException("defaultValue is not contained in legalValues"); 
      if (paramOpenMBeanParameterInfo.hasMinValue() && compare(paramOpenMBeanParameterInfo.getMinValue(), object) > 0)
        throw new OpenDataException("minValue cannot be greater than defaultValue"); 
      if (paramOpenMBeanParameterInfo.hasMaxValue() && compare(paramOpenMBeanParameterInfo.getMaxValue(), object) < 0)
        throw new OpenDataException("maxValue cannot be less than defaultValue"); 
    } 
    if (paramOpenMBeanParameterInfo.hasLegalValues()) {
      if (openType1 instanceof TabularType || openType1.isArray())
        throw new OpenDataException("Legal values not supported for TabularType and arrays"); 
      for (Object object : paramOpenMBeanParameterInfo.getLegalValues()) {
        if (!openType1.isValue(object)) {
          String str = "Element of legalValues [" + object + "] is not a valid value for the specified openType [" + openType1.toString() + "]";
          throw new OpenDataException(str);
        } 
      } 
    } 
    if (paramOpenMBeanParameterInfo.hasMinValue() && paramOpenMBeanParameterInfo.hasMaxValue() && compare(paramOpenMBeanParameterInfo.getMinValue(), paramOpenMBeanParameterInfo.getMaxValue()) > 0)
      throw new OpenDataException("minValue cannot be greater than maxValue"); 
  }
  
  static int compare(Object paramObject1, Object paramObject2) { return ((Comparable)paramObject1).compareTo(paramObject2); }
  
  static <T> Descriptor makeDescriptor(OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2) {
    HashMap hashMap = new HashMap();
    if (paramT != null)
      hashMap.put("defaultValue", paramT); 
    if (paramArrayOfT != null) {
      HashSet hashSet = new HashSet();
      for (T t : paramArrayOfT)
        hashSet.add(t); 
      Set set = Collections.unmodifiableSet(hashSet);
      hashMap.put("legalValues", set);
    } 
    if (paramComparable1 != null)
      hashMap.put("minValue", paramComparable1); 
    if (paramComparable2 != null)
      hashMap.put("maxValue", paramComparable2); 
    if (hashMap.isEmpty())
      return paramOpenType.getDescriptor(); 
    hashMap.put("openType", paramOpenType);
    return new ImmutableDescriptor(hashMap);
  }
  
  static <T> Descriptor makeDescriptor(OpenType<T> paramOpenType, T paramT, Set<T> paramSet, Comparable<T> paramComparable1, Comparable<T> paramComparable2) {
    Object[] arrayOfObject;
    if (paramSet == null) {
      arrayOfObject = null;
    } else {
      arrayOfObject = (Object[])cast(new Object[paramSet.size()]);
      paramSet.toArray(arrayOfObject);
    } 
    return makeDescriptor(paramOpenType, paramT, arrayOfObject, paramComparable1, paramComparable2);
  }
  
  static <T> T valueFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType) {
    Object object = paramDescriptor.getFieldValue(paramString);
    if (object == null)
      return null; 
    try {
      return (T)convertFrom(object, paramOpenType);
    } catch (Exception exception) {
      String str = "Cannot convert descriptor field " + paramString + "  to " + paramOpenType.getTypeName();
      throw (IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(str), exception);
    } 
  }
  
  static <T> Set<T> valuesFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType) {
    List list;
    Object object = paramDescriptor.getFieldValue(paramString);
    if (object == null)
      return null; 
    if (object instanceof Set) {
      Set set = (Set)object;
      boolean bool = true;
      for (Object object1 : set) {
        if (!paramOpenType.isValue(object1)) {
          bool = false;
          break;
        } 
      } 
      if (bool)
        return (Set)cast(set); 
      list = set;
    } else if (object instanceof Object[]) {
      list = Arrays.asList((Object[])object);
    } else {
      String str = "Descriptor value for " + paramString + " must be a Set or an array: " + object.getClass().getName();
      throw new IllegalArgumentException(str);
    } 
    HashSet hashSet = new HashSet();
    for (Object object1 : list)
      hashSet.add(convertFrom(object1, paramOpenType)); 
    return hashSet;
  }
  
  static <T> Comparable<?> comparableValueFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType) {
    Object object = valueFrom(paramDescriptor, paramString, paramOpenType);
    if (object == null || object instanceof Comparable)
      return (Comparable)object; 
    String str = "Descriptor field " + paramString + " with value " + object + " is not Comparable";
    throw new IllegalArgumentException(str);
  }
  
  private static <T> T convertFrom(Object paramObject, OpenType<T> paramOpenType) { return paramOpenType.isValue(paramObject) ? (T)cast(paramObject) : (T)convertFromStrings(paramObject, paramOpenType); }
  
  private static <T> T convertFromStrings(Object paramObject, OpenType<T> paramOpenType) {
    if (paramOpenType instanceof ArrayType)
      return (T)convertFromStringArray(paramObject, paramOpenType); 
    if (paramObject instanceof String)
      return (T)convertFromString((String)paramObject, paramOpenType); 
    String str = "Cannot convert value " + paramObject + " of type " + paramObject.getClass().getName() + " to type " + paramOpenType.getTypeName();
    throw new IllegalArgumentException(str);
  }
  
  private static <T> T convertFromString(String paramString, OpenType<T> paramOpenType) {
    Object object;
    Method method;
    Class clazz;
    try {
      method = paramOpenType.safeGetClassName();
      ReflectUtil.checkPackageAccess(method);
      clazz = (Class)cast(Class.forName(method));
    } catch (ClassNotFoundException null) {
      throw new NoClassDefFoundError(method.toString());
    } 
    try {
      method = clazz.getMethod("valueOf", new Class[] { String.class });
      if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() != clazz)
        method = null; 
    } catch (NoSuchMethodException null) {
      method = null;
    } 
    if (method != null)
      try {
        return (T)clazz.cast(MethodUtil.invoke(method, null, new Object[] { paramString }));
      } catch (Exception null) {
        String str = "Could not convert \"" + paramString + "\" using method: " + method;
        throw new IllegalArgumentException(str, object);
      }  
    try {
      object = clazz.getConstructor(new Class[] { String.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      object = null;
    } 
    if (object != null)
      try {
        return (T)object.newInstance(new Object[] { paramString });
      } catch (Exception exception) {
        String str = "Could not convert \"" + paramString + "\" using constructor: " + object;
        throw new IllegalArgumentException(str, exception);
      }  
    throw new IllegalArgumentException("Don't know how to convert string to " + paramOpenType.getTypeName());
  }
  
  private static <T> T convertFromStringArray(Object paramObject, OpenType<T> paramOpenType) {
    ArrayType arrayType2;
    Class clazz2;
    Class clazz1;
    ArrayType arrayType1 = (ArrayType)paramOpenType;
    OpenType openType1 = arrayType1.getElementOpenType();
    int i = arrayType1.getDimension();
    String str = "[";
    for (byte b1 = 1; b1 < i; b1++)
      str = str + "["; 
    try {
      arrayType2 = openType1.safeGetClassName();
      ReflectUtil.checkPackageAccess(arrayType2);
      clazz2 = (clazz1 = Class.forName(str + "Ljava.lang.String;")).forName(str + "L" + arrayType2 + ";");
    } catch (ClassNotFoundException null) {
      throw new NoClassDefFoundError(arrayType2.toString());
    } 
    if (!clazz1.isInstance(paramObject)) {
      arrayType2 = "Value for " + i + "-dimensional array of " + openType1.getTypeName() + " must be same type or a String array with same dimensions";
      throw new IllegalArgumentException(arrayType2);
    } 
    if (i == 1) {
      arrayType2 = openType1;
    } else {
      try {
        arrayType2 = new ArrayType(i - 1, openType1);
      } catch (OpenDataException openDataException) {
        throw new IllegalArgumentException(openDataException.getMessage(), openDataException);
      } 
    } 
    int j = Array.getLength(paramObject);
    Object[] arrayOfObject = (Object[])Array.newInstance(clazz2.getComponentType(), j);
    for (byte b2 = 0; b2 < j; b2++) {
      Object object1 = Array.get(paramObject, b2);
      Object object2 = convertFromStrings(object1, arrayType2);
      Array.set(arrayOfObject, b2, object2);
    } 
    return (T)cast(arrayOfObject);
  }
  
  static <T> T cast(Object paramObject) { return (T)paramObject; }
  
  public OpenType<?> getOpenType() { return this.openType; }
  
  public Object getDefaultValue() { return this.defaultValue; }
  
  public Set<?> getLegalValues() { return this.legalValues; }
  
  public Comparable<?> getMinValue() { return this.minValue; }
  
  public Comparable<?> getMaxValue() { return this.maxValue; }
  
  public boolean hasDefaultValue() { return (this.defaultValue != null); }
  
  public boolean hasLegalValues() { return (this.legalValues != null); }
  
  public boolean hasMinValue() { return (this.minValue != null); }
  
  public boolean hasMaxValue() { return (this.maxValue != null); }
  
  public boolean isValue(Object paramObject) { return isValue(this, paramObject); }
  
  static boolean isValue(OpenMBeanParameterInfo paramOpenMBeanParameterInfo, Object paramObject) { return (paramOpenMBeanParameterInfo.hasDefaultValue() && paramObject == null) ? true : ((paramOpenMBeanParameterInfo.getOpenType().isValue(paramObject) && (!paramOpenMBeanParameterInfo.hasLegalValues() || paramOpenMBeanParameterInfo.getLegalValues().contains(paramObject)) && (!paramOpenMBeanParameterInfo.hasMinValue() || paramOpenMBeanParameterInfo.getMinValue().compareTo(paramObject) <= 0) && (!paramOpenMBeanParameterInfo.hasMaxValue() || paramOpenMBeanParameterInfo.getMaxValue().compareTo(paramObject) >= 0))); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof OpenMBeanAttributeInfo))
      return false; 
    OpenMBeanAttributeInfo openMBeanAttributeInfo = (OpenMBeanAttributeInfo)paramObject;
    return (isReadable() == openMBeanAttributeInfo.isReadable() && isWritable() == openMBeanAttributeInfo.isWritable() && isIs() == openMBeanAttributeInfo.isIs() && equal(this, openMBeanAttributeInfo));
  }
  
  static boolean equal(OpenMBeanParameterInfo paramOpenMBeanParameterInfo1, OpenMBeanParameterInfo paramOpenMBeanParameterInfo2) {
    if (paramOpenMBeanParameterInfo1 instanceof DescriptorRead) {
      if (!(paramOpenMBeanParameterInfo2 instanceof DescriptorRead))
        return false; 
      Descriptor descriptor1 = ((DescriptorRead)paramOpenMBeanParameterInfo1).getDescriptor();
      Descriptor descriptor2 = ((DescriptorRead)paramOpenMBeanParameterInfo2).getDescriptor();
      if (!descriptor1.equals(descriptor2))
        return false; 
    } else if (paramOpenMBeanParameterInfo2 instanceof DescriptorRead) {
      return false;
    } 
    return (paramOpenMBeanParameterInfo1.getName().equals(paramOpenMBeanParameterInfo2.getName()) && paramOpenMBeanParameterInfo1.getOpenType().equals(paramOpenMBeanParameterInfo2.getOpenType()) && (paramOpenMBeanParameterInfo1.hasDefaultValue() ? paramOpenMBeanParameterInfo1.getDefaultValue().equals(paramOpenMBeanParameterInfo2.getDefaultValue()) : !paramOpenMBeanParameterInfo2.hasDefaultValue()) && (paramOpenMBeanParameterInfo1.hasMinValue() ? paramOpenMBeanParameterInfo1.getMinValue().equals(paramOpenMBeanParameterInfo2.getMinValue()) : !paramOpenMBeanParameterInfo2.hasMinValue()) && (paramOpenMBeanParameterInfo1.hasMaxValue() ? paramOpenMBeanParameterInfo1.getMaxValue().equals(paramOpenMBeanParameterInfo2.getMaxValue()) : !paramOpenMBeanParameterInfo2.hasMaxValue()) && (paramOpenMBeanParameterInfo1.hasLegalValues() ? paramOpenMBeanParameterInfo1.getLegalValues().equals(paramOpenMBeanParameterInfo2.getLegalValues()) : !paramOpenMBeanParameterInfo2.hasLegalValues()));
  }
  
  public int hashCode() {
    if (this.myHashCode == null)
      this.myHashCode = Integer.valueOf(hashCode(this)); 
    return this.myHashCode.intValue();
  }
  
  static int hashCode(OpenMBeanParameterInfo paramOpenMBeanParameterInfo) {
    int i = 0;
    i += paramOpenMBeanParameterInfo.getName().hashCode();
    i += paramOpenMBeanParameterInfo.getOpenType().hashCode();
    if (paramOpenMBeanParameterInfo.hasDefaultValue())
      i += paramOpenMBeanParameterInfo.getDefaultValue().hashCode(); 
    if (paramOpenMBeanParameterInfo.hasMinValue())
      i += paramOpenMBeanParameterInfo.getMinValue().hashCode(); 
    if (paramOpenMBeanParameterInfo.hasMaxValue())
      i += paramOpenMBeanParameterInfo.getMaxValue().hashCode(); 
    if (paramOpenMBeanParameterInfo.hasLegalValues())
      i += paramOpenMBeanParameterInfo.getLegalValues().hashCode(); 
    if (paramOpenMBeanParameterInfo instanceof DescriptorRead)
      i += ((DescriptorRead)paramOpenMBeanParameterInfo).getDescriptor().hashCode(); 
    return i;
  }
  
  public String toString() {
    if (this.myToString == null)
      this.myToString = toString(this); 
    return this.myToString;
  }
  
  static String toString(OpenMBeanParameterInfo paramOpenMBeanParameterInfo) {
    Descriptor descriptor = (paramOpenMBeanParameterInfo instanceof DescriptorRead) ? ((DescriptorRead)paramOpenMBeanParameterInfo).getDescriptor() : null;
    return paramOpenMBeanParameterInfo.getClass().getName() + "(name=" + paramOpenMBeanParameterInfo.getName() + ",openType=" + paramOpenMBeanParameterInfo.getOpenType() + ",default=" + paramOpenMBeanParameterInfo.getDefaultValue() + ",minValue=" + paramOpenMBeanParameterInfo.getMinValue() + ",maxValue=" + paramOpenMBeanParameterInfo.getMaxValue() + ",legalValues=" + paramOpenMBeanParameterInfo.getLegalValues() + ((descriptor == null) ? "" : (",descriptor=" + descriptor)) + ")";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenMBeanAttributeInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */