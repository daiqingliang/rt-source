package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.ConstantPool;

public final class TypeAnnotationParser {
  private static final TypeAnnotation[] EMPTY_TYPE_ANNOTATION_ARRAY = new TypeAnnotation[0];
  
  private static final byte CLASS_TYPE_PARAMETER = 0;
  
  private static final byte METHOD_TYPE_PARAMETER = 1;
  
  private static final byte CLASS_EXTENDS = 16;
  
  private static final byte CLASS_TYPE_PARAMETER_BOUND = 17;
  
  private static final byte METHOD_TYPE_PARAMETER_BOUND = 18;
  
  private static final byte FIELD = 19;
  
  private static final byte METHOD_RETURN = 20;
  
  private static final byte METHOD_RECEIVER = 21;
  
  private static final byte METHOD_FORMAL_PARAMETER = 22;
  
  private static final byte THROWS = 23;
  
  private static final byte LOCAL_VARIABLE = 64;
  
  private static final byte RESOURCE_VARIABLE = 65;
  
  private static final byte EXCEPTION_PARAMETER = 66;
  
  private static final byte INSTANCEOF = 67;
  
  private static final byte NEW = 68;
  
  private static final byte CONSTRUCTOR_REFERENCE = 69;
  
  private static final byte METHOD_REFERENCE = 70;
  
  private static final byte CAST = 71;
  
  private static final byte CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
  
  private static final byte METHOD_INVOCATION_TYPE_ARGUMENT = 73;
  
  private static final byte CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
  
  private static final byte METHOD_REFERENCE_TYPE_ARGUMENT = 75;
  
  public static AnnotatedType buildAnnotatedType(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass, Type paramType, TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget) {
    TypeAnnotation[] arrayOfTypeAnnotation1 = parseTypeAnnotations(paramArrayOfByte, paramConstantPool, paramAnnotatedElement, paramClass);
    ArrayList arrayList = new ArrayList(arrayOfTypeAnnotation1.length);
    for (TypeAnnotation typeAnnotation : arrayOfTypeAnnotation1) {
      TypeAnnotation.TypeAnnotationTargetInfo typeAnnotationTargetInfo = typeAnnotation.getTargetInfo();
      if (typeAnnotationTargetInfo.getTarget() == paramTypeAnnotationTarget)
        arrayList.add(typeAnnotation); 
    } 
    TypeAnnotation[] arrayOfTypeAnnotation2 = (TypeAnnotation[])arrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
    return AnnotatedTypeFactory.buildAnnotatedType(paramType, TypeAnnotation.LocationInfo.BASE_LOCATION, arrayOfTypeAnnotation2, arrayOfTypeAnnotation2, paramAnnotatedElement);
  }
  
  public static AnnotatedType[] buildAnnotatedTypes(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass, Type[] paramArrayOfType, TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget) {
    int i = paramArrayOfType.length;
    AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[i];
    Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
    ArrayList[] arrayOfArrayList = new ArrayList[i];
    TypeAnnotation[] arrayOfTypeAnnotation = parseTypeAnnotations(paramArrayOfByte, paramConstantPool, paramAnnotatedElement, paramClass);
    for (TypeAnnotation typeAnnotation : arrayOfTypeAnnotation) {
      TypeAnnotation.TypeAnnotationTargetInfo typeAnnotationTargetInfo = typeAnnotation.getTargetInfo();
      if (typeAnnotationTargetInfo.getTarget() == paramTypeAnnotationTarget) {
        int j = typeAnnotationTargetInfo.getCount();
        if (arrayOfArrayList[j] == null) {
          ArrayList arrayList1 = new ArrayList(arrayOfTypeAnnotation.length);
          arrayOfArrayList[j] = arrayList1;
        } 
        ArrayList arrayList = arrayOfArrayList[j];
        arrayList.add(typeAnnotation);
      } 
    } 
    for (byte b = 0; b < i; b++) {
      TypeAnnotation[] arrayOfTypeAnnotation1;
      ArrayList arrayList = arrayOfArrayList[b];
      if (arrayList != null) {
        arrayOfTypeAnnotation1 = (TypeAnnotation[])arrayList.toArray(new TypeAnnotation[arrayList.size()]);
      } else {
        arrayOfTypeAnnotation1 = EMPTY_TYPE_ANNOTATION_ARRAY;
      } 
      arrayOfAnnotatedType[b] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[b], TypeAnnotation.LocationInfo.BASE_LOCATION, arrayOfTypeAnnotation1, arrayOfTypeAnnotation1, paramAnnotatedElement);
    } 
    return arrayOfAnnotatedType;
  }
  
  public static AnnotatedType buildAnnotatedSuperclass(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass) {
    Type type = paramClass.getGenericSuperclass();
    return (type == null) ? AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE : buildAnnotatedType(paramArrayOfByte, paramConstantPool, paramClass, paramClass, type, TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
  }
  
  public static AnnotatedType[] buildAnnotatedInterfaces(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass) { return (paramClass == Object.class || paramClass.isArray() || paramClass.isPrimitive() || paramClass == void.class) ? AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE_ARRAY : buildAnnotatedTypes(paramArrayOfByte, paramConstantPool, paramClass, paramClass, paramClass.getGenericInterfaces(), TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS); }
  
  public static <D extends java.lang.reflect.GenericDeclaration> Annotation[] parseTypeVariableAnnotations(D paramD, int paramInt) {
    TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget;
    Executable executable;
    if (paramD instanceof Class) {
      executable = (Class)paramD;
      typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER;
    } else if (paramD instanceof Executable) {
      executable = (Executable)paramD;
      typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER;
    } else {
      throw new AssertionError("Unknown GenericDeclaration " + paramD + "\nthis should not happen.");
    } 
    List list = TypeAnnotation.filter(parseAllTypeAnnotations(executable), typeAnnotationTarget);
    ArrayList arrayList = new ArrayList(list.size());
    for (TypeAnnotation typeAnnotation : list) {
      if (typeAnnotation.getTargetInfo().getCount() == paramInt)
        arrayList.add(typeAnnotation.getAnnotation()); 
    } 
    return (Annotation[])arrayList.toArray(new Annotation[0]);
  }
  
  public static <D extends java.lang.reflect.GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] paramArrayOfType, D paramD, int paramInt) { return parseAnnotatedBounds(paramArrayOfType, paramD, paramInt, TypeAnnotation.LocationInfo.BASE_LOCATION); }
  
  private static <D extends java.lang.reflect.GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] paramArrayOfType, D paramD, int paramInt, TypeAnnotation.LocationInfo paramLocationInfo) {
    List list = fetchBounds(paramD);
    if (paramArrayOfType != null) {
      byte b1 = 0;
      AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[paramArrayOfType.length];
      if (paramArrayOfType.length > 0) {
        Type type = paramArrayOfType[0];
        if (!(type instanceof Class)) {
          b1 = 1;
        } else {
          Class clazz = (Class)type;
          if (clazz.isInterface())
            b1 = 1; 
        } 
      } 
      for (byte b2 = 0; b2 < paramArrayOfType.length; b2++) {
        ArrayList arrayList = new ArrayList(list.size());
        for (TypeAnnotation typeAnnotation : list) {
          TypeAnnotation.TypeAnnotationTargetInfo typeAnnotationTargetInfo = typeAnnotation.getTargetInfo();
          if (typeAnnotationTargetInfo.getSecondaryIndex() == b2 + b1 && typeAnnotationTargetInfo.getCount() == paramInt)
            arrayList.add(typeAnnotation); 
        } 
        arrayOfAnnotatedType[b2] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[b2], paramLocationInfo, (TypeAnnotation[])arrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY), (TypeAnnotation[])list.toArray(EMPTY_TYPE_ANNOTATION_ARRAY), paramD);
      } 
      return arrayOfAnnotatedType;
    } 
    return new AnnotatedType[0];
  }
  
  private static <D extends java.lang.reflect.GenericDeclaration> List<TypeAnnotation> fetchBounds(D paramD) {
    TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget;
    Executable executable;
    if (paramD instanceof Class) {
      typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND;
      executable = (Class)paramD;
    } else {
      typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND;
      executable = (Executable)paramD;
    } 
    return TypeAnnotation.filter(parseAllTypeAnnotations(executable), typeAnnotationTarget);
  }
  
  static TypeAnnotation[] parseAllTypeAnnotations(AnnotatedElement paramAnnotatedElement) {
    byte[] arrayOfByte;
    Class clazz;
    JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
    if (paramAnnotatedElement instanceof Class) {
      clazz = (Class)paramAnnotatedElement;
      arrayOfByte = javaLangAccess.getRawClassTypeAnnotations(clazz);
    } else if (paramAnnotatedElement instanceof Executable) {
      clazz = ((Executable)paramAnnotatedElement).getDeclaringClass();
      arrayOfByte = javaLangAccess.getRawExecutableTypeAnnotations((Executable)paramAnnotatedElement);
    } else {
      return EMPTY_TYPE_ANNOTATION_ARRAY;
    } 
    return parseTypeAnnotations(arrayOfByte, javaLangAccess.getConstantPool(clazz), paramAnnotatedElement, clazz);
  }
  
  private static TypeAnnotation[] parseTypeAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass) {
    if (paramArrayOfByte == null)
      return EMPTY_TYPE_ANNOTATION_ARRAY; 
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    short s = byteBuffer.getShort() & 0xFFFF;
    ArrayList arrayList = new ArrayList(s);
    for (byte b = 0; b < s; b++) {
      TypeAnnotation typeAnnotation = parseTypeAnnotation(byteBuffer, paramConstantPool, paramAnnotatedElement, paramClass);
      if (typeAnnotation != null)
        arrayList.add(typeAnnotation); 
    } 
    return (TypeAnnotation[])arrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
  }
  
  static Map<Class<? extends Annotation>, Annotation> mapTypeAnnotations(TypeAnnotation[] paramArrayOfTypeAnnotation) {
    LinkedHashMap linkedHashMap = new LinkedHashMap();
    for (TypeAnnotation typeAnnotation : paramArrayOfTypeAnnotation) {
      Annotation annotation = typeAnnotation.getAnnotation();
      Class clazz = annotation.annotationType();
      AnnotationType annotationType = AnnotationType.getInstance(clazz);
      if (annotationType.retention() == RetentionPolicy.RUNTIME && linkedHashMap.put(clazz, annotation) != null)
        throw new AnnotationFormatError("Duplicate annotation for class: " + clazz + ": " + annotation); 
    } 
    return linkedHashMap;
  }
  
  private static TypeAnnotation parseTypeAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass) {
    try {
      TypeAnnotation.TypeAnnotationTargetInfo typeAnnotationTargetInfo = parseTargetInfo(paramByteBuffer);
      TypeAnnotation.LocationInfo locationInfo = TypeAnnotation.LocationInfo.parseLocationInfo(paramByteBuffer);
      Annotation annotation = AnnotationParser.parseAnnotation(paramByteBuffer, paramConstantPool, paramClass, false);
      return (typeAnnotationTargetInfo == null) ? null : new TypeAnnotation(typeAnnotationTargetInfo, locationInfo, annotation, paramAnnotatedElement);
    } catch (IllegalArgumentException|java.nio.BufferUnderflowException illegalArgumentException) {
      throw new AnnotationFormatError(illegalArgumentException);
    } 
  }
  
  private static TypeAnnotation.TypeAnnotationTargetInfo parseTargetInfo(ByteBuffer paramByteBuffer) {
    byte b3;
    TypeAnnotation.TypeAnnotationTargetInfo typeAnnotationTargetInfo;
    short s1;
    byte b2;
    short s;
    byte b1 = paramByteBuffer.get() & 0xFF;
    switch (b1) {
      case 0:
      case 1:
        b2 = paramByteBuffer.get() & 0xFF;
        if (b1 == 0) {
          typeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER, b2);
        } else {
          typeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER, b2);
        } 
        return typeAnnotationTargetInfo;
      case 16:
        s = paramByteBuffer.getShort();
        if (s == -1)
          return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS); 
        if (s >= 0)
          return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS, s); 
        break;
      case 17:
        return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND, paramByteBuffer);
      case 18:
        return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND, paramByteBuffer);
      case 19:
        return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.FIELD);
      case 20:
        return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
      case 21:
        return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
      case 22:
        s = paramByteBuffer.get() & 0xFF;
        return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER, s);
      case 23:
        return parseShortTarget(TypeAnnotation.TypeAnnotationTarget.THROWS, paramByteBuffer);
      case 64:
      case 65:
        s = paramByteBuffer.getShort();
        for (s1 = 0; s1 < s; s1++) {
          short s2 = paramByteBuffer.getShort();
          short s3 = paramByteBuffer.getShort();
          short s4 = paramByteBuffer.getShort();
        } 
        return null;
      case 66:
        s1 = paramByteBuffer.get();
        return null;
      case 67:
      case 68:
      case 69:
      case 70:
        s1 = paramByteBuffer.getShort();
        return null;
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
        s1 = paramByteBuffer.getShort();
        b3 = paramByteBuffer.get();
        return null;
    } 
    throw new AnnotationFormatError("Could not parse bytes for type annotations");
  }
  
  private static TypeAnnotation.TypeAnnotationTargetInfo parseShortTarget(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, ByteBuffer paramByteBuffer) {
    short s = paramByteBuffer.getShort() & 0xFFFF;
    return new TypeAnnotation.TypeAnnotationTargetInfo(paramTypeAnnotationTarget, s);
  }
  
  private static TypeAnnotation.TypeAnnotationTargetInfo parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, ByteBuffer paramByteBuffer) {
    byte b1 = paramByteBuffer.get() & 0xFF;
    byte b2 = paramByteBuffer.get() & 0xFF;
    return new TypeAnnotation.TypeAnnotationTargetInfo(paramTypeAnnotationTarget, b1, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\TypeAnnotationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */