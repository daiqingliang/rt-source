package java.lang.invoke;

import java.io.FilePermission;
import java.lang.invoke.AbstractValidatingLambdaMetafactory;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.InnerClassLambdaMetafactory;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.ProxyClassesDumper;
import java.lang.invoke.TypeConvertingMethodAdapter;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.PropertyPermission;
import java.util.concurrent.atomic.AtomicInteger;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

final class InnerClassLambdaMetafactory extends AbstractValidatingLambdaMetafactory {
  private static final Unsafe UNSAFE = Unsafe.getUnsafe();
  
  private static final int CLASSFILE_VERSION = 52;
  
  private static final String METHOD_DESCRIPTOR_VOID = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]);
  
  private static final String JAVA_LANG_OBJECT = "java/lang/Object";
  
  private static final String NAME_CTOR = "<init>";
  
  private static final String NAME_FACTORY = "get$Lambda";
  
  private static final String NAME_SERIALIZED_LAMBDA = "java/lang/invoke/SerializedLambda";
  
  private static final String NAME_NOT_SERIALIZABLE_EXCEPTION = "java/io/NotSerializableException";
  
  private static final String DESCR_METHOD_WRITE_REPLACE = "()Ljava/lang/Object;";
  
  private static final String DESCR_METHOD_WRITE_OBJECT = "(Ljava/io/ObjectOutputStream;)V";
  
  private static final String DESCR_METHOD_READ_OBJECT = "(Ljava/io/ObjectInputStream;)V";
  
  private static final String NAME_METHOD_WRITE_REPLACE = "writeReplace";
  
  private static final String NAME_METHOD_READ_OBJECT = "readObject";
  
  private static final String NAME_METHOD_WRITE_OBJECT = "writeObject";
  
  private static final String DESCR_CTOR_SERIALIZED_LAMBDA = MethodType.methodType(void.class, Class.class, new Class[] { String.class, String.class, String.class, int.class, String.class, String.class, String.class, String.class, Object[].class }).toMethodDescriptorString();
  
  private static final String DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION = MethodType.methodType(void.class, String.class).toMethodDescriptorString();
  
  private static final String[] SER_HOSTILE_EXCEPTIONS = { "java/io/NotSerializableException" };
  
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private static final AtomicInteger counter = new AtomicInteger(0);
  
  private static final ProxyClassesDumper dumper;
  
  private final String implMethodClassName = this.implDefiningClass.getName().replace('.', '/');
  
  private final String implMethodName = this.implInfo.getName();
  
  private final String implMethodDesc = this.implMethodType.toMethodDescriptorString();
  
  private final Class<?> implMethodReturnClass = (this.implKind == 8) ? this.implDefiningClass : this.implMethodType.returnType();
  
  private final MethodType constructorType;
  
  private final ClassWriter cw;
  
  private final String[] argNames;
  
  private final String[] argDescs;
  
  private final String lambdaClassName;
  
  public InnerClassLambdaMetafactory(MethodHandles.Lookup paramLookup, MethodType paramMethodType1, String paramString, MethodType paramMethodType2, MethodHandle paramMethodHandle, MethodType paramMethodType3, boolean paramBoolean, Class<?>[] paramArrayOfClass, MethodType[] paramArrayOfMethodType) throws LambdaConversionException {
    super(paramLookup, paramMethodType1, paramString, paramMethodType2, paramMethodHandle, paramMethodType3, paramBoolean, paramArrayOfClass, paramArrayOfMethodType);
    this.constructorType = paramMethodType1.changeReturnType(void.class);
    this.lambdaClassName = this.targetClass.getName().replace('.', '/') + "$$Lambda$" + counter.incrementAndGet();
    this.cw = new ClassWriter(1);
    int i = paramMethodType1.parameterCount();
    if (i > 0) {
      this.argNames = new String[i];
      this.argDescs = new String[i];
      for (byte b = 0; b < i; b++) {
        this.argNames[b] = "arg$" + (b + true);
        this.argDescs[b] = BytecodeDescriptor.unparse(paramMethodType1.parameterType(b));
      } 
    } else {
      this.argNames = this.argDescs = EMPTY_STRING_ARRAY;
    } 
  }
  
  CallSite buildCallSite() throws LambdaConversionException {
    final Class innerClass = spinInnerClass();
    if (this.invokedType.parameterCount() == 0) {
      Constructor[] arrayOfConstructor = (Constructor[])AccessController.doPrivileged(new PrivilegedAction<Constructor<?>[]>() {
            public Constructor<?>[] run() {
              Constructor[] arrayOfConstructor = innerClass.getDeclaredConstructors();
              if (arrayOfConstructor.length == 1)
                arrayOfConstructor[0].setAccessible(true); 
              return arrayOfConstructor;
            }
          });
      if (arrayOfConstructor.length != 1)
        throw new LambdaConversionException("Expected one lambda constructor for " + clazz.getCanonicalName() + ", got " + arrayOfConstructor.length); 
      try {
        Object object = arrayOfConstructor[0].newInstance(new Object[0]);
        return new ConstantCallSite(MethodHandles.constant(this.samBase, object));
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw new LambdaConversionException("Exception instantiating lambda object", reflectiveOperationException);
      } 
    } 
    try {
      UNSAFE.ensureClassInitialized(clazz);
      return new ConstantCallSite(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(clazz, "get$Lambda", this.invokedType));
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new LambdaConversionException("Exception finding constructor", reflectiveOperationException);
    } 
  }
  
  private Class<?> spinInnerClass() throws LambdaConversionException {
    String[] arrayOfString;
    String str = this.samBase.getName().replace('.', '/');
    boolean bool = (!this.isSerializable && java.io.Serializable.class.isAssignableFrom(this.samBase)) ? 1 : 0;
    if (this.markerInterfaces.length == 0) {
      arrayOfString = new String[] { str };
    } else {
      LinkedHashSet linkedHashSet = new LinkedHashSet(this.markerInterfaces.length + 1);
      linkedHashSet.add(str);
      for (Class clazz : this.markerInterfaces) {
        linkedHashSet.add(clazz.getName().replace('.', '/'));
        bool |= ((!this.isSerializable && java.io.Serializable.class.isAssignableFrom(clazz)) ? 1 : 0);
      } 
      arrayOfString = (String[])linkedHashSet.toArray(new String[linkedHashSet.size()]);
    } 
    this.cw.visit(52, 4144, this.lambdaClassName, null, "java/lang/Object", arrayOfString);
    for (byte b = 0; b < this.argDescs.length; b++) {
      FieldVisitor fieldVisitor = this.cw.visitField(18, this.argNames[b], this.argDescs[b], null, null);
      fieldVisitor.visitEnd();
    } 
    generateConstructor();
    if (this.invokedType.parameterCount() != 0)
      generateFactory(); 
    MethodVisitor methodVisitor = this.cw.visitMethod(1, this.samMethodName, this.samMethodType.toMethodDescriptorString(), null, null);
    methodVisitor.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    (new ForwardingMethodGenerator(methodVisitor)).generate(this.samMethodType);
    if (this.additionalBridges != null)
      for (MethodType methodType : this.additionalBridges) {
        methodVisitor = this.cw.visitMethod(65, this.samMethodName, methodType.toMethodDescriptorString(), null, null);
        methodVisitor.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        (new ForwardingMethodGenerator(methodVisitor)).generate(methodType);
      }  
    if (this.isSerializable) {
      generateSerializationFriendlyMethods();
    } else if (bool) {
      generateSerializationHostileMethods();
    } 
    this.cw.visitEnd();
    final byte[] classBytes = this.cw.toByteArray();
    if (dumper != null)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              dumper.dumpClass(InnerClassLambdaMetafactory.this.lambdaClassName, classBytes);
              return null;
            }
          }null, new Permission[] { new FilePermission("<<ALL FILES>>", "read, write"), new PropertyPermission("user.dir", "read") }); 
    return UNSAFE.defineAnonymousClass(this.targetClass, arrayOfByte, null);
  }
  
  private void generateFactory() {
    MethodVisitor methodVisitor = this.cw.visitMethod(10, "get$Lambda", this.invokedType.toMethodDescriptorString(), null, null);
    methodVisitor.visitCode();
    methodVisitor.visitTypeInsn(187, this.lambdaClassName);
    methodVisitor.visitInsn(89);
    int i = this.invokedType.parameterCount();
    byte b = 0;
    int j = 0;
    while (b < i) {
      Class clazz = this.invokedType.parameterType(b);
      methodVisitor.visitVarInsn(getLoadOpcode(clazz), j);
      j += getParameterSize(clazz);
      b++;
    } 
    methodVisitor.visitMethodInsn(183, this.lambdaClassName, "<init>", this.constructorType.toMethodDescriptorString(), false);
    methodVisitor.visitInsn(176);
    methodVisitor.visitMaxs(-1, -1);
    methodVisitor.visitEnd();
  }
  
  private void generateConstructor() {
    MethodVisitor methodVisitor = this.cw.visitMethod(2, "<init>", this.constructorType.toMethodDescriptorString(), null, null);
    methodVisitor.visitCode();
    methodVisitor.visitVarInsn(25, 0);
    methodVisitor.visitMethodInsn(183, "java/lang/Object", "<init>", METHOD_DESCRIPTOR_VOID, false);
    int i = this.invokedType.parameterCount();
    byte b = 0;
    int j = 0;
    while (b < i) {
      methodVisitor.visitVarInsn(25, 0);
      Class clazz = this.invokedType.parameterType(b);
      methodVisitor.visitVarInsn(getLoadOpcode(clazz), j + true);
      j += getParameterSize(clazz);
      methodVisitor.visitFieldInsn(181, this.lambdaClassName, this.argNames[b], this.argDescs[b]);
      b++;
    } 
    methodVisitor.visitInsn(177);
    methodVisitor.visitMaxs(-1, -1);
    methodVisitor.visitEnd();
  }
  
  private void generateSerializationFriendlyMethods() {
    TypeConvertingMethodAdapter typeConvertingMethodAdapter = new TypeConvertingMethodAdapter(this.cw.visitMethod(18, "writeReplace", "()Ljava/lang/Object;", null, null));
    typeConvertingMethodAdapter.visitCode();
    typeConvertingMethodAdapter.visitTypeInsn(187, "java/lang/invoke/SerializedLambda");
    typeConvertingMethodAdapter.visitInsn(89);
    typeConvertingMethodAdapter.visitLdcInsn(Type.getType(this.targetClass));
    typeConvertingMethodAdapter.visitLdcInsn(this.invokedType.returnType().getName().replace('.', '/'));
    typeConvertingMethodAdapter.visitLdcInsn(this.samMethodName);
    typeConvertingMethodAdapter.visitLdcInsn(this.samMethodType.toMethodDescriptorString());
    typeConvertingMethodAdapter.visitLdcInsn(Integer.valueOf(this.implInfo.getReferenceKind()));
    typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getDeclaringClass().getName().replace('.', '/'));
    typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getName());
    typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getMethodType().toMethodDescriptorString());
    typeConvertingMethodAdapter.visitLdcInsn(this.instantiatedMethodType.toMethodDescriptorString());
    typeConvertingMethodAdapter.iconst(this.argDescs.length);
    typeConvertingMethodAdapter.visitTypeInsn(189, "java/lang/Object");
    for (byte b = 0; b < this.argDescs.length; b++) {
      typeConvertingMethodAdapter.visitInsn(89);
      typeConvertingMethodAdapter.iconst(b);
      typeConvertingMethodAdapter.visitVarInsn(25, 0);
      typeConvertingMethodAdapter.visitFieldInsn(180, this.lambdaClassName, this.argNames[b], this.argDescs[b]);
      typeConvertingMethodAdapter.boxIfTypePrimitive(Type.getType(this.argDescs[b]));
      typeConvertingMethodAdapter.visitInsn(83);
    } 
    typeConvertingMethodAdapter.visitMethodInsn(183, "java/lang/invoke/SerializedLambda", "<init>", DESCR_CTOR_SERIALIZED_LAMBDA, false);
    typeConvertingMethodAdapter.visitInsn(176);
    typeConvertingMethodAdapter.visitMaxs(-1, -1);
    typeConvertingMethodAdapter.visitEnd();
  }
  
  private void generateSerializationHostileMethods() {
    MethodVisitor methodVisitor = this.cw.visitMethod(18, "writeObject", "(Ljava/io/ObjectOutputStream;)V", null, SER_HOSTILE_EXCEPTIONS);
    methodVisitor.visitCode();
    methodVisitor.visitTypeInsn(187, "java/io/NotSerializableException");
    methodVisitor.visitInsn(89);
    methodVisitor.visitLdcInsn("Non-serializable lambda");
    methodVisitor.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
    methodVisitor.visitInsn(191);
    methodVisitor.visitMaxs(-1, -1);
    methodVisitor.visitEnd();
    methodVisitor = this.cw.visitMethod(18, "readObject", "(Ljava/io/ObjectInputStream;)V", null, SER_HOSTILE_EXCEPTIONS);
    methodVisitor.visitCode();
    methodVisitor.visitTypeInsn(187, "java/io/NotSerializableException");
    methodVisitor.visitInsn(89);
    methodVisitor.visitLdcInsn("Non-serializable lambda");
    methodVisitor.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
    methodVisitor.visitInsn(191);
    methodVisitor.visitMaxs(-1, -1);
    methodVisitor.visitEnd();
  }
  
  static int getParameterSize(Class<?> paramClass) { return (paramClass == void.class) ? 0 : ((paramClass == long.class || paramClass == double.class) ? 2 : 1); }
  
  static int getLoadOpcode(Class<?> paramClass) {
    if (paramClass == void.class)
      throw new InternalError("Unexpected void type of load opcode"); 
    return 21 + getOpcodeOffset(paramClass);
  }
  
  static int getReturnOpcode(Class<?> paramClass) { return (paramClass == void.class) ? 177 : (172 + getOpcodeOffset(paramClass)); }
  
  private static int getOpcodeOffset(Class<?> paramClass) { return paramClass.isPrimitive() ? ((paramClass == long.class) ? 1 : ((paramClass == float.class) ? 2 : ((paramClass == double.class) ? 3 : 0))) : 4; }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.internal.lambda.dumpProxyClasses"), null, new Permission[] { new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read") });
    dumper = (null == str) ? null : ProxyClassesDumper.getInstance(str);
  }
  
  private class ForwardingMethodGenerator extends TypeConvertingMethodAdapter {
    ForwardingMethodGenerator(MethodVisitor param1MethodVisitor) { super(param1MethodVisitor); }
    
    void generate(MethodType param1MethodType) {
      visitCode();
      if (InnerClassLambdaMetafactory.this.implKind == 8) {
        visitTypeInsn(187, InnerClassLambdaMetafactory.this.implMethodClassName);
        visitInsn(89);
      } 
      for (byte b = 0; b < InnerClassLambdaMetafactory.this.argNames.length; b++) {
        visitVarInsn(25, 0);
        visitFieldInsn(180, InnerClassLambdaMetafactory.this.lambdaClassName, InnerClassLambdaMetafactory.this.argNames[b], InnerClassLambdaMetafactory.this.argDescs[b]);
      } 
      convertArgumentTypes(param1MethodType);
      visitMethodInsn(invocationOpcode(), InnerClassLambdaMetafactory.this.implMethodClassName, InnerClassLambdaMetafactory.this.implMethodName, InnerClassLambdaMetafactory.this.implMethodDesc, InnerClassLambdaMetafactory.this.implDefiningClass.isInterface());
      Class clazz = param1MethodType.returnType();
      convertType(InnerClassLambdaMetafactory.this.implMethodReturnClass, clazz, clazz);
      visitInsn(InnerClassLambdaMetafactory.getReturnOpcode(clazz));
      visitMaxs(-1, -1);
      visitEnd();
    }
    
    private void convertArgumentTypes(MethodType param1MethodType) {
      int i = 0;
      boolean bool1 = (InnerClassLambdaMetafactory.this.implIsInstanceMethod && InnerClassLambdaMetafactory.this.invokedType.parameterCount() == 0) ? 1 : 0;
      boolean bool2 = bool1 ? 1 : 0;
      if (bool1) {
        Class clazz = param1MethodType.parameterType(0);
        visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(clazz), i + true);
        i += InnerClassLambdaMetafactory.getParameterSize(clazz);
        convertType(clazz, InnerClassLambdaMetafactory.this.implDefiningClass, InnerClassLambdaMetafactory.this.instantiatedMethodType.parameterType(0));
      } 
      int j = param1MethodType.parameterCount();
      int k = InnerClassLambdaMetafactory.this.implMethodType.parameterCount() - j;
      for (int m = bool2; m < j; m++) {
        Class clazz = param1MethodType.parameterType(m);
        visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(clazz), i + 1);
        i += InnerClassLambdaMetafactory.getParameterSize(clazz);
        convertType(clazz, InnerClassLambdaMetafactory.this.implMethodType.parameterType(k + m), InnerClassLambdaMetafactory.this.instantiatedMethodType.parameterType(m));
      } 
    }
    
    private int invocationOpcode() throws InternalError {
      switch (InnerClassLambdaMetafactory.this.implKind) {
        case 6:
          return 184;
        case 8:
          return 183;
        case 5:
          return 182;
        case 9:
          return 185;
        case 7:
          return 183;
      } 
      throw new InternalError("Unexpected invocation kind: " + InnerClassLambdaMetafactory.this.implKind);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\InnerClassLambdaMetafactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */