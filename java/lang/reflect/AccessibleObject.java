package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.AccessController;
import java.security.Permission;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;

public class AccessibleObject implements AnnotatedElement {
  private static final Permission ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
  
  boolean override;
  
  static final ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
  
  public static void setAccessible(AccessibleObject[] paramArrayOfAccessibleObject, boolean paramBoolean) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(ACCESS_PERMISSION); 
    for (byte b = 0; b < paramArrayOfAccessibleObject.length; b++)
      setAccessible0(paramArrayOfAccessibleObject[b], paramBoolean); 
  }
  
  public void setAccessible(boolean paramBoolean) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(ACCESS_PERMISSION); 
    setAccessible0(this, paramBoolean);
  }
  
  private static void setAccessible0(AccessibleObject paramAccessibleObject, boolean paramBoolean) throws SecurityException {
    if (paramAccessibleObject instanceof Constructor && paramBoolean == true) {
      Constructor constructor = (Constructor)paramAccessibleObject;
      if (constructor.getDeclaringClass() == Class.class)
        throw new SecurityException("Cannot make a java.lang.Class constructor accessible"); 
    } 
    paramAccessibleObject.override = paramBoolean;
  }
  
  public boolean isAccessible() { return this.override; }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) { throw new AssertionError("All subclasses should override this method"); }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass) { return super.isAnnotationPresent(paramClass); }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass) { throw new AssertionError("All subclasses should override this method"); }
  
  public Annotation[] getAnnotations() { return getDeclaredAnnotations(); }
  
  public <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass) { return (T)getAnnotation(paramClass); }
  
  public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass) { return (T[])getAnnotationsByType(paramClass); }
  
  public Annotation[] getDeclaredAnnotations() { throw new AssertionError("All subclasses should override this method"); }
  
  void checkAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt) throws IllegalAccessException {
    if (paramClass1 == paramClass2)
      return; 
    Object object = this.securityCheckCache;
    Class<?> clazz = paramClass2;
    if (paramObject != null && Modifier.isProtected(paramInt) && (clazz = paramObject.getClass()) != paramClass2) {
      if (object instanceof Class[]) {
        Class[] arrayOfClass = (Class[])object;
        if (arrayOfClass[true] == clazz && arrayOfClass[false] == paramClass1)
          return; 
      } 
    } else if (object == paramClass1) {
      return;
    } 
    slowCheckMemberAccess(paramClass1, paramClass2, paramObject, paramInt, clazz);
  }
  
  void slowCheckMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt, Class<?> paramClass3) throws IllegalAccessException {
    Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, paramInt);
    new Class[2][0] = paramClass1;
    new Class[2][1] = paramClass3;
    Class<?> clazz = (paramClass3 == paramClass2) ? paramClass1 : new Class[2];
    this.securityCheckCache = clazz;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\AccessibleObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */