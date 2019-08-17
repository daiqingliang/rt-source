package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BytecodeDescriptor {
  public static List<Class<?>> parseMethod(String paramString, ClassLoader paramClassLoader) { return parseMethod(paramString, 0, paramString.length(), paramClassLoader); }
  
  static List<Class<?>> parseMethod(String paramString, int paramInt1, int paramInt2, ClassLoader paramClassLoader) {
    if (paramClassLoader == null)
      paramClassLoader = ClassLoader.getSystemClassLoader(); 
    String str = paramString;
    int[] arrayOfInt = { paramInt1 };
    ArrayList arrayList = new ArrayList();
    if (arrayOfInt[0] < paramInt2 && str.charAt(arrayOfInt[0]) == '(') {
      arrayOfInt[0] = arrayOfInt[0] + 1;
      while (arrayOfInt[0] < paramInt2 && str.charAt(arrayOfInt[0]) != ')') {
        Class clazz1 = parseSig(str, arrayOfInt, paramInt2, paramClassLoader);
        if (clazz1 == null || clazz1 == void.class)
          parseError(str, "bad argument type"); 
        arrayList.add(clazz1);
      } 
      arrayOfInt[0] = arrayOfInt[0] + 1;
    } else {
      parseError(str, "not a method type");
    } 
    Class clazz = parseSig(str, arrayOfInt, paramInt2, paramClassLoader);
    if (clazz == null || arrayOfInt[0] != paramInt2)
      parseError(str, "bad return type"); 
    arrayList.add(clazz);
    return arrayList;
  }
  
  private static void parseError(String paramString1, String paramString2) { throw new IllegalArgumentException("bad signature: " + paramString1 + ": " + paramString2); }
  
  private static Class<?> parseSig(String paramString, int[] paramArrayOfInt, int paramInt, ClassLoader paramClassLoader) {
    if (paramArrayOfInt[0] == paramInt)
      return null; 
    paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
    char c = paramString.charAt(paramArrayOfInt[0]);
    if (c == 'L') {
      int i = paramArrayOfInt[0];
      int j = paramString.indexOf(';', i);
      if (j < 0)
        return null; 
      paramArrayOfInt[0] = j + 1;
      String str = paramString.substring(i, j).replace('/', '.');
      try {
        return paramClassLoader.loadClass(str);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new TypeNotPresentException(str, classNotFoundException);
      } 
    } 
    if (c == '[') {
      Class clazz = parseSig(paramString, paramArrayOfInt, paramInt, paramClassLoader);
      if (clazz != null)
        clazz = Array.newInstance(clazz, 0).getClass(); 
      return clazz;
    } 
    return Wrapper.forBasicType(c).primitiveType();
  }
  
  public static String unparse(Class<?> paramClass) {
    StringBuilder stringBuilder = new StringBuilder();
    unparseSig(paramClass, stringBuilder);
    return stringBuilder.toString();
  }
  
  public static String unparse(MethodType paramMethodType) { return unparseMethod(paramMethodType.returnType(), paramMethodType.parameterList()); }
  
  public static String unparse(Object paramObject) { return (paramObject instanceof Class) ? unparse((Class)paramObject) : ((paramObject instanceof MethodType) ? unparse((MethodType)paramObject) : (String)paramObject); }
  
  public static String unparseMethod(Class<?> paramClass, List<Class<?>> paramList) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('(');
    for (Class clazz : paramList)
      unparseSig(clazz, stringBuilder); 
    stringBuilder.append(')');
    unparseSig(paramClass, stringBuilder);
    return stringBuilder.toString();
  }
  
  private static void unparseSig(Class<?> paramClass, StringBuilder paramStringBuilder) {
    char c = Wrapper.forBasicType(paramClass).basicTypeChar();
    if (c != 'L') {
      paramStringBuilder.append(c);
    } else {
      boolean bool = !paramClass.isArray() ? 1 : 0;
      if (bool)
        paramStringBuilder.append('L'); 
      paramStringBuilder.append(paramClass.getName().replace('.', '/'));
      if (bool)
        paramStringBuilder.append(';'); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\invok\\util\BytecodeDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */