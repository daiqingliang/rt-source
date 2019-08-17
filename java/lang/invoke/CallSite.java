package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;

public abstract class CallSite {
  MethodHandle target;
  
  private static final MethodHandle GET_TARGET;
  
  private static final MethodHandle THROW_UCS;
  
  private static final long TARGET_OFFSET;
  
  CallSite(MethodType paramMethodType) { this.target = makeUninitializedCallSite(paramMethodType); }
  
  CallSite(MethodHandle paramMethodHandle) {
    paramMethodHandle.type();
    this.target = paramMethodHandle;
  }
  
  CallSite(MethodType paramMethodType, MethodHandle paramMethodHandle) throws Throwable {
    this(paramMethodType);
    ConstantCallSite constantCallSite = (ConstantCallSite)this;
    MethodHandle methodHandle = (MethodHandle)paramMethodHandle.invokeWithArguments(new Object[] { constantCallSite });
    checkTargetChange(this.target, methodHandle);
    this.target = methodHandle;
  }
  
  public MethodType type() { return this.target.type(); }
  
  public abstract MethodHandle getTarget();
  
  public abstract void setTarget(MethodHandle paramMethodHandle);
  
  void checkTargetChange(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    if (!methodType2.equals(methodType1))
      throw wrongTargetType(paramMethodHandle2, methodType1); 
  }
  
  private static WrongMethodTypeException wrongTargetType(MethodHandle paramMethodHandle, MethodType paramMethodType) { return new WrongMethodTypeException(String.valueOf(paramMethodHandle) + " should be of type " + paramMethodType); }
  
  public abstract MethodHandle dynamicInvoker();
  
  MethodHandle makeDynamicInvoker() {
    BoundMethodHandle boundMethodHandle = GET_TARGET.bindArgumentL(0, this);
    MethodHandle methodHandle = MethodHandles.exactInvoker(type());
    return MethodHandles.foldArguments(methodHandle, boundMethodHandle);
  }
  
  private static Object uninitializedCallSite(Object... paramVarArgs) { throw new IllegalStateException("uninitialized call site"); }
  
  private MethodHandle makeUninitializedCallSite(MethodType paramMethodType) {
    MethodType methodType = paramMethodType.basicType();
    MethodHandle methodHandle = methodType.form().cachedMethodHandle(2);
    if (methodHandle == null) {
      methodHandle = THROW_UCS.asType(methodType);
      methodHandle = methodType.form().setCachedMethodHandle(2, methodHandle);
    } 
    return methodHandle.viewAsType(paramMethodType, false);
  }
  
  void setTargetNormal(MethodHandle paramMethodHandle) { MethodHandleNatives.setCallSiteTargetNormal(this, paramMethodHandle); }
  
  MethodHandle getTargetVolatile() { return (MethodHandle)MethodHandleStatics.UNSAFE.getObjectVolatile(this, TARGET_OFFSET); }
  
  void setTargetVolatile(MethodHandle paramMethodHandle) { MethodHandleNatives.setCallSiteTargetVolatile(this, paramMethodHandle); }
  
  static CallSite makeSite(MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, Class<?> paramClass) {
    CallSite callSite;
    MethodHandles.Lookup lookup = MethodHandles.Lookup.IMPL_LOOKUP.in(paramClass);
    try {
      Object object;
      paramObject = maybeReBox(paramObject);
      if (paramObject == null) {
        object = paramMethodHandle.invoke(lookup, paramString, paramMethodType);
      } else if (!paramObject.getClass().isArray()) {
        object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, paramObject);
      } else {
        MethodHandle methodHandle2;
        MethodHandle methodHandle1;
        MethodType methodType2;
        MethodType methodType1;
        Object[] arrayOfObject = (Object[])paramObject;
        maybeReBoxElements(arrayOfObject);
        switch (arrayOfObject.length) {
          case 0:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType);
            break;
          case 1:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, arrayOfObject[0]);
            break;
          case 2:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, arrayOfObject[0], arrayOfObject[1]);
            break;
          case 3:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, arrayOfObject[0], arrayOfObject[1], arrayOfObject[2]);
            break;
          case 4:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3]);
            break;
          case 5:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4]);
            break;
          case 6:
            object = paramMethodHandle.invoke(lookup, paramString, paramMethodType, arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5]);
            break;
          default:
            if (3 + arrayOfObject.length > 254)
              throw new BootstrapMethodError("too many bootstrap method arguments"); 
            methodType2 = (methodType1 = paramMethodHandle.type()).genericMethodType(3 + arrayOfObject.length);
            methodHandle1 = paramMethodHandle.asType(methodType2);
            methodHandle2 = methodType2.invokers().spreadInvoker(3);
            object = methodHandle2.invokeExact(methodHandle1, lookup, paramString, paramMethodType, arrayOfObject);
            break;
        } 
      } 
      if (object instanceof CallSite) {
        callSite = (CallSite)object;
      } else {
        throw new ClassCastException("bootstrap method failed to produce a CallSite");
      } 
      if (!callSite.getTarget().type().equals(paramMethodType))
        throw wrongTargetType(callSite.getTarget(), paramMethodType); 
    } catch (Throwable throwable) {
      BootstrapMethodError bootstrapMethodError;
      if (throwable instanceof BootstrapMethodError) {
        bootstrapMethodError = (BootstrapMethodError)throwable;
      } else {
        bootstrapMethodError = new BootstrapMethodError("call site initialization exception", throwable);
      } 
      throw bootstrapMethodError;
    } 
    return callSite;
  }
  
  private static Object maybeReBox(Object paramObject) {
    if (paramObject instanceof Integer) {
      int i = ((Integer)paramObject).intValue();
      if (i == (byte)i)
        paramObject = Integer.valueOf(i); 
    } 
    return paramObject;
  }
  
  private static void maybeReBoxElements(Object[] paramArrayOfObject) {
    for (byte b = 0; b < paramArrayOfObject.length; b++)
      paramArrayOfObject[b] = maybeReBox(paramArrayOfObject[b]); 
  }
  
  static  {
    MethodHandleImpl.initStatics();
    try {
      GET_TARGET = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(CallSite.class, "getTarget", MethodType.methodType(MethodHandle.class));
      THROW_UCS = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(CallSite.class, "uninitializedCallSite", MethodType.methodType(Object.class, Object[].class));
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError(reflectiveOperationException);
    } 
    try {
      TARGET_OFFSET = MethodHandleStatics.UNSAFE.objectFieldOffset(CallSite.class.getDeclaredField("target"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\CallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */