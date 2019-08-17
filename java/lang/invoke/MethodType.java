package java.lang.invoke;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.invoke.Invokers;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodTypeForm;
import java.lang.invoke.Stable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;

public final class MethodType implements Serializable {
  private static final long serialVersionUID = 292L;
  
  private final Class<?> rtype;
  
  private final Class<?>[] ptypes;
  
  @Stable
  private MethodTypeForm form;
  
  @Stable
  private MethodType wrapAlt;
  
  @Stable
  private Invokers invokers;
  
  @Stable
  private String methodDescriptor;
  
  static final int MAX_JVM_ARITY = 255;
  
  static final int MAX_MH_ARITY = 254;
  
  static final int MAX_MH_INVOKER_ARITY = 253;
  
  static final ConcurrentWeakInternSet<MethodType> internTable = new ConcurrentWeakInternSet();
  
  static final Class<?>[] NO_PTYPES = new Class[0];
  
  private static final MethodType[] objectOnlyTypes = new MethodType[20];
  
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
  
  private static final long rtypeOffset;
  
  private static final long ptypesOffset;
  
  private MethodType(Class<?> paramClass, Class<?>[] paramArrayOfClass, boolean paramBoolean) {
    checkRtype(paramClass);
    checkPtypes(paramArrayOfClass);
    this.rtype = paramClass;
    this.ptypes = paramBoolean ? paramArrayOfClass : (Class[])Arrays.copyOf(paramArrayOfClass, paramArrayOfClass.length);
  }
  
  private MethodType(Class<?>[] paramArrayOfClass, Class<?> paramClass) {
    this.rtype = paramClass;
    this.ptypes = paramArrayOfClass;
  }
  
  MethodTypeForm form() { return this.form; }
  
  Class<?> rtype() { return this.rtype; }
  
  Class<?>[] ptypes() { return this.ptypes; }
  
  void setForm(MethodTypeForm paramMethodTypeForm) { this.form = paramMethodTypeForm; }
  
  private static void checkRtype(Class<?> paramClass) { Objects.requireNonNull(paramClass); }
  
  private static void checkPtype(Class<?> paramClass) {
    Objects.requireNonNull(paramClass);
    if (paramClass == void.class)
      throw MethodHandleStatics.newIllegalArgumentException("parameter type cannot be void"); 
  }
  
  private static int checkPtypes(Class<?>[] paramArrayOfClass) {
    int i = 0;
    for (Class<?> clazz : paramArrayOfClass) {
      checkPtype(clazz);
      if (clazz == double.class || clazz == long.class)
        i++; 
    } 
    checkSlotCount(paramArrayOfClass.length + i);
    return i;
  }
  
  static void checkSlotCount(int paramInt) {
    if ((paramInt & 0xFF) != paramInt)
      throw MethodHandleStatics.newIllegalArgumentException("bad parameter count " + paramInt); 
  }
  
  private static IndexOutOfBoundsException newIndexOutOfBoundsException(Object paramObject) {
    if (paramObject instanceof Integer)
      paramObject = "bad index: " + paramObject; 
    return new IndexOutOfBoundsException(paramObject.toString());
  }
  
  public static MethodType methodType(Class<?> paramClass, Class<?>[] paramArrayOfClass) { return makeImpl(paramClass, paramArrayOfClass, false); }
  
  public static MethodType methodType(Class<?> paramClass, List<Class<?>> paramList) {
    boolean bool = false;
    return makeImpl(paramClass, listToArray(paramList), bool);
  }
  
  private static Class<?>[] listToArray(List<Class<?>> paramList) {
    checkSlotCount(paramList.size());
    return (Class[])paramList.toArray(NO_PTYPES);
  }
  
  public static MethodType methodType(Class<?> paramClass1, Class<?> paramClass2, Class<?>... paramVarArgs) {
    Class[] arrayOfClass = new Class[1 + paramVarArgs.length];
    arrayOfClass[0] = paramClass2;
    System.arraycopy(paramVarArgs, 0, arrayOfClass, 1, paramVarArgs.length);
    return makeImpl(paramClass1, arrayOfClass, true);
  }
  
  public static MethodType methodType(Class<?> paramClass) { return makeImpl(paramClass, NO_PTYPES, true); }
  
  public static MethodType methodType(Class<?> paramClass1, Class<?> paramClass2) { return makeImpl(paramClass1, new Class[] { paramClass2 }, true); }
  
  public static MethodType methodType(Class<?> paramClass, MethodType paramMethodType) { return makeImpl(paramClass, paramMethodType.ptypes, true); }
  
  static MethodType makeImpl(Class<?> paramClass, Class<?>[] paramArrayOfClass, boolean paramBoolean) {
    MethodType methodType = (MethodType)internTable.get(new MethodType(paramArrayOfClass, paramClass));
    if (methodType != null)
      return methodType; 
    if (paramArrayOfClass.length == 0) {
      paramArrayOfClass = NO_PTYPES;
      paramBoolean = true;
    } 
    methodType = new MethodType(paramClass, paramArrayOfClass, paramBoolean);
    methodType.form = MethodTypeForm.findForm(methodType);
    return (MethodType)internTable.add(methodType);
  }
  
  public static MethodType genericMethodType(int paramInt, boolean paramBoolean) {
    checkSlotCount(paramInt);
    int i = !paramBoolean ? 0 : 1;
    int j = paramInt * 2 + i;
    if (j < objectOnlyTypes.length) {
      MethodType methodType1 = objectOnlyTypes[j];
      if (methodType1 != null)
        return methodType1; 
    } 
    Class[] arrayOfClass = new Class[paramInt + i];
    Arrays.fill(arrayOfClass, Object.class);
    if (i != 0)
      arrayOfClass[paramInt] = Object[].class; 
    MethodType methodType = makeImpl(Object.class, arrayOfClass, true);
    if (j < objectOnlyTypes.length)
      objectOnlyTypes[j] = methodType; 
    return methodType;
  }
  
  public static MethodType genericMethodType(int paramInt) { return genericMethodType(paramInt, false); }
  
  public MethodType changeParameterType(int paramInt, Class<?> paramClass) {
    if (parameterType(paramInt) == paramClass)
      return this; 
    checkPtype(paramClass);
    Class[] arrayOfClass = (Class[])this.ptypes.clone();
    arrayOfClass[paramInt] = paramClass;
    return makeImpl(this.rtype, arrayOfClass, true);
  }
  
  public MethodType insertParameterTypes(int paramInt, Class<?>... paramVarArgs) {
    int i = this.ptypes.length;
    if (paramInt < 0 || paramInt > i)
      throw newIndexOutOfBoundsException(Integer.valueOf(paramInt)); 
    int j = checkPtypes(paramVarArgs);
    checkSlotCount(parameterSlotCount() + paramVarArgs.length + j);
    int k = paramVarArgs.length;
    if (k == 0)
      return this; 
    Class[] arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, 0, i + k);
    System.arraycopy(arrayOfClass, paramInt, arrayOfClass, paramInt + k, i - paramInt);
    System.arraycopy(paramVarArgs, 0, arrayOfClass, paramInt, k);
    return makeImpl(this.rtype, arrayOfClass, true);
  }
  
  public MethodType appendParameterTypes(Class<?>... paramVarArgs) { return insertParameterTypes(parameterCount(), paramVarArgs); }
  
  public MethodType insertParameterTypes(int paramInt, List<Class<?>> paramList) { return insertParameterTypes(paramInt, listToArray(paramList)); }
  
  public MethodType appendParameterTypes(List<Class<?>> paramList) { return insertParameterTypes(parameterCount(), paramList); }
  
  MethodType replaceParameterTypes(int paramInt1, int paramInt2, Class<?>... paramVarArgs) {
    if (paramInt1 == paramInt2)
      return insertParameterTypes(paramInt1, paramVarArgs); 
    int i = this.ptypes.length;
    if (0 > paramInt1 || paramInt1 > paramInt2 || paramInt2 > i)
      throw newIndexOutOfBoundsException("start=" + paramInt1 + " end=" + paramInt2); 
    int j = paramVarArgs.length;
    return (j == 0) ? dropParameterTypes(paramInt1, paramInt2) : dropParameterTypes(paramInt1, paramInt2).insertParameterTypes(paramInt1, paramVarArgs);
  }
  
  MethodType asSpreaderType(Class<?> paramClass, int paramInt) {
    assert parameterCount() >= paramInt;
    int i = this.ptypes.length - paramInt;
    if (paramInt == 0)
      return this; 
    if (paramClass == Object[].class) {
      if (isGeneric())
        return this; 
      if (i == 0) {
        MethodType methodType = genericMethodType(paramInt);
        if (this.rtype != Object.class)
          methodType = methodType.changeReturnType(this.rtype); 
        return methodType;
      } 
    } 
    Class clazz = paramClass.getComponentType();
    assert clazz != null;
    for (int j = i; j < this.ptypes.length; j++) {
      if (this.ptypes[j] != clazz) {
        Class[] arrayOfClass = (Class[])this.ptypes.clone();
        Arrays.fill(arrayOfClass, j, this.ptypes.length, clazz);
        return methodType(this.rtype, arrayOfClass);
      } 
    } 
    return this;
  }
  
  Class<?> leadingReferenceParameter() {
    Class clazz;
    if (this.ptypes.length == 0 || (clazz = this.ptypes[0]).isPrimitive())
      throw MethodHandleStatics.newIllegalArgumentException("no leading reference parameter"); 
    return clazz;
  }
  
  MethodType asCollectorType(Class<?> paramClass, int paramInt) {
    MethodType methodType;
    assert parameterCount() >= 1;
    assert lastParameterType().isAssignableFrom(paramClass);
    if (paramClass == Object[].class) {
      methodType = genericMethodType(paramInt);
      if (this.rtype != Object.class)
        methodType = methodType.changeReturnType(this.rtype); 
    } else {
      Class clazz = paramClass.getComponentType();
      assert clazz != null;
      methodType = methodType(this.rtype, Collections.nCopies(paramInt, clazz));
    } 
    return (this.ptypes.length == 1) ? methodType : methodType.insertParameterTypes(0, parameterList().subList(0, this.ptypes.length - 1));
  }
  
  public MethodType dropParameterTypes(int paramInt1, int paramInt2) {
    Class[] arrayOfClass;
    int i = this.ptypes.length;
    if (0 > paramInt1 || paramInt1 > paramInt2 || paramInt2 > i)
      throw newIndexOutOfBoundsException("start=" + paramInt1 + " end=" + paramInt2); 
    if (paramInt1 == paramInt2)
      return this; 
    if (paramInt1 == 0) {
      if (paramInt2 == i) {
        arrayOfClass = NO_PTYPES;
      } else {
        arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, paramInt2, i);
      } 
    } else if (paramInt2 == i) {
      arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, 0, paramInt1);
    } else {
      int j = i - paramInt2;
      arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, 0, paramInt1 + j);
      System.arraycopy(this.ptypes, paramInt2, arrayOfClass, paramInt1, j);
    } 
    return makeImpl(this.rtype, arrayOfClass, true);
  }
  
  public MethodType changeReturnType(Class<?> paramClass) { return (returnType() == paramClass) ? this : makeImpl(paramClass, this.ptypes, true); }
  
  public boolean hasPrimitives() { return this.form.hasPrimitives(); }
  
  public boolean hasWrappers() { return (unwrap() != this); }
  
  public MethodType erase() { return this.form.erasedType(); }
  
  MethodType basicType() { return this.form.basicType(); }
  
  MethodType invokerType() { return insertParameterTypes(0, new Class[] { MethodHandle.class }); }
  
  public MethodType generic() { return genericMethodType(parameterCount()); }
  
  boolean isGeneric() { return (this == erase() && !hasPrimitives()); }
  
  public MethodType wrap() { return hasPrimitives() ? wrapWithPrims(this) : this; }
  
  public MethodType unwrap() {
    MethodType methodType;
    return (methodType = !hasPrimitives() ? this : wrapWithPrims(this)).unwrapWithNoPrims(methodType);
  }
  
  private static MethodType wrapWithPrims(MethodType paramMethodType) {
    assert paramMethodType.hasPrimitives();
    MethodType methodType = paramMethodType.wrapAlt;
    if (methodType == null) {
      methodType = MethodTypeForm.canonicalize(paramMethodType, 2, 2);
      assert methodType != null;
      paramMethodType.wrapAlt = methodType;
    } 
    return methodType;
  }
  
  private static MethodType unwrapWithNoPrims(MethodType paramMethodType) {
    assert !paramMethodType.hasPrimitives();
    MethodType methodType = paramMethodType.wrapAlt;
    if (methodType == null) {
      methodType = MethodTypeForm.canonicalize(paramMethodType, 3, 3);
      if (methodType == null)
        methodType = paramMethodType; 
      paramMethodType.wrapAlt = methodType;
    } 
    return methodType;
  }
  
  public Class<?> parameterType(int paramInt) { return this.ptypes[paramInt]; }
  
  public int parameterCount() { return this.ptypes.length; }
  
  public Class<?> returnType() { return this.rtype; }
  
  public List<Class<?>> parameterList() { return Collections.unmodifiableList(Arrays.asList((Object[])this.ptypes.clone())); }
  
  Class<?> lastParameterType() {
    int i = this.ptypes.length;
    return (i == 0) ? void.class : this.ptypes[i - 1];
  }
  
  public Class<?>[] parameterArray() { return (Class[])this.ptypes.clone(); }
  
  public boolean equals(Object paramObject) { return (this == paramObject || (paramObject instanceof MethodType && equals((MethodType)paramObject))); }
  
  private boolean equals(MethodType paramMethodType) { return (this.rtype == paramMethodType.rtype && Arrays.equals(this.ptypes, paramMethodType.ptypes)); }
  
  public int hashCode() {
    int i = 31 + this.rtype.hashCode();
    for (Class clazz : this.ptypes)
      i = 31 * i + clazz.hashCode(); 
    return i;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(");
    for (byte b = 0; b < this.ptypes.length; b++) {
      if (b)
        stringBuilder.append(","); 
      stringBuilder.append(this.ptypes[b].getSimpleName());
    } 
    stringBuilder.append(")");
    stringBuilder.append(this.rtype.getSimpleName());
    return stringBuilder.toString();
  }
  
  boolean isViewableAs(MethodType paramMethodType, boolean paramBoolean) { return !VerifyType.isNullConversion(returnType(), paramMethodType.returnType(), paramBoolean) ? false : parametersAreViewableAs(paramMethodType, paramBoolean); }
  
  boolean parametersAreViewableAs(MethodType paramMethodType, boolean paramBoolean) {
    if (this.form == paramMethodType.form && this.form.erasedType == this)
      return true; 
    if (this.ptypes == paramMethodType.ptypes)
      return true; 
    int i = parameterCount();
    if (i != paramMethodType.parameterCount())
      return false; 
    for (byte b = 0; b < i; b++) {
      if (!VerifyType.isNullConversion(paramMethodType.parameterType(b), parameterType(b), paramBoolean))
        return false; 
    } 
    return true;
  }
  
  boolean isConvertibleTo(MethodType paramMethodType) {
    MethodTypeForm methodTypeForm1 = form();
    MethodTypeForm methodTypeForm2 = paramMethodType.form();
    if (methodTypeForm1 == methodTypeForm2)
      return true; 
    if (!canConvert(returnType(), paramMethodType.returnType()))
      return false; 
    Class[] arrayOfClass1 = paramMethodType.ptypes;
    Class[] arrayOfClass2 = this.ptypes;
    if (arrayOfClass1 == arrayOfClass2)
      return true; 
    int i;
    if ((i = arrayOfClass1.length) != arrayOfClass2.length)
      return false; 
    if (i <= 1)
      return !(i == 1 && !canConvert(arrayOfClass1[0], arrayOfClass2[0])); 
    if ((methodTypeForm1.primitiveParameterCount() == 0 && methodTypeForm1.erasedType == this) || (methodTypeForm2.primitiveParameterCount() == 0 && methodTypeForm2.erasedType == paramMethodType)) {
      assert canConvertParameters(arrayOfClass1, arrayOfClass2);
      return true;
    } 
    return canConvertParameters(arrayOfClass1, arrayOfClass2);
  }
  
  boolean explicitCastEquivalentToAsType(MethodType paramMethodType) {
    if (this == paramMethodType)
      return true; 
    if (!explicitCastEquivalentToAsType(this.rtype, paramMethodType.rtype))
      return false; 
    Class[] arrayOfClass1 = paramMethodType.ptypes;
    Class[] arrayOfClass2 = this.ptypes;
    if (arrayOfClass2 == arrayOfClass1)
      return true; 
    assert arrayOfClass2.length == arrayOfClass1.length;
    for (byte b = 0; b < arrayOfClass2.length; b++) {
      if (!explicitCastEquivalentToAsType(arrayOfClass1[b], arrayOfClass2[b]))
        return false; 
    } 
    return true;
  }
  
  private static boolean explicitCastEquivalentToAsType(Class<?> paramClass1, Class<?> paramClass2) { return (paramClass1 == paramClass2 || paramClass2 == Object.class || paramClass2 == void.class) ? true : (paramClass1.isPrimitive() ? canConvert(paramClass1, paramClass2) : (paramClass2.isPrimitive() ? 0 : ((!paramClass2.isInterface() || paramClass2.isAssignableFrom(paramClass1)) ? 1 : 0))); }
  
  private boolean canConvertParameters(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2) {
    for (byte b = 0; b < paramArrayOfClass1.length; b++) {
      if (!canConvert(paramArrayOfClass1[b], paramArrayOfClass2[b]))
        return false; 
    } 
    return true;
  }
  
  static boolean canConvert(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2 || paramClass1 == Object.class || paramClass2 == Object.class)
      return true; 
    if (paramClass1.isPrimitive()) {
      if (paramClass1 == void.class)
        return true; 
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass1);
      return paramClass2.isPrimitive() ? Wrapper.forPrimitiveType(paramClass2).isConvertibleFrom(wrapper) : paramClass2.isAssignableFrom(wrapper.wrapperType());
    } 
    if (paramClass2.isPrimitive()) {
      if (paramClass2 == void.class)
        return true; 
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass2);
      return paramClass1.isAssignableFrom(wrapper.wrapperType()) ? true : ((Wrapper.isWrapperType(paramClass1) && wrapper.isConvertibleFrom(Wrapper.forWrapperType(paramClass1))));
    } 
    return true;
  }
  
  int parameterSlotCount() { return this.form.parameterSlotCount(); }
  
  Invokers invokers() {
    Invokers invokers1 = this.invokers;
    if (invokers1 != null)
      return invokers1; 
    this.invokers = invokers1 = new Invokers(this);
    return invokers1;
  }
  
  int parameterSlotDepth(int paramInt) {
    if (paramInt < 0 || paramInt > this.ptypes.length)
      parameterType(paramInt); 
    return this.form.parameterToArgSlot(paramInt - 1);
  }
  
  int returnSlotCount() { return this.form.returnSlotCount(); }
  
  public static MethodType fromMethodDescriptorString(String paramString, ClassLoader paramClassLoader) throws IllegalArgumentException, TypeNotPresentException {
    if (!paramString.startsWith("(") || paramString.indexOf(')') < 0 || paramString.indexOf('.') >= 0)
      throw MethodHandleStatics.newIllegalArgumentException("not a method descriptor: " + paramString); 
    List list = BytecodeDescriptor.parseMethod(paramString, paramClassLoader);
    Class clazz = (Class)list.remove(list.size() - 1);
    checkSlotCount(list.size());
    Class[] arrayOfClass = listToArray(list);
    return makeImpl(clazz, arrayOfClass, true);
  }
  
  public String toMethodDescriptorString() {
    String str = this.methodDescriptor;
    if (str == null) {
      str = BytecodeDescriptor.unparse(this);
      this.methodDescriptor = str;
    } 
    return str;
  }
  
  static String toFieldDescriptorString(Class<?> paramClass) { return BytecodeDescriptor.unparse(paramClass); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(returnType());
    paramObjectOutputStream.writeObject(parameterArray());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    MethodType_init(void.class, NO_PTYPES);
    paramObjectInputStream.defaultReadObject();
    Class clazz = (Class)paramObjectInputStream.readObject();
    Class[] arrayOfClass = (Class[])paramObjectInputStream.readObject();
    arrayOfClass = (Class[])arrayOfClass.clone();
    MethodType_init(clazz, arrayOfClass);
  }
  
  private void MethodType_init(Class<?> paramClass, Class<?>[] paramArrayOfClass) {
    checkRtype(paramClass);
    checkPtypes(paramArrayOfClass);
    MethodHandleStatics.UNSAFE.putObject(this, rtypeOffset, paramClass);
    MethodHandleStatics.UNSAFE.putObject(this, ptypesOffset, paramArrayOfClass);
  }
  
  private Object readResolve() {
    try {
      return methodType(this.rtype, this.ptypes);
    } finally {
      MethodType_init(void.class, NO_PTYPES);
    } 
  }
  
  static  {
    try {
      rtypeOffset = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodType.class.getDeclaredField("rtype"));
      ptypesOffset = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodType.class.getDeclaredField("ptypes"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  private static class ConcurrentWeakInternSet<T> extends Object {
    private final ConcurrentMap<WeakEntry<T>, WeakEntry<T>> map = new ConcurrentHashMap();
    
    private final ReferenceQueue<T> stale = new ReferenceQueue();
    
    public T get(T param1T) {
      if (param1T == null)
        throw new NullPointerException(); 
      expungeStaleElements();
      WeakEntry weakEntry = (WeakEntry)this.map.get(new WeakEntry(param1T));
      if (weakEntry != null) {
        Object object = weakEntry.get();
        if (object != null)
          return (T)object; 
      } 
      return null;
    }
    
    public T add(T param1T) {
      T t;
      if (param1T == null)
        throw new NullPointerException(); 
      WeakEntry weakEntry = new WeakEntry(param1T, this.stale);
      do {
        expungeStaleElements();
        WeakEntry weakEntry1 = (WeakEntry)this.map.putIfAbsent(weakEntry, weakEntry);
        t = (weakEntry1 == null) ? param1T : weakEntry1.get();
      } while (t == null);
      return t;
    }
    
    private void expungeStaleElements() {
      Reference reference;
      while ((reference = this.stale.poll()) != null)
        this.map.remove(reference); 
    }
    
    private static class WeakEntry<T> extends WeakReference<T> {
      public final int hashcode;
      
      public WeakEntry(T param2T, ReferenceQueue<T> param2ReferenceQueue) {
        super(param2T, param2ReferenceQueue);
        this.hashcode = param2T.hashCode();
      }
      
      public WeakEntry(T param2T) {
        super(param2T);
        this.hashcode = param2T.hashCode();
      }
      
      public boolean equals(Object param2Object) {
        if (param2Object instanceof WeakEntry) {
          Object object1 = ((WeakEntry)param2Object).get();
          Object object2 = get();
          return (object1 == null || object2 == null) ? ((this == param2Object)) : object2.equals(object1);
        } 
        return false;
      }
      
      public int hashCode() { return this.hashcode; }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */