package sun.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import sun.misc.VM;

public class Reflection {
  @CallerSensitive
  public static native Class<?> getCallerClass();
  
  @Deprecated
  public static native Class<?> getCallerClass(int paramInt);
  
  public static native int getClassAccessFlags(Class<?> paramClass);
  
  public static boolean quickCheckMemberAccess(Class<?> paramClass, int paramInt) { return Modifier.isPublic(getClassAccessFlags(paramClass) & paramInt); }
  
  public static void ensureMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt) throws IllegalAccessException {
    if (paramClass1 == null || paramClass2 == null)
      throw new InternalError(); 
    if (!verifyMemberAccess(paramClass1, paramClass2, paramObject, paramInt))
      throw new IllegalAccessException("Class " + paramClass1.getName() + " can not access a member of class " + paramClass2.getName() + " with modifiers \"" + Modifier.toString(paramInt) + "\""); 
  }
  
  public static boolean verifyMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt) {
    boolean bool1 = false;
    boolean bool = false;
    if (paramClass1 == paramClass2)
      return true; 
    if (!Modifier.isPublic(getClassAccessFlags(paramClass2))) {
      bool = isSameClassPackage(paramClass1, paramClass2);
      bool1 = true;
      if (!bool)
        return false; 
    } 
    if (Modifier.isPublic(paramInt))
      return true; 
    boolean bool2 = false;
    if (Modifier.isProtected(paramInt) && isSubclassOf(paramClass1, paramClass2))
      bool2 = true; 
    if (!bool2 && !Modifier.isPrivate(paramInt)) {
      if (!bool1) {
        bool = isSameClassPackage(paramClass1, paramClass2);
        bool1 = true;
      } 
      if (bool)
        bool2 = true; 
    } 
    if (!bool2)
      return false; 
    if (Modifier.isProtected(paramInt)) {
      Class<?> clazz = (paramObject == null) ? paramClass2 : paramObject.getClass();
      if (clazz != paramClass1) {
        if (!bool1) {
          bool = isSameClassPackage(paramClass1, paramClass2);
          bool1 = true;
        } 
        if (!bool && !isSubclassOf(clazz, paramClass1))
          return false; 
      } 
    } 
    return true;
  }
  
  private static boolean isSameClassPackage(Class<?> paramClass1, Class<?> paramClass2) { return isSameClassPackage(paramClass1.getClassLoader(), paramClass1.getName(), paramClass2.getClassLoader(), paramClass2.getName()); }
  
  private static boolean isSameClassPackage(ClassLoader paramClassLoader1, String paramString1, ClassLoader paramClassLoader2, String paramString2) {
    if (paramClassLoader1 != paramClassLoader2)
      return false; 
    int i = paramString1.lastIndexOf('.');
    int j = paramString2.lastIndexOf('.');
    if (i == -1 || j == -1)
      return (i == j); 
    int k = 0;
    int m = 0;
    if (paramString1.charAt(k) == '[') {
      do {
        k++;
      } while (paramString1.charAt(k) == '[');
      if (paramString1.charAt(k) != 'L')
        throw new InternalError("Illegal class name " + paramString1); 
    } 
    if (paramString2.charAt(m) == '[') {
      do {
        m++;
      } while (paramString2.charAt(m) == '[');
      if (paramString2.charAt(m) != 'L')
        throw new InternalError("Illegal class name " + paramString2); 
    } 
    int n = i - k;
    int i1 = j - m;
    return (n != i1) ? false : paramString1.regionMatches(false, k, paramString2, m, n);
  }
  
  static boolean isSubclassOf(Class<?> paramClass1, Class<?> paramClass2) {
    while (paramClass1 != null) {
      if (paramClass1 == paramClass2)
        return true; 
      paramClass1 = paramClass1.getSuperclass();
    } 
    return false;
  }
  
  public static void registerFieldsToFilter(Class<?> paramClass, String... paramVarArgs) { fieldFilterMap = registerFilter(fieldFilterMap, paramClass, paramVarArgs); }
  
  public static void registerMethodsToFilter(Class<?> paramClass, String... paramVarArgs) { methodFilterMap = registerFilter(methodFilterMap, paramClass, paramVarArgs); }
  
  private static Map<Class<?>, String[]> registerFilter(Map<Class<?>, String[]> paramMap, Class<?> paramClass, String... paramVarArgs) {
    if (paramMap.get(paramClass) != null)
      throw new IllegalArgumentException("Filter already registered: " + paramClass); 
    paramMap = new HashMap<Class<?>, String[]>(paramMap);
    paramMap.put(paramClass, paramVarArgs);
    return paramMap;
  }
  
  public static Field[] filterFields(Class<?> paramClass, Field[] paramArrayOfField) { return (fieldFilterMap == null) ? paramArrayOfField : (Field[])filter(paramArrayOfField, (String[])fieldFilterMap.get(paramClass)); }
  
  public static Method[] filterMethods(Class<?> paramClass, Method[] paramArrayOfMethod) { return (methodFilterMap == null) ? paramArrayOfMethod : (Method[])filter(paramArrayOfMethod, (String[])methodFilterMap.get(paramClass)); }
  
  private static Member[] filter(Member[] paramArrayOfMember, String[] paramArrayOfString) {
    if (paramArrayOfString == null || paramArrayOfMember.length == 0)
      return paramArrayOfMember; 
    byte b1 = 0;
    for (Member member : paramArrayOfMember) {
      boolean bool = false;
      for (String str : paramArrayOfString) {
        if (member.getName() == str) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        b1++; 
    } 
    Member[] arrayOfMember = (Member[])Array.newInstance(paramArrayOfMember[0].getClass(), b1);
    byte b2 = 0;
    for (Member member : paramArrayOfMember) {
      boolean bool = false;
      for (String str : paramArrayOfString) {
        if (member.getName() == str) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        arrayOfMember[b2++] = member; 
    } 
    return arrayOfMember;
  }
  
  public static boolean isCallerSensitive(Method paramMethod) {
    ClassLoader classLoader = paramMethod.getDeclaringClass().getClassLoader();
    return (VM.isSystemDomainLoader(classLoader) || isExtClassLoader(classLoader)) ? paramMethod.isAnnotationPresent(CallerSensitive.class) : 0;
  }
  
  private static boolean isExtClassLoader(ClassLoader paramClassLoader) {
    for (ClassLoader classLoader = ClassLoader.getSystemClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
      if (classLoader.getParent() == null && classLoader == paramClassLoader)
        return true; 
    } 
    return false;
  }
  
  static  {
    HashMap hashMap = new HashMap();
    hashMap.put(Reflection.class, new String[] { "fieldFilterMap", "methodFilterMap" });
    hashMap.put(System.class, new String[] { "security" });
    hashMap.put(Class.class, new String[] { "classLoader" });
    fieldFilterMap = hashMap;
    methodFilterMap = new HashMap();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\Reflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */