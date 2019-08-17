package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

public class ReflectAnnotationReader implements MetadataReader {
  public Annotation[] getAnnotations(Method paramMethod) { return paramMethod.getAnnotations(); }
  
  public Annotation[][] getParameterAnnotations(final Method method) { return (Annotation[][])AccessController.doPrivileged(new PrivilegedAction<Annotation[][]>() {
          public Annotation[][] run() { return method.getParameterAnnotations(); }
        }); }
  
  public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) { return (A)(Annotation)AccessController.doPrivileged(new PrivilegedAction<A>() {
          public A run() { return (A)m.getAnnotation(annType); }
        }); }
  
  public <A extends Annotation> A getAnnotation(final Class<A> annType, final Class<?> cls) { return (A)(Annotation)AccessController.doPrivileged(new PrivilegedAction<A>() {
          public A run() { return (A)cls.getAnnotation(annType); }
        }); }
  
  public Annotation[] getAnnotations(final Class<?> cls) { return (Annotation[])AccessController.doPrivileged(new PrivilegedAction<Annotation[]>() {
          public Annotation[] run() { return cls.getAnnotations(); }
        }); }
  
  public void getProperties(Map<String, Object> paramMap, Class<?> paramClass) {}
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod) {}
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod, int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\ReflectAnnotationReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */