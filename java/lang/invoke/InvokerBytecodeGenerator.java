package java.lang.invoke;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.InvokerBytecodeGenerator;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodTypeForm;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.reflect.misc.ReflectUtil;

class InvokerBytecodeGenerator {
  private static final String MH = "java/lang/invoke/MethodHandle";
  
  private static final String MHI = "java/lang/invoke/MethodHandleImpl";
  
  private static final String LF = "java/lang/invoke/LambdaForm";
  
  private static final String LFN = "java/lang/invoke/LambdaForm$Name";
  
  private static final String CLS = "java/lang/Class";
  
  private static final String OBJ = "java/lang/Object";
  
  private static final String OBJARY = "[Ljava/lang/Object;";
  
  private static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
  
  private static final String LF_SIG = "Ljava/lang/invoke/LambdaForm;";
  
  private static final String LFN_SIG = "Ljava/lang/invoke/LambdaForm$Name;";
  
  private static final String LL_SIG = "(Ljava/lang/Object;)Ljava/lang/Object;";
  
  private static final String LLV_SIG = "(Ljava/lang/Object;Ljava/lang/Object;)V";
  
  private static final String CLL_SIG = "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;";
  
  private static final String superName = "java/lang/Object";
  
  private final String className;
  
  private final String sourceFile;
  
  private final LambdaForm lambdaForm;
  
  private final String invokerName;
  
  private final MethodType invokerType;
  
  private final int[] localsMap;
  
  private final LambdaForm.BasicType[] localTypes;
  
  private final Class<?>[] localClasses;
  
  private ClassWriter cw;
  
  private MethodVisitor mv;
  
  private static final MemberName.Factory MEMBERNAME_FACTORY = MemberName.getFactory();
  
  private static final Class<?> HOST_CLASS = LambdaForm.class;
  
  private static final HashMap<String, Integer> DUMP_CLASS_FILES_COUNTERS;
  
  private static final File DUMP_CLASS_FILES_DIR;
  
  Map<Object, CpPatch> cpPatches = new HashMap();
  
  int cph = 0;
  
  private static Class<?>[] STATICALLY_INVOCABLE_PACKAGES;
  
  private InvokerBytecodeGenerator(LambdaForm paramLambdaForm, int paramInt, String paramString1, String paramString2, MethodType paramMethodType) {
    if (paramString2.contains(".")) {
      int i = paramString2.indexOf(".");
      paramString1 = paramString2.substring(0, i);
      paramString2 = paramString2.substring(i + 1);
    } 
    if (MethodHandleStatics.DUMP_CLASS_FILES)
      paramString1 = makeDumpableClassName(paramString1); 
    this.className = "java/lang/invoke/LambdaForm$" + paramString1;
    this.sourceFile = "LambdaForm$" + paramString1;
    this.lambdaForm = paramLambdaForm;
    this.invokerName = paramString2;
    this.invokerType = paramMethodType;
    this.localsMap = new int[paramInt + 1];
    this.localTypes = new LambdaForm.BasicType[paramInt + 1];
    this.localClasses = new Class[paramInt + 1];
  }
  
  private InvokerBytecodeGenerator(String paramString1, String paramString2, MethodType paramMethodType) {
    this(null, paramMethodType.parameterCount(), paramString1, paramString2, paramMethodType);
    this.localTypes[this.localTypes.length - 1] = LambdaForm.BasicType.V_TYPE;
    for (byte b = 0; b < this.localsMap.length; b++) {
      this.localsMap[b] = paramMethodType.parameterSlotCount() - paramMethodType.parameterSlotDepth(b);
      if (b < paramMethodType.parameterCount())
        this.localTypes[b] = LambdaForm.BasicType.basicType(paramMethodType.parameterType(b)); 
    } 
  }
  
  private InvokerBytecodeGenerator(String paramString, LambdaForm paramLambdaForm, MethodType paramMethodType) {
    this(paramLambdaForm, paramLambdaForm.names.length, paramString, paramLambdaForm.debugName, paramMethodType);
    LambdaForm.Name[] arrayOfName = paramLambdaForm.names;
    byte b = 0;
    int i = 0;
    while (b < this.localsMap.length) {
      this.localsMap[b] = i;
      if (b < arrayOfName.length) {
        LambdaForm.BasicType basicType = arrayOfName[b].type();
        i += basicType.basicTypeSlots();
        this.localTypes[b] = basicType;
      } 
      b++;
    } 
  }
  
  static void maybeDump(final String className, final byte[] classFile) {
    if (MethodHandleStatics.DUMP_CLASS_FILES)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              try {
                String str = className;
                File file = new File(DUMP_CLASS_FILES_DIR, str + ".class");
                System.out.println("dump: " + file);
                file.getParentFile().mkdirs();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(classFile);
                fileOutputStream.close();
                return null;
              } catch (IOException iOException) {
                throw MethodHandleStatics.newInternalError(iOException);
              } 
            }
          }); 
  }
  
  private static String makeDumpableClassName(String paramString) {
    Integer integer;
    synchronized (DUMP_CLASS_FILES_COUNTERS) {
      integer = (Integer)DUMP_CLASS_FILES_COUNTERS.get(paramString);
      if (integer == null)
        integer = Integer.valueOf(0); 
      DUMP_CLASS_FILES_COUNTERS.put(paramString, Integer.valueOf(integer.intValue() + 1));
    } 
    String str;
    for (str = integer.toString(); str.length() < 3; str = "0" + str);
    return paramString + str;
  }
  
  String constantPlaceholder(Object paramObject) {
    String str = "CONSTANT_PLACEHOLDER_" + this.cph++;
    if (MethodHandleStatics.DUMP_CLASS_FILES)
      str = str + " <<" + debugString(paramObject) + ">>"; 
    if (this.cpPatches.containsKey(str))
      throw new InternalError("observed CP placeholder twice: " + str); 
    int i = this.cw.newConst(str);
    this.cpPatches.put(str, new CpPatch(i, str, paramObject));
    return str;
  }
  
  Object[] cpPatches(byte[] paramArrayOfByte) {
    int i = getConstantPoolSize(paramArrayOfByte);
    Object[] arrayOfObject = new Object[i];
    for (CpPatch cpPatch : this.cpPatches.values()) {
      if (cpPatch.index >= i)
        throw new InternalError("in cpool[" + i + "]: " + cpPatch + "\n" + Arrays.toString(Arrays.copyOf(paramArrayOfByte, 20))); 
      arrayOfObject[cpPatch.index] = cpPatch.value;
    } 
    return arrayOfObject;
  }
  
  private static String debugString(Object paramObject) {
    if (paramObject instanceof MethodHandle) {
      MethodHandle methodHandle = (MethodHandle)paramObject;
      MemberName memberName = methodHandle.internalMemberName();
      return (memberName != null) ? memberName.toString() : methodHandle.debugString();
    } 
    return paramObject.toString();
  }
  
  private static int getConstantPoolSize(byte[] paramArrayOfByte) { return (paramArrayOfByte[8] & 0xFF) << 8 | paramArrayOfByte[9] & 0xFF; }
  
  private MemberName loadMethod(byte[] paramArrayOfByte) {
    Class clazz = loadAndInitializeInvokerClass(paramArrayOfByte, cpPatches(paramArrayOfByte));
    return resolveInvokerMember(clazz, this.invokerName, this.invokerType);
  }
  
  private static Class<?> loadAndInitializeInvokerClass(byte[] paramArrayOfByte, Object[] paramArrayOfObject) {
    Class clazz = MethodHandleStatics.UNSAFE.defineAnonymousClass(HOST_CLASS, paramArrayOfByte, paramArrayOfObject);
    MethodHandleStatics.UNSAFE.ensureClassInitialized(clazz);
    return clazz;
  }
  
  private static MemberName resolveInvokerMember(Class<?> paramClass, String paramString, MethodType paramMethodType) {
    MemberName memberName = new MemberName(paramClass, paramString, paramMethodType, (byte)6);
    try {
      memberName = MEMBERNAME_FACTORY.resolveOrFail((byte)6, memberName, HOST_CLASS, ReflectiveOperationException.class);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError(reflectiveOperationException);
    } 
    return memberName;
  }
  
  private void classFilePrologue() {
    this.cw = new ClassWriter(3);
    this.cw.visit(52, 48, this.className, null, "java/lang/Object", null);
    this.cw.visitSource(this.sourceFile, null);
    String str = this.invokerType.toMethodDescriptorString();
    this.mv = this.cw.visitMethod(8, this.invokerName, str, null, null);
  }
  
  private void classFileEpilogue() {
    this.mv.visitMaxs(0, 0);
    this.mv.visitEnd();
  }
  
  private void emitConst(Object paramObject) {
    if (paramObject == null) {
      this.mv.visitInsn(1);
      return;
    } 
    if (paramObject instanceof Integer) {
      emitIconstInsn(((Integer)paramObject).intValue());
      return;
    } 
    if (paramObject instanceof Long) {
      long l = ((Long)paramObject).longValue();
      if (l == (short)(int)l) {
        emitIconstInsn((int)l);
        this.mv.visitInsn(133);
        return;
      } 
    } 
    if (paramObject instanceof Float) {
      float f = ((Float)paramObject).floatValue();
      if (f == (short)(int)f) {
        emitIconstInsn((int)f);
        this.mv.visitInsn(134);
        return;
      } 
    } 
    if (paramObject instanceof Double) {
      double d = ((Double)paramObject).doubleValue();
      if (d == (short)(int)d) {
        emitIconstInsn((int)d);
        this.mv.visitInsn(135);
        return;
      } 
    } 
    if (paramObject instanceof Boolean) {
      emitIconstInsn(((Boolean)paramObject).booleanValue() ? 1 : 0);
      return;
    } 
    this.mv.visitLdcInsn(paramObject);
  }
  
  private void emitIconstInsn(int paramInt) {
    byte b;
    switch (paramInt) {
      case 0:
        b = 3;
        break;
      case 1:
        b = 4;
        break;
      case 2:
        b = 5;
        break;
      case 3:
        b = 6;
        break;
      case 4:
        b = 7;
        break;
      case 5:
        b = 8;
        break;
      default:
        if (paramInt == (byte)paramInt) {
          this.mv.visitIntInsn(16, paramInt & 0xFF);
        } else if (paramInt == (short)paramInt) {
          this.mv.visitIntInsn(17, (char)paramInt);
        } else {
          this.mv.visitLdcInsn(Integer.valueOf(paramInt));
        } 
        return;
    } 
    this.mv.visitInsn(b);
  }
  
  private void emitLoadInsn(LambdaForm.BasicType paramBasicType, int paramInt) {
    int i = loadInsnOpcode(paramBasicType);
    this.mv.visitVarInsn(i, this.localsMap[paramInt]);
  }
  
  private int loadInsnOpcode(LambdaForm.BasicType paramBasicType) throws InternalError {
    switch (paramBasicType) {
      case SELECT_ALTERNATIVE:
        return 21;
      case GUARD_WITH_CATCH:
        return 22;
      case NEW_ARRAY:
        return 23;
      case ARRAY_LOAD:
        return 24;
      case ARRAY_STORE:
        return 25;
    } 
    throw new InternalError("unknown type: " + paramBasicType);
  }
  
  private void emitAloadInsn(int paramInt) { emitLoadInsn(LambdaForm.BasicType.L_TYPE, paramInt); }
  
  private void emitStoreInsn(LambdaForm.BasicType paramBasicType, int paramInt) {
    int i = storeInsnOpcode(paramBasicType);
    this.mv.visitVarInsn(i, this.localsMap[paramInt]);
  }
  
  private int storeInsnOpcode(LambdaForm.BasicType paramBasicType) throws InternalError {
    switch (paramBasicType) {
      case SELECT_ALTERNATIVE:
        return 54;
      case GUARD_WITH_CATCH:
        return 55;
      case NEW_ARRAY:
        return 56;
      case ARRAY_LOAD:
        return 57;
      case ARRAY_STORE:
        return 58;
    } 
    throw new InternalError("unknown type: " + paramBasicType);
  }
  
  private void emitAstoreInsn(int paramInt) { emitStoreInsn(LambdaForm.BasicType.L_TYPE, paramInt); }
  
  private byte arrayTypeCode(Wrapper paramWrapper) {
    switch (paramWrapper) {
      case SELECT_ALTERNATIVE:
        return 4;
      case GUARD_WITH_CATCH:
        return 8;
      case NEW_ARRAY:
        return 5;
      case ARRAY_LOAD:
        return 9;
      case ARRAY_STORE:
        return 10;
      case IDENTITY:
        return 11;
      case ZERO:
        return 6;
      case NONE:
        return 7;
      case null:
        return 0;
    } 
    throw new InternalError();
  }
  
  private int arrayInsnOpcode(byte paramByte, int paramInt) throws InternalError {
    byte b;
    assert paramInt == 83 || paramInt == 50;
    switch (paramByte) {
      case 4:
        b = 84;
        return b - 83 + paramInt;
      case 8:
        b = 84;
        return b - 83 + paramInt;
      case 5:
        b = 85;
        return b - 83 + paramInt;
      case 9:
        b = 86;
        return b - 83 + paramInt;
      case 10:
        b = 79;
        return b - 83 + paramInt;
      case 11:
        b = 80;
        return b - 83 + paramInt;
      case 6:
        b = 81;
        return b - 83 + paramInt;
      case 7:
        b = 82;
        return b - 83 + paramInt;
      case 0:
        b = 83;
        return b - 83 + paramInt;
    } 
    throw new InternalError();
  }
  
  private void freeFrameLocal(int paramInt) {
    int i = indexForFrameLocal(paramInt);
    if (i < 0)
      return; 
    LambdaForm.BasicType basicType = this.localTypes[i];
    int j = makeLocalTemp(basicType);
    this.mv.visitVarInsn(loadInsnOpcode(basicType), paramInt);
    this.mv.visitVarInsn(storeInsnOpcode(basicType), j);
    assert this.localsMap[i] == paramInt;
    this.localsMap[i] = j;
    assert indexForFrameLocal(paramInt) < 0;
  }
  
  private int indexForFrameLocal(int paramInt) {
    for (byte b = 0; b < this.localsMap.length; b++) {
      if (this.localsMap[b] == paramInt && this.localTypes[b] != LambdaForm.BasicType.V_TYPE)
        return b; 
    } 
    return -1;
  }
  
  private int makeLocalTemp(LambdaForm.BasicType paramBasicType) throws InternalError {
    int i = this.localsMap[this.localsMap.length - 1];
    this.localsMap[this.localsMap.length - 1] = i + paramBasicType.basicTypeSlots();
    return i;
  }
  
  private void emitBoxing(Wrapper paramWrapper) {
    String str1 = "java/lang/" + paramWrapper.wrapperType().getSimpleName();
    String str2 = "valueOf";
    String str3 = "(" + paramWrapper.basicTypeChar() + ")L" + str1 + ";";
    this.mv.visitMethodInsn(184, str1, str2, str3, false);
  }
  
  private void emitUnboxing(Wrapper paramWrapper) {
    String str1 = "java/lang/" + paramWrapper.wrapperType().getSimpleName();
    String str2 = paramWrapper.primitiveSimpleName() + "Value";
    String str3 = "()" + paramWrapper.basicTypeChar();
    emitReferenceCast(paramWrapper.wrapperType(), null);
    this.mv.visitMethodInsn(182, str1, str2, str3, false);
  }
  
  private void emitImplicitConversion(LambdaForm.BasicType paramBasicType, Class<?> paramClass, Object paramObject) {
    assert LambdaForm.BasicType.basicType(paramClass) == paramBasicType;
    if (paramClass == paramBasicType.basicTypeClass() && paramBasicType != LambdaForm.BasicType.L_TYPE)
      return; 
    switch (paramBasicType) {
      case ARRAY_STORE:
        if (VerifyType.isNullConversion(Object.class, paramClass, false)) {
          if (MethodHandleStatics.PROFILE_LEVEL > 0)
            emitReferenceCast(Object.class, paramObject); 
          return;
        } 
        emitReferenceCast(paramClass, paramObject);
        return;
      case SELECT_ALTERNATIVE:
        if (!VerifyType.isNullConversion(int.class, paramClass, false))
          emitPrimCast(paramBasicType.basicTypeWrapper(), Wrapper.forPrimitiveType(paramClass)); 
        return;
    } 
    throw MethodHandleStatics.newInternalError("bad implicit conversion: tc=" + paramBasicType + ": " + paramClass);
  }
  
  private boolean assertStaticType(Class<?> paramClass, LambdaForm.Name paramName) {
    int i = paramName.index();
    Class clazz = this.localClasses[i];
    if (clazz != null && (clazz == paramClass || paramClass.isAssignableFrom(clazz)))
      return true; 
    if (clazz == null || clazz.isAssignableFrom(paramClass))
      this.localClasses[i] = paramClass; 
    return false;
  }
  
  private void emitReferenceCast(Class<?> paramClass, Object paramObject) {
    LambdaForm.Name name = null;
    if (paramObject instanceof LambdaForm.Name) {
      LambdaForm.Name name1 = (LambdaForm.Name)paramObject;
      if (assertStaticType(paramClass, name1))
        return; 
      if (this.lambdaForm.useCount(name1) > 1)
        name = name1; 
    } 
    if (isStaticallyNameable(paramClass)) {
      String str = getInternalName(paramClass);
      this.mv.visitTypeInsn(192, str);
    } else {
      this.mv.visitLdcInsn(constantPlaceholder(paramClass));
      this.mv.visitTypeInsn(192, "java/lang/Class");
      this.mv.visitInsn(95);
      this.mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "castReference", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;", false);
      if (Object[].class.isAssignableFrom(paramClass)) {
        this.mv.visitTypeInsn(192, "[Ljava/lang/Object;");
      } else if (MethodHandleStatics.PROFILE_LEVEL > 0) {
        this.mv.visitTypeInsn(192, "java/lang/Object");
      } 
    } 
    if (name != null) {
      this.mv.visitInsn(89);
      emitAstoreInsn(name.index());
    } 
  }
  
  private void emitReturnInsn(LambdaForm.BasicType paramBasicType) {
    char c;
    switch (paramBasicType) {
      case SELECT_ALTERNATIVE:
        c = '¬';
        break;
      case GUARD_WITH_CATCH:
        c = '­';
        break;
      case NEW_ARRAY:
        c = '®';
        break;
      case ARRAY_LOAD:
        c = '¯';
        break;
      case ARRAY_STORE:
        c = '°';
        break;
      case IDENTITY:
        c = '±';
        break;
      default:
        throw new InternalError("unknown return type: " + paramBasicType);
    } 
    this.mv.visitInsn(c);
  }
  
  private static String getInternalName(Class<?> paramClass) {
    if (paramClass == Object.class)
      return "java/lang/Object"; 
    if (paramClass == Object[].class)
      return "[Ljava/lang/Object;"; 
    if (paramClass == Class.class)
      return "java/lang/Class"; 
    if (paramClass == MethodHandle.class)
      return "java/lang/invoke/MethodHandle"; 
    assert VerifyAccess.isTypeVisible(paramClass, Object.class) : paramClass.getName();
    return paramClass.getName().replace('.', '/');
  }
  
  static MemberName generateCustomizedCode(LambdaForm paramLambdaForm, MethodType paramMethodType) {
    InvokerBytecodeGenerator invokerBytecodeGenerator = new InvokerBytecodeGenerator("MH", paramLambdaForm, paramMethodType);
    return invokerBytecodeGenerator.loadMethod(invokerBytecodeGenerator.generateCustomizedCodeBytes());
  }
  
  private boolean checkActualReceiver() {
    this.mv.visitInsn(89);
    this.mv.visitVarInsn(25, this.localsMap[0]);
    this.mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "assertSame", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
    return true;
  }
  
  private byte[] generateCustomizedCodeBytes() {
    classFilePrologue();
    this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Compiled;", true);
    if (this.lambdaForm.forceInline) {
      this.mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
    } else {
      this.mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
    } 
    if (this.lambdaForm.customized != null) {
      this.mv.visitLdcInsn(constantPlaceholder(this.lambdaForm.customized));
      this.mv.visitTypeInsn(192, "java/lang/invoke/MethodHandle");
      assert checkActualReceiver();
      this.mv.visitVarInsn(58, this.localsMap[0]);
    } 
    LambdaForm.Name name = null;
    for (int i = this.lambdaForm.arity;; i++) {
      if (i < this.lambdaForm.names.length) {
        Class clazz;
        MemberName memberName;
        LambdaForm.Name name1 = this.lambdaForm.names[i];
        emitStoreResult(name);
        name = name1;
        MethodHandleImpl.Intrinsic intrinsic = name1.function.intrinsicName();
        switch (intrinsic) {
          case SELECT_ALTERNATIVE:
            assert isSelectAlternative(i);
            if (MethodHandleStatics.PROFILE_GWT) {
              assert name1.arguments[0] instanceof LambdaForm.Name && nameRefersTo((LambdaForm.Name)name1.arguments[0], MethodHandleImpl.class, "profileBoolean");
              this.mv.visitAnnotation("Ljava/lang/invoke/InjectedProfile;", true);
            } 
            name = emitSelectAlternative(name1, this.lambdaForm.names[i + 1]);
            i++;
            break;
          case GUARD_WITH_CATCH:
            assert isGuardWithCatch(i);
            name = emitGuardWithCatch(i);
            i += 2;
            break;
          case NEW_ARRAY:
            clazz = name1.function.methodType().returnType();
            if (isStaticallyNameable(clazz)) {
              emitNewArray(name1);
              break;
            } 
          case ARRAY_LOAD:
            emitArrayLoad(name1);
            break;
          case ARRAY_STORE:
            emitArrayStore(name1);
            break;
          case IDENTITY:
            assert name1.arguments.length == 1;
            emitPushArguments(name1);
            break;
          case ZERO:
            assert name1.arguments.length == 0;
            emitConst(name1.type.basicTypeWrapper().zero());
            break;
          case NONE:
            memberName = name1.function.member();
            if (isStaticallyInvocable(memberName)) {
              emitStaticInvoke(memberName, name1);
            } else {
              emitInvoke(name1);
            } 
            i++;
            continue;
          default:
            throw MethodHandleStatics.newInternalError("Unknown intrinsic: " + intrinsic);
        } 
      } else {
        break;
      } 
    } 
    emitReturn(name);
    classFileEpilogue();
    bogusMethod(new Object[] { this.lambdaForm });
    byte[] arrayOfByte = this.cw.toByteArray();
    maybeDump(this.className, arrayOfByte);
    return arrayOfByte;
  }
  
  void emitArrayLoad(LambdaForm.Name paramName) { emitArrayOp(paramName, 50); }
  
  void emitArrayStore(LambdaForm.Name paramName) { emitArrayOp(paramName, 83); }
  
  void emitArrayOp(LambdaForm.Name paramName, int paramInt) {
    assert paramInt == 50 || paramInt == 83;
    Class clazz = paramName.function.methodType().parameterType(0).getComponentType();
    assert clazz != null;
    emitPushArguments(paramName);
    if (clazz.isPrimitive()) {
      Wrapper wrapper = Wrapper.forPrimitiveType(clazz);
      paramInt = arrayInsnOpcode(arrayTypeCode(wrapper), paramInt);
    } 
    this.mv.visitInsn(paramInt);
  }
  
  void emitInvoke(LambdaForm.Name paramName) {
    assert !isLinkerMethodInvoke(paramName);
    MethodHandle methodHandle = paramName.function.resolvedHandle;
    assert methodHandle != null : paramName.exprString();
    this.mv.visitLdcInsn(constantPlaceholder(methodHandle));
    emitReferenceCast(MethodHandle.class, methodHandle);
    emitPushArguments(paramName);
    MethodType methodType = paramName.function.methodType();
    this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", methodType.basicType().toMethodDescriptorString(), false);
  }
  
  static boolean isStaticallyInvocable(LambdaForm.Name paramName) { return isStaticallyInvocable(paramName.function.member()); }
  
  static boolean isStaticallyInvocable(MemberName paramMemberName) {
    if (paramMemberName == null)
      return false; 
    if (paramMemberName.isConstructor())
      return false; 
    Class clazz = paramMemberName.getDeclaringClass();
    if (clazz.isArray() || clazz.isPrimitive())
      return false; 
    if (clazz.isAnonymousClass() || clazz.isLocalClass())
      return false; 
    if (clazz.getClassLoader() != MethodHandle.class.getClassLoader())
      return false; 
    if (ReflectUtil.isVMAnonymousClass(clazz))
      return false; 
    MethodType methodType = paramMemberName.getMethodOrFieldType();
    if (!isStaticallyNameable(methodType.returnType()))
      return false; 
    for (Class clazz1 : methodType.parameterArray()) {
      if (!isStaticallyNameable(clazz1))
        return false; 
    } 
    return (!paramMemberName.isPrivate() && VerifyAccess.isSamePackage(MethodHandle.class, clazz)) ? true : ((paramMemberName.isPublic() && isStaticallyNameable(clazz)));
  }
  
  static boolean isStaticallyNameable(Class<?> paramClass) {
    if (paramClass == Object.class)
      return true; 
    while (paramClass.isArray())
      paramClass = paramClass.getComponentType(); 
    if (paramClass.isPrimitive())
      return true; 
    if (ReflectUtil.isVMAnonymousClass(paramClass))
      return false; 
    if (paramClass.getClassLoader() != Object.class.getClassLoader())
      return false; 
    if (VerifyAccess.isSamePackage(MethodHandle.class, paramClass))
      return true; 
    if (!Modifier.isPublic(paramClass.getModifiers()))
      return false; 
    for (Class clazz : STATICALLY_INVOCABLE_PACKAGES) {
      if (VerifyAccess.isSamePackage(clazz, paramClass))
        return true; 
    } 
    return false;
  }
  
  void emitStaticInvoke(LambdaForm.Name paramName) { emitStaticInvoke(paramName.function.member(), paramName); }
  
  void emitStaticInvoke(MemberName paramMemberName, LambdaForm.Name paramName) {
    assert paramMemberName.equals(paramName.function.member());
    Class clazz = paramMemberName.getDeclaringClass();
    String str1 = getInternalName(clazz);
    String str2 = paramMemberName.getName();
    byte b = paramMemberName.getReferenceKind();
    if (b == 7) {
      assert paramMemberName.canBeStaticallyBound() : paramMemberName;
      b = 5;
    } 
    if (paramMemberName.getDeclaringClass().isInterface() && b == 5)
      b = 9; 
    emitPushArguments(paramName);
    if (paramMemberName.isMethod()) {
      String str = paramMemberName.getMethodType().toMethodDescriptorString();
      this.mv.visitMethodInsn(refKindOpcode(b), str1, str2, str, paramMemberName.getDeclaringClass().isInterface());
    } else {
      String str = MethodType.toFieldDescriptorString(paramMemberName.getFieldType());
      this.mv.visitFieldInsn(refKindOpcode(b), str1, str2, str);
    } 
    if (paramName.type == LambdaForm.BasicType.L_TYPE) {
      Class clazz1 = paramMemberName.getInvocationType().returnType();
      assert !clazz1.isPrimitive();
      if (clazz1 != Object.class && !clazz1.isInterface())
        assertStaticType(clazz1, paramName); 
    } 
  }
  
  void emitNewArray(LambdaForm.Name paramName) {
    Class clazz1 = paramName.function.methodType().returnType();
    if (paramName.arguments.length == 0) {
      Object object;
      try {
        object = paramName.function.resolvedHandle.invoke();
      } catch (Throwable throwable) {
        throw MethodHandleStatics.newInternalError(throwable);
      } 
      assert Array.getLength(object) == 0;
      assert object.getClass() == clazz1;
      this.mv.visitLdcInsn(constantPlaceholder(object));
      emitReferenceCast(clazz1, object);
      return;
    } 
    Class clazz2 = clazz1.getComponentType();
    assert clazz2 != null;
    emitIconstInsn(paramName.arguments.length);
    int i = 83;
    if (!clazz2.isPrimitive()) {
      this.mv.visitTypeInsn(189, getInternalName(clazz2));
    } else {
      byte b1 = arrayTypeCode(Wrapper.forPrimitiveType(clazz2));
      i = arrayInsnOpcode(b1, i);
      this.mv.visitIntInsn(188, b1);
    } 
    for (byte b = 0; b < paramName.arguments.length; b++) {
      this.mv.visitInsn(89);
      emitIconstInsn(b);
      emitPushArgument(paramName, b);
      this.mv.visitInsn(i);
    } 
    assertStaticType(clazz1, paramName);
  }
  
  int refKindOpcode(byte paramByte) {
    switch (paramByte) {
      case 5:
        return 182;
      case 6:
        return 184;
      case 7:
        return 183;
      case 9:
        return 185;
      case 1:
        return 180;
      case 3:
        return 181;
      case 2:
        return 178;
      case 4:
        return 179;
    } 
    throw new InternalError("refKind=" + paramByte);
  }
  
  private boolean memberRefersTo(MemberName paramMemberName, Class<?> paramClass, String paramString) { return (paramMemberName != null && paramMemberName.getDeclaringClass() == paramClass && paramMemberName.getName().equals(paramString)); }
  
  private boolean nameRefersTo(LambdaForm.Name paramName, Class<?> paramClass, String paramString) { return (paramName.function != null && memberRefersTo(paramName.function.member(), paramClass, paramString)); }
  
  private boolean isInvokeBasic(LambdaForm.Name paramName) {
    if (paramName.function == null)
      return false; 
    if (paramName.arguments.length < 1)
      return false; 
    MemberName memberName = paramName.function.member();
    return (memberRefersTo(memberName, MethodHandle.class, "invokeBasic") && !memberName.isPublic() && !memberName.isStatic());
  }
  
  private boolean isLinkerMethodInvoke(LambdaForm.Name paramName) {
    if (paramName.function == null)
      return false; 
    if (paramName.arguments.length < 1)
      return false; 
    MemberName memberName = paramName.function.member();
    return (memberName != null && memberName.getDeclaringClass() == MethodHandle.class && !memberName.isPublic() && memberName.isStatic() && memberName.getName().startsWith("linkTo"));
  }
  
  private boolean isSelectAlternative(int paramInt) {
    if (paramInt + 1 >= this.lambdaForm.names.length)
      return false; 
    LambdaForm.Name name1 = this.lambdaForm.names[paramInt];
    LambdaForm.Name name2 = this.lambdaForm.names[paramInt + 1];
    return (nameRefersTo(name1, MethodHandleImpl.class, "selectAlternative") && isInvokeBasic(name2) && name2.lastUseIndex(name1) == 0 && this.lambdaForm.lastUseIndex(name1) == paramInt + 1);
  }
  
  private boolean isGuardWithCatch(int paramInt) {
    if (paramInt + 2 >= this.lambdaForm.names.length)
      return false; 
    LambdaForm.Name name1 = this.lambdaForm.names[paramInt];
    LambdaForm.Name name2 = this.lambdaForm.names[paramInt + 1];
    LambdaForm.Name name3 = this.lambdaForm.names[paramInt + 2];
    return (nameRefersTo(name2, MethodHandleImpl.class, "guardWithCatch") && isInvokeBasic(name1) && isInvokeBasic(name3) && name2.lastUseIndex(name1) == 3 && this.lambdaForm.lastUseIndex(name1) == paramInt + 1 && name3.lastUseIndex(name2) == 1 && this.lambdaForm.lastUseIndex(name2) == paramInt + 2);
  }
  
  private LambdaForm.Name emitSelectAlternative(LambdaForm.Name paramName1, LambdaForm.Name paramName2) {
    assert isStaticallyInvocable(paramName2);
    LambdaForm.Name name = (LambdaForm.Name)paramName2.arguments[0];
    Label label1 = new Label();
    Label label2 = new Label();
    emitPushArgument(paramName1, 0);
    this.mv.visitJumpInsn(153, label1);
    Class[] arrayOfClass = (Class[])this.localClasses.clone();
    emitPushArgument(paramName1, 1);
    emitAstoreInsn(name.index());
    emitStaticInvoke(paramName2);
    this.mv.visitJumpInsn(167, label2);
    this.mv.visitLabel(label1);
    System.arraycopy(arrayOfClass, 0, this.localClasses, 0, arrayOfClass.length);
    emitPushArgument(paramName1, 2);
    emitAstoreInsn(name.index());
    emitStaticInvoke(paramName2);
    this.mv.visitLabel(label2);
    System.arraycopy(arrayOfClass, 0, this.localClasses, 0, arrayOfClass.length);
    return paramName2;
  }
  
  private LambdaForm.Name emitGuardWithCatch(int paramInt) {
    LambdaForm.Name name1 = this.lambdaForm.names[paramInt];
    LambdaForm.Name name2 = this.lambdaForm.names[paramInt + 1];
    LambdaForm.Name name3 = this.lambdaForm.names[paramInt + 2];
    Label label1 = new Label();
    Label label2 = new Label();
    Label label3 = new Label();
    Label label4 = new Label();
    Class clazz = name3.function.resolvedHandle.type().returnType();
    MethodType methodType1 = name1.function.resolvedHandle.type().dropParameterTypes(0, 1).changeReturnType(clazz);
    this.mv.visitTryCatchBlock(label1, label2, label3, "java/lang/Throwable");
    this.mv.visitLabel(label1);
    emitPushArgument(name2, 0);
    emitPushArguments(name1, 1);
    this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", methodType1.basicType().toMethodDescriptorString(), false);
    this.mv.visitLabel(label2);
    this.mv.visitJumpInsn(167, label4);
    this.mv.visitLabel(label3);
    this.mv.visitInsn(89);
    emitPushArgument(name2, 1);
    this.mv.visitInsn(95);
    this.mv.visitMethodInsn(182, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
    Label label5 = new Label();
    this.mv.visitJumpInsn(153, label5);
    emitPushArgument(name2, 2);
    this.mv.visitInsn(95);
    emitPushArguments(name1, 1);
    MethodType methodType2 = methodType1.insertParameterTypes(0, new Class[] { Throwable.class });
    this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", methodType2.basicType().toMethodDescriptorString(), false);
    this.mv.visitJumpInsn(167, label4);
    this.mv.visitLabel(label5);
    this.mv.visitInsn(191);
    this.mv.visitLabel(label4);
    return name3;
  }
  
  private void emitPushArguments(LambdaForm.Name paramName) { emitPushArguments(paramName, 0); }
  
  private void emitPushArguments(LambdaForm.Name paramName, int paramInt) {
    for (int i = paramInt; i < paramName.arguments.length; i++)
      emitPushArgument(paramName, i); 
  }
  
  private void emitPushArgument(LambdaForm.Name paramName, int paramInt) {
    Object object = paramName.arguments[paramInt];
    Class clazz = paramName.function.methodType().parameterType(paramInt);
    emitPushArgument(clazz, object);
  }
  
  private void emitPushArgument(Class<?> paramClass, Object paramObject) {
    LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(paramClass);
    if (paramObject instanceof LambdaForm.Name) {
      LambdaForm.Name name = (LambdaForm.Name)paramObject;
      emitLoadInsn(name.type, name.index());
      emitImplicitConversion(name.type, paramClass, name);
    } else if ((paramObject == null || paramObject instanceof String) && basicType == LambdaForm.BasicType.L_TYPE) {
      emitConst(paramObject);
    } else if (Wrapper.isWrapperType(paramObject.getClass()) && basicType != LambdaForm.BasicType.L_TYPE) {
      emitConst(paramObject);
    } else {
      this.mv.visitLdcInsn(constantPlaceholder(paramObject));
      emitImplicitConversion(LambdaForm.BasicType.L_TYPE, paramClass, paramObject);
    } 
  }
  
  private void emitStoreResult(LambdaForm.Name paramName) {
    if (paramName != null && paramName.type != LambdaForm.BasicType.V_TYPE)
      emitStoreInsn(paramName.type, paramName.index()); 
  }
  
  private void emitReturn(LambdaForm.Name paramName) {
    Class clazz = this.invokerType.returnType();
    LambdaForm.BasicType basicType;
    assert basicType == (basicType = this.lambdaForm.returnType()).basicType(clazz);
    if (basicType == LambdaForm.BasicType.V_TYPE) {
      this.mv.visitInsn(177);
    } else {
      LambdaForm.Name name = this.lambdaForm.names[this.lambdaForm.result];
      if (name != paramName)
        emitLoadInsn(basicType, this.lambdaForm.result); 
      emitImplicitConversion(basicType, clazz, name);
      emitReturnInsn(basicType);
    } 
  }
  
  private void emitPrimCast(Wrapper paramWrapper1, Wrapper paramWrapper2) {
    if (paramWrapper1 == paramWrapper2)
      return; 
    if (paramWrapper1.isSubwordOrInt()) {
      emitI2X(paramWrapper2);
    } else if (paramWrapper2.isSubwordOrInt()) {
      emitX2I(paramWrapper1);
      if (paramWrapper2.bitWidth() < 32)
        emitI2X(paramWrapper2); 
    } else {
      boolean bool = false;
      switch (paramWrapper1) {
        case IDENTITY:
          switch (paramWrapper2) {
            case ZERO:
              this.mv.visitInsn(137);
              break;
            case NONE:
              this.mv.visitInsn(138);
              break;
          } 
          bool = true;
          break;
        case ZERO:
          switch (paramWrapper2) {
            case IDENTITY:
              this.mv.visitInsn(140);
              break;
            case NONE:
              this.mv.visitInsn(141);
              break;
          } 
          bool = true;
          break;
        case NONE:
          switch (paramWrapper2) {
            case IDENTITY:
              this.mv.visitInsn(143);
              break;
            case ZERO:
              this.mv.visitInsn(144);
              break;
          } 
          bool = true;
          break;
        default:
          bool = true;
          break;
      } 
      if (bool)
        throw new IllegalStateException("unhandled prim cast: " + paramWrapper1 + "2" + paramWrapper2); 
    } 
  }
  
  private void emitI2X(Wrapper paramWrapper) {
    switch (paramWrapper) {
      case GUARD_WITH_CATCH:
        this.mv.visitInsn(145);
      case ARRAY_LOAD:
        this.mv.visitInsn(147);
      case NEW_ARRAY:
        this.mv.visitInsn(146);
      case ARRAY_STORE:
        return;
      case IDENTITY:
        this.mv.visitInsn(133);
      case ZERO:
        this.mv.visitInsn(134);
      case NONE:
        this.mv.visitInsn(135);
      case SELECT_ALTERNATIVE:
        this.mv.visitInsn(4);
        this.mv.visitInsn(126);
    } 
    throw new InternalError("unknown type: " + paramWrapper);
  }
  
  private void emitX2I(Wrapper paramWrapper) {
    switch (paramWrapper) {
      case IDENTITY:
        this.mv.visitInsn(136);
        return;
      case ZERO:
        this.mv.visitInsn(139);
        return;
      case NONE:
        this.mv.visitInsn(142);
        return;
    } 
    throw new InternalError("unknown type: " + paramWrapper);
  }
  
  static MemberName generateLambdaFormInterpreterEntryPoint(String paramString) {
    assert LambdaForm.isValidSignature(paramString);
    String str = "interpret_" + LambdaForm.signatureReturn(paramString).basicTypeChar();
    MethodType methodType = LambdaForm.signatureType(paramString);
    methodType = methodType.changeParameterType(0, MethodHandle.class);
    InvokerBytecodeGenerator invokerBytecodeGenerator = new InvokerBytecodeGenerator("LFI", str, methodType);
    return invokerBytecodeGenerator.loadMethod(invokerBytecodeGenerator.generateLambdaFormInterpreterEntryPointBytes());
  }
  
  private byte[] generateLambdaFormInterpreterEntryPointBytes() {
    classFilePrologue();
    this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    this.mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
    emitIconstInsn(this.invokerType.parameterCount());
    this.mv.visitTypeInsn(189, "java/lang/Object");
    for (byte b = 0; b < this.invokerType.parameterCount(); b++) {
      Class clazz1 = this.invokerType.parameterType(b);
      this.mv.visitInsn(89);
      emitIconstInsn(b);
      emitLoadInsn(LambdaForm.BasicType.basicType(clazz1), b);
      if (clazz1.isPrimitive())
        emitBoxing(Wrapper.forPrimitiveType(clazz1)); 
      this.mv.visitInsn(83);
    } 
    emitAloadInsn(0);
    this.mv.visitFieldInsn(180, "java/lang/invoke/MethodHandle", "form", "Ljava/lang/invoke/LambdaForm;");
    this.mv.visitInsn(95);
    this.mv.visitMethodInsn(182, "java/lang/invoke/LambdaForm", "interpretWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
    Class clazz = this.invokerType.returnType();
    if (clazz.isPrimitive() && clazz != void.class)
      emitUnboxing(Wrapper.forPrimitiveType(clazz)); 
    emitReturnInsn(LambdaForm.BasicType.basicType(clazz));
    classFileEpilogue();
    bogusMethod(new Object[] { this.invokerType });
    byte[] arrayOfByte = this.cw.toByteArray();
    maybeDump(this.className, arrayOfByte);
    return arrayOfByte;
  }
  
  static MemberName generateNamedFunctionInvoker(MethodTypeForm paramMethodTypeForm) {
    MethodType methodType = LambdaForm.NamedFunction.INVOKER_METHOD_TYPE;
    String str = "invoke_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(paramMethodTypeForm.erasedType()));
    InvokerBytecodeGenerator invokerBytecodeGenerator = new InvokerBytecodeGenerator("NFI", str, methodType);
    return invokerBytecodeGenerator.loadMethod(invokerBytecodeGenerator.generateNamedFunctionInvokerImpl(paramMethodTypeForm));
  }
  
  private byte[] generateNamedFunctionInvokerImpl(MethodTypeForm paramMethodTypeForm) {
    MethodType methodType = paramMethodTypeForm.erasedType();
    classFilePrologue();
    this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    this.mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
    emitAloadInsn(0);
    for (byte b = 0; b < methodType.parameterCount(); b++) {
      emitAloadInsn(1);
      emitIconstInsn(b);
      this.mv.visitInsn(50);
      Class clazz1 = methodType.parameterType(b);
      if (clazz1.isPrimitive()) {
        Class clazz2 = methodType.basicType().wrap().parameterType(b);
        Wrapper wrapper1 = Wrapper.forBasicType(clazz1);
        Wrapper wrapper2 = wrapper1.isSubwordOrInt() ? Wrapper.INT : wrapper1;
        emitUnboxing(wrapper2);
        emitPrimCast(wrapper2, wrapper1);
      } 
    } 
    String str = methodType.basicType().toMethodDescriptorString();
    this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", str, false);
    Class clazz = methodType.returnType();
    if (clazz != void.class && clazz.isPrimitive()) {
      Wrapper wrapper1 = Wrapper.forBasicType(clazz);
      Wrapper wrapper2 = wrapper1.isSubwordOrInt() ? Wrapper.INT : wrapper1;
      emitPrimCast(wrapper1, wrapper2);
      emitBoxing(wrapper2);
    } 
    if (clazz == void.class)
      this.mv.visitInsn(1); 
    emitReturnInsn(LambdaForm.BasicType.L_TYPE);
    classFileEpilogue();
    bogusMethod(new Object[] { methodType });
    byte[] arrayOfByte = this.cw.toByteArray();
    maybeDump(this.className, arrayOfByte);
    return arrayOfByte;
  }
  
  private void bogusMethod(Object... paramVarArgs) {
    if (MethodHandleStatics.DUMP_CLASS_FILES) {
      this.mv = this.cw.visitMethod(8, "dummy", "()V", null, null);
      for (Object object : paramVarArgs) {
        this.mv.visitLdcInsn(object.toString());
        this.mv.visitInsn(87);
      } 
      this.mv.visitInsn(177);
      this.mv.visitMaxs(0, 0);
      this.mv.visitEnd();
    } 
  }
  
  static  {
    if (MethodHandleStatics.DUMP_CLASS_FILES) {
      DUMP_CLASS_FILES_COUNTERS = new HashMap();
      try {
        File file = new File("DUMP_CLASS_FILES");
        if (!file.exists())
          file.mkdirs(); 
        DUMP_CLASS_FILES_DIR = file;
        System.out.println("Dumping class files to " + DUMP_CLASS_FILES_DIR + "/...");
      } catch (Exception exception) {
        throw MethodHandleStatics.newInternalError(exception);
      } 
    } else {
      DUMP_CLASS_FILES_COUNTERS = null;
      DUMP_CLASS_FILES_DIR = null;
    } 
    STATICALLY_INVOCABLE_PACKAGES = new Class[] { Object.class, Arrays.class, sun.misc.Unsafe.class };
  }
  
  class CpPatch {
    final int index;
    
    final String placeholder;
    
    final Object value;
    
    CpPatch(int param1Int, String param1String, Object param1Object) {
      this.index = param1Int;
      this.placeholder = param1String;
      this.value = param1Object;
    }
    
    public String toString() { return "CpPatch/index=" + this.index + ",placeholder=" + this.placeholder + ",value=" + this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\InvokerBytecodeGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */