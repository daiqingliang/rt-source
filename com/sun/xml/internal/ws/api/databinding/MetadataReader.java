package com.sun.xml.internal.ws.api.databinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public interface MetadataReader {
  Annotation[] getAnnotations(Method paramMethod);
  
  Annotation[][] getParameterAnnotations(Method paramMethod);
  
  <A extends Annotation> A getAnnotation(Class<A> paramClass, Method paramMethod);
  
  <A extends Annotation> A getAnnotation(Class<A> paramClass1, Class<?> paramClass2);
  
  Annotation[] getAnnotations(Class<?> paramClass);
  
  void getProperties(Map<String, Object> paramMap, Class<?> paramClass);
  
  void getProperties(Map<String, Object> paramMap, Method paramMethod);
  
  void getProperties(Map<String, Object> paramMap, Method paramMethod, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\MetadataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */