package sun.reflect.annotation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import sun.misc.Unsafe;

class AnnotationInvocationHandler implements InvocationHandler, Serializable {
  private static final long serialVersionUID = 6182022883658399397L;
  
  private final Class<? extends Annotation> type;
  
  private final Map<String, Object> memberValues;
  
  AnnotationInvocationHandler(Class<? extends Annotation> paramClass, Map<String, Object> paramMap) {
    Class[] arrayOfClass = paramClass.getInterfaces();
    if (!paramClass.isAnnotation() || arrayOfClass.length != 1 || arrayOfClass[false] != Annotation.class)
      throw new AnnotationFormatError("Attempt to create proxy for a non-annotation type."); 
    this.type = paramClass;
    this.memberValues = paramMap;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    if (str.equals("equals") && arrayOfClass.length == 1 && arrayOfClass[false] == Object.class)
      return equalsImpl(paramArrayOfObject[0]); 
    if (arrayOfClass.length != 0)
      throw new AssertionError("Too many parameters for an annotation method"); 
    switch (str) {
      case "toString":
        return toStringImpl();
      case "hashCode":
        return Integer.valueOf(hashCodeImpl());
      case "annotationType":
        return this.type;
    } 
    Object object = this.memberValues.get(str);
    if (object == null)
      throw new IncompleteAnnotationException(this.type, str); 
    if (object instanceof ExceptionProxy)
      throw ((ExceptionProxy)object).generateException(); 
    if (object.getClass().isArray() && Array.getLength(object) != 0)
      object = cloneArray(object); 
    return object;
  }
  
  private Object cloneArray(Object paramObject) {
    Class clazz = paramObject.getClass();
    if (clazz == byte[].class) {
      byte[] arrayOfByte = (byte[])paramObject;
      return arrayOfByte.clone();
    } 
    if (clazz == char[].class) {
      char[] arrayOfChar = (char[])paramObject;
      return arrayOfChar.clone();
    } 
    if (clazz == double[].class) {
      double[] arrayOfDouble = (double[])paramObject;
      return arrayOfDouble.clone();
    } 
    if (clazz == float[].class) {
      float[] arrayOfFloat = (float[])paramObject;
      return arrayOfFloat.clone();
    } 
    if (clazz == int[].class) {
      int[] arrayOfInt = (int[])paramObject;
      return arrayOfInt.clone();
    } 
    if (clazz == long[].class) {
      long[] arrayOfLong = (long[])paramObject;
      return arrayOfLong.clone();
    } 
    if (clazz == short[].class) {
      short[] arrayOfShort = (short[])paramObject;
      return arrayOfShort.clone();
    } 
    if (clazz == boolean[].class) {
      boolean[] arrayOfBoolean = (boolean[])paramObject;
      return arrayOfBoolean.clone();
    } 
    Object[] arrayOfObject = (Object[])paramObject;
    return arrayOfObject.clone();
  }
  
  private String toStringImpl() {
    StringBuilder stringBuilder = new StringBuilder(128);
    stringBuilder.append('@');
    stringBuilder.append(this.type.getName());
    stringBuilder.append('(');
    boolean bool = true;
    for (Map.Entry entry : this.memberValues.entrySet()) {
      if (bool) {
        bool = false;
      } else {
        stringBuilder.append(", ");
      } 
      stringBuilder.append((String)entry.getKey());
      stringBuilder.append('=');
      stringBuilder.append(memberValueToString(entry.getValue()));
    } 
    stringBuilder.append(')');
    return stringBuilder.toString();
  }
  
  private static String memberValueToString(Object paramObject) {
    Class clazz = paramObject.getClass();
    return !clazz.isArray() ? paramObject.toString() : ((clazz == byte[].class) ? Arrays.toString((byte[])paramObject) : ((clazz == char[].class) ? Arrays.toString((char[])paramObject) : ((clazz == double[].class) ? Arrays.toString((double[])paramObject) : ((clazz == float[].class) ? Arrays.toString((float[])paramObject) : ((clazz == int[].class) ? Arrays.toString((int[])paramObject) : ((clazz == long[].class) ? Arrays.toString((long[])paramObject) : ((clazz == short[].class) ? Arrays.toString((short[])paramObject) : ((clazz == boolean[].class) ? Arrays.toString((boolean[])paramObject) : Arrays.toString((Object[])paramObject)))))))));
  }
  
  private Boolean equalsImpl(Object paramObject) {
    if (paramObject == this)
      return Boolean.valueOf(true); 
    if (!this.type.isInstance(paramObject))
      return Boolean.valueOf(false); 
    for (Method method : getMemberMethods()) {
      String str = method.getName();
      Object object1 = this.memberValues.get(str);
      Object object2 = null;
      AnnotationInvocationHandler annotationInvocationHandler = asOneOfUs(paramObject);
      if (annotationInvocationHandler != null) {
        object2 = annotationInvocationHandler.memberValues.get(str);
      } else {
        try {
          object2 = method.invoke(paramObject, new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
          return Boolean.valueOf(false);
        } catch (IllegalAccessException illegalAccessException) {
          throw new AssertionError(illegalAccessException);
        } 
      } 
      if (!memberValueEquals(object1, object2))
        return Boolean.valueOf(false); 
    } 
    return Boolean.valueOf(true);
  }
  
  private AnnotationInvocationHandler asOneOfUs(Object paramObject) {
    if (Proxy.isProxyClass(paramObject.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject);
      if (invocationHandler instanceof AnnotationInvocationHandler)
        return (AnnotationInvocationHandler)invocationHandler; 
    } 
    return null;
  }
  
  private static boolean memberValueEquals(Object paramObject1, Object paramObject2) {
    Class clazz = paramObject1.getClass();
    if (!clazz.isArray())
      return paramObject1.equals(paramObject2); 
    if (paramObject1 instanceof Object[] && paramObject2 instanceof Object[])
      return Arrays.equals((Object[])paramObject1, (Object[])paramObject2); 
    if (paramObject2.getClass() != clazz)
      return false; 
    if (clazz == byte[].class)
      return Arrays.equals((byte[])paramObject1, (byte[])paramObject2); 
    if (clazz == char[].class)
      return Arrays.equals((char[])paramObject1, (char[])paramObject2); 
    if (clazz == double[].class)
      return Arrays.equals((double[])paramObject1, (double[])paramObject2); 
    if (clazz == float[].class)
      return Arrays.equals((float[])paramObject1, (float[])paramObject2); 
    if (clazz == int[].class)
      return Arrays.equals((int[])paramObject1, (int[])paramObject2); 
    if (clazz == long[].class)
      return Arrays.equals((long[])paramObject1, (long[])paramObject2); 
    if (clazz == short[].class)
      return Arrays.equals((short[])paramObject1, (short[])paramObject2); 
    assert clazz == boolean[].class;
    return Arrays.equals((boolean[])paramObject1, (boolean[])paramObject2);
  }
  
  private Method[] getMemberMethods() {
    if (this.memberMethods == null)
      this.memberMethods = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
            public Method[] run() {
              Method[] arrayOfMethod = AnnotationInvocationHandler.this.type.getDeclaredMethods();
              AnnotationInvocationHandler.this.validateAnnotationMethods(arrayOfMethod);
              AccessibleObject.setAccessible(arrayOfMethod, true);
              return arrayOfMethod;
            }
          }); 
    return this.memberMethods;
  }
  
  private void validateAnnotationMethods(Method[] paramArrayOfMethod) {
    boolean bool = true;
    for (Method method : paramArrayOfMethod) {
      if (method.getModifiers() != 1025 || method.isDefault() || method.getParameterCount() != 0 || method.getExceptionTypes().length != 0) {
        bool = false;
        break;
      } 
      Class clazz = method.getReturnType();
      if (clazz.isArray()) {
        clazz = clazz.getComponentType();
        if (clazz.isArray()) {
          bool = false;
          break;
        } 
      } 
      if ((!clazz.isPrimitive() || clazz == void.class) && clazz != String.class && clazz != Class.class && !clazz.isEnum() && !clazz.isAnnotation()) {
        bool = false;
        break;
      } 
      String str = method.getName();
      if ((str.equals("toString") && clazz == String.class) || (str.equals("hashCode") && clazz == int.class) || (str.equals("annotationType") && clazz == Class.class)) {
        bool = false;
        break;
      } 
    } 
    if (bool)
      return; 
    throw new AnnotationFormatError("Malformed method on an annotation type");
  }
  
  private int hashCodeImpl() {
    int i = 0;
    for (Map.Entry entry : this.memberValues.entrySet())
      i += (127 * ((String)entry.getKey()).hashCode() ^ memberValueHashCode(entry.getValue())); 
    return i;
  }
  
  private static int memberValueHashCode(Object paramObject) {
    Class clazz = paramObject.getClass();
    return !clazz.isArray() ? paramObject.hashCode() : ((clazz == byte[].class) ? Arrays.hashCode((byte[])paramObject) : ((clazz == char[].class) ? Arrays.hashCode((char[])paramObject) : ((clazz == double[].class) ? Arrays.hashCode((double[])paramObject) : ((clazz == float[].class) ? Arrays.hashCode((float[])paramObject) : ((clazz == int[].class) ? Arrays.hashCode((int[])paramObject) : ((clazz == long[].class) ? Arrays.hashCode((long[])paramObject) : ((clazz == short[].class) ? Arrays.hashCode((short[])paramObject) : ((clazz == boolean[].class) ? Arrays.hashCode((boolean[])paramObject) : Arrays.hashCode((Object[])paramObject)))))))));
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Class clazz = (Class)getField.get("type", null);
    Map map1 = (Map)getField.get("memberValues", null);
    AnnotationType annotationType = null;
    try {
      annotationType = AnnotationType.getInstance(clazz);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException("Non-annotation type in annotation serial stream");
    } 
    Map map2 = annotationType.memberTypes();
    LinkedHashMap linkedHashMap = new LinkedHashMap();
    for (Map.Entry entry : map1.entrySet()) {
      String str = (String)entry.getKey();
      Object object = null;
      Class clazz1 = (Class)map2.get(str);
      if (clazz1 != null) {
        object = entry.getValue();
        if (!clazz1.isInstance(object) && !(object instanceof ExceptionProxy))
          object = (new AnnotationTypeMismatchExceptionProxy(object.getClass() + "[" + object + "]")).setMember((Method)annotationType.members().get(str)); 
      } 
      linkedHashMap.put(str, object);
    } 
    UnsafeAccessor.setType(this, clazz);
    UnsafeAccessor.setMemberValues(this, linkedHashMap);
  }
  
  private static class UnsafeAccessor {
    private static final Unsafe unsafe;
    
    private static final long typeOffset;
    
    private static final long memberValuesOffset;
    
    static void setType(AnnotationInvocationHandler param1AnnotationInvocationHandler, Class<? extends Annotation> param1Class) { unsafe.putObject(param1AnnotationInvocationHandler, typeOffset, param1Class); }
    
    static void setMemberValues(AnnotationInvocationHandler param1AnnotationInvocationHandler, Map<String, Object> param1Map) { unsafe.putObject(param1AnnotationInvocationHandler, memberValuesOffset, param1Map); }
    
    static  {
      try {
        unsafe = Unsafe.getUnsafe();
        typeOffset = unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("type"));
        memberValuesOffset = unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("memberValues"));
      } catch (Exception exception) {
        throw new ExceptionInInitializerError(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\AnnotationInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */