package java.lang.invoke;

import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class MethodHandleStatics {
  static final Unsafe UNSAFE = Unsafe.getUnsafe();
  
  static final boolean DEBUG_METHOD_HANDLE_NAMES;
  
  static final boolean DUMP_CLASS_FILES;
  
  static final boolean TRACE_INTERPRETER;
  
  static final boolean TRACE_METHOD_LINKAGE;
  
  static final int COMPILE_THRESHOLD;
  
  static final int DONT_INLINE_THRESHOLD;
  
  static final int PROFILE_LEVEL;
  
  static final boolean PROFILE_GWT;
  
  static final int CUSTOMIZE_THRESHOLD;
  
  static boolean debugEnabled() { return DEBUG_METHOD_HANDLE_NAMES | DUMP_CLASS_FILES | TRACE_INTERPRETER | TRACE_METHOD_LINKAGE; }
  
  static String getNameString(MethodHandle paramMethodHandle, MethodType paramMethodType) {
    if (paramMethodType == null)
      paramMethodType = paramMethodHandle.type(); 
    MemberName memberName = null;
    if (paramMethodHandle != null)
      memberName = paramMethodHandle.internalMemberName(); 
    return (memberName == null) ? ("invoke" + paramMethodType) : (memberName.getName() + paramMethodType);
  }
  
  static String getNameString(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) { return getNameString(paramMethodHandle1, (paramMethodHandle2 == null) ? (MethodType)null : paramMethodHandle2.type()); }
  
  static String getNameString(MethodHandle paramMethodHandle) { return getNameString(paramMethodHandle, (MethodType)null); }
  
  static String addTypeString(Object paramObject, MethodHandle paramMethodHandle) {
    String str = String.valueOf(paramObject);
    if (paramMethodHandle == null)
      return str; 
    int i = str.indexOf('(');
    if (i >= 0)
      str = str.substring(0, i); 
    return str + paramMethodHandle.type();
  }
  
  static InternalError newInternalError(String paramString) { return new InternalError(paramString); }
  
  static InternalError newInternalError(String paramString, Throwable paramThrowable) { return new InternalError(paramString, paramThrowable); }
  
  static InternalError newInternalError(Throwable paramThrowable) { return new InternalError(paramThrowable); }
  
  static RuntimeException newIllegalStateException(String paramString) { return new IllegalStateException(paramString); }
  
  static RuntimeException newIllegalStateException(String paramString, Object paramObject) { return new IllegalStateException(message(paramString, paramObject)); }
  
  static RuntimeException newIllegalArgumentException(String paramString) { return new IllegalArgumentException(paramString); }
  
  static RuntimeException newIllegalArgumentException(String paramString, Object paramObject) { return new IllegalArgumentException(message(paramString, paramObject)); }
  
  static RuntimeException newIllegalArgumentException(String paramString, Object paramObject1, Object paramObject2) { return new IllegalArgumentException(message(paramString, paramObject1, paramObject2)); }
  
  static Error uncaughtException(Throwable paramThrowable) {
    if (paramThrowable instanceof Error)
      throw (Error)paramThrowable; 
    if (paramThrowable instanceof RuntimeException)
      throw (RuntimeException)paramThrowable; 
    throw newInternalError("uncaught exception", paramThrowable);
  }
  
  static Error NYI() { throw new AssertionError("NYI"); }
  
  private static String message(String paramString, Object paramObject) {
    if (paramObject != null)
      paramString = paramString + ": " + paramObject; 
    return paramString;
  }
  
  private static String message(String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject1 != null || paramObject2 != null)
      paramString = paramString + ": " + paramObject1 + ", " + paramObject2; 
    return paramString;
  }
  
  static  {
    final Object[] values = new Object[9];
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            values[0] = Boolean.valueOf(Boolean.getBoolean("java.lang.invoke.MethodHandle.DEBUG_NAMES"));
            values[1] = Boolean.valueOf(Boolean.getBoolean("java.lang.invoke.MethodHandle.DUMP_CLASS_FILES"));
            values[2] = Boolean.valueOf(Boolean.getBoolean("java.lang.invoke.MethodHandle.TRACE_INTERPRETER"));
            values[3] = Boolean.valueOf(Boolean.getBoolean("java.lang.invoke.MethodHandle.TRACE_METHOD_LINKAGE"));
            values[4] = Integer.getInteger("java.lang.invoke.MethodHandle.COMPILE_THRESHOLD", 0);
            values[5] = Integer.getInteger("java.lang.invoke.MethodHandle.DONT_INLINE_THRESHOLD", 30);
            values[6] = Integer.getInteger("java.lang.invoke.MethodHandle.PROFILE_LEVEL", 0);
            values[7] = Boolean.valueOf(Boolean.parseBoolean(System.getProperty("java.lang.invoke.MethodHandle.PROFILE_GWT", "true")));
            values[8] = Integer.getInteger("java.lang.invoke.MethodHandle.CUSTOMIZE_THRESHOLD", 127);
            return null;
          }
        });
    DEBUG_METHOD_HANDLE_NAMES = ((Boolean)arrayOfObject[0]).booleanValue();
    DUMP_CLASS_FILES = ((Boolean)arrayOfObject[1]).booleanValue();
    TRACE_INTERPRETER = ((Boolean)arrayOfObject[2]).booleanValue();
    TRACE_METHOD_LINKAGE = ((Boolean)arrayOfObject[3]).booleanValue();
    COMPILE_THRESHOLD = ((Integer)arrayOfObject[4]).intValue();
    DONT_INLINE_THRESHOLD = ((Integer)arrayOfObject[5]).intValue();
    PROFILE_LEVEL = ((Integer)arrayOfObject[6]).intValue();
    PROFILE_GWT = ((Boolean)arrayOfObject[7]).booleanValue();
    CUSTOMIZE_THRESHOLD = ((Integer)arrayOfObject[8]).intValue();
    if (CUSTOMIZE_THRESHOLD < -1 || CUSTOMIZE_THRESHOLD > 127)
      throw newInternalError("CUSTOMIZE_THRESHOLD should be in [-1...127] range"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandleStatics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */