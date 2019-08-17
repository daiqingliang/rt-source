package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.DirectMethodHandle;
import java.lang.invoke.ForceInline;
import java.lang.invoke.InvokerBytecodeGenerator;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;

class DirectMethodHandle extends MethodHandle {
  final MemberName member;
  
  private static final MemberName.Factory IMPL_NAMES = MemberName.getFactory();
  
  private static byte AF_GETFIELD = 0;
  
  private static byte AF_PUTFIELD = 1;
  
  private static byte AF_GETSTATIC = 2;
  
  private static byte AF_PUTSTATIC = 3;
  
  private static byte AF_GETSTATIC_INIT = 4;
  
  private static byte AF_PUTSTATIC_INIT = 5;
  
  private static byte AF_LIMIT = 6;
  
  private static int FT_LAST_WRAPPER = Wrapper.values().length - 1;
  
  private static int FT_UNCHECKED_REF = Wrapper.OBJECT.ordinal();
  
  private static int FT_CHECKED_REF = FT_LAST_WRAPPER + 1;
  
  private static int FT_LIMIT = FT_LAST_WRAPPER + 2;
  
  private static final LambdaForm[] ACCESSOR_FORMS = new LambdaForm[afIndex(AF_LIMIT, false, 0)];
  
  private DirectMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm, MemberName paramMemberName) {
    super(paramMethodType, paramLambdaForm);
    if (!paramMemberName.isResolved())
      throw new InternalError(); 
    if (paramMemberName.getDeclaringClass().isInterface() && paramMemberName.isMethod() && !paramMemberName.isAbstract()) {
      MemberName memberName = (memberName = new MemberName(Object.class, paramMemberName.getName(), paramMemberName.getMethodType(), paramMemberName.getReferenceKind())).getFactory().resolveOrNull(memberName.getReferenceKind(), memberName, null);
      if (memberName != null && memberName.isPublic()) {
        assert paramMemberName.getReferenceKind() == memberName.getReferenceKind();
        paramMemberName = memberName;
      } 
    } 
    this.member = paramMemberName;
  }
  
  static DirectMethodHandle make(byte paramByte, Class<?> paramClass, MemberName paramMemberName) {
    MethodType methodType = paramMemberName.getMethodOrFieldType();
    if (!paramMemberName.isStatic()) {
      if (!paramMemberName.getDeclaringClass().isAssignableFrom(paramClass) || paramMemberName.isConstructor())
        throw new InternalError(paramMemberName.toString()); 
      methodType = methodType.insertParameterTypes(0, new Class[] { paramClass });
    } 
    if (!paramMemberName.isField()) {
      switch (paramByte) {
        case 7:
          paramMemberName = paramMemberName.asSpecial();
          lambdaForm1 = preparedLambdaForm(paramMemberName);
          return new Special(methodType, lambdaForm1, paramMemberName, null);
        case 9:
          lambdaForm1 = preparedLambdaForm(paramMemberName);
          return new Interface(methodType, lambdaForm1, paramMemberName, paramClass, null);
      } 
      LambdaForm lambdaForm1 = preparedLambdaForm(paramMemberName);
      return new DirectMethodHandle(methodType, lambdaForm1, paramMemberName);
    } 
    LambdaForm lambdaForm = preparedFieldLambdaForm(paramMemberName);
    if (paramMemberName.isStatic()) {
      long l1 = MethodHandleNatives.staticFieldOffset(paramMemberName);
      Object object = MethodHandleNatives.staticFieldBase(paramMemberName);
      return new StaticAccessor(methodType, lambdaForm, paramMemberName, object, l1, null);
    } 
    long l = MethodHandleNatives.objectFieldOffset(paramMemberName);
    assert l == (int)l;
    return new Accessor(methodType, lambdaForm, paramMemberName, (int)l, null);
  }
  
  static DirectMethodHandle make(Class<?> paramClass, MemberName paramMemberName) {
    byte b = paramMemberName.getReferenceKind();
    if (b == 7)
      b = 5; 
    return make(b, paramClass, paramMemberName);
  }
  
  static DirectMethodHandle make(MemberName paramMemberName) { return paramMemberName.isConstructor() ? makeAllocator(paramMemberName) : make(paramMemberName.getDeclaringClass(), paramMemberName); }
  
  static DirectMethodHandle make(Method paramMethod) { return make(paramMethod.getDeclaringClass(), new MemberName(paramMethod)); }
  
  static DirectMethodHandle make(Field paramField) { return make(paramField.getDeclaringClass(), new MemberName(paramField)); }
  
  private static DirectMethodHandle makeAllocator(MemberName paramMemberName) {
    assert paramMemberName.isConstructor() && paramMemberName.getName().equals("<init>");
    Class clazz = paramMemberName.getDeclaringClass();
    paramMemberName = paramMemberName.asConstructor();
    assert paramMemberName.isConstructor() && paramMemberName.getReferenceKind() == 8 : paramMemberName;
    MethodType methodType = paramMemberName.getMethodType().changeReturnType(clazz);
    LambdaForm lambdaForm = preparedLambdaForm(paramMemberName);
    MemberName memberName = paramMemberName.asSpecial();
    assert memberName.getMethodType().returnType() == void.class;
    return new Constructor(methodType, lambdaForm, paramMemberName, memberName, clazz, null);
  }
  
  BoundMethodHandle rebind() { return BoundMethodHandle.makeReinvoker(this); }
  
  MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    assert getClass() == DirectMethodHandle.class;
    return new DirectMethodHandle(paramMethodType, paramLambdaForm, this.member);
  }
  
  String internalProperties() { return "\n& DMH.MN=" + internalMemberName(); }
  
  @ForceInline
  MemberName internalMemberName() { return this.member; }
  
  private static LambdaForm preparedLambdaForm(MemberName paramMemberName) {
    byte b;
    assert paramMemberName.isInvocable() : paramMemberName;
    MethodType methodType = paramMemberName.getInvocationType().basicType();
    assert !paramMemberName.isMethodHandleInvoke() : paramMemberName;
    switch (paramMemberName.getReferenceKind()) {
      case 5:
        b = 0;
        break;
      case 6:
        b = 1;
        break;
      case 7:
        b = 2;
        break;
      case 9:
        b = 4;
        break;
      case 8:
        b = 3;
        break;
      default:
        throw new InternalError(paramMemberName.toString());
    } 
    if (b == 1 && shouldBeInitialized(paramMemberName)) {
      preparedLambdaForm(methodType, b);
      b = 5;
    } 
    LambdaForm lambdaForm = preparedLambdaForm(methodType, b);
    maybeCompile(lambdaForm, paramMemberName);
    assert lambdaForm.methodType().dropParameterTypes(0, 1).equals(paramMemberName.getInvocationType().basicType()) : Arrays.asList(new Object[] { paramMemberName, paramMemberName.getInvocationType().basicType(), lambdaForm, lambdaForm.methodType() });
    return lambdaForm;
  }
  
  private static LambdaForm preparedLambdaForm(MethodType paramMethodType, int paramInt) {
    LambdaForm lambdaForm = paramMethodType.form().cachedLambdaForm(paramInt);
    if (lambdaForm != null)
      return lambdaForm; 
    lambdaForm = makePreparedLambdaForm(paramMethodType, paramInt);
    return paramMethodType.form().setCachedLambdaForm(paramInt, lambdaForm);
  }
  
  private static LambdaForm makePreparedLambdaForm(MethodType paramMethodType, int paramInt) {
    String str1;
    boolean bool1 = (paramInt == 5) ? 1 : 0;
    boolean bool2 = (paramInt == 3) ? 1 : 0;
    boolean bool3 = (paramInt == 4) ? 1 : 0;
    switch (paramInt) {
      case 0:
        str1 = "linkToVirtual";
        str2 = "DMH.invokeVirtual";
        break;
      case 1:
        str1 = "linkToStatic";
        str2 = "DMH.invokeStatic";
        break;
      case 5:
        str1 = "linkToStatic";
        str2 = "DMH.invokeStaticInit";
        break;
      case 2:
        str1 = "linkToSpecial";
        str2 = "DMH.invokeSpecial";
        break;
      case 4:
        str1 = "linkToInterface";
        str2 = "DMH.invokeInterface";
        break;
      case 3:
        str1 = "linkToSpecial";
        str2 = "DMH.newInvokeSpecial";
        break;
      default:
        throw new InternalError("which=" + paramInt);
    } 
    MethodType methodType = paramMethodType.appendParameterTypes(new Class[] { MemberName.class });
    if (bool2)
      methodType = methodType.insertParameterTypes(0, new Class[] { Object.class }).changeReturnType(void.class); 
    MemberName memberName = new MemberName(MethodHandle.class, str1, methodType, (byte)6);
    try {
      memberName = IMPL_NAMES.resolveOrFail((byte)6, memberName, null, NoSuchMethodException.class);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError(reflectiveOperationException);
    } 
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = bool2 ? j++ : -1;
    int m = j++;
    int n = bool3 ? j++ : -1;
    int i1 = j++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, paramMethodType.invokerType());
    assert arrayOfName.length == j;
    if (bool2) {
      arrayOfName[k] = new LambdaForm.Name(Lazy.NF_allocateInstance, new Object[] { arrayOfName[0] });
      arrayOfName[m] = new LambdaForm.Name(Lazy.NF_constructorMethod, new Object[] { arrayOfName[0] });
    } else if (bool1) {
      arrayOfName[m] = new LambdaForm.Name(Lazy.NF_internalMemberNameEnsureInit, new Object[] { arrayOfName[0] });
    } else {
      arrayOfName[m] = new LambdaForm.Name(Lazy.NF_internalMemberName, new Object[] { arrayOfName[0] });
    } 
    assert findDirectMethodHandle(arrayOfName[m]) == arrayOfName[false];
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 1, m + 1, Object[].class);
    if (bool3) {
      arrayOfName[n] = new LambdaForm.Name(Lazy.NF_checkReceiver, new Object[] { arrayOfName[0], arrayOfName[1] });
      arrayOfObject[0] = arrayOfName[n];
    } 
    assert arrayOfObject[arrayOfObject.length - true] == arrayOfName[m];
    int i2 = -2;
    if (bool2) {
      assert arrayOfObject[arrayOfObject.length - 2] == arrayOfName[k];
      System.arraycopy(arrayOfObject, 0, arrayOfObject, 1, arrayOfObject.length - 2);
      arrayOfObject[0] = arrayOfName[k];
      i2 = k;
    } 
    arrayOfName[i1] = new LambdaForm.Name(memberName, arrayOfObject);
    String str2 = str2 + "_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(paramMethodType));
    LambdaForm lambdaForm = new LambdaForm(str2, i, arrayOfName, i2);
    lambdaForm.compileToBytecode();
    return lambdaForm;
  }
  
  static Object findDirectMethodHandle(LambdaForm.Name paramName) {
    if (paramName.function == Lazy.NF_internalMemberName || paramName.function == Lazy.NF_internalMemberNameEnsureInit || paramName.function == Lazy.NF_constructorMethod) {
      assert paramName.arguments.length == 1;
      return paramName.arguments[0];
    } 
    return null;
  }
  
  private static void maybeCompile(LambdaForm paramLambdaForm, MemberName paramMemberName) {
    if (VerifyAccess.isSamePackage(paramMemberName.getDeclaringClass(), MethodHandle.class))
      paramLambdaForm.compileToBytecode(); 
  }
  
  @ForceInline
  static Object internalMemberName(Object paramObject) { return ((DirectMethodHandle)paramObject).member; }
  
  static Object internalMemberNameEnsureInit(Object paramObject) {
    DirectMethodHandle directMethodHandle = (DirectMethodHandle)paramObject;
    directMethodHandle.ensureInitialized();
    return directMethodHandle.member;
  }
  
  static boolean shouldBeInitialized(MemberName paramMemberName) {
    switch (paramMemberName.getReferenceKind()) {
      case 2:
      case 4:
      case 6:
      case 8:
        break;
      default:
        return false;
    } 
    Class clazz = paramMemberName.getDeclaringClass();
    if (clazz == sun.invoke.util.ValueConversions.class || clazz == MethodHandleImpl.class || clazz == Invokers.class)
      return false; 
    if (VerifyAccess.isSamePackage(MethodHandle.class, clazz) || VerifyAccess.isSamePackage(sun.invoke.util.ValueConversions.class, clazz)) {
      if (MethodHandleStatics.UNSAFE.shouldBeInitialized(clazz))
        MethodHandleStatics.UNSAFE.ensureClassInitialized(clazz); 
      return false;
    } 
    return MethodHandleStatics.UNSAFE.shouldBeInitialized(clazz);
  }
  
  private void ensureInitialized() {
    if (checkInitialized(this.member))
      if (this.member.isField()) {
        updateForm(preparedFieldLambdaForm(this.member));
      } else {
        updateForm(preparedLambdaForm(this.member));
      }  
  }
  
  private static boolean checkInitialized(MemberName paramMemberName) {
    Class clazz = paramMemberName.getDeclaringClass();
    WeakReference weakReference = (WeakReference)EnsureInitialized.INSTANCE.get(clazz);
    if (weakReference == null)
      return true; 
    Thread thread;
    if (thread == (thread = (Thread)weakReference.get()).currentThread()) {
      if (MethodHandleStatics.UNSAFE.shouldBeInitialized(clazz))
        return false; 
    } else {
      MethodHandleStatics.UNSAFE.ensureClassInitialized(clazz);
    } 
    assert !MethodHandleStatics.UNSAFE.shouldBeInitialized(clazz);
    EnsureInitialized.INSTANCE.remove(clazz);
    return true;
  }
  
  static void ensureInitialized(Object paramObject) { ((DirectMethodHandle)paramObject).ensureInitialized(); }
  
  static Object constructorMethod(Object paramObject) {
    Constructor constructor = (Constructor)paramObject;
    return constructor.initMethod;
  }
  
  static Object allocateInstance(Object paramObject) {
    Constructor constructor = (Constructor)paramObject;
    return MethodHandleStatics.UNSAFE.allocateInstance(constructor.instanceClass);
  }
  
  @ForceInline
  static long fieldOffset(Object paramObject) { return ((Accessor)paramObject).fieldOffset; }
  
  @ForceInline
  static Object checkBase(Object paramObject) {
    paramObject.getClass();
    return paramObject;
  }
  
  @ForceInline
  static Object nullCheck(Object paramObject) {
    paramObject.getClass();
    return paramObject;
  }
  
  @ForceInline
  static Object staticBase(Object paramObject) { return ((StaticAccessor)paramObject).staticBase; }
  
  @ForceInline
  static long staticOffset(Object paramObject) { return ((StaticAccessor)paramObject).staticOffset; }
  
  @ForceInline
  static Object checkCast(Object paramObject1, Object paramObject2) { return ((DirectMethodHandle)paramObject1).checkCast(paramObject2); }
  
  Object checkCast(Object paramObject) { return this.member.getReturnType().cast(paramObject); }
  
  private static int afIndex(byte paramByte, boolean paramBoolean, int paramInt) { return paramByte * FT_LIMIT * 2 + (paramBoolean ? FT_LIMIT : 0) + paramInt; }
  
  private static int ftypeKind(Class<?> paramClass) { return paramClass.isPrimitive() ? Wrapper.forPrimitiveType(paramClass).ordinal() : (VerifyType.isNullReferenceConversion(Object.class, paramClass) ? FT_UNCHECKED_REF : FT_CHECKED_REF); }
  
  private static LambdaForm preparedFieldLambdaForm(MemberName paramMemberName) {
    byte b;
    Class clazz = paramMemberName.getFieldType();
    boolean bool = paramMemberName.isVolatile();
    switch (paramMemberName.getReferenceKind()) {
      case 1:
        b = AF_GETFIELD;
        break;
      case 3:
        b = AF_PUTFIELD;
        break;
      case 2:
        b = AF_GETSTATIC;
        break;
      case 4:
        b = AF_PUTSTATIC;
        break;
      default:
        throw new InternalError(paramMemberName.toString());
    } 
    if (shouldBeInitialized(paramMemberName)) {
      preparedFieldLambdaForm(b, bool, clazz);
      assert AF_GETSTATIC_INIT - AF_GETSTATIC == AF_PUTSTATIC_INIT - AF_PUTSTATIC;
      b = (byte)(b + AF_GETSTATIC_INIT - AF_GETSTATIC);
    } 
    LambdaForm lambdaForm = preparedFieldLambdaForm(b, bool, clazz);
    maybeCompile(lambdaForm, paramMemberName);
    assert lambdaForm.methodType().dropParameterTypes(0, 1).equals(paramMemberName.getInvocationType().basicType()) : Arrays.asList(new Object[] { paramMemberName, paramMemberName.getInvocationType().basicType(), lambdaForm, lambdaForm.methodType() });
    return lambdaForm;
  }
  
  private static LambdaForm preparedFieldLambdaForm(byte paramByte, boolean paramBoolean, Class<?> paramClass) {
    int i = afIndex(paramByte, paramBoolean, ftypeKind(paramClass));
    LambdaForm lambdaForm = ACCESSOR_FORMS[i];
    if (lambdaForm != null)
      return lambdaForm; 
    lambdaForm = makePreparedFieldLambdaForm(paramByte, paramBoolean, ftypeKind(paramClass));
    ACCESSOR_FORMS[i] = lambdaForm;
    return lambdaForm;
  }
  
  private static LambdaForm makePreparedFieldLambdaForm(byte paramByte, boolean paramBoolean, int paramInt) {
    MethodType methodType1;
    boolean bool1 = ((paramByte & true) == (AF_GETFIELD & true)) ? 1 : 0;
    boolean bool2 = (paramByte >= AF_GETSTATIC) ? 1 : 0;
    boolean bool3 = (paramByte >= AF_GETSTATIC_INIT) ? 1 : 0;
    boolean bool4 = (paramInt == FT_CHECKED_REF) ? 1 : 0;
    Wrapper wrapper = bool4 ? Wrapper.OBJECT : Wrapper.values()[paramInt];
    Class clazz = wrapper.primitiveType();
    assert ftypeKind(bool4 ? String.class : clazz) == paramInt;
    String str1 = wrapper.primitiveSimpleName();
    String str2 = Character.toUpperCase(str1.charAt(0)) + str1.substring(1);
    if (paramBoolean)
      str2 = str2 + "Volatile"; 
    String str3 = bool1 ? "get" : "put";
    String str4 = str3 + str2;
    if (bool1) {
      methodType1 = MethodType.methodType(clazz, Object.class, new Class[] { long.class });
    } else {
      methodType1 = MethodType.methodType(void.class, Object.class, new Class[] { long.class, clazz });
    } 
    MemberName memberName = new MemberName(sun.misc.Unsafe.class, str4, methodType1, (byte)5);
    try {
      memberName = IMPL_NAMES.resolveOrFail((byte)5, memberName, null, NoSuchMethodException.class);
    } catch (ReflectiveOperationException null) {
      throw MethodHandleStatics.newInternalError(methodType2);
    } 
    if (bool1) {
      methodType2 = MethodType.methodType(clazz);
    } else {
      methodType2 = MethodType.methodType(void.class, clazz);
    } 
    MethodType methodType2 = methodType2.basicType();
    if (!bool2)
      methodType2 = methodType2.insertParameterTypes(0, new Class[] { Object.class }); 
    int i = 1 + methodType2.parameterCount();
    byte b1 = bool2 ? -1 : 1;
    byte b2 = bool1 ? -1 : (i - 1);
    int j = i;
    int k = bool2 ? j++ : -1;
    int m = j++;
    int n = (b1 >= 0) ? j++ : -1;
    int i1 = bool3 ? j++ : -1;
    int i2 = (bool4 && !bool1) ? j++ : -1;
    int i3 = j++;
    int i4 = (bool4 && bool1) ? j++ : -1;
    int i5 = j - 1;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, methodType2.invokerType());
    if (bool3)
      arrayOfName[i1] = new LambdaForm.Name(Lazy.NF_ensureInitialized, new Object[] { arrayOfName[0] }); 
    if (bool4 && !bool1)
      arrayOfName[i2] = new LambdaForm.Name(Lazy.NF_checkCast, new Object[] { arrayOfName[0], arrayOfName[b2] }); 
    Object[] arrayOfObject = new Object[1 + methodType1.parameterCount()];
    assert arrayOfObject.length == (bool1 ? 3 : 4);
    arrayOfObject[0] = MethodHandleStatics.UNSAFE;
    if (bool2) {
      arrayOfName[k] = new LambdaForm.Name(Lazy.NF_staticBase, new Object[] { arrayOfName[0] });
      arrayOfObject[1] = new LambdaForm.Name(Lazy.NF_staticBase, new Object[] { arrayOfName[0] });
      arrayOfName[m] = new LambdaForm.Name(Lazy.NF_staticOffset, new Object[] { arrayOfName[0] });
      arrayOfObject[2] = new LambdaForm.Name(Lazy.NF_staticOffset, new Object[] { arrayOfName[0] });
    } else {
      arrayOfName[n] = new LambdaForm.Name(Lazy.NF_checkBase, new Object[] { arrayOfName[b1] });
      arrayOfObject[1] = new LambdaForm.Name(Lazy.NF_checkBase, new Object[] { arrayOfName[b1] });
      arrayOfName[m] = new LambdaForm.Name(Lazy.NF_fieldOffset, new Object[] { arrayOfName[0] });
      arrayOfObject[2] = new LambdaForm.Name(Lazy.NF_fieldOffset, new Object[] { arrayOfName[0] });
    } 
    if (!bool1)
      arrayOfObject[3] = bool4 ? arrayOfName[i2] : arrayOfName[b2]; 
    for (Object object : arrayOfObject)
      assert object != null; 
    arrayOfName[i3] = new LambdaForm.Name(memberName, arrayOfObject);
    if (bool4 && bool1)
      arrayOfName[i4] = new LambdaForm.Name(Lazy.NF_checkCast, new Object[] { arrayOfName[0], arrayOfName[i3] }); 
    for (LambdaForm.Name name : arrayOfName)
      assert name != null; 
    String str5 = bool2 ? "Static" : "Field";
    String str6 = str4 + str5;
    if (bool4)
      str6 = str6 + "Cast"; 
    if (bool3)
      str6 = str6 + "Init"; 
    return new LambdaForm(str6, i, arrayOfName, i5);
  }
  
  static class Accessor extends DirectMethodHandle {
    final Class<?> fieldType;
    
    final int fieldOffset;
    
    private Accessor(MethodType param1MethodType, LambdaForm param1LambdaForm, MemberName param1MemberName, int param1Int) {
      super(param1MethodType, param1LambdaForm, param1MemberName, null);
      this.fieldType = param1MemberName.getFieldType();
      this.fieldOffset = param1Int;
    }
    
    Object checkCast(Object param1Object) { return this.fieldType.cast(param1Object); }
    
    MethodHandle copyWith(MethodType param1MethodType, LambdaForm param1LambdaForm) { return new Accessor(param1MethodType, param1LambdaForm, this.member, this.fieldOffset); }
  }
  
  static class Constructor extends DirectMethodHandle {
    final MemberName initMethod;
    
    final Class<?> instanceClass;
    
    private Constructor(MethodType param1MethodType, LambdaForm param1LambdaForm, MemberName param1MemberName1, MemberName param1MemberName2, Class<?> param1Class) {
      super(param1MethodType, param1LambdaForm, param1MemberName1, null);
      this.initMethod = param1MemberName2;
      this.instanceClass = param1Class;
      assert param1MemberName2.isResolved();
    }
    
    MethodHandle copyWith(MethodType param1MethodType, LambdaForm param1LambdaForm) { return new Constructor(param1MethodType, param1LambdaForm, this.member, this.initMethod, this.instanceClass); }
  }
  
  private static class EnsureInitialized extends ClassValue<WeakReference<Thread>> {
    static final EnsureInitialized INSTANCE = new EnsureInitialized();
    
    protected WeakReference<Thread> computeValue(Class<?> param1Class) {
      MethodHandleStatics.UNSAFE.ensureClassInitialized(param1Class);
      return MethodHandleStatics.UNSAFE.shouldBeInitialized(param1Class) ? new WeakReference(Thread.currentThread()) : null;
    }
  }
  
  static class Interface extends DirectMethodHandle {
    private final Class<?> refc;
    
    private Interface(MethodType param1MethodType, LambdaForm param1LambdaForm, MemberName param1MemberName, Class<?> param1Class) {
      super(param1MethodType, param1LambdaForm, param1MemberName, null);
      assert param1Class.isInterface() : param1Class;
      this.refc = param1Class;
    }
    
    MethodHandle copyWith(MethodType param1MethodType, LambdaForm param1LambdaForm) { return new Interface(param1MethodType, param1LambdaForm, this.member, this.refc); }
    
    Object checkReceiver(Object param1Object) {
      if (!this.refc.isInstance(param1Object)) {
        String str = String.format("Class %s does not implement the requested interface %s", new Object[] { param1Object.getClass().getName(), this.refc.getName() });
        throw new IncompatibleClassChangeError(str);
      } 
      return param1Object;
    }
  }
  
  private static class Lazy {
    static final LambdaForm.NamedFunction NF_internalMemberName;
    
    static final LambdaForm.NamedFunction NF_internalMemberNameEnsureInit;
    
    static final LambdaForm.NamedFunction NF_ensureInitialized;
    
    static final LambdaForm.NamedFunction NF_fieldOffset;
    
    static final LambdaForm.NamedFunction NF_checkBase;
    
    static final LambdaForm.NamedFunction NF_staticBase;
    
    static final LambdaForm.NamedFunction NF_staticOffset;
    
    static final LambdaForm.NamedFunction NF_checkCast;
    
    static final LambdaForm.NamedFunction NF_allocateInstance;
    
    static final LambdaForm.NamedFunction NF_constructorMethod;
    
    static final LambdaForm.NamedFunction NF_checkReceiver;
    
    static  {
      try {
        LambdaForm.NamedFunction[] arrayOfNamedFunction = { 
            NF_internalMemberName = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberName", new Class[] { Object.class })), NF_internalMemberNameEnsureInit = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberNameEnsureInit", new Class[] { Object.class })), NF_ensureInitialized = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("ensureInitialized", new Class[] { Object.class })), NF_fieldOffset = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("fieldOffset", new Class[] { Object.class })), NF_checkBase = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkBase", new Class[] { Object.class })), NF_staticBase = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticBase", new Class[] { Object.class })), NF_staticOffset = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticOffset", new Class[] { Object.class })), NF_checkCast = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkCast", new Class[] { Object.class, Object.class })), NF_allocateInstance = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("allocateInstance", new Class[] { Object.class })), NF_constructorMethod = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("constructorMethod", new Class[] { Object.class })), 
            NF_checkReceiver = new LambdaForm.NamedFunction(new MemberName(DirectMethodHandle.Interface.class.getDeclaredMethod("checkReceiver", new Class[] { Object.class }))) };
        for (LambdaForm.NamedFunction namedFunction : arrayOfNamedFunction) {
          assert InvokerBytecodeGenerator.isStaticallyInvocable(namedFunction.member) : namedFunction;
          namedFunction.resolve();
        } 
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.newInternalError(reflectiveOperationException);
      } 
    }
  }
  
  static class Special extends DirectMethodHandle {
    private Special(MethodType param1MethodType, LambdaForm param1LambdaForm, MemberName param1MemberName) { super(param1MethodType, param1LambdaForm, param1MemberName, null); }
    
    boolean isInvokeSpecial() { return true; }
    
    MethodHandle copyWith(MethodType param1MethodType, LambdaForm param1LambdaForm) { return new Special(param1MethodType, param1LambdaForm, this.member); }
  }
  
  static class StaticAccessor extends DirectMethodHandle {
    private final Class<?> fieldType;
    
    private final Object staticBase;
    
    private final long staticOffset;
    
    private StaticAccessor(MethodType param1MethodType, LambdaForm param1LambdaForm, MemberName param1MemberName, Object param1Object, long param1Long) {
      super(param1MethodType, param1LambdaForm, param1MemberName, null);
      this.fieldType = param1MemberName.getFieldType();
      this.staticBase = param1Object;
      this.staticOffset = param1Long;
    }
    
    Object checkCast(Object param1Object) { return this.fieldType.cast(param1Object); }
    
    MethodHandle copyWith(MethodType param1MethodType, LambdaForm param1LambdaForm) { return new StaticAccessor(param1MethodType, param1LambdaForm, this.member, this.staticBase, this.staticOffset); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\DirectMethodHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */