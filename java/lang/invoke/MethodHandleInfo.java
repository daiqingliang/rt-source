package java.lang.invoke;

import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.Objects;

public interface MethodHandleInfo {
  public static final int REF_getField = 1;
  
  public static final int REF_getStatic = 2;
  
  public static final int REF_putField = 3;
  
  public static final int REF_putStatic = 4;
  
  public static final int REF_invokeVirtual = 5;
  
  public static final int REF_invokeStatic = 6;
  
  public static final int REF_invokeSpecial = 7;
  
  public static final int REF_newInvokeSpecial = 8;
  
  public static final int REF_invokeInterface = 9;
  
  int getReferenceKind();
  
  Class<?> getDeclaringClass();
  
  String getName();
  
  MethodType getMethodType();
  
  <T extends java.lang.reflect.Member> T reflectAs(Class<T> paramClass, MethodHandles.Lookup paramLookup);
  
  int getModifiers();
  
  default boolean isVarArgs() { return MethodHandleNatives.refKindIsField((byte)getReferenceKind()) ? false : Modifier.isTransient(getModifiers()); }
  
  static String referenceKindToString(int paramInt) {
    if (!MethodHandleNatives.refKindIsValid(paramInt))
      throw MethodHandleStatics.newIllegalArgumentException("invalid reference kind", Integer.valueOf(paramInt)); 
    return MethodHandleNatives.refKindName((byte)paramInt);
  }
  
  static String toString(int paramInt, Class<?> paramClass, String paramString, MethodType paramMethodType) {
    Objects.requireNonNull(paramString);
    Objects.requireNonNull(paramMethodType);
    return String.format("%s %s.%s:%s", new Object[] { referenceKindToString(paramInt), paramClass.getName(), paramString, paramMethodType });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandleInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */