package java.lang.invoke;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.DirectMethodHandle;
import java.lang.invoke.DontInline;
import java.lang.invoke.InvokerBytecodeGenerator;
import java.lang.invoke.Invokers;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.LambdaForm.BasicType;
import java.lang.invoke.LambdaForm.Hidden;
import java.lang.invoke.LambdaForm.Name;
import java.lang.invoke.LambdaFormEditor;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodTypeForm;
import java.lang.invoke.SimpleMethodHandle;
import java.lang.invoke.Stable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import sun.invoke.util.Wrapper;

class LambdaForm {
  final int arity;
  
  final int result;
  
  final boolean forceInline;
  
  final MethodHandle customized;
  
  @Stable
  final Name[] names;
  
  final String debugName;
  
  MemberName vmentry;
  
  private boolean isCompiled;
  
  public static final int VOID_RESULT = -1;
  
  public static final int LAST_RESULT = -2;
  
  private static final boolean USE_PREDEFINED_INTERPRET_METHODS = true;
  
  private static final int COMPILE_THRESHOLD = Math.max(-1, MethodHandleStatics.COMPILE_THRESHOLD);
  
  private int invocationCounter = 0;
  
  static final int INTERNED_ARGUMENT_LIMIT = 10;
  
  private static final Name[][] INTERNED_ARGUMENTS = new Name[BasicType.ARG_TYPE_LIMIT][10];
  
  private static final MemberName.Factory IMPL_NAMES;
  
  private static final LambdaForm[] LF_identityForm;
  
  private static final LambdaForm[] LF_zeroForm;
  
  private static final NamedFunction[] NF_identity;
  
  private static final NamedFunction[] NF_zero;
  
  private static final HashMap<String, Integer> DEBUG_NAME_COUNTERS;
  
  private static final boolean TRACE_INTERPRETER;
  
  LambdaForm(String paramString, int paramInt1, Name[] paramArrayOfName, int paramInt2) { this(paramString, paramInt1, paramArrayOfName, paramInt2, true, null); }
  
  LambdaForm(String paramString, int paramInt1, Name[] paramArrayOfName, int paramInt2, boolean paramBoolean, MethodHandle paramMethodHandle) {
    assert namesOK(paramInt1, paramArrayOfName);
    this.arity = paramInt1;
    this.result = fixResult(paramInt2, paramArrayOfName);
    this.names = (Name[])paramArrayOfName.clone();
    this.debugName = fixDebugName(paramString);
    this.forceInline = paramBoolean;
    this.customized = paramMethodHandle;
    int i = normalize();
    if (i > 253) {
      assert i <= 255;
      compileToBytecode();
    } 
  }
  
  LambdaForm(String paramString, int paramInt, Name[] paramArrayOfName) { this(paramString, paramInt, paramArrayOfName, -2, true, null); }
  
  LambdaForm(String paramString, int paramInt, Name[] paramArrayOfName, boolean paramBoolean) { this(paramString, paramInt, paramArrayOfName, -2, paramBoolean, null); }
  
  LambdaForm(String paramString, Name[] paramArrayOfName1, Name[] paramArrayOfName2, Name paramName) { this(paramString, paramArrayOfName1.length, buildNames(paramArrayOfName1, paramArrayOfName2, paramName), -2, true, null); }
  
  LambdaForm(String paramString, Name[] paramArrayOfName1, Name[] paramArrayOfName2, Name paramName, boolean paramBoolean) { this(paramString, paramArrayOfName1.length, buildNames(paramArrayOfName1, paramArrayOfName2, paramName), -2, paramBoolean, null); }
  
  private static Name[] buildNames(Name[] paramArrayOfName1, Name[] paramArrayOfName2, Name paramName) {
    int i = paramArrayOfName1.length;
    int j = i + paramArrayOfName2.length + ((paramName == null) ? 0 : 1);
    Name[] arrayOfName = (Name[])Arrays.copyOf(paramArrayOfName1, j);
    System.arraycopy(paramArrayOfName2, 0, arrayOfName, i, paramArrayOfName2.length);
    if (paramName != null)
      arrayOfName[j - 1] = paramName; 
    return arrayOfName;
  }
  
  private LambdaForm(String paramString) {
    assert isValidSignature(paramString);
    this.arity = signatureArity(paramString);
    this.result = (signatureReturn(paramString) == BasicType.V_TYPE) ? -1 : this.arity;
    this.names = buildEmptyNames(this.arity, paramString);
    this.debugName = "LF.zero";
    this.forceInline = true;
    this.customized = null;
    assert nameRefsAreLegal();
    assert isEmpty();
    assert paramString.equals(basicTypeSignature()) : paramString + " != " + basicTypeSignature();
  }
  
  private static Name[] buildEmptyNames(int paramInt, String paramString) {
    assert isValidSignature(paramString);
    int i = paramInt + 1;
    if (paramInt < 0 || paramString.length() != i + 1)
      throw new IllegalArgumentException("bad arity for " + paramString); 
    byte b = (BasicType.basicType(paramString.charAt(i)) == BasicType.V_TYPE) ? 0 : 1;
    Name[] arrayOfName = arguments(b, paramString.substring(0, paramInt));
    for (int j = 0; j < b; j++) {
      Name name = new Name(constantZero(BasicType.basicType(paramString.charAt(i + j))), new Object[0]);
      arrayOfName[paramInt + j] = name.newIndex(paramInt + j);
    } 
    return arrayOfName;
  }
  
  private static int fixResult(int paramInt, Name[] paramArrayOfName) {
    if (paramInt == -2)
      paramInt = paramArrayOfName.length - 1; 
    if (paramInt >= 0 && (paramArrayOfName[paramInt]).type == BasicType.V_TYPE)
      paramInt = -1; 
    return paramInt;
  }
  
  private static String fixDebugName(String paramString) {
    if (DEBUG_NAME_COUNTERS != null) {
      Integer integer;
      int i = paramString.indexOf('_');
      int j = paramString.length();
      if (i < 0)
        i = j; 
      String str = paramString.substring(0, i);
      synchronized (DEBUG_NAME_COUNTERS) {
        integer = (Integer)DEBUG_NAME_COUNTERS.get(str);
        if (integer == null)
          integer = Integer.valueOf(0); 
        DEBUG_NAME_COUNTERS.put(str, Integer.valueOf(integer.intValue() + 1));
      } 
      StringBuilder stringBuilder = new StringBuilder(str);
      stringBuilder.append('_');
      int k = stringBuilder.length();
      stringBuilder.append(integer.intValue());
      for (int m = stringBuilder.length() - k; m < 3; m++)
        stringBuilder.insert(k, '0'); 
      if (i < j) {
        while (++i < j && Character.isDigit(paramString.charAt(i)))
          i++; 
        if (i < j && paramString.charAt(i) == '_')
          i++; 
        if (i < j)
          stringBuilder.append('_').append(paramString, i, j); 
      } 
      return stringBuilder.toString();
    } 
    return paramString;
  }
  
  private static boolean namesOK(int paramInt, Name[] paramArrayOfName) {
    for (byte b = 0; b < paramArrayOfName.length; b++) {
      Name name = paramArrayOfName[b];
      assert name != null : "n is null";
      if (b < paramInt) {
        assert name.isParam() : name + " is not param at " + b;
      } else {
        assert !name.isParam() : name + " is param at " + b;
      } 
    } 
    return true;
  }
  
  LambdaForm customize(MethodHandle paramMethodHandle) {
    LambdaForm lambdaForm = new LambdaForm(this.debugName, this.arity, this.names, this.result, this.forceInline, paramMethodHandle);
    if (COMPILE_THRESHOLD > 0 && this.isCompiled)
      lambdaForm.compileToBytecode(); 
    lambdaForm.transformCache = this;
    return lambdaForm;
  }
  
  LambdaForm uncustomize() {
    if (this.customized == null)
      return this; 
    assert this.transformCache != null;
    LambdaForm lambdaForm = (LambdaForm)this.transformCache;
    if (COMPILE_THRESHOLD > 0 && this.isCompiled)
      lambdaForm.compileToBytecode(); 
    return lambdaForm;
  }
  
  private int normalize() {
    Name[] arrayOfName = null;
    int i = 0;
    byte b = 0;
    int j;
    for (j = 0; j < this.names.length; j++) {
      Name name = this.names[j];
      if (!name.initIndex(j)) {
        if (arrayOfName == null) {
          arrayOfName = (Name[])this.names.clone();
          b = j;
        } 
        this.names[j] = name.cloneWithIndex(j);
      } 
      if (name.arguments != null && i < name.arguments.length)
        i = name.arguments.length; 
    } 
    if (arrayOfName != null) {
      j = this.arity;
      if (j <= b)
        j = b + 1; 
      for (int m = j; m < this.names.length; m++) {
        Name name = this.names[m].replaceNames(arrayOfName, this.names, b, m);
        this.names[m] = name.newIndex(m);
      } 
    } 
    assert nameRefsAreLegal();
    j = Math.min(this.arity, 10);
    boolean bool = false;
    int k;
    for (k = 0; k < j; k++) {
      Name name1 = this.names[k];
      Name name2 = internArgument(name1);
      if (name1 != name2) {
        this.names[k] = name2;
        bool = true;
      } 
    } 
    if (bool)
      for (k = this.arity; k < this.names.length; k++)
        this.names[k].internArguments();  
    assert nameRefsAreLegal();
    return i;
  }
  
  boolean nameRefsAreLegal() {
    assert this.arity >= 0 && this.arity <= this.names.length;
    assert this.result >= -1 && this.result < this.names.length;
    int i;
    for (i = 0; i < this.arity; i++) {
      Name name = this.names[i];
      assert name.index() == i : Arrays.asList(new Integer[] { null, (new Integer[2][false] = Integer.valueOf(name.index())).valueOf(i) });
      assert name.isParam();
    } 
    for (i = this.arity; i < this.names.length; i++) {
      Name name = this.names[i];
      assert name.index() == i;
      for (Object object : name.arguments) {
        if (object instanceof Name) {
          Name name1;
          short s = name1.index;
          assert 0 <= s && s < this.names.length : name.debugString() + ": 0 <= i2 && i2 < names.length: 0 <= " + s + " < " + this.names.length;
          assert this.names[s] == name1 : Arrays.asList(new Object[] { 
                "-1-", Integer.valueOf(i), "-2-", name.debugString(), "-3-", Integer.valueOf(s), "-4-", name1.debugString(), "-5-", this.names[s].debugString(), 
                "-6-", this });
          assert s < i;
        } 
      } 
    } 
    return true;
  }
  
  BasicType returnType() {
    if (this.result < 0)
      return BasicType.V_TYPE; 
    Name name = this.names[this.result];
    return name.type;
  }
  
  BasicType parameterType(int paramInt) { return (parameter(paramInt)).type; }
  
  Name parameter(int paramInt) {
    assert paramInt < this.arity;
    Name name = this.names[paramInt];
    assert name.isParam();
    return name;
  }
  
  Object parameterConstraint(int paramInt) { return (parameter(paramInt)).constraint; }
  
  int arity() { return this.arity; }
  
  int expressionCount() { return this.names.length - this.arity; }
  
  MethodType methodType() { return signatureType(basicTypeSignature()); }
  
  final String basicTypeSignature() {
    StringBuilder stringBuilder = new StringBuilder(arity() + 3);
    byte b = 0;
    int i = arity();
    while (b < i) {
      stringBuilder.append(parameterType(b).basicTypeChar());
      b++;
    } 
    return stringBuilder.append('_').append(returnType().basicTypeChar()).toString();
  }
  
  static int signatureArity(String paramString) {
    assert isValidSignature(paramString);
    return paramString.indexOf('_');
  }
  
  static BasicType signatureReturn(String paramString) { return BasicType.basicType(paramString.charAt(signatureArity(paramString) + 1)); }
  
  static boolean isValidSignature(String paramString) {
    int i = paramString.indexOf('_');
    if (i < 0)
      return false; 
    int j = paramString.length();
    if (j != i + 2)
      return false; 
    for (byte b = 0; b < j; b++) {
      if (b != i) {
        char c = paramString.charAt(b);
        if (c == 'V')
          return (b == j - 1 && i == j - 2); 
        if (!BasicType.isArgBasicTypeChar(c))
          return false; 
      } 
    } 
    return true;
  }
  
  static MethodType signatureType(String paramString) {
    Class[] arrayOfClass = new Class[signatureArity(paramString)];
    for (byte b = 0; b < arrayOfClass.length; b++)
      arrayOfClass[b] = (BasicType.basicType(paramString.charAt(b))).btClass; 
    Class clazz = (signatureReturn(paramString)).btClass;
    return MethodType.methodType(clazz, arrayOfClass);
  }
  
  public void prepare() {
    if (COMPILE_THRESHOLD == 0 && !this.isCompiled)
      compileToBytecode(); 
    if (this.vmentry != null)
      return; 
    LambdaForm lambdaForm = getPreparedForm(basicTypeSignature());
    this.vmentry = lambdaForm.vmentry;
  }
  
  MemberName compileToBytecode() {
    if (this.vmentry != null && this.isCompiled)
      return this.vmentry; 
    MethodType methodType = methodType();
    assert this.vmentry == null || this.vmentry.getMethodType().basicType().equals(methodType);
    try {
      this.vmentry = InvokerBytecodeGenerator.generateCustomizedCode(this, methodType);
      if (TRACE_INTERPRETER)
        traceInterpreter("compileToBytecode", this); 
      this.isCompiled = true;
      return this.vmentry;
    } catch (Error|Exception error) {
      throw MethodHandleStatics.newInternalError(toString(), error);
    } 
  }
  
  private static void computeInitialPreparedForms() {
    for (MemberName memberName : MemberName.getFactory().getMethods(LambdaForm.class, false, null, null, null)) {
      if (!memberName.isStatic() || !memberName.isPackage())
        continue; 
      MethodType methodType = memberName.getMethodType();
      if (methodType.parameterCount() > 0 && methodType.parameterType(false) == MethodHandle.class && memberName.getName().startsWith("interpret_")) {
        String str = basicTypeSignature(methodType);
        assert memberName.getName().equals("interpret" + str.substring(str.indexOf('_')));
        LambdaForm lambdaForm = new LambdaForm(str);
        lambdaForm.vmentry = memberName;
        lambdaForm = methodType.form().setCachedLambdaForm(6, lambdaForm);
      } 
    } 
  }
  
  static Object interpret_L(MethodHandle paramMethodHandle) throws Throwable {
    Object[] arrayOfObject = { paramMethodHandle };
    String str = null;
    assert argumentTypesMatch(str = "L_L", arrayOfObject);
    Object object = paramMethodHandle.form.interpretWithArguments(arrayOfObject);
    assert returnTypesMatch(str, arrayOfObject, object);
    return object;
  }
  
  static Object interpret_L(MethodHandle paramMethodHandle, Object paramObject) throws Throwable {
    Object[] arrayOfObject = { paramMethodHandle, paramObject };
    String str = null;
    assert argumentTypesMatch(str = "LL_L", arrayOfObject);
    Object object = paramMethodHandle.form.interpretWithArguments(arrayOfObject);
    assert returnTypesMatch(str, arrayOfObject, object);
    return object;
  }
  
  static Object interpret_L(MethodHandle paramMethodHandle, Object paramObject1, Object paramObject2) throws Throwable {
    Object[] arrayOfObject = { paramMethodHandle, paramObject1, paramObject2 };
    String str = null;
    assert argumentTypesMatch(str = "LLL_L", arrayOfObject);
    Object object = paramMethodHandle.form.interpretWithArguments(arrayOfObject);
    assert returnTypesMatch(str, arrayOfObject, object);
    return object;
  }
  
  private static LambdaForm getPreparedForm(String paramString) {
    MethodType methodType = signatureType(paramString);
    LambdaForm lambdaForm = methodType.form().cachedLambdaForm(6);
    if (lambdaForm != null)
      return lambdaForm; 
    assert isValidSignature(paramString);
    lambdaForm = new LambdaForm(paramString);
    lambdaForm.vmentry = InvokerBytecodeGenerator.generateLambdaFormInterpreterEntryPoint(paramString);
    return methodType.form().setCachedLambdaForm(6, lambdaForm);
  }
  
  private static boolean argumentTypesMatch(String paramString, Object[] paramArrayOfObject) {
    int i = signatureArity(paramString);
    assert paramArrayOfObject.length == i : "av.length == arity: av.length=" + paramArrayOfObject.length + ", arity=" + i;
    assert paramArrayOfObject[0] instanceof MethodHandle : "av[0] not instace of MethodHandle: " + paramArrayOfObject[false];
    MethodHandle methodHandle = (MethodHandle)paramArrayOfObject[0];
    MethodType methodType = methodHandle.type();
    assert methodType.parameterCount() == i - 1;
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      Class clazz = !b ? MethodHandle.class : methodType.parameterType(b - true);
      assert valueMatches(BasicType.basicType(paramString.charAt(b)), clazz, paramArrayOfObject[b]);
    } 
    return true;
  }
  
  private static boolean valueMatches(BasicType paramBasicType, Class<?> paramClass, Object paramObject) {
    if (paramClass == void.class)
      paramBasicType = BasicType.V_TYPE; 
    assert paramBasicType == BasicType.basicType(paramClass) : paramBasicType + " == basicType(" + paramClass + ")=" + BasicType.basicType(paramClass);
    switch (paramBasicType) {
      case I_TYPE:
        assert checkInt(paramClass, paramObject) : "checkInt(" + paramClass + "," + paramObject + ")";
      case J_TYPE:
        assert paramObject instanceof Long : "instanceof Long: " + paramObject;
      case F_TYPE:
        assert paramObject instanceof Float : "instanceof Float: " + paramObject;
      case D_TYPE:
        assert paramObject instanceof Double : "instanceof Double: " + paramObject;
      case L_TYPE:
        assert checkRef(paramClass, paramObject) : "checkRef(" + paramClass + "," + paramObject + ")";
      case V_TYPE:
        return true;
    } 
    assert false;
  }
  
  private static boolean returnTypesMatch(String paramString, Object[] paramArrayOfObject, Object paramObject) {
    MethodHandle methodHandle = (MethodHandle)paramArrayOfObject[0];
    return valueMatches(signatureReturn(paramString), methodHandle.type().returnType(), paramObject);
  }
  
  private static boolean checkInt(Class<?> paramClass, Object paramObject) {
    assert paramObject instanceof Integer;
    if (paramClass == int.class)
      return true; 
    Wrapper wrapper = Wrapper.forBasicType(paramClass);
    assert wrapper.isSubwordOrInt();
    Object object = Wrapper.INT.wrap(wrapper.wrap(paramObject));
    return paramObject.equals(object);
  }
  
  private static boolean checkRef(Class<?> paramClass, Object paramObject) {
    assert !paramClass.isPrimitive();
    return (paramObject == null) ? true : (paramClass.isInterface() ? true : paramClass.isInstance(paramObject));
  }
  
  @Hidden
  @DontInline
  Object interpretWithArguments(Object... paramVarArgs) throws Throwable {
    if (TRACE_INTERPRETER)
      return interpretWithArgumentsTracing(paramVarArgs); 
    checkInvocationCounter();
    assert arityCheck(paramVarArgs);
    Object[] arrayOfObject = Arrays.copyOf(paramVarArgs, this.names.length);
    for (int i = paramVarArgs.length; i < arrayOfObject.length; i++)
      arrayOfObject[i] = interpretName(this.names[i], arrayOfObject); 
    Object object = (this.result < 0) ? null : arrayOfObject[this.result];
    assert resultCheck(paramVarArgs, object);
    return object;
  }
  
  @Hidden
  @DontInline
  Object interpretName(Name paramName, Object[] paramArrayOfObject) throws Throwable {
    if (TRACE_INTERPRETER)
      traceInterpreter("| interpretName", paramName.debugString(), (Object[])null); 
    Object[] arrayOfObject = Arrays.copyOf(paramName.arguments, paramName.arguments.length, Object[].class);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      Object object = arrayOfObject[b];
      if (object instanceof Name) {
        int i = ((Name)object).index();
        assert this.names[i] == object;
        object = paramArrayOfObject[i];
        arrayOfObject[b] = object;
      } 
    } 
    return paramName.function.invokeWithArguments(arrayOfObject);
  }
  
  private void checkInvocationCounter() {
    if (COMPILE_THRESHOLD != 0 && this.invocationCounter < COMPILE_THRESHOLD) {
      this.invocationCounter++;
      if (this.invocationCounter >= COMPILE_THRESHOLD)
        compileToBytecode(); 
    } 
  }
  
  Object interpretWithArgumentsTracing(Object... paramVarArgs) throws Throwable {
    Object object;
    traceInterpreter("[ interpretWithArguments", this, paramVarArgs);
    if (this.invocationCounter < COMPILE_THRESHOLD) {
      int i = this.invocationCounter++;
      traceInterpreter("| invocationCounter", Integer.valueOf(i));
      if (this.invocationCounter >= COMPILE_THRESHOLD)
        compileToBytecode(); 
    } 
    try {
      assert arityCheck(paramVarArgs);
      Object[] arrayOfObject = Arrays.copyOf(paramVarArgs, this.names.length);
      for (int i = paramVarArgs.length; i < arrayOfObject.length; i++)
        arrayOfObject[i] = interpretName(this.names[i], arrayOfObject); 
      object = (this.result < 0) ? null : arrayOfObject[this.result];
    } catch (Throwable throwable) {
      traceInterpreter("] throw =>", throwable);
      throw throwable;
    } 
    traceInterpreter("] return =>", object);
    return object;
  }
  
  static void traceInterpreter(String paramString, Object paramObject, Object... paramVarArgs) {
    if (TRACE_INTERPRETER)
      System.out.println("LFI: " + paramString + " " + ((paramObject != null) ? paramObject : "") + ((paramVarArgs != null && paramVarArgs.length != 0) ? Arrays.asList(paramVarArgs) : "")); 
  }
  
  static void traceInterpreter(String paramString, Object paramObject) { traceInterpreter(paramString, paramObject, (Object[])null); }
  
  private boolean arityCheck(Object[] paramArrayOfObject) {
    assert paramArrayOfObject.length == this.arity : this.arity + "!=" + Arrays.asList(paramArrayOfObject) + ".length";
    assert paramArrayOfObject[0] instanceof MethodHandle : "not MH: " + paramArrayOfObject[false];
    MethodHandle methodHandle = (MethodHandle)paramArrayOfObject[0];
    assert methodHandle.internalForm() == this;
    argumentTypesMatch(basicTypeSignature(), paramArrayOfObject);
    return true;
  }
  
  private boolean resultCheck(Object[] paramArrayOfObject, Object paramObject) {
    MethodHandle methodHandle = (MethodHandle)paramArrayOfObject[0];
    MethodType methodType = methodHandle.type();
    assert valueMatches(returnType(), methodType.returnType(), paramObject);
    return true;
  }
  
  private boolean isEmpty() { return (this.result < 0) ? ((this.names.length == this.arity)) : ((this.result == this.arity && this.names.length == this.arity + 1) ? this.names[this.arity].isConstantZero() : 0); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(this.debugName + "=Lambda(");
    for (byte b = 0; b < this.names.length; b++) {
      if (b == this.arity)
        stringBuilder.append(")=>{"); 
      Name name = this.names[b];
      if (b >= this.arity)
        stringBuilder.append("\n    "); 
      stringBuilder.append(name.paramString());
      if (b < this.arity) {
        if (b + true < this.arity)
          stringBuilder.append(","); 
      } else {
        stringBuilder.append("=").append(name.exprString());
        stringBuilder.append(";");
      } 
    } 
    if (this.arity == this.names.length)
      stringBuilder.append(")=>{"); 
    stringBuilder.append((this.result < 0) ? "void" : this.names[this.result]).append("}");
    if (TRACE_INTERPRETER) {
      stringBuilder.append(":").append(basicTypeSignature());
      stringBuilder.append("/").append(this.vmentry);
    } 
    return stringBuilder.toString();
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof LambdaForm && equals((LambdaForm)paramObject)); }
  
  public boolean equals(LambdaForm paramLambdaForm) { return (this.result != paramLambdaForm.result) ? false : Arrays.equals(this.names, paramLambdaForm.names); }
  
  public int hashCode() { return this.result + 31 * Arrays.hashCode(this.names); }
  
  LambdaFormEditor editor() { return LambdaFormEditor.lambdaFormEditor(this); }
  
  boolean contains(Name paramName) {
    int i = paramName.index();
    if (i >= 0)
      return (i < this.names.length && paramName.equals(this.names[i])); 
    for (int j = this.arity; j < this.names.length; j++) {
      if (paramName.equals(this.names[j]))
        return true; 
    } 
    return false;
  }
  
  LambdaForm addArguments(int paramInt, BasicType... paramVarArgs) {
    int i = paramInt + 1;
    assert i <= this.arity;
    int j = this.names.length;
    int k = paramVarArgs.length;
    Name[] arrayOfName = (Name[])Arrays.copyOf(this.names, j + k);
    int m = this.arity + k;
    int n = this.result;
    if (n >= i)
      n += k; 
    System.arraycopy(this.names, i, arrayOfName, i + k, j - i);
    for (int i1 = 0; i1 < k; i1++)
      arrayOfName[i + i1] = new Name(paramVarArgs[i1]); 
    return new LambdaForm(this.debugName, m, arrayOfName, n);
  }
  
  LambdaForm addArguments(int paramInt, List<Class<?>> paramList) { return addArguments(paramInt, BasicType.basicTypes(paramList)); }
  
  LambdaForm permuteArguments(int paramInt, int[] paramArrayOfInt, BasicType[] paramArrayOfBasicType) {
    int i = this.names.length;
    int j = paramArrayOfBasicType.length;
    int k = paramArrayOfInt.length;
    assert paramInt + k == this.arity;
    assert permutedTypesMatch(paramArrayOfInt, paramArrayOfBasicType, this.names, paramInt);
    int m;
    for (m = 0; m < k && paramArrayOfInt[m] == m; m++);
    Name[] arrayOfName = new Name[i - k + j];
    System.arraycopy(this.names, 0, arrayOfName, 0, paramInt + m);
    int n = i - this.arity;
    System.arraycopy(this.names, paramInt + k, arrayOfName, paramInt + j, n);
    int i1 = arrayOfName.length - n;
    int i2 = this.result;
    if (i2 >= 0)
      if (i2 < paramInt + k) {
        i2 = paramArrayOfInt[i2 - paramInt];
      } else {
        i2 = i2 - k + j;
      }  
    int i3;
    for (i3 = m; i3 < k; i3++) {
      Name name1 = this.names[paramInt + i3];
      int i4 = paramArrayOfInt[i3];
      Name name2 = arrayOfName[paramInt + i4];
      if (name2 == null) {
        arrayOfName[paramInt + i4] = name2 = new Name(paramArrayOfBasicType[i4]);
      } else {
        assert name2.type == paramArrayOfBasicType[i4];
      } 
      for (int i5 = i1; i5 < arrayOfName.length; i5++)
        arrayOfName[i5] = arrayOfName[i5].replaceName(name1, name2); 
    } 
    for (i3 = paramInt + m; i3 < i1; i3++) {
      if (arrayOfName[i3] == null)
        arrayOfName[i3] = argument(i3, paramArrayOfBasicType[i3 - paramInt]); 
    } 
    for (i3 = this.arity; i3 < this.names.length; i3++) {
      int i4 = i3 - this.arity + i1;
      Name name1 = this.names[i3];
      Name name2 = arrayOfName[i4];
      if (name1 != name2)
        for (int i5 = i4 + 1; i5 < arrayOfName.length; i5++)
          arrayOfName[i5] = arrayOfName[i5].replaceName(name1, name2);  
    } 
    return new LambdaForm(this.debugName, i1, arrayOfName, i2);
  }
  
  static boolean permutedTypesMatch(int[] paramArrayOfInt, BasicType[] paramArrayOfBasicType, Name[] paramArrayOfName, int paramInt) {
    int i = paramArrayOfBasicType.length;
    int j = paramArrayOfInt.length;
    for (int k = 0; k < j; k++) {
      assert paramArrayOfName[paramInt + k].isParam();
      assert (paramArrayOfName[paramInt + k]).type == paramArrayOfBasicType[paramArrayOfInt[k]];
    } 
    return true;
  }
  
  public static String basicTypeSignature(MethodType paramMethodType) {
    char[] arrayOfChar = new char[paramMethodType.parameterCount() + 2];
    byte b = 0;
    for (Class clazz : paramMethodType.parameterList())
      arrayOfChar[b++] = BasicType.basicTypeChar(clazz); 
    arrayOfChar[b++] = '_';
    arrayOfChar[b++] = BasicType.basicTypeChar(paramMethodType.returnType());
    assert b == arrayOfChar.length;
    return String.valueOf(arrayOfChar);
  }
  
  public static String shortenSignature(String paramString) {
    byte b = -1;
    byte b1 = 0;
    StringBuilder stringBuilder = null;
    int i = paramString.length();
    if (i < 3)
      return paramString; 
    for (byte b2 = 0; b2 <= i; b2++) {
      byte b3 = b;
      b = (b2 == i) ? -1 : paramString.charAt(b2);
      if (b == b3) {
        b1++;
      } else {
        byte b4 = b1;
        b1 = 1;
        if (b4 < 3) {
          if (stringBuilder != null)
            while (--b4 >= 0)
              stringBuilder.append((char)b3);  
        } else {
          if (stringBuilder == null)
            stringBuilder = (new StringBuilder()).append(paramString, 0, b2 - b4); 
          stringBuilder.append((char)b3).append(b4);
        } 
      } 
    } 
    return (stringBuilder == null) ? paramString : stringBuilder.toString();
  }
  
  int lastUseIndex(Name paramName) {
    short s = paramName.index;
    int i = this.names.length;
    assert this.names[s] == paramName;
    if (this.result == s)
      return i; 
    int j = i;
    while (--j > s) {
      if (this.names[j].lastUseIndex(paramName) >= 0)
        return j; 
    } 
    return -1;
  }
  
  int useCount(Name paramName) {
    short s = paramName.index;
    int i = this.names.length;
    int j = lastUseIndex(paramName);
    if (j < 0)
      return 0; 
    int k = 0;
    if (j == i) {
      k++;
      j--;
    } 
    int m = paramName.index() + 1;
    if (m < this.arity)
      m = this.arity; 
    for (int n = m; n <= j; n++)
      k += this.names[n].useCount(paramName); 
    return k;
  }
  
  static Name argument(int paramInt, char paramChar) { return argument(paramInt, BasicType.basicType(paramChar)); }
  
  static Name argument(int paramInt, BasicType paramBasicType) { return (paramInt >= 10) ? new Name(paramInt, paramBasicType) : INTERNED_ARGUMENTS[paramBasicType.ordinal()][paramInt]; }
  
  static Name internArgument(Name paramName) {
    assert paramName.isParam() : "not param: " + paramName;
    assert paramName.index < 10;
    return (paramName.constraint != null) ? paramName : argument(paramName.index, paramName.type);
  }
  
  static Name[] arguments(int paramInt, String paramString) {
    int i = paramString.length();
    Name[] arrayOfName = new Name[i + paramInt];
    for (byte b = 0; b < i; b++)
      arrayOfName[b] = argument(b, paramString.charAt(b)); 
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, char... paramVarArgs) {
    int i = paramVarArgs.length;
    Name[] arrayOfName = new Name[i + paramInt];
    for (byte b = 0; b < i; b++)
      arrayOfName[b] = argument(b, paramVarArgs[b]); 
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, List<Class<?>> paramList) {
    int i = paramList.size();
    Name[] arrayOfName = new Name[i + paramInt];
    for (byte b = 0; b < i; b++)
      arrayOfName[b] = argument(b, BasicType.basicType((Class)paramList.get(b))); 
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, Class<?>... paramVarArgs) {
    int i = paramVarArgs.length;
    Name[] arrayOfName = new Name[i + paramInt];
    for (byte b = 0; b < i; b++)
      arrayOfName[b] = argument(b, BasicType.basicType(paramVarArgs[b])); 
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, MethodType paramMethodType) {
    int i = paramMethodType.parameterCount();
    Name[] arrayOfName = new Name[i + paramInt];
    for (byte b = 0; b < i; b++)
      arrayOfName[b] = argument(b, BasicType.basicType(paramMethodType.parameterType(b))); 
    return arrayOfName;
  }
  
  static LambdaForm identityForm(BasicType paramBasicType) { return LF_identityForm[paramBasicType.ordinal()]; }
  
  static LambdaForm zeroForm(BasicType paramBasicType) { return LF_zeroForm[paramBasicType.ordinal()]; }
  
  static NamedFunction identity(BasicType paramBasicType) { return NF_identity[paramBasicType.ordinal()]; }
  
  static NamedFunction constantZero(BasicType paramBasicType) { return NF_zero[paramBasicType.ordinal()]; }
  
  private static void createIdentityForms() {
    for (BasicType basicType : BasicType.ALL_TYPES) {
      LambdaForm lambdaForm2;
      LambdaForm lambdaForm1;
      int i = basicType.ordinal();
      char c = basicType.basicTypeChar();
      boolean bool = (basicType == BasicType.V_TYPE) ? 1 : 0;
      Class clazz = basicType.btClass;
      MethodType methodType1 = MethodType.methodType(clazz);
      MethodType methodType2 = bool ? methodType1 : methodType1.appendParameterTypes(new Class[] { clazz });
      MemberName memberName1 = new MemberName(LambdaForm.class, "identity_" + c, methodType2, (byte)6);
      MemberName memberName2 = new MemberName(LambdaForm.class, "zero_" + c, methodType1, (byte)6);
      try {
        memberName2 = IMPL_NAMES.resolveOrFail((byte)6, memberName2, null, NoSuchMethodException.class);
        memberName1 = IMPL_NAMES.resolveOrFail((byte)6, memberName1, null, NoSuchMethodException.class);
      } catch (IllegalAccessException|NoSuchMethodException illegalAccessException) {
        throw MethodHandleStatics.newInternalError(illegalAccessException);
      } 
      NamedFunction namedFunction1 = new NamedFunction(memberName1);
      if (bool) {
        Name[] arrayOfName = { argument(0, BasicType.L_TYPE) };
        lambdaForm1 = new LambdaForm(memberName1.getName(), 1, arrayOfName, -1);
      } else {
        Name[] arrayOfName = { argument(0, BasicType.L_TYPE), argument(1, basicType) };
        lambdaForm1 = new LambdaForm(memberName1.getName(), 2, arrayOfName, 1);
      } 
      LF_identityForm[i] = lambdaForm1;
      NF_identity[i] = namedFunction1;
      NamedFunction namedFunction2 = new NamedFunction(memberName2);
      if (bool) {
        lambdaForm2 = lambdaForm1;
      } else {
        Object object = Wrapper.forBasicType(c).zero();
        Name[] arrayOfName = { argument(0, BasicType.L_TYPE), new Name(namedFunction1, new Object[] { object }) };
        lambdaForm2 = new LambdaForm(memberName2.getName(), 1, arrayOfName, 1);
      } 
      LF_zeroForm[i] = lambdaForm2;
      NF_zero[i] = namedFunction2;
      assert namedFunction1.isIdentity();
      assert namedFunction2.isConstantZero();
      assert (new Name(namedFunction2, new Object[0])).isConstantZero();
    } 
    for (BasicType basicType : BasicType.ALL_TYPES) {
      int i = basicType.ordinal();
      NamedFunction namedFunction1 = NF_identity[i];
      LambdaForm lambdaForm1 = LF_identityForm[i];
      MemberName memberName1 = namedFunction1.member;
      namedFunction1.resolvedHandle = SimpleMethodHandle.make(memberName1.getInvocationType(), lambdaForm1);
      NamedFunction namedFunction2 = NF_zero[i];
      LambdaForm lambdaForm2 = LF_zeroForm[i];
      MemberName memberName2 = namedFunction2.member;
      namedFunction2.resolvedHandle = SimpleMethodHandle.make(memberName2.getInvocationType(), lambdaForm2);
      assert namedFunction1.isIdentity();
      assert namedFunction2.isConstantZero();
      assert (new Name(namedFunction2, new Object[0])).isConstantZero();
    } 
  }
  
  private static int identity_I(int paramInt) { return paramInt; }
  
  private static long identity_J(long paramLong) { return paramLong; }
  
  private static float identity_F(float paramFloat) { return paramFloat; }
  
  private static double identity_D(double paramDouble) { return paramDouble; }
  
  private static Object identity_L(Object paramObject) { return paramObject; }
  
  private static void identity_V() {}
  
  private static int zero_I() { return 0; }
  
  private static long zero_J() { return 0L; }
  
  private static float zero_F() { return 0.0F; }
  
  private static double zero_D() { return 0.0D; }
  
  private static Object zero_L() { return null; }
  
  private static void zero_V() {}
  
  static  {
    for (BasicType basicType : BasicType.ARG_TYPES) {
      int i = basicType.ordinal();
      for (byte b = 0; b < INTERNED_ARGUMENTS[i].length; b++)
        INTERNED_ARGUMENTS[i][b] = new Name(b, basicType); 
    } 
    IMPL_NAMES = MemberName.getFactory();
    LF_identityForm = new LambdaForm[BasicType.TYPE_LIMIT];
    LF_zeroForm = new LambdaForm[BasicType.TYPE_LIMIT];
    NF_identity = new NamedFunction[BasicType.TYPE_LIMIT];
    NF_zero = new NamedFunction[BasicType.TYPE_LIMIT];
    if (MethodHandleStatics.debugEnabled()) {
      DEBUG_NAME_COUNTERS = new HashMap();
    } else {
      DEBUG_NAME_COUNTERS = null;
    } 
    createIdentityForms();
    computeInitialPreparedForms();
    NamedFunction.initializeInvokers();
    TRACE_INTERPRETER = MethodHandleStatics.TRACE_INTERPRETER;
  }
  
  enum BasicType {
    L_TYPE('L', Object.class, Wrapper.OBJECT),
    I_TYPE('I', int.class, Wrapper.INT),
    J_TYPE('J', long.class, Wrapper.LONG),
    F_TYPE('F', float.class, Wrapper.FLOAT),
    D_TYPE('D', double.class, Wrapper.DOUBLE),
    V_TYPE('V', void.class, Wrapper.VOID);
    
    static final BasicType[] ALL_TYPES;
    
    static final BasicType[] ARG_TYPES;
    
    static final int ARG_TYPE_LIMIT;
    
    static final int TYPE_LIMIT;
    
    private final char btChar;
    
    private final Class<?> btClass;
    
    private final Wrapper btWrapper;
    
    BasicType(Class<?> param1Class, Wrapper param1Wrapper1, Wrapper param1Wrapper2) {
      this.btChar = param1Class;
      this.btClass = param1Wrapper1;
      this.btWrapper = param1Wrapper2;
    }
    
    char basicTypeChar() { return this.btChar; }
    
    Class<?> basicTypeClass() { return this.btClass; }
    
    Wrapper basicTypeWrapper() { return this.btWrapper; }
    
    int basicTypeSlots() { return this.btWrapper.stackSlots(); }
    
    static BasicType basicType(byte param1Byte) { return ALL_TYPES[param1Byte]; }
    
    static BasicType basicType(char param1Char) {
      switch (param1Char) {
        case 'L':
          return L_TYPE;
        case 'I':
          return I_TYPE;
        case 'J':
          return J_TYPE;
        case 'F':
          return F_TYPE;
        case 'D':
          return D_TYPE;
        case 'V':
          return V_TYPE;
        case 'B':
        case 'C':
        case 'S':
        case 'Z':
          return I_TYPE;
      } 
      throw MethodHandleStatics.newInternalError("Unknown type char: '" + param1Char + "'");
    }
    
    static BasicType basicType(Wrapper param1Wrapper) {
      char c = param1Wrapper.basicTypeChar();
      return basicType(c);
    }
    
    static BasicType basicType(Class<?> param1Class) { return !param1Class.isPrimitive() ? L_TYPE : basicType(Wrapper.forPrimitiveType(param1Class)); }
    
    static char basicTypeChar(Class<?> param1Class) { return (basicType(param1Class)).btChar; }
    
    static BasicType[] basicTypes(List<Class<?>> param1List) {
      BasicType[] arrayOfBasicType = new BasicType[param1List.size()];
      for (byte b = 0; b < arrayOfBasicType.length; b++)
        arrayOfBasicType[b] = basicType((Class)param1List.get(b)); 
      return arrayOfBasicType;
    }
    
    static BasicType[] basicTypes(String param1String) {
      BasicType[] arrayOfBasicType = new BasicType[param1String.length()];
      for (byte b = 0; b < arrayOfBasicType.length; b++)
        arrayOfBasicType[b] = basicType(param1String.charAt(b)); 
      return arrayOfBasicType;
    }
    
    static byte[] basicTypesOrd(BasicType[] param1ArrayOfBasicType) {
      byte[] arrayOfByte = new byte[param1ArrayOfBasicType.length];
      for (byte b = 0; b < param1ArrayOfBasicType.length; b++)
        arrayOfByte[b] = (byte)param1ArrayOfBasicType[b].ordinal(); 
      return arrayOfByte;
    }
    
    static boolean isBasicTypeChar(char param1Char) { return ("LIJFDV".indexOf(param1Char) >= 0); }
    
    static boolean isArgBasicTypeChar(char param1Char) { return ("LIJFD".indexOf(param1Char) >= 0); }
    
    private static boolean checkBasicType() {
      byte b;
      for (b = 0; b < ARG_TYPE_LIMIT; b++) {
        assert ARG_TYPES[b].ordinal() == b;
        assert ARG_TYPES[b] == ALL_TYPES[b];
      } 
      for (b = 0; b < TYPE_LIMIT; b++)
        assert ALL_TYPES[b].ordinal() == b; 
      assert ALL_TYPES[TYPE_LIMIT - true] == V_TYPE;
      assert !Arrays.asList(ARG_TYPES).contains(V_TYPE);
      return true;
    }
    
    static  {
      ALL_TYPES = values();
      ARG_TYPES = (BasicType[])Arrays.copyOf(ALL_TYPES, ALL_TYPES.length - 1);
      ARG_TYPE_LIMIT = ARG_TYPES.length;
      TYPE_LIMIT = ALL_TYPES.length;
      assert checkBasicType();
    }
  }
  
  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface Compiled {}
  
  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface Hidden {}
  
  static final class Name {
    final LambdaForm.BasicType type;
    
    private short index;
    
    final LambdaForm.NamedFunction function;
    
    final Object constraint;
    
    @Stable
    final Object[] arguments;
    
    private Name(int param1Int, LambdaForm.BasicType param1BasicType, LambdaForm.NamedFunction param1NamedFunction, Object[] param1ArrayOfObject) {
      this.index = (short)param1Int;
      this.type = param1BasicType;
      this.function = param1NamedFunction;
      this.arguments = param1ArrayOfObject;
      this.constraint = null;
      assert this.index == param1Int;
    }
    
    private Name(Name param1Name, Object param1Object) {
      this.index = param1Name.index;
      this.type = param1Name.type;
      this.function = param1Name.function;
      this.arguments = param1Name.arguments;
      this.constraint = param1Object;
      assert param1Object == null || isParam();
      assert param1Object == null || param1Object instanceof BoundMethodHandle.SpeciesData || param1Object instanceof Class;
    }
    
    Name(MethodHandle param1MethodHandle, Object... param1VarArgs) { this(new LambdaForm.NamedFunction(param1MethodHandle), param1VarArgs); }
    
    Name(MethodType param1MethodType, Object... param1VarArgs) {
      this(new LambdaForm.NamedFunction(param1MethodType), param1VarArgs);
      assert param1VarArgs[0] instanceof Name && ((Name)param1VarArgs[false]).type == LambdaForm.BasicType.L_TYPE;
    }
    
    Name(MemberName param1MemberName, Object... param1VarArgs) { this(new LambdaForm.NamedFunction(param1MemberName), param1VarArgs); }
    
    Name(LambdaForm.NamedFunction param1NamedFunction, Object... param1VarArgs) {
      this(-1, param1NamedFunction.returnType(), param1NamedFunction, param1VarArgs = Arrays.copyOf(param1VarArgs, param1VarArgs.length, Object[].class));
      assert param1VarArgs.length == param1NamedFunction.arity() : "arity mismatch: arguments.length=" + param1VarArgs.length + " == function.arity()=" + param1NamedFunction.arity() + " in " + debugString();
      for (byte b = 0; b < param1VarArgs.length; b++)
        assert typesMatch(param1NamedFunction.parameterType(b), param1VarArgs[b]) : "types don't match: function.parameterType(" + b + ")=" + param1NamedFunction.parameterType(b) + ", arguments[" + b + "]=" + param1VarArgs[b] + " in " + debugString(); 
    }
    
    Name(int param1Int, LambdaForm.BasicType param1BasicType) { this(param1Int, param1BasicType, null, null); }
    
    Name(LambdaForm.BasicType param1BasicType) { this(-1, param1BasicType); }
    
    LambdaForm.BasicType type() { return this.type; }
    
    int index() { return this.index; }
    
    boolean initIndex(int param1Int) {
      if (this.index != param1Int) {
        if (this.index != -1)
          return false; 
        this.index = (short)param1Int;
      } 
      return true;
    }
    
    char typeChar() { return this.type.btChar; }
    
    void resolve() {
      if (this.function != null)
        this.function.resolve(); 
    }
    
    Name newIndex(int param1Int) { return initIndex(param1Int) ? this : cloneWithIndex(param1Int); }
    
    Name cloneWithIndex(int param1Int) {
      Object[] arrayOfObject = (this.arguments == null) ? null : (Object[])this.arguments.clone();
      return (new Name(param1Int, this.type, this.function, arrayOfObject)).withConstraint(this.constraint);
    }
    
    Name withConstraint(Object param1Object) { return (param1Object == this.constraint) ? this : new Name(this, param1Object); }
    
    Name replaceName(Name param1Name1, Name param1Name2) {
      if (param1Name1 == param1Name2)
        return this; 
      Object[] arrayOfObject = this.arguments;
      if (arrayOfObject == null)
        return this; 
      boolean bool = false;
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] == param1Name1) {
          if (!bool) {
            bool = true;
            arrayOfObject = (Object[])arrayOfObject.clone();
          } 
          arrayOfObject[b] = param1Name2;
        } 
      } 
      return !bool ? this : new Name(this.function, arrayOfObject);
    }
    
    Name replaceNames(Name[] param1ArrayOfName1, Name[] param1ArrayOfName2, int param1Int1, int param1Int2) {
      if (param1Int1 >= param1Int2)
        return this; 
      Object[] arrayOfObject = this.arguments;
      boolean bool = false;
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] instanceof Name) {
          Name name = (Name)arrayOfObject[b];
          short s = name.index;
          if (s < 0 || s >= param1ArrayOfName2.length || name != param1ArrayOfName2[s])
            for (int i = param1Int1; i < param1Int2; i++) {
              if (name == param1ArrayOfName1[i]) {
                if (name == param1ArrayOfName2[i])
                  break; 
                if (!bool) {
                  bool = true;
                  arrayOfObject = (Object[])arrayOfObject.clone();
                } 
                arrayOfObject[b] = param1ArrayOfName2[i];
                break;
              } 
            }  
        } 
      } 
      return !bool ? this : new Name(this.function, arrayOfObject);
    }
    
    void internArguments() {
      Object[] arrayOfObject = this.arguments;
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] instanceof Name) {
          Name name = (Name)arrayOfObject[b];
          if (name.isParam() && name.index < 10)
            arrayOfObject[b] = LambdaForm.internArgument(name); 
        } 
      } 
    }
    
    boolean isParam() { return (this.function == null); }
    
    boolean isConstantZero() { return (!isParam() && this.arguments.length == 0 && this.function.isConstantZero()); }
    
    public String toString() { return (isParam() ? "a" : "t") + ((this.index >= 0) ? this.index : System.identityHashCode(this)) + ":" + typeChar(); }
    
    public String debugString() {
      String str = paramString();
      return (this.function == null) ? str : (str + "=" + exprString());
    }
    
    public String paramString() {
      String str = toString();
      Object object = this.constraint;
      if (object == null)
        return str; 
      if (object instanceof Class)
        object = ((Class)object).getSimpleName(); 
      return str + "/" + object;
    }
    
    public String exprString() {
      if (this.function == null)
        return toString(); 
      StringBuilder stringBuilder = new StringBuilder(this.function.toString());
      stringBuilder.append("(");
      String str = "";
      for (Object object : this.arguments) {
        stringBuilder.append(str);
        str = ",";
        if (object instanceof Name || object instanceof Integer) {
          stringBuilder.append(object);
        } else {
          stringBuilder.append("(").append(object).append(")");
        } 
      } 
      stringBuilder.append(")");
      return stringBuilder.toString();
    }
    
    static boolean typesMatch(LambdaForm.BasicType param1BasicType, Object param1Object) {
      if (param1Object instanceof Name)
        return (((Name)param1Object).type == param1BasicType); 
      switch (LambdaForm.null.$SwitchMap$java$lang$invoke$LambdaForm$BasicType[param1BasicType.ordinal()]) {
        case 1:
          return param1Object instanceof Integer;
        case 2:
          return param1Object instanceof Long;
        case 3:
          return param1Object instanceof Float;
        case 4:
          return param1Object instanceof Double;
      } 
      assert param1BasicType == LambdaForm.BasicType.L_TYPE;
      return true;
    }
    
    int lastUseIndex(Name param1Name) {
      if (this.arguments == null)
        return -1; 
      int i = this.arguments.length;
      while (--i >= 0) {
        if (this.arguments[i] == param1Name)
          return i; 
      } 
      return -1;
    }
    
    int useCount(Name param1Name) {
      if (this.arguments == null)
        return 0; 
      byte b = 0;
      int i = this.arguments.length;
      while (--i >= 0) {
        if (this.arguments[i] == param1Name)
          b++; 
      } 
      return b;
    }
    
    boolean contains(Name param1Name) { return (this == param1Name || lastUseIndex(param1Name) >= 0); }
    
    public boolean equals(Name param1Name) { return (this == param1Name) ? true : (isParam() ? false : ((this.type == param1Name.type && this.function.equals(param1Name.function) && Arrays.equals(this.arguments, param1Name.arguments)))); }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof Name && equals((Name)param1Object)); }
    
    public int hashCode() { return isParam() ? (this.index | this.type.ordinal() << 8) : (this.function.hashCode() ^ Arrays.hashCode(this.arguments)); }
  }
  
  static class NamedFunction {
    final MemberName member;
    
    @Stable
    MethodHandle resolvedHandle;
    
    @Stable
    MethodHandle invoker;
    
    static final MethodType INVOKER_METHOD_TYPE = MethodType.methodType(Object.class, MethodHandle.class, new Class[] { Object[].class });
    
    NamedFunction(MethodHandle param1MethodHandle) { this(param1MethodHandle.internalMemberName(), param1MethodHandle); }
    
    NamedFunction(MemberName param1MemberName, MethodHandle param1MethodHandle) {
      this.member = param1MemberName;
      this.resolvedHandle = param1MethodHandle;
    }
    
    NamedFunction(MethodType param1MethodType) {
      assert param1MethodType == param1MethodType.basicType() : param1MethodType;
      if (param1MethodType.parameterSlotCount() < 253) {
        this.resolvedHandle = param1MethodType.invokers().basicInvoker();
        this.member = this.resolvedHandle.internalMemberName();
      } else {
        this.member = Invokers.invokeBasicMethod(param1MethodType);
      } 
      assert isInvokeBasic(this.member);
    }
    
    private static boolean isInvokeBasic(MemberName param1MemberName) { return (param1MemberName != null && param1MemberName.getDeclaringClass() == MethodHandle.class && "invokeBasic".equals(param1MemberName.getName())); }
    
    NamedFunction(Method param1Method) { this(new MemberName(param1Method)); }
    
    NamedFunction(Field param1Field) { this(new MemberName(param1Field)); }
    
    NamedFunction(MemberName param1MemberName) {
      this.member = param1MemberName;
      this.resolvedHandle = null;
    }
    
    MethodHandle resolvedHandle() {
      if (this.resolvedHandle == null)
        resolve(); 
      return this.resolvedHandle;
    }
    
    void resolve() { this.resolvedHandle = DirectMethodHandle.make(this.member); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null)
        return false; 
      if (!(param1Object instanceof NamedFunction))
        return false; 
      NamedFunction namedFunction = (NamedFunction)param1Object;
      return (this.member != null && this.member.equals(namedFunction.member));
    }
    
    public int hashCode() { return (this.member != null) ? this.member.hashCode() : super.hashCode(); }
    
    static void initializeInvokers() {
      for (MemberName memberName : MemberName.getFactory().getMethods(NamedFunction.class, false, null, null, null)) {
        if (!memberName.isStatic() || !memberName.isPackage())
          continue; 
        MethodType methodType = memberName.getMethodType();
        if (methodType.equals(INVOKER_METHOD_TYPE) && memberName.getName().startsWith("invoke_")) {
          String str = memberName.getName().substring("invoke_".length());
          int i = LambdaForm.signatureArity(str);
          MethodType methodType1 = MethodType.genericMethodType(i);
          if (LambdaForm.signatureReturn(str) == LambdaForm.BasicType.V_TYPE)
            methodType1 = methodType1.changeReturnType(void.class); 
          MethodTypeForm methodTypeForm = methodType1.form();
          methodTypeForm.setCachedMethodHandle(1, DirectMethodHandle.make(memberName));
        } 
      } 
    }
    
    @Hidden
    static Object invoke__V(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(0, void.class, param1MethodHandle, param1ArrayOfObject);
      param1MethodHandle.invokeBasic();
      return null;
    }
    
    @Hidden
    static Object invoke_L_V(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(1, void.class, param1MethodHandle, param1ArrayOfObject);
      param1MethodHandle.invokeBasic(param1ArrayOfObject[0]);
      return null;
    }
    
    @Hidden
    static Object invoke_LL_V(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(2, void.class, param1MethodHandle, param1ArrayOfObject);
      param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1]);
      return null;
    }
    
    @Hidden
    static Object invoke_LLL_V(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(3, void.class, param1MethodHandle, param1ArrayOfObject);
      param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1], param1ArrayOfObject[2]);
      return null;
    }
    
    @Hidden
    static Object invoke_LLLL_V(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(4, void.class, param1MethodHandle, param1ArrayOfObject);
      param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1], param1ArrayOfObject[2], param1ArrayOfObject[3]);
      return null;
    }
    
    @Hidden
    static Object invoke_LLLLL_V(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(5, void.class, param1MethodHandle, param1ArrayOfObject);
      param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1], param1ArrayOfObject[2], param1ArrayOfObject[3], param1ArrayOfObject[4]);
      return null;
    }
    
    @Hidden
    static Object invoke__L(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(0, param1MethodHandle, param1ArrayOfObject);
      return param1MethodHandle.invokeBasic();
    }
    
    @Hidden
    static Object invoke_L_L(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(1, param1MethodHandle, param1ArrayOfObject);
      return param1MethodHandle.invokeBasic(param1ArrayOfObject[0]);
    }
    
    @Hidden
    static Object invoke_LL_L(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(2, param1MethodHandle, param1ArrayOfObject);
      return param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1]);
    }
    
    @Hidden
    static Object invoke_LLL_L(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(3, param1MethodHandle, param1ArrayOfObject);
      return param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1], param1ArrayOfObject[2]);
    }
    
    @Hidden
    static Object invoke_LLLL_L(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(4, param1MethodHandle, param1ArrayOfObject);
      return param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1], param1ArrayOfObject[2], param1ArrayOfObject[3]);
    }
    
    @Hidden
    static Object invoke_LLLLL_L(MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) throws Throwable {
      assert arityCheck(5, param1MethodHandle, param1ArrayOfObject);
      return param1MethodHandle.invokeBasic(param1ArrayOfObject[0], param1ArrayOfObject[1], param1ArrayOfObject[2], param1ArrayOfObject[3], param1ArrayOfObject[4]);
    }
    
    private static boolean arityCheck(int param1Int, MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) { return arityCheck(param1Int, Object.class, param1MethodHandle, param1ArrayOfObject); }
    
    private static boolean arityCheck(int param1Int, Class<?> param1Class, MethodHandle param1MethodHandle, Object[] param1ArrayOfObject) {
      assert param1ArrayOfObject.length == param1Int : Arrays.asList(new Integer[] { null, (new Integer[2][false] = Integer.valueOf(param1ArrayOfObject.length)).valueOf(param1Int) });
      assert param1MethodHandle.type().basicType() == MethodType.genericMethodType(param1Int).changeReturnType(param1Class) : Arrays.asList(new Object[] { param1MethodHandle, param1Class, Integer.valueOf(param1Int) });
      MemberName memberName = param1MethodHandle.internalMemberName();
      if (isInvokeBasic(memberName)) {
        assert param1Int > 0;
        assert param1ArrayOfObject[0] instanceof MethodHandle;
        MethodHandle methodHandle = (MethodHandle)param1ArrayOfObject[0];
        assert methodHandle.type().basicType() == MethodType.genericMethodType(param1Int - true).changeReturnType(param1Class) : Arrays.asList(new Object[] { memberName, methodHandle, param1Class, Integer.valueOf(param1Int) });
      } 
      return true;
    }
    
    private static MethodHandle computeInvoker(MethodTypeForm param1MethodTypeForm) {
      param1MethodTypeForm = param1MethodTypeForm.basicType().form();
      MethodHandle methodHandle1 = param1MethodTypeForm.cachedMethodHandle(1);
      if (methodHandle1 != null)
        return methodHandle1; 
      MemberName memberName = InvokerBytecodeGenerator.generateNamedFunctionInvoker(param1MethodTypeForm);
      methodHandle1 = DirectMethodHandle.make(memberName);
      MethodHandle methodHandle2 = param1MethodTypeForm.cachedMethodHandle(1);
      if (methodHandle2 != null)
        return methodHandle2; 
      if (!methodHandle1.type().equals(INVOKER_METHOD_TYPE))
        throw MethodHandleStatics.newInternalError(methodHandle1.debugString()); 
      return param1MethodTypeForm.setCachedMethodHandle(1, methodHandle1);
    }
    
    @Hidden
    Object invokeWithArguments(Object... param1VarArgs) throws Throwable {
      if (TRACE_INTERPRETER)
        return invokeWithArgumentsTracing(param1VarArgs); 
      assert checkArgumentTypes(param1VarArgs, methodType());
      return invoker().invokeBasic(resolvedHandle(), param1VarArgs);
    }
    
    @Hidden
    Object invokeWithArgumentsTracing(Object[] param1ArrayOfObject) throws Throwable {
      Object object;
      try {
        LambdaForm.traceInterpreter("[ call", this, param1ArrayOfObject);
        if (this.invoker == null) {
          LambdaForm.traceInterpreter("| getInvoker", this);
          invoker();
        } 
        if (this.resolvedHandle == null) {
          LambdaForm.traceInterpreter("| resolve", this);
          resolvedHandle();
        } 
        assert checkArgumentTypes(param1ArrayOfObject, methodType());
        object = invoker().invokeBasic(resolvedHandle(), param1ArrayOfObject);
      } catch (Throwable throwable) {
        LambdaForm.traceInterpreter("] throw =>", throwable);
        throw throwable;
      } 
      LambdaForm.traceInterpreter("] return =>", object);
      return object;
    }
    
    private MethodHandle invoker() { return (this.invoker != null) ? this.invoker : (this.invoker = computeInvoker(methodType().form())); }
    
    private static boolean checkArgumentTypes(Object[] param1ArrayOfObject, MethodType param1MethodType) { return true; }
    
    MethodType methodType() { return (this.resolvedHandle != null) ? this.resolvedHandle.type() : this.member.getInvocationType(); }
    
    MemberName member() {
      assert assertMemberIsConsistent();
      return this.member;
    }
    
    private boolean assertMemberIsConsistent() {
      if (this.resolvedHandle instanceof DirectMethodHandle) {
        MemberName memberName = this.resolvedHandle.internalMemberName();
        assert memberName.equals(this.member);
      } 
      return true;
    }
    
    Class<?> memberDeclaringClassOrNull() { return (this.member == null) ? null : this.member.getDeclaringClass(); }
    
    LambdaForm.BasicType returnType() { return LambdaForm.BasicType.basicType(methodType().returnType()); }
    
    LambdaForm.BasicType parameterType(int param1Int) { return LambdaForm.BasicType.basicType(methodType().parameterType(param1Int)); }
    
    int arity() { return methodType().parameterCount(); }
    
    public String toString() { return (this.member == null) ? String.valueOf(this.resolvedHandle) : (this.member.getDeclaringClass().getSimpleName() + "." + this.member.getName()); }
    
    public boolean isIdentity() { return equals(LambdaForm.identity(returnType())); }
    
    public boolean isConstantZero() { return equals(LambdaForm.constantZero(returnType())); }
    
    public MethodHandleImpl.Intrinsic intrinsicName() { return (this.resolvedHandle == null) ? MethodHandleImpl.Intrinsic.NONE : this.resolvedHandle.intrinsicName(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\LambdaForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */