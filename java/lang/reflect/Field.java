package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.FieldAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.FieldRepository;
import sun.reflect.generics.scope.ClassScope;

public final class Field extends AccessibleObject implements Member {
  private Class<?> clazz;
  
  private int slot;
  
  private String name;
  
  private Class<?> type;
  
  private int modifiers;
  
  private String signature;
  
  private FieldRepository genericInfo;
  
  private byte[] annotations;
  
  private FieldAccessor fieldAccessor;
  
  private FieldAccessor overrideFieldAccessor;
  
  private Field root;
  
  private Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  private String getGenericSignature() { return this.signature; }
  
  private GenericsFactory getFactory() {
    Class clazz1 = getDeclaringClass();
    return CoreReflectionFactory.make(clazz1, ClassScope.make(clazz1));
  }
  
  private FieldRepository getGenericInfo() {
    if (this.genericInfo == null)
      this.genericInfo = FieldRepository.make(getGenericSignature(), getFactory()); 
    return this.genericInfo;
  }
  
  Field(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte) {
    this.clazz = paramClass1;
    this.name = paramString1;
    this.type = paramClass2;
    this.modifiers = paramInt1;
    this.slot = paramInt2;
    this.signature = paramString2;
    this.annotations = paramArrayOfByte;
  }
  
  Field copy() {
    if (this.root != null)
      throw new IllegalArgumentException("Can not copy a non-root Field"); 
    Field field = new Field(this.clazz, this.name, this.type, this.modifiers, this.slot, this.signature, this.annotations);
    field.root = this;
    field.fieldAccessor = this.fieldAccessor;
    field.overrideFieldAccessor = this.overrideFieldAccessor;
    return field;
  }
  
  public Class<?> getDeclaringClass() { return this.clazz; }
  
  public String getName() { return this.name; }
  
  public int getModifiers() { return this.modifiers; }
  
  public boolean isEnumConstant() { return ((getModifiers() & 0x4000) != 0); }
  
  public boolean isSynthetic() { return Modifier.isSynthetic(getModifiers()); }
  
  public Class<?> getType() { return this.type; }
  
  public Type getGenericType() { return (getGenericSignature() != null) ? getGenericInfo().getGenericType() : getType(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Field) {
      Field field = (Field)paramObject;
      return (getDeclaringClass() == field.getDeclaringClass() && getName() == field.getName() && getType() == field.getType());
    } 
    return false;
  }
  
  public int hashCode() { return getDeclaringClass().getName().hashCode() ^ getName().hashCode(); }
  
  public String toString() {
    int i = getModifiers();
    return ((i == 0) ? "" : (Modifier.toString(i) + " ")) + getType().getTypeName() + " " + getDeclaringClass().getTypeName() + "." + getName();
  }
  
  public String toGenericString() {
    int i = getModifiers();
    Type type1 = getGenericType();
    return ((i == 0) ? "" : (Modifier.toString(i) + " ")) + type1.getTypeName() + " " + getDeclaringClass().getTypeName() + "." + getName();
  }
  
  @CallerSensitive
  public Object get(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).get(paramObject);
  }
  
  @CallerSensitive
  public boolean getBoolean(Object paramObject) {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getBoolean(paramObject);
  }
  
  @CallerSensitive
  public byte getByte(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getByte(paramObject);
  }
  
  @CallerSensitive
  public char getChar(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getChar(paramObject);
  }
  
  @CallerSensitive
  public short getShort(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getShort(paramObject);
  }
  
  @CallerSensitive
  public int getInt(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getInt(paramObject);
  }
  
  @CallerSensitive
  public long getLong(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getLong(paramObject);
  }
  
  @CallerSensitive
  public float getFloat(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getFloat(paramObject);
  }
  
  @CallerSensitive
  public double getDouble(Object paramObject) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    return getFieldAccessor(paramObject).getDouble(paramObject);
  }
  
  @CallerSensitive
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject1, this.modifiers);
    } 
    getFieldAccessor(paramObject1).set(paramObject1, paramObject2);
  }
  
  @CallerSensitive
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setBoolean(paramObject, paramBoolean);
  }
  
  @CallerSensitive
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setByte(paramObject, paramByte);
  }
  
  @CallerSensitive
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setChar(paramObject, paramChar);
  }
  
  @CallerSensitive
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setShort(paramObject, paramShort);
  }
  
  @CallerSensitive
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setInt(paramObject, paramInt);
  }
  
  @CallerSensitive
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setLong(paramObject, paramLong);
  }
  
  @CallerSensitive
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setFloat(paramObject, paramFloat);
  }
  
  @CallerSensitive
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    getFieldAccessor(paramObject).setDouble(paramObject, paramDouble);
  }
  
  private FieldAccessor getFieldAccessor(Object paramObject) throws IllegalAccessException {
    boolean bool = this.override;
    FieldAccessor fieldAccessor1 = bool ? this.overrideFieldAccessor : this.fieldAccessor;
    return (fieldAccessor1 != null) ? fieldAccessor1 : acquireFieldAccessor(bool);
  }
  
  private FieldAccessor acquireFieldAccessor(boolean paramBoolean) {
    FieldAccessor fieldAccessor1 = null;
    if (this.root != null)
      fieldAccessor1 = this.root.getFieldAccessor(paramBoolean); 
    if (fieldAccessor1 != null) {
      if (paramBoolean) {
        this.overrideFieldAccessor = fieldAccessor1;
      } else {
        this.fieldAccessor = fieldAccessor1;
      } 
    } else {
      fieldAccessor1 = reflectionFactory.newFieldAccessor(this, paramBoolean);
      setFieldAccessor(fieldAccessor1, paramBoolean);
    } 
    return fieldAccessor1;
  }
  
  private FieldAccessor getFieldAccessor(boolean paramBoolean) { return paramBoolean ? this.overrideFieldAccessor : this.fieldAccessor; }
  
  private void setFieldAccessor(FieldAccessor paramFieldAccessor, boolean paramBoolean) {
    if (paramBoolean) {
      this.overrideFieldAccessor = paramFieldAccessor;
    } else {
      this.fieldAccessor = paramFieldAccessor;
    } 
    if (this.root != null)
      this.root.setFieldAccessor(paramFieldAccessor, paramBoolean); 
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T)(Annotation)paramClass.cast(declaredAnnotations().get(paramClass));
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T[])AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations() { return AnnotationParser.toArray(declaredAnnotations()); }
  
  private Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
    if (this.declaredAnnotations == null) {
      Field field = this.root;
      if (field != null) {
        this.declaredAnnotations = field.declaredAnnotations();
      } else {
        this.declaredAnnotations = AnnotationParser.parseAnnotations(this.annotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
      } 
    } 
    return this.declaredAnnotations;
  }
  
  private native byte[] getTypeAnnotationBytes0();
  
  public AnnotatedType getAnnotatedType() { return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getGenericType(), TypeAnnotation.TypeAnnotationTarget.FIELD); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */