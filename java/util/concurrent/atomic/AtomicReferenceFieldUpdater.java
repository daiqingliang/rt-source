package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicReferenceFieldUpdater<T, V> extends Object {
  @CallerSensitive
  public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> paramClass1, Class<W> paramClass2, String paramString) { return new AtomicReferenceFieldUpdaterImpl(paramClass1, paramClass2, paramString, Reflection.getCallerClass()); }
  
  public abstract boolean compareAndSet(T paramT, V paramV1, V paramV2);
  
  public abstract boolean weakCompareAndSet(T paramT, V paramV1, V paramV2);
  
  public abstract void set(T paramT, V paramV);
  
  public abstract void lazySet(T paramT, V paramV);
  
  public abstract V get(T paramT);
  
  public V getAndSet(T paramT, V paramV) {
    Object object;
    do {
      object = get(paramT);
    } while (!compareAndSet(paramT, object, paramV));
    return (V)object;
  }
  
  public final V getAndUpdate(T paramT, UnaryOperator<V> paramUnaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get(paramT);
      object2 = paramUnaryOperator.apply(object1);
    } while (!compareAndSet(paramT, object1, object2));
    return (V)object1;
  }
  
  public final V updateAndGet(T paramT, UnaryOperator<V> paramUnaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get(paramT);
      object2 = paramUnaryOperator.apply(object1);
    } while (!compareAndSet(paramT, object1, object2));
    return (V)object2;
  }
  
  public final V getAndAccumulate(T paramT, V paramV, BinaryOperator<V> paramBinaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get(paramT);
      object2 = paramBinaryOperator.apply(object1, paramV);
    } while (!compareAndSet(paramT, object1, object2));
    return (V)object1;
  }
  
  public final V accumulateAndGet(T paramT, V paramV, BinaryOperator<V> paramBinaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get(paramT);
      object2 = paramBinaryOperator.apply(object1, paramV);
    } while (!compareAndSet(paramT, object1, object2));
    return (V)object2;
  }
  
  private static final class AtomicReferenceFieldUpdaterImpl<T, V> extends AtomicReferenceFieldUpdater<T, V> {
    private static final Unsafe U = Unsafe.getUnsafe();
    
    private final long offset;
    
    private final Class<?> cclass;
    
    private final Class<T> tclass;
    
    private final Class<V> vclass;
    
    AtomicReferenceFieldUpdaterImpl(final Class<T> tclass, Class<V> param1Class2, final String fieldName, Class<?> param1Class3) {
      try {
        field = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
              public Field run() throws NoSuchFieldException { return tclass.getDeclaredField(fieldName); }
            });
        i = field.getModifiers();
        ReflectUtil.ensureMemberAccess(param1Class3, param1Class1, null, i);
        ClassLoader classLoader1 = param1Class1.getClassLoader();
        ClassLoader classLoader2 = param1Class3.getClassLoader();
        if (classLoader2 != null && classLoader2 != classLoader1 && (classLoader1 == null || !isAncestor(classLoader1, classLoader2)))
          ReflectUtil.checkPackageAccess(param1Class1); 
        clazz = field.getType();
      } catch (PrivilegedActionException privilegedActionException) {
        throw new RuntimeException(privilegedActionException.getException());
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
      if (param1Class2 != clazz)
        throw new ClassCastException(); 
      if (param1Class2.isPrimitive())
        throw new IllegalArgumentException("Must be reference type"); 
      if (!Modifier.isVolatile(i))
        throw new IllegalArgumentException("Must be volatile type"); 
      this.cclass = (Modifier.isProtected(i) && param1Class1.isAssignableFrom(param1Class3) && !isSamePackage(param1Class1, param1Class3)) ? param1Class3 : param1Class1;
      this.tclass = param1Class1;
      this.vclass = param1Class2;
      this.offset = U.objectFieldOffset(field);
    }
    
    private static boolean isAncestor(ClassLoader param1ClassLoader1, ClassLoader param1ClassLoader2) {
      ClassLoader classLoader = param1ClassLoader1;
      do {
        classLoader = classLoader.getParent();
        if (param1ClassLoader2 == classLoader)
          return true; 
      } while (classLoader != null);
      return false;
    }
    
    private static boolean isSamePackage(Class<?> param1Class1, Class<?> param1Class2) { return (param1Class1.getClassLoader() == param1Class2.getClassLoader() && Objects.equals(getPackageName(param1Class1), getPackageName(param1Class2))); }
    
    private static String getPackageName(Class<?> param1Class) {
      String str = param1Class.getName();
      int i = str.lastIndexOf('.');
      return (i != -1) ? str.substring(0, i) : "";
    }
    
    private final void accessCheck(T param1T) {
      if (!this.cclass.isInstance(param1T))
        throwAccessCheckException(param1T); 
    }
    
    private final void throwAccessCheckException(T param1T) {
      if (this.cclass == this.tclass)
        throw new ClassCastException(); 
      throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + param1T.getClass().getName()));
    }
    
    private final void valueCheck(V param1V) {
      if (param1V != null && !this.vclass.isInstance(param1V))
        throwCCE(); 
    }
    
    static void throwCCE() { throw new ClassCastException(); }
    
    public final boolean compareAndSet(T param1T, V param1V1, V param1V2) {
      accessCheck(param1T);
      valueCheck(param1V2);
      return U.compareAndSwapObject(param1T, this.offset, param1V1, param1V2);
    }
    
    public final boolean weakCompareAndSet(T param1T, V param1V1, V param1V2) {
      accessCheck(param1T);
      valueCheck(param1V2);
      return U.compareAndSwapObject(param1T, this.offset, param1V1, param1V2);
    }
    
    public final void set(T param1T, V param1V) {
      accessCheck(param1T);
      valueCheck(param1V);
      U.putObjectVolatile(param1T, this.offset, param1V);
    }
    
    public final void lazySet(T param1T, V param1V) {
      accessCheck(param1T);
      valueCheck(param1V);
      U.putOrderedObject(param1T, this.offset, param1V);
    }
    
    public final V get(T param1T) {
      accessCheck(param1T);
      return (V)U.getObjectVolatile(param1T, this.offset);
    }
    
    public final V getAndSet(T param1T, V param1V) {
      accessCheck(param1T);
      valueCheck(param1V);
      return (V)U.getAndSetObject(param1T, this.offset, param1V);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicReferenceFieldUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */