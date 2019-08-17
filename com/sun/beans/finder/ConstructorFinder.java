package com.sun.beans.finder;

import com.sun.beans.util.Cache;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;

public final class ConstructorFinder extends AbstractFinder<Constructor<?>> {
  private static final Cache<Signature, Constructor<?>> CACHE = new Cache<Signature, Constructor<?>>(Cache.Kind.SOFT, Cache.Kind.SOFT) {
      public Constructor create(Signature param1Signature) {
        try {
          ConstructorFinder constructorFinder = new ConstructorFinder(param1Signature.getArgs(), null);
          return (Constructor)constructorFinder.find(param1Signature.getType().getConstructors());
        } catch (Exception exception) {
          throw new SignatureException(exception);
        } 
      }
    };
  
  public static Constructor<?> findConstructor(Class<?> paramClass, Class<?>... paramVarArgs) throws NoSuchMethodException {
    if (paramClass.isPrimitive())
      throw new NoSuchMethodException("Primitive wrapper does not contain constructors"); 
    if (paramClass.isInterface())
      throw new NoSuchMethodException("Interface does not contain constructors"); 
    if (Modifier.isAbstract(paramClass.getModifiers()))
      throw new NoSuchMethodException("Abstract class cannot be instantiated"); 
    if (!Modifier.isPublic(paramClass.getModifiers()) || !ReflectUtil.isPackageAccessible(paramClass))
      throw new NoSuchMethodException("Class is not accessible"); 
    PrimitiveWrapperMap.replacePrimitivesWithWrappers(paramVarArgs);
    Signature signature = new Signature(paramClass, paramVarArgs);
    try {
      return (Constructor)CACHE.get(signature);
    } catch (SignatureException signatureException) {
      throw signatureException.toNoSuchMethodException("Constructor is not found");
    } 
  }
  
  private ConstructorFinder(Class<?>[] paramArrayOfClass) { super(paramArrayOfClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\ConstructorFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */