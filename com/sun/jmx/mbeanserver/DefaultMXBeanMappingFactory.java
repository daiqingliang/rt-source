package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.io.InvalidObjectException;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataInvocationHandler;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultMXBeanMappingFactory extends MXBeanMappingFactory {
  private static final Mappings mappings = new Mappings(null);
  
  private static final List<MXBeanMapping> permanentMappings = Util.newList();
  
  private static final String[] keyArray;
  
  private static final String[] keyValueArray;
  
  private static final Map<Type, Type> inProgress;
  
  static boolean isIdentity(MXBeanMapping paramMXBeanMapping) { return (paramMXBeanMapping instanceof NonNullMXBeanMapping && ((NonNullMXBeanMapping)paramMXBeanMapping).isIdentity()); }
  
  private static MXBeanMapping getMapping(Type paramType) {
    WeakReference weakReference = (WeakReference)mappings.get(paramType);
    return (weakReference == null) ? null : (MXBeanMapping)weakReference.get();
  }
  
  private static void putMapping(Type paramType, MXBeanMapping paramMXBeanMapping) {
    WeakReference weakReference = new WeakReference(paramMXBeanMapping);
    mappings.put(paramType, weakReference);
  }
  
  private static void putPermanentMapping(Type paramType, MXBeanMapping paramMXBeanMapping) {
    putMapping(paramType, paramMXBeanMapping);
    permanentMappings.add(paramMXBeanMapping);
  }
  
  public MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    if (inProgress.containsKey(paramType))
      throw new OpenDataException("Recursive data structure, including " + MXBeanIntrospector.typeName(paramType)); 
    MXBeanMapping mXBeanMapping = getMapping(paramType);
    if (mXBeanMapping != null)
      return mXBeanMapping; 
    inProgress.put(paramType, paramType);
    try {
      mXBeanMapping = makeMapping(paramType, paramMXBeanMappingFactory);
    } catch (OpenDataException openDataException) {
      throw openDataException("Cannot convert type: " + MXBeanIntrospector.typeName(paramType), openDataException);
    } finally {
      inProgress.remove(paramType);
    } 
    putMapping(paramType, mXBeanMapping);
    return mXBeanMapping;
  }
  
  private MXBeanMapping makeMapping(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    if (paramType instanceof GenericArrayType) {
      Type type = ((GenericArrayType)paramType).getGenericComponentType();
      return makeArrayOrCollectionMapping(paramType, type, paramMXBeanMappingFactory);
    } 
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      if (clazz.isEnum())
        return makeEnumMapping(clazz, java.lang.annotation.ElementType.class); 
      if (clazz.isArray()) {
        Class clazz1 = clazz.getComponentType();
        return makeArrayOrCollectionMapping(clazz, clazz1, paramMXBeanMappingFactory);
      } 
      return JMX.isMXBeanInterface(clazz) ? makeMXBeanRefMapping(clazz) : makeCompositeMapping(clazz, paramMXBeanMappingFactory);
    } 
    if (paramType instanceof ParameterizedType)
      return makeParameterizedTypeMapping((ParameterizedType)paramType, paramMXBeanMappingFactory); 
    throw new OpenDataException("Cannot map type: " + paramType);
  }
  
  private static <T extends Enum<T>> MXBeanMapping makeEnumMapping(Class<?> paramClass1, Class<T> paramClass2) {
    ReflectUtil.checkPackageAccess(paramClass1);
    return new EnumMapping((Class)Util.cast(paramClass1));
  }
  
  private MXBeanMapping makeArrayOrCollectionMapping(Type paramType1, Type paramType2, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    String str;
    Class clazz2;
    MXBeanMapping mXBeanMapping = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
    OpenType openType = mXBeanMapping.getOpenType();
    ArrayType arrayType = ArrayType.getArrayType(openType);
    Class clazz1 = mXBeanMapping.getOpenClass();
    if (clazz1.isArray()) {
      str = "[" + clazz1.getName();
    } else {
      str = "[L" + clazz1.getName() + ";";
    } 
    try {
      clazz2 = Class.forName(str);
    } catch (ClassNotFoundException classNotFoundException) {
      throw openDataException("Cannot obtain array class", classNotFoundException);
    } 
    return (paramType1 instanceof ParameterizedType) ? new CollectionMapping(paramType1, arrayType, clazz2, mXBeanMapping) : (isIdentity(mXBeanMapping) ? new IdentityMapping(paramType1, arrayType) : new ArrayMapping(paramType1, arrayType, clazz2, mXBeanMapping));
  }
  
  private MXBeanMapping makeTabularMapping(Type paramType1, boolean paramBoolean, Type paramType2, Type paramType3, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    String str = MXBeanIntrospector.typeName(paramType1);
    MXBeanMapping mXBeanMapping1 = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
    MXBeanMapping mXBeanMapping2 = paramMXBeanMappingFactory.mappingForType(paramType3, paramMXBeanMappingFactory);
    OpenType openType1 = mXBeanMapping1.getOpenType();
    OpenType openType2 = mXBeanMapping2.getOpenType();
    CompositeType compositeType = new CompositeType(str, str, keyValueArray, keyValueArray, new OpenType[] { openType1, openType2 });
    TabularType tabularType = new TabularType(str, str, compositeType, keyArray);
    return new TabularMapping(paramType1, paramBoolean, tabularType, mXBeanMapping1, mXBeanMapping2);
  }
  
  private MXBeanMapping makeParameterizedTypeMapping(ParameterizedType paramParameterizedType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    Type type = paramParameterizedType.getRawType();
    if (type instanceof Class) {
      Class clazz = (Class)type;
      if (clazz == List.class || clazz == Set.class || clazz == SortedSet.class) {
        Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
        assert arrayOfType.length == 1;
        if (clazz == SortedSet.class)
          mustBeComparable(clazz, arrayOfType[0]); 
        return makeArrayOrCollectionMapping(paramParameterizedType, arrayOfType[0], paramMXBeanMappingFactory);
      } 
      boolean bool = (clazz == SortedMap.class);
      if (clazz == Map.class || bool) {
        Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
        assert arrayOfType.length == 2;
        if (bool)
          mustBeComparable(clazz, arrayOfType[0]); 
        return makeTabularMapping(paramParameterizedType, bool, arrayOfType[0], arrayOfType[1], paramMXBeanMappingFactory);
      } 
    } 
    throw new OpenDataException("Cannot convert type: " + paramParameterizedType);
  }
  
  private static MXBeanMapping makeMXBeanRefMapping(Type paramType) { return new MXBeanRefMapping(paramType); }
  
  private MXBeanMapping makeCompositeMapping(Class<?> paramClass, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    boolean bool = (paramClass.getName().equals("com.sun.management.GcInfo") && paramClass.getClassLoader() == null) ? 1 : 0;
    ReflectUtil.checkPackageAccess(paramClass);
    List list = MBeanAnalyzer.eliminateCovariantMethods(Arrays.asList(paramClass.getMethods()));
    SortedMap sortedMap = Util.newSortedMap();
    for (Method method1 : list) {
      String str = propertyName(method1);
      if (str == null || (bool && str.equals("CompositeType")))
        continue; 
      Method method2 = (Method)sortedMap.put(decapitalize(str), method1);
      if (method2 != null) {
        String str1 = "Class " + paramClass.getName() + " has method name clash: " + method2.getName() + ", " + method1.getName();
        throw new OpenDataException(str1);
      } 
    } 
    int i = sortedMap.size();
    if (i == 0)
      throw new OpenDataException("Can't map " + paramClass.getName() + " to an open data type"); 
    Method[] arrayOfMethod = new Method[i];
    String[] arrayOfString = new String[i];
    OpenType[] arrayOfOpenType = new OpenType[i];
    byte b = 0;
    for (Map.Entry entry : sortedMap.entrySet()) {
      arrayOfString[b] = (String)entry.getKey();
      Method method = (Method)entry.getValue();
      arrayOfMethod[b] = method;
      Type type = method.getGenericReturnType();
      arrayOfOpenType[b] = paramMXBeanMappingFactory.mappingForType(type, paramMXBeanMappingFactory).getOpenType();
      b++;
    } 
    CompositeType compositeType = new CompositeType(paramClass.getName(), paramClass.getName(), arrayOfString, arrayOfString, arrayOfOpenType);
    return new CompositeMapping(paramClass, compositeType, arrayOfString, arrayOfMethod, paramMXBeanMappingFactory);
  }
  
  static InvalidObjectException invalidObjectException(String paramString, Throwable paramThrowable) { return (InvalidObjectException)EnvHelp.initCause(new InvalidObjectException(paramString), paramThrowable); }
  
  static InvalidObjectException invalidObjectException(Throwable paramThrowable) { return invalidObjectException(paramThrowable.getMessage(), paramThrowable); }
  
  static OpenDataException openDataException(String paramString, Throwable paramThrowable) { return (OpenDataException)EnvHelp.initCause(new OpenDataException(paramString), paramThrowable); }
  
  static OpenDataException openDataException(Throwable paramThrowable) { return openDataException(paramThrowable.getMessage(), paramThrowable); }
  
  static void mustBeComparable(Class<?> paramClass, Type paramType) throws OpenDataException {
    if (!(paramType instanceof Class) || !Comparable.class.isAssignableFrom((Class)paramType)) {
      String str = "Parameter class " + paramType + " of " + paramClass.getName() + " does not implement " + Comparable.class.getName();
      throw new OpenDataException(str);
    } 
  }
  
  public static String decapitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    int i = Character.offsetByCodePoints(paramString, 0, 1);
    return (i < paramString.length() && Character.isUpperCase(paramString.codePointAt(i))) ? paramString : (paramString.substring(0, i).toLowerCase() + paramString.substring(i));
  }
  
  static String capitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    int i = paramString.offsetByCodePoints(0, 1);
    return paramString.substring(0, i).toUpperCase() + paramString.substring(i);
  }
  
  public static String propertyName(Method paramMethod) {
    String str1 = null;
    String str2 = paramMethod.getName();
    if (str2.startsWith("get")) {
      str1 = str2.substring(3);
    } else if (str2.startsWith("is") && paramMethod.getReturnType() == boolean.class) {
      str1 = str2.substring(2);
    } 
    return (str1 == null || str1.length() == 0 || paramMethod.getParameterTypes().length > 0 || paramMethod.getReturnType() == void.class || str2.equals("getClass")) ? null : str1;
  }
  
  static  {
    OpenType[] arrayOfOpenType = { 
        SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, 
        SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
    for (byte b = 0; b < arrayOfOpenType.length; b++) {
      Class clazz;
      OpenType openType = arrayOfOpenType[b];
      try {
        clazz = Class.forName(openType.getClassName(), false, ObjectName.class.getClassLoader());
      } catch (ClassNotFoundException classNotFoundException) {
        throw new Error(classNotFoundException);
      } 
      IdentityMapping identityMapping = new IdentityMapping(clazz, openType);
      putPermanentMapping(clazz, identityMapping);
      if (clazz.getName().startsWith("java.lang."))
        try {
          Field field = clazz.getField("TYPE");
          Class clazz1 = (Class)field.get(null);
          IdentityMapping identityMapping1 = new IdentityMapping(clazz1, openType);
          putPermanentMapping(clazz1, identityMapping1);
          if (clazz1 != void.class) {
            Class clazz2 = Array.newInstance(clazz1, 0).getClass();
            ArrayType arrayType = ArrayType.getPrimitiveArrayType(clazz2);
            IdentityMapping identityMapping2 = new IdentityMapping(clazz2, arrayType);
            putPermanentMapping(clazz2, identityMapping2);
          } 
        } catch (NoSuchFieldException noSuchFieldException) {
        
        } catch (IllegalAccessException illegalAccessException) {
          assert false;
        }  
    } 
    keyArray = new String[] { "key" };
    keyValueArray = new String[] { "key", "value" };
    inProgress = Util.newIdentityHashMap();
  }
  
  private static final class ArrayMapping extends NonNullMXBeanMapping {
    private final MXBeanMapping elementMapping;
    
    ArrayMapping(Type param1Type, ArrayType<?> param1ArrayType, Class<?> param1Class, MXBeanMapping param1MXBeanMapping) {
      super(param1Type, param1ArrayType);
      this.elementMapping = param1MXBeanMapping;
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      Object[] arrayOfObject1 = (Object[])param1Object;
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = (Object[])Array.newInstance(getOpenClass().getComponentType(), i);
      for (byte b = 0; b < i; b++)
        arrayOfObject2[b] = this.elementMapping.toOpenValue(arrayOfObject1[b]); 
      return arrayOfObject2;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws OpenDataException {
      Class clazz;
      Object[] arrayOfObject1 = (Object[])param1Object;
      Type type = getJavaType();
      if (type instanceof GenericArrayType) {
        clazz = ((GenericArrayType)type).getGenericComponentType();
      } else if (type instanceof Class && ((Class)type).isArray()) {
        clazz = ((Class)type).getComponentType();
      } else {
        throw new IllegalArgumentException("Not an array: " + type);
      } 
      Object[] arrayOfObject2 = (Object[])Array.newInstance((Class)clazz, arrayOfObject1.length);
      for (byte b = 0; b < arrayOfObject1.length; b++)
        arrayOfObject2[b] = this.elementMapping.fromOpenValue(arrayOfObject1[b]); 
      return arrayOfObject2;
    }
    
    public void checkReconstructible() { this.elementMapping.checkReconstructible(); }
  }
  
  private static final class CollectionMapping extends NonNullMXBeanMapping {
    private final Class<? extends Collection<?>> collectionClass;
    
    private final MXBeanMapping elementMapping;
    
    CollectionMapping(Type param1Type, ArrayType<?> param1ArrayType, Class<?> param1Class, MXBeanMapping param1MXBeanMapping) {
      super(param1Type, param1ArrayType);
      this.elementMapping = param1MXBeanMapping;
      Type type = ((ParameterizedType)param1Type).getRawType();
      Class clazz = (Class)type;
      if (clazz == List.class) {
        object = java.util.ArrayList.class;
      } else if (clazz == Set.class) {
        object = java.util.HashSet.class;
      } else if (clazz == SortedSet.class) {
        object = TreeSet.class;
      } else {
        assert false;
        object = null;
      } 
      this.collectionClass = (Class)Util.cast(object);
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      Collection collection = (Collection)param1Object;
      if (collection instanceof SortedSet) {
        Comparator comparator = ((SortedSet)collection).comparator();
        if (comparator != null) {
          String str = "Cannot convert SortedSet with non-null comparator: " + comparator;
          throw DefaultMXBeanMappingFactory.openDataException(str, new IllegalArgumentException(str));
        } 
      } 
      Object[] arrayOfObject = (Object[])Array.newInstance(getOpenClass().getComponentType(), collection.size());
      byte b = 0;
      for (Object object : collection)
        arrayOfObject[b++] = this.elementMapping.toOpenValue(object); 
      return arrayOfObject;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws OpenDataException {
      Collection collection;
      Object[] arrayOfObject = (Object[])param1Object;
      try {
        collection = (Collection)Util.cast(this.collectionClass.newInstance());
      } catch (Exception exception) {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot create collection", exception);
      } 
      for (Object object1 : arrayOfObject) {
        Object object2 = this.elementMapping.fromOpenValue(object1);
        if (!collection.add(object2)) {
          String str = "Could not add " + object1 + " to " + this.collectionClass.getName() + " (duplicate set element?)";
          throw new InvalidObjectException(str);
        } 
      } 
      return collection;
    }
    
    public void checkReconstructible() { this.elementMapping.checkReconstructible(); }
  }
  
  private static abstract class CompositeBuilder {
    private final Class<?> targetClass;
    
    private final String[] itemNames;
    
    CompositeBuilder(Class<?> param1Class, String[] param1ArrayOfString) {
      this.targetClass = param1Class;
      this.itemNames = param1ArrayOfString;
    }
    
    Class<?> getTargetClass() { return this.targetClass; }
    
    String[] getItemNames() { return this.itemNames; }
    
    abstract String applicable(Method[] param1ArrayOfMethod) throws InvalidObjectException;
    
    Throwable possibleCause() { return null; }
    
    abstract Object fromCompositeData(CompositeData param1CompositeData, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) throws InvalidObjectException;
  }
  
  private static class CompositeBuilderCheckGetters extends CompositeBuilder {
    private final MXBeanMapping[] getterConverters;
    
    private Throwable possibleCause;
    
    CompositeBuilderCheckGetters(Class<?> param1Class, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) {
      super(param1Class, param1ArrayOfString);
      this.getterConverters = param1ArrayOfMXBeanMapping;
    }
    
    String applicable(Method[] param1ArrayOfMethod) throws InvalidObjectException {
      for (byte b = 0; b < param1ArrayOfMethod.length; b++) {
        try {
          this.getterConverters[b].checkReconstructible();
        } catch (InvalidObjectException invalidObjectException) {
          this.possibleCause = invalidObjectException;
          return "method " + param1ArrayOfMethod[b].getName() + " returns type that cannot be mapped back from OpenData";
        } 
      } 
      return "";
    }
    
    Throwable possibleCause() { return this.possibleCause; }
    
    final Object fromCompositeData(CompositeData param1CompositeData, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) throws InvalidObjectException { throw new Error(); }
  }
  
  private static final class CompositeBuilderViaConstructor extends CompositeBuilder {
    private List<Constr> annotatedConstructors;
    
    CompositeBuilderViaConstructor(Class<?> param1Class, String[] param1ArrayOfString) { super(param1Class, param1ArrayOfString); }
    
    String applicable(Method[] param1ArrayOfMethod) throws InvalidObjectException {
      if (!AnnotationHelper.isAvailable())
        return "@ConstructorProperties annotation not available"; 
      Class clazz = getTargetClass();
      Constructor[] arrayOfConstructor = clazz.getConstructors();
      List list = Util.newList();
      for (Constructor constructor : arrayOfConstructor) {
        if (Modifier.isPublic(constructor.getModifiers()) && AnnotationHelper.getPropertyNames(constructor) != null)
          list.add(constructor); 
      } 
      if (list.isEmpty())
        return "no constructor has @ConstructorProperties annotation"; 
      this.annotatedConstructors = Util.newList();
      Map map = Util.newMap();
      String[] arrayOfString = getItemNames();
      for (byte b = 0; b < arrayOfString.length; b++)
        map.put(arrayOfString[b], Integer.valueOf(b)); 
      Set set = Util.newSet();
      for (Constructor constructor : list) {
        String[] arrayOfString1 = AnnotationHelper.getPropertyNames(constructor);
        Type[] arrayOfType = constructor.getGenericParameterTypes();
        if (arrayOfType.length != arrayOfString1.length) {
          String str = "Number of constructor params does not match @ConstructorProperties annotation: " + constructor;
          throw new InvalidObjectException(str);
        } 
        int[] arrayOfInt = new int[param1ArrayOfMethod.length];
        for (byte b1 = 0; b1 < param1ArrayOfMethod.length; b1++)
          arrayOfInt[b1] = -1; 
        BitSet bitSet = new BitSet();
        for (byte b2 = 0; b2 < arrayOfString1.length; b2++) {
          String str = arrayOfString1[b2];
          if (!map.containsKey(str)) {
            String str1 = "@ConstructorProperties includes name " + str + " which does not correspond to a property";
            for (String str2 : map.keySet()) {
              if (str2.equalsIgnoreCase(str))
                str1 = str1 + " (differs only in case from property " + str2 + ")"; 
            } 
            str1 = str1 + ": " + constructor;
            throw new InvalidObjectException(str1);
          } 
          int i = ((Integer)map.get(str)).intValue();
          arrayOfInt[i] = b2;
          if (bitSet.get(i)) {
            String str1 = "@ConstructorProperties contains property " + str + " more than once: " + constructor;
            throw new InvalidObjectException(str1);
          } 
          bitSet.set(i);
          Method method = param1ArrayOfMethod[i];
          Type type = method.getGenericReturnType();
          if (!type.equals(arrayOfType[b2])) {
            String str1 = "@ConstructorProperties gives property " + str + " of type " + type + " for parameter  of type " + arrayOfType[b2] + ": " + constructor;
            throw new InvalidObjectException(str1);
          } 
        } 
        if (!set.add(bitSet)) {
          String str = "More than one constructor has a @ConstructorProperties annotation with this set of names: " + Arrays.toString(arrayOfString1);
          throw new InvalidObjectException(str);
        } 
        Constr constr = new Constr(constructor, arrayOfInt, bitSet);
        this.annotatedConstructors.add(constr);
      } 
      for (BitSet bitSet : set) {
        boolean bool = false;
        for (BitSet bitSet1 : set) {
          if (bitSet == bitSet1) {
            bool = true;
            continue;
          } 
          if (bool) {
            BitSet bitSet2 = new BitSet();
            bitSet2.or(bitSet);
            bitSet2.or(bitSet1);
            if (!set.contains(bitSet2)) {
              TreeSet treeSet = new TreeSet();
              int i;
              for (i = bitSet2.nextSetBit(0); i >= 0; i = bitSet2.nextSetBit(i + 1))
                treeSet.add(arrayOfString[i]); 
              String str = "Constructors with @ConstructorProperties annotation  would be ambiguous for these items: " + treeSet;
              throw new InvalidObjectException(str);
            } 
          } 
        } 
      } 
      return null;
    }
    
    final Object fromCompositeData(CompositeData param1CompositeData, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) throws InvalidObjectException {
      CompositeType compositeType = param1CompositeData.getCompositeType();
      BitSet bitSet = new BitSet();
      for (byte b1 = 0; b1 < param1ArrayOfString.length; b1++) {
        if (compositeType.getType(param1ArrayOfString[b1]) != null)
          bitSet.set(b1); 
      } 
      Constr constr = null;
      for (Constr constr1 : this.annotatedConstructors) {
        if (subset(constr1.presentParams, bitSet) && (constr == null || subset(constr.presentParams, constr1.presentParams)))
          constr = constr1; 
      } 
      if (constr == null) {
        String str = "No constructor has a @ConstructorProperties for this set of items: " + compositeType.keySet();
        throw new InvalidObjectException(str);
      } 
      Object[] arrayOfObject = new Object[constr.presentParams.cardinality()];
      for (b2 = 0; b2 < param1ArrayOfString.length; b2++) {
        if (constr.presentParams.get(b2)) {
          Object object1 = param1CompositeData.get(param1ArrayOfString[b2]);
          Object object2 = param1ArrayOfMXBeanMapping[b2].fromOpenValue(object1);
          int i = constr.paramIndexes[b2];
          if (i >= 0)
            arrayOfObject[i] = object2; 
        } 
      } 
      try {
        ReflectUtil.checkPackageAccess(constr.constructor.getDeclaringClass());
        return constr.constructor.newInstance(arrayOfObject);
      } catch (Exception b2) {
        Exception exception;
        String str = "Exception constructing " + getTargetClass().getName();
        throw DefaultMXBeanMappingFactory.invalidObjectException(str, exception);
      } 
    }
    
    private static boolean subset(BitSet param1BitSet1, BitSet param1BitSet2) {
      BitSet bitSet = (BitSet)param1BitSet1.clone();
      bitSet.andNot(param1BitSet2);
      return bitSet.isEmpty();
    }
    
    static class AnnotationHelper {
      private static Class<? extends Annotation> constructorPropertiesClass;
      
      private static Method valueMethod;
      
      private static void findConstructorPropertiesClass() {
        try {
          constructorPropertiesClass = Class.forName("java.beans.ConstructorProperties", false, DefaultMXBeanMappingFactory.class.getClassLoader());
          valueMethod = constructorPropertiesClass.getMethod("value", new Class[0]);
        } catch (ClassNotFoundException classNotFoundException) {
        
        } catch (NoSuchMethodException noSuchMethodException) {
          throw new InternalError(noSuchMethodException);
        } 
      }
      
      static boolean isAvailable() { return (constructorPropertiesClass != null); }
      
      static String[] getPropertyNames(Constructor<?> param2Constructor) {
        if (!isAvailable())
          return null; 
        Annotation annotation = param2Constructor.getAnnotation(constructorPropertiesClass);
        if (annotation == null)
          return null; 
        try {
          return (String[])valueMethod.invoke(annotation, new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
          throw new InternalError(invocationTargetException);
        } catch (IllegalAccessException illegalAccessException) {
          throw new InternalError(illegalAccessException);
        } 
      }
      
      static  {
        findConstructorPropertiesClass();
      }
    }
    
    private static class Constr {
      final Constructor<?> constructor;
      
      final int[] paramIndexes;
      
      final BitSet presentParams;
      
      Constr(Constructor<?> param2Constructor, int[] param2ArrayOfInt, BitSet param2BitSet) {
        this.constructor = param2Constructor;
        this.paramIndexes = param2ArrayOfInt;
        this.presentParams = param2BitSet;
      }
    }
  }
  
  private static final class CompositeBuilderViaFrom extends CompositeBuilder {
    private Method fromMethod;
    
    CompositeBuilderViaFrom(Class<?> param1Class, String[] param1ArrayOfString) { super(param1Class, param1ArrayOfString); }
    
    String applicable(Method[] param1ArrayOfMethod) throws InvalidObjectException {
      Class clazz = getTargetClass();
      try {
        Method method = clazz.getMethod("from", new Class[] { CompositeData.class });
        if (!Modifier.isStatic(method.getModifiers()))
          throw new InvalidObjectException("Method from(CompositeData) is not static"); 
        if (method.getReturnType() != getTargetClass()) {
          String str = "Method from(CompositeData) returns " + MXBeanIntrospector.typeName(method.getReturnType()) + " not " + MXBeanIntrospector.typeName(clazz);
          throw new InvalidObjectException(str);
        } 
        this.fromMethod = method;
        return null;
      } catch (InvalidObjectException invalidObjectException) {
        throw invalidObjectException;
      } catch (Exception exception) {
        return "no method from(CompositeData)";
      } 
    }
    
    final Object fromCompositeData(CompositeData param1CompositeData, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) throws InvalidObjectException {
      try {
        return MethodUtil.invoke(this.fromMethod, null, new Object[] { param1CompositeData });
      } catch (Exception exception) {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Failed to invoke from(CompositeData)", exception);
      } 
    }
  }
  
  private static final class CompositeBuilderViaProxy extends CompositeBuilder {
    CompositeBuilderViaProxy(Class<?> param1Class, String[] param1ArrayOfString) { super(param1Class, param1ArrayOfString); }
    
    String applicable(Method[] param1ArrayOfMethod) throws InvalidObjectException {
      Class clazz = getTargetClass();
      if (!clazz.isInterface())
        return "not an interface"; 
      Set set = Util.newSet(Arrays.asList(clazz.getMethods()));
      set.removeAll(Arrays.asList(param1ArrayOfMethod));
      String str = null;
      for (Method method : set) {
        String str1 = method.getName();
        Class[] arrayOfClass = method.getParameterTypes();
        try {
          Method method1 = Object.class.getMethod(str1, arrayOfClass);
          if (!Modifier.isPublic(method1.getModifiers()))
            str = str1; 
        } catch (NoSuchMethodException noSuchMethodException) {
          str = str1;
        } 
      } 
      return (str != null) ? ("contains methods other than getters (" + str + ")") : null;
    }
    
    final Object fromCompositeData(CompositeData param1CompositeData, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) throws InvalidObjectException {
      Class clazz = getTargetClass();
      return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new CompositeDataInvocationHandler(param1CompositeData));
    }
  }
  
  private static class CompositeBuilderViaSetters extends CompositeBuilder {
    private Method[] setters;
    
    CompositeBuilderViaSetters(Class<?> param1Class, String[] param1ArrayOfString) { super(param1Class, param1ArrayOfString); }
    
    String applicable(Method[] param1ArrayOfMethod) throws InvalidObjectException {
      try {
        Constructor constructor = getTargetClass().getConstructor(new Class[0]);
      } catch (Exception exception) {
        return "does not have a public no-arg constructor";
      } 
      Method[] arrayOfMethod = new Method[param1ArrayOfMethod.length];
      for (byte b = 0; b < param1ArrayOfMethod.length; b++) {
        Method method2;
        Method method1 = param1ArrayOfMethod[b];
        Class clazz = method1.getReturnType();
        String str1 = DefaultMXBeanMappingFactory.propertyName(method1);
        String str2 = "set" + str1;
        try {
          method2 = getTargetClass().getMethod(str2, new Class[] { clazz });
          if (method2.getReturnType() != void.class)
            throw new Exception(); 
        } catch (Exception exception) {
          return "not all getters have corresponding setters (" + method1 + ")";
        } 
        arrayOfMethod[b] = method2;
      } 
      this.setters = arrayOfMethod;
      return null;
    }
    
    Object fromCompositeData(CompositeData param1CompositeData, String[] param1ArrayOfString, MXBeanMapping[] param1ArrayOfMXBeanMapping) throws InvalidObjectException {
      Object object;
      try {
        Class clazz = getTargetClass();
        ReflectUtil.checkPackageAccess(clazz);
        object = clazz.newInstance();
        for (byte b = 0; b < param1ArrayOfString.length; b++) {
          if (param1CompositeData.containsKey(param1ArrayOfString[b])) {
            Object object1 = param1CompositeData.get(param1ArrayOfString[b]);
            Object object2 = param1ArrayOfMXBeanMapping[b].fromOpenValue(object1);
            MethodUtil.invoke(this.setters[b], object, new Object[] { object2 });
          } 
        } 
      } catch (Exception exception) {
        throw DefaultMXBeanMappingFactory.invalidObjectException(exception);
      } 
      return object;
    }
  }
  
  private final class CompositeMapping extends NonNullMXBeanMapping {
    private final String[] itemNames;
    
    private final Method[] getters;
    
    private final MXBeanMapping[] getterMappings;
    
    private DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder;
    
    CompositeMapping(Class<?> param1Class, CompositeType param1CompositeType, String[] param1ArrayOfString, Method[] param1ArrayOfMethod, MXBeanMappingFactory param1MXBeanMappingFactory) throws OpenDataException {
      super(param1Class, param1CompositeType);
      assert param1ArrayOfString.length == param1ArrayOfMethod.length;
      this.itemNames = param1ArrayOfString;
      this.getters = param1ArrayOfMethod;
      this.getterMappings = new MXBeanMapping[param1ArrayOfMethod.length];
      for (byte b = 0; b < param1ArrayOfMethod.length; b++) {
        Type type = param1ArrayOfMethod[b].getGenericReturnType();
        this.getterMappings[b] = param1MXBeanMappingFactory.mappingForType(type, param1MXBeanMappingFactory);
      } 
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      CompositeType compositeType = (CompositeType)getOpenType();
      if (param1Object instanceof CompositeDataView)
        return ((CompositeDataView)param1Object).toCompositeData(compositeType); 
      if (param1Object == null)
        return null; 
      Object[] arrayOfObject = new Object[this.getters.length];
      for (byte b = 0; b < this.getters.length; b++) {
        try {
          Object object = MethodUtil.invoke(this.getters[b], param1Object, (Object[])null);
          arrayOfObject[b] = this.getterMappings[b].toOpenValue(object);
        } catch (Exception exception) {
          throw DefaultMXBeanMappingFactory.openDataException("Error calling getter for " + this.itemNames[b] + ": " + exception, exception);
        } 
      } 
      return new CompositeDataSupport(compositeType, this.itemNames, arrayOfObject);
    }
    
    private void makeCompositeBuilder() {
      if (this.compositeBuilder != null)
        return; 
      Class clazz = (Class)getJavaType();
      CompositeBuilder[][] arrayOfCompositeBuilder = { { new DefaultMXBeanMappingFactory.CompositeBuilderViaFrom(clazz, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor(clazz, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderCheckGetters(clazz, this.itemNames, this.getterMappings), new DefaultMXBeanMappingFactory.CompositeBuilderViaSetters(clazz, this.itemNames), new DefaultMXBeanMappingFactory.CompositeBuilderViaProxy(clazz, this.itemNames) } };
      CompositeBuilder compositeBuilder1 = null;
      StringBuilder stringBuilder = new StringBuilder();
      Throwable throwable = null;
      label33: for (CompositeBuilder[] arrayOfCompositeBuilder1 : arrayOfCompositeBuilder) {
        for (byte b = 0; b < arrayOfCompositeBuilder1.length; b++) {
          CompositeBuilder compositeBuilder2 = arrayOfCompositeBuilder1[b];
          String str = compositeBuilder2.applicable(this.getters);
          if (str == null) {
            compositeBuilder1 = compositeBuilder2;
            break label33;
          } 
          Throwable throwable1 = compositeBuilder2.possibleCause();
          if (throwable1 != null)
            throwable = throwable1; 
          if (str.length() > 0) {
            if (stringBuilder.length() > 0)
              stringBuilder.append("; "); 
            stringBuilder.append(str);
            if (!b)
              break; 
          } 
        } 
      } 
      if (compositeBuilder1 == null) {
        String str = "Do not know how to make a " + clazz.getName() + " from a CompositeData: " + stringBuilder;
        if (throwable != null)
          str = str + ". Remaining exceptions show a POSSIBLE cause."; 
        throw DefaultMXBeanMappingFactory.invalidObjectException(str, throwable);
      } 
      this.compositeBuilder = compositeBuilder1;
    }
    
    public void checkReconstructible() { makeCompositeBuilder(); }
    
    final Object fromNonNullOpenValue(Object param1Object) throws OpenDataException {
      makeCompositeBuilder();
      return this.compositeBuilder.fromCompositeData((CompositeData)param1Object, this.itemNames, this.getterMappings);
    }
  }
  
  private static final class EnumMapping<T extends Enum<T>> extends NonNullMXBeanMapping {
    private final Class<T> enumClass;
    
    EnumMapping(Class<T> param1Class) {
      super(param1Class, SimpleType.STRING);
      this.enumClass = param1Class;
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException { return ((Enum)param1Object).name(); }
    
    final T fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      try {
        return (T)Enum.valueOf(this.enumClass, (String)param1Object);
      } catch (Exception exception) {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot convert to enum: " + param1Object, exception);
      } 
    }
  }
  
  private static final class IdentityMapping extends NonNullMXBeanMapping {
    IdentityMapping(Type param1Type, OpenType<?> param1OpenType) { super(param1Type, param1OpenType); }
    
    boolean isIdentity() { return true; }
    
    Object fromNonNullOpenValue(Object param1Object) throws OpenDataException { return param1Object; }
    
    Object toNonNullOpenValue(Object param1Object) throws OpenDataException { return param1Object; }
  }
  
  private static final class MXBeanRefMapping extends NonNullMXBeanMapping {
    MXBeanRefMapping(Type param1Type) { super(param1Type, SimpleType.OBJECTNAME); }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      MXBeanLookup mXBeanLookup = lookupNotNull(OpenDataException.class);
      ObjectName objectName = mXBeanLookup.mxbeanToObjectName(param1Object);
      if (objectName == null)
        throw new OpenDataException("No name for object: " + param1Object); 
      return objectName;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws OpenDataException {
      MXBeanLookup mXBeanLookup = lookupNotNull(InvalidObjectException.class);
      ObjectName objectName = (ObjectName)param1Object;
      Object object = mXBeanLookup.objectNameToMXBean(objectName, (Class)getJavaType());
      if (object == null) {
        String str = "No MXBean for name: " + objectName;
        throw new InvalidObjectException(str);
      } 
      return object;
    }
    
    private <T extends Exception> MXBeanLookup lookupNotNull(Class<T> param1Class) throws T {
      MXBeanLookup mXBeanLookup = MXBeanLookup.getLookup();
      if (mXBeanLookup == null) {
        Exception exception;
        try {
          Constructor constructor = param1Class.getConstructor(new Class[] { String.class });
          exception = (Exception)constructor.newInstance(new Object[] { "Cannot convert MXBean interface in this context" });
        } catch (Exception exception1) {
          throw new RuntimeException(exception1);
        } 
        throw exception;
      } 
      return mXBeanLookup;
    }
  }
  
  private static final class Mappings extends WeakHashMap<Type, WeakReference<MXBeanMapping>> {
    private Mappings() {}
  }
  
  static abstract class NonNullMXBeanMapping extends MXBeanMapping {
    NonNullMXBeanMapping(Type param1Type, OpenType<?> param1OpenType) { super(param1Type, param1OpenType); }
    
    public final Object fromOpenValue(Object param1Object) throws OpenDataException { return (param1Object == null) ? null : fromNonNullOpenValue(param1Object); }
    
    public final Object toOpenValue(Object param1Object) throws OpenDataException { return (param1Object == null) ? null : toNonNullOpenValue(param1Object); }
    
    abstract Object fromNonNullOpenValue(Object param1Object) throws OpenDataException;
    
    abstract Object toNonNullOpenValue(Object param1Object) throws OpenDataException;
    
    boolean isIdentity() { return false; }
  }
  
  private static final class TabularMapping extends NonNullMXBeanMapping {
    private final boolean sortedMap;
    
    private final MXBeanMapping keyMapping;
    
    private final MXBeanMapping valueMapping;
    
    TabularMapping(Type param1Type, boolean param1Boolean, TabularType param1TabularType, MXBeanMapping param1MXBeanMapping1, MXBeanMapping param1MXBeanMapping2) {
      super(param1Type, param1TabularType);
      this.sortedMap = param1Boolean;
      this.keyMapping = param1MXBeanMapping1;
      this.valueMapping = param1MXBeanMapping2;
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      Map map = (Map)Util.cast(param1Object);
      if (map instanceof SortedMap) {
        Comparator comparator = ((SortedMap)map).comparator();
        if (comparator != null) {
          String str = "Cannot convert SortedMap with non-null comparator: " + comparator;
          throw DefaultMXBeanMappingFactory.openDataException(str, new IllegalArgumentException(str));
        } 
      } 
      TabularType tabularType = (TabularType)getOpenType();
      TabularDataSupport tabularDataSupport = new TabularDataSupport(tabularType);
      CompositeType compositeType = tabularType.getRowType();
      for (Map.Entry entry : map.entrySet()) {
        Object object1 = this.keyMapping.toOpenValue(entry.getKey());
        Object object2 = this.valueMapping.toOpenValue(entry.getValue());
        CompositeDataSupport compositeDataSupport = new CompositeDataSupport(compositeType, keyValueArray, new Object[] { object1, object2 });
        tabularDataSupport.put(compositeDataSupport);
      } 
      return tabularDataSupport;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws OpenDataException {
      TabularData tabularData = (TabularData)param1Object;
      Collection collection = (Collection)Util.cast(tabularData.values());
      SortedMap sortedMap1 = this.sortedMap ? Util.newSortedMap() : Util.newInsertionOrderMap();
      for (CompositeData compositeData : collection) {
        Object object1 = this.keyMapping.fromOpenValue(compositeData.get("key"));
        Object object2 = this.valueMapping.fromOpenValue(compositeData.get("value"));
        if (sortedMap1.put(object1, object2) != null) {
          String str = "Duplicate entry in TabularData: key=" + object1;
          throw new InvalidObjectException(str);
        } 
      } 
      return sortedMap1;
    }
    
    public void checkReconstructible() {
      this.keyMapping.checkReconstructible();
      this.valueMapping.checkReconstructible();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\DefaultMXBeanMappingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */