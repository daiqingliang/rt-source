package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import sun.security.action.GetBooleanAction;

public class ProxyGenerator {
  private static final int CLASSFILE_MAJOR_VERSION = 49;
  
  private static final int CLASSFILE_MINOR_VERSION = 0;
  
  private static final int CONSTANT_UTF8 = 1;
  
  private static final int CONSTANT_UNICODE = 2;
  
  private static final int CONSTANT_INTEGER = 3;
  
  private static final int CONSTANT_FLOAT = 4;
  
  private static final int CONSTANT_LONG = 5;
  
  private static final int CONSTANT_DOUBLE = 6;
  
  private static final int CONSTANT_CLASS = 7;
  
  private static final int CONSTANT_STRING = 8;
  
  private static final int CONSTANT_FIELD = 9;
  
  private static final int CONSTANT_METHOD = 10;
  
  private static final int CONSTANT_INTERFACEMETHOD = 11;
  
  private static final int CONSTANT_NAMEANDTYPE = 12;
  
  private static final int ACC_PUBLIC = 1;
  
  private static final int ACC_PRIVATE = 2;
  
  private static final int ACC_STATIC = 8;
  
  private static final int ACC_FINAL = 16;
  
  private static final int ACC_SUPER = 32;
  
  private static final int opc_aconst_null = 1;
  
  private static final int opc_iconst_0 = 3;
  
  private static final int opc_bipush = 16;
  
  private static final int opc_sipush = 17;
  
  private static final int opc_ldc = 18;
  
  private static final int opc_ldc_w = 19;
  
  private static final int opc_iload = 21;
  
  private static final int opc_lload = 22;
  
  private static final int opc_fload = 23;
  
  private static final int opc_dload = 24;
  
  private static final int opc_aload = 25;
  
  private static final int opc_iload_0 = 26;
  
  private static final int opc_lload_0 = 30;
  
  private static final int opc_fload_0 = 34;
  
  private static final int opc_dload_0 = 38;
  
  private static final int opc_aload_0 = 42;
  
  private static final int opc_astore = 58;
  
  private static final int opc_astore_0 = 75;
  
  private static final int opc_aastore = 83;
  
  private static final int opc_pop = 87;
  
  private static final int opc_dup = 89;
  
  private static final int opc_ireturn = 172;
  
  private static final int opc_lreturn = 173;
  
  private static final int opc_freturn = 174;
  
  private static final int opc_dreturn = 175;
  
  private static final int opc_areturn = 176;
  
  private static final int opc_return = 177;
  
  private static final int opc_getstatic = 178;
  
  private static final int opc_putstatic = 179;
  
  private static final int opc_getfield = 180;
  
  private static final int opc_invokevirtual = 182;
  
  private static final int opc_invokespecial = 183;
  
  private static final int opc_invokestatic = 184;
  
  private static final int opc_invokeinterface = 185;
  
  private static final int opc_new = 187;
  
  private static final int opc_anewarray = 189;
  
  private static final int opc_athrow = 191;
  
  private static final int opc_checkcast = 192;
  
  private static final int opc_wide = 196;
  
  private static final String superclassName = "java/lang/reflect/Proxy";
  
  private static final String handlerFieldName = "h";
  
  private static final boolean saveGeneratedFiles = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.misc.ProxyGenerator.saveGeneratedFiles"))).booleanValue();
  
  private static Method hashCodeMethod;
  
  private static Method equalsMethod;
  
  private static Method toStringMethod;
  
  private String className;
  
  private Class<?>[] interfaces;
  
  private int accessFlags;
  
  private ConstantPool cp = new ConstantPool(null);
  
  private List<FieldInfo> fields = new ArrayList();
  
  private List<MethodInfo> methods = new ArrayList();
  
  private Map<String, List<ProxyMethod>> proxyMethods = new HashMap();
  
  private int proxyMethodCount = 0;
  
  public static byte[] generateProxyClass(String paramString, Class<?>[] paramArrayOfClass) { return generateProxyClass(paramString, paramArrayOfClass, 49); }
  
  public static byte[] generateProxyClass(final String name, Class<?>[] paramArrayOfClass, int paramInt) {
    ProxyGenerator proxyGenerator = new ProxyGenerator(paramString, paramArrayOfClass, paramInt);
    final byte[] classFile = proxyGenerator.generateClassFile();
    if (saveGeneratedFiles)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              try {
                Path path;
                int i = name.lastIndexOf('.');
                if (i > 0) {
                  Path path1 = Paths.get(name.substring(0, i).replace('.', File.separatorChar), new String[0]);
                  Files.createDirectories(path1, new java.nio.file.attribute.FileAttribute[0]);
                  path = path1.resolve(name.substring(i + 1, name.length()) + ".class");
                } else {
                  path = Paths.get(name + ".class", new String[0]);
                } 
                Files.write(path, classFile, new java.nio.file.OpenOption[0]);
                return null;
              } catch (IOException iOException) {
                throw new InternalError("I/O exception saving generated file: " + iOException);
              } 
            }
          }); 
    return arrayOfByte;
  }
  
  private ProxyGenerator(String paramString, Class<?>[] paramArrayOfClass, int paramInt) {
    this.className = paramString;
    this.interfaces = paramArrayOfClass;
    this.accessFlags = paramInt;
  }
  
  private byte[] generateClassFile() {
    addProxyMethod(hashCodeMethod, Object.class);
    addProxyMethod(equalsMethod, Object.class);
    addProxyMethod(toStringMethod, Object.class);
    for (Class clazz : this.interfaces) {
      for (Method method : clazz.getMethods())
        addProxyMethod(method, clazz); 
    } 
    for (List list : this.proxyMethods.values())
      checkReturnTypes(list); 
    try {
      this.methods.add(generateConstructor());
      for (List list : this.proxyMethods.values()) {
        for (ProxyMethod proxyMethod : list) {
          this.fields.add(new FieldInfo(proxyMethod.methodFieldName, "Ljava/lang/reflect/Method;", 10));
          this.methods.add(proxyMethod.generateMethod());
        } 
      } 
      this.methods.add(generateStaticInitializer());
    } catch (IOException iOException) {
      throw new InternalError("unexpected I/O Exception", iOException);
    } 
    if (this.methods.size() > 65535)
      throw new IllegalArgumentException("method limit exceeded"); 
    if (this.fields.size() > 65535)
      throw new IllegalArgumentException("field limit exceeded"); 
    this.cp.getClass(dotToSlash(this.className));
    this.cp.getClass("java/lang/reflect/Proxy");
    for (Class clazz : this.interfaces)
      this.cp.getClass(dotToSlash(clazz.getName())); 
    this.cp.setReadOnly();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      dataOutputStream.writeInt(-889275714);
      dataOutputStream.writeShort(0);
      dataOutputStream.writeShort(49);
      this.cp.write(dataOutputStream);
      dataOutputStream.writeShort(this.accessFlags);
      dataOutputStream.writeShort(this.cp.getClass(dotToSlash(this.className)));
      dataOutputStream.writeShort(this.cp.getClass("java/lang/reflect/Proxy"));
      dataOutputStream.writeShort(this.interfaces.length);
      for (Class clazz : this.interfaces)
        dataOutputStream.writeShort(this.cp.getClass(dotToSlash(clazz.getName()))); 
      dataOutputStream.writeShort(this.fields.size());
      for (FieldInfo fieldInfo : this.fields)
        fieldInfo.write(dataOutputStream); 
      dataOutputStream.writeShort(this.methods.size());
      for (MethodInfo methodInfo : this.methods)
        methodInfo.write(dataOutputStream); 
      dataOutputStream.writeShort(0);
    } catch (IOException iOException) {
      throw new InternalError("unexpected I/O Exception", iOException);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  private void addProxyMethod(Method paramMethod, Class<?> paramClass) {
    String str1 = paramMethod.getName();
    Class[] arrayOfClass1 = paramMethod.getParameterTypes();
    Class clazz = paramMethod.getReturnType();
    Class[] arrayOfClass2 = paramMethod.getExceptionTypes();
    String str2 = str1 + getParameterDescriptors(arrayOfClass1);
    List list = (List)this.proxyMethods.get(str2);
    if (list != null) {
      for (ProxyMethod proxyMethod : list) {
        if (clazz == proxyMethod.returnType) {
          ArrayList arrayList = new ArrayList();
          collectCompatibleTypes(arrayOfClass2, proxyMethod.exceptionTypes, arrayList);
          collectCompatibleTypes(proxyMethod.exceptionTypes, arrayOfClass2, arrayList);
          proxyMethod.exceptionTypes = new Class[arrayList.size()];
          proxyMethod.exceptionTypes = (Class[])arrayList.toArray(proxyMethod.exceptionTypes);
          return;
        } 
      } 
    } else {
      list = new ArrayList(3);
      this.proxyMethods.put(str2, list);
    } 
    list.add(new ProxyMethod(str1, arrayOfClass1, clazz, arrayOfClass2, paramClass, null));
  }
  
  private static void checkReturnTypes(List<ProxyMethod> paramList) {
    if (paramList.size() < 2)
      return; 
    LinkedList linkedList = new LinkedList();
    label30: for (ProxyMethod proxyMethod : paramList) {
      Class clazz = proxyMethod.returnType;
      if (clazz.isPrimitive())
        throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(proxyMethod.methodName, proxyMethod.parameterTypes) + " but incompatible return types: " + clazz.getName() + " and others"); 
      boolean bool = false;
      ListIterator listIterator = linkedList.listIterator();
      while (listIterator.hasNext()) {
        Class clazz1 = (Class)listIterator.next();
        if (clazz.isAssignableFrom(clazz1)) {
          assert false;
          continue label30;
        } 
        if (clazz1.isAssignableFrom(clazz)) {
          if (!bool) {
            listIterator.set(clazz);
            bool = true;
            continue;
          } 
          listIterator.remove();
        } 
      } 
      if (!bool)
        linkedList.add(clazz); 
    } 
    if (linkedList.size() > 1) {
      ProxyMethod proxyMethod = (ProxyMethod)paramList.get(0);
      throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(proxyMethod.methodName, proxyMethod.parameterTypes) + " but incompatible return types: " + linkedList);
    } 
  }
  
  private MethodInfo generateConstructor() throws IOException {
    MethodInfo methodInfo = new MethodInfo("<init>", "(Ljava/lang/reflect/InvocationHandler;)V", 1);
    DataOutputStream dataOutputStream = new DataOutputStream(methodInfo.code);
    code_aload(0, dataOutputStream);
    code_aload(1, dataOutputStream);
    dataOutputStream.writeByte(183);
    dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/reflect/Proxy", "<init>", "(Ljava/lang/reflect/InvocationHandler;)V"));
    dataOutputStream.writeByte(177);
    methodInfo.maxStack = 10;
    methodInfo.maxLocals = 2;
    methodInfo.declaredExceptions = new short[0];
    return methodInfo;
  }
  
  private MethodInfo generateStaticInitializer() throws IOException {
    MethodInfo methodInfo = new MethodInfo("<clinit>", "()V", 8);
    byte b = 1;
    int i = 0;
    DataOutputStream dataOutputStream = new DataOutputStream(methodInfo.code);
    for (List list : this.proxyMethods.values()) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        ProxyMethod proxyMethod;
        proxyMethod.codeFieldInitialization(dataOutputStream);
      } 
    } 
    dataOutputStream.writeByte(177);
    short s1 = (short)methodInfo.code.size();
    short s2 = s1;
    methodInfo.exceptionTable.add(new ExceptionTableEntry(i, s2, s1, this.cp.getClass("java/lang/NoSuchMethodException")));
    code_astore(b, dataOutputStream);
    dataOutputStream.writeByte(187);
    dataOutputStream.writeShort(this.cp.getClass("java/lang/NoSuchMethodError"));
    dataOutputStream.writeByte(89);
    code_aload(b, dataOutputStream);
    dataOutputStream.writeByte(182);
    dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
    dataOutputStream.writeByte(183);
    dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V"));
    dataOutputStream.writeByte(191);
    s1 = (short)methodInfo.code.size();
    methodInfo.exceptionTable.add(new ExceptionTableEntry(i, s2, s1, this.cp.getClass("java/lang/ClassNotFoundException")));
    code_astore(b, dataOutputStream);
    dataOutputStream.writeByte(187);
    dataOutputStream.writeShort(this.cp.getClass("java/lang/NoClassDefFoundError"));
    dataOutputStream.writeByte(89);
    code_aload(b, dataOutputStream);
    dataOutputStream.writeByte(182);
    dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
    dataOutputStream.writeByte(183);
    dataOutputStream.writeShort(this.cp.getMethodRef("java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V"));
    dataOutputStream.writeByte(191);
    if (methodInfo.code.size() > 65535)
      throw new IllegalArgumentException("code size limit exceeded"); 
    methodInfo.maxStack = 10;
    methodInfo.maxLocals = (short)(b + 1);
    methodInfo.declaredExceptions = new short[0];
    return methodInfo;
  }
  
  private void code_iload(int paramInt, DataOutputStream paramDataOutputStream) throws IOException { codeLocalLoadStore(paramInt, 21, 26, paramDataOutputStream); }
  
  private void code_lload(int paramInt, DataOutputStream paramDataOutputStream) throws IOException { codeLocalLoadStore(paramInt, 22, 30, paramDataOutputStream); }
  
  private void code_fload(int paramInt, DataOutputStream paramDataOutputStream) throws IOException { codeLocalLoadStore(paramInt, 23, 34, paramDataOutputStream); }
  
  private void code_dload(int paramInt, DataOutputStream paramDataOutputStream) throws IOException { codeLocalLoadStore(paramInt, 24, 38, paramDataOutputStream); }
  
  private void code_aload(int paramInt, DataOutputStream paramDataOutputStream) throws IOException { codeLocalLoadStore(paramInt, 25, 42, paramDataOutputStream); }
  
  private void code_astore(int paramInt, DataOutputStream paramDataOutputStream) throws IOException { codeLocalLoadStore(paramInt, 58, 75, paramDataOutputStream); }
  
  private void codeLocalLoadStore(int paramInt1, int paramInt2, int paramInt3, DataOutputStream paramDataOutputStream) throws IOException {
    assert paramInt1 >= 0 && paramInt1 <= 65535;
    if (paramInt1 <= 3) {
      paramDataOutputStream.writeByte(paramInt3 + paramInt1);
    } else if (paramInt1 <= 255) {
      paramDataOutputStream.writeByte(paramInt2);
      paramDataOutputStream.writeByte(paramInt1 & 0xFF);
    } else {
      paramDataOutputStream.writeByte(196);
      paramDataOutputStream.writeByte(paramInt2);
      paramDataOutputStream.writeShort(paramInt1 & 0xFFFF);
    } 
  }
  
  private void code_ldc(int paramInt, DataOutputStream paramDataOutputStream) throws IOException {
    assert paramInt >= 0 && paramInt <= 65535;
    if (paramInt <= 255) {
      paramDataOutputStream.writeByte(18);
      paramDataOutputStream.writeByte(paramInt & 0xFF);
    } else {
      paramDataOutputStream.writeByte(19);
      paramDataOutputStream.writeShort(paramInt & 0xFFFF);
    } 
  }
  
  private void code_ipush(int paramInt, DataOutputStream paramDataOutputStream) throws IOException {
    if (paramInt >= -1 && paramInt <= 5) {
      paramDataOutputStream.writeByte(3 + paramInt);
    } else if (paramInt >= -128 && paramInt <= 127) {
      paramDataOutputStream.writeByte(16);
      paramDataOutputStream.writeByte(paramInt & 0xFF);
    } else if (paramInt >= -32768 && paramInt <= 32767) {
      paramDataOutputStream.writeByte(17);
      paramDataOutputStream.writeShort(paramInt & 0xFFFF);
    } else {
      throw new AssertionError();
    } 
  }
  
  private void codeClassForName(Class<?> paramClass, DataOutputStream paramDataOutputStream) throws IOException {
    code_ldc(this.cp.getString(paramClass.getName()), paramDataOutputStream);
    paramDataOutputStream.writeByte(184);
    paramDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;"));
  }
  
  private static String dotToSlash(String paramString) { return paramString.replace('.', '/'); }
  
  private static String getMethodDescriptor(Class<?>[] paramArrayOfClass, Class<?> paramClass) { return getParameterDescriptors(paramArrayOfClass) + ((paramClass == void.class) ? "V" : getFieldType(paramClass)); }
  
  private static String getParameterDescriptors(Class<?>[] paramArrayOfClass) {
    StringBuilder stringBuilder = new StringBuilder("(");
    for (byte b = 0; b < paramArrayOfClass.length; b++)
      stringBuilder.append(getFieldType(paramArrayOfClass[b])); 
    stringBuilder.append(')');
    return stringBuilder.toString();
  }
  
  private static String getFieldType(Class<?> paramClass) { return paramClass.isPrimitive() ? (PrimitiveTypeInfo.get(paramClass)).baseTypeString : (paramClass.isArray() ? paramClass.getName().replace('.', '/') : ("L" + dotToSlash(paramClass.getName()) + ";")); }
  
  private static String getFriendlyMethodSignature(String paramString, Class<?>[] paramArrayOfClass) {
    StringBuilder stringBuilder = new StringBuilder(paramString);
    stringBuilder.append('(');
    for (byte b = 0; b < paramArrayOfClass.length; b++) {
      if (b)
        stringBuilder.append(','); 
      Class<?> clazz = paramArrayOfClass[b];
      byte b1;
      for (b1 = 0; clazz.isArray(); b1++)
        clazz = clazz.getComponentType(); 
      stringBuilder.append(clazz.getName());
      while (b1-- > 0)
        stringBuilder.append("[]"); 
    } 
    stringBuilder.append(')');
    return stringBuilder.toString();
  }
  
  private static int getWordsPerType(Class<?> paramClass) { return (paramClass == long.class || paramClass == double.class) ? 2 : 1; }
  
  private static void collectCompatibleTypes(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, List<Class<?>> paramList) {
    for (Class<?> clazz : paramArrayOfClass1) {
      if (!paramList.contains(clazz))
        for (Class<?> clazz1 : paramArrayOfClass2) {
          if (clazz1.isAssignableFrom(clazz)) {
            paramList.add(clazz);
            break;
          } 
        }  
    } 
  }
  
  private static List<Class<?>> computeUniqueCatchList(Class<?>[] paramArrayOfClass) {
    ArrayList arrayList = new ArrayList();
    arrayList.add(Error.class);
    arrayList.add(RuntimeException.class);
    for (Class<?> clazz : paramArrayOfClass) {
      if (clazz.isAssignableFrom(Throwable.class)) {
        arrayList.clear();
        break;
      } 
      if (Throwable.class.isAssignableFrom(clazz)) {
        byte b = 0;
        while (true) {
          if (b < arrayList.size()) {
            Class clazz1 = (Class)arrayList.get(b);
            if (clazz1.isAssignableFrom(clazz))
              break; 
            if (clazz.isAssignableFrom(clazz1)) {
              arrayList.remove(b);
              continue;
            } 
            b++;
            continue;
          } 
          arrayList.add(clazz);
          break;
        } 
      } 
    } 
    return arrayList;
  }
  
  static  {
    try {
      hashCodeMethod = Object.class.getMethod("hashCode", new Class[0]);
      equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
      toStringMethod = Object.class.getMethod("toString", new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } 
  }
  
  private static class ConstantPool {
    private List<Entry> pool = new ArrayList(32);
    
    private Map<Object, Short> map = new HashMap(16);
    
    private boolean readOnly = false;
    
    private ConstantPool() {}
    
    public short getUtf8(String param1String) {
      if (param1String == null)
        throw new NullPointerException(); 
      return getValue(param1String);
    }
    
    public short getInteger(int param1Int) { return getValue(new Integer(param1Int)); }
    
    public short getFloat(float param1Float) { return getValue(new Float(param1Float)); }
    
    public short getClass(String param1String) {
      short s = getUtf8(param1String);
      return getIndirect(new IndirectEntry(7, s));
    }
    
    public short getString(String param1String) {
      short s = getUtf8(param1String);
      return getIndirect(new IndirectEntry(8, s));
    }
    
    public short getFieldRef(String param1String1, String param1String2, String param1String3) {
      short s1 = getClass(param1String1);
      short s2 = getNameAndType(param1String2, param1String3);
      return getIndirect(new IndirectEntry(9, s1, s2));
    }
    
    public short getMethodRef(String param1String1, String param1String2, String param1String3) {
      short s1 = getClass(param1String1);
      short s2 = getNameAndType(param1String2, param1String3);
      return getIndirect(new IndirectEntry(10, s1, s2));
    }
    
    public short getInterfaceMethodRef(String param1String1, String param1String2, String param1String3) {
      short s1 = getClass(param1String1);
      short s2 = getNameAndType(param1String2, param1String3);
      return getIndirect(new IndirectEntry(11, s1, s2));
    }
    
    public short getNameAndType(String param1String1, String param1String2) {
      short s1 = getUtf8(param1String1);
      short s2 = getUtf8(param1String2);
      return getIndirect(new IndirectEntry(12, s1, s2));
    }
    
    public void setReadOnly() { this.readOnly = true; }
    
    public void write(OutputStream param1OutputStream) throws IOException {
      DataOutputStream dataOutputStream = new DataOutputStream(param1OutputStream);
      dataOutputStream.writeShort(this.pool.size() + 1);
      for (Entry entry : this.pool)
        entry.write(dataOutputStream); 
    }
    
    private short addEntry(Entry param1Entry) {
      this.pool.add(param1Entry);
      if (this.pool.size() >= 65535)
        throw new IllegalArgumentException("constant pool size limit exceeded"); 
      return (short)this.pool.size();
    }
    
    private short getValue(Object param1Object) {
      Short short = (Short)this.map.get(param1Object);
      if (short != null)
        return short.shortValue(); 
      if (this.readOnly)
        throw new InternalError("late constant pool addition: " + param1Object); 
      short s = addEntry(new ValueEntry(param1Object));
      this.map.put(param1Object, new Short(s));
      return s;
    }
    
    private short getIndirect(IndirectEntry param1IndirectEntry) {
      Short short = (Short)this.map.get(param1IndirectEntry);
      if (short != null)
        return short.shortValue(); 
      if (this.readOnly)
        throw new InternalError("late constant pool addition"); 
      short s = addEntry(param1IndirectEntry);
      this.map.put(param1IndirectEntry, new Short(s));
      return s;
    }
    
    private static abstract class Entry {
      private Entry() {}
      
      public abstract void write(DataOutputStream param2DataOutputStream) throws IOException;
    }
    
    private static class IndirectEntry extends Entry {
      private int tag;
      
      private short index0;
      
      private short index1;
      
      public IndirectEntry(int param2Int, short param2Short) {
        super(null);
        this.tag = param2Int;
        this.index0 = param2Short;
        this.index1 = 0;
      }
      
      public IndirectEntry(int param2Int, short param2Short1, short param2Short2) {
        super(null);
        this.tag = param2Int;
        this.index0 = param2Short1;
        this.index1 = param2Short2;
      }
      
      public void write(DataOutputStream param2DataOutputStream) throws IOException {
        param2DataOutputStream.writeByte(this.tag);
        param2DataOutputStream.writeShort(this.index0);
        if (this.tag == 9 || this.tag == 10 || this.tag == 11 || this.tag == 12)
          param2DataOutputStream.writeShort(this.index1); 
      }
      
      public int hashCode() { return this.tag + this.index0 + this.index1; }
      
      public boolean equals(Object param2Object) {
        if (param2Object instanceof IndirectEntry) {
          IndirectEntry indirectEntry = (IndirectEntry)param2Object;
          if (this.tag == indirectEntry.tag && this.index0 == indirectEntry.index0 && this.index1 == indirectEntry.index1)
            return true; 
        } 
        return false;
      }
    }
    
    private static class ValueEntry extends Entry {
      private Object value;
      
      public ValueEntry(Object param2Object) {
        super(null);
        this.value = param2Object;
      }
      
      public void write(DataOutputStream param2DataOutputStream) throws IOException {
        if (this.value instanceof String) {
          param2DataOutputStream.writeByte(1);
          param2DataOutputStream.writeUTF((String)this.value);
        } else if (this.value instanceof Integer) {
          param2DataOutputStream.writeByte(3);
          param2DataOutputStream.writeInt(((Integer)this.value).intValue());
        } else if (this.value instanceof Float) {
          param2DataOutputStream.writeByte(4);
          param2DataOutputStream.writeFloat(((Float)this.value).floatValue());
        } else if (this.value instanceof Long) {
          param2DataOutputStream.writeByte(5);
          param2DataOutputStream.writeLong(((Long)this.value).longValue());
        } else if (this.value instanceof Double) {
          param2DataOutputStream.writeDouble(6.0D);
          param2DataOutputStream.writeDouble(((Double)this.value).doubleValue());
        } else {
          throw new InternalError("bogus value entry: " + this.value);
        } 
      }
    }
  }
  
  private static class ExceptionTableEntry {
    public short startPc;
    
    public short endPc;
    
    public short handlerPc;
    
    public short catchType;
    
    public ExceptionTableEntry(short param1Short1, short param1Short2, short param1Short3, short param1Short4) {
      this.startPc = param1Short1;
      this.endPc = param1Short2;
      this.handlerPc = param1Short3;
      this.catchType = param1Short4;
    }
  }
  
  private class FieldInfo {
    public int accessFlags;
    
    public String name;
    
    public String descriptor;
    
    public FieldInfo(String param1String1, String param1String2, int param1Int) {
      this.name = param1String1;
      this.descriptor = param1String2;
      this.accessFlags = param1Int;
      this$0.cp.getUtf8(param1String1);
      this$0.cp.getUtf8(param1String2);
    }
    
    public void write(DataOutputStream param1DataOutputStream) throws IOException {
      param1DataOutputStream.writeShort(this.accessFlags);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.name));
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.descriptor));
      param1DataOutputStream.writeShort(0);
    }
  }
  
  private class MethodInfo {
    public int accessFlags;
    
    public String name;
    
    public String descriptor;
    
    public short maxStack;
    
    public short maxLocals;
    
    public ByteArrayOutputStream code = new ByteArrayOutputStream();
    
    public List<ProxyGenerator.ExceptionTableEntry> exceptionTable = new ArrayList();
    
    public short[] declaredExceptions;
    
    public MethodInfo(String param1String1, String param1String2, int param1Int) {
      this.name = param1String1;
      this.descriptor = param1String2;
      this.accessFlags = param1Int;
      this$0.cp.getUtf8(param1String1);
      this$0.cp.getUtf8(param1String2);
      this$0.cp.getUtf8("Code");
      this$0.cp.getUtf8("Exceptions");
    }
    
    public void write(DataOutputStream param1DataOutputStream) throws IOException {
      param1DataOutputStream.writeShort(this.accessFlags);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.name));
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.descriptor));
      param1DataOutputStream.writeShort(2);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8("Code"));
      param1DataOutputStream.writeInt(12 + this.code.size() + 8 * this.exceptionTable.size());
      param1DataOutputStream.writeShort(this.maxStack);
      param1DataOutputStream.writeShort(this.maxLocals);
      param1DataOutputStream.writeInt(this.code.size());
      this.code.writeTo(param1DataOutputStream);
      param1DataOutputStream.writeShort(this.exceptionTable.size());
      for (ProxyGenerator.ExceptionTableEntry exceptionTableEntry : this.exceptionTable) {
        param1DataOutputStream.writeShort(exceptionTableEntry.startPc);
        param1DataOutputStream.writeShort(exceptionTableEntry.endPc);
        param1DataOutputStream.writeShort(exceptionTableEntry.handlerPc);
        param1DataOutputStream.writeShort(exceptionTableEntry.catchType);
      } 
      param1DataOutputStream.writeShort(0);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8("Exceptions"));
      param1DataOutputStream.writeInt(2 + 2 * this.declaredExceptions.length);
      param1DataOutputStream.writeShort(this.declaredExceptions.length);
      for (short s : this.declaredExceptions)
        param1DataOutputStream.writeShort(s); 
    }
  }
  
  private static class PrimitiveTypeInfo {
    public String baseTypeString;
    
    public String wrapperClassName;
    
    public String wrapperValueOfDesc;
    
    public String unwrapMethodName;
    
    public String unwrapMethodDesc;
    
    private static Map<Class<?>, PrimitiveTypeInfo> table = new HashMap();
    
    private static void add(Class<?> param1Class1, Class<?> param1Class2) { table.put(param1Class1, new PrimitiveTypeInfo(param1Class1, param1Class2)); }
    
    private PrimitiveTypeInfo(Class<?> param1Class1, Class<?> param1Class2) {
      assert param1Class1.isPrimitive();
      this.baseTypeString = Array.newInstance(param1Class1, 0).getClass().getName().substring(1);
      this.wrapperClassName = ProxyGenerator.dotToSlash(param1Class2.getName());
      this.wrapperValueOfDesc = "(" + this.baseTypeString + ")L" + this.wrapperClassName + ";";
      this.unwrapMethodName = param1Class1.getName() + "Value";
      this.unwrapMethodDesc = "()" + this.baseTypeString;
    }
    
    public static PrimitiveTypeInfo get(Class<?> param1Class) { return (PrimitiveTypeInfo)table.get(param1Class); }
    
    static  {
      add(byte.class, Byte.class);
      add(char.class, Character.class);
      add(double.class, Double.class);
      add(float.class, Float.class);
      add(int.class, Integer.class);
      add(long.class, Long.class);
      add(short.class, Short.class);
      add(boolean.class, Boolean.class);
    }
  }
  
  private class ProxyMethod {
    public String methodName;
    
    public Class<?>[] parameterTypes;
    
    public Class<?> returnType;
    
    public Class<?>[] exceptionTypes;
    
    public Class<?> fromClass;
    
    public String methodFieldName;
    
    private ProxyMethod(String param1String, Class<?>[] param1ArrayOfClass1, Class<?> param1Class1, Class<?>[] param1ArrayOfClass2, Class<?> param1Class2) {
      this.methodName = param1String;
      this.parameterTypes = param1ArrayOfClass1;
      this.returnType = param1Class1;
      this.exceptionTypes = param1ArrayOfClass2;
      this.fromClass = param1Class2;
      this.methodFieldName = "m" + this$0.proxyMethodCount++;
    }
    
    private ProxyGenerator.MethodInfo generateMethod() throws IOException {
      String str = ProxyGenerator.getMethodDescriptor(this.parameterTypes, this.returnType);
      ProxyGenerator.MethodInfo methodInfo = new ProxyGenerator.MethodInfo(ProxyGenerator.this, this.methodName, str, 17);
      int[] arrayOfInt = new int[this.parameterTypes.length];
      int i = 1;
      int j;
      for (j = 0; j < arrayOfInt.length; j++) {
        arrayOfInt[j] = i;
        i += ProxyGenerator.getWordsPerType(this.parameterTypes[j]);
      } 
      j = i;
      int k = 0;
      DataOutputStream dataOutputStream = new DataOutputStream(methodInfo.code);
      ProxyGenerator.this.code_aload(0, dataOutputStream);
      dataOutputStream.writeByte(180);
      dataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef("java/lang/reflect/Proxy", "h", "Ljava/lang/reflect/InvocationHandler;"));
      ProxyGenerator.this.code_aload(0, dataOutputStream);
      dataOutputStream.writeByte(178);
      dataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(ProxyGenerator.dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
      if (this.parameterTypes.length > 0) {
        ProxyGenerator.this.code_ipush(this.parameterTypes.length, dataOutputStream);
        dataOutputStream.writeByte(189);
        dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Object"));
        for (byte b1 = 0; b1 < this.parameterTypes.length; b1++) {
          dataOutputStream.writeByte(89);
          ProxyGenerator.this.code_ipush(b1, dataOutputStream);
          codeWrapArgument(this.parameterTypes[b1], arrayOfInt[b1], dataOutputStream);
          dataOutputStream.writeByte(83);
        } 
      } else {
        dataOutputStream.writeByte(1);
      } 
      dataOutputStream.writeByte(185);
      dataOutputStream.writeShort(ProxyGenerator.this.cp.getInterfaceMethodRef("java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
      dataOutputStream.writeByte(4);
      dataOutputStream.writeByte(0);
      if (this.returnType == void.class) {
        dataOutputStream.writeByte(87);
        dataOutputStream.writeByte(177);
      } else {
        codeUnwrapReturnValue(this.returnType, dataOutputStream);
      } 
      short s1 = (short)methodInfo.code.size();
      short s2 = s1;
      List list = ProxyGenerator.computeUniqueCatchList(this.exceptionTypes);
      if (list.size() > 0) {
        for (Class clazz : list)
          methodInfo.exceptionTable.add(new ProxyGenerator.ExceptionTableEntry(k, s2, s1, ProxyGenerator.this.cp.getClass(ProxyGenerator.dotToSlash(clazz.getName())))); 
        dataOutputStream.writeByte(191);
        s1 = (short)methodInfo.code.size();
        methodInfo.exceptionTable.add(new ProxyGenerator.ExceptionTableEntry(k, s2, s1, ProxyGenerator.this.cp.getClass("java/lang/Throwable")));
        ProxyGenerator.this.code_astore(j, dataOutputStream);
        dataOutputStream.writeByte(187);
        dataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/reflect/UndeclaredThrowableException"));
        dataOutputStream.writeByte(89);
        ProxyGenerator.this.code_aload(j, dataOutputStream);
        dataOutputStream.writeByte(183);
        dataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/reflect/UndeclaredThrowableException", "<init>", "(Ljava/lang/Throwable;)V"));
        dataOutputStream.writeByte(191);
      } 
      if (methodInfo.code.size() > 65535)
        throw new IllegalArgumentException("code size limit exceeded"); 
      methodInfo.maxStack = 10;
      methodInfo.maxLocals = (short)(j + 1);
      methodInfo.declaredExceptions = new short[this.exceptionTypes.length];
      for (byte b = 0; b < this.exceptionTypes.length; b++)
        methodInfo.declaredExceptions[b] = ProxyGenerator.this.cp.getClass(ProxyGenerator.dotToSlash(this.exceptionTypes[b].getName())); 
      return methodInfo;
    }
    
    private void codeWrapArgument(Class<?> param1Class, int param1Int, DataOutputStream param1DataOutputStream) throws IOException {
      if (param1Class.isPrimitive()) {
        ProxyGenerator.PrimitiveTypeInfo primitiveTypeInfo = ProxyGenerator.PrimitiveTypeInfo.get(param1Class);
        if (param1Class == int.class || param1Class == boolean.class || param1Class == byte.class || param1Class == char.class || param1Class == short.class) {
          ProxyGenerator.this.code_iload(param1Int, param1DataOutputStream);
        } else if (param1Class == long.class) {
          ProxyGenerator.this.code_lload(param1Int, param1DataOutputStream);
        } else if (param1Class == float.class) {
          ProxyGenerator.this.code_fload(param1Int, param1DataOutputStream);
        } else if (param1Class == double.class) {
          ProxyGenerator.this.code_dload(param1Int, param1DataOutputStream);
        } else {
          throw new AssertionError();
        } 
        param1DataOutputStream.writeByte(184);
        param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef(primitiveTypeInfo.wrapperClassName, "valueOf", primitiveTypeInfo.wrapperValueOfDesc));
      } else {
        ProxyGenerator.this.code_aload(param1Int, param1DataOutputStream);
      } 
    }
    
    private void codeUnwrapReturnValue(Class<?> param1Class, DataOutputStream param1DataOutputStream) throws IOException {
      if (param1Class.isPrimitive()) {
        ProxyGenerator.PrimitiveTypeInfo primitiveTypeInfo = ProxyGenerator.PrimitiveTypeInfo.get(param1Class);
        param1DataOutputStream.writeByte(192);
        param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getClass(primitiveTypeInfo.wrapperClassName));
        param1DataOutputStream.writeByte(182);
        param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef(primitiveTypeInfo.wrapperClassName, primitiveTypeInfo.unwrapMethodName, primitiveTypeInfo.unwrapMethodDesc));
        if (param1Class == int.class || param1Class == boolean.class || param1Class == byte.class || param1Class == char.class || param1Class == short.class) {
          param1DataOutputStream.writeByte(172);
        } else if (param1Class == long.class) {
          param1DataOutputStream.writeByte(173);
        } else if (param1Class == float.class) {
          param1DataOutputStream.writeByte(174);
        } else if (param1Class == double.class) {
          param1DataOutputStream.writeByte(175);
        } else {
          throw new AssertionError();
        } 
      } else {
        param1DataOutputStream.writeByte(192);
        param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getClass(ProxyGenerator.dotToSlash(param1Class.getName())));
        param1DataOutputStream.writeByte(176);
      } 
    }
    
    private void codeFieldInitialization(DataOutputStream param1DataOutputStream) throws IOException {
      ProxyGenerator.this.codeClassForName(this.fromClass, param1DataOutputStream);
      ProxyGenerator.this.code_ldc(ProxyGenerator.this.cp.getString(this.methodName), param1DataOutputStream);
      ProxyGenerator.this.code_ipush(this.parameterTypes.length, param1DataOutputStream);
      param1DataOutputStream.writeByte(189);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Class"));
      for (byte b = 0; b < this.parameterTypes.length; b++) {
        param1DataOutputStream.writeByte(89);
        ProxyGenerator.this.code_ipush(b, param1DataOutputStream);
        if (this.parameterTypes[b].isPrimitive()) {
          ProxyGenerator.PrimitiveTypeInfo primitiveTypeInfo = ProxyGenerator.PrimitiveTypeInfo.get(this.parameterTypes[b]);
          param1DataOutputStream.writeByte(178);
          param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(primitiveTypeInfo.wrapperClassName, "TYPE", "Ljava/lang/Class;"));
        } else {
          ProxyGenerator.this.codeClassForName(this.parameterTypes[b], param1DataOutputStream);
        } 
        param1DataOutputStream.writeByte(83);
      } 
      param1DataOutputStream.writeByte(182);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"));
      param1DataOutputStream.writeByte(179);
      param1DataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(ProxyGenerator.dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ProxyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */