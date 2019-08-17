package java.lang.invoke;

import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyAccess;

final class MemberName implements Member, Cloneable {
  private Class<?> clazz;
  
  private String name;
  
  private Object type;
  
  private int flags;
  
  private Object resolution;
  
  private static final int MH_INVOKE_MODS = 273;
  
  static final int BRIDGE = 64;
  
  static final int VARARGS = 128;
  
  static final int SYNTHETIC = 4096;
  
  static final int ANNOTATION = 8192;
  
  static final int ENUM = 16384;
  
  static final String CONSTRUCTOR_NAME = "<init>";
  
  static final int RECOGNIZED_MODIFIERS = 65535;
  
  static final int IS_METHOD = 65536;
  
  static final int IS_CONSTRUCTOR = 131072;
  
  static final int IS_FIELD = 262144;
  
  static final int IS_TYPE = 524288;
  
  static final int CALLER_SENSITIVE = 1048576;
  
  static final int ALL_ACCESS = 7;
  
  static final int ALL_KINDS = 983040;
  
  static final int IS_INVOCABLE = 196608;
  
  static final int IS_FIELD_OR_METHOD = 327680;
  
  static final int SEARCH_ALL_SUPERS = 3145728;
  
  public Class<?> getDeclaringClass() { return this.clazz; }
  
  public ClassLoader getClassLoader() { return this.clazz.getClassLoader(); }
  
  public String getName() {
    if (this.name == null) {
      expandFromVM();
      if (this.name == null)
        return null; 
    } 
    return this.name;
  }
  
  public MethodType getMethodOrFieldType() {
    if (isInvocable())
      return getMethodType(); 
    if (isGetter())
      return MethodType.methodType(getFieldType()); 
    if (isSetter())
      return MethodType.methodType(void.class, getFieldType()); 
    throw new InternalError("not a method or field: " + this);
  }
  
  public MethodType getMethodType() {
    if (this.type == null) {
      expandFromVM();
      if (this.type == null)
        return null; 
    } 
    if (!isInvocable())
      throw MethodHandleStatics.newIllegalArgumentException("not invocable, no method type"); 
    null = this.type;
    if (null instanceof MethodType)
      return (MethodType)null; 
    synchronized (this) {
      if (this.type instanceof String) {
        String str = (String)this.type;
        MethodType methodType = MethodType.fromMethodDescriptorString(str, getClassLoader());
        this.type = methodType;
      } else if (this.type instanceof Object[]) {
        Object[] arrayOfObject = (Object[])this.type;
        Class[] arrayOfClass = (Class[])arrayOfObject[1];
        Class clazz1 = (Class)arrayOfObject[0];
        MethodType methodType = MethodType.methodType(clazz1, arrayOfClass);
        this.type = methodType;
      } 
      assert this.type instanceof MethodType : "bad method type " + this.type;
    } 
    return (MethodType)this.type;
  }
  
  public MethodType getInvocationType() {
    MethodType methodType = getMethodOrFieldType();
    return (isConstructor() && getReferenceKind() == 8) ? methodType.changeReturnType(this.clazz) : (!isStatic() ? methodType.insertParameterTypes(0, new Class[] { this.clazz }) : methodType);
  }
  
  public Class<?>[] getParameterTypes() { return getMethodType().parameterArray(); }
  
  public Class<?> getReturnType() { return getMethodType().returnType(); }
  
  public Class<?> getFieldType() {
    if (this.type == null) {
      expandFromVM();
      if (this.type == null)
        return null; 
    } 
    if (isInvocable())
      throw MethodHandleStatics.newIllegalArgumentException("not a field or nested class, no simple type"); 
    null = this.type;
    if (null instanceof Class)
      return (Class)null; 
    synchronized (this) {
      if (this.type instanceof String) {
        String str = (String)this.type;
        MethodType methodType = MethodType.fromMethodDescriptorString("()" + str, getClassLoader());
        Class clazz1 = methodType.returnType();
        this.type = clazz1;
      } 
      assert this.type instanceof Class : "bad field type " + this.type;
    } 
    return (Class)this.type;
  }
  
  public Object getType() { return isInvocable() ? getMethodType() : getFieldType(); }
  
  public String getSignature() {
    if (this.type == null) {
      expandFromVM();
      if (this.type == null)
        return null; 
    } 
    return isInvocable() ? BytecodeDescriptor.unparse(getMethodType()) : BytecodeDescriptor.unparse(getFieldType());
  }
  
  public int getModifiers() { return this.flags & 0xFFFF; }
  
  public byte getReferenceKind() { return (byte)(this.flags >>> 24 & 0xF); }
  
  private boolean referenceKindIsConsistent() {
    byte b = getReferenceKind();
    if (b == 0)
      return isType(); 
    if (isField()) {
      assert staticIsConsistent();
      assert MethodHandleNatives.refKindIsField(b);
    } else if (isConstructor()) {
      assert b == 8 || b == 7;
    } else if (isMethod()) {
      assert staticIsConsistent();
      assert MethodHandleNatives.refKindIsMethod(b);
      if (this.clazz.isInterface() && !$assertionsDisabled && b != 9 && b != 6 && b != 7 && (b != 5 || !isObjectPublicMethod()))
        throw new AssertionError(); 
    } else {
      assert false;
    } 
    return true;
  }
  
  private boolean isObjectPublicMethod() {
    if (this.clazz == Object.class)
      return true; 
    MethodType methodType = getMethodType();
    return (this.name.equals("toString") && methodType.returnType() == String.class && methodType.parameterCount() == 0) ? true : ((this.name.equals("hashCode") && methodType.returnType() == int.class && methodType.parameterCount() == 0) ? true : ((this.name.equals("equals") && methodType.returnType() == boolean.class && methodType.parameterCount() == 1 && methodType.parameterType(false) == Object.class)));
  }
  
  boolean referenceKindIsConsistentWith(int paramInt) {
    byte b = getReferenceKind();
    if (b == paramInt)
      return true; 
    switch (paramInt) {
      case 9:
        assert b == 5 || b == 7 : this;
        return true;
      case 5:
      case 8:
        assert b == 7 : this;
        return true;
    } 
    assert false : this + " != " + MethodHandleNatives.refKindName((byte)paramInt);
    return true;
  }
  
  private boolean staticIsConsistent() {
    byte b = getReferenceKind();
    return (MethodHandleNatives.refKindIsStatic(b) == isStatic() || getModifiers() == 0);
  }
  
  private boolean vminfoIsConsistent() {
    byte b = getReferenceKind();
    assert isResolved();
    Object object1 = MethodHandleNatives.getMemberVMInfo(this);
    assert object1 instanceof Object[];
    long l = ((Long)(Object[])object1[0]).longValue();
    Object object2 = (Object[])object1[1];
    if (MethodHandleNatives.refKindIsField(b)) {
      assert l >= 0L : l + ":" + this;
      assert object2 instanceof Class;
    } else {
      if (MethodHandleNatives.refKindDoesDispatch(b)) {
        assert l >= 0L : l + ":" + this;
      } else {
        assert l < 0L : l;
      } 
      assert object2 instanceof MemberName : object2 + " in " + this;
    } 
    return true;
  }
  
  private MemberName changeReferenceKind(byte paramByte1, byte paramByte2) {
    assert getReferenceKind() == paramByte2;
    assert MethodHandleNatives.refKindIsValid(paramByte1);
    this.flags += (paramByte1 - paramByte2 << 24);
    return this;
  }
  
  private boolean testFlags(int paramInt1, int paramInt2) { return ((this.flags & paramInt1) == paramInt2); }
  
  private boolean testAllFlags(int paramInt) { return testFlags(paramInt, paramInt); }
  
  private boolean testAnyFlags(int paramInt) { return !testFlags(paramInt, 0); }
  
  public boolean isMethodHandleInvoke() { return (testFlags(280, 272) && this.clazz == MethodHandle.class) ? isMethodHandleInvokeName(this.name) : 0; }
  
  public static boolean isMethodHandleInvokeName(String paramString) {
    switch (paramString) {
      case "invoke":
      case "invokeExact":
        return true;
    } 
    return false;
  }
  
  public boolean isStatic() { return Modifier.isStatic(this.flags); }
  
  public boolean isPublic() { return Modifier.isPublic(this.flags); }
  
  public boolean isPrivate() { return Modifier.isPrivate(this.flags); }
  
  public boolean isProtected() { return Modifier.isProtected(this.flags); }
  
  public boolean isFinal() { return Modifier.isFinal(this.flags); }
  
  public boolean canBeStaticallyBound() { return Modifier.isFinal(this.flags | this.clazz.getModifiers()); }
  
  public boolean isVolatile() { return Modifier.isVolatile(this.flags); }
  
  public boolean isAbstract() { return Modifier.isAbstract(this.flags); }
  
  public boolean isNative() { return Modifier.isNative(this.flags); }
  
  public boolean isBridge() { return testAllFlags(65600); }
  
  public boolean isVarargs() { return (testAllFlags(128) && isInvocable()); }
  
  public boolean isSynthetic() { return testAllFlags(4096); }
  
  public boolean isInvocable() { return testAnyFlags(196608); }
  
  public boolean isFieldOrMethod() { return testAnyFlags(327680); }
  
  public boolean isMethod() { return testAllFlags(65536); }
  
  public boolean isConstructor() { return testAllFlags(131072); }
  
  public boolean isField() { return testAllFlags(262144); }
  
  public boolean isType() { return testAllFlags(524288); }
  
  public boolean isPackage() { return !testAnyFlags(7); }
  
  public boolean isCallerSensitive() { return testAllFlags(1048576); }
  
  public boolean isAccessibleFrom(Class<?> paramClass) { return VerifyAccess.isMemberAccessible(getDeclaringClass(), getDeclaringClass(), this.flags, paramClass, 15); }
  
  private void init(Class<?> paramClass, String paramString, Object paramObject, int paramInt) {
    this.clazz = paramClass;
    this.name = paramString;
    this.type = paramObject;
    this.flags = paramInt;
    assert testAnyFlags(983040);
    assert this.resolution == null;
  }
  
  private void expandFromVM() {
    if (this.type != null)
      return; 
    if (!isResolved())
      return; 
    MethodHandleNatives.expand(this);
  }
  
  private static int flagsMods(int paramInt1, int paramInt2, byte paramByte) {
    assert (paramInt1 & 0xFFFF) == 0;
    assert (paramInt2 & 0xFFFF0000) == 0;
    assert (paramByte & 0xFFFFFFF0) == 0;
    return paramInt1 | paramInt2 | paramByte << 24;
  }
  
  public MemberName(Method paramMethod) { this(paramMethod, false); }
  
  public MemberName(Method paramMethod, boolean paramBoolean) {
    paramMethod.getClass();
    MethodHandleNatives.init(this, paramMethod);
    if (this.clazz == null) {
      if (paramMethod.getDeclaringClass() == MethodHandle.class && isMethodHandleInvokeName(paramMethod.getName())) {
        MethodType methodType = MethodType.methodType(paramMethod.getReturnType(), paramMethod.getParameterTypes());
        int i = flagsMods(65536, paramMethod.getModifiers(), (byte)5);
        init(MethodHandle.class, paramMethod.getName(), methodType, i);
        if (isMethodHandleInvoke())
          return; 
      } 
      throw new LinkageError(paramMethod.toString());
    } 
    assert isResolved() && this.clazz != null;
    this.name = paramMethod.getName();
    if (this.type == null)
      this.type = new Object[] { paramMethod.getReturnType(), paramMethod.getParameterTypes() }; 
    if (paramBoolean) {
      if (isAbstract())
        throw new AbstractMethodError(toString()); 
      if (getReferenceKind() == 5) {
        changeReferenceKind((byte)7, (byte)5);
      } else if (getReferenceKind() == 9) {
        changeReferenceKind((byte)7, (byte)9);
      } 
    } 
  }
  
  public MemberName asSpecial() {
    switch (getReferenceKind()) {
      case 7:
        return this;
      case 5:
        return clone().changeReferenceKind((byte)7, (byte)5);
      case 9:
        return clone().changeReferenceKind((byte)7, (byte)9);
      case 8:
        return clone().changeReferenceKind((byte)7, (byte)8);
    } 
    throw new IllegalArgumentException(toString());
  }
  
  public MemberName asConstructor() {
    switch (getReferenceKind()) {
      case 7:
        return clone().changeReferenceKind((byte)8, (byte)7);
      case 8:
        return this;
    } 
    throw new IllegalArgumentException(toString());
  }
  
  public MemberName asNormalOriginal() {
    byte b = this.clazz.isInterface() ? 9 : 5;
    byte b1 = getReferenceKind();
    byte b2 = b1;
    MemberName memberName = this;
    switch (b1) {
      case 5:
      case 7:
      case 9:
        b2 = b;
        break;
    } 
    if (b2 == b1)
      return this; 
    memberName = clone().changeReferenceKind(b2, b1);
    assert referenceKindIsConsistentWith(memberName.getReferenceKind());
    return memberName;
  }
  
  public MemberName(Constructor<?> paramConstructor) {
    paramConstructor.getClass();
    MethodHandleNatives.init(this, paramConstructor);
    assert isResolved() && this.clazz != null;
    this.name = "<init>";
    if (this.type == null)
      this.type = new Object[] { void.class, paramConstructor.getParameterTypes() }; 
  }
  
  public MemberName(Field paramField) { this(paramField, false); }
  
  public MemberName(Field paramField, boolean paramBoolean) {
    paramField.getClass();
    MethodHandleNatives.init(this, paramField);
    assert isResolved() && this.clazz != null;
    this.name = paramField.getName();
    this.type = paramField.getType();
    byte b = getReferenceKind();
    assert b == (isStatic() ? 2 : 1);
    if (paramBoolean)
      changeReferenceKind((byte)(b + 2), b); 
  }
  
  public boolean isGetter() { return MethodHandleNatives.refKindIsGetter(getReferenceKind()); }
  
  public boolean isSetter() { return MethodHandleNatives.refKindIsSetter(getReferenceKind()); }
  
  public MemberName asSetter() {
    byte b1 = getReferenceKind();
    assert MethodHandleNatives.refKindIsGetter(b1);
    byte b2 = (byte)(b1 + 2);
    return clone().changeReferenceKind(b2, b1);
  }
  
  public MemberName(Class<?> paramClass) {
    init(paramClass.getDeclaringClass(), paramClass.getSimpleName(), paramClass, flagsMods(524288, paramClass.getModifiers(), (byte)0));
    initResolved(true);
  }
  
  static MemberName makeMethodHandleInvoke(String paramString, MethodType paramMethodType) { return makeMethodHandleInvoke(paramString, paramMethodType, 4369); }
  
  static MemberName makeMethodHandleInvoke(String paramString, MethodType paramMethodType, int paramInt) {
    MemberName memberName = new MemberName(MethodHandle.class, paramString, paramMethodType, (byte)5);
    memberName.flags |= paramInt;
    assert memberName.isMethodHandleInvoke() : memberName;
    return memberName;
  }
  
  MemberName() {}
  
  protected MemberName clone() {
    try {
      return (MemberName)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw MethodHandleStatics.newInternalError(cloneNotSupportedException);
    } 
  }
  
  public MemberName getDefinition() {
    if (!isResolved())
      throw new IllegalStateException("must be resolved: " + this); 
    if (isType())
      return this; 
    MemberName memberName = clone();
    memberName.clazz = null;
    memberName.type = null;
    memberName.name = null;
    memberName.resolution = memberName;
    memberName.expandFromVM();
    assert memberName.getName().equals(getName());
    return memberName;
  }
  
  public int hashCode() { return Objects.hash(new Object[] { this.clazz, Byte.valueOf(getReferenceKind()), this.name, getType() }); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof MemberName && equals((MemberName)paramObject)); }
  
  public boolean equals(MemberName paramMemberName) { return (this == paramMemberName) ? true : ((paramMemberName == null) ? false : ((this.clazz == paramMemberName.clazz && getReferenceKind() == paramMemberName.getReferenceKind() && Objects.equals(this.name, paramMemberName.name) && Objects.equals(getType(), paramMemberName.getType())))); }
  
  public MemberName(Class<?> paramClass1, String paramString, Class<?> paramClass2, byte paramByte) {
    init(paramClass1, paramString, paramClass2, flagsMods(262144, 0, paramByte));
    initResolved(false);
  }
  
  public MemberName(Class<?> paramClass, String paramString, MethodType paramMethodType, byte paramByte) {
    int i = (paramString != null && paramString.equals("<init>")) ? 131072 : 65536;
    init(paramClass, paramString, paramMethodType, flagsMods(i, 0, paramByte));
    initResolved(false);
  }
  
  public MemberName(byte paramByte, Class<?> paramClass, String paramString, Object paramObject) {
    if (MethodHandleNatives.refKindIsField(paramByte)) {
      i = 262144;
      if (!(paramObject instanceof Class))
        throw MethodHandleStatics.newIllegalArgumentException("not a field type"); 
    } else if (MethodHandleNatives.refKindIsMethod(paramByte)) {
      i = 65536;
      if (!(paramObject instanceof MethodType))
        throw MethodHandleStatics.newIllegalArgumentException("not a method type"); 
    } else if (paramByte == 8) {
      i = 131072;
      if (!(paramObject instanceof MethodType) || !"<init>".equals(paramString))
        throw MethodHandleStatics.newIllegalArgumentException("not a constructor type or name"); 
    } else {
      throw MethodHandleStatics.newIllegalArgumentException("bad reference kind " + paramByte);
    } 
    init(paramClass, paramString, paramObject, flagsMods(i, 0, paramByte));
    initResolved(false);
  }
  
  public boolean hasReceiverTypeDispatch() { return MethodHandleNatives.refKindDoesDispatch(getReferenceKind()); }
  
  public boolean isResolved() { return (this.resolution == null); }
  
  private void initResolved(boolean paramBoolean) {
    assert this.resolution == null;
    if (!paramBoolean)
      this.resolution = this; 
    assert isResolved() == paramBoolean;
  }
  
  void checkForTypeAlias(Class<?> paramClass) {
    Class clazz1;
    if (isInvocable()) {
      if (this.type instanceof MethodType) {
        clazz1 = (MethodType)this.type;
      } else {
        this.type = clazz1 = getMethodType();
      } 
      if (clazz1.erase() == clazz1)
        return; 
      if (VerifyAccess.isTypeVisible(clazz1, paramClass))
        return; 
      throw new LinkageError("bad method type alias: " + clazz1 + " not visible from " + paramClass);
    } 
    if (this.type instanceof Class) {
      clazz1 = (Class)this.type;
    } else {
      this.type = clazz1 = getFieldType();
    } 
    if (VerifyAccess.isTypeVisible(clazz1, paramClass))
      return; 
    throw new LinkageError("bad field type alias: " + clazz1 + " not visible from " + paramClass);
  }
  
  public String toString() {
    if (isType())
      return this.type.toString(); 
    StringBuilder stringBuilder = new StringBuilder();
    if (getDeclaringClass() != null) {
      stringBuilder.append(getName(this.clazz));
      stringBuilder.append('.');
    } 
    String str = getName();
    stringBuilder.append((str == null) ? "*" : str);
    Object object = getType();
    if (!isInvocable()) {
      stringBuilder.append('/');
      stringBuilder.append((object == null) ? "*" : getName(object));
    } else {
      stringBuilder.append((object == null) ? "(*)*" : getName(object));
    } 
    byte b = getReferenceKind();
    if (b != 0) {
      stringBuilder.append('/');
      stringBuilder.append(MethodHandleNatives.refKindName(b));
    } 
    return stringBuilder.toString();
  }
  
  private static String getName(Object paramObject) { return (paramObject instanceof Class) ? ((Class)paramObject).getName() : String.valueOf(paramObject); }
  
  public IllegalAccessException makeAccessException(String paramString, Object paramObject) {
    paramString = paramString + ": " + toString();
    if (paramObject != null)
      paramString = paramString + ", from " + paramObject; 
    return new IllegalAccessException(paramString);
  }
  
  private String message() { return isResolved() ? "no access" : (isConstructor() ? "no such constructor" : (isMethod() ? "no such method" : "no such field")); }
  
  public ReflectiveOperationException makeAccessException() {
    NoSuchFieldException noSuchFieldException;
    String str = message() + ": " + toString();
    if (isResolved() || (!(this.resolution instanceof NoSuchMethodError) && !(this.resolution instanceof NoSuchFieldError))) {
      noSuchFieldException = new IllegalAccessException(str);
    } else if (isConstructor()) {
      noSuchFieldException = new NoSuchMethodException(str);
    } else if (isMethod()) {
      noSuchFieldException = new NoSuchMethodException(str);
    } else {
      noSuchFieldException = new NoSuchFieldException(str);
    } 
    if (this.resolution instanceof Throwable)
      noSuchFieldException.initCause((Throwable)this.resolution); 
    return noSuchFieldException;
  }
  
  static Factory getFactory() { return Factory.INSTANCE; }
  
  static class Factory {
    static Factory INSTANCE = new Factory();
    
    private static int ALLOWED_FLAGS = 983040;
    
    List<MemberName> getMembers(Class<?> param1Class1, String param1String, Object param1Object, int param1Int, Class<?> param1Class2) {
      param1Int &= ALLOWED_FLAGS;
      String str = null;
      if (param1Object != null) {
        str = BytecodeDescriptor.unparse(param1Object);
        if (str.startsWith("(")) {
          param1Int &= 0xFFF3FFFF;
        } else {
          param1Int &= 0xFFF4FFFF;
        } 
      } 
      byte b = (param1String == null) ? 10 : ((param1Object == null) ? 4 : 1);
      MemberName[] arrayOfMemberName = newMemberBuffer(b);
      int i = 0;
      ArrayList arrayList1 = null;
      int j = 0;
      while (true) {
        j = MethodHandleNatives.getMembers(param1Class1, param1String, str, param1Int, param1Class2, i, arrayOfMemberName);
        if (j <= arrayOfMemberName.length) {
          if (j < 0)
            j = 0; 
          i += j;
          break;
        } 
        i += arrayOfMemberName.length;
        int k = j - arrayOfMemberName.length;
        if (arrayList1 == null)
          arrayList1 = new ArrayList(1); 
        arrayList1.add(arrayOfMemberName);
        int m = arrayOfMemberName.length;
        m = Math.max(m, k);
        m = Math.max(m, i / 4);
        arrayOfMemberName = newMemberBuffer(Math.min(8192, m));
      } 
      ArrayList arrayList2 = new ArrayList(i);
      if (arrayList1 != null)
        for (MemberName[] arrayOfMemberName1 : arrayList1)
          Collections.addAll(arrayList2, arrayOfMemberName1);  
      arrayList2.addAll(Arrays.asList(arrayOfMemberName).subList(0, j));
      if (param1Object != null && param1Object != str) {
        Iterator iterator = arrayList2.iterator();
        while (iterator.hasNext()) {
          MemberName memberName = (MemberName)iterator.next();
          if (!param1Object.equals(memberName.getType()))
            iterator.remove(); 
        } 
      } 
      return arrayList2;
    }
    
    private MemberName resolve(byte param1Byte, MemberName param1MemberName, Class<?> param1Class) {
      MemberName memberName = param1MemberName.clone();
      assert param1Byte == memberName.getReferenceKind();
      try {
        memberName = MethodHandleNatives.resolve(memberName, param1Class);
        memberName.checkForTypeAlias(memberName.getDeclaringClass());
        memberName.resolution = null;
      } catch (ClassNotFoundException|LinkageError classNotFoundException) {
        assert !memberName.isResolved();
        memberName.resolution = classNotFoundException;
        return memberName;
      } 
      assert memberName.referenceKindIsConsistent();
      memberName.initResolved(true);
      assert memberName.vminfoIsConsistent();
      return memberName;
    }
    
    public <NoSuchMemberException extends ReflectiveOperationException> MemberName resolveOrFail(byte param1Byte, MemberName param1MemberName, Class<?> param1Class1, Class<NoSuchMemberException> param1Class2) throws IllegalAccessException, NoSuchMemberException {
      MemberName memberName = resolve(param1Byte, param1MemberName, param1Class1);
      if (memberName.isResolved())
        return memberName; 
      ReflectiveOperationException reflectiveOperationException = memberName.makeAccessException();
      if (reflectiveOperationException instanceof IllegalAccessException)
        throw (IllegalAccessException)reflectiveOperationException; 
      throw (ReflectiveOperationException)param1Class2.cast(reflectiveOperationException);
    }
    
    public MemberName resolveOrNull(byte param1Byte, MemberName param1MemberName, Class<?> param1Class) {
      MemberName memberName = resolve(param1Byte, param1MemberName, param1Class);
      return memberName.isResolved() ? memberName : null;
    }
    
    public List<MemberName> getMethods(Class<?> param1Class1, boolean param1Boolean, Class<?> param1Class2) { return getMethods(param1Class1, param1Boolean, null, null, param1Class2); }
    
    public List<MemberName> getMethods(Class<?> param1Class1, boolean param1Boolean, String param1String, MethodType param1MethodType, Class<?> param1Class2) {
      int i = 0x10000 | (param1Boolean ? 3145728 : 0);
      return getMembers(param1Class1, param1String, param1MethodType, i, param1Class2);
    }
    
    public List<MemberName> getConstructors(Class<?> param1Class1, Class<?> param1Class2) { return getMembers(param1Class1, null, null, 131072, param1Class2); }
    
    public List<MemberName> getFields(Class<?> param1Class1, boolean param1Boolean, Class<?> param1Class2) { return getFields(param1Class1, param1Boolean, null, null, param1Class2); }
    
    public List<MemberName> getFields(Class<?> param1Class1, boolean param1Boolean, String param1String, Class<?> param1Class2, Class<?> param1Class3) {
      int i = 0x40000 | (param1Boolean ? 3145728 : 0);
      return getMembers(param1Class1, param1String, param1Class2, i, param1Class3);
    }
    
    public List<MemberName> getNestedTypes(Class<?> param1Class1, boolean param1Boolean, Class<?> param1Class2) {
      int i = 0x80000 | (param1Boolean ? 3145728 : 0);
      return getMembers(param1Class1, null, null, i, param1Class2);
    }
    
    private static MemberName[] newMemberBuffer(int param1Int) {
      MemberName[] arrayOfMemberName = new MemberName[param1Int];
      for (byte b = 0; b < param1Int; b++)
        arrayOfMemberName[b] = new MemberName(); 
      return arrayOfMemberName;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MemberName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */