package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

public final class ObjectStreamClassUtil_1_3 {
  private static Comparator compareClassByName = new CompareClassByName(null);
  
  private static Comparator compareMemberByName = new CompareMemberByName(null);
  
  private static Method hasStaticInitializerMethod = null;
  
  public static long computeSerialVersionUID(Class paramClass) {
    null = ObjectStreamClass.getSerialVersionUID(paramClass);
    return (null == 0L) ? null : getSerialVersion(null, paramClass).longValue();
  }
  
  private static Long getSerialVersion(final long csuid, final Class cl) { return (Long)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            long l;
            try {
              Field field = cl.getDeclaredField("serialVersionUID");
              int i = field.getModifiers();
              if (Modifier.isStatic(i) && Modifier.isFinal(i) && Modifier.isPrivate(i)) {
                l = csuid;
              } else {
                l = ObjectStreamClassUtil_1_3._computeSerialVersionUID(cl);
              } 
            } catch (NoSuchFieldException noSuchFieldException) {
              l = ObjectStreamClassUtil_1_3._computeSerialVersionUID(cl);
            } 
            return new Long(l);
          }
        }); }
  
  public static long computeStructuralUID(boolean paramBoolean, Class<?> paramClass) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try {
      if (!java.io.Serializable.class.isAssignableFrom(paramClass) || paramClass.isInterface())
        return 0L; 
      if (java.io.Externalizable.class.isAssignableFrom(paramClass))
        return 1L; 
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      DigestOutputStream digestOutputStream = new DigestOutputStream(byteArrayOutputStream, messageDigest);
      DataOutputStream dataOutputStream = new DataOutputStream(digestOutputStream);
      Class clazz = paramClass.getSuperclass();
      if (clazz != null && clazz != Object.class) {
        boolean bool = false;
        Class[] arrayOfClass = { java.io.ObjectOutputStream.class };
        Method method = getDeclaredMethod(clazz, "writeObject", arrayOfClass, 2, 8);
        if (method != null)
          bool = true; 
        dataOutputStream.writeLong(computeStructuralUID(bool, clazz));
      } 
      if (paramBoolean) {
        dataOutputStream.writeInt(2);
      } else {
        dataOutputStream.writeInt(1);
      } 
      Field[] arrayOfField = getDeclaredFields(paramClass);
      Arrays.sort(arrayOfField, compareMemberByName);
      for (byte b = 0; b < arrayOfField.length; b++) {
        Field field = arrayOfField[b];
        int k = field.getModifiers();
        if (!Modifier.isTransient(k) && !Modifier.isStatic(k)) {
          dataOutputStream.writeUTF(field.getName());
          dataOutputStream.writeUTF(getSignature(field.getType()));
        } 
      } 
      dataOutputStream.flush();
      byte[] arrayOfByte = messageDigest.digest();
      int i = Math.min(8, arrayOfByte.length);
      for (int j = i; j > 0; j--)
        l += ((arrayOfByte[j] & 0xFF) << j * 8); 
    } catch (IOException iOException) {
      l = -1L;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SecurityException(noSuchAlgorithmException.getMessage());
    } 
    return l;
  }
  
  private static long _computeSerialVersionUID(Class paramClass) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      DigestOutputStream digestOutputStream = new DigestOutputStream(byteArrayOutputStream, messageDigest);
      DataOutputStream dataOutputStream = new DataOutputStream(digestOutputStream);
      dataOutputStream.writeUTF(paramClass.getName());
      int i = paramClass.getModifiers();
      i &= 0x611;
      Method[] arrayOfMethod = paramClass.getDeclaredMethods();
      if ((i & 0x200) != 0) {
        i &= 0xFFFFFBFF;
        if (arrayOfMethod.length > 0)
          i |= 0x400; 
      } 
      dataOutputStream.writeInt(i);
      if (!paramClass.isArray()) {
        Class[] arrayOfClass = paramClass.getInterfaces();
        Arrays.sort(arrayOfClass, compareClassByName);
        for (byte b = 0; b < arrayOfClass.length; b++)
          dataOutputStream.writeUTF(arrayOfClass[b].getName()); 
      } 
      Field[] arrayOfField = paramClass.getDeclaredFields();
      Arrays.sort(arrayOfField, compareMemberByName);
      for (byte b1 = 0; b1 < arrayOfField.length; b1++) {
        Field field = arrayOfField[b1];
        int j = field.getModifiers();
        if (!Modifier.isPrivate(j) || (!Modifier.isTransient(j) && !Modifier.isStatic(j))) {
          dataOutputStream.writeUTF(field.getName());
          dataOutputStream.writeInt(j);
          dataOutputStream.writeUTF(getSignature(field.getType()));
        } 
      } 
      if (hasStaticInitializer(paramClass)) {
        dataOutputStream.writeUTF("<clinit>");
        dataOutputStream.writeInt(8);
        dataOutputStream.writeUTF("()V");
      } 
      MethodSignature[] arrayOfMethodSignature1 = MethodSignature.removePrivateAndSort(paramClass.getDeclaredConstructors());
      for (byte b2 = 0; b2 < arrayOfMethodSignature1.length; b2++) {
        MethodSignature methodSignature = arrayOfMethodSignature1[b2];
        String str1 = "<init>";
        String str2 = methodSignature.signature;
        str2 = str2.replace('/', '.');
        dataOutputStream.writeUTF(str1);
        dataOutputStream.writeInt(methodSignature.member.getModifiers());
        dataOutputStream.writeUTF(str2);
      } 
      MethodSignature[] arrayOfMethodSignature2 = MethodSignature.removePrivateAndSort(arrayOfMethod);
      for (byte b3 = 0; b3 < arrayOfMethodSignature2.length; b3++) {
        MethodSignature methodSignature = arrayOfMethodSignature2[b3];
        String str = methodSignature.signature;
        str = str.replace('/', '.');
        dataOutputStream.writeUTF(methodSignature.member.getName());
        dataOutputStream.writeInt(methodSignature.member.getModifiers());
        dataOutputStream.writeUTF(str);
      } 
      dataOutputStream.flush();
      byte[] arrayOfByte = messageDigest.digest();
      for (byte b4 = 0; b4 < Math.min(8, arrayOfByte.length); b4++)
        l += ((arrayOfByte[b4] & 0xFF) << b4 * 8); 
    } catch (IOException iOException) {
      l = -1L;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SecurityException(noSuchAlgorithmException.getMessage());
    } 
    return l;
  }
  
  private static String getSignature(Class paramClass) {
    String str = null;
    if (paramClass.isArray()) {
      Class clazz = paramClass;
      byte b1 = 0;
      while (clazz.isArray()) {
        b1++;
        clazz = clazz.getComponentType();
      } 
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b2 = 0; b2 < b1; b2++)
        stringBuffer.append("["); 
      stringBuffer.append(getSignature(clazz));
      str = stringBuffer.toString();
    } else if (paramClass.isPrimitive()) {
      if (paramClass == int.class) {
        str = "I";
      } else if (paramClass == byte.class) {
        str = "B";
      } else if (paramClass == long.class) {
        str = "J";
      } else if (paramClass == float.class) {
        str = "F";
      } else if (paramClass == double.class) {
        str = "D";
      } else if (paramClass == short.class) {
        str = "S";
      } else if (paramClass == char.class) {
        str = "C";
      } else if (paramClass == boolean.class) {
        str = "Z";
      } else if (paramClass == void.class) {
        str = "V";
      } 
    } else {
      str = "L" + paramClass.getName().replace('.', '/') + ";";
    } 
    return str;
  }
  
  private static String getSignature(Method paramMethod) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getSignature(arrayOfClass[b])); 
    stringBuffer.append(")");
    stringBuffer.append(getSignature(paramMethod.getReturnType()));
    return stringBuffer.toString();
  }
  
  private static String getSignature(Constructor paramConstructor) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getSignature(arrayOfClass[b])); 
    stringBuffer.append(")V");
    return stringBuffer.toString();
  }
  
  private static Field[] getDeclaredFields(final Class clz) { return (Field[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return clz.getDeclaredFields(); }
        }); }
  
  private static boolean hasStaticInitializer(Class paramClass) {
    if (hasStaticInitializerMethod == null) {
      Class clazz = null;
      try {
        if (clazz == null)
          clazz = java.io.ObjectStreamClass.class; 
        hasStaticInitializerMethod = clazz.getDeclaredMethod("hasStaticInitializer", new Class[] { Class.class });
      } catch (NoSuchMethodException noSuchMethodException) {}
      if (hasStaticInitializerMethod == null)
        throw new InternalError("Can't find hasStaticInitializer method on " + clazz.getName()); 
      hasStaticInitializerMethod.setAccessible(true);
    } 
    try {
      Boolean bool = (Boolean)hasStaticInitializerMethod.invoke(null, new Object[] { paramClass });
      return bool.booleanValue();
    } catch (Exception exception) {
      throw new InternalError("Error invoking hasStaticInitializer: " + exception);
    } 
  }
  
  private static Method getDeclaredMethod(final Class cl, final String methodName, final Class[] args, final int requiredModifierMask, final int disallowedModifierMask) { return (Method)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Method method = null;
            try {
              method = cl.getDeclaredMethod(methodName, args);
              int i = method.getModifiers();
              if ((i & disallowedModifierMask) != 0 || (i & requiredModifierMask) != requiredModifierMask)
                method = null; 
            } catch (NoSuchMethodException noSuchMethodException) {}
            return method;
          }
        }); }
  
  private static class CompareClassByName implements Comparator {
    private CompareClassByName() {}
    
    public int compare(Object param1Object1, Object param1Object2) {
      Class clazz1 = (Class)param1Object1;
      Class clazz2 = (Class)param1Object2;
      return clazz1.getName().compareTo(clazz2.getName());
    }
  }
  
  private static class CompareMemberByName implements Comparator {
    private CompareMemberByName() {}
    
    public int compare(Object param1Object1, Object param1Object2) {
      String str1 = ((Member)param1Object1).getName();
      String str2 = ((Member)param1Object2).getName();
      if (param1Object1 instanceof Method) {
        str1 = str1 + ObjectStreamClassUtil_1_3.getSignature((Method)param1Object1);
        str2 = str2 + ObjectStreamClassUtil_1_3.getSignature((Method)param1Object2);
      } else if (param1Object1 instanceof Constructor) {
        str1 = str1 + ObjectStreamClassUtil_1_3.getSignature((Constructor)param1Object1);
        str2 = str2 + ObjectStreamClassUtil_1_3.getSignature((Constructor)param1Object2);
      } 
      return str1.compareTo(str2);
    }
  }
  
  private static class MethodSignature implements Comparator {
    Member member;
    
    String signature;
    
    static MethodSignature[] removePrivateAndSort(Member[] param1ArrayOfMember) {
      byte b1 = 0;
      for (byte b2 = 0; b2 < param1ArrayOfMember.length; b2++) {
        if (!Modifier.isPrivate(param1ArrayOfMember[b2].getModifiers()))
          b1++; 
      } 
      MethodSignature[] arrayOfMethodSignature = new MethodSignature[b1];
      byte b3 = 0;
      for (byte b4 = 0; b4 < param1ArrayOfMember.length; b4++) {
        if (!Modifier.isPrivate(param1ArrayOfMember[b4].getModifiers())) {
          arrayOfMethodSignature[b3] = new MethodSignature(param1ArrayOfMember[b4]);
          b3++;
        } 
      } 
      if (b3 > 0)
        Arrays.sort(arrayOfMethodSignature, arrayOfMethodSignature[0]); 
      return arrayOfMethodSignature;
    }
    
    public int compare(Object param1Object1, Object param1Object2) {
      int i;
      if (param1Object1 == param1Object2)
        return 0; 
      MethodSignature methodSignature1 = (MethodSignature)param1Object1;
      MethodSignature methodSignature2 = (MethodSignature)param1Object2;
      if (isConstructor()) {
        i = methodSignature1.signature.compareTo(methodSignature2.signature);
      } else {
        i = methodSignature1.member.getName().compareTo(methodSignature2.member.getName());
        if (i == 0)
          i = methodSignature1.signature.compareTo(methodSignature2.signature); 
      } 
      return i;
    }
    
    private final boolean isConstructor() { return this.member instanceof Constructor; }
    
    private MethodSignature(Member param1Member) {
      this.member = param1Member;
      if (isConstructor()) {
        this.signature = ObjectStreamClassUtil_1_3.getSignature((Constructor)param1Member);
      } else {
        this.signature = ObjectStreamClassUtil_1_3.getSignature((Method)param1Member);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectStreamClassUtil_1_3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */