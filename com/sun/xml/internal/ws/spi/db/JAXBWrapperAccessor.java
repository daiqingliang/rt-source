package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

public class JAXBWrapperAccessor extends WrapperAccessor {
  protected Class<?> contentClass;
  
  protected HashMap<Object, Class> elementDeclaredTypes;
  
  public JAXBWrapperAccessor(Class<?> paramClass) {
    this.contentClass = paramClass;
    HashMap hashMap1 = new HashMap();
    HashMap hashMap2 = new HashMap();
    HashMap hashMap3 = new HashMap();
    HashMap hashMap4 = new HashMap();
    HashMap hashMap5 = new HashMap();
    HashMap hashMap6 = new HashMap();
    HashMap hashMap7 = new HashMap();
    HashMap hashMap8 = new HashMap();
    for (Method method : this.contentClass.getMethods()) {
      if (PropertySetterBase.setterPattern(method)) {
        String str = method.getName().substring(3, method.getName().length()).toLowerCase();
        hashMap3.put(str, method);
      } 
      if (PropertyGetterBase.getterPattern(method)) {
        String str1 = method.getName();
        String str2 = str1.startsWith("is") ? str1.substring(2, method.getName().length()).toLowerCase() : str1.substring(3, method.getName().length()).toLowerCase();
        hashMap6.put(str2, method);
      } 
    } 
    HashSet hashSet = new HashSet();
    for (Field field : getAllFields(this.contentClass)) {
      XmlElementWrapper xmlElementWrapper = (XmlElementWrapper)field.getAnnotation(XmlElementWrapper.class);
      XmlElement xmlElement = (XmlElement)field.getAnnotation(XmlElement.class);
      XmlElementRef xmlElementRef = (XmlElementRef)field.getAnnotation(XmlElementRef.class);
      String str1 = field.getName().toLowerCase();
      String str2 = "";
      String str3 = field.getName();
      if (xmlElementWrapper != null) {
        str2 = xmlElementWrapper.namespace();
        if (xmlElementWrapper.name() != null && !xmlElementWrapper.name().equals("") && !xmlElementWrapper.name().equals("##default"))
          str3 = xmlElementWrapper.name(); 
      } else if (xmlElement != null) {
        str2 = xmlElement.namespace();
        if (xmlElement.name() != null && !xmlElement.name().equals("") && !xmlElement.name().equals("##default"))
          str3 = xmlElement.name(); 
      } else if (xmlElementRef != null) {
        str2 = xmlElementRef.namespace();
        if (xmlElementRef.name() != null && !xmlElementRef.name().equals("") && !xmlElementRef.name().equals("##default"))
          str3 = xmlElementRef.name(); 
      } 
      if (hashSet.contains(str3)) {
        this.elementLocalNameCollision = true;
      } else {
        hashSet.add(str3);
      } 
      QName qName = new QName(str2, str3);
      if (field.getType().equals(JAXBElement.class) && field.getGenericType() instanceof ParameterizedType) {
        Type type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
        if (type instanceof Class) {
          hashMap7.put(qName, (Class)type);
          hashMap8.put(str3, (Class)type);
        } else if (type instanceof GenericArrayType) {
          Type type1 = ((GenericArrayType)type).getGenericComponentType();
          if (type1 instanceof Class) {
            Class clazz = Array.newInstance((Class)type1, 0).getClass();
            hashMap7.put(qName, clazz);
            hashMap8.put(str3, clazz);
          } 
        } 
      } 
      if (str1.startsWith("_") && !str3.startsWith("_"))
        str1 = str1.substring(1); 
      Method method1 = (Method)hashMap3.get(str1);
      Method method2 = (Method)hashMap6.get(str1);
      PropertySetter propertySetter = createPropertySetter(field, method1);
      PropertyGetter propertyGetter = createPropertyGetter(field, method2);
      hashMap1.put(qName, propertySetter);
      hashMap2.put(str3, propertySetter);
      hashMap4.put(qName, propertyGetter);
      hashMap5.put(str3, propertyGetter);
    } 
    if (this.elementLocalNameCollision) {
      this.propertySetters = hashMap1;
      this.propertyGetters = hashMap4;
      this.elementDeclaredTypes = hashMap7;
    } else {
      this.propertySetters = hashMap2;
      this.propertyGetters = hashMap5;
      this.elementDeclaredTypes = hashMap8;
    } 
  }
  
  protected static List<Field> getAllFields(Class<?> paramClass) {
    ArrayList arrayList = new ArrayList();
    while (!Object.class.equals(paramClass)) {
      arrayList.addAll(Arrays.asList(getDeclaredFields(paramClass)));
      paramClass = paramClass.getSuperclass();
    } 
    return arrayList;
  }
  
  protected static Field[] getDeclaredFields(final Class<?> clz) {
    try {
      return (System.getSecurityManager() == null) ? paramClass.getDeclaredFields() : (Field[])AccessController.doPrivileged(new PrivilegedExceptionAction<Field[]>() {
            public Field[] run() throws IllegalAccessException { return clz.getDeclaredFields(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      privilegedActionException.printStackTrace();
      return null;
    } 
  }
  
  protected static PropertyGetter createPropertyGetter(Field paramField, Method paramMethod) {
    if (!paramField.isAccessible() && paramMethod != null) {
      MethodGetter methodGetter = new MethodGetter(paramMethod);
      if (methodGetter.getType().toString().equals(paramField.getType().toString()))
        return methodGetter; 
    } 
    return new FieldGetter(paramField);
  }
  
  protected static PropertySetter createPropertySetter(Field paramField, Method paramMethod) {
    if (!paramField.isAccessible() && paramMethod != null) {
      MethodSetter methodSetter = new MethodSetter(paramMethod);
      if (methodSetter.getType().toString().equals(paramField.getType().toString()))
        return methodSetter; 
    } 
    return new FieldSetter(paramField);
  }
  
  private Class getElementDeclaredType(QName paramQName) {
    QName qName = this.elementLocalNameCollision ? paramQName : paramQName.getLocalPart();
    return (Class)this.elementDeclaredTypes.get(qName);
  }
  
  public PropertyAccessor getPropertyAccessor(String paramString1, String paramString2) {
    final QName n = new QName(paramString1, paramString2);
    final PropertySetter setter = getPropertySetter(qName);
    final PropertyGetter getter = getPropertyGetter(qName);
    final boolean isJAXBElement = propertySetter.getType().equals(JAXBElement.class);
    final boolean isListType = List.class.isAssignableFrom(propertySetter.getType());
    final Class elementDeclaredType = bool1 ? getElementDeclaredType(qName) : null;
    return new PropertyAccessor() {
        public Object get(Object param1Object) throws DatabindingException {
          Object object;
          if (isJAXBElement) {
            JAXBElement jAXBElement = (JAXBElement)getter.get(param1Object);
            object = (jAXBElement == null) ? null : jAXBElement.getValue();
          } else {
            object = getter.get(param1Object);
          } 
          if (object == null && isListType) {
            object = new ArrayList();
            set(param1Object, object);
          } 
          return object;
        }
        
        public void set(Object param1Object1, Object param1Object2) throws DatabindingException {
          if (isJAXBElement) {
            JAXBElement jAXBElement = new JAXBElement(n, elementDeclaredType, JAXBWrapperAccessor.this.contentClass, param1Object2);
            setter.set(param1Object1, jAXBElement);
          } else {
            setter.set(param1Object1, param1Object2);
          } 
        }
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\JAXBWrapperAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */