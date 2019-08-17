package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.org.objectweb.asm.AnnotationVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import com.sun.xml.internal.ws.org.objectweb.asm.FieldVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class WrapperBeanGenerator {
  private static final Logger LOGGER = Logger.getLogger(WrapperBeanGenerator.class.getName());
  
  private static final FieldFactory FIELD_FACTORY = new FieldFactory(null);
  
  private static final AbstractWrapperBeanGenerator RUNTIME_GENERATOR = new RuntimeWrapperBeanGenerator(new RuntimeInlineAnnotationReader(), Utils.REFLECTION_NAVIGATOR, FIELD_FACTORY);
  
  private static byte[] createBeanImage(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Collection<Field> paramCollection) throws Exception {
    ClassWriter classWriter = new ClassWriter(0);
    classWriter.visit(49, 33, replaceDotWithSlash(paramString1), null, "java/lang/Object", null);
    AnnotationVisitor annotationVisitor1 = classWriter.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
    annotationVisitor1.visit("name", paramString2);
    annotationVisitor1.visit("namespace", paramString3);
    annotationVisitor1.visitEnd();
    AnnotationVisitor annotationVisitor2 = classWriter.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
    annotationVisitor2.visit("name", paramString4);
    annotationVisitor2.visit("namespace", paramString5);
    if (paramCollection.size() > 1) {
      AnnotationVisitor annotationVisitor = annotationVisitor2.visitArray("propOrder");
      Iterator iterator1 = paramCollection.iterator();
      while (iterator1.hasNext()) {
        Field field;
        annotationVisitor.visit("propOrder", field.fieldName);
      } 
      annotationVisitor.visitEnd();
    } 
    annotationVisitor2.visitEnd();
    Iterator iterator = paramCollection.iterator();
    while (iterator.hasNext()) {
      Field field;
      FieldVisitor fieldVisitor = classWriter.visitField(1, field.fieldName, field.asmType.getDescriptor(), field.getSignature(), null);
      for (Annotation annotation : field.jaxbAnnotations) {
        if (annotation instanceof XmlMimeType) {
          AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Ljavax/xml/bind/annotation/XmlMimeType;", true);
          annotationVisitor.visit("value", ((XmlMimeType)annotation).value());
          annotationVisitor.visitEnd();
          continue;
        } 
        if (annotation instanceof XmlJavaTypeAdapter) {
          AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
          annotationVisitor.visit("value", getASMType(((XmlJavaTypeAdapter)annotation).value()));
          annotationVisitor.visitEnd();
          continue;
        } 
        if (annotation instanceof javax.xml.bind.annotation.XmlAttachmentRef) {
          AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Ljavax/xml/bind/annotation/XmlAttachmentRef;", true);
          annotationVisitor.visitEnd();
          continue;
        } 
        if (annotation instanceof javax.xml.bind.annotation.XmlList) {
          AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Ljavax/xml/bind/annotation/XmlList;", true);
          annotationVisitor.visitEnd();
          continue;
        } 
        if (annotation instanceof XmlElement) {
          AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
          XmlElement xmlElement = (XmlElement)annotation;
          annotationVisitor.visit("name", xmlElement.name());
          annotationVisitor.visit("namespace", xmlElement.namespace());
          if (xmlElement.nillable())
            annotationVisitor.visit("nillable", Boolean.valueOf(true)); 
          if (xmlElement.required())
            annotationVisitor.visit("required", Boolean.valueOf(true)); 
          annotationVisitor.visitEnd();
          continue;
        } 
        throw new WebServiceException("Unknown JAXB annotation " + annotation);
      } 
      fieldVisitor.visitEnd();
    } 
    MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", "()V", null, null);
    methodVisitor.visitCode();
    methodVisitor.visitVarInsn(25, 0);
    methodVisitor.visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
    methodVisitor.visitInsn(177);
    methodVisitor.visitMaxs(1, 1);
    methodVisitor.visitEnd();
    classWriter.visitEnd();
    if (LOGGER.isLoggable(Level.FINE)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("\n");
      stringBuilder.append("@XmlRootElement(name=").append(paramString2).append(", namespace=").append(paramString3).append(")");
      stringBuilder.append("\n");
      stringBuilder.append("@XmlType(name=").append(paramString4).append(", namespace=").append(paramString5);
      if (paramCollection.size() > 1) {
        stringBuilder.append(", propOrder={");
        for (Field field : paramCollection) {
          stringBuilder.append(" ");
          stringBuilder.append(field.fieldName);
        } 
        stringBuilder.append(" }");
      } 
      stringBuilder.append(")");
      stringBuilder.append("\n");
      stringBuilder.append("public class ").append(paramString1).append(" {");
      for (Field field : paramCollection) {
        stringBuilder.append("\n");
        for (Annotation annotation : field.jaxbAnnotations) {
          stringBuilder.append("\n    ");
          if (annotation instanceof XmlMimeType) {
            stringBuilder.append("@XmlMimeType(value=").append(((XmlMimeType)annotation).value()).append(")");
            continue;
          } 
          if (annotation instanceof XmlJavaTypeAdapter) {
            stringBuilder.append("@XmlJavaTypeAdapter(value=").append(getASMType(((XmlJavaTypeAdapter)annotation).value())).append(")");
            continue;
          } 
          if (annotation instanceof javax.xml.bind.annotation.XmlAttachmentRef) {
            stringBuilder.append("@XmlAttachmentRef");
            continue;
          } 
          if (annotation instanceof javax.xml.bind.annotation.XmlList) {
            stringBuilder.append("@XmlList");
            continue;
          } 
          if (annotation instanceof XmlElement) {
            XmlElement xmlElement = (XmlElement)annotation;
            stringBuilder.append("\n    ");
            stringBuilder.append("@XmlElement(name=").append(xmlElement.name()).append(", namespace=").append(xmlElement.namespace());
            if (xmlElement.nillable())
              stringBuilder.append(", nillable=true"); 
            if (xmlElement.required())
              stringBuilder.append(", required=true"); 
            stringBuilder.append(")");
            continue;
          } 
          throw new WebServiceException("Unknown JAXB annotation " + annotation);
        } 
        stringBuilder.append("\n    ");
        stringBuilder.append("public ");
        if (field.getSignature() == null) {
          stringBuilder.append(field.asmType.getDescriptor());
        } else {
          stringBuilder.append(field.getSignature());
        } 
        stringBuilder.append(" ");
        stringBuilder.append(field.fieldName);
      } 
      stringBuilder.append("\n\n}");
      LOGGER.fine(stringBuilder.toString());
    } 
    return classWriter.toByteArray();
  }
  
  private static String replaceDotWithSlash(String paramString) { return paramString.replace('.', '/'); }
  
  static Class createRequestWrapperBean(String paramString, Method paramMethod, QName paramQName, ClassLoader paramClassLoader) {
    byte[] arrayOfByte;
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Request Wrapper Class : {0}", paramString); 
    List list = RUNTIME_GENERATOR.collectRequestBeanMembers(paramMethod);
    try {
      arrayOfByte = createBeanImage(paramString, paramQName.getLocalPart(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQName.getNamespaceURI(), list);
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return Injector.inject(paramClassLoader, paramString, arrayOfByte);
  }
  
  static Class createResponseWrapperBean(String paramString, Method paramMethod, QName paramQName, ClassLoader paramClassLoader) {
    byte[] arrayOfByte;
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Response Wrapper Class : {0}", paramString); 
    List list = RUNTIME_GENERATOR.collectResponseBeanMembers(paramMethod);
    try {
      arrayOfByte = createBeanImage(paramString, paramQName.getLocalPart(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQName.getNamespaceURI(), list);
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return Injector.inject(paramClassLoader, paramString, arrayOfByte);
  }
  
  private static Type getASMType(Type paramType) {
    assert paramType != null;
    if (paramType instanceof Class)
      return Type.getType((Class)paramType); 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      if (parameterizedType.getRawType() instanceof Class)
        return Type.getType((Class)parameterizedType.getRawType()); 
    } 
    if (paramType instanceof java.lang.reflect.GenericArrayType)
      return Type.getType(FieldSignature.vms(paramType)); 
    if (paramType instanceof java.lang.reflect.WildcardType)
      return Type.getType(FieldSignature.vms(paramType)); 
    if (paramType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable)paramType;
      if (typeVariable.getBounds()[0] instanceof Class)
        return Type.getType((Class)typeVariable.getBounds()[0]); 
    } 
    throw new IllegalArgumentException("Not creating ASM Type for type = " + paramType);
  }
  
  static Class createExceptionBean(String paramString1, Class paramClass, String paramString2, String paramString3, String paramString4, ClassLoader paramClassLoader) { return createExceptionBean(paramString1, paramClass, paramString2, paramString3, paramString4, paramClassLoader, true); }
  
  static Class createExceptionBean(String paramString1, Class paramClass, String paramString2, String paramString3, String paramString4, ClassLoader paramClassLoader, boolean paramBoolean) {
    byte[] arrayOfByte;
    Collection collection = RUNTIME_GENERATOR.collectExceptionBeanMembers(paramClass, paramBoolean);
    try {
      arrayOfByte = createBeanImage(paramString1, paramString3, paramString4, paramClass.getSimpleName(), paramString2, collection);
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return Injector.inject(paramClassLoader, paramString1, arrayOfByte);
  }
  
  static void write(byte[] paramArrayOfByte, String paramString) {
    paramString = paramString.substring(paramString.lastIndexOf(".") + 1);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(paramString + ".class");
      fileOutputStream.write(paramArrayOfByte);
      fileOutputStream.flush();
      fileOutputStream.close();
    } catch (IOException iOException) {
      LOGGER.log(Level.INFO, "Error Writing class", iOException);
    } 
  }
  
  private static class Field extends Object implements Comparable<Field> {
    private final Type reflectType;
    
    private final Type asmType;
    
    private final String fieldName;
    
    private final List<Annotation> jaxbAnnotations;
    
    Field(String param1String, Type param1Type, Type param1Type1, List<Annotation> param1List) {
      this.reflectType = param1Type;
      this.asmType = param1Type1;
      this.fieldName = param1String;
      this.jaxbAnnotations = param1List;
    }
    
    String getSignature() { return (this.reflectType instanceof Class) ? null : ((this.reflectType instanceof TypeVariable) ? null : FieldSignature.vms(this.reflectType)); }
    
    public int compareTo(Field param1Field) { return this.fieldName.compareTo(param1Field.fieldName); }
  }
  
  private static final class FieldFactory extends Object implements AbstractWrapperBeanGenerator.BeanMemberFactory<Type, Field> {
    private FieldFactory() {}
    
    public WrapperBeanGenerator.Field createWrapperBeanMember(Type param1Type, String param1String, List<Annotation> param1List) { return new WrapperBeanGenerator.Field(param1String, param1Type, WrapperBeanGenerator.getASMType(param1Type), param1List); }
  }
  
  private static final class RuntimeWrapperBeanGenerator extends AbstractWrapperBeanGenerator<Type, Class, Method, Field> {
    protected RuntimeWrapperBeanGenerator(AnnotationReader<Type, Class, ?, Method> param1AnnotationReader, Navigator<Type, Class, ?, Method> param1Navigator, AbstractWrapperBeanGenerator.BeanMemberFactory<Type, WrapperBeanGenerator.Field> param1BeanMemberFactory) { super(param1AnnotationReader, param1Navigator, param1BeanMemberFactory); }
    
    protected Type getSafeType(Type param1Type) { return param1Type; }
    
    protected Type getHolderValueType(Type param1Type) {
      if (param1Type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)param1Type;
        if (parameterizedType.getRawType().equals(javax.xml.ws.Holder.class))
          return parameterizedType.getActualTypeArguments()[0]; 
      } 
      return null;
    }
    
    protected boolean isVoidType(Type param1Type) { return (param1Type == void.class); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\WrapperBeanGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */