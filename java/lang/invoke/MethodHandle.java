package java.lang.invoke;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandle.PolymorphicSignature;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.util.Arrays;
import java.util.List;

public abstract class MethodHandle {
  private final MethodType type;
  
  final LambdaForm form;
  
  MethodHandle asTypeCache;
  
  byte customizationCount;
  
  private static final long FORM_OFFSET;
  
  public MethodType type() { return this.type; }
  
  MethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    paramMethodType.getClass();
    paramLambdaForm.getClass();
    this.type = paramMethodType;
    this.form = paramLambdaForm.uncustomize();
    this.form.prepare();
  }
  
  @PolymorphicSignature
  public final native Object invokeExact(Object... paramVarArgs) throws Throwable;
  
  @PolymorphicSignature
  public final native Object invoke(Object... paramVarArgs) throws Throwable;
  
  @PolymorphicSignature
  final native Object invokeBasic(Object... paramVarArgs) throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToVirtual(Object... paramVarArgs) throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToStatic(Object... paramVarArgs) throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToSpecial(Object... paramVarArgs) throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToInterface(Object... paramVarArgs) throws Throwable;
  
  public Object invokeWithArguments(Object... paramVarArgs) throws Throwable {
    MethodType methodType = MethodType.genericMethodType((paramVarArgs == null) ? 0 : paramVarArgs.length);
    return methodType.invokers().spreadInvoker(0).invokeExact(asType(methodType), paramVarArgs);
  }
  
  public Object invokeWithArguments(List<?> paramList) throws Throwable { return invokeWithArguments(paramList.toArray()); }
  
  public MethodHandle asType(MethodType paramMethodType) {
    if (paramMethodType == this.type)
      return this; 
    MethodHandle methodHandle = asTypeCached(paramMethodType);
    return (methodHandle != null) ? methodHandle : asTypeUncached(paramMethodType);
  }
  
  private MethodHandle asTypeCached(MethodType paramMethodType) {
    MethodHandle methodHandle = this.asTypeCache;
    return (methodHandle != null && paramMethodType == methodHandle.type) ? methodHandle : null;
  }
  
  MethodHandle asTypeUncached(MethodType paramMethodType) {
    if (!this.type.isConvertibleTo(paramMethodType))
      throw new WrongMethodTypeException("cannot convert " + this + " to " + paramMethodType); 
    return this.asTypeCache = MethodHandleImpl.makePairwiseConvert(this, paramMethodType, true);
  }
  
  public MethodHandle asSpreader(Class<?> paramClass, int paramInt) {
    MethodType methodType1 = asSpreaderChecks(paramClass, paramInt);
    int i = type().parameterCount();
    int j = i - paramInt;
    MethodHandle methodHandle = asType(methodType1);
    BoundMethodHandle boundMethodHandle = methodHandle.rebind();
    LambdaForm lambdaForm = boundMethodHandle.editor().spreadArgumentsForm(1 + j, paramClass, paramInt);
    MethodType methodType2 = methodType1.replaceParameterTypes(j, i, new Class[] { paramClass });
    return boundMethodHandle.copyWith(methodType2, lambdaForm);
  }
  
  private MethodType asSpreaderChecks(Class<?> paramClass, int paramInt) {
    spreadArrayChecks(paramClass, paramInt);
    int i = type().parameterCount();
    if (i < paramInt || paramInt < 0)
      throw MethodHandleStatics.newIllegalArgumentException("bad spread array length"); 
    Class clazz = paramClass.getComponentType();
    MethodType methodType1 = type();
    boolean bool1 = true;
    boolean bool2 = false;
    for (int j = i - paramInt; j < i; j++) {
      Class clazz1 = methodType1.parameterType(j);
      if (clazz1 != clazz) {
        bool1 = false;
        if (!MethodType.canConvert(clazz, clazz1)) {
          bool2 = true;
          break;
        } 
      } 
    } 
    if (bool1)
      return methodType1; 
    MethodType methodType2 = methodType1.asSpreaderType(paramClass, paramInt);
    if (!bool2)
      return methodType2; 
    asType(methodType2);
    throw MethodHandleStatics.newInternalError("should not return", null);
  }
  
  private void spreadArrayChecks(Class<?> paramClass, int paramInt) {
    Class clazz = paramClass.getComponentType();
    if (clazz == null)
      throw MethodHandleStatics.newIllegalArgumentException("not an array type", paramClass); 
    if ((paramInt & 0x7F) != paramInt) {
      if ((paramInt & 0xFF) != paramInt)
        throw MethodHandleStatics.newIllegalArgumentException("array length is not legal", Integer.valueOf(paramInt)); 
      assert paramInt >= 128;
      if (clazz == long.class || clazz == double.class)
        throw MethodHandleStatics.newIllegalArgumentException("array length is not legal for long[] or double[]", Integer.valueOf(paramInt)); 
    } 
  }
  
  public MethodHandle asCollector(Class<?> paramClass, int paramInt) {
    asCollectorChecks(paramClass, paramInt);
    int i = type().parameterCount() - 1;
    BoundMethodHandle boundMethodHandle = rebind();
    MethodType methodType = type().asCollectorType(paramClass, paramInt);
    MethodHandle methodHandle = MethodHandleImpl.varargsArray(paramClass, paramInt);
    LambdaForm lambdaForm = boundMethodHandle.editor().collectArgumentArrayForm(1 + i, methodHandle);
    if (lambdaForm != null)
      return boundMethodHandle.copyWith(methodType, lambdaForm); 
    lambdaForm = boundMethodHandle.editor().collectArgumentsForm(1 + i, methodHandle.type().basicType());
    return boundMethodHandle.copyWithExtendL(methodType, lambdaForm, methodHandle);
  }
  
  boolean asCollectorChecks(Class<?> paramClass, int paramInt) {
    spreadArrayChecks(paramClass, paramInt);
    int i = type().parameterCount();
    if (i != 0) {
      Class clazz = type().parameterType(i - 1);
      if (clazz == paramClass)
        return true; 
      if (clazz.isAssignableFrom(paramClass))
        return false; 
    } 
    throw MethodHandleStatics.newIllegalArgumentException("array type not assignable to trailing argument", this, paramClass);
  }
  
  public MethodHandle asVarargsCollector(Class<?> paramClass) {
    paramClass.getClass();
    boolean bool = asCollectorChecks(paramClass, 0);
    return (isVarargsCollector() && bool) ? this : MethodHandleImpl.makeVarargsCollector(this, paramClass);
  }
  
  public boolean isVarargsCollector() { return false; }
  
  public MethodHandle asFixedArity() {
    assert !isVarargsCollector();
    return this;
  }
  
  public MethodHandle bindTo(Object paramObject) {
    paramObject = this.type.leadingReferenceParameter().cast(paramObject);
    return bindArgumentL(0, paramObject);
  }
  
  public String toString() { return MethodHandleStatics.DEBUG_METHOD_HANDLE_NAMES ? ("MethodHandle" + debugString()) : standardString(); }
  
  String standardString() { return "MethodHandle" + this.type; }
  
  String debugString() { return this.type + " : " + internalForm() + internalProperties(); }
  
  BoundMethodHandle bindArgumentL(int paramInt, Object paramObject) { return rebind().bindArgumentL(paramInt, paramObject); }
  
  MethodHandle setVarargs(MemberName paramMemberName) throws IllegalAccessException {
    if (!paramMemberName.isVarargs())
      return this; 
    Class clazz = type().lastParameterType();
    if (clazz.isArray())
      return MethodHandleImpl.makeVarargsCollector(this, clazz); 
    throw paramMemberName.makeAccessException("cannot make variable arity", null);
  }
  
  MethodHandle viewAsType(MethodType paramMethodType, boolean paramBoolean) {
    assert viewAsTypeChecks(paramMethodType, paramBoolean);
    BoundMethodHandle boundMethodHandle = rebind();
    assert !(boundMethodHandle instanceof DirectMethodHandle);
    return boundMethodHandle.copyWith(paramMethodType, boundMethodHandle.form);
  }
  
  boolean viewAsTypeChecks(MethodType paramMethodType, boolean paramBoolean) {
    if (paramBoolean) {
      assert type().isViewableAs(paramMethodType, true) : Arrays.asList(new Object[] { this, paramMethodType });
    } else {
      assert type().basicType().isViewableAs(paramMethodType.basicType(), true) : Arrays.asList(new Object[] { this, paramMethodType });
    } 
    return true;
  }
  
  LambdaForm internalForm() { return this.form; }
  
  MemberName internalMemberName() { return null; }
  
  Class<?> internalCallerClass() { return null; }
  
  MethodHandleImpl.Intrinsic intrinsicName() { return MethodHandleImpl.Intrinsic.NONE; }
  
  MethodHandle withInternalMemberName(MemberName paramMemberName, boolean paramBoolean) {
    if (paramMemberName != null)
      return MethodHandleImpl.makeWrappedMember(this, paramMemberName, paramBoolean); 
    if (internalMemberName() == null)
      return this; 
    BoundMethodHandle boundMethodHandle = rebind();
    assert boundMethodHandle.internalMemberName() == null;
    return boundMethodHandle;
  }
  
  boolean isInvokeSpecial() { return false; }
  
  Object internalValues() { return null; }
  
  Object internalProperties() { return ""; }
  
  abstract MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm);
  
  abstract BoundMethodHandle rebind();
  
  void updateForm(LambdaForm paramLambdaForm) {
    assert paramLambdaForm.customized == null || paramLambdaForm.customized == this;
    if (this.form == paramLambdaForm)
      return; 
    paramLambdaForm.prepare();
    MethodHandleStatics.UNSAFE.putObject(this, FORM_OFFSET, paramLambdaForm);
    MethodHandleStatics.UNSAFE.fullFence();
  }
  
  void customize() {
    if (this.form.customized == null) {
      LambdaForm lambdaForm = this.form.customize(this);
      updateForm(lambdaForm);
    } else {
      assert this.form.customized == this;
    } 
  }
  
  static  {
    MethodHandleImpl.initStatics();
    try {
      FORM_OFFSET = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodHandle.class.getDeclaredField("form"));
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError(reflectiveOperationException);
    } 
  }
  
  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface PolymorphicSignature {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */