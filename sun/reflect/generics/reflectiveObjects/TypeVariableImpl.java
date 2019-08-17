package sun.reflect.generics.reflectiveObjects;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.misc.ReflectUtil;

public class TypeVariableImpl<D extends GenericDeclaration> extends LazyReflectiveObjectGenerator implements TypeVariable<D> {
  D genericDeclaration;
  
  private String name;
  
  private Type[] bounds;
  
  private FieldTypeSignature[] boundASTs;
  
  private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
  
  private TypeVariableImpl(D paramD, String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature, GenericsFactory paramGenericsFactory) {
    super(paramGenericsFactory);
    this.genericDeclaration = paramD;
    this.name = paramString;
    this.boundASTs = paramArrayOfFieldTypeSignature;
  }
  
  private FieldTypeSignature[] getBoundASTs() {
    assert this.bounds == null;
    return this.boundASTs;
  }
  
  public static <T extends GenericDeclaration> TypeVariableImpl<T> make(T paramT, String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature, GenericsFactory paramGenericsFactory) {
    if (!(paramT instanceof Class) && !(paramT instanceof java.lang.reflect.Method) && !(paramT instanceof java.lang.reflect.Constructor))
      throw new AssertionError("Unexpected kind of GenericDeclaration" + paramT.getClass().toString()); 
    return new TypeVariableImpl(paramT, paramString, paramArrayOfFieldTypeSignature, paramGenericsFactory);
  }
  
  public Type[] getBounds() {
    if (this.bounds == null) {
      FieldTypeSignature[] arrayOfFieldTypeSignature = getBoundASTs();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (byte b = 0; b < arrayOfFieldTypeSignature.length; b++) {
        Reifier reifier = getReifier();
        arrayOfFieldTypeSignature[b].accept(reifier);
        arrayOfType[b] = reifier.getResult();
      } 
      this.bounds = arrayOfType;
    } 
    return (Type[])this.bounds.clone();
  }
  
  public D getGenericDeclaration() {
    if (this.genericDeclaration instanceof Class) {
      ReflectUtil.checkPackageAccess((Class)this.genericDeclaration);
    } else if (this.genericDeclaration instanceof java.lang.reflect.Method || this.genericDeclaration instanceof java.lang.reflect.Constructor) {
      ReflectUtil.conservativeCheckMemberAccess((Member)this.genericDeclaration);
    } else {
      throw new AssertionError("Unexpected kind of GenericDeclaration");
    } 
    return (D)this.genericDeclaration;
  }
  
  public String getName() { return this.name; }
  
  public String toString() { return getName(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof TypeVariable && paramObject.getClass() == TypeVariableImpl.class) {
      TypeVariable typeVariable = (TypeVariable)paramObject;
      GenericDeclaration genericDeclaration1 = typeVariable.getGenericDeclaration();
      String str = typeVariable.getName();
      return (Objects.equals(this.genericDeclaration, genericDeclaration1) && Objects.equals(this.name, str));
    } 
    return false;
  }
  
  public int hashCode() { return this.genericDeclaration.hashCode() ^ this.name.hashCode(); }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T)(Annotation)mapAnnotations(getAnnotations()).get(paramClass);
  }
  
  public <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T)getAnnotation(paramClass);
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T[])AnnotationSupport.getDirectlyAndIndirectlyPresent(mapAnnotations(getAnnotations()), paramClass);
  }
  
  public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T[])getAnnotationsByType(paramClass);
  }
  
  public Annotation[] getAnnotations() {
    int i = typeVarIndex();
    if (i < 0)
      throw new AssertionError("Index must be non-negative."); 
    return TypeAnnotationParser.parseTypeVariableAnnotations(getGenericDeclaration(), i);
  }
  
  public Annotation[] getDeclaredAnnotations() { return getAnnotations(); }
  
  public AnnotatedType[] getAnnotatedBounds() { return TypeAnnotationParser.parseAnnotatedBounds(getBounds(), getGenericDeclaration(), typeVarIndex()); }
  
  private int typeVarIndex() {
    TypeVariable[] arrayOfTypeVariable = getGenericDeclaration().getTypeParameters();
    byte b = -1;
    for (TypeVariable typeVariable : arrayOfTypeVariable) {
      b++;
      if (equals(typeVariable))
        return b; 
    } 
    return -1;
  }
  
  private static Map<Class<? extends Annotation>, Annotation> mapAnnotations(Annotation[] paramArrayOfAnnotation) {
    LinkedHashMap linkedHashMap = new LinkedHashMap();
    for (Annotation annotation : paramArrayOfAnnotation) {
      Class clazz = annotation.annotationType();
      AnnotationType annotationType = AnnotationType.getInstance(clazz);
      if (annotationType.retention() == RetentionPolicy.RUNTIME && linkedHashMap.put(clazz, annotation) != null)
        throw new AnnotationFormatError("Duplicate annotation for class: " + clazz + ": " + annotation); 
    } 
    return linkedHashMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\reflectiveObjects\TypeVariableImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */