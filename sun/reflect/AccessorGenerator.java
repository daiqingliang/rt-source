package sun.reflect;

import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

class AccessorGenerator implements ClassFileConstants {
  static final Unsafe unsafe = Unsafe.getUnsafe();
  
  protected static final short S0 = 0;
  
  protected static final short S1 = 1;
  
  protected static final short S2 = 2;
  
  protected static final short S3 = 3;
  
  protected static final short S4 = 4;
  
  protected static final short S5 = 5;
  
  protected static final short S6 = 6;
  
  protected ClassFileAssembler asm;
  
  protected int modifiers;
  
  protected short thisClass;
  
  protected short superClass;
  
  protected short targetClass;
  
  protected short throwableClass;
  
  protected short classCastClass;
  
  protected short nullPointerClass;
  
  protected short illegalArgumentClass;
  
  protected short invocationTargetClass;
  
  protected short initIdx;
  
  protected short initNameAndTypeIdx;
  
  protected short initStringNameAndTypeIdx;
  
  protected short nullPointerCtorIdx;
  
  protected short illegalArgumentCtorIdx;
  
  protected short illegalArgumentStringCtorIdx;
  
  protected short invocationTargetCtorIdx;
  
  protected short superCtorIdx;
  
  protected short objectClass;
  
  protected short toStringIdx;
  
  protected short codeIdx;
  
  protected short exceptionsIdx;
  
  protected short booleanIdx;
  
  protected short booleanCtorIdx;
  
  protected short booleanUnboxIdx;
  
  protected short byteIdx;
  
  protected short byteCtorIdx;
  
  protected short byteUnboxIdx;
  
  protected short characterIdx;
  
  protected short characterCtorIdx;
  
  protected short characterUnboxIdx;
  
  protected short doubleIdx;
  
  protected short doubleCtorIdx;
  
  protected short doubleUnboxIdx;
  
  protected short floatIdx;
  
  protected short floatCtorIdx;
  
  protected short floatUnboxIdx;
  
  protected short integerIdx;
  
  protected short integerCtorIdx;
  
  protected short integerUnboxIdx;
  
  protected short longIdx;
  
  protected short longCtorIdx;
  
  protected short longUnboxIdx;
  
  protected short shortIdx;
  
  protected short shortCtorIdx;
  
  protected short shortUnboxIdx;
  
  protected final short NUM_COMMON_CPOOL_ENTRIES = 30;
  
  protected final short NUM_BOXING_CPOOL_ENTRIES = 72;
  
  protected static final Class<?>[] primitiveTypes = { boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class };
  
  private ClassFileAssembler illegalArgumentCodeBuffer;
  
  protected void emitCommonConstantPoolEntries() {
    this.asm.emitConstantPoolUTF8("java/lang/Throwable");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.throwableClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/ClassCastException");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.classCastClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/NullPointerException");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.nullPointerClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/IllegalArgumentException");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.illegalArgumentClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/reflect/InvocationTargetException");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.invocationTargetClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("<init>");
    this.initIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("()V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.initNameAndTypeIdx = this.asm.cpi();
    this.asm.emitConstantPoolMethodref(this.nullPointerClass, this.initNameAndTypeIdx);
    this.nullPointerCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initNameAndTypeIdx);
    this.illegalArgumentCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(Ljava/lang/String;)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.initStringNameAndTypeIdx = this.asm.cpi();
    this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initStringNameAndTypeIdx);
    this.illegalArgumentStringCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(Ljava/lang/Throwable;)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(this.invocationTargetClass, this.asm.cpi());
    this.invocationTargetCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolMethodref(this.superClass, this.initNameAndTypeIdx);
    this.superCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Object");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.objectClass = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("toString");
    this.asm.emitConstantPoolUTF8("()Ljava/lang/String;");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(this.objectClass, this.asm.cpi());
    this.toStringIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("Code");
    this.codeIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("Exceptions");
    this.exceptionsIdx = this.asm.cpi();
  }
  
  protected void emitBoxingContantPoolEntries() {
    this.asm.emitConstantPoolUTF8("java/lang/Boolean");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.booleanIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(Z)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.booleanCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("booleanValue");
    this.asm.emitConstantPoolUTF8("()Z");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.booleanUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Byte");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.byteIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(B)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.byteCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("byteValue");
    this.asm.emitConstantPoolUTF8("()B");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.byteUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Character");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.characterIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(C)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.characterCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("charValue");
    this.asm.emitConstantPoolUTF8("()C");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.characterUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Double");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.doubleIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(D)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.doubleCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("doubleValue");
    this.asm.emitConstantPoolUTF8("()D");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.doubleUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Float");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.floatIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(F)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.floatCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("floatValue");
    this.asm.emitConstantPoolUTF8("()F");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.floatUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Integer");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.integerIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(I)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.integerCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("intValue");
    this.asm.emitConstantPoolUTF8("()I");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.integerUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Long");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.longIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(J)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.longCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("longValue");
    this.asm.emitConstantPoolUTF8("()J");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.longUnboxIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("java/lang/Short");
    this.asm.emitConstantPoolClass(this.asm.cpi());
    this.shortIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("(S)V");
    this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
    this.shortCtorIdx = this.asm.cpi();
    this.asm.emitConstantPoolUTF8("shortValue");
    this.asm.emitConstantPoolUTF8("()S");
    this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
    this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
    this.shortUnboxIdx = this.asm.cpi();
  }
  
  protected static short add(short paramShort1, short paramShort2) { return (short)(paramShort1 + paramShort2); }
  
  protected static short sub(short paramShort1, short paramShort2) { return (short)(paramShort1 - paramShort2); }
  
  protected boolean isStatic() { return Modifier.isStatic(this.modifiers); }
  
  protected boolean isPrivate() { return Modifier.isPrivate(this.modifiers); }
  
  protected static String getClassName(Class<?> paramClass, boolean paramBoolean) {
    if (paramClass.isPrimitive()) {
      if (paramClass == boolean.class)
        return "Z"; 
      if (paramClass == byte.class)
        return "B"; 
      if (paramClass == char.class)
        return "C"; 
      if (paramClass == double.class)
        return "D"; 
      if (paramClass == float.class)
        return "F"; 
      if (paramClass == int.class)
        return "I"; 
      if (paramClass == long.class)
        return "J"; 
      if (paramClass == short.class)
        return "S"; 
      if (paramClass == void.class)
        return "V"; 
      throw new InternalError("Should have found primitive type");
    } 
    return paramClass.isArray() ? ("[" + getClassName(paramClass.getComponentType(), true)) : (paramBoolean ? internalize("L" + paramClass.getName() + ";") : internalize(paramClass.getName()));
  }
  
  private static String internalize(String paramString) { return paramString.replace('.', '/'); }
  
  protected void emitConstructor() {
    ClassFileAssembler classFileAssembler = new ClassFileAssembler();
    classFileAssembler.setMaxLocals(1);
    classFileAssembler.opc_aload_0();
    classFileAssembler.opc_invokespecial(this.superCtorIdx, 0, 0);
    classFileAssembler.opc_return();
    emitMethod(this.initIdx, classFileAssembler.getMaxLocals(), classFileAssembler, null, null);
  }
  
  protected void emitMethod(short paramShort, int paramInt, ClassFileAssembler paramClassFileAssembler1, ClassFileAssembler paramClassFileAssembler2, short[] paramArrayOfShort) {
    short s1 = paramClassFileAssembler1.getLength();
    short s = 0;
    if (paramClassFileAssembler2 != null) {
      s = paramClassFileAssembler2.getLength();
      if (s % 8 != 0)
        throw new IllegalArgumentException("Illegal exception table"); 
    } 
    short s2 = 12 + s1 + s;
    s /= 8;
    this.asm.emitShort((short)1);
    this.asm.emitShort(paramShort);
    this.asm.emitShort(add(paramShort, (short)1));
    if (paramArrayOfShort == null) {
      this.asm.emitShort((short)1);
    } else {
      this.asm.emitShort((short)2);
    } 
    this.asm.emitShort(this.codeIdx);
    this.asm.emitInt(s2);
    this.asm.emitShort(paramClassFileAssembler1.getMaxStack());
    this.asm.emitShort((short)Math.max(paramInt, paramClassFileAssembler1.getMaxLocals()));
    this.asm.emitInt(s1);
    this.asm.append(paramClassFileAssembler1);
    this.asm.emitShort((short)s);
    if (paramClassFileAssembler2 != null)
      this.asm.append(paramClassFileAssembler2); 
    this.asm.emitShort((short)0);
    if (paramArrayOfShort != null) {
      this.asm.emitShort(this.exceptionsIdx);
      this.asm.emitInt(2 + 2 * paramArrayOfShort.length);
      this.asm.emitShort((short)paramArrayOfShort.length);
      for (byte b = 0; b < paramArrayOfShort.length; b++)
        this.asm.emitShort(paramArrayOfShort[b]); 
    } 
  }
  
  protected short indexForPrimitiveType(Class<?> paramClass) {
    if (paramClass == boolean.class)
      return this.booleanIdx; 
    if (paramClass == byte.class)
      return this.byteIdx; 
    if (paramClass == char.class)
      return this.characterIdx; 
    if (paramClass == double.class)
      return this.doubleIdx; 
    if (paramClass == float.class)
      return this.floatIdx; 
    if (paramClass == int.class)
      return this.integerIdx; 
    if (paramClass == long.class)
      return this.longIdx; 
    if (paramClass == short.class)
      return this.shortIdx; 
    throw new InternalError("Should have found primitive type");
  }
  
  protected short ctorIndexForPrimitiveType(Class<?> paramClass) {
    if (paramClass == boolean.class)
      return this.booleanCtorIdx; 
    if (paramClass == byte.class)
      return this.byteCtorIdx; 
    if (paramClass == char.class)
      return this.characterCtorIdx; 
    if (paramClass == double.class)
      return this.doubleCtorIdx; 
    if (paramClass == float.class)
      return this.floatCtorIdx; 
    if (paramClass == int.class)
      return this.integerCtorIdx; 
    if (paramClass == long.class)
      return this.longCtorIdx; 
    if (paramClass == short.class)
      return this.shortCtorIdx; 
    throw new InternalError("Should have found primitive type");
  }
  
  protected static boolean canWidenTo(Class<?> paramClass1, Class<?> paramClass2) {
    if (!paramClass1.isPrimitive())
      return false; 
    if (paramClass1 == boolean.class) {
      if (paramClass2 == boolean.class)
        return true; 
    } else if (paramClass1 == byte.class) {
      if (paramClass2 == byte.class || paramClass2 == short.class || paramClass2 == int.class || paramClass2 == long.class || paramClass2 == float.class || paramClass2 == double.class)
        return true; 
    } else if (paramClass1 == short.class) {
      if (paramClass2 == short.class || paramClass2 == int.class || paramClass2 == long.class || paramClass2 == float.class || paramClass2 == double.class)
        return true; 
    } else if (paramClass1 == char.class) {
      if (paramClass2 == char.class || paramClass2 == int.class || paramClass2 == long.class || paramClass2 == float.class || paramClass2 == double.class)
        return true; 
    } else if (paramClass1 == int.class) {
      if (paramClass2 == int.class || paramClass2 == long.class || paramClass2 == float.class || paramClass2 == double.class)
        return true; 
    } else if (paramClass1 == long.class) {
      if (paramClass2 == long.class || paramClass2 == float.class || paramClass2 == double.class)
        return true; 
    } else if (paramClass1 == float.class) {
      if (paramClass2 == float.class || paramClass2 == double.class)
        return true; 
    } else if (paramClass1 == double.class && paramClass2 == double.class) {
      return true;
    } 
    return false;
  }
  
  protected static void emitWideningBytecodeForPrimitiveConversion(ClassFileAssembler paramClassFileAssembler, Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == byte.class || paramClass1 == short.class || paramClass1 == char.class || paramClass1 == int.class) {
      if (paramClass2 == long.class) {
        paramClassFileAssembler.opc_i2l();
      } else if (paramClass2 == float.class) {
        paramClassFileAssembler.opc_i2f();
      } else if (paramClass2 == double.class) {
        paramClassFileAssembler.opc_i2d();
      } 
    } else if (paramClass1 == long.class) {
      if (paramClass2 == float.class) {
        paramClassFileAssembler.opc_l2f();
      } else if (paramClass2 == double.class) {
        paramClassFileAssembler.opc_l2d();
      } 
    } else if (paramClass1 == float.class && paramClass2 == double.class) {
      paramClassFileAssembler.opc_f2d();
    } 
  }
  
  protected short unboxingMethodForPrimitiveType(Class<?> paramClass) {
    if (paramClass == boolean.class)
      return this.booleanUnboxIdx; 
    if (paramClass == byte.class)
      return this.byteUnboxIdx; 
    if (paramClass == char.class)
      return this.characterUnboxIdx; 
    if (paramClass == short.class)
      return this.shortUnboxIdx; 
    if (paramClass == int.class)
      return this.integerUnboxIdx; 
    if (paramClass == long.class)
      return this.longUnboxIdx; 
    if (paramClass == float.class)
      return this.floatUnboxIdx; 
    if (paramClass == double.class)
      return this.doubleUnboxIdx; 
    throw new InternalError("Illegal primitive type " + paramClass.getName());
  }
  
  protected static boolean isPrimitive(Class<?> paramClass) { return (paramClass.isPrimitive() && paramClass != void.class); }
  
  protected int typeSizeInStackSlots(Class<?> paramClass) { return (paramClass == void.class) ? 0 : ((paramClass == long.class || paramClass == double.class) ? 2 : 1); }
  
  protected ClassFileAssembler illegalArgumentCodeBuffer() {
    if (this.illegalArgumentCodeBuffer == null) {
      this.illegalArgumentCodeBuffer = new ClassFileAssembler();
      this.illegalArgumentCodeBuffer.opc_new(this.illegalArgumentClass);
      this.illegalArgumentCodeBuffer.opc_dup();
      this.illegalArgumentCodeBuffer.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
      this.illegalArgumentCodeBuffer.opc_athrow();
    } 
    return this.illegalArgumentCodeBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\AccessorGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */