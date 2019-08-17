package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;

class MethodAccessorGenerator extends AccessorGenerator {
  private static final short NUM_BASE_CPOOL_ENTRIES = 12;
  
  private static final short NUM_METHODS = 2;
  
  private static final short NUM_SERIALIZATION_CPOOL_ENTRIES = 2;
  
  private Class<?> declaringClass;
  
  private Class<?>[] parameterTypes;
  
  private Class<?> returnType;
  
  private boolean isConstructor;
  
  private boolean forSerialization;
  
  private short targetMethodRef;
  
  private short invokeIdx;
  
  private short invokeDescriptorIdx;
  
  private short nonPrimitiveParametersBaseIdx;
  
  public MethodAccessor generateMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt) { return (MethodAccessor)generate(paramClass1, paramString, paramArrayOfClass1, paramClass2, paramArrayOfClass2, paramInt, false, false, null); }
  
  public ConstructorAccessor generateConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt) { return (ConstructorAccessor)generate(paramClass, "<init>", paramArrayOfClass1, void.class, paramArrayOfClass2, paramInt, true, false, null); }
  
  public SerializationConstructorAccessorImpl generateSerializationConstructor(Class<?> paramClass1, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt, Class<?> paramClass2) { return (SerializationConstructorAccessorImpl)generate(paramClass1, "<init>", paramArrayOfClass1, void.class, paramArrayOfClass2, paramInt, true, true, paramClass2); }
  
  private MagicAccessorImpl generate(final Class<?> declaringClass, String paramString, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt, boolean paramBoolean1, boolean paramBoolean2, Class<?> paramClass3) {
    ByteVector byteVector = ByteVectorFactory.create();
    this.asm = new ClassFileAssembler(byteVector);
    this.declaringClass = paramClass1;
    this.parameterTypes = paramArrayOfClass1;
    this.returnType = paramClass2;
    this.modifiers = paramInt;
    this.isConstructor = paramBoolean1;
    this.forSerialization = paramBoolean2;
    this.asm.emitMagicAndVersion();
    short s1 = 42;
    boolean bool = usesPrimitiveTypes();
    if (bool)
      s1 = (short)(s1 + 72); 
    if (paramBoolean2)
      s1 = (short)(s1 + 2); 
    s1 = (short)(s1 + (short)(2 * numNonPrimitiveParameterTypes()));
    this.asm.emitShort(add(s1, (short)1));
    final String generatedName = generateName(paramBoolean1, paramBoolean2);
    this.asm.emitConstantPoolUTF8(str);
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.thisClass = this.asm.cpi();
    if (paramBoolean1) {
      if (paramBoolean2) {
        this.asm.emitConstantPoolUTF8("sun/reflect/SerializationConstructorAccessorImpl");
      } else {
        this.asm.emitConstantPoolUTF8("sun/reflect/ConstructorAccessorImpl");
      } 
    } else {
      this.asm.emitConstantPoolUTF8("sun/reflect/MethodAccessorImpl");
    } 
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.superClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8(getClassName(paramClass1, false));
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.targetClass = this.asm.cpi();
    short s2 = 0;
    if (paramBoolean2) {
      this.asm.emitConstantPoolUTF8(getClassName(paramClass3, false));
      this.asm.emitConstantPoolClass(this.asm.cpi());
      s2 = this.asm.cpi();
    } 
    this.asm.emitConstantPoolUTF8(paramString);
    this.asm.emitConstantPoolUTF8(buildInternalSignature());
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    if (isInterface()) {
      this.asm.emitConstantPoolInterfaceMethodref(this.targetClass, this.asm.cpi());
    } else if (paramBoolean2) {
      this.asm.emitConstantPoolMethodref(s2, this.asm.cpi());
    } else {
      this.asm.emitConstantPoolMethodref(this.targetClass, this.asm.cpi());
    } 
    this.targetMethodRef = this.asm.cpi();
    if (paramBoolean1) {
      this.asm.emitConstantPoolUTF8("newInstance");
    } else {
      this.asm.emitConstantPoolUTF8("invoke");
    } 
    this.invokeIdx = this.asm.cpi();
    if (paramBoolean1) {
      this.asm.emitConstantPoolUTF8("([Ljava/lang/Object;)Ljava/lang/Object;");
    } else {
      this.asm.emitConstantPoolUTF8("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
    } 
    this.invokeDescriptorIdx = this.asm.cpi();
    this.nonPrimitiveParametersBaseIdx = add(this.asm.cpi(), (short)2);
    for (byte b = 0; b < paramArrayOfClass1.length; b++) {
      Class<?> clazz = paramArrayOfClass1[b];
      if (!isPrimitive(clazz)) {
        this.asm.emitConstantPoolUTF8(getClassName(clazz, false));
        this.asm.emitConstantPoolClass(this.asm.cpi());
      } 
    } 
    emitCommonConstantPoolEntries();
    if (bool)
      emitBoxingContantPoolEntries(); 
    if (this.asm.cpi() != s1)
      throw new InternalError("Adjust this code (cpi = " + this.asm.cpi() + ", numCPEntries = " + s1 + ")"); 
    this.asm.emitShort((short)1);
    this.asm.emitShort(this.thisClass);
    this.asm.emitShort(this.superClass);
    this.asm.emitShort((short)0);
    this.asm.emitShort((short)0);
    this.asm.emitShort((short)2);
    emitConstructor();
    emitInvoke();
    this.asm.emitShort((short)0);
    byteVector.trim();
    final byte[] bytes = byteVector.getData();
    return (MagicAccessorImpl)AccessController.doPrivileged(new PrivilegedAction<MagicAccessorImpl>() {
          public MagicAccessorImpl run() {
            try {
              return (MagicAccessorImpl)ClassDefiner.defineClass(generatedName, bytes, 0, bytes.length, declaringClass.getClassLoader()).newInstance();
            } catch (InstantiationException|IllegalAccessException instantiationException) {
              throw new InternalError(instantiationException);
            } 
          }
        });
  }
  
  private void emitInvoke() {
    if (this.parameterTypes.length > 65535)
      throw new InternalError("Can't handle more than 65535 parameters"); 
    ClassFileAssembler classFileAssembler1 = new ClassFileAssembler();
    if (this.isConstructor) {
      classFileAssembler1.setMaxLocals(2);
    } else {
      classFileAssembler1.setMaxLocals(3);
    } 
    short s1 = 0;
    if (this.isConstructor) {
      classFileAssembler1.opc_new(this.targetClass);
      classFileAssembler1.opc_dup();
    } else {
      if (isPrimitive(this.returnType)) {
        classFileAssembler1.opc_new(indexForPrimitiveType(this.returnType));
        classFileAssembler1.opc_dup();
      } 
      if (!isStatic()) {
        classFileAssembler1.opc_aload_1();
        Label label = new Label();
        classFileAssembler1.opc_ifnonnull(label);
        classFileAssembler1.opc_new(this.nullPointerClass);
        classFileAssembler1.opc_dup();
        classFileAssembler1.opc_invokespecial(this.nullPointerCtorIdx, 0, 0);
        classFileAssembler1.opc_athrow();
        label.bind();
        s1 = classFileAssembler1.getLength();
        classFileAssembler1.opc_aload_1();
        classFileAssembler1.opc_checkcast(this.targetClass);
      } 
    } 
    Label label1 = new Label();
    if (this.parameterTypes.length == 0) {
      if (this.isConstructor) {
        classFileAssembler1.opc_aload_1();
      } else {
        classFileAssembler1.opc_aload_2();
      } 
      classFileAssembler1.opc_ifnull(label1);
    } 
    if (this.isConstructor) {
      classFileAssembler1.opc_aload_1();
    } else {
      classFileAssembler1.opc_aload_2();
    } 
    classFileAssembler1.opc_arraylength();
    classFileAssembler1.opc_sipush((short)this.parameterTypes.length);
    classFileAssembler1.opc_if_icmpeq(label1);
    classFileAssembler1.opc_new(this.illegalArgumentClass);
    classFileAssembler1.opc_dup();
    classFileAssembler1.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
    classFileAssembler1.opc_athrow();
    label1.bind();
    short s2 = this.nonPrimitiveParametersBaseIdx;
    Label label2 = null;
    byte b = 1;
    short s3;
    for (s3 = 0; s3 < this.parameterTypes.length; s3++) {
      Class clazz = this.parameterTypes[s3];
      b = (byte)(b + (byte)typeSizeInStackSlots(clazz));
      if (label2 != null) {
        label2.bind();
        label2 = null;
      } 
      if (this.isConstructor) {
        classFileAssembler1.opc_aload_1();
      } else {
        classFileAssembler1.opc_aload_2();
      } 
      classFileAssembler1.opc_sipush((short)s3);
      classFileAssembler1.opc_aaload();
      if (isPrimitive(clazz)) {
        if (this.isConstructor) {
          classFileAssembler1.opc_astore_2();
        } else {
          classFileAssembler1.opc_astore_3();
        } 
        Label label = null;
        label2 = new Label();
        for (byte b1 = 0; b1 < primitiveTypes.length; b1++) {
          Class clazz1 = primitiveTypes[b1];
          if (canWidenTo(clazz1, clazz)) {
            if (label != null)
              label.bind(); 
            if (this.isConstructor) {
              classFileAssembler1.opc_aload_2();
            } else {
              classFileAssembler1.opc_aload_3();
            } 
            classFileAssembler1.opc_instanceof(indexForPrimitiveType(clazz1));
            label = new Label();
            classFileAssembler1.opc_ifeq(label);
            if (this.isConstructor) {
              classFileAssembler1.opc_aload_2();
            } else {
              classFileAssembler1.opc_aload_3();
            } 
            classFileAssembler1.opc_checkcast(indexForPrimitiveType(clazz1));
            classFileAssembler1.opc_invokevirtual(unboxingMethodForPrimitiveType(clazz1), 0, typeSizeInStackSlots(clazz1));
            emitWideningBytecodeForPrimitiveConversion(classFileAssembler1, clazz1, clazz);
            classFileAssembler1.opc_goto(label2);
          } 
        } 
        if (label == null)
          throw new InternalError("Must have found at least identity conversion"); 
        label.bind();
        classFileAssembler1.opc_new(this.illegalArgumentClass);
        classFileAssembler1.opc_dup();
        classFileAssembler1.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
        classFileAssembler1.opc_athrow();
      } else {
        classFileAssembler1.opc_checkcast(s2);
        s2 = add(s2, (short)2);
      } 
    } 
    if (label2 != null)
      label2.bind(); 
    s3 = classFileAssembler1.getLength();
    if (this.isConstructor) {
      classFileAssembler1.opc_invokespecial(this.targetMethodRef, b, 0);
    } else if (isStatic()) {
      classFileAssembler1.opc_invokestatic(this.targetMethodRef, b, typeSizeInStackSlots(this.returnType));
    } else if (isInterface()) {
      if (isPrivate()) {
        classFileAssembler1.opc_invokespecial(this.targetMethodRef, b, 0);
      } else {
        classFileAssembler1.opc_invokeinterface(this.targetMethodRef, b, b, typeSizeInStackSlots(this.returnType));
      } 
    } else {
      classFileAssembler1.opc_invokevirtual(this.targetMethodRef, b, typeSizeInStackSlots(this.returnType));
    } 
    short s4 = classFileAssembler1.getLength();
    if (!this.isConstructor)
      if (isPrimitive(this.returnType)) {
        classFileAssembler1.opc_invokespecial(ctorIndexForPrimitiveType(this.returnType), typeSizeInStackSlots(this.returnType), 0);
      } else if (this.returnType == void.class) {
        classFileAssembler1.opc_aconst_null();
      }  
    classFileAssembler1.opc_areturn();
    short s5 = classFileAssembler1.getLength();
    classFileAssembler1.setStack(1);
    classFileAssembler1.opc_invokespecial(this.toStringIdx, 0, 1);
    classFileAssembler1.opc_new(this.illegalArgumentClass);
    classFileAssembler1.opc_dup_x1();
    classFileAssembler1.opc_swap();
    classFileAssembler1.opc_invokespecial(this.illegalArgumentStringCtorIdx, 1, 0);
    classFileAssembler1.opc_athrow();
    short s6 = classFileAssembler1.getLength();
    classFileAssembler1.setStack(1);
    classFileAssembler1.opc_new(this.invocationTargetClass);
    classFileAssembler1.opc_dup_x1();
    classFileAssembler1.opc_swap();
    classFileAssembler1.opc_invokespecial(this.invocationTargetCtorIdx, 1, 0);
    classFileAssembler1.opc_athrow();
    ClassFileAssembler classFileAssembler2 = new ClassFileAssembler();
    classFileAssembler2.emitShort(s1);
    classFileAssembler2.emitShort(s3);
    classFileAssembler2.emitShort(s5);
    classFileAssembler2.emitShort(this.classCastClass);
    classFileAssembler2.emitShort(s1);
    classFileAssembler2.emitShort(s3);
    classFileAssembler2.emitShort(s5);
    classFileAssembler2.emitShort(this.nullPointerClass);
    classFileAssembler2.emitShort(s3);
    classFileAssembler2.emitShort(s4);
    classFileAssembler2.emitShort(s6);
    classFileAssembler2.emitShort(this.throwableClass);
    emitMethod(this.invokeIdx, classFileAssembler1.getMaxLocals(), classFileAssembler1, classFileAssembler2, new short[] { this.invocationTargetClass });
  }
  
  private boolean usesPrimitiveTypes() {
    if (this.returnType.isPrimitive())
      return true; 
    for (byte b = 0; b < this.parameterTypes.length; b++) {
      if (this.parameterTypes[b].isPrimitive())
        return true; 
    } 
    return false;
  }
  
  private int numNonPrimitiveParameterTypes() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.parameterTypes.length; b2++) {
      if (!this.parameterTypes[b2].isPrimitive())
        b1++; 
    } 
    return b1;
  }
  
  private boolean isInterface() { return this.declaringClass.isInterface(); }
  
  private String buildInternalSignature() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    for (byte b = 0; b < this.parameterTypes.length; b++)
      stringBuffer.append(getClassName(this.parameterTypes[b], true)); 
    stringBuffer.append(")");
    stringBuffer.append(getClassName(this.returnType, true));
    return stringBuffer.toString();
  }
  
  private static String generateName(boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1) {
      if (paramBoolean2) {
        int k = ++serializationConstructorSymnum;
        return "sun/reflect/GeneratedSerializationConstructorAccessor" + k;
      } 
      int j = ++constructorSymnum;
      return "sun/reflect/GeneratedConstructorAccessor" + j;
    } 
    int i = ++methodSymnum;
    return "sun/reflect/GeneratedMethodAccessor" + i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\MethodAccessorGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */