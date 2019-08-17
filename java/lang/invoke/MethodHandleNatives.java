package java.lang.invoke;

import java.lang.invoke.CallSite;
import java.lang.invoke.Invokers;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

class MethodHandleNatives {
  static final boolean COUNT_GWT;
  
  static native void init(MemberName paramMemberName, Object paramObject);
  
  static native void expand(MemberName paramMemberName);
  
  static native MemberName resolve(MemberName paramMemberName, Class<?> paramClass) throws LinkageError, ClassNotFoundException;
  
  static native int getMembers(Class<?> paramClass1, String paramString1, String paramString2, int paramInt1, Class<?> paramClass2, int paramInt2, MemberName[] paramArrayOfMemberName);
  
  static native long objectFieldOffset(MemberName paramMemberName);
  
  static native long staticFieldOffset(MemberName paramMemberName);
  
  static native Object staticFieldBase(MemberName paramMemberName);
  
  static native Object getMemberVMInfo(MemberName paramMemberName);
  
  static native int getConstant(int paramInt);
  
  static native void setCallSiteTargetNormal(CallSite paramCallSite, MethodHandle paramMethodHandle);
  
  static native void setCallSiteTargetVolatile(CallSite paramCallSite, MethodHandle paramMethodHandle);
  
  private static native void registerNatives();
  
  static boolean refKindIsValid(int paramInt) { return (paramInt > 0 && paramInt < 10); }
  
  static boolean refKindIsField(byte paramByte) {
    assert refKindIsValid(paramByte);
    return (paramByte <= 4);
  }
  
  static boolean refKindIsGetter(byte paramByte) {
    assert refKindIsValid(paramByte);
    return (paramByte <= 2);
  }
  
  static boolean refKindIsSetter(byte paramByte) { return (refKindIsField(paramByte) && !refKindIsGetter(paramByte)); }
  
  static boolean refKindIsMethod(byte paramByte) { return (!refKindIsField(paramByte) && paramByte != 8); }
  
  static boolean refKindIsConstructor(byte paramByte) { return (paramByte == 8); }
  
  static boolean refKindHasReceiver(byte paramByte) {
    assert refKindIsValid(paramByte);
    return ((paramByte & true) != 0);
  }
  
  static boolean refKindIsStatic(byte paramByte) { return (!refKindHasReceiver(paramByte) && paramByte != 8); }
  
  static boolean refKindDoesDispatch(byte paramByte) {
    assert refKindIsValid(paramByte);
    return (paramByte == 5 || paramByte == 9);
  }
  
  static String refKindName(byte paramByte) {
    assert refKindIsValid(paramByte);
    switch (paramByte) {
      case 1:
        return "getField";
      case 2:
        return "getStatic";
      case 3:
        return "putField";
      case 4:
        return "putStatic";
      case 5:
        return "invokeVirtual";
      case 6:
        return "invokeStatic";
      case 7:
        return "invokeSpecial";
      case 8:
        return "newInvokeSpecial";
      case 9:
        return "invokeInterface";
    } 
    return "REF_???";
  }
  
  private static native int getNamedCon(int paramInt, Object[] paramArrayOfObject);
  
  static boolean verifyConstants() {
    Object[] arrayOfObject = { null };
    for (byte b = 0;; b++) {
      arrayOfObject[0] = null;
      int i = getNamedCon(b, arrayOfObject);
      if (arrayOfObject[false] == null)
        break; 
      String str = (String)arrayOfObject[0];
      try {
        Field field = Constants.class.getDeclaredField(str);
        int j = field.getInt(null);
        if (j != i) {
          String str1 = str + ": JVM has " + i + " while Java has " + j;
          if (str.equals("CONV_OP_LIMIT")) {
            System.err.println("warning: " + str1);
          } else {
            throw new InternalError(str1);
          } 
        } 
      } catch (NoSuchFieldException|IllegalAccessException noSuchFieldException) {
        String str1 = str + ": JVM has " + i + " which Java does not define";
      } 
    } 
    return true;
  }
  
  static MemberName linkCallSite(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object[] paramArrayOfObject) {
    MethodHandle methodHandle = (MethodHandle)paramObject2;
    Class clazz = (Class)paramObject1;
    String str = paramObject3.toString().intern();
    MethodType methodType = (MethodType)paramObject4;
    return !MethodHandleStatics.TRACE_METHOD_LINKAGE ? linkCallSiteImpl(clazz, methodHandle, str, methodType, paramObject5, paramArrayOfObject) : linkCallSiteTracing(clazz, methodHandle, str, methodType, paramObject5, paramArrayOfObject);
  }
  
  static MemberName linkCallSiteImpl(Class<?> paramClass, MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, Object[] paramArrayOfObject) {
    CallSite callSite = CallSite.makeSite(paramMethodHandle, paramString, paramMethodType, paramObject, paramClass);
    if (callSite instanceof ConstantCallSite) {
      paramArrayOfObject[0] = callSite.dynamicInvoker();
      return Invokers.linkToTargetMethod(paramMethodType);
    } 
    paramArrayOfObject[0] = callSite;
    return Invokers.linkToCallSiteMethod(paramMethodType);
  }
  
  static MemberName linkCallSiteTracing(Class<?> paramClass, MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, Object[] paramArrayOfObject) {
    MethodHandle methodHandle = paramMethodHandle.internalMemberName();
    if (methodHandle == null)
      methodHandle = paramMethodHandle; 
    List list = (paramObject instanceof Object[]) ? Arrays.asList((Object[])paramObject) : paramObject;
    System.out.println("linkCallSite " + paramClass.getName() + " " + methodHandle + " " + paramString + paramMethodType + "/" + list);
    try {
      MemberName memberName = linkCallSiteImpl(paramClass, paramMethodHandle, paramString, paramMethodType, paramObject, paramArrayOfObject);
      System.out.println("linkCallSite => " + memberName + " + " + paramArrayOfObject[0]);
      return memberName;
    } catch (Throwable throwable) {
      System.out.println("linkCallSite => throw " + throwable);
      throw throwable;
    } 
  }
  
  static MethodType findMethodHandleType(Class<?> paramClass, Class<?>[] paramArrayOfClass) { return MethodType.makeImpl(paramClass, paramArrayOfClass, true); }
  
  static MemberName linkMethod(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject, Object[] paramArrayOfObject) { return !MethodHandleStatics.TRACE_METHOD_LINKAGE ? linkMethodImpl(paramClass1, paramInt, paramClass2, paramString, paramObject, paramArrayOfObject) : linkMethodTracing(paramClass1, paramInt, paramClass2, paramString, paramObject, paramArrayOfObject); }
  
  static MemberName linkMethodImpl(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject, Object[] paramArrayOfObject) {
    try {
      if (paramClass2 == MethodHandle.class && paramInt == 5)
        return Invokers.methodHandleInvokeLinkerMethod(paramString, fixMethodType(paramClass1, paramObject), paramArrayOfObject); 
    } catch (Throwable throwable) {
      if (throwable instanceof LinkageError)
        throw (LinkageError)throwable; 
      throw new LinkageError(throwable.getMessage(), throwable);
    } 
    throw new LinkageError("no such method " + paramClass2.getName() + "." + paramString + paramObject);
  }
  
  private static MethodType fixMethodType(Class<?> paramClass, Object paramObject) { return (paramObject instanceof MethodType) ? (MethodType)paramObject : MethodType.fromMethodDescriptorString((String)paramObject, paramClass.getClassLoader()); }
  
  static MemberName linkMethodTracing(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject, Object[] paramArrayOfObject) {
    System.out.println("linkMethod " + paramClass2.getName() + "." + paramString + paramObject + "/" + Integer.toHexString(paramInt));
    try {
      MemberName memberName = linkMethodImpl(paramClass1, paramInt, paramClass2, paramString, paramObject, paramArrayOfObject);
      System.out.println("linkMethod => " + memberName + " + " + paramArrayOfObject[0]);
      return memberName;
    } catch (Throwable throwable) {
      System.out.println("linkMethod => throw " + throwable);
      throw throwable;
    } 
  }
  
  static MethodHandle linkMethodHandleConstant(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject) {
    try {
      MethodHandles.Lookup lookup = MethodHandles.Lookup.IMPL_LOOKUP.in(paramClass1);
      assert refKindIsValid(paramInt);
      return lookup.linkMethodHandleConstant((byte)paramInt, paramClass2, paramString, paramObject);
    } catch (IllegalAccessException illegalAccessException) {
      Throwable throwable = illegalAccessException.getCause();
      if (throwable instanceof AbstractMethodError)
        throw (AbstractMethodError)throwable; 
      IllegalAccessError illegalAccessError = new IllegalAccessError(illegalAccessException.getMessage());
      throw initCauseFrom(illegalAccessError, illegalAccessException);
    } catch (NoSuchMethodException noSuchMethodException) {
      NoSuchMethodError noSuchMethodError = new NoSuchMethodError(noSuchMethodException.getMessage());
      throw initCauseFrom(noSuchMethodError, noSuchMethodException);
    } catch (NoSuchFieldException noSuchFieldException) {
      NoSuchFieldError noSuchFieldError = new NoSuchFieldError(noSuchFieldException.getMessage());
      throw initCauseFrom(noSuchFieldError, noSuchFieldException);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      IncompatibleClassChangeError incompatibleClassChangeError = new IncompatibleClassChangeError();
      throw initCauseFrom(incompatibleClassChangeError, reflectiveOperationException);
    } 
  }
  
  private static Error initCauseFrom(Error paramError, Exception paramException) {
    Throwable throwable = paramException.getCause();
    if (paramError.getClass().isInstance(throwable))
      return (Error)throwable; 
    paramError.initCause((throwable == null) ? paramException : throwable);
    return paramError;
  }
  
  static boolean isCallerSensitive(MemberName paramMemberName) { return !paramMemberName.isInvocable() ? false : ((paramMemberName.isCallerSensitive() || canBeCalledVirtual(paramMemberName))); }
  
  static boolean canBeCalledVirtual(MemberName paramMemberName) {
    assert paramMemberName.isInvocable();
    Class clazz = paramMemberName.getDeclaringClass();
    switch (paramMemberName.getName()) {
      case "checkMemberAccess":
        return canBeCalledVirtual(paramMemberName, SecurityManager.class);
      case "getContextClassLoader":
        return canBeCalledVirtual(paramMemberName, Thread.class);
    } 
    return false;
  }
  
  static boolean canBeCalledVirtual(MemberName paramMemberName, Class<?> paramClass) {
    Class clazz = paramMemberName.getDeclaringClass();
    return (clazz == paramClass) ? true : ((paramMemberName.isStatic() || paramMemberName.isPrivate()) ? false : ((paramClass.isAssignableFrom(clazz) || clazz.isInterface())));
  }
  
  static  {
    registerNatives();
    COUNT_GWT = (getConstant(4) != 0);
    MethodHandleImpl.initStatics();
    for (byte b = 1; b < 10; b = (byte)(b + 1))
      assert refKindHasReceiver(b) == (((1 << b & 0x2AA) != 0)) : b; 
    assert verifyConstants();
  }
  
  static class Constants {
    static final int GC_COUNT_GWT = 4;
    
    static final int GC_LAMBDA_SUPPORT = 5;
    
    static final int MN_IS_METHOD = 65536;
    
    static final int MN_IS_CONSTRUCTOR = 131072;
    
    static final int MN_IS_FIELD = 262144;
    
    static final int MN_IS_TYPE = 524288;
    
    static final int MN_CALLER_SENSITIVE = 1048576;
    
    static final int MN_REFERENCE_KIND_SHIFT = 24;
    
    static final int MN_REFERENCE_KIND_MASK = 15;
    
    static final int MN_SEARCH_SUPERCLASSES = 1048576;
    
    static final int MN_SEARCH_INTERFACES = 2097152;
    
    static final int T_BOOLEAN = 4;
    
    static final int T_CHAR = 5;
    
    static final int T_FLOAT = 6;
    
    static final int T_DOUBLE = 7;
    
    static final int T_BYTE = 8;
    
    static final int T_SHORT = 9;
    
    static final int T_INT = 10;
    
    static final int T_LONG = 11;
    
    static final int T_OBJECT = 12;
    
    static final int T_VOID = 14;
    
    static final int T_ILLEGAL = 99;
    
    static final byte CONSTANT_Utf8 = 1;
    
    static final byte CONSTANT_Integer = 3;
    
    static final byte CONSTANT_Float = 4;
    
    static final byte CONSTANT_Long = 5;
    
    static final byte CONSTANT_Double = 6;
    
    static final byte CONSTANT_Class = 7;
    
    static final byte CONSTANT_String = 8;
    
    static final byte CONSTANT_Fieldref = 9;
    
    static final byte CONSTANT_Methodref = 10;
    
    static final byte CONSTANT_InterfaceMethodref = 11;
    
    static final byte CONSTANT_NameAndType = 12;
    
    static final byte CONSTANT_MethodHandle = 15;
    
    static final byte CONSTANT_MethodType = 16;
    
    static final byte CONSTANT_InvokeDynamic = 18;
    
    static final byte CONSTANT_LIMIT = 19;
    
    static final char ACC_PUBLIC = '\001';
    
    static final char ACC_PRIVATE = '\002';
    
    static final char ACC_PROTECTED = '\004';
    
    static final char ACC_STATIC = '\b';
    
    static final char ACC_FINAL = '\020';
    
    static final char ACC_SYNCHRONIZED = ' ';
    
    static final char ACC_VOLATILE = '@';
    
    static final char ACC_TRANSIENT = '';
    
    static final char ACC_NATIVE = 'Ā';
    
    static final char ACC_INTERFACE = 'Ȁ';
    
    static final char ACC_ABSTRACT = 'Ѐ';
    
    static final char ACC_STRICT = 'ࠀ';
    
    static final char ACC_SYNTHETIC = 'က';
    
    static final char ACC_ANNOTATION = ' ';
    
    static final char ACC_ENUM = '䀀';
    
    static final char ACC_SUPER = ' ';
    
    static final char ACC_BRIDGE = '@';
    
    static final char ACC_VARARGS = '';
    
    static final byte REF_NONE = 0;
    
    static final byte REF_getField = 1;
    
    static final byte REF_getStatic = 2;
    
    static final byte REF_putField = 3;
    
    static final byte REF_putStatic = 4;
    
    static final byte REF_invokeVirtual = 5;
    
    static final byte REF_invokeStatic = 6;
    
    static final byte REF_invokeSpecial = 7;
    
    static final byte REF_newInvokeSpecial = 8;
    
    static final byte REF_invokeInterface = 9;
    
    static final byte REF_LIMIT = 10;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandleNatives.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */