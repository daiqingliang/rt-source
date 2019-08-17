package sun.management;

import com.sun.management.VMOption;
import java.io.InvalidObjectException;
import java.lang.management.LockInfo;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public abstract class MappedMXBeanType {
  private static final WeakHashMap<Type, MappedMXBeanType> convertedTypes = new WeakHashMap();
  
  boolean isBasicType = false;
  
  OpenType<?> openType = inProgress;
  
  Class<?> mappedTypeClass;
  
  private static final String KEY = "key";
  
  private static final String VALUE = "value";
  
  private static final String[] mapIndexNames = { "key" };
  
  private static final String[] mapItemNames = { "key", "value" };
  
  private static final Class<?> COMPOSITE_DATA_CLASS = CompositeData.class;
  
  private static final OpenType<?> inProgress;
  
  private static final OpenType[] simpleTypes;
  
  static MappedMXBeanType newMappedType(Type paramType) throws OpenDataException {
    GenericArrayMXBeanType genericArrayMXBeanType = null;
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      if (clazz.isEnum()) {
        genericArrayMXBeanType = new EnumMXBeanType(clazz);
      } else if (clazz.isArray()) {
        ArrayMXBeanType arrayMXBeanType = new ArrayMXBeanType(clazz);
      } else {
        CompositeDataMXBeanType compositeDataMXBeanType = new CompositeDataMXBeanType(clazz);
      } 
    } else if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      Type type = parameterizedType.getRawType();
      if (type instanceof Class) {
        Class clazz = (Class)type;
        if (clazz == List.class) {
          ListMXBeanType listMXBeanType = new ListMXBeanType(parameterizedType);
        } else if (clazz == Map.class) {
          MapMXBeanType mapMXBeanType = new MapMXBeanType(parameterizedType);
        } 
      } 
    } else if (paramType instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramType;
      genericArrayMXBeanType = new GenericArrayMXBeanType(genericArrayType);
    } 
    if (genericArrayMXBeanType == null)
      throw new OpenDataException(paramType + " is not a supported MXBean type."); 
    convertedTypes.put(paramType, genericArrayMXBeanType);
    return genericArrayMXBeanType;
  }
  
  static MappedMXBeanType newBasicType(Class<?> paramClass, OpenType<?> paramOpenType) throws OpenDataException {
    BasicMXBeanType basicMXBeanType = new BasicMXBeanType(paramClass, paramOpenType);
    convertedTypes.put(paramClass, basicMXBeanType);
    return basicMXBeanType;
  }
  
  static MappedMXBeanType getMappedType(Type paramType) throws OpenDataException {
    MappedMXBeanType mappedMXBeanType = (MappedMXBeanType)convertedTypes.get(paramType);
    if (mappedMXBeanType == null)
      mappedMXBeanType = newMappedType(paramType); 
    if (mappedMXBeanType.getOpenType() instanceof InProgress)
      throw new OpenDataException("Recursive data structure"); 
    return mappedMXBeanType;
  }
  
  public static OpenType<?> toOpenType(Type paramType) throws OpenDataException {
    MappedMXBeanType mappedMXBeanType = getMappedType(paramType);
    return mappedMXBeanType.getOpenType();
  }
  
  public static Object toJavaTypeData(Object paramObject, Type paramType) throws OpenDataException, InvalidObjectException {
    if (paramObject == null)
      return null; 
    MappedMXBeanType mappedMXBeanType = getMappedType(paramType);
    return mappedMXBeanType.toJavaTypeData(paramObject);
  }
  
  public static Object toOpenTypeData(Object paramObject, Type paramType) throws OpenDataException, InvalidObjectException {
    if (paramObject == null)
      return null; 
    MappedMXBeanType mappedMXBeanType = getMappedType(paramType);
    return mappedMXBeanType.toOpenTypeData(paramObject);
  }
  
  OpenType<?> getOpenType() { return this.openType; }
  
  boolean isBasicType() { return this.isBasicType; }
  
  String getTypeName() { return getMappedTypeClass().getName(); }
  
  Class<?> getMappedTypeClass() { return this.mappedTypeClass; }
  
  abstract Type getJavaType();
  
  abstract String getName();
  
  abstract Object toOpenTypeData(Object paramObject) throws OpenDataException;
  
  abstract Object toJavaTypeData(Object paramObject) throws OpenDataException;
  
  private static String decapitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    if (paramString.length() > 1 && Character.isUpperCase(paramString.charAt(1)) && Character.isUpperCase(paramString.charAt(0)))
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toLowerCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  static  {
    try {
      inProgress1 = new InProgress();
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    inProgress = inProgress1;
    simpleTypes = new OpenType[] { 
        SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, 
        SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
    try {
      for (byte b = 0; b < simpleTypes.length; b++) {
        Class clazz;
        OpenType openType1 = simpleTypes[b];
        try {
          clazz = Class.forName(openType1.getClassName(), false, MappedMXBeanType.class.getClassLoader());
          newBasicType(clazz, openType1);
        } catch (ClassNotFoundException classNotFoundException) {
          throw new AssertionError(classNotFoundException);
        } catch (OpenDataException openDataException) {
          throw new AssertionError(openDataException);
        } 
        if (clazz.getName().startsWith("java.lang."))
          try {
            Field field = clazz.getField("TYPE");
            Class clazz1 = (Class)field.get(null);
            newBasicType(clazz1, openType1);
          } catch (NoSuchFieldException noSuchFieldException) {
          
          } catch (IllegalAccessException illegalAccessException) {
            throw new AssertionError(illegalAccessException);
          }  
      } 
    } catch (OpenDataException inProgress1) {
      throw new AssertionError(inProgress1);
    } 
  }
  
  static class ArrayMXBeanType extends MappedMXBeanType {
    final Class<?> arrayClass;
    
    protected MappedMXBeanType componentType;
    
    protected MappedMXBeanType baseElementType;
    
    ArrayMXBeanType(Class<?> param1Class) throws OpenDataException {
      this.arrayClass = param1Class;
      this.componentType = getMappedType(param1Class.getComponentType());
      StringBuilder stringBuilder = new StringBuilder();
      Class<?> clazz = param1Class;
      byte b;
      for (b = 0; clazz.isArray(); b++) {
        stringBuilder.append('[');
        clazz = clazz.getComponentType();
      } 
      this.baseElementType = getMappedType(clazz);
      if (clazz.isPrimitive()) {
        stringBuilder = new StringBuilder(param1Class.getName());
      } else {
        stringBuilder.append("L" + this.baseElementType.getTypeName() + ";");
      } 
      try {
        this.mappedTypeClass = Class.forName(stringBuilder.toString());
      } catch (ClassNotFoundException classNotFoundException) {
        OpenDataException openDataException = new OpenDataException("Cannot obtain array class");
        openDataException.initCause(classNotFoundException);
        throw openDataException;
      } 
      this.openType = new ArrayType(b, this.baseElementType.getOpenType());
    }
    
    protected ArrayMXBeanType() { this.arrayClass = null; }
    
    Type getJavaType() { return this.arrayClass; }
    
    String getName() { return this.arrayClass.getName(); }
    
    Object toOpenTypeData(Object param1Object) throws OpenDataException {
      if (this.baseElementType.isBasicType())
        return param1Object; 
      Object[] arrayOfObject1 = (Object[])param1Object;
      Object[] arrayOfObject2 = (Object[])Array.newInstance(this.componentType.getMappedTypeClass(), arrayOfObject1.length);
      byte b = 0;
      for (Object object : arrayOfObject1) {
        if (object == null) {
          arrayOfObject2[b] = null;
        } else {
          arrayOfObject2[b] = this.componentType.toOpenTypeData(object);
        } 
        b++;
      } 
      return arrayOfObject2;
    }
    
    Object toJavaTypeData(Object param1Object) throws OpenDataException {
      if (this.baseElementType.isBasicType())
        return param1Object; 
      Object[] arrayOfObject1 = (Object[])param1Object;
      Object[] arrayOfObject2 = (Object[])Array.newInstance((Class)this.componentType.getJavaType(), arrayOfObject1.length);
      byte b = 0;
      for (Object object : arrayOfObject1) {
        if (object == null) {
          arrayOfObject2[b] = null;
        } else {
          arrayOfObject2[b] = this.componentType.toJavaTypeData(object);
        } 
        b++;
      } 
      return arrayOfObject2;
    }
  }
  
  static class BasicMXBeanType extends MappedMXBeanType {
    final Class<?> basicType;
    
    BasicMXBeanType(Class<?> param1Class, OpenType<?> param1OpenType) {
      this.basicType = param1Class;
      this.openType = param1OpenType;
      this.mappedTypeClass = param1Class;
      this.isBasicType = true;
    }
    
    Type getJavaType() { return this.basicType; }
    
    String getName() { return this.basicType.getName(); }
    
    Object toOpenTypeData(Object param1Object) throws OpenDataException { return param1Object; }
    
    Object toJavaTypeData(Object param1Object) throws OpenDataException { return param1Object; }
  }
  
  static class CompositeDataMXBeanType extends MappedMXBeanType {
    final Class<?> javaClass;
    
    final boolean isCompositeData;
    
    Method fromMethod = null;
    
    CompositeDataMXBeanType(Class<?> param1Class) throws OpenDataException {
      this.javaClass = param1Class;
      this.mappedTypeClass = COMPOSITE_DATA_CLASS;
      try {
        this.fromMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
              public Method run() throws NoSuchMethodException { return MappedMXBeanType.CompositeDataMXBeanType.this.javaClass.getMethod("from", new Class[] { MappedMXBeanType.access$200() }); }
            });
      } catch (PrivilegedActionException privilegedActionException) {}
      if (COMPOSITE_DATA_CLASS.isAssignableFrom(param1Class)) {
        this.isCompositeData = true;
        this.openType = null;
      } else {
        this.isCompositeData = false;
        Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
              public Method[] run() { return MappedMXBeanType.CompositeDataMXBeanType.this.javaClass.getMethods(); }
            });
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (byte b = 0; b < arrayOfMethod.length; b++) {
          String str2;
          Method method = arrayOfMethod[b];
          String str1 = method.getName();
          Type type = method.getGenericReturnType();
          if (str1.startsWith("get")) {
            str2 = str1.substring(3);
          } else if (str1.startsWith("is") && type instanceof Class && (Class)type == boolean.class) {
            str2 = str1.substring(2);
          } else {
            continue;
          } 
          if (!str2.equals("") && method.getParameterTypes().length <= 0 && type != void.class && !str2.equals("Class")) {
            arrayList1.add(MappedMXBeanType.decapitalize(str2));
            arrayList2.add(toOpenType(type));
          } 
          continue;
        } 
        String[] arrayOfString = (String[])arrayList1.toArray(new String[0]);
        this.openType = new CompositeType(param1Class.getName(), param1Class.getName(), arrayOfString, arrayOfString, (OpenType[])arrayList2.toArray(new OpenType[0]));
      } 
    }
    
    Type getJavaType() { return this.javaClass; }
    
    String getName() { return this.javaClass.getName(); }
    
    Object toOpenTypeData(Object param1Object) throws OpenDataException {
      if (param1Object instanceof MemoryUsage)
        return MemoryUsageCompositeData.toCompositeData((MemoryUsage)param1Object); 
      if (param1Object instanceof ThreadInfo)
        return ThreadInfoCompositeData.toCompositeData((ThreadInfo)param1Object); 
      if (param1Object instanceof LockInfo)
        return (param1Object instanceof MonitorInfo) ? MonitorInfoCompositeData.toCompositeData((MonitorInfo)param1Object) : LockInfoCompositeData.toCompositeData((LockInfo)param1Object); 
      if (param1Object instanceof MemoryNotificationInfo)
        return MemoryNotifInfoCompositeData.toCompositeData((MemoryNotificationInfo)param1Object); 
      if (param1Object instanceof VMOption)
        return VMOptionCompositeData.toCompositeData((VMOption)param1Object); 
      if (this.isCompositeData) {
        CompositeData compositeData = (CompositeData)param1Object;
        CompositeType compositeType = compositeData.getCompositeType();
        String[] arrayOfString = (String[])compositeType.keySet().toArray(new String[0]);
        Object[] arrayOfObject = compositeData.getAll(arrayOfString);
        return new CompositeDataSupport(compositeType, arrayOfString, arrayOfObject);
      } 
      throw new OpenDataException(this.javaClass.getName() + " is not supported for platform MXBeans");
    }
    
    Object toJavaTypeData(Object param1Object) throws OpenDataException {
      if (this.fromMethod == null)
        throw new AssertionError("Does not support data conversion"); 
      try {
        return this.fromMethod.invoke(null, new Object[] { param1Object });
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        OpenDataException openDataException = new OpenDataException("Failed to invoke " + this.fromMethod.getName() + " to convert CompositeData  to " + this.javaClass.getName());
        openDataException.initCause(invocationTargetException);
        throw openDataException;
      } 
    }
  }
  
  static class EnumMXBeanType extends MappedMXBeanType {
    final Class enumClass;
    
    EnumMXBeanType(Class<?> param1Class) throws OpenDataException {
      this.enumClass = param1Class;
      this.openType = SimpleType.STRING;
      this.mappedTypeClass = String.class;
    }
    
    Type getJavaType() { return this.enumClass; }
    
    String getName() { return this.enumClass.getName(); }
    
    Object toOpenTypeData(Object param1Object) throws OpenDataException { return ((Enum)param1Object).name(); }
    
    Object toJavaTypeData(Object param1Object) throws OpenDataException {
      try {
        return Enum.valueOf(this.enumClass, (String)param1Object);
      } catch (IllegalArgumentException illegalArgumentException) {
        InvalidObjectException invalidObjectException = new InvalidObjectException("Enum constant named " + (String)param1Object + " is missing");
        invalidObjectException.initCause(illegalArgumentException);
        throw invalidObjectException;
      } 
    }
  }
  
  static class GenericArrayMXBeanType extends ArrayMXBeanType {
    final GenericArrayType gtype;
    
    GenericArrayMXBeanType(GenericArrayType param1GenericArrayType) throws OpenDataException {
      this.gtype = param1GenericArrayType;
      this.componentType = getMappedType(param1GenericArrayType.getGenericComponentType());
      StringBuilder stringBuilder = new StringBuilder();
      Type type = param1GenericArrayType;
      byte b;
      for (b = 0; type instanceof GenericArrayType; b++) {
        stringBuilder.append('[');
        GenericArrayType genericArrayType = (GenericArrayType)type;
        type = genericArrayType.getGenericComponentType();
      } 
      this.baseElementType = getMappedType(type);
      if (type instanceof Class && ((Class)type).isPrimitive()) {
        stringBuilder = new StringBuilder(param1GenericArrayType.toString());
      } else {
        stringBuilder.append("L" + this.baseElementType.getTypeName() + ";");
      } 
      try {
        this.mappedTypeClass = Class.forName(stringBuilder.toString());
      } catch (ClassNotFoundException classNotFoundException) {
        OpenDataException openDataException = new OpenDataException("Cannot obtain array class");
        openDataException.initCause(classNotFoundException);
        throw openDataException;
      } 
      this.openType = new ArrayType(b, this.baseElementType.getOpenType());
    }
    
    Type getJavaType() { return this.gtype; }
    
    String getName() { return this.gtype.toString(); }
  }
  
  private static class InProgress extends OpenType {
    private static final String description = "Marker to detect recursive type use -- internal use only!";
    
    private static final long serialVersionUID = -3413063475064374490L;
    
    InProgress() { super("java.lang.String", "java.lang.String", "Marker to detect recursive type use -- internal use only!"); }
    
    public String toString() { return "Marker to detect recursive type use -- internal use only!"; }
    
    public int hashCode() { return 0; }
    
    public boolean equals(Object param1Object) { return false; }
    
    public boolean isValue(Object param1Object) { return false; }
  }
  
  static class ListMXBeanType extends MappedMXBeanType {
    final ParameterizedType javaType;
    
    final MappedMXBeanType paramType;
    
    final String typeName;
    
    ListMXBeanType(ParameterizedType param1ParameterizedType) throws OpenDataException {
      this.javaType = param1ParameterizedType;
      Type[] arrayOfType = param1ParameterizedType.getActualTypeArguments();
      assert arrayOfType.length == 1;
      if (!(arrayOfType[0] instanceof Class))
        throw new OpenDataException("Element Type for " + param1ParameterizedType + " not supported"); 
      Class clazz = (Class)arrayOfType[0];
      if (clazz.isArray())
        throw new OpenDataException("Element Type for " + param1ParameterizedType + " not supported"); 
      this.paramType = getMappedType(clazz);
      this.typeName = "List<" + this.paramType.getName() + ">";
      try {
        this.mappedTypeClass = Class.forName("[L" + this.paramType.getTypeName() + ";");
      } catch (ClassNotFoundException classNotFoundException) {
        OpenDataException openDataException = new OpenDataException("Array class not found");
        openDataException.initCause(classNotFoundException);
        throw openDataException;
      } 
      this.openType = new ArrayType(1, this.paramType.getOpenType());
    }
    
    Type getJavaType() { return this.javaType; }
    
    String getName() { return this.typeName; }
    
    Object toOpenTypeData(Object param1Object) throws OpenDataException {
      List list = (List)param1Object;
      Object[] arrayOfObject = (Object[])Array.newInstance(this.paramType.getMappedTypeClass(), list.size());
      byte b = 0;
      for (Object object : list)
        arrayOfObject[b++] = this.paramType.toOpenTypeData(object); 
      return arrayOfObject;
    }
    
    Object toJavaTypeData(Object param1Object) throws OpenDataException {
      Object[] arrayOfObject = (Object[])param1Object;
      ArrayList arrayList = new ArrayList(arrayOfObject.length);
      for (Object object : arrayOfObject)
        arrayList.add(this.paramType.toJavaTypeData(object)); 
      return arrayList;
    }
  }
  
  static class MapMXBeanType extends MappedMXBeanType {
    final ParameterizedType javaType;
    
    final MappedMXBeanType keyType;
    
    final MappedMXBeanType valueType;
    
    final String typeName;
    
    MapMXBeanType(ParameterizedType param1ParameterizedType) throws OpenDataException {
      this.javaType = param1ParameterizedType;
      Type[] arrayOfType = param1ParameterizedType.getActualTypeArguments();
      assert arrayOfType.length == 2;
      this.keyType = getMappedType(arrayOfType[0]);
      this.valueType = getMappedType(arrayOfType[1]);
      this.typeName = "Map<" + this.keyType.getName() + "," + this.valueType.getName() + ">";
      OpenType[] arrayOfOpenType = { this.keyType.getOpenType(), this.valueType.getOpenType() };
      CompositeType compositeType = new CompositeType(this.typeName, this.typeName, mapItemNames, mapItemNames, arrayOfOpenType);
      this.openType = new TabularType(this.typeName, this.typeName, compositeType, mapIndexNames);
      this.mappedTypeClass = TabularData.class;
    }
    
    Type getJavaType() { return this.javaType; }
    
    String getName() { return this.typeName; }
    
    Object toOpenTypeData(Object param1Object) throws OpenDataException {
      Map map = (Map)param1Object;
      TabularType tabularType = (TabularType)this.openType;
      TabularDataSupport tabularDataSupport = new TabularDataSupport(tabularType);
      CompositeType compositeType = tabularType.getRowType();
      for (Map.Entry entry : map.entrySet()) {
        Object object1 = this.keyType.toOpenTypeData(entry.getKey());
        Object object2 = this.valueType.toOpenTypeData(entry.getValue());
        CompositeDataSupport compositeDataSupport = new CompositeDataSupport(compositeType, mapItemNames, new Object[] { object1, object2 });
        tabularDataSupport.put(compositeDataSupport);
      } 
      return tabularDataSupport;
    }
    
    Object toJavaTypeData(Object param1Object) throws OpenDataException {
      TabularData tabularData = (TabularData)param1Object;
      HashMap hashMap = new HashMap();
      for (CompositeData compositeData : tabularData.values()) {
        Object object1 = this.keyType.toJavaTypeData(compositeData.get("key"));
        Object object2 = this.valueType.toJavaTypeData(compositeData.get("value"));
        hashMap.put(object1, object2);
      } 
      return hashMap;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MappedMXBeanType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */