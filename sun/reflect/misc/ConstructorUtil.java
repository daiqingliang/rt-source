package sun.reflect.misc;

import java.lang.reflect.Constructor;

public final class ConstructorUtil {
  public static Constructor<?> getConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass) throws NoSuchMethodException {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getConstructor(paramArrayOfClass);
  }
  
  public static Constructor<?>[] getConstructors(Class<?> paramClass) {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getConstructors();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\misc\ConstructorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */