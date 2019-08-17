package java.lang.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.DelegatingMethodHandle;
import java.lang.invoke.ForceInline;
import java.lang.invoke.InvokerBytecodeGenerator;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.LambdaForm.Hidden;
import java.lang.invoke.LambdaForm.Name;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleImpl.Intrinsic;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SimpleMethodHandle;
import java.lang.invoke.Stable;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Array;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import sun.invoke.empty.Empty;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

abstract class MethodHandleImpl {
  private static final int MAX_ARITY;
  
  private static final Function<MethodHandle, LambdaForm> PRODUCE_BLOCK_INLINING_FORM;
  
  private static final Function<MethodHandle, LambdaForm> PRODUCE_REINVOKER_FORM;
  
  static MethodHandle[] FAKE_METHOD_HANDLE_INVOKE;
  
  private static final Object[] NO_ARGS_ARRAY;
  
  private static final int FILL_ARRAYS_COUNT = 11;
  
  private static final int LEFT_ARGS = 10;
  
  private static final MethodHandle[] FILL_ARRAY_TO_RIGHT;
  
  private static final ClassValue<MethodHandle[]> TYPED_COLLECTORS;
  
  static final int MAX_JVM_ARITY = 255;
  
  static void initStatics() { MemberName.Factory.INSTANCE.getClass(); }
  
  static MethodHandle makeArrayElementAccessor(Class<?> paramClass, boolean paramBoolean) {
    if (paramClass == Object[].class)
      return paramBoolean ? ArrayAccessor.OBJECT_ARRAY_SETTER : ArrayAccessor.OBJECT_ARRAY_GETTER; 
    if (!paramClass.isArray())
      throw MethodHandleStatics.newIllegalArgumentException("not an array: " + paramClass); 
    MethodHandle[] arrayOfMethodHandle = (MethodHandle[])ArrayAccessor.TYPED_ACCESSORS.get(paramClass);
    boolean bool = paramBoolean ? 1 : 0;
    MethodHandle methodHandle = arrayOfMethodHandle[bool];
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = ArrayAccessor.getAccessor(paramClass, paramBoolean);
    MethodType methodType = ArrayAccessor.correctType(paramClass, paramBoolean);
    if (methodHandle.type() != methodType) {
      assert methodHandle.type().parameterType(false) == Object[].class;
      assert (paramBoolean ? methodHandle.type().parameterType(2) : methodHandle.type().returnType()) == Object.class;
      assert paramBoolean || methodType.parameterType(false).getComponentType() == methodType.returnType();
      methodHandle = methodHandle.viewAsType(methodType, false);
    } 
    methodHandle = makeIntrinsic(methodHandle, paramBoolean ? Intrinsic.ARRAY_STORE : Intrinsic.ARRAY_LOAD);
    synchronized (arrayOfMethodHandle) {
      if (arrayOfMethodHandle[bool] == null) {
        arrayOfMethodHandle[bool] = methodHandle;
      } else {
        methodHandle = arrayOfMethodHandle[bool];
      } 
    } 
    return methodHandle;
  }
  
  static MethodHandle makePairwiseConvert(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    MethodType methodType = paramMethodHandle.type();
    return (paramMethodType == methodType) ? paramMethodHandle : makePairwiseConvertByEditor(paramMethodHandle, paramMethodType, paramBoolean1, paramBoolean2);
  }
  
  private static int countNonNull(Object[] paramArrayOfObject) {
    byte b = 0;
    for (Object object : paramArrayOfObject) {
      if (object != null)
        b++; 
    } 
    return b;
  }
  
  static MethodHandle makePairwiseConvertByEditor(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    Object[] arrayOfObject = computeValueConversions(paramMethodType, paramMethodHandle.type(), paramBoolean1, paramBoolean2);
    int i = countNonNull(arrayOfObject);
    if (i == 0)
      return paramMethodHandle.viewAsType(paramMethodType, paramBoolean1); 
    MethodType methodType1 = paramMethodType.basicType();
    MethodType methodType2 = paramMethodHandle.type().basicType();
    BoundMethodHandle boundMethodHandle = paramMethodHandle.rebind();
    for (byte b = 0; b < arrayOfObject.length - 1; b++) {
      Object object1 = arrayOfObject[b];
      if (object1 != null) {
        MethodHandle methodHandle;
        if (object1 instanceof Class) {
          methodHandle = Lazy.MH_castReference.bindTo(object1);
        } else {
          methodHandle = (MethodHandle)object1;
        } 
        Class clazz = methodType1.parameterType(b);
        if (--i == 0) {
          methodType2 = paramMethodType;
        } else {
          methodType2 = methodType2.changeParameterType(b, clazz);
        } 
        LambdaForm lambdaForm = boundMethodHandle.editor().filterArgumentForm(1 + b, LambdaForm.BasicType.basicType(clazz));
        boundMethodHandle = boundMethodHandle.copyWithExtendL(methodType2, lambdaForm, methodHandle);
        boundMethodHandle = boundMethodHandle.rebind();
      } 
    } 
    Object object = arrayOfObject[arrayOfObject.length - 1];
    if (object != null) {
      MethodHandle methodHandle;
      if (object instanceof Class) {
        if (object == void.class) {
          methodHandle = null;
        } else {
          methodHandle = Lazy.MH_castReference.bindTo(object);
        } 
      } else {
        methodHandle = (MethodHandle)object;
      } 
      Class clazz = methodType1.returnType();
      assert --i == 0;
      methodType2 = paramMethodType;
      if (methodHandle != null) {
        boundMethodHandle = boundMethodHandle.rebind();
        LambdaForm lambdaForm = boundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType(clazz), false);
        boundMethodHandle = boundMethodHandle.copyWithExtendL(methodType2, lambdaForm, methodHandle);
      } else {
        LambdaForm lambdaForm = boundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType(clazz), true);
        boundMethodHandle = boundMethodHandle.copyWith(methodType2, lambdaForm);
      } 
    } 
    assert i == 0;
    assert boundMethodHandle.type().equals(paramMethodType);
    return boundMethodHandle;
  }
  
  static MethodHandle makePairwiseConvertIndirect(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    assert paramMethodHandle.type().parameterCount() == paramMethodType.parameterCount();
    Object[] arrayOfObject1 = computeValueConversions(paramMethodType, paramMethodHandle.type(), paramBoolean1, paramBoolean2);
    int i = paramMethodType.parameterCount();
    int j = countNonNull(arrayOfObject1);
    boolean bool1 = (arrayOfObject1[i] != null) ? 1 : 0;
    boolean bool2 = (paramMethodType.returnType() == void.class) ? 1 : 0;
    if (bool1 && bool2) {
      j--;
      bool1 = false;
    } 
    int k = 1 + i;
    int m = k + j + 1;
    byte b1 = !bool1 ? -1 : (m - 1);
    int n = (!bool1 ? m : b1) - 1;
    byte b2 = bool2 ? -1 : (m - 1);
    MethodType methodType = paramMethodType.basicType().invokerType();
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(m - k, methodType);
    Object[] arrayOfObject2 = new Object[0 + i];
    int i1 = k;
    for (byte b = 0; b < i; b++) {
      Object object1 = arrayOfObject1[b];
      if (object1 == null) {
        arrayOfObject2[false + b] = arrayOfName[true + b];
      } else {
        LambdaForm.Name name;
        if (object1 instanceof Class) {
          Class clazz = (Class)object1;
          name = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { clazz, arrayOfName[true + b] });
        } else {
          MethodHandle methodHandle = (MethodHandle)object1;
          name = new LambdaForm.Name(methodHandle, new Object[] { arrayOfName[true + b] });
        } 
        assert arrayOfName[i1] == null;
        arrayOfName[i1++] = name;
        assert arrayOfObject2[false + b] == null;
        arrayOfObject2[false + b] = name;
      } 
    } 
    assert i1 == n;
    arrayOfName[n] = new LambdaForm.Name(paramMethodHandle, arrayOfObject2);
    Object object = arrayOfObject1[i];
    if (!bool1) {
      assert n == arrayOfName.length - 1;
    } else {
      LambdaForm.Name name;
      if (object == void.class) {
        name = new LambdaForm.Name(LambdaForm.constantZero(LambdaForm.BasicType.basicType(paramMethodType.returnType())), new Object[0]);
      } else if (object instanceof Class) {
        Class clazz = (Class)object;
        name = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { clazz, arrayOfName[n] });
      } else {
        MethodHandle methodHandle = (MethodHandle)object;
        if (methodHandle.type().parameterCount() == 0) {
          name = new LambdaForm.Name(methodHandle, new Object[0]);
        } else {
          name = new LambdaForm.Name(methodHandle, new Object[] { arrayOfName[n] });
        } 
      } 
      assert arrayOfName[b1] == null;
      arrayOfName[b1] = name;
      assert b1 == arrayOfName.length - 1;
    } 
    LambdaForm lambdaForm = new LambdaForm("convert", methodType.parameterCount(), arrayOfName, b2);
    return SimpleMethodHandle.make(paramMethodType, lambdaForm);
  }
  
  @ForceInline
  static <T, U> T castReference(Class<? extends T> paramClass, U paramU) {
    if (paramU != null && !paramClass.isInstance(paramU))
      throw newClassCastException(paramClass, paramU); 
    return (T)paramU;
  }
  
  private static ClassCastException newClassCastException(Class<?> paramClass, Object paramObject) { return new ClassCastException("Cannot cast " + paramObject.getClass().getName() + " to " + paramClass.getName()); }
  
  static Object[] computeValueConversions(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean1, boolean paramBoolean2) {
    int i = paramMethodType1.parameterCount();
    Object[] arrayOfObject = new Object[i + 1];
    for (byte b = 0; b <= i; b++) {
      boolean bool = (b == i) ? 1 : 0;
      Class clazz1 = bool ? paramMethodType2.returnType() : paramMethodType1.parameterType(b);
      Class clazz2 = bool ? paramMethodType1.returnType() : paramMethodType2.parameterType(b);
      if (!VerifyType.isNullConversion(clazz1, clazz2, paramBoolean1))
        arrayOfObject[b] = valueConversion(clazz1, clazz2, paramBoolean1, paramBoolean2); 
    } 
    return arrayOfObject;
  }
  
  static MethodHandle makePairwiseConvert(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean) { return makePairwiseConvert(paramMethodHandle, paramMethodType, paramBoolean, false); }
  
  static Object valueConversion(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean1, boolean paramBoolean2) {
    MethodHandle methodHandle;
    assert !VerifyType.isNullConversion(paramClass1, paramClass2, paramBoolean1);
    if (paramClass2 == void.class)
      return paramClass2; 
    if (paramClass1.isPrimitive()) {
      if (paramClass1 == void.class)
        return void.class; 
      if (paramClass2.isPrimitive()) {
        methodHandle = ValueConversions.convertPrimitive(paramClass1, paramClass2);
      } else {
        Wrapper wrapper = Wrapper.forPrimitiveType(paramClass1);
        methodHandle = ValueConversions.boxExact(wrapper);
        assert methodHandle.type().parameterType(false) == wrapper.primitiveType();
        assert methodHandle.type().returnType() == wrapper.wrapperType();
        if (!VerifyType.isNullConversion(wrapper.wrapperType(), paramClass2, paramBoolean1)) {
          MethodType methodType = MethodType.methodType(paramClass2, paramClass1);
          if (paramBoolean1) {
            methodHandle = methodHandle.asType(methodType);
          } else {
            methodHandle = makePairwiseConvert(methodHandle, methodType, false);
          } 
        } 
      } 
    } else if (paramClass2.isPrimitive()) {
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass2);
      if (paramBoolean2 || paramClass1 == wrapper.wrapperType()) {
        methodHandle = ValueConversions.unboxExact(wrapper, paramBoolean1);
      } else {
        methodHandle = paramBoolean1 ? ValueConversions.unboxWiden(wrapper) : ValueConversions.unboxCast(wrapper);
      } 
    } else {
      return paramClass2;
    } 
    assert methodHandle.type().parameterCount() <= 1 : "pc" + Arrays.asList(new Object[] { paramClass1.getSimpleName(), paramClass2.getSimpleName(), methodHandle });
    return methodHandle;
  }
  
  static MethodHandle makeVarargsCollector(MethodHandle paramMethodHandle, Class<?> paramClass) {
    MethodType methodType = paramMethodHandle.type();
    int i = methodType.parameterCount() - 1;
    if (methodType.parameterType(i) != paramClass)
      paramMethodHandle = paramMethodHandle.asType(methodType.changeParameterType(i, paramClass)); 
    paramMethodHandle = paramMethodHandle.asFixedArity();
    return new AsVarargsCollector(paramMethodHandle, paramClass);
  }
  
  static MethodHandle makeSpreadArguments(MethodHandle paramMethodHandle, Class<?> paramClass, int paramInt1, int paramInt2) {
    MethodType methodType1 = paramMethodHandle.type();
    for (int i = 0; i < paramInt2; i++) {
      Class clazz = VerifyType.spreadArgElementType(paramClass, i);
      if (clazz == null)
        clazz = Object.class; 
      methodType1 = methodType1.changeParameterType(paramInt1 + i, clazz);
    } 
    paramMethodHandle = paramMethodHandle.asType(methodType1);
    MethodType methodType2 = methodType1.replaceParameterTypes(paramInt1, paramInt1 + paramInt2, new Class[] { paramClass });
    MethodType methodType3 = methodType2.invokerType();
    LambdaForm.Name[] arrayOfName1 = LambdaForm.arguments(paramInt2 + 2, methodType3);
    int j = methodType3.parameterCount();
    int[] arrayOfInt = new int[methodType1.parameterCount()];
    byte b1 = 0;
    byte b2;
    for (b2 = 1; b1 < methodType1.parameterCount() + 1; b2++) {
      Class clazz = methodType3.parameterType(b1);
      if (b1 == paramInt1) {
        MethodHandle methodHandle = MethodHandles.arrayElementGetter(paramClass);
        LambdaForm.Name name = arrayOfName1[b2];
        arrayOfName1[j++] = new LambdaForm.Name(Lazy.NF_checkSpreadArgument, new Object[] { name, Integer.valueOf(paramInt2) });
        for (byte b = 0; b < paramInt2; b++) {
          arrayOfInt[b1] = j;
          arrayOfName1[j++] = new LambdaForm.Name(methodHandle, new Object[] { name, Integer.valueOf(b) });
          b1++;
        } 
      } else if (b1 < arrayOfInt.length) {
        arrayOfInt[b1] = b2;
      } 
      b1++;
    } 
    assert j == arrayOfName1.length - 1;
    LambdaForm.Name[] arrayOfName2 = new LambdaForm.Name[methodType1.parameterCount()];
    for (b2 = 0; b2 < methodType1.parameterCount(); b2++) {
      int k = arrayOfInt[b2];
      arrayOfName2[b2] = arrayOfName1[k];
    } 
    arrayOfName1[arrayOfName1.length - 1] = new LambdaForm.Name(paramMethodHandle, (Object[])arrayOfName2);
    LambdaForm lambdaForm = new LambdaForm("spread", methodType3.parameterCount(), arrayOfName1);
    return SimpleMethodHandle.make(methodType2, lambdaForm);
  }
  
  static void checkSpreadArgument(Object paramObject, int paramInt) {
    if (paramObject == null) {
      if (paramInt == 0)
        return; 
    } else if (paramObject instanceof Object[]) {
      int i = (Object[])paramObject.length;
      if (i == paramInt)
        return; 
    } else {
      int i = Array.getLength(paramObject);
      if (i == paramInt)
        return; 
    } 
    throw MethodHandleStatics.newIllegalArgumentException("array is not of length " + paramInt);
  }
  
  static MethodHandle makeCollectArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt, boolean paramBoolean) {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    int i = methodType2.parameterCount();
    Class clazz = methodType2.returnType();
    int j = (clazz == void.class) ? 0 : 1;
    MethodType methodType3 = methodType1.dropParameterTypes(paramInt, paramInt + j);
    if (!paramBoolean)
      methodType3 = methodType3.insertParameterTypes(paramInt, methodType2.parameterList()); 
    MethodType methodType4 = methodType3.invokerType();
    LambdaForm.Name[] arrayOfName1 = LambdaForm.arguments(2, methodType4);
    int k = arrayOfName1.length - 2;
    int m = arrayOfName1.length - 1;
    Name[] arrayOfName = (Name[])Arrays.copyOfRange(arrayOfName1, 1 + paramInt, 1 + paramInt + i);
    arrayOfName1[k] = new LambdaForm.Name(paramMethodHandle2, (Object[])arrayOfName);
    LambdaForm.Name[] arrayOfName2 = new LambdaForm.Name[methodType1.parameterCount()];
    int n = 1;
    int i1 = 0;
    int i2 = paramInt;
    System.arraycopy(arrayOfName1, n, arrayOfName2, i1, i2);
    n += i2;
    i1 += i2;
    if (clazz != void.class)
      arrayOfName2[i1++] = arrayOfName1[k]; 
    i2 = i;
    if (paramBoolean) {
      System.arraycopy(arrayOfName1, n, arrayOfName2, i1, i2);
      i1 += i2;
    } 
    n += i2;
    i2 = arrayOfName2.length - i1;
    System.arraycopy(arrayOfName1, n, arrayOfName2, i1, i2);
    assert n + i2 == k;
    arrayOfName1[m] = new LambdaForm.Name(paramMethodHandle1, (Object[])arrayOfName2);
    LambdaForm lambdaForm = new LambdaForm("collect", methodType4.parameterCount(), arrayOfName1);
    return SimpleMethodHandle.make(methodType3, lambdaForm);
  }
  
  @Hidden
  static MethodHandle selectAlternative(boolean paramBoolean, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) { return paramBoolean ? paramMethodHandle1 : paramMethodHandle2; }
  
  @Hidden
  static boolean profileBoolean(boolean paramBoolean, int[] paramArrayOfInt) {
    boolean bool = paramBoolean ? 1 : 0;
    try {
      paramArrayOfInt[bool] = Math.addExact(paramArrayOfInt[bool], 1);
    } catch (ArithmeticException arithmeticException) {
      paramArrayOfInt[bool] = paramArrayOfInt[bool] / 2;
    } 
    return paramBoolean;
  }
  
  static MethodHandle makeGuardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
    BoundMethodHandle boundMethodHandle;
    MethodType methodType1 = paramMethodHandle2.type();
    assert paramMethodHandle1.type().equals(methodType1.changeReturnType(boolean.class)) && paramMethodHandle3.type().equals(methodType1);
    MethodType methodType2 = methodType1.basicType();
    LambdaForm lambdaForm = makeGuardWithTestForm(methodType2);
    try {
      if (MethodHandleStatics.PROFILE_GWT) {
        int[] arrayOfInt = new int[2];
        boundMethodHandle = BoundMethodHandle.speciesData_LLLL().constructor().invokeBasic(methodType1, lambdaForm, paramMethodHandle1, profile(paramMethodHandle2), profile(paramMethodHandle3), arrayOfInt);
      } else {
        boundMethodHandle = BoundMethodHandle.speciesData_LLL().constructor().invokeBasic(methodType1, lambdaForm, paramMethodHandle1, profile(paramMethodHandle2), profile(paramMethodHandle3));
      } 
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
    assert boundMethodHandle.type() == methodType1;
    return boundMethodHandle;
  }
  
  static MethodHandle profile(MethodHandle paramMethodHandle) { return (MethodHandleStatics.DONT_INLINE_THRESHOLD >= 0) ? makeBlockInlningWrapper(paramMethodHandle) : paramMethodHandle; }
  
  static MethodHandle makeBlockInlningWrapper(MethodHandle paramMethodHandle) {
    LambdaForm lambdaForm = (LambdaForm)PRODUCE_BLOCK_INLINING_FORM.apply(paramMethodHandle);
    return new CountingWrapper(paramMethodHandle, lambdaForm, PRODUCE_BLOCK_INLINING_FORM, PRODUCE_REINVOKER_FORM, MethodHandleStatics.DONT_INLINE_THRESHOLD, null);
  }
  
  static LambdaForm makeGuardWithTestForm(MethodType paramMethodType) {
    LambdaForm lambdaForm = paramMethodType.form().cachedLambdaForm(17);
    if (lambdaForm != null)
      return lambdaForm; 
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = j++;
    int m = j++;
    int n = j++;
    int i1 = MethodHandleStatics.PROFILE_GWT ? j++ : -1;
    int i2 = j++;
    int i3 = (i1 != -1) ? j++ : -1;
    int i4 = j - 1;
    int i5 = j++;
    int i6 = j++;
    assert i6 == i5 + 1;
    MethodType methodType1 = paramMethodType.invokerType();
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, methodType1);
    BoundMethodHandle.SpeciesData speciesData = (i1 != -1) ? BoundMethodHandle.speciesData_LLLL() : BoundMethodHandle.speciesData_LLL();
    arrayOfName[0] = arrayOfName[0].withConstraint(speciesData);
    arrayOfName[k] = new LambdaForm.Name(speciesData.getterFunction(0), new Object[] { arrayOfName[0] });
    arrayOfName[m] = new LambdaForm.Name(speciesData.getterFunction(1), new Object[] { arrayOfName[0] });
    arrayOfName[n] = new LambdaForm.Name(speciesData.getterFunction(2), new Object[] { arrayOfName[0] });
    if (i1 != -1)
      arrayOfName[i1] = new LambdaForm.Name(speciesData.getterFunction(3), new Object[] { arrayOfName[0] }); 
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, i, Object[].class);
    MethodType methodType2 = paramMethodType.changeReturnType(boolean.class).basicType();
    arrayOfObject[0] = arrayOfName[k];
    arrayOfName[i2] = new LambdaForm.Name(methodType2, arrayOfObject);
    if (i3 != -1)
      arrayOfName[i3] = new LambdaForm.Name(Lazy.NF_profileBoolean, new Object[] { arrayOfName[i2], arrayOfName[i1] }); 
    arrayOfName[i5] = new LambdaForm.Name(Lazy.MH_selectAlternative, new Object[] { arrayOfName[i4], arrayOfName[m], arrayOfName[n] });
    arrayOfObject[0] = arrayOfName[i5];
    arrayOfName[i6] = new LambdaForm.Name(paramMethodType, arrayOfObject);
    lambdaForm = new LambdaForm("guard", methodType1.parameterCount(), arrayOfName, true);
    return paramMethodType.form().setCachedLambdaForm(17, lambdaForm);
  }
  
  private static LambdaForm makeGuardWithCatchForm(MethodType paramMethodType) {
    MethodType methodType1 = paramMethodType.invokerType();
    LambdaForm lambdaForm = paramMethodType.form().cachedLambdaForm(16);
    if (lambdaForm != null)
      return lambdaForm; 
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = j++;
    int m = j++;
    int n = j++;
    int i1 = j++;
    int i2 = j++;
    int i3 = j++;
    int i4 = j++;
    int i5 = j++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, methodType1);
    BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_LLLLL();
    arrayOfName[0] = arrayOfName[0].withConstraint(speciesData);
    arrayOfName[k] = new LambdaForm.Name(speciesData.getterFunction(0), new Object[] { arrayOfName[0] });
    arrayOfName[m] = new LambdaForm.Name(speciesData.getterFunction(1), new Object[] { arrayOfName[0] });
    arrayOfName[n] = new LambdaForm.Name(speciesData.getterFunction(2), new Object[] { arrayOfName[0] });
    arrayOfName[i1] = new LambdaForm.Name(speciesData.getterFunction(3), new Object[] { arrayOfName[0] });
    arrayOfName[i2] = new LambdaForm.Name(speciesData.getterFunction(4), new Object[] { arrayOfName[0] });
    MethodType methodType2 = paramMethodType.changeReturnType(Object.class);
    MethodHandle methodHandle1 = MethodHandles.basicInvoker(methodType2);
    Object[] arrayOfObject1 = new Object[methodHandle1.type().parameterCount()];
    arrayOfObject1[0] = arrayOfName[i1];
    System.arraycopy(arrayOfName, 1, arrayOfObject1, 1, i - 1);
    arrayOfName[i3] = new LambdaForm.Name(makeIntrinsic(methodHandle1, Intrinsic.GUARD_WITH_CATCH), arrayOfObject1);
    Object[] arrayOfObject2 = { arrayOfName[k], arrayOfName[m], arrayOfName[n], arrayOfName[i3] };
    arrayOfName[i4] = new LambdaForm.Name(Lazy.NF_guardWithCatch, arrayOfObject2);
    MethodHandle methodHandle2 = MethodHandles.basicInvoker(MethodType.methodType(paramMethodType.rtype(), Object.class));
    Object[] arrayOfObject3 = { arrayOfName[i2], arrayOfName[i4] };
    arrayOfName[i5] = new LambdaForm.Name(methodHandle2, arrayOfObject3);
    lambdaForm = new LambdaForm("guardWithCatch", methodType1.parameterCount(), arrayOfName);
    return paramMethodType.form().setCachedLambdaForm(16, lambdaForm);
  }
  
  static MethodHandle makeGuardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2) {
    BoundMethodHandle boundMethodHandle;
    MethodHandle methodHandle2;
    MethodType methodType1 = paramMethodHandle1.type();
    LambdaForm lambdaForm = makeGuardWithCatchForm(methodType1.basicType());
    MethodType methodType2 = methodType1.changeReturnType(Object[].class);
    MethodHandle methodHandle1 = varargsArray(methodType1.parameterCount()).asType(methodType2);
    Class clazz = methodType1.returnType();
    if (clazz.isPrimitive()) {
      if (clazz == void.class) {
        methodHandle2 = ValueConversions.ignore();
      } else {
        Wrapper wrapper = Wrapper.forPrimitiveType(methodType1.returnType());
        methodHandle2 = ValueConversions.unboxExact(wrapper);
      } 
    } else {
      methodHandle2 = MethodHandles.identity(Object.class);
    } 
    BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_LLLLL();
    try {
      boundMethodHandle = speciesData.constructor().invokeBasic(methodType1, lambdaForm, paramMethodHandle1, paramClass, paramMethodHandle2, methodHandle1, methodHandle2);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
    assert boundMethodHandle.type() == methodType1;
    return boundMethodHandle;
  }
  
  @Hidden
  static Object guardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2, Object... paramVarArgs) throws Throwable {
    try {
      return paramMethodHandle1.asFixedArity().invokeWithArguments(paramVarArgs);
    } catch (Throwable throwable) {
      if (!paramClass.isInstance(throwable))
        throw throwable; 
      return paramMethodHandle2.asFixedArity().invokeWithArguments(prepend(throwable, paramVarArgs));
    } 
  }
  
  @Hidden
  private static Object[] prepend(Object paramObject, Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length + 1];
    arrayOfObject[0] = paramObject;
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 1, paramArrayOfObject.length);
    return arrayOfObject;
  }
  
  static MethodHandle throwException(MethodType paramMethodType) {
    assert Throwable.class.isAssignableFrom(paramMethodType.parameterType(0));
    int i = paramMethodType.parameterCount();
    if (i > 1) {
      null = throwException(paramMethodType.dropParameterTypes(1, i));
      return MethodHandles.dropArguments(null, 1, paramMethodType.parameterList().subList(1, i));
    } 
    return makePairwiseConvert(Lazy.NF_throwException.resolvedHandle(), paramMethodType, false, true);
  }
  
  static <T extends Throwable> Empty throwException(T paramT) throws T { throw paramT; }
  
  static MethodHandle fakeMethodHandleInvoke(MemberName paramMemberName) {
    boolean bool;
    assert paramMemberName.isMethodHandleInvoke();
    switch (paramMemberName.getName()) {
      case "invoke":
        bool = false;
        break;
      case "invokeExact":
        bool = true;
        break;
      default:
        throw new InternalError(paramMemberName.getName());
    } 
    MethodHandle methodHandle = FAKE_METHOD_HANDLE_INVOKE[bool];
    if (methodHandle != null)
      return methodHandle; 
    MethodType methodType = MethodType.methodType(Object.class, UnsupportedOperationException.class, new Class[] { MethodHandle.class, Object[].class });
    methodHandle = throwException(methodType);
    methodHandle = methodHandle.bindTo(new UnsupportedOperationException("cannot reflectively invoke MethodHandle"));
    if (!paramMemberName.getInvocationType().equals(methodHandle.type()))
      throw new InternalError(paramMemberName.toString()); 
    methodHandle = methodHandle.withInternalMemberName(paramMemberName, false);
    methodHandle = methodHandle.asVarargsCollector(Object[].class);
    assert paramMemberName.isVarargs();
    FAKE_METHOD_HANDLE_INVOKE[bool] = methodHandle;
    return methodHandle;
  }
  
  static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass) { return BindCaller.bindCaller(paramMethodHandle, paramClass); }
  
  static MethodHandle makeWrappedMember(MethodHandle paramMethodHandle, MemberName paramMemberName, boolean paramBoolean) { return (paramMemberName.equals(paramMethodHandle.internalMemberName()) && paramBoolean == paramMethodHandle.isInvokeSpecial()) ? paramMethodHandle : new WrappedMember(paramMethodHandle, paramMethodHandle.type(), paramMemberName, paramBoolean, null, null); }
  
  static MethodHandle makeIntrinsic(MethodHandle paramMethodHandle, Intrinsic paramIntrinsic) { return (paramIntrinsic == paramMethodHandle.intrinsicName()) ? paramMethodHandle : new IntrinsicMethodHandle(paramMethodHandle, paramIntrinsic); }
  
  static MethodHandle makeIntrinsic(MethodType paramMethodType, LambdaForm paramLambdaForm, Intrinsic paramIntrinsic) { return new IntrinsicMethodHandle(SimpleMethodHandle.make(paramMethodType, paramLambdaForm), paramIntrinsic); }
  
  private static MethodHandle findCollector(String paramString, int paramInt, Class<?> paramClass, Class<?>... paramVarArgs) {
    MethodType methodType = MethodType.genericMethodType(paramInt).changeReturnType(paramClass).insertParameterTypes(0, paramVarArgs);
    try {
      return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, paramString, methodType);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      return null;
    } 
  }
  
  private static Object[] makeArray(Object... paramVarArgs) { return paramVarArgs; }
  
  private static Object[] array() { return NO_ARGS_ARRAY; }
  
  private static Object[] array(Object paramObject) { return makeArray(new Object[] { paramObject }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2) { return makeArray(new Object[] { paramObject1, paramObject2 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 }); }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 }); }
  
  private static MethodHandle[] makeArrays() {
    ArrayList arrayList = new ArrayList();
    while (true) {
      MethodHandle methodHandle = findCollector("array", arrayList.size(), Object[].class, new Class[0]);
      if (methodHandle == null)
        break; 
      methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY);
      arrayList.add(methodHandle);
    } 
    assert arrayList.size() == 11;
    return (MethodHandle[])arrayList.toArray(new MethodHandle[MAX_ARITY + 1]);
  }
  
  private static Object[] fillNewArray(Integer paramInteger, Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramInteger.intValue()];
    fillWithArguments(arrayOfObject, 0, paramArrayOfObject);
    return arrayOfObject;
  }
  
  private static Object[] fillNewTypedArray(Object[] paramArrayOfObject1, Integer paramInteger, Object[] paramArrayOfObject2) {
    Object[] arrayOfObject = Arrays.copyOf(paramArrayOfObject1, paramInteger.intValue());
    assert arrayOfObject.getClass() != Object[].class;
    fillWithArguments(arrayOfObject, 0, paramArrayOfObject2);
    return arrayOfObject;
  }
  
  private static void fillWithArguments(Object[] paramArrayOfObject1, int paramInt, Object... paramVarArgs1) { System.arraycopy(paramVarArgs1, 0, paramArrayOfObject1, paramInt, paramVarArgs1.length); }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
    return paramArrayOfObject;
  }
  
  private static MethodHandle[] makeFillArrays() {
    ArrayList arrayList = new ArrayList();
    arrayList.add(null);
    while (true) {
      MethodHandle methodHandle = findCollector("fillArray", arrayList.size(), Object[].class, new Class[] { Integer.class, Object[].class });
      if (methodHandle == null)
        break; 
      arrayList.add(methodHandle);
    } 
    assert arrayList.size() == 11;
    return (MethodHandle[])arrayList.toArray(new MethodHandle[0]);
  }
  
  private static Object copyAsPrimitiveArray(Wrapper paramWrapper, Object... paramVarArgs) {
    Object object = paramWrapper.makeArray(paramVarArgs.length);
    paramWrapper.copyArrayUnboxing(paramVarArgs, 0, object, 0, paramVarArgs.length);
    return object;
  }
  
  static MethodHandle varargsArray(int paramInt) {
    MethodHandle methodHandle = ARRAYS[paramInt];
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = findCollector("array", paramInt, Object[].class, new Class[0]);
    if (methodHandle != null)
      methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY); 
    if (methodHandle != null) {
      ARRAYS[paramInt] = methodHandle;
      return methodHandle;
    } 
    methodHandle = buildVarargsArray(Lazy.MH_fillNewArray, Lazy.MH_arrayIdentity, paramInt);
    assert assertCorrectArity(methodHandle, paramInt);
    methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY);
    ARRAYS[paramInt] = methodHandle;
    return methodHandle;
  }
  
  private static boolean assertCorrectArity(MethodHandle paramMethodHandle, int paramInt) {
    assert paramMethodHandle.type().parameterCount() == paramInt : "arity != " + paramInt + ": " + paramMethodHandle;
    return true;
  }
  
  static <T> T[] identity(T[] paramArrayOfT) { return paramArrayOfT; }
  
  private static MethodHandle buildVarargsArray(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt) {
    int i = Math.min(paramInt, 10);
    int j = paramInt - i;
    MethodHandle methodHandle1 = paramMethodHandle1.bindTo(Integer.valueOf(paramInt));
    methodHandle1 = methodHandle1.asCollector(Object[].class, i);
    MethodHandle methodHandle2 = paramMethodHandle2;
    if (j > 0) {
      MethodHandle methodHandle = fillToRight(10 + j);
      if (methodHandle2 == Lazy.MH_arrayIdentity) {
        methodHandle2 = methodHandle;
      } else {
        methodHandle2 = MethodHandles.collectArguments(methodHandle2, 0, methodHandle);
      } 
    } 
    if (methodHandle2 == Lazy.MH_arrayIdentity) {
      methodHandle2 = methodHandle1;
    } else {
      methodHandle2 = MethodHandles.collectArguments(methodHandle2, 0, methodHandle1);
    } 
    return methodHandle2;
  }
  
  private static MethodHandle fillToRight(int paramInt) {
    MethodHandle methodHandle = FILL_ARRAY_TO_RIGHT[paramInt];
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = buildFiller(paramInt);
    assert assertCorrectArity(methodHandle, paramInt - 10 + 1);
    FILL_ARRAY_TO_RIGHT[paramInt] = methodHandle;
    return methodHandle;
  }
  
  private static MethodHandle buildFiller(int paramInt) {
    if (paramInt <= 10)
      return Lazy.MH_arrayIdentity; 
    int i = paramInt % 10;
    int j = paramInt - i;
    if (i == 0) {
      j = paramInt - (i = 10);
      if (FILL_ARRAY_TO_RIGHT[j] == null)
        for (byte b = 0; b < j; b += 10) {
          if (b > 10)
            fillToRight(b); 
        }  
    } 
    if (j < 10)
      i = paramInt - (j = 10); 
    assert i > 0;
    MethodHandle methodHandle1 = fillToRight(j);
    MethodHandle methodHandle2 = FILL_ARRAYS[i].bindTo(Integer.valueOf(j));
    assert methodHandle1.type().parameterCount() == 1 + j - 10;
    assert methodHandle2.type().parameterCount() == 1 + i;
    return (j == 10) ? methodHandle2 : MethodHandles.collectArguments(methodHandle2, 0, methodHandle1);
  }
  
  static MethodHandle varargsArray(Class<?> paramClass, int paramInt) {
    Class clazz = paramClass.getComponentType();
    if (clazz == null)
      throw new IllegalArgumentException("not an array: " + paramClass); 
    if (paramInt >= 126) {
      int i = paramInt;
      if (i <= 254 && clazz.isPrimitive())
        i *= Wrapper.forPrimitiveType(clazz).stackSlots(); 
      if (i > 254)
        throw new IllegalArgumentException("too many arguments: " + paramClass.getSimpleName() + ", length " + paramInt); 
    } 
    if (clazz == Object.class)
      return varargsArray(paramInt); 
    MethodHandle[] arrayOfMethodHandle = (MethodHandle[])TYPED_COLLECTORS.get(clazz);
    MethodHandle methodHandle = (paramInt < arrayOfMethodHandle.length) ? arrayOfMethodHandle[paramInt] : null;
    if (methodHandle != null)
      return methodHandle; 
    if (paramInt == 0) {
      Object object = Array.newInstance(paramClass.getComponentType(), 0);
      methodHandle = MethodHandles.constant(paramClass, object);
    } else if (clazz.isPrimitive()) {
      MethodHandle methodHandle1 = Lazy.MH_fillNewArray;
      MethodHandle methodHandle2 = buildArrayProducer(paramClass);
      methodHandle = buildVarargsArray(methodHandle1, methodHandle2, paramInt);
    } else {
      Class clazz1 = paramClass.asSubclass(Object[].class);
      Object[] arrayOfObject = Arrays.copyOf(NO_ARGS_ARRAY, 0, clazz1);
      MethodHandle methodHandle1 = Lazy.MH_fillNewTypedArray.bindTo(arrayOfObject);
      MethodHandle methodHandle2 = Lazy.MH_arrayIdentity;
      methodHandle = buildVarargsArray(methodHandle1, methodHandle2, paramInt);
    } 
    methodHandle = methodHandle.asType(MethodType.methodType(paramClass, Collections.nCopies(paramInt, clazz)));
    methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY);
    assert assertCorrectArity(methodHandle, paramInt);
    if (paramInt < arrayOfMethodHandle.length)
      arrayOfMethodHandle[paramInt] = methodHandle; 
    return methodHandle;
  }
  
  private static MethodHandle buildArrayProducer(Class<?> paramClass) {
    Class clazz = paramClass.getComponentType();
    assert clazz.isPrimitive();
    return Lazy.MH_copyAsPrimitiveArray.bindTo(Wrapper.forPrimitiveType(clazz));
  }
  
  static void assertSame(Object paramObject1, Object paramObject2) {
    if (paramObject1 != paramObject2) {
      String str = String.format("mh1 != mh2: mh1 = %s (form: %s); mh2 = %s (form: %s)", new Object[] { paramObject1, ((MethodHandle)paramObject1).form, paramObject2, ((MethodHandle)paramObject2).form });
      throw MethodHandleStatics.newInternalError(str);
    } 
  }
  
  static  {
    final Object[] values = { Integer.valueOf(255) };
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            values[0] = Integer.getInteger(MethodHandleImpl.class.getName() + ".MAX_ARITY", 255);
            return null;
          }
        });
    MAX_ARITY = ((Integer)arrayOfObject[0]).intValue();
    PRODUCE_BLOCK_INLINING_FORM = new Function<MethodHandle, LambdaForm>() {
        public LambdaForm apply(MethodHandle param1MethodHandle) { return DelegatingMethodHandle.makeReinvokerForm(param1MethodHandle, 9, MethodHandleImpl.CountingWrapper.class, "reinvoker.dontInline", false, DelegatingMethodHandle.NF_getTarget, MethodHandleImpl.CountingWrapper.NF_maybeStopCounting); }
      };
    PRODUCE_REINVOKER_FORM = new Function<MethodHandle, LambdaForm>() {
        public LambdaForm apply(MethodHandle param1MethodHandle) { return DelegatingMethodHandle.makeReinvokerForm(param1MethodHandle, 8, DelegatingMethodHandle.class, DelegatingMethodHandle.NF_getTarget); }
      };
    FAKE_METHOD_HANDLE_INVOKE = new MethodHandle[2];
    NO_ARGS_ARRAY = new Object[0];
    FILL_ARRAY_TO_RIGHT = new MethodHandle[MAX_ARITY + 1];
    TYPED_COLLECTORS = new ClassValue<MethodHandle[]>() {
        protected MethodHandle[] computeValue(Class<?> param1Class) { return new MethodHandle[256]; }
      };
  }
  
  static final class ArrayAccessor {
    static final int GETTER_INDEX = 0;
    
    static final int SETTER_INDEX = 1;
    
    static final int INDEX_LIMIT = 2;
    
    static final ClassValue<MethodHandle[]> TYPED_ACCESSORS = new ClassValue<MethodHandle[]>() {
        protected MethodHandle[] computeValue(Class<?> param2Class) { return new MethodHandle[2]; }
      };
    
    static final MethodHandle OBJECT_ARRAY_GETTER;
    
    static final MethodHandle OBJECT_ARRAY_SETTER;
    
    static int getElementI(int[] param1ArrayOfInt, int param1Int) { return param1ArrayOfInt[param1Int]; }
    
    static long getElementJ(long[] param1ArrayOfLong, int param1Int) { return param1ArrayOfLong[param1Int]; }
    
    static float getElementF(float[] param1ArrayOfFloat, int param1Int) { return param1ArrayOfFloat[param1Int]; }
    
    static double getElementD(double[] param1ArrayOfDouble, int param1Int) { return param1ArrayOfDouble[param1Int]; }
    
    static boolean getElementZ(boolean[] param1ArrayOfBoolean, int param1Int) { return param1ArrayOfBoolean[param1Int]; }
    
    static byte getElementB(byte[] param1ArrayOfByte, int param1Int) { return param1ArrayOfByte[param1Int]; }
    
    static short getElementS(short[] param1ArrayOfShort, int param1Int) { return param1ArrayOfShort[param1Int]; }
    
    static char getElementC(char[] param1ArrayOfChar, int param1Int) { return param1ArrayOfChar[param1Int]; }
    
    static Object getElementL(Object[] param1ArrayOfObject, int param1Int) { return param1ArrayOfObject[param1Int]; }
    
    static void setElementI(int[] param1ArrayOfInt, int param1Int1, int param1Int2) { param1ArrayOfInt[param1Int1] = param1Int2; }
    
    static void setElementJ(long[] param1ArrayOfLong, int param1Int, long param1Long) { param1ArrayOfLong[param1Int] = param1Long; }
    
    static void setElementF(float[] param1ArrayOfFloat, int param1Int, float param1Float) { param1ArrayOfFloat[param1Int] = param1Float; }
    
    static void setElementD(double[] param1ArrayOfDouble, int param1Int, double param1Double) { param1ArrayOfDouble[param1Int] = param1Double; }
    
    static void setElementZ(boolean[] param1ArrayOfBoolean, int param1Int, boolean param1Boolean) { param1ArrayOfBoolean[param1Int] = param1Boolean; }
    
    static void setElementB(byte[] param1ArrayOfByte, int param1Int, byte param1Byte) { param1ArrayOfByte[param1Int] = param1Byte; }
    
    static void setElementS(short[] param1ArrayOfShort, int param1Int, short param1Short) { param1ArrayOfShort[param1Int] = param1Short; }
    
    static void setElementC(char[] param1ArrayOfChar, int param1Int, char param1Char) { param1ArrayOfChar[param1Int] = param1Char; }
    
    static void setElementL(Object[] param1ArrayOfObject, int param1Int, Object param1Object) { param1ArrayOfObject[param1Int] = param1Object; }
    
    static String name(Class<?> param1Class, boolean param1Boolean) {
      Class clazz = param1Class.getComponentType();
      if (clazz == null)
        throw MethodHandleStatics.newIllegalArgumentException("not an array", param1Class); 
      return (!param1Boolean ? "getElement" : "setElement") + Wrapper.basicTypeChar(clazz);
    }
    
    static MethodType type(Class<?> param1Class, boolean param1Boolean) {
      Class clazz1 = param1Class.getComponentType();
      Class<?> clazz2 = param1Class;
      if (!clazz1.isPrimitive()) {
        clazz2 = Object[].class;
        clazz1 = Object.class;
      } 
      return !param1Boolean ? MethodType.methodType(clazz1, clazz2, new Class[] { int.class }) : MethodType.methodType(void.class, clazz2, new Class[] { int.class, clazz1 });
    }
    
    static MethodType correctType(Class<?> param1Class, boolean param1Boolean) {
      Class clazz = param1Class.getComponentType();
      return !param1Boolean ? MethodType.methodType(clazz, param1Class, new Class[] { int.class }) : MethodType.methodType(void.class, param1Class, new Class[] { int.class, clazz });
    }
    
    static MethodHandle getAccessor(Class<?> param1Class, boolean param1Boolean) {
      String str = name(param1Class, param1Boolean);
      MethodType methodType = type(param1Class, param1Boolean);
      try {
        return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(ArrayAccessor.class, str, methodType);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.uncaughtException(reflectiveOperationException);
      } 
    }
    
    static  {
      MethodHandle[] arrayOfMethodHandle = (MethodHandle[])TYPED_ACCESSORS.get(Object[].class);
      arrayOfMethodHandle[0] = OBJECT_ARRAY_GETTER = MethodHandleImpl.makeIntrinsic(getAccessor(Object[].class, false), MethodHandleImpl.Intrinsic.ARRAY_LOAD);
      arrayOfMethodHandle[1] = OBJECT_ARRAY_SETTER = MethodHandleImpl.makeIntrinsic(getAccessor(Object[].class, true), MethodHandleImpl.Intrinsic.ARRAY_STORE);
      assert InvokerBytecodeGenerator.isStaticallyInvocable(OBJECT_ARRAY_GETTER.internalMemberName());
      assert InvokerBytecodeGenerator.isStaticallyInvocable(OBJECT_ARRAY_SETTER.internalMemberName());
    }
  }
  
  private static final class AsVarargsCollector extends DelegatingMethodHandle {
    private final MethodHandle target;
    
    private final Class<?> arrayType;
    
    @Stable
    private MethodHandle asCollectorCache;
    
    AsVarargsCollector(MethodHandle param1MethodHandle, Class<?> param1Class) { this(param1MethodHandle.type(), param1MethodHandle, param1Class); }
    
    AsVarargsCollector(MethodType param1MethodType, MethodHandle param1MethodHandle, Class<?> param1Class) {
      super(param1MethodType, param1MethodHandle);
      this.target = param1MethodHandle;
      this.arrayType = param1Class;
      this.asCollectorCache = param1MethodHandle.asCollector(param1Class, 0);
    }
    
    public boolean isVarargsCollector() { return true; }
    
    protected MethodHandle getTarget() { return this.target; }
    
    public MethodHandle asFixedArity() { return this.target; }
    
    MethodHandle setVarargs(MemberName param1MemberName) { return param1MemberName.isVarargs() ? this : asFixedArity(); }
    
    public MethodHandle asTypeUncached(MethodType param1MethodType) {
      MethodHandle methodHandle2;
      MethodType methodType = type();
      int i = methodType.parameterCount() - 1;
      int j = param1MethodType.parameterCount();
      if (j == i + 1 && methodType.parameterType(i).isAssignableFrom(param1MethodType.parameterType(i)))
        return this.asTypeCache = asFixedArity().asType(param1MethodType); 
      MethodHandle methodHandle1 = this.asCollectorCache;
      if (methodHandle1 != null && methodHandle1.type().parameterCount() == j)
        return this.asTypeCache = methodHandle1.asType(param1MethodType); 
      int k = j - i;
      try {
        methodHandle2 = asFixedArity().asCollector(this.arrayType, k);
        assert methodHandle2.type().parameterCount() == j : "newArity=" + j + " but collector=" + methodHandle2;
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new WrongMethodTypeException("cannot build collector", illegalArgumentException);
      } 
      this.asCollectorCache = methodHandle2;
      return this.asTypeCache = methodHandle2.asType(param1MethodType);
    }
    
    boolean viewAsTypeChecks(MethodType param1MethodType, boolean param1Boolean) {
      super.viewAsTypeChecks(param1MethodType, true);
      if (param1Boolean)
        return true; 
      assert type().lastParameterType().getComponentType().isAssignableFrom(param1MethodType.lastParameterType().getComponentType()) : Arrays.asList(new Object[] { this, param1MethodType });
      return true;
    }
  }
  
  private static class BindCaller {
    private static ClassValue<MethodHandle> CV_makeInjectedInvoker = new ClassValue<MethodHandle>() {
        protected MethodHandle computeValue(Class<?> param2Class) { return MethodHandleImpl.BindCaller.makeInjectedInvoker(param2Class); }
      };
    
    private static final MethodHandle MH_checkCallerClass;
    
    private static final byte[] T_BYTES;
    
    static MethodHandle bindCaller(MethodHandle param1MethodHandle, Class<?> param1Class) {
      if (param1Class == null || param1Class.isArray() || param1Class.isPrimitive() || param1Class.getName().startsWith("java.") || param1Class.getName().startsWith("sun."))
        throw new InternalError(); 
      MethodHandle methodHandle1 = prepareForInvoker(param1MethodHandle);
      MethodHandle methodHandle2 = (MethodHandle)CV_makeInjectedInvoker.get(param1Class);
      return restoreToType(methodHandle2.bindTo(methodHandle1), param1MethodHandle, param1Class);
    }
    
    private static MethodHandle makeInjectedInvoker(Class<?> param1Class) {
      MethodHandle methodHandle;
      Class clazz = MethodHandleStatics.UNSAFE.defineAnonymousClass(param1Class, T_BYTES, null);
      if (param1Class.getClassLoader() != clazz.getClassLoader())
        throw new InternalError(param1Class.getName() + " (CL)"); 
      try {
        if (param1Class.getProtectionDomain() != clazz.getProtectionDomain())
          throw new InternalError(param1Class.getName() + " (PD)"); 
      } catch (SecurityException null) {}
      try {
        methodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(clazz, "init", MethodType.methodType(void.class));
        methodHandle.invokeExact();
      } catch (Throwable null) {
        throw MethodHandleStatics.uncaughtException(methodHandle);
      } 
      try {
        MethodType methodType = MethodType.methodType(Object.class, MethodHandle.class, new Class[] { Object[].class });
        methodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(clazz, "invoke_V", methodType);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.uncaughtException(reflectiveOperationException);
      } 
      try {
        MethodHandle methodHandle1 = prepareForInvoker(MH_checkCallerClass);
        Object object = methodHandle.invokeExact(methodHandle1, new Object[] { param1Class, clazz });
      } catch (Throwable throwable) {
        throw new InternalError(throwable);
      } 
      return methodHandle;
    }
    
    private static MethodHandle prepareForInvoker(MethodHandle param1MethodHandle) {
      param1MethodHandle = param1MethodHandle.asFixedArity();
      MethodType methodType = param1MethodHandle.type();
      int i = methodType.parameterCount();
      MethodHandle methodHandle = param1MethodHandle.asType(methodType.generic());
      methodHandle.internalForm().compileToBytecode();
      methodHandle = methodHandle.asSpreader(Object[].class, i);
      methodHandle.internalForm().compileToBytecode();
      return methodHandle;
    }
    
    private static MethodHandle restoreToType(MethodHandle param1MethodHandle1, MethodHandle param1MethodHandle2, Class<?> param1Class) {
      MethodType methodType = param1MethodHandle2.type();
      null = param1MethodHandle1.asCollector(Object[].class, methodType.parameterCount());
      MemberName memberName = param1MethodHandle2.internalMemberName();
      null = null.asType(methodType);
      return new MethodHandleImpl.WrappedMember(null, methodType, memberName, param1MethodHandle2.isInvokeSpecial(), param1Class, null);
    }
    
    @CallerSensitive
    private static boolean checkCallerClass(Class<?> param1Class1, Class<?> param1Class2) {
      Class clazz = Reflection.getCallerClass();
      if (clazz != param1Class1 && clazz != param1Class2)
        throw new InternalError("found " + clazz.getName() + ", expected " + param1Class1.getName() + ((param1Class1 == param1Class2) ? "" : (", or else " + param1Class2.getName()))); 
      return true;
    }
    
    static  {
      Class clazz = BindCaller.class;
      assert checkCallerClass(clazz, clazz);
      try {
        MH_checkCallerClass = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(clazz, "checkCallerClass", MethodType.methodType(boolean.class, Class.class, new Class[] { Class.class }));
        assert MH_checkCallerClass.invokeExact(clazz, clazz);
      } catch (Throwable throwable) {
        throw new InternalError(throwable);
      } 
      final Object[] values = { null };
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              try {
                Class clazz = MethodHandleImpl.BindCaller.T.class;
                String str1 = clazz.getName();
                String str2 = str1.substring(str1.lastIndexOf('.') + 1) + ".class";
                URLConnection uRLConnection = clazz.getResource(str2).openConnection();
                int i = uRLConnection.getContentLength();
                byte[] arrayOfByte = new byte[i];
                try (InputStream null = uRLConnection.getInputStream()) {
                  j = inputStream.read(arrayOfByte);
                  if (j != i)
                    throw new IOException(str2); 
                } 
                values[0] = arrayOfByte;
              } catch (IOException iOException) {
                throw new InternalError(iOException);
              } 
              return null;
            }
          });
      T_BYTES = (byte[])arrayOfObject[0];
    }
    
    private static class T {
      static void init() {}
      
      static Object invoke_V(MethodHandle param2MethodHandle, Object[] param2ArrayOfObject) throws Throwable { return param2MethodHandle.invokeExact(param2ArrayOfObject); }
    }
  }
  
  static class CountingWrapper extends DelegatingMethodHandle {
    private final MethodHandle target;
    
    private int count;
    
    private Function<MethodHandle, LambdaForm> countingFormProducer;
    
    private Function<MethodHandle, LambdaForm> nonCountingFormProducer;
    
    static final LambdaForm.NamedFunction NF_maybeStopCounting;
    
    private CountingWrapper(MethodHandle param1MethodHandle, LambdaForm param1LambdaForm, Function<MethodHandle, LambdaForm> param1Function1, Function<MethodHandle, LambdaForm> param1Function2, int param1Int) {
      super(param1MethodHandle.type(), param1LambdaForm);
      this.target = param1MethodHandle;
      this.count = param1Int;
      this.countingFormProducer = param1Function1;
      this.nonCountingFormProducer = param1Function2;
      this.isCounting = (param1Int > 0);
    }
    
    @Hidden
    protected MethodHandle getTarget() { return this.target; }
    
    public MethodHandle asTypeUncached(MethodType param1MethodType) {
      MethodHandle methodHandle2;
      MethodHandle methodHandle1 = this.target.asType(param1MethodType);
      if (this.isCounting) {
        LambdaForm lambdaForm = (LambdaForm)this.countingFormProducer.apply(methodHandle1);
        methodHandle2 = new CountingWrapper(methodHandle1, lambdaForm, this.countingFormProducer, this.nonCountingFormProducer, MethodHandleStatics.DONT_INLINE_THRESHOLD);
      } else {
        methodHandle2 = methodHandle1;
      } 
      return this.asTypeCache = methodHandle2;
    }
    
    boolean countDown() {
      if (this.count <= 0) {
        if (this.isCounting) {
          this.isCounting = false;
          return true;
        } 
        return false;
      } 
      this.count--;
      return false;
    }
    
    @Hidden
    static void maybeStopCounting(Object param1Object) {
      CountingWrapper countingWrapper = (CountingWrapper)param1Object;
      if (countingWrapper.countDown()) {
        LambdaForm lambdaForm = (LambdaForm)countingWrapper.nonCountingFormProducer.apply(countingWrapper.target);
        lambdaForm.compileToBytecode();
        countingWrapper.updateForm(lambdaForm);
      } 
    }
    
    static  {
      Class clazz = CountingWrapper.class;
      try {
        NF_maybeStopCounting = new LambdaForm.NamedFunction(clazz.getDeclaredMethod("maybeStopCounting", new Class[] { Object.class }));
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.newInternalError(reflectiveOperationException);
      } 
    }
  }
  
  enum Intrinsic {
    SELECT_ALTERNATIVE, GUARD_WITH_CATCH, NEW_ARRAY, ARRAY_LOAD, ARRAY_STORE, IDENTITY, ZERO, NONE;
  }
  
  private static final class IntrinsicMethodHandle extends DelegatingMethodHandle {
    private final MethodHandle target;
    
    private final MethodHandleImpl.Intrinsic intrinsicName;
    
    IntrinsicMethodHandle(MethodHandle param1MethodHandle, MethodHandleImpl.Intrinsic param1Intrinsic) {
      super(param1MethodHandle.type(), param1MethodHandle);
      this.target = param1MethodHandle;
      this.intrinsicName = param1Intrinsic;
    }
    
    protected MethodHandle getTarget() { return this.target; }
    
    MethodHandleImpl.Intrinsic intrinsicName() { return this.intrinsicName; }
    
    public MethodHandle asTypeUncached(MethodType param1MethodType) { return this.asTypeCache = this.target.asType(param1MethodType); }
    
    String internalProperties() { return super.internalProperties() + "\n& Intrinsic=" + this.intrinsicName; }
    
    public MethodHandle asCollector(Class<?> param1Class, int param1Int) {
      if (this.intrinsicName == MethodHandleImpl.Intrinsic.IDENTITY) {
        MethodType methodType = type().asCollectorType(param1Class, param1Int);
        MethodHandle methodHandle = MethodHandleImpl.varargsArray(param1Class, param1Int);
        return methodHandle.asType(methodType);
      } 
      return super.asCollector(param1Class, param1Int);
    }
  }
  
  static class Lazy {
    private static final Class<?> MHI = MethodHandleImpl.class;
    
    private static final MethodHandle[] ARRAYS = MethodHandleImpl.makeArrays();
    
    private static final MethodHandle[] FILL_ARRAYS = MethodHandleImpl.makeFillArrays();
    
    static final LambdaForm.NamedFunction NF_checkSpreadArgument;
    
    static final LambdaForm.NamedFunction NF_guardWithCatch;
    
    static final LambdaForm.NamedFunction NF_throwException;
    
    static final LambdaForm.NamedFunction NF_profileBoolean;
    
    static final MethodHandle MH_castReference;
    
    static final MethodHandle MH_selectAlternative;
    
    static final MethodHandle MH_copyAsPrimitiveArray;
    
    static final MethodHandle MH_fillNewTypedArray;
    
    static final MethodHandle MH_fillNewArray;
    
    static final MethodHandle MH_arrayIdentity;
    
    static  {
      try {
        NF_checkSpreadArgument = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("checkSpreadArgument", new Class[] { Object.class, int.class }));
        NF_guardWithCatch = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("guardWithCatch", new Class[] { MethodHandle.class, Class.class, MethodHandle.class, Object[].class }));
        NF_throwException = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("throwException", new Class[] { Throwable.class }));
        NF_profileBoolean = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("profileBoolean", new Class[] { boolean.class, int[].class }));
        NF_checkSpreadArgument.resolve();
        NF_guardWithCatch.resolve();
        NF_throwException.resolve();
        NF_profileBoolean.resolve();
        MH_castReference = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "castReference", MethodType.methodType(Object.class, Class.class, new Class[] { Object.class }));
        MH_copyAsPrimitiveArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "copyAsPrimitiveArray", MethodType.methodType(Object.class, Wrapper.class, new Class[] { Object[].class }));
        MH_arrayIdentity = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "identity", MethodType.methodType(Object[].class, Object[].class));
        MH_fillNewArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewArray", MethodType.methodType(Object[].class, Integer.class, new Class[] { Object[].class }));
        MH_fillNewTypedArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewTypedArray", MethodType.methodType(Object[].class, Object[].class, new Class[] { Integer.class, Object[].class }));
        MH_selectAlternative = MethodHandleImpl.makeIntrinsic(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "selectAlternative", MethodType.methodType(MethodHandle.class, boolean.class, new Class[] { MethodHandle.class, MethodHandle.class })), MethodHandleImpl.Intrinsic.SELECT_ALTERNATIVE);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.newInternalError(reflectiveOperationException);
      } 
    }
  }
  
  private static final class WrappedMember extends DelegatingMethodHandle {
    private final MethodHandle target;
    
    private final MemberName member;
    
    private final Class<?> callerClass;
    
    private final boolean isInvokeSpecial;
    
    private WrappedMember(MethodHandle param1MethodHandle, MethodType param1MethodType, MemberName param1MemberName, boolean param1Boolean, Class<?> param1Class) {
      super(param1MethodType, param1MethodHandle);
      this.target = param1MethodHandle;
      this.member = param1MemberName;
      this.callerClass = param1Class;
      this.isInvokeSpecial = param1Boolean;
    }
    
    MemberName internalMemberName() { return this.member; }
    
    Class<?> internalCallerClass() { return this.callerClass; }
    
    boolean isInvokeSpecial() { return this.isInvokeSpecial; }
    
    protected MethodHandle getTarget() { return this.target; }
    
    public MethodHandle asTypeUncached(MethodType param1MethodType) { return this.asTypeCache = this.target.asType(param1MethodType); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandleImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */