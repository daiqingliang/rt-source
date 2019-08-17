package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.DirectMethodHandle;
import java.lang.invoke.InfoFromMemberName;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.Wrapper;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class MethodHandles {
  private static final MemberName.Factory IMPL_NAMES = MemberName.getFactory();
  
  private static final Permission ACCESS_PERMISSION;
  
  private static final MethodHandle[] IDENTITY_MHS;
  
  private static final MethodHandle[] ZERO_MHS;
  
  @CallerSensitive
  public static Lookup lookup() { return new Lookup(Reflection.getCallerClass()); }
  
  public static Lookup publicLookup() { return Lookup.PUBLIC_LOOKUP; }
  
  public static <T extends java.lang.reflect.Member> T reflectAs(Class<T> paramClass, MethodHandle paramMethodHandle) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(ACCESS_PERMISSION); 
    Lookup lookup = Lookup.IMPL_LOOKUP;
    return (T)lookup.revealDirect(paramMethodHandle).reflectAs(paramClass, lookup);
  }
  
  public static MethodHandle arrayElementGetter(Class<?> paramClass) throws IllegalArgumentException { return MethodHandleImpl.makeArrayElementAccessor(paramClass, false); }
  
  public static MethodHandle arrayElementSetter(Class<?> paramClass) throws IllegalArgumentException { return MethodHandleImpl.makeArrayElementAccessor(paramClass, true); }
  
  public static MethodHandle spreadInvoker(MethodType paramMethodType, int paramInt) {
    if (paramInt < 0 || paramInt > paramMethodType.parameterCount())
      throw MethodHandleStatics.newIllegalArgumentException("bad argument count", Integer.valueOf(paramInt)); 
    paramMethodType = paramMethodType.asSpreaderType(Object[].class, paramMethodType.parameterCount() - paramInt);
    return paramMethodType.invokers().spreadInvoker(paramInt);
  }
  
  public static MethodHandle exactInvoker(MethodType paramMethodType) { return paramMethodType.invokers().exactInvoker(); }
  
  public static MethodHandle invoker(MethodType paramMethodType) { return paramMethodType.invokers().genericInvoker(); }
  
  static MethodHandle basicInvoker(MethodType paramMethodType) { return paramMethodType.invokers().basicInvoker(); }
  
  public static MethodHandle explicitCastArguments(MethodHandle paramMethodHandle, MethodType paramMethodType) {
    explicitCastArgumentsChecks(paramMethodHandle, paramMethodType);
    MethodType methodType = paramMethodHandle.type();
    return (methodType == paramMethodType) ? paramMethodHandle : (methodType.explicitCastEquivalentToAsType(paramMethodType) ? paramMethodHandle.asFixedArity().asType(paramMethodType) : MethodHandleImpl.makePairwiseConvert(paramMethodHandle, paramMethodType, false));
  }
  
  private static void explicitCastArgumentsChecks(MethodHandle paramMethodHandle, MethodType paramMethodType) {
    if (paramMethodHandle.type().parameterCount() != paramMethodType.parameterCount())
      throw new WrongMethodTypeException("cannot explicitly cast " + paramMethodHandle + " to " + paramMethodType); 
  }
  
  public static MethodHandle permuteArguments(MethodHandle paramMethodHandle, MethodType paramMethodType, int... paramVarArgs) {
    paramVarArgs = (int[])paramVarArgs.clone();
    MethodType methodType = paramMethodHandle.type();
    permuteArgumentChecks(paramVarArgs, paramMethodType, methodType);
    int[] arrayOfInt = paramVarArgs;
    BoundMethodHandle boundMethodHandle = paramMethodHandle.rebind();
    LambdaForm lambdaForm = boundMethodHandle.form;
    int i = paramMethodType.parameterCount();
    int j;
    while ((j = findFirstDupOrDrop(paramVarArgs, i)) != 0) {
      if (j > 0) {
        int k = j;
        int m = k;
        int n = paramVarArgs[k];
        boolean bool = false;
        int i1;
        while ((i1 = paramVarArgs[--m]) != n) {
          if (n > i1)
            bool = true; 
        } 
        if (!bool) {
          k = m;
          m = j;
        } 
        lambdaForm = lambdaForm.editor().dupArgumentForm(1 + k, 1 + m);
        assert paramVarArgs[k] == paramVarArgs[m];
        methodType = methodType.dropParameterTypes(m, m + 1);
        i1 = m + 1;
        System.arraycopy(paramVarArgs, i1, paramVarArgs, m, paramVarArgs.length - i1);
        paramVarArgs = Arrays.copyOf(paramVarArgs, paramVarArgs.length - 1);
      } else {
        int k = j ^ 0xFFFFFFFF;
        byte b;
        for (b = 0; b < paramVarArgs.length && paramVarArgs[b] < k; b++);
        Class clazz = paramMethodType.parameterType(k);
        lambdaForm = lambdaForm.editor().addArgumentForm(1 + b, LambdaForm.BasicType.basicType(clazz));
        methodType = methodType.insertParameterTypes(b, new Class[] { clazz });
        int m = b + 1;
        paramVarArgs = Arrays.copyOf(paramVarArgs, paramVarArgs.length + 1);
        System.arraycopy(paramVarArgs, b, paramVarArgs, m, paramVarArgs.length - m);
        paramVarArgs[b] = k;
      } 
      assert permuteArgumentChecks(paramVarArgs, paramMethodType, methodType);
    } 
    assert paramVarArgs.length == i;
    lambdaForm = lambdaForm.editor().permuteArgumentsForm(1, paramVarArgs);
    return (paramMethodType == boundMethodHandle.type() && lambdaForm == boundMethodHandle.internalForm()) ? boundMethodHandle : boundMethodHandle.copyWith(paramMethodType, lambdaForm);
  }
  
  private static int findFirstDupOrDrop(int[] paramArrayOfInt, int paramInt) {
    if (paramInt < 63) {
      long l1 = 0L;
      for (byte b = 0; b < paramArrayOfInt.length; b++) {
        int k = paramArrayOfInt[b];
        if (k >= paramInt)
          return paramArrayOfInt.length; 
        long l = 1L << k;
        if ((l1 & l) != 0L)
          return b; 
        l1 |= l;
      } 
      if (l1 == (1L << paramInt) - 1L) {
        assert Long.numberOfTrailingZeros(Long.lowestOneBit(l1 ^ 0xFFFFFFFFFFFFFFFFL)) == paramInt;
        return 0;
      } 
      long l2 = Long.lowestOneBit(l1 ^ 0xFFFFFFFFFFFFFFFFL);
      int j = Long.numberOfTrailingZeros(l2);
      assert j <= paramInt;
      return (j == paramInt) ? 0 : (j ^ 0xFFFFFFFF);
    } 
    BitSet bitSet = new BitSet(paramInt);
    int i;
    for (i = 0; i < paramArrayOfInt.length; i++) {
      int j = paramArrayOfInt[i];
      if (j >= paramInt)
        return paramArrayOfInt.length; 
      if (bitSet.get(j))
        return i; 
      bitSet.set(j);
    } 
    i = bitSet.nextClearBit(0);
    assert i <= paramInt;
    return (i == paramInt) ? 0 : (i ^ 0xFFFFFFFF);
  }
  
  private static boolean permuteArgumentChecks(int[] paramArrayOfInt, MethodType paramMethodType1, MethodType paramMethodType2) {
    if (paramMethodType1.returnType() != paramMethodType2.returnType())
      throw MethodHandleStatics.newIllegalArgumentException("return types do not match", paramMethodType2, paramMethodType1); 
    if (paramArrayOfInt.length == paramMethodType2.parameterCount()) {
      int i = paramMethodType1.parameterCount();
      boolean bool = false;
      for (byte b = 0; b < paramArrayOfInt.length; b++) {
        int j = paramArrayOfInt[b];
        if (j < 0 || j >= i) {
          bool = true;
          break;
        } 
        Class clazz1 = paramMethodType1.parameterType(j);
        Class clazz2 = paramMethodType2.parameterType(b);
        if (clazz1 != clazz2)
          throw MethodHandleStatics.newIllegalArgumentException("parameter types do not match after reorder", paramMethodType2, paramMethodType1); 
      } 
      if (!bool)
        return true; 
    } 
    throw MethodHandleStatics.newIllegalArgumentException("bad reorder array: " + Arrays.toString(paramArrayOfInt));
  }
  
  public static MethodHandle constant(Class<?> paramClass, Object paramObject) {
    if (paramClass.isPrimitive()) {
      if (paramClass == void.class)
        throw MethodHandleStatics.newIllegalArgumentException("void type"); 
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass);
      paramObject = wrapper.convert(paramObject, paramClass);
      return wrapper.zero().equals(paramObject) ? zero(wrapper, paramClass) : insertArguments(identity(paramClass), 0, new Object[] { paramObject });
    } 
    return (paramObject == null) ? zero(Wrapper.OBJECT, paramClass) : identity(paramClass).bindTo(paramObject);
  }
  
  public static MethodHandle identity(Class<?> paramClass) throws IllegalArgumentException {
    Wrapper wrapper = paramClass.isPrimitive() ? Wrapper.forPrimitiveType(paramClass) : Wrapper.OBJECT;
    int i = wrapper.ordinal();
    MethodHandle methodHandle = IDENTITY_MHS[i];
    if (methodHandle == null)
      methodHandle = setCachedMethodHandle(IDENTITY_MHS, i, makeIdentity(wrapper.primitiveType())); 
    if (methodHandle.type().returnType() == paramClass)
      return methodHandle; 
    assert wrapper == Wrapper.OBJECT;
    return makeIdentity(paramClass);
  }
  
  private static MethodHandle makeIdentity(Class<?> paramClass) throws IllegalArgumentException {
    MethodType methodType = MethodType.methodType(paramClass, paramClass);
    LambdaForm lambdaForm = LambdaForm.identityForm(LambdaForm.BasicType.basicType(paramClass));
    return MethodHandleImpl.makeIntrinsic(methodType, lambdaForm, MethodHandleImpl.Intrinsic.IDENTITY);
  }
  
  private static MethodHandle zero(Wrapper paramWrapper, Class<?> paramClass) {
    int i = paramWrapper.ordinal();
    MethodHandle methodHandle = ZERO_MHS[i];
    if (methodHandle == null)
      methodHandle = setCachedMethodHandle(ZERO_MHS, i, makeZero(paramWrapper.primitiveType())); 
    if (methodHandle.type().returnType() == paramClass)
      return methodHandle; 
    assert paramWrapper == Wrapper.OBJECT;
    return makeZero(paramClass);
  }
  
  private static MethodHandle makeZero(Class<?> paramClass) throws IllegalArgumentException {
    MethodType methodType = MethodType.methodType(paramClass);
    LambdaForm lambdaForm = LambdaForm.zeroForm(LambdaForm.BasicType.basicType(paramClass));
    return MethodHandleImpl.makeIntrinsic(methodType, lambdaForm, MethodHandleImpl.Intrinsic.ZERO);
  }
  
  private static MethodHandle setCachedMethodHandle(MethodHandle[] paramArrayOfMethodHandle, int paramInt, MethodHandle paramMethodHandle) {
    MethodHandle methodHandle = paramArrayOfMethodHandle[paramInt];
    if (methodHandle != null)
      return methodHandle; 
    paramArrayOfMethodHandle[paramInt] = paramMethodHandle;
    return paramMethodHandle;
  }
  
  public static MethodHandle insertArguments(MethodHandle paramMethodHandle, int paramInt, Object... paramVarArgs) {
    int i = paramVarArgs.length;
    Class[] arrayOfClass = insertArgumentsChecks(paramMethodHandle, i, paramInt);
    if (i == 0)
      return paramMethodHandle; 
    BoundMethodHandle boundMethodHandle = paramMethodHandle.rebind();
    for (int j = 0; j < i; j++) {
      Object object = paramVarArgs[j];
      Class clazz = arrayOfClass[paramInt + j];
      if (clazz.isPrimitive()) {
        boundMethodHandle = insertArgumentPrimitive(boundMethodHandle, paramInt, clazz, object);
      } else {
        object = clazz.cast(object);
        boundMethodHandle = boundMethodHandle.bindArgumentL(paramInt, object);
      } 
    } 
    return boundMethodHandle;
  }
  
  private static BoundMethodHandle insertArgumentPrimitive(BoundMethodHandle paramBoundMethodHandle, int paramInt, Class<?> paramClass, Object paramObject) {
    Wrapper wrapper = Wrapper.forPrimitiveType(paramClass);
    paramObject = wrapper.convert(paramObject, paramClass);
    switch (wrapper) {
      case INT:
        return paramBoundMethodHandle.bindArgumentI(paramInt, ((Integer)paramObject).intValue());
      case LONG:
        return paramBoundMethodHandle.bindArgumentJ(paramInt, ((Long)paramObject).longValue());
      case FLOAT:
        return paramBoundMethodHandle.bindArgumentF(paramInt, ((Float)paramObject).floatValue());
      case DOUBLE:
        return paramBoundMethodHandle.bindArgumentD(paramInt, ((Double)paramObject).doubleValue());
    } 
    return paramBoundMethodHandle.bindArgumentI(paramInt, ValueConversions.widenSubword(paramObject));
  }
  
  private static Class<?>[] insertArgumentsChecks(MethodHandle paramMethodHandle, int paramInt1, int paramInt2) throws RuntimeException {
    MethodType methodType = paramMethodHandle.type();
    int i = methodType.parameterCount();
    int j = i - paramInt1;
    if (j < 0)
      throw MethodHandleStatics.newIllegalArgumentException("too many values to insert"); 
    if (paramInt2 < 0 || paramInt2 > j)
      throw MethodHandleStatics.newIllegalArgumentException("no argument type to append"); 
    return methodType.ptypes();
  }
  
  public static MethodHandle dropArguments(MethodHandle paramMethodHandle, int paramInt, List<Class<?>> paramList) {
    paramList = copyTypes(paramList);
    MethodType methodType1 = paramMethodHandle.type();
    int i = dropArgumentChecks(methodType1, paramInt, paramList);
    MethodType methodType2 = methodType1.insertParameterTypes(paramInt, paramList);
    if (i == 0)
      return paramMethodHandle; 
    null = paramMethodHandle.rebind();
    LambdaForm lambdaForm = null.form;
    int j = 1 + paramInt;
    for (Class clazz : paramList)
      lambdaForm = lambdaForm.editor().addArgumentForm(j++, LambdaForm.BasicType.basicType(clazz)); 
    return null.copyWith(methodType2, lambdaForm);
  }
  
  private static List<Class<?>> copyTypes(List<Class<?>> paramList) {
    Object[] arrayOfObject = paramList.toArray();
    return Arrays.asList(Arrays.copyOf(arrayOfObject, arrayOfObject.length, Class[].class));
  }
  
  private static int dropArgumentChecks(MethodType paramMethodType, int paramInt, List<Class<?>> paramList) {
    int i = paramList.size();
    MethodType.checkSlotCount(i);
    int j = paramMethodType.parameterCount();
    int k = j + i;
    if (paramInt < 0 || paramInt > j)
      throw MethodHandleStatics.newIllegalArgumentException("no argument type to remove" + Arrays.asList(new Object[] { paramMethodType, Integer.valueOf(paramInt), paramList, Integer.valueOf(k), Integer.valueOf(j) })); 
    return i;
  }
  
  public static MethodHandle dropArguments(MethodHandle paramMethodHandle, int paramInt, Class<?>... paramVarArgs) { return dropArguments(paramMethodHandle, paramInt, Arrays.asList(paramVarArgs)); }
  
  public static MethodHandle filterArguments(MethodHandle paramMethodHandle, int paramInt, MethodHandle... paramVarArgs) {
    filterArgumentsCheckArity(paramMethodHandle, paramInt, paramVarArgs);
    MethodHandle methodHandle = paramMethodHandle;
    int i = paramInt - 1;
    for (MethodHandle methodHandle1 : paramVarArgs) {
      i++;
      if (methodHandle1 != null)
        methodHandle = filterArgument(methodHandle, i, methodHandle1); 
    } 
    return methodHandle;
  }
  
  static MethodHandle filterArgument(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2) {
    filterArgumentChecks(paramMethodHandle1, paramInt, paramMethodHandle2);
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    null = paramMethodHandle1.rebind();
    Class clazz = methodType2.parameterType(0);
    LambdaForm lambdaForm = null.editor().filterArgumentForm(1 + paramInt, LambdaForm.BasicType.basicType(clazz));
    MethodType methodType3 = methodType1.changeParameterType(paramInt, clazz);
    return null.copyWithExtendL(methodType3, lambdaForm, paramMethodHandle2);
  }
  
  private static void filterArgumentsCheckArity(MethodHandle paramMethodHandle, int paramInt, MethodHandle[] paramArrayOfMethodHandle) {
    MethodType methodType = paramMethodHandle.type();
    int i = methodType.parameterCount();
    if (paramInt + paramArrayOfMethodHandle.length > i)
      throw MethodHandleStatics.newIllegalArgumentException("too many filters"); 
  }
  
  private static void filterArgumentChecks(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2) throws RuntimeException {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    if (methodType2.parameterCount() != 1 || methodType2.returnType() != methodType1.parameterType(paramInt))
      throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", methodType1, methodType2); 
  }
  
  public static MethodHandle collectArguments(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2) {
    MethodType methodType1 = collectArgumentsChecks(paramMethodHandle1, paramInt, paramMethodHandle2);
    MethodType methodType2 = paramMethodHandle2.type();
    BoundMethodHandle boundMethodHandle = paramMethodHandle1.rebind();
    if (methodType2.returnType().isArray() && paramMethodHandle2.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY) {
      LambdaForm lambdaForm1 = boundMethodHandle.editor().collectArgumentArrayForm(1 + paramInt, paramMethodHandle2);
      if (lambdaForm1 != null)
        return boundMethodHandle.copyWith(methodType1, lambdaForm1); 
    } 
    LambdaForm lambdaForm = boundMethodHandle.editor().collectArgumentsForm(1 + paramInt, methodType2.basicType());
    return boundMethodHandle.copyWithExtendL(methodType1, lambdaForm, paramMethodHandle2);
  }
  
  private static MethodType collectArgumentsChecks(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2) throws RuntimeException {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    Class clazz = methodType2.returnType();
    List list = methodType2.parameterList();
    if (clazz == void.class)
      return methodType1.insertParameterTypes(paramInt, list); 
    if (clazz != methodType1.parameterType(paramInt))
      throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", methodType1, methodType2); 
    return methodType1.dropParameterTypes(paramInt, paramInt + 1).insertParameterTypes(paramInt, list);
  }
  
  public static MethodHandle filterReturnValue(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    filterReturnValueChecks(methodType1, methodType2);
    null = paramMethodHandle1.rebind();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(methodType2.returnType());
    LambdaForm lambdaForm = null.editor().filterReturnForm(basicType, false);
    MethodType methodType3 = methodType1.changeReturnType(methodType2.returnType());
    return null.copyWithExtendL(methodType3, lambdaForm, paramMethodHandle2);
  }
  
  private static void filterReturnValueChecks(MethodType paramMethodType1, MethodType paramMethodType2) throws RuntimeException {
    Class clazz = paramMethodType1.returnType();
    int i = paramMethodType2.parameterCount();
    if ((i == 0) ? (clazz != void.class) : (clazz != paramMethodType2.parameterType(false) || i != 1))
      throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", paramMethodType1, paramMethodType2); 
  }
  
  public static MethodHandle foldArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) {
    byte b = 0;
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    Class clazz = foldArgumentChecks(b, methodType1, methodType2);
    null = paramMethodHandle1.rebind();
    boolean bool = (clazz == void.class);
    LambdaForm lambdaForm = null.editor().foldArgumentsForm(1 + b, bool, methodType2.basicType());
    MethodType methodType3 = methodType1;
    if (!bool)
      methodType3 = methodType3.dropParameterTypes(b, b + 1); 
    return null.copyWithExtendL(methodType3, lambdaForm, paramMethodHandle2);
  }
  
  private static Class<?> foldArgumentChecks(int paramInt, MethodType paramMethodType1, MethodType paramMethodType2) {
    int i = paramMethodType2.parameterCount();
    Class clazz = paramMethodType2.returnType();
    int j = (clazz == void.class) ? 0 : 1;
    int k = paramInt + j;
    boolean bool = (paramMethodType1.parameterCount() >= k + i) ? 1 : 0;
    if (bool && !paramMethodType2.parameterList().equals(paramMethodType1.parameterList().subList(k, k + i)))
      bool = false; 
    if (bool && j != 0 && paramMethodType2.returnType() != paramMethodType1.parameterType(false))
      bool = false; 
    if (!bool)
      throw misMatchedTypes("target and combiner types", paramMethodType1, paramMethodType2); 
    return clazz;
  }
  
  public static MethodHandle guardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    MethodType methodType3 = paramMethodHandle3.type();
    if (!methodType2.equals(methodType3))
      throw misMatchedTypes("target and fallback types", methodType2, methodType3); 
    if (methodType1.returnType() != boolean.class)
      throw MethodHandleStatics.newIllegalArgumentException("guard type is not a predicate " + methodType1); 
    List list1 = methodType2.parameterList();
    List list2 = methodType1.parameterList();
    if (!list1.equals(list2)) {
      int i = list2.size();
      int j = list1.size();
      if (i >= j || !list1.subList(0, i).equals(list2))
        throw misMatchedTypes("target and test types", methodType2, methodType1); 
      paramMethodHandle1 = dropArguments(paramMethodHandle1, i, list1.subList(i, j));
      methodType1 = paramMethodHandle1.type();
    } 
    return MethodHandleImpl.makeGuardWithTest(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
  }
  
  static RuntimeException misMatchedTypes(String paramString, MethodType paramMethodType1, MethodType paramMethodType2) { return MethodHandleStatics.newIllegalArgumentException(paramString + " must match: " + paramMethodType1 + " != " + paramMethodType2); }
  
  public static MethodHandle catchException(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2) {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    if (methodType2.parameterCount() < 1 || !methodType2.parameterType(0).isAssignableFrom(paramClass))
      throw MethodHandleStatics.newIllegalArgumentException("handler does not accept exception type " + paramClass); 
    if (methodType2.returnType() != methodType1.returnType())
      throw misMatchedTypes("target and handler return types", methodType1, methodType2); 
    List list1 = methodType1.parameterList();
    List list2 = methodType2.parameterList();
    list2 = list2.subList(1, list2.size());
    if (!list1.equals(list2)) {
      int i = list2.size();
      int j = list1.size();
      if (i >= j || !list1.subList(0, i).equals(list2))
        throw misMatchedTypes("target and handler types", methodType1, methodType2); 
      paramMethodHandle2 = dropArguments(paramMethodHandle2, 1 + i, list1.subList(i, j));
      methodType2 = paramMethodHandle2.type();
    } 
    return MethodHandleImpl.makeGuardWithCatch(paramMethodHandle1, paramClass, paramMethodHandle2);
  }
  
  public static MethodHandle throwException(Class<?> paramClass1, Class<? extends Throwable> paramClass2) {
    if (!Throwable.class.isAssignableFrom(paramClass2))
      throw new ClassCastException(paramClass2.getName()); 
    return MethodHandleImpl.throwException(MethodType.methodType(paramClass1, paramClass2));
  }
  
  static  {
    MethodHandleImpl.initStatics();
    ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
    IDENTITY_MHS = new MethodHandle[Wrapper.values().length];
    ZERO_MHS = new MethodHandle[Wrapper.values().length];
  }
  
  public static final class Lookup {
    private final Class<?> lookupClass;
    
    private final int allowedModes;
    
    public static final int PUBLIC = 1;
    
    public static final int PRIVATE = 2;
    
    public static final int PROTECTED = 4;
    
    public static final int PACKAGE = 8;
    
    private static final int ALL_MODES = 15;
    
    private static final int TRUSTED = -1;
    
    static final Lookup PUBLIC_LOOKUP;
    
    static final Lookup IMPL_LOOKUP;
    
    private static final boolean ALLOW_NESTMATE_ACCESS = false;
    
    static ConcurrentHashMap<MemberName, DirectMethodHandle> LOOKASIDE_TABLE;
    
    private static int fixmods(int param1Int) {
      param1Int &= 0x7;
      return (param1Int != 0) ? param1Int : 8;
    }
    
    public Class<?> lookupClass() { return this.lookupClass; }
    
    private Class<?> lookupClassOrNull() { return (this.allowedModes == -1) ? null : this.lookupClass; }
    
    public int lookupModes() { return this.allowedModes & 0xF; }
    
    Lookup(Class<?> param1Class) {
      this(param1Class, 15);
      checkUnprivilegedlookupClass(param1Class, 15);
    }
    
    private Lookup(Class<?> param1Class, int param1Int) {
      this.lookupClass = param1Class;
      this.allowedModes = param1Int;
    }
    
    public Lookup in(Class<?> param1Class) {
      param1Class.getClass();
      if (this.allowedModes == -1)
        return new Lookup(param1Class, 15); 
      if (param1Class == this.lookupClass)
        return this; 
      int i = this.allowedModes & 0xB;
      if ((i & 0x8) != 0 && !VerifyAccess.isSamePackage(this.lookupClass, param1Class))
        i &= 0xFFFFFFF5; 
      if ((i & 0x2) != 0 && !VerifyAccess.isSamePackageMember(this.lookupClass, param1Class))
        i &= 0xFFFFFFFD; 
      if ((i & true) != 0 && !VerifyAccess.isClassAccessible(param1Class, this.lookupClass, this.allowedModes))
        i = 0; 
      checkUnprivilegedlookupClass(param1Class, i);
      return new Lookup(param1Class, i);
    }
    
    private static void checkUnprivilegedlookupClass(Class<?> param1Class, int param1Int) {
      String str = param1Class.getName();
      if (str.startsWith("java.lang.invoke."))
        throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + param1Class); 
      if (param1Int == 15 && param1Class.getClassLoader() == null && (str.startsWith("java.") || (str.startsWith("sun.") && !str.startsWith("sun.invoke.") && !str.equals("sun.reflect.ReflectionFactory"))))
        throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + param1Class); 
    }
    
    public String toString() {
      String str = this.lookupClass.getName();
      switch (this.allowedModes) {
        case 0:
          return str + "/noaccess";
        case 1:
          return str + "/public";
        case 9:
          return str + "/package";
        case 11:
          return str + "/private";
        case 15:
          return str;
        case -1:
          return "/trusted";
      } 
      str = str + "/" + Integer.toHexString(this.allowedModes);
      assert false : str;
      return str;
    }
    
    public MethodHandle findStatic(Class<?> param1Class, String param1String, MethodType param1MethodType) throws NoSuchMethodException, IllegalAccessException {
      MemberName memberName = resolveOrFail((byte)6, param1Class, param1String, param1MethodType);
      return getDirectMethod((byte)6, param1Class, memberName, findBoundCallerClass(memberName));
    }
    
    public MethodHandle findVirtual(Class<?> param1Class, String param1String, MethodType param1MethodType) throws NoSuchMethodException, IllegalAccessException {
      if (param1Class == MethodHandle.class) {
        MethodHandle methodHandle = findVirtualForMH(param1String, param1MethodType);
        if (methodHandle != null)
          return methodHandle; 
      } 
      byte b = param1Class.isInterface() ? 9 : 5;
      MemberName memberName = resolveOrFail(b, param1Class, param1String, param1MethodType);
      return getDirectMethod(b, param1Class, memberName, findBoundCallerClass(memberName));
    }
    
    private MethodHandle findVirtualForMH(String param1String, MethodType param1MethodType) {
      if ("invoke".equals(param1String))
        return MethodHandles.invoker(param1MethodType); 
      if ("invokeExact".equals(param1String))
        return MethodHandles.exactInvoker(param1MethodType); 
      assert !MemberName.isMethodHandleInvokeName(param1String);
      return null;
    }
    
    public MethodHandle findConstructor(Class<?> param1Class, MethodType param1MethodType) throws NoSuchMethodException, IllegalAccessException {
      if (param1Class.isArray())
        throw new NoSuchMethodException("no constructor for array class: " + param1Class.getName()); 
      String str = "<init>";
      MemberName memberName = resolveOrFail((byte)8, param1Class, str, param1MethodType);
      return getDirectConstructor(param1Class, memberName);
    }
    
    public MethodHandle findSpecial(Class<?> param1Class1, String param1String, MethodType param1MethodType, Class<?> param1Class2) throws NoSuchMethodException, IllegalAccessException {
      checkSpecialCaller(param1Class2);
      Lookup lookup = in(param1Class2);
      MemberName memberName = lookup.resolveOrFail((byte)7, param1Class1, param1String, param1MethodType);
      return lookup.getDirectMethod((byte)7, param1Class1, memberName, findBoundCallerClass(memberName));
    }
    
    public MethodHandle findGetter(Class<?> param1Class1, String param1String, Class<?> param1Class2) throws NoSuchFieldException, IllegalAccessException {
      MemberName memberName = resolveOrFail((byte)1, param1Class1, param1String, param1Class2);
      return getDirectField((byte)1, param1Class1, memberName);
    }
    
    public MethodHandle findSetter(Class<?> param1Class1, String param1String, Class<?> param1Class2) throws NoSuchFieldException, IllegalAccessException {
      MemberName memberName = resolveOrFail((byte)3, param1Class1, param1String, param1Class2);
      return getDirectField((byte)3, param1Class1, memberName);
    }
    
    public MethodHandle findStaticGetter(Class<?> param1Class1, String param1String, Class<?> param1Class2) throws NoSuchFieldException, IllegalAccessException {
      MemberName memberName = resolveOrFail((byte)2, param1Class1, param1String, param1Class2);
      return getDirectField((byte)2, param1Class1, memberName);
    }
    
    public MethodHandle findStaticSetter(Class<?> param1Class1, String param1String, Class<?> param1Class2) throws NoSuchFieldException, IllegalAccessException {
      MemberName memberName = resolveOrFail((byte)4, param1Class1, param1String, param1Class2);
      return getDirectField((byte)4, param1Class1, memberName);
    }
    
    public MethodHandle bind(Object param1Object, String param1String, MethodType param1MethodType) throws NoSuchMethodException, IllegalAccessException {
      Class clazz = param1Object.getClass();
      MemberName memberName = resolveOrFail((byte)7, clazz, param1String, param1MethodType);
      MethodHandle methodHandle = getDirectMethodNoRestrict((byte)7, clazz, memberName, findBoundCallerClass(memberName));
      return methodHandle.bindArgumentL(0, param1Object).setVarargs(memberName);
    }
    
    public MethodHandle unreflect(Method param1Method) throws IllegalAccessException {
      if (param1Method.getDeclaringClass() == MethodHandle.class) {
        MethodHandle methodHandle = unreflectForMH(param1Method);
        if (methodHandle != null)
          return methodHandle; 
      } 
      MemberName memberName = new MemberName(param1Method);
      byte b = memberName.getReferenceKind();
      if (b == 7)
        b = 5; 
      assert memberName.isMethod();
      Lookup lookup = param1Method.isAccessible() ? IMPL_LOOKUP : this;
      return lookup.getDirectMethodNoSecurityManager(b, memberName.getDeclaringClass(), memberName, findBoundCallerClass(memberName));
    }
    
    private MethodHandle unreflectForMH(Method param1Method) throws IllegalAccessException { return MemberName.isMethodHandleInvokeName(param1Method.getName()) ? MethodHandleImpl.fakeMethodHandleInvoke(new MemberName(param1Method)) : null; }
    
    public MethodHandle unreflectSpecial(Method param1Method, Class<?> param1Class) throws IllegalAccessException {
      checkSpecialCaller(param1Class);
      Lookup lookup = in(param1Class);
      MemberName memberName = new MemberName(param1Method, true);
      assert memberName.isMethod();
      return lookup.getDirectMethodNoSecurityManager((byte)7, memberName.getDeclaringClass(), memberName, findBoundCallerClass(memberName));
    }
    
    public MethodHandle unreflectConstructor(Constructor<?> param1Constructor) throws IllegalAccessException {
      MemberName memberName = new MemberName(param1Constructor);
      assert memberName.isConstructor();
      Lookup lookup = param1Constructor.isAccessible() ? IMPL_LOOKUP : this;
      return lookup.getDirectConstructorNoSecurityManager(memberName.getDeclaringClass(), memberName);
    }
    
    public MethodHandle unreflectGetter(Field param1Field) throws IllegalAccessException { return unreflectField(param1Field, false); }
    
    private MethodHandle unreflectField(Field param1Field, boolean param1Boolean) throws IllegalAccessException {
      MemberName memberName = new MemberName(param1Field, param1Boolean);
      assert false;
      throw new AssertionError();
    }
    
    public MethodHandle unreflectSetter(Field param1Field) throws IllegalAccessException { return unreflectField(param1Field, true); }
    
    public MethodHandleInfo revealDirect(MethodHandle param1MethodHandle) {
      MemberName memberName = param1MethodHandle.internalMemberName();
      if (memberName == null || (!memberName.isResolved() && !memberName.isMethodHandleInvoke()))
        throw MethodHandleStatics.newIllegalArgumentException("not a direct method handle"); 
      Class clazz = memberName.getDeclaringClass();
      byte b = memberName.getReferenceKind();
      assert MethodHandleNatives.refKindIsValid(b);
      if (b == 7 && !param1MethodHandle.isInvokeSpecial())
        b = 5; 
      if (b == 5 && clazz.isInterface())
        b = 9; 
      try {
        checkAccess(b, clazz, memberName);
        checkSecurityManager(clazz, memberName);
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalArgumentException(illegalAccessException);
      } 
      if (this.allowedModes != -1 && memberName.isCallerSensitive()) {
        Class clazz1 = param1MethodHandle.internalCallerClass();
        if (!hasPrivateAccess() || clazz1 != lookupClass())
          throw new IllegalArgumentException("method handle is caller sensitive: " + clazz1); 
      } 
      return new InfoFromMemberName(this, memberName, b);
    }
    
    MemberName resolveOrFail(byte param1Byte, Class<?> param1Class1, String param1String, Class<?> param1Class2) throws NoSuchFieldException, IllegalAccessException {
      checkSymbolicClass(param1Class1);
      param1String.getClass();
      param1Class2.getClass();
      return IMPL_NAMES.resolveOrFail(param1Byte, new MemberName(param1Class1, param1String, param1Class2, param1Byte), lookupClassOrNull(), NoSuchFieldException.class);
    }
    
    MemberName resolveOrFail(byte param1Byte, Class<?> param1Class, String param1String, MethodType param1MethodType) throws NoSuchMethodException, IllegalAccessException {
      checkSymbolicClass(param1Class);
      param1String.getClass();
      param1MethodType.getClass();
      checkMethodName(param1Byte, param1String);
      return IMPL_NAMES.resolveOrFail(param1Byte, new MemberName(param1Class, param1String, param1MethodType, param1Byte), lookupClassOrNull(), NoSuchMethodException.class);
    }
    
    MemberName resolveOrFail(byte param1Byte, MemberName param1MemberName) throws ReflectiveOperationException {
      checkSymbolicClass(param1MemberName.getDeclaringClass());
      param1MemberName.getName().getClass();
      param1MemberName.getType().getClass();
      return IMPL_NAMES.resolveOrFail(param1Byte, param1MemberName, lookupClassOrNull(), ReflectiveOperationException.class);
    }
    
    void checkSymbolicClass(Class<?> param1Class) {
      param1Class.getClass();
      Class clazz = lookupClassOrNull();
      if (clazz != null && !VerifyAccess.isClassAccessible(param1Class, clazz, this.allowedModes))
        throw (new MemberName(param1Class)).makeAccessException("symbolic reference class is not public", this); 
    }
    
    void checkMethodName(byte param1Byte, String param1String) throws NoSuchMethodException {
      if (param1String.startsWith("<") && param1Byte != 8)
        throw new NoSuchMethodException("illegal method name: " + param1String); 
    }
    
    Class<?> findBoundCallerClass(MemberName param1MemberName) throws IllegalAccessException {
      Class clazz = null;
      if (MethodHandleNatives.isCallerSensitive(param1MemberName))
        if (hasPrivateAccess()) {
          clazz = this.lookupClass;
        } else {
          throw new IllegalAccessException("Attempt to lookup caller-sensitive method using restricted lookup object");
        }  
      return clazz;
    }
    
    private boolean hasPrivateAccess() { return ((this.allowedModes & 0x2) != 0); }
    
    void checkSecurityManager(Class<?> param1Class, MemberName param1MemberName) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager == null)
        return; 
      if (this.allowedModes == -1)
        return; 
      boolean bool = hasPrivateAccess();
      if (!bool || !VerifyAccess.classLoaderIsAncestor(this.lookupClass, param1Class))
        ReflectUtil.checkPackageAccess(param1Class); 
      if (param1MemberName.isPublic())
        return; 
      if (!bool)
        securityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION); 
      Class clazz = param1MemberName.getDeclaringClass();
      if (!bool && clazz != param1Class)
        ReflectUtil.checkPackageAccess(clazz); 
    }
    
    void checkMethod(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException {
      String str;
      boolean bool = (param1Byte == 6) ? 1 : 0;
      if (param1MemberName.isConstructor()) {
        str = "expected a method, not a constructor";
      } else if (!param1MemberName.isMethod()) {
        str = "expected a method";
      } else if (bool != param1MemberName.isStatic()) {
        str = bool ? "expected a static method" : "expected a non-static method";
      } else {
        checkAccess(param1Byte, param1Class, param1MemberName);
        return;
      } 
      throw param1MemberName.makeAccessException(str, this);
    }
    
    void checkField(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException {
      String str;
      boolean bool = !MethodHandleNatives.refKindHasReceiver(param1Byte) ? 1 : 0;
      if (bool != param1MemberName.isStatic()) {
        str = bool ? "expected a static field" : "expected a non-static field";
      } else {
        checkAccess(param1Byte, param1Class, param1MemberName);
        return;
      } 
      throw param1MemberName.makeAccessException(str, this);
    }
    
    void checkAccess(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException {
      assert param1MemberName.referenceKindIsConsistentWith(param1Byte) && MethodHandleNatives.refKindIsValid(param1Byte) && MethodHandleNatives.refKindIsField(param1Byte) == param1MemberName.isField();
      int i = this.allowedModes;
      if (i == -1)
        return; 
      int j = param1MemberName.getModifiers();
      if (Modifier.isProtected(j) && param1Byte == 5 && param1MemberName.getDeclaringClass() == Object.class && param1MemberName.getName().equals("clone") && param1Class.isArray())
        j ^= 0x5; 
      if (Modifier.isProtected(j) && param1Byte == 8)
        j ^= 0x4; 
      if (Modifier.isFinal(j) && MethodHandleNatives.refKindIsSetter(param1Byte))
        throw param1MemberName.makeAccessException("unexpected set of a final field", this); 
      if (Modifier.isPublic(j) && Modifier.isPublic(param1Class.getModifiers()) && i != 0)
        return; 
      int k = fixmods(j);
      if ((k & i) != 0) {
        if (VerifyAccess.isMemberAccessible(param1Class, param1MemberName.getDeclaringClass(), j, lookupClass(), i))
          return; 
      } else if ((k & 0x4) != 0 && (i & 0x8) != 0 && VerifyAccess.isSamePackage(param1MemberName.getDeclaringClass(), lookupClass())) {
        return;
      } 
      throw param1MemberName.makeAccessException(accessFailedMessage(param1Class, param1MemberName), this);
    }
    
    String accessFailedMessage(Class<?> param1Class, MemberName param1MemberName) {
      Class clazz = param1MemberName.getDeclaringClass();
      int i = param1MemberName.getModifiers();
      boolean bool = (Modifier.isPublic(clazz.getModifiers()) && (clazz == param1Class || Modifier.isPublic(param1Class.getModifiers()))) ? 1 : 0;
      if (!bool && (this.allowedModes & 0x8) != 0)
        bool = (VerifyAccess.isClassAccessible(clazz, lookupClass(), 15) && (clazz == param1Class || VerifyAccess.isClassAccessible(param1Class, lookupClass(), 15))) ? 1 : 0; 
      return !bool ? "class is not public" : (Modifier.isPublic(i) ? "access to public member failed" : (Modifier.isPrivate(i) ? "member is private" : (Modifier.isProtected(i) ? "member is protected" : "member is private to package")));
    }
    
    private void checkSpecialCaller(Class<?> param1Class) {
      int i = this.allowedModes;
      if (i == -1)
        return; 
      if (!hasPrivateAccess() || param1Class != lookupClass())
        throw (new MemberName(param1Class)).makeAccessException("no private access for invokespecial", this); 
    }
    
    private boolean restrictProtectedReceiver(MemberName param1MemberName) { return (param1MemberName.isProtected() && !param1MemberName.isStatic() && this.allowedModes != -1 && param1MemberName.getDeclaringClass() != lookupClass() && !VerifyAccess.isSamePackage(param1MemberName.getDeclaringClass(), lookupClass())); }
    
    private MethodHandle restrictReceiver(MemberName param1MemberName, DirectMethodHandle param1DirectMethodHandle, Class<?> param1Class) throws IllegalAccessException {
      assert !param1MemberName.isStatic();
      if (!param1MemberName.getDeclaringClass().isAssignableFrom(param1Class))
        throw param1MemberName.makeAccessException("caller class must be a subclass below the method", param1Class); 
      MethodType methodType1 = param1DirectMethodHandle.type();
      if (methodType1.parameterType(false) == param1Class)
        return param1DirectMethodHandle; 
      MethodType methodType2 = methodType1.changeParameterType(0, param1Class);
      assert !param1DirectMethodHandle.isVarargsCollector();
      assert param1DirectMethodHandle.viewAsTypeChecks(methodType2, true);
      return param1DirectMethodHandle.copyWith(methodType2, param1DirectMethodHandle.form);
    }
    
    private MethodHandle getDirectMethod(byte param1Byte, Class<?> param1Class1, MemberName param1MemberName, Class<?> param1Class2) throws IllegalAccessException { return getDirectMethodCommon(param1Byte, param1Class1, param1MemberName, true, true, param1Class2); }
    
    private MethodHandle getDirectMethodNoRestrict(byte param1Byte, Class<?> param1Class1, MemberName param1MemberName, Class<?> param1Class2) throws IllegalAccessException { return getDirectMethodCommon(param1Byte, param1Class1, param1MemberName, true, false, param1Class2); }
    
    private MethodHandle getDirectMethodNoSecurityManager(byte param1Byte, Class<?> param1Class1, MemberName param1MemberName, Class<?> param1Class2) throws IllegalAccessException { return getDirectMethodCommon(param1Byte, param1Class1, param1MemberName, false, true, param1Class2); }
    
    private MethodHandle getDirectMethodCommon(byte param1Byte, Class<?> param1Class1, MemberName param1MemberName, boolean param1Boolean1, boolean param1Boolean2, Class<?> param1Class2) throws IllegalAccessException {
      checkMethod(param1Byte, param1Class1, param1MemberName);
      if (param1Boolean1)
        checkSecurityManager(param1Class1, param1MemberName); 
      assert !param1MemberName.isMethodHandleInvoke();
      if (param1Byte == 7 && param1Class1 != lookupClass() && !param1Class1.isInterface() && param1Class1 != lookupClass().getSuperclass() && param1Class1.isAssignableFrom(lookupClass())) {
        MemberName memberName;
        assert !param1MemberName.getName().equals("<init>");
        Class clazz = lookupClass();
        do {
          clazz = clazz.getSuperclass();
          memberName = new MemberName(clazz, param1MemberName.getName(), param1MemberName.getMethodType(), (byte)7);
          memberName = IMPL_NAMES.resolveOrNull(param1Byte, memberName, lookupClassOrNull());
        } while (memberName == null && param1Class1 != clazz);
        if (memberName == null)
          throw new InternalError(param1MemberName.toString()); 
        param1MemberName = memberName;
        param1Class1 = clazz;
        checkMethod(param1Byte, param1Class1, param1MemberName);
      } 
      DirectMethodHandle directMethodHandle = DirectMethodHandle.make(param1Byte, param1Class1, param1MemberName);
      null = directMethodHandle;
      if (param1Boolean2 && (param1Byte == 7 || (MethodHandleNatives.refKindHasReceiver(param1Byte) && restrictProtectedReceiver(param1MemberName))))
        null = restrictReceiver(param1MemberName, directMethodHandle, lookupClass()); 
      null = maybeBindCaller(param1MemberName, null, param1Class2);
      return null.setVarargs(param1MemberName);
    }
    
    private MethodHandle maybeBindCaller(MemberName param1MemberName, MethodHandle param1MethodHandle, Class<?> param1Class) throws IllegalAccessException {
      if (this.allowedModes == -1 || !MethodHandleNatives.isCallerSensitive(param1MemberName))
        return param1MethodHandle; 
      Class clazz = this.lookupClass;
      if (!hasPrivateAccess())
        clazz = param1Class; 
      return MethodHandleImpl.bindCaller(param1MethodHandle, clazz);
    }
    
    private MethodHandle getDirectField(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException { return getDirectFieldCommon(param1Byte, param1Class, param1MemberName, true); }
    
    private MethodHandle getDirectFieldNoSecurityManager(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException { return getDirectFieldCommon(param1Byte, param1Class, param1MemberName, false); }
    
    private MethodHandle getDirectFieldCommon(byte param1Byte, Class<?> param1Class, MemberName param1MemberName, boolean param1Boolean) throws IllegalAccessException {
      checkField(param1Byte, param1Class, param1MemberName);
      if (param1Boolean)
        checkSecurityManager(param1Class, param1MemberName); 
      DirectMethodHandle directMethodHandle = DirectMethodHandle.make(param1Class, param1MemberName);
      boolean bool = (MethodHandleNatives.refKindHasReceiver(param1Byte) && restrictProtectedReceiver(param1MemberName)) ? 1 : 0;
      return bool ? restrictReceiver(param1MemberName, directMethodHandle, lookupClass()) : directMethodHandle;
    }
    
    private MethodHandle getDirectConstructor(Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException { return getDirectConstructorCommon(param1Class, param1MemberName, true); }
    
    private MethodHandle getDirectConstructorNoSecurityManager(Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException { return getDirectConstructorCommon(param1Class, param1MemberName, false); }
    
    private MethodHandle getDirectConstructorCommon(Class<?> param1Class, MemberName param1MemberName, boolean param1Boolean) throws IllegalAccessException {
      assert param1MemberName.isConstructor();
      checkAccess((byte)8, param1Class, param1MemberName);
      if (param1Boolean)
        checkSecurityManager(param1Class, param1MemberName); 
      assert !MethodHandleNatives.isCallerSensitive(param1MemberName);
      return DirectMethodHandle.make(param1MemberName).setVarargs(param1MemberName);
    }
    
    MethodHandle linkMethodHandleConstant(byte param1Byte, Class<?> param1Class, String param1String, Object param1Object) throws ReflectiveOperationException {
      if (!(param1Object instanceof Class) && !(param1Object instanceof MethodType))
        throw new InternalError("unresolved MemberName"); 
      MemberName memberName1 = new MemberName(param1Byte, param1Class, param1String, param1Object);
      MethodHandle methodHandle = (MethodHandle)LOOKASIDE_TABLE.get(memberName1);
      if (methodHandle != null) {
        checkSymbolicClass(param1Class);
        return methodHandle;
      } 
      if (param1Class == MethodHandle.class && param1Byte == 5) {
        methodHandle = findVirtualForMH(memberName1.getName(), memberName1.getMethodType());
        if (methodHandle != null)
          return methodHandle; 
      } 
      MemberName memberName2 = resolveOrFail(param1Byte, memberName1);
      methodHandle = getDirectMethodForConstant(param1Byte, param1Class, memberName2);
      if (methodHandle instanceof DirectMethodHandle && canBeCached(param1Byte, param1Class, memberName2)) {
        MemberName memberName = methodHandle.internalMemberName();
        if (memberName != null)
          memberName = memberName.asNormalOriginal(); 
        if (memberName1.equals(memberName))
          LOOKASIDE_TABLE.put(memberName, (DirectMethodHandle)methodHandle); 
      } 
      return methodHandle;
    }
    
    private boolean canBeCached(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) {
      if (param1Byte == 7)
        return false; 
      if (!Modifier.isPublic(param1Class.getModifiers()) || !Modifier.isPublic(param1MemberName.getDeclaringClass().getModifiers()) || !param1MemberName.isPublic() || param1MemberName.isCallerSensitive())
        return false; 
      ClassLoader classLoader = param1Class.getClassLoader();
      if (!VM.isSystemDomainLoader(classLoader)) {
        ClassLoader classLoader1 = ClassLoader.getSystemClassLoader();
        boolean bool = false;
        while (classLoader1 != null) {
          if (classLoader == classLoader1) {
            bool = true;
            break;
          } 
          classLoader1 = classLoader1.getParent();
        } 
        if (!bool)
          return false; 
      } 
      try {
        MemberName memberName = MethodHandles.publicLookup().resolveOrFail(param1Byte, new MemberName(param1Byte, param1Class, param1MemberName.getName(), param1MemberName.getType()));
        checkSecurityManager(param1Class, memberName);
      } catch (ReflectiveOperationException|SecurityException reflectiveOperationException) {
        return false;
      } 
      return true;
    }
    
    private MethodHandle getDirectMethodForConstant(byte param1Byte, Class<?> param1Class, MemberName param1MemberName) throws IllegalAccessException {
      if (MethodHandleNatives.refKindIsField(param1Byte))
        return getDirectFieldNoSecurityManager(param1Byte, param1Class, param1MemberName); 
      if (MethodHandleNatives.refKindIsMethod(param1Byte))
        return getDirectMethodNoSecurityManager(param1Byte, param1Class, param1MemberName, this.lookupClass); 
      if (param1Byte == 8)
        return getDirectConstructorNoSecurityManager(param1Class, param1MemberName); 
      throw MethodHandleStatics.newIllegalArgumentException("bad MethodHandle constant #" + param1MemberName);
    }
    
    static  {
      IMPL_NAMES.getClass();
      PUBLIC_LOOKUP = new Lookup(Object.class, 1);
      IMPL_LOOKUP = new Lookup(Object.class, -1);
      LOOKASIDE_TABLE = new ConcurrentHashMap();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandles.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */