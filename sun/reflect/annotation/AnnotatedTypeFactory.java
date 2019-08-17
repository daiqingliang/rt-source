package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class AnnotatedTypeFactory {
  static final AnnotatedType EMPTY_ANNOTATED_TYPE = new AnnotatedTypeBaseImpl(null, TypeAnnotation.LocationInfo.BASE_LOCATION, new TypeAnnotation[0], new TypeAnnotation[0], null);
  
  static final AnnotatedType[] EMPTY_ANNOTATED_TYPE_ARRAY = new AnnotatedType[0];
  
  public static AnnotatedType buildAnnotatedType(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement) {
    if (paramType == null)
      return EMPTY_ANNOTATED_TYPE; 
    if (isArray(paramType))
      return new AnnotatedArrayTypeImpl(paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement); 
    if (paramType instanceof Class)
      return new AnnotatedTypeBaseImpl(paramType, addNesting(paramType, paramLocationInfo), paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement); 
    if (paramType instanceof TypeVariable)
      return new AnnotatedTypeVariableImpl((TypeVariable)paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement); 
    if (paramType instanceof ParameterizedType)
      return new AnnotatedParameterizedTypeImpl((ParameterizedType)paramType, addNesting(paramType, paramLocationInfo), paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement); 
    if (paramType instanceof WildcardType)
      return new AnnotatedWildcardTypeImpl((WildcardType)paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement); 
    throw new AssertionError("Unknown instance of Type: " + paramType + "\nThis should not happen.");
  }
  
  private static TypeAnnotation.LocationInfo addNesting(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo) {
    if (isArray(paramType))
      return paramLocationInfo; 
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return (clazz.getEnclosingClass() == null) ? paramLocationInfo : (Modifier.isStatic(clazz.getModifiers()) ? addNesting(clazz.getEnclosingClass(), paramLocationInfo) : addNesting(clazz.getEnclosingClass(), paramLocationInfo.pushInner()));
    } 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      return (parameterizedType.getOwnerType() == null) ? paramLocationInfo : addNesting(parameterizedType.getOwnerType(), paramLocationInfo.pushInner());
    } 
    return paramLocationInfo;
  }
  
  private static boolean isArray(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      if (clazz.isArray())
        return true; 
    } else if (paramType instanceof GenericArrayType) {
      return true;
    } 
    return false;
  }
  
  private static final class AnnotatedArrayTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedArrayType {
    AnnotatedArrayTypeImpl(Type param1Type, TypeAnnotation.LocationInfo param1LocationInfo, TypeAnnotation[] param1ArrayOfTypeAnnotation1, TypeAnnotation[] param1ArrayOfTypeAnnotation2, AnnotatedElement param1AnnotatedElement) { super(param1Type, param1LocationInfo, param1ArrayOfTypeAnnotation1, param1ArrayOfTypeAnnotation2, param1AnnotatedElement); }
    
    public AnnotatedType getAnnotatedGenericComponentType() { return AnnotatedTypeFactory.buildAnnotatedType(getComponentType(), getLocation().pushArray(), getTypeAnnotations(), getTypeAnnotations(), getDecl()); }
    
    private Type getComponentType() {
      Type type = getType();
      if (type instanceof Class) {
        Class clazz = (Class)type;
        return clazz.getComponentType();
      } 
      return ((GenericArrayType)type).getGenericComponentType();
    }
  }
  
  private static final class AnnotatedParameterizedTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedParameterizedType {
    AnnotatedParameterizedTypeImpl(ParameterizedType param1ParameterizedType, TypeAnnotation.LocationInfo param1LocationInfo, TypeAnnotation[] param1ArrayOfTypeAnnotation1, TypeAnnotation[] param1ArrayOfTypeAnnotation2, AnnotatedElement param1AnnotatedElement) { super(param1ParameterizedType, param1LocationInfo, param1ArrayOfTypeAnnotation1, param1ArrayOfTypeAnnotation2, param1AnnotatedElement); }
    
    public AnnotatedType[] getAnnotatedActualTypeArguments() {
      Type[] arrayOfType = getParameterizedType().getActualTypeArguments();
      AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[arrayOfType.length];
      Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
      int i = getTypeAnnotations().length;
      for (byte b = 0; b < arrayOfAnnotatedType.length; b++) {
        ArrayList arrayList = new ArrayList(i);
        TypeAnnotation.LocationInfo locationInfo = getLocation().pushTypeArg((short)(byte)b);
        for (TypeAnnotation typeAnnotation : getTypeAnnotations()) {
          if (typeAnnotation.getLocationInfo().isSameLocationInfo(locationInfo))
            arrayList.add(typeAnnotation); 
        } 
        arrayOfAnnotatedType[b] = AnnotatedTypeFactory.buildAnnotatedType(arrayOfType[b], locationInfo, (TypeAnnotation[])arrayList.toArray(new TypeAnnotation[0]), getTypeAnnotations(), getDecl());
      } 
      return arrayOfAnnotatedType;
    }
    
    private ParameterizedType getParameterizedType() { return (ParameterizedType)getType(); }
  }
  
  private static class AnnotatedTypeBaseImpl implements AnnotatedType {
    private final Type type;
    
    private final AnnotatedElement decl;
    
    private final TypeAnnotation.LocationInfo location;
    
    private final TypeAnnotation[] allOnSameTargetTypeAnnotations;
    
    private final Map<Class<? extends Annotation>, Annotation> annotations;
    
    AnnotatedTypeBaseImpl(Type param1Type, TypeAnnotation.LocationInfo param1LocationInfo, TypeAnnotation[] param1ArrayOfTypeAnnotation1, TypeAnnotation[] param1ArrayOfTypeAnnotation2, AnnotatedElement param1AnnotatedElement) {
      this.type = param1Type;
      this.decl = param1AnnotatedElement;
      this.location = param1LocationInfo;
      this.allOnSameTargetTypeAnnotations = param1ArrayOfTypeAnnotation2;
      this.annotations = TypeAnnotationParser.mapTypeAnnotations(param1LocationInfo.filter(param1ArrayOfTypeAnnotation1));
    }
    
    public final Annotation[] getAnnotations() { return getDeclaredAnnotations(); }
    
    public final <T extends Annotation> T getAnnotation(Class<T> param1Class) { return (T)getDeclaredAnnotation(param1Class); }
    
    public final <T extends Annotation> T[] getAnnotationsByType(Class<T> param1Class) { return (T[])getDeclaredAnnotationsByType(param1Class); }
    
    public final Annotation[] getDeclaredAnnotations() { return (Annotation[])this.annotations.values().toArray(new Annotation[0]); }
    
    public final <T extends Annotation> T getDeclaredAnnotation(Class<T> param1Class) { return (T)(Annotation)this.annotations.get(param1Class); }
    
    public final <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> param1Class) { return (T[])AnnotationSupport.getDirectlyAndIndirectlyPresent(this.annotations, param1Class); }
    
    public final Type getType() { return this.type; }
    
    final TypeAnnotation.LocationInfo getLocation() { return this.location; }
    
    final TypeAnnotation[] getTypeAnnotations() { return this.allOnSameTargetTypeAnnotations; }
    
    final AnnotatedElement getDecl() { return this.decl; }
  }
  
  private static final class AnnotatedTypeVariableImpl extends AnnotatedTypeBaseImpl implements AnnotatedTypeVariable {
    AnnotatedTypeVariableImpl(TypeVariable<?> param1TypeVariable, TypeAnnotation.LocationInfo param1LocationInfo, TypeAnnotation[] param1ArrayOfTypeAnnotation1, TypeAnnotation[] param1ArrayOfTypeAnnotation2, AnnotatedElement param1AnnotatedElement) { super(param1TypeVariable, param1LocationInfo, param1ArrayOfTypeAnnotation1, param1ArrayOfTypeAnnotation2, param1AnnotatedElement); }
    
    public AnnotatedType[] getAnnotatedBounds() { return getTypeVariable().getAnnotatedBounds(); }
    
    private TypeVariable<?> getTypeVariable() { return (TypeVariable)getType(); }
  }
  
  private static final class AnnotatedWildcardTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedWildcardType {
    private final boolean hasUpperBounds;
    
    AnnotatedWildcardTypeImpl(WildcardType param1WildcardType, TypeAnnotation.LocationInfo param1LocationInfo, TypeAnnotation[] param1ArrayOfTypeAnnotation1, TypeAnnotation[] param1ArrayOfTypeAnnotation2, AnnotatedElement param1AnnotatedElement) {
      super(param1WildcardType, param1LocationInfo, param1ArrayOfTypeAnnotation1, param1ArrayOfTypeAnnotation2, param1AnnotatedElement);
      this.hasUpperBounds = (param1WildcardType.getLowerBounds().length == 0);
    }
    
    public AnnotatedType[] getAnnotatedUpperBounds() { return !hasUpperBounds() ? new AnnotatedType[0] : getAnnotatedBounds(getWildcardType().getUpperBounds()); }
    
    public AnnotatedType[] getAnnotatedLowerBounds() { return this.hasUpperBounds ? new AnnotatedType[0] : getAnnotatedBounds(getWildcardType().getLowerBounds()); }
    
    private AnnotatedType[] getAnnotatedBounds(Type[] param1ArrayOfType) {
      AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[param1ArrayOfType.length];
      Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
      TypeAnnotation.LocationInfo locationInfo = getLocation().pushWildcard();
      int i = getTypeAnnotations().length;
      for (byte b = 0; b < arrayOfAnnotatedType.length; b++) {
        ArrayList arrayList = new ArrayList(i);
        for (TypeAnnotation typeAnnotation : getTypeAnnotations()) {
          if (typeAnnotation.getLocationInfo().isSameLocationInfo(locationInfo))
            arrayList.add(typeAnnotation); 
        } 
        arrayOfAnnotatedType[b] = AnnotatedTypeFactory.buildAnnotatedType(param1ArrayOfType[b], locationInfo, (TypeAnnotation[])arrayList.toArray(new TypeAnnotation[0]), getTypeAnnotations(), getDecl());
      } 
      return arrayOfAnnotatedType;
    }
    
    private WildcardType getWildcardType() { return (WildcardType)getType(); }
    
    private boolean hasUpperBounds() { return this.hasUpperBounds; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\AnnotatedTypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */