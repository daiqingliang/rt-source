package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.DelegatingMethodHandle;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.util.Arrays;

abstract class DelegatingMethodHandle extends MethodHandle {
  static final LambdaForm.NamedFunction NF_getTarget;
  
  protected DelegatingMethodHandle(MethodHandle paramMethodHandle) { this(paramMethodHandle.type(), paramMethodHandle); }
  
  protected DelegatingMethodHandle(MethodType paramMethodType, MethodHandle paramMethodHandle) { super(paramMethodType, chooseDelegatingForm(paramMethodHandle)); }
  
  protected DelegatingMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm) { super(paramMethodType, paramLambdaForm); }
  
  protected abstract MethodHandle getTarget();
  
  abstract MethodHandle asTypeUncached(MethodType paramMethodType);
  
  MemberName internalMemberName() { return getTarget().internalMemberName(); }
  
  boolean isInvokeSpecial() { return getTarget().isInvokeSpecial(); }
  
  Class<?> internalCallerClass() { return getTarget().internalCallerClass(); }
  
  MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) { throw MethodHandleStatics.newIllegalArgumentException("do not use this"); }
  
  String internalProperties() { return "\n& Class=" + getClass().getSimpleName() + "\n& Target=" + getTarget().debugString(); }
  
  BoundMethodHandle rebind() { return getTarget().rebind(); }
  
  private static LambdaForm chooseDelegatingForm(MethodHandle paramMethodHandle) { return (paramMethodHandle instanceof SimpleMethodHandle) ? paramMethodHandle.internalForm() : makeReinvokerForm(paramMethodHandle, 8, DelegatingMethodHandle.class, NF_getTarget); }
  
  static LambdaForm makeReinvokerForm(MethodHandle paramMethodHandle, int paramInt, Object paramObject, LambdaForm.NamedFunction paramNamedFunction) {
    switch (paramInt) {
      case 7:
        str = "BMH.reinvoke";
        return makeReinvokerForm(paramMethodHandle, paramInt, paramObject, str, true, paramNamedFunction, null);
      case 8:
        str = "MH.delegate";
        return makeReinvokerForm(paramMethodHandle, paramInt, paramObject, str, true, paramNamedFunction, null);
    } 
    String str = "MH.reinvoke";
    return makeReinvokerForm(paramMethodHandle, paramInt, paramObject, str, true, paramNamedFunction, null);
  }
  
  static LambdaForm makeReinvokerForm(MethodHandle paramMethodHandle, int paramInt, Object paramObject, String paramString, boolean paramBoolean, LambdaForm.NamedFunction paramNamedFunction1, LambdaForm.NamedFunction paramNamedFunction2) {
    MethodType methodType = paramMethodHandle.type().basicType();
    boolean bool1 = (paramInt < 0 || methodType.parameterSlotCount() > 253) ? 1 : 0;
    boolean bool2 = (paramNamedFunction2 != null) ? 1 : 0;
    if (!bool1) {
      LambdaForm lambdaForm1 = methodType.form().cachedLambdaForm(paramInt);
      if (lambdaForm1 != null)
        return lambdaForm1; 
    } 
    int i = 1 + methodType.parameterCount();
    int j = i;
    int k = bool2 ? j++ : -1;
    byte b = bool1 ? -1 : j++;
    int m = j++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, methodType.invokerType());
    assert arrayOfName.length == j;
    arrayOfName[0] = arrayOfName[0].withConstraint(paramObject);
    if (bool2)
      arrayOfName[k] = new LambdaForm.Name(paramNamedFunction2, new Object[] { arrayOfName[0] }); 
    if (bool1) {
      Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 1, i, Object[].class);
      arrayOfName[m] = new LambdaForm.Name(paramMethodHandle, arrayOfObject);
    } else {
      arrayOfName[b] = new LambdaForm.Name(paramNamedFunction1, new Object[] { arrayOfName[0] });
      Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, i, Object[].class);
      arrayOfObject[0] = arrayOfName[b];
      arrayOfName[m] = new LambdaForm.Name(methodType, arrayOfObject);
    } 
    LambdaForm lambdaForm = new LambdaForm(paramString, i, arrayOfName, paramBoolean);
    if (!bool1)
      lambdaForm = methodType.form().setCachedLambdaForm(paramInt, lambdaForm); 
    return lambdaForm;
  }
  
  static  {
    try {
      NF_getTarget = new LambdaForm.NamedFunction(DelegatingMethodHandle.class.getDeclaredMethod("getTarget", new Class[0]));
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError(reflectiveOperationException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\DelegatingMethodHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */